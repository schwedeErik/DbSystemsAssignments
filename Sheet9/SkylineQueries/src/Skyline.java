
import java.util.*;

public class Skyline {

    /**
     * To execute the program use:
     *
     *     java Skyline.java
     *
     * For the exercise write the code of the method calculateSkyline.
     * You should use the class FakeRTree, which simulates some methods of a real R tree.
     *
     * The template will check for correct results and will inform you upon execution.
     *
     * To help you the template contains the classes Point(x,y) and Rectangle(x,y,SizeX,SizeY),
     * which represent Points (with coordinates x,y) and Rectangles (with coordinates x,y,x+SizeX,y+SizeY)
     *
     * The FakeRTree has the methods:
     *      queryNN(Point p), which returns the nearest Point p' to Point p (or null if the tree is empty)
     *      queryNN(Point p, Rectangle R), which returns the nearest Point p' to Point p, which is contained in Rectangle R (or null if there is none)
     *      queryOverlap(Rectangle R), which returns all Points in the tree that are contained in R.
     */
    public static void main (String[] args) {

        if(test1() && test2()){
            System.out.println("=======================================");
            System.out.println("Result of solution is correct.");
            System.out.println("=======================================");
            System.exit(0);
        }else {
            System.out.println("=======================================");
            System.out.println("Result of solution is not correct.");
            System.out.println("=======================================");
            System.exit(1);
        }

    }

    private static boolean test1(){
        System.out.println("=======================================");
        System.out.println("Starting Test 1");
        System.out.println("=======================================");
        FakeRTree tree = new FakeRTree();
        tree.add(10,20);
        tree.add(12,10);
        tree.add(8,11);
        tree.add(16,19);
        tree.add(6,4);
        tree.add(5,6);
        tree.add(14,12);
        tree.add(2,5);
        tree.add(3,10);
        tree.add(13,19);
        tree.add(17,5);
        tree.add(9,3);
        tree.add(20,8);
        tree.add(8,10);

        Set<Point> skyline = calculateSkyline(tree);
        System.out.println("Calculated skyline: " + skyline);
        Set<Point> q1 = new HashSet<Point>();
        q1.add(new Point(2,5));
        q1.add(new Point(9,3));
        q1.add(new Point(6,4));
        System.out.println("Expected skyline: " + q1);
        if (compareResults(skyline,q1)){

             System.out.println("Test one successful!");
             return true;
        }else{
            System.out.println("Test one failed!");
            return false;
        }
    }

    private static boolean test2(){
        System.out.println("=======================================");
        System.out.println("Starting Test 2");
        System.out.println("=======================================");
        FakeRTree tree = new FakeRTree();
        Random rand = new Random(234782343247L);
        int numPoints = 5000 ;
        int maxRange = 10000;
        for (int i = 0; i < numPoints; i++) {
            tree.add(new Point(rand.nextInt(maxRange),rand.nextInt(maxRange)));
        }
        Set<Point> skyline = calculateSkyline(tree);
        Set<Point> q2 = new HashSet<Point>();
        q2.add(new Point(404,54));
        q2.add(new Point(173,146));
        q2.add(new Point(126,292));
        q2.add(new Point(7,7709));
        q2.add(new Point(458,20));
        q2.add(new Point(5,8080));
        q2.add(new Point(1030,2));
        q2.add(new Point(12,377));
        System.out.println("Calculated skyline: " + skyline);
        System.out.println("Expected skyline: " + q2);
        if (compareResults(skyline,q2)){
            System.out.println("Test two successful!");
            return true;
        }else{
            System.out.println("Test two failed!");
            return false;
        }
    }


    private static boolean compareResults(Set<Point> r1, Set<Point> r2){
        return r1.containsAll(r2) && r1.size()==r2.size();
    }
    
    private static Set<Point> calculateSkyline(FakeRTree tree){

        // find nn to origen (0,0)
        // result is called i --> first skyline point
        Set<Point> ret = new HashSet<>();
        var origin = new Point(0,0);
        var i = tree.queryNN(origin);
        ret.add(i);

        // a number that is bigger than the biggest inserted dimension
        var infinity = 10000;

        var sizeXRectangleQue = new ArrayList<Rectangle>();
        var sizeYRectangleQue = new ArrayList<Rectangle>();

        // for rectangles that have a limited x size
        sizeXRectangleQue.add(new Rectangle(origin.x, i.y, i.x,infinity));

        // fore rectangles that have a limited y size
        sizeYRectangleQue.add(new Rectangle(i.x, origin.y,infinity,i.y ));

        // for sizeX
        do {
            var skyLinePoint = tree.queryNN(origin,sizeXRectangleQue.get(0));
            sizeXRectangleQue.remove(0);

            if(skyLinePoint == null)
                continue;

            ret.add(skyLinePoint);
            // the only difference in the tow do while loops
            sizeXRectangleQue.add(new Rectangle(origin.x, skyLinePoint.y, skyLinePoint.x,infinity));
        }while(!sizeXRectangleQue.isEmpty());

        // for sizeY
        do {
            var skyLinePoint = tree.queryNN(origin,sizeYRectangleQue.get(0));
            sizeYRectangleQue.remove(0);

            if(skyLinePoint == null)
                continue;

            ret.add(skyLinePoint);
            sizeYRectangleQue.add(new Rectangle(skyLinePoint.x, origin.y,infinity,skyLinePoint.y ));
        }while(!sizeYRectangleQue.isEmpty());

        return ret;
    }













    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Calculates the distance between this Point and another one
         * @param other The other Point to compare against
         * @return The distance between the Points
         */
        public double distance(Point other){
            return Math.sqrt(Math.pow((x-other.x),2)+Math.pow((y-other.y),2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x +"," + y +')';
        }
    }

    public static class Rectangle {
        int x;
        int y;
        int sizeX;
        int sizeY;

        public Rectangle(int x, int y, int sizeX, int sizeY) {
            this.x = x;
            this.y = y;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }

        /**
         * Checks if Point p is contained in the Rectangle (with inclusive borders)
         * @param p Point p to check against
         * @return True if p is within the Rectangle, False otherwise
         */
        public boolean contains(Point p){
            return (p.x>=x && p.x <= x+sizeX) && (p.y>=y && p.y <= y+sizeY);
        }

        /**
         * Checks if Point p is contained in the Rectangle (with exclusive borders)
         * @param p Point p to check against
         * @return True if p is within the Rectangle, False otherwise
         */
        public boolean containsInner(Point p){
            return (p.x>x && p.x < x+sizeX) && (p.y>y && p.y < y+sizeY);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rectangle rectangle = (Rectangle) o;
            return x == rectangle.x &&
                    y == rectangle.y &&
                    sizeX == rectangle.sizeX &&
                    sizeY == rectangle.sizeY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, sizeX, sizeY);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y +"," + sizeX +"," + sizeY +')';
        }
    }



    public static class FakeRTree {

        Set<Point> points;

        public FakeRTree() {
            points = new HashSet<Point>();
        }


        void add(int x, int y) {
            points.add(new Point(x,y));
        }
        void add(Point p) {
            points.add(p);
        }

        /**
         * Returns the closest Point to q, or null if the tree is empty.
         * @param q Query Point, to compare to
         * @return Closest Point, or null
         */
        Point queryNN(Point q) {
            Point ret = null;
            double dbest = Double.MAX_VALUE;
            for (Point p : points) {
                double d = q.distance(p);
                if (d<dbest) {
                    ret = p;
                    dbest = d;
                }
            }
            return ret;
        }

        /**
         * Returns the closest Point to q, or null if the tree is empty.
         * @param q Query Point, to compare to
         * @param rec Rectangle to restrict query (Rectangle boundaries are not inclusive).
         * @return Closest Point, or null
         */
        Point queryNN(Point q, Rectangle rec) {
            Point ret = null;
            double dbest = Double.MAX_VALUE;
            for (Point p : points) {
                double d = q.distance(p);
                if (d<dbest && rec.containsInner(p)) {
                    ret = p;
                    dbest = d;
                }
            }
            return ret;
        }

        /**
         * Returns all points contained in the Rectangle
         * @param q Query Rectangle
         * @return A set of Points contained in the Rectangle.
         */
        Set<Point> queryOverlap(Rectangle q) {
            HashSet<Point> ret = new HashSet<Point>();
            for (Point p: points) {
                if (q.contains(p)) ret.add(p);
            }
            return ret;
        }

    }
}
