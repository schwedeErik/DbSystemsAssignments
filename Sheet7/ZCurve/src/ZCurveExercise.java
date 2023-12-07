import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 *
 * To execute the program use:
 *
 * java ZCurveExercise.java <k> <Point-File> <Query-File>
 *
 * For the exercise write the code of the methods: - ZCurveExercise::getKNN -
 * ZCurveExercise::getKNNZCurve - Point::distance - Point::calculateZValue
 * 
 * Hints: To get a string of bits in an integer you can use
 * `Integer.toBinaryString(int i)`. To convert a string back to int you can use
 * `Integer.parseInt("1001", 2)`.
 */
public class ZCurveExercise {

  public static void main(String... args) throws IOException {
    System.out.println("ZCurve exercise:");
    System.out.println("============================================");
    if (args.length < 3) {
      System.out.println("Error: 3 parameters expected: k, data-file, query-file");
      System.exit(-1);
    }
    List<Point> points = readFile(args[1]);
    List<Point> queries = readFile(args[2]);
    int k = Integer.parseInt(args[0]);

    System.out.println("============================================");
    System.out.println("Points:");
    for (Point p : points) {
      System.out.println(p.toString());
    }
    System.out.println("============================================");
    System.out.println("Queries:");
    for (Point q : queries) {
      System.out.println(q.toString());
    }

    System.out.println("============================================");
    System.out.println("KNN (k = " + k + "):");
    for (Point q : queries) {
      List<Point> knn = getKNN(k, points, q);
      System.out.println(q.toString() + ": " + knn.toString());
    }
    System.out.println("============================================");
    System.out.println("KNN ZCurve (k = " + k + "):");
    for (Point q : queries) {
      List<Point> knn = getKNNZCurve(k, points, q);
      System.out.println(q.toString() + ": " + knn.toString());
    }
    System.out.println("============================================");
  }

  /**
   * Reads a file and returns the points in it as a List
   * 
   * @param file the path to the file to read
   * @return A list of points contained in the file.
   * @throws IOException
   */
  private static List<Point> readFile(String file) throws IOException {
    System.out.println("Reading file: " + file);
    List<String> lines = Files.readAllLines(Paths.get(file));

    return lines.stream().map(s -> s.split(" ")).map(e -> new Point(Integer.parseInt(e[0]), Integer.parseInt(e[1])))
        .collect(Collectors.toList());
  }

  /**
   * Computes the k NN of query in the list of points
   * 
   * @param k      How many nearest neighbors to return
   * @param points The list of points in which to search for the NN
   * @param query  The query point, for which the NN are searched
   * @return A list of k NN, of the query point, in the list of points
   */
  private static List<Point> getKNN(int k, List<Point> points, Point query) {
    var sortedPoints =   new ArrayList<Point>(points.stream().toList());
    sortedPoints.sort((p1,p2) -> Double.compare(p1.distance(query),p2.distance(query)));
    return sortedPoints.subList(0,k);
  }

  /**
   * Computes the k NN of query in the list of points, based on the ZCurve value
   * 
   * @param k      How many nearest neighbors to return
   * @param points The list of points in which to search for the NN
   * @param query  The query point, for which the NN are searched
   * @return A list of k NN, of the query point, in the list of points
   */
  private static List<Point> getKNNZCurve(int k, List<Point> points, Point query) {
    /////////////////////////////////////////
    // TODO Your Code Here
    /////////////////////////////////////////
    System.out.println("getKNNZCurve function not implemented!");
    return new ArrayList<Point>();
  }

  /**
   * Represents a 2D point
   */
  static class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    /**
     * @return the String representation of the given point
     */
    public String toString() {
      return "(" + x + "," + y + ")" + " {" + calculateZValue() + "}";
    }

    /**
     * Calculates the distance to a given Point
     * 
     * @param o the other Point to calculate the distance to
     * @return The distance between this Point and Point o
     */
    public double distance(Point o) {
      //Distance between two points:
      //  d = sqrt((x2-x1)^2 + (y2-y1)^2)
      return Math.sqrt(Math.pow((o.x - x),2) + Math.pow((o.y - y),2));
    }

    /**
     * @return the ZCurve value of the given point
     */
    public long calculateZValue() {
      var xBitValue = String.format("%32s",Integer.toBinaryString(x)).replaceAll(" ", "0");
      var yBitValue = String.format("%32s",Integer.toBinaryString(y)).replaceAll(" ", "0");
      String resultBitValue = "";

      for(int i = 0; i < xBitValue.length(); i++)
      {
      resultBitValue = resultBitValue + yBitValue.charAt(i) + xBitValue.charAt(i);
      }

      return Integer.parseInt(resultBitValue, 2);
    }
  }

}
