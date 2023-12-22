import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ExtendibleHashing {

  /**
   * To execute the program you have to use:
   *
   * java ExtendibleHashing.java <Max Bucket Size> <Data File>
   *
   * For the exercise write the code of the methods Directory.addEntry and
   * Directory.toString(). 
   * You will have to complete the Directory class to provide functionality to these functions.
   * The Hashing class contains some helper functions to
   * work with the byte datatype.
   *
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Error: 2 parameters expected: Bucketsize, data-file");
      System.exit(-1);
    }

    int maxBucketSize = 0;
    try {
      maxBucketSize = Integer.parseInt(args[0]);
    } catch (Exception e) {
      System.out.println("The first argument must be an integer.");
      System.exit(1);
    }
    Directory d = new Directory(maxBucketSize);
    hashFileToDirectory(args[1], d);
    System.out.println(d.toString());
  }

  /**
   * Reads a file; calculates the hash of each line and adds it to the directory
   * 
   * @param file      the path to the file to read
   * @param directory the directory to store the hashes in
   * @throws IOException
   */
  private static void hashFileToDirectory(String file, Directory directory) throws IOException {
    System.out.println("Reading file: " + file);
    List<String> lines = Files.readAllLines(Paths.get(file));
    for (String line : lines) {
      directory.addEntry(Integer.valueOf(line.hashCode() % 255).byteValue(), line);
    }

  }

  //////////////////////////////////////////////////////////////////////////////
  // Below are some helper functions that can be used to modify/display bits
  //////////////////////////////////////////////////////////////////////////////

  /**
   * Returns only the first X significant bits of the byte value
   * 
   * @param value The value to be modified.
   * @param x     How many bits to return
   * @return A byte representing only the first x significant bits of value.
   */
  static byte getXSignificantBits(byte value, int x) {
    // Create mask
    byte mask = 0;
    for (int i = 0; i < 8; ++i) {
      if (i >= 8 - x) {
        mask |= (1 << i);
      }
    }
    String maskStr = ExtendibleHashing.getBooleanString(mask);
    String valuexMask = ExtendibleHashing.getBooleanString((byte) (value & mask));
    return (byte) (value & mask);
  }

  /**
   * Flips the byte order between little endian and big endian.
   * 
   * @param value The byte to reverse
   * @return The reversed byte
   */
  static byte reverseByteOrder(byte value) {
    byte b = 0;
    for (int i = 0; i < 8; ++i) {
      b <<= 1;
      b |= (value & 1);
      value >>= 1;
    }
    return b;
  }

  /**
   * Returns the value as Binary string
   * 
   * @param value The value to transform
   * @return The binary string
   */
  static String getBooleanString(int value) {
    return String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
  }

  /**
   * Returns the value as Binary string
   * 
   * @param value The value to transform
   * @return The binary string
   */
  static String getBooleanString(byte value) {
    return getBooleanString(value & 0xFF);
  }

  /**
   * Returns the boolean value of index in values. For example: 3 => 00000011:
   * getBoolIndex(0, 3) = True; getBoolIndex(2, 3) = False i: 76543210
   * 
   * @param index The index to look at (Starting from the least significant bit)
   * @param value The value to get the boolean from
   * @return The boolean value at index in value.
   */
  static Boolean getBoolIndex(int index, byte value) {
    byte mask = 0b1;
    mask <<= index;
    return (mask & value) != 0;
  }
}

/**
 * The Directory class should represent the hashing directory. You have to
 * implement the addEntry and toString() methods. How you actually store and
 * represent the Buckets is up to you.
 *
 * There are relatively simple implementations with a vector of buckets, where
 * each entry in the vector represents a hash prefix.
 */
class Directory {

  private int maxBucketSize; // Maximum Bucket size
  private int d = 0; // Global Depth

  private Vector<DataBucket> bucketVector;


  Directory(int maxBucketSize) {
    System.out.println("Initialized Directory with maximum bucket size: " + maxBucketSize);
    this.maxBucketSize = maxBucketSize;
    this.bucketVector = new Vector<>();
    byte zeroByte = 0;
    this.bucketVector.add(new DataBucket(zeroByte,0));
  }

  public static int kSignificantBitsToInteger(byte value, int depth) {
    if(depth == 0)
      return 0;
    return Integer.parseInt(kSignificantBitsToString(value, depth),2);
  }
  public static String kSignificantBitsToString(byte value, int depth) {
    StringBuilder result = new StringBuilder();
    for (int i = 7; i >= 8-depth; i--) {
      if((value & (1 << i)) != 0)
      {
        result.append("1");
        continue;
      }
      result.append("0");
    }
    return result.toString();
  }

  private byte getHash(String data)
  {
    return Integer.valueOf(data.hashCode() % 255).byteValue();
  }

  private void doubleDirectory()
  {
    //before: a b c d
    //after:  a a b b c c d d
    var bucketVectorSize = bucketVector.size();
    for (int i = 0; i < bucketVectorSize; i++ )
    {
      bucketVector.add(i*2+1, bucketVector.get(i*2));
    }
    d++;
  }

  private void splitBucket(int bucketIndex)
  {
    var bucket = bucketVector.get(bucketIndex);
    var bucketCount = (int)((Math.pow(2,d-bucket.c))/2);
    var newKey =  (byte)(bucket.key + (int)Math.pow(2,8-(bucket.c+1)));
    var newBucket = new DataBucket(newKey,bucket.c+1);
    var tt = bucket.Data.stream().toList();
    for (var entry:
         tt) {

      var hash = getHash(entry);
      var relevantBits = ExtendibleHashing.getXSignificantBits(hash,bucket.c+1);

      if(ExtendibleHashing.getBoolIndex(8 - (bucket.c + 1), relevantBits))
      {
        newBucket.Data.add(entry);
        bucket.Data.remove(entry);
      }
    }

    for (int i = 0; i < bucketCount; i ++)
    {
      int newBucketIndex = i+bucketIndex+bucketCount;
      bucketVector.remove(newBucketIndex);
      bucketVector.add(newBucketIndex,newBucket);
    }
    bucket.c++;
  }

  private byte moveBitsToEnd(byte originalByte, int numBitsToMove) {
    // Ensure numBitsToMove is within the range [0, 8]
    numBitsToMove = Math.min(Math.max(numBitsToMove, 0), 8);
    if(numBitsToMove == 0)
      return 0;

    byte frontBits = (byte) (originalByte >> (8 - numBitsToMove));

    // Shift the remaining bits to the left and set the moved bits to zero
    originalByte <<= numBitsToMove;
    originalByte &= (1 << numBitsToMove) - 1;

    // Combine the shifted bits with the extracted bits
    byte resultByte = (byte) (originalByte | frontBits);

    return resultByte;
  }
  /**
   * This function will be called with the hashed integer value. You may assume
   * that only binary hash values <= 8 bits are inserted.
   * 
   * @param hash The value to be used for hashing.
   * @param data The data point to be inserted.
   *
   */
  void addEntry(byte hash, String data) {
    System.out.println("Inserting '" + data + "' with hash value " + ExtendibleHashing.getBooleanString(hash));

    var dSignificantBits = ExtendibleHashing.getXSignificantBits(hash, d);
    //var bucketIndex = moveBitsToEnd(hash,d);
    var bucketIndex = kSignificantBitsToInteger(hash,d);
    //var bucketIndex = dSignificantBits >> (8-d);
    //byte test = (byte)((dSignificantBits >>> (8-d)) & 0b00000001);
    var bucket =bucketVector.get(bucketIndex);


    if(bucket.Data.size() == maxBucketSize)
    {
      if(bucket.c == d)
      {
        doubleDirectory();
        addEntry(hash,data);
        return;
      }
      splitBucket(bucketVector.indexOf(bucket));
      addEntry(hash,data);
      return;
    }

    bucket.Data.add(data);







    //check global depth
    //take the first d bits from the hash
    /*var directoryKey = kBitsToString(hash,d);
    //check the entry in the directory for that d bit sequence
    //go to the bucket it points too

    var dirBucket = directoryBuckets.get(directoryKey);
    if(dirBucket == null)
    {
      dirBucket = new DirectoryPointer(directoryKey,new DataBucket(directoryKey,maxBucketSize));
      directoryBuckets.put(directoryKey, dirBucket);
    }
    dirBucket.dataBucket.Data.add(data);

    //check if the buckte exedet the maxBucketSize
    // no -> add the item to the bucket
    // yes -> split the bucket by increasing the depth of the bucket
    // check if the new depth exieds the global depth
    // yes -> increase the global depth
    // no -> everything is fine

    System.err.println("ERROR: Directory.addEntry not implemented!");


    System.out.println("======================================");
    System.out.println(toString());
    System.out.println("======================================");*/
  }

  /**
   * This function should present the following: Global Depth All buckets with:
   * Their contained data points Local depth Hash prefixes pointing to them
   * 
   * @return A String representing the above content.
   */
  public String toString() {



    var outputString =
            "Global depth d:" + d + "\n" +
            "Data buckets:\n";

    var buckets = new HashSet<>(bucketVector);

    for (var bucket:
         buckets) {
      outputString = outputString + "  Bucket: C= " + bucket.c + ", Prefix = " + kSignificantBitsToString(bucket.key,bucket.c)  + "\n"
      + "    Data entries:\n      ";
      for(var data:
          bucket.Data)
      {
        outputString = outputString + "\'" +data + "\'" + "\n      ";
      }
      outputString = outputString + "\n";
    }
    return outputString;
  }
}

class DataBucket
{
  public int c;
  public byte key;
  public List<String> Data;

  DataBucket(byte key, int c)
  {
    this.key  = key;
    this.Data = new ArrayList<>();
    this.c = c;
  }

}

class DirectoryPointer
{
  public String key;
  public DataBucket dataBucket;

  public int GetD()
  {
    return key.length();
  }

  DirectoryPointer(String key, DataBucket dataBucket)
  {
    this.key = key;
    this.dataBucket = dataBucket;
  }

}