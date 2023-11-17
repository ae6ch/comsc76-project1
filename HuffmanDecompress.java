import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of Huffman decompression
 * 
 */
public class HuffmanDecompress {
   private static final int SIZE = 256; // Number of characters in the ASCII set
   private static final int MAX_INPUT_BUFFER = 1024; // Maximum number of bytes to read at a time
   private BufferedInputStream input;
   private ObjectInputStream objIn;
   private FileInputStream fIn;
   private String[] codeChar;
   byte[] digest;
   String inFileName;
   String outFileName;
   MessageDigest md = null;

   public HuffmanDecompress(String inFileName, String outFileName) {
      this.inFileName = inFileName;
      this.outFileName = outFileName;

      readHuffHeader();
   }

   /**
    * Get the digest of the file
    * 
    * @return The digest of the file
    */

   public byte[] getDigest() {
      return digest;
   }

   /**
    * Get the hash algorithm used to create the digest
    * 
    * @return The hash algorithm
    */
   public String getHashAlgorithm() {
      return md.getAlgorithm();
   }

   /**
    * Read the Huffman header from the file
    *
    */
   private void readHuffHeader() {
      try {
         openFile();
         HuffmanHeader header = (HuffmanHeader) objIn.readObject();
         codeChar = header.codes;
         digest = header.digest;

         md = MessageDigest.getInstance(header.digestAlgorithm);

      } catch (ClassNotFoundException | IOException | NoSuchAlgorithmException e) {
         System.out.println(e.getMessage());
         System.exit(1);
      }

   }

   /**
    * Open the file for reading, and create the input streams
    * 
    * @throws IOException
    */
   private void openFile() throws IOException {
      fIn = new FileInputStream(inFileName);
      input = new BufferedInputStream(fIn);
      objIn = new ObjectInputStream(fIn);
   }

   /**
    * Fill up the queue with bits from the file
    * 
    * @param inputStream The file to read from
    * @param inputPipe   The queue to fill
    * @throws IOException
    * @throws InterruptedException
    */
   public void fillQueue(BufferedInputStream inputStream, BlockingQueue<Character> inputPipe)
         throws IOException, InterruptedException {

      while ((inputStream.available() > 0) && (inputPipe.remainingCapacity() > 8)) { // 8 bytes needed in the pipe per
                                                                                     // byte read from file
         for (char c : getBits(inputStream.read()).toCharArray()) {
            inputPipe.put(c);
         }

      }
   }

   /**
    * Decompress the file and write it to the output file
    * 
    */
   public boolean write() {
      try (FileOutputStream fileOutput = new FileOutputStream(outFileName)) {
         BlockingQueue<Character> inputPipe = new LinkedBlockingQueue<>(MAX_INPUT_BUFFER);

         HashMap<String, Integer> codeMap = new HashMap<>(); // Create a map of codes to characters
         for (int j = 0; j < SIZE; j++) {
            if (codeChar[j] != null) {
               codeMap.put(codeChar[j], j);
            }
         }

         String window = "";

         while ((input.available() > 0) || (!inputPipe.isEmpty())) { // Loop until we have read all the bits from the
                                                                     // file and the pipe is empty
            if (inputPipe.remainingCapacity() > 8) // Add more bits to the processing pipe, minium of 8 bytes needed to
                                                   // call fillQueu
               fillQueue(input, inputPipe);

            window += inputPipe.take();

            if (codeMap.containsKey(window)) { // If the current string is a key in the map, write the value

               int j = codeMap.get(window);
               fileOutput.write(j);
               md.update((byte) j);
               window = "";
            }

            if (MessageDigest.isEqual(((MessageDigest) md.clone()).digest(), digest)) { // Compare currently
                                                                                        // calculated digest to the
                                                                                        // one read from the file
               return true;
            }
         }
      } catch (Exception e) { // TODO catch specific exceptions
         System.out.println(e.getMessage());
         System.exit(1);
      }
      return false;
   }

   public static String getBits(int c) {
      StringBuilder output = new StringBuilder();
      for (int i = 7; i >= 0; i--) { // There is a static method in Integer that does almost the same thing, but it
         // "helpfully" removes leading zeros.
         output.append((c >> i) & 1);
      }
      return output.toString();

   }
}