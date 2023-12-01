import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 *
 * To execute the program you have to use:
 *
 * java MinHashingExercise.java <File1> <File2>
 *
 * For the exercise write the code of the methods jaccard, similaritykHash, and
 * similaritykValues.
 * 
 * You can use the hash(int k, String str) function to use the i-th hash function to calculate the hash of the given string.
 *
 */
public class MinHashingExercise {

    public static void main(String... args) throws IOException {
		System.out.println("Min Hashing exercise:");
		System.out.println("============================================");
		if (args.length < 2) {
			System.out.println("Error: Two files required as input!");
			System.exit(-1);
		}
		List<String> file1 = readFile(args[0]);
		List<String> file2 = readFile(args[1]);

		System.out.println("============================================");
		System.out.print("Calculating Jaccard similarity: ");
		System.out.println(jaccard(file1, file2));
		System.out.println("============================================");
		for (int i = 1; i <= 6; i++) {
			System.out.print("Calculating similarity for k = " + i + " hash functions: ");
			System.out.println(similaritykHash(i, file1, file2));
		}
		System.out.println("============================================");
		for (int i = 1; i <= 6; i++) {
			System.out.print("Calculating similarity for k = " + i + " hash values: ");
			System.out.println(similaritykValues(i, file1, file2));
		}
		System.out.println("============================================");
	}

    /**
     * Calculates the Jaccard similarity measure of two lists of strings.
     * @param lhs The first list of strings.
     * @param rhs The second list of strings.
     * @return The Jaccard similarity of the two lists in range [0,1].
     */

    private static double jaccard(List<String> lhs, List<String> rhs) {

        var distinctWords = new ArrayList<String>();
        var allWords = Stream.concat(lhs.stream(), rhs.stream()).toList();
        for (var word:
                allWords) {
            boolean duplicat = false;
            for (var distinctWord:
                 distinctWords) {


                if(word.equals(distinctWord))
                {
                   duplicat = true;
                   break;
                }
            }

            if(duplicat)
                continue;

            distinctWords.add(word);
        }
        var duplicatedWords = new ArrayList<String>();

        for (var leftWord:
             lhs) {
            boolean notDuplicated = true;
            for(var rightWord:
                rhs)
            {
                if(leftWord.equals(rightWord) && duplicatedWords.stream().noneMatch( dw -> dw.equals(rightWord)))
                {
                    duplicatedWords.add(rightWord);
                }
            }
        }

        return ((double)(duplicatedWords.size()))/((double)(distinctWords.size()));
    }

    private static int customHashFunction(String word, int seed) {
        int hash = word.hashCode()%((seed+1)*2);
        return hash;
    }

    /**
     * Calculates the Min-Hashing similarity measure of two lists of strings.
     * @param k The number of different hash functions to use.
     * @param lhs The first list of strings.
     * @param rhs The second list of strings.
     * @return The Min-Hashing similarity of the two lists in range [0,1].
     */
    private static double similaritykHash(int k, List<String> lhs, List<String> rhs) {

        // List of hash values of the elements in lhs for each hash function
        var lhsHashValues = new ArrayList<ArrayList<Integer>>();


        // List of hash values for rhs for each hash function
        var rhsHashValues = new ArrayList<ArrayList<Integer>>();

        double hits = 0;
        for(int i = 0; i<k; i++)
        {
            lhsHashValues.add(new ArrayList<Integer>());
            rhsHashValues.add(new ArrayList<Integer>());
            for (var word:
                 lhs) {
                lhsHashValues.get(i).add(customHashFunction(word, i));
            }
            for (var word:
                 rhs) {
                rhsHashValues.get(i).add(customHashFunction(word, i));
            }

            var minLhsHashValue = lhsHashValues.get(i).stream().min(Comparator.comparing(Integer::intValue)).get();
            var minRhsHashValue = rhsHashValues.get(i).stream().min(Comparator.comparing(Integer::intValue)).get();

            if(minRhsHashValue == minLhsHashValue)
                hits++;
        }
        return hits/((double)(k));
    }

    /**
     * Calculates the Min-Hashing similarity measure of two lists of strings.
     * @param k The number of hash values to use (from one hash function).
     * @param lhs The first list of strings.
     * @param rhs The second list of strings.
     * @return The Min-Hashing similarity of the two lists in range [0,1].
     */
    private static double similaritykValues(int k, List<String> lhs, List<String> rhs) {
        var lhsHashValues = new ArrayList<Integer>();
        var rhsHashValues = new ArrayList<Integer>();
        for (var word:
             lhs) {
            if(!lhsHashValues.contains(word.hashCode()))
                lhsHashValues.add(word.hashCode());
        }

        for (var word:
             rhs) {
            if(!rhsHashValues.contains(word.hashCode()))
                rhsHashValues.add(word.hashCode());
        }

        List<Integer> kSmallestLhsValues = lhsHashValues.stream().sorted().toList().subList(0,k);
        List<Integer> kSmallestRhsValues = rhsHashValues.stream().sorted().toList().subList(0,k);

        var hitList = new ArrayList<Integer>();
        for(var lhsHashValue:
            kSmallestLhsValues)
        {
            for(var rhsHashValue:
            kSmallestRhsValues)
            {
            if(rhsHashValue.intValue() == lhsHashValue.intValue())
                hitList.add(rhsHashValue);
            }
        }

        return ((double)(hitList.stream().distinct().toList().size()))/((double)(k));
    }

    /**
     * Reads a file and returns the words in it as a List
     * 
     * @param file the path to the file to read
     * @return A list of words contained in the file.
     * @throws IOException
     */
    private static List<String> readFile(String file) throws IOException {
        System.out.println("Reading file: " + file);
        String contents = new String(Files.readAllBytes(Paths.get(file)));
        System.out.println(contents);
        return Arrays.asList(contents.split(" "));
    }

    /**
     * Calculates the k-th hash value of str
     * 
     * @param k   The index of the hash function in [0,5]
     * @param str The string to hash
     * @return The k-th hash value of str
     */
    static int hash(int k, String str) {
        int hash = str.hashCode();
        switch (k) {
        case 0:
            return hash % 2012;
        case 1:
            return hash % 1024;
        case 2:
            return hash % 4273;
        case 3:
            return hash % 582;
        case 4:
            return hash % 8362;
        case 5:
            return hash % 2743;
        default:
            return -1;
        }
    }
}
