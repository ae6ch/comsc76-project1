import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.io.File;

/**
 * Implementation of Huffman compression
 * 
 * This class compresses a file using Huffman compression. It is based on the
 * code in the textbook.
 * A Huffman tree is built from the file, and the codes are written to the
 * compressed file along with a MessageDigest of the uncompressed file.
 * The MessageDigest is a check against corrupt files, and it also solves the
 * problem of 0's stuffed at the end of the file matching the Huffman code for
 * 0.
 * 
 */
public class HuffmanCompress {
  private static final int SIZE = 256;
  Tree tree;
  String[] codes;
  int[] codeCounts;
  String inFileName;
  String outFileName;
  long uncompressedSize;
  String hashAlgorithm;
  MessageDigest md = null;

  byte[] digest = null; // The digest of the file, populated by getCharacterFrequency

  /**
   * Constructor for file compression
   * 
   * @param inFileName  file to be compressed
   * @param outFileName compressed file
   */
  public HuffmanCompress(String inFileName, String outFileName, String hashAlgorithm) {

    this.inFileName = inFileName;
    this.outFileName = outFileName;
    this.hashAlgorithm = hashAlgorithm;
    try {
      md = MessageDigest.getInstance(hashAlgorithm);
    } catch (NoSuchAlgorithmException e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
    encode(); // build the Huffman tree and get the codes
  }

  /**
   * Print Huffman codes for characters
   * 
   */
  public void printCodes() {
    System.out.printf("%-15s%-15s%-15s%-15s\n", "ASCII Code", "Character", "Frequency", "Code");

    for (int i = 0; i < codes.length; i++)
      if (codeCounts[i] != 0) // (char)i is not in text if counts[i] is 0
        System.out.printf("%-15d%-15s%-15d%-15s\n",
            i, (char) i + "", codeCounts[i], codes[i]);

  }

  /**
   * Encode the file
   * 
   * Reads the file, creates the Huffman tree, and gets the codes
   */
  private void encode() {
    codeCounts = getCharacterFrequency(inFileName); // Count frequency
    tree = getHuffmanTree(codeCounts); // Create a Huffman tree
    codes = getCode(tree.root); // Get codes

  }

  /**
   * Write the compressed file
   * 
   * Reads the file again, and writes the compressed file
   */
  public void writeCompressed() {
    try (
        FileOutputStream fileOutput = new FileOutputStream(outFileName);
        ObjectOutputStream objOut = new ObjectOutputStream(fileOutput);
        BitOutputStream bitOut = new BitOutputStream(fileOutput);
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(inFileName))) {
          // Write the header to the compressed file
          // Header contains the hash algorithm, the digest, and the codes
      HuffmanHeader header = new HuffmanHeader(hashAlgorithm, digest, codes);
      objOut.writeObject(header);

      // Write the compressed file by reading from the file 1 byte at a time and looking up the code for each byte and writing it to the compressed file
      while (input.available() > 0) {
        int r = input.read();
        bitOut.writeBit(codes[r]);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
      System.exit(1);

    }
  }

  /**
   * Get Huffman codes for the characters
   * 
   * This method is called once after a Huffman tree is built
   * 
   * @param root The root of the Huffman tree
   * @return The array of Huffman codes
   */
  private static String[] getCode(Node root) {
    if (root == null)
      return null;
    String[] codes = new String[SIZE];
    assignCode(root, codes);
    return codes;
  }

  /**
   * Recursively get codes to the leaf node
   * 
   * @param root  The root of the Huffman tree
   * @param codes The array for storing codes
   */
  private static void assignCode(Node root, String[] codes) {
    if (root.left != null) {
      root.left.code = root.code + "0";
      assignCode(root.left, codes);

      root.right.code = root.code + "1";
      assignCode(root.right, codes);
    } else {
      codes[(int) root.element] = root.code;
    }
  }

  /**
   * Get a Huffman tree from the codes
   * 
   * @param counts The array that contains the character frequencies
   * @return The Huffman tree
   */
  private static Tree getHuffmanTree(int[] counts) {
    // Create a heap to hold trees
    Heap<Tree> heap = new Heap<>(); // Defined in Listing 24.10
    for (int i = 0; i < counts.length; i++) {
      if (counts[i] > 0)
        heap.add(new Tree(counts[i], (char) i)); // A leaf node tree
    }

    while (heap.getSize() > 1) {
      Tree t1 = heap.remove(); // Remove the smallest weight tree
      Tree t2 = heap.remove(); // Remove the next smallest weight
      heap.add(new Tree(t1, t2)); // Combine two trees
    }

    return heap.remove(); // The final tree
  }

  /**
   * Get the frequency of the characters
   *
   * @param inFileName The name of the input file
   * @return The array of frequencies
   */
  private int[] getCharacterFrequency(String inFileName) {
    int[] counts = new int[SIZE];
    long fileSize = new File(inFileName).length();
    try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(inFileName))) {
      while (input.available() > 0) {
        int r = input.read();
        counts[r]++; // Read byte and increase count
        uncompressedSize++; // Count the size of the uncompressed file as we read
        md.update((byte) r); // Update the digest
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    System.out.printf("\nUncompressed size: %d\n", uncompressedSize);

    if (md != null) {
      digest = md.digest();
    } else {
      System.out.println("Message digest is null");
      System.exit(1);
    }
    System.out.println(hashAlgorithm + " Digest: " + String.format("%02X", new BigInteger(1, digest)));

    return counts;
  }
}