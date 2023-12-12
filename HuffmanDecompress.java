import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Decompress_a_File
 * COMSC76 Team 1
 * 11/29/23
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
   private void fillQueue(BufferedInputStream inputStream, BlockingQueue<Character> inputPipe)
         throws IOException, InterruptedException {
      // Loop and read all the bytes. 8 bytes free needed in the pipe per read
      while ((inputStream.available() > 0) && (inputPipe.remainingCapacity() > 8)) {
         for (char c : getBits(inputStream.read()).toCharArray()) {
            inputPipe.put(c);
         }

      }
   }

   /**
    * Decompress the file and write it to the output file
    * 
    * @return True if the file was successfully decompressed, false otherwise
    */
   public boolean write() {
      try (FileOutputStream fileOutput = new FileOutputStream(outFileName)) {
         BlockingQueue<Character> inputPipe = new LinkedBlockingQueue<>(MAX_INPUT_BUFFER);

         Map<String, Integer> codeMap = IntStream.range(0, SIZE) // Working with a map is easier then the array
               .filter(j -> codeChar[j] != null)
               .boxed()
               .collect(Collectors.toMap(j -> codeChar[j], Function.identity()));

         String window = "";
         // Loop until we have read all the bits from the file and the pipe is empty
         while ((input.available() > 0) || (!inputPipe.isEmpty())) {
            if (inputPipe.remainingCapacity() > 8) // Add more bits to the processing pipe, min of 8 bytes needed
               fillQueue(input, inputPipe);
            window += inputPipe.take();
            if (codeMap.containsKey(window)) { // If the current string is a key in the map, write the value
               int j = codeMap.get(window);
               fileOutput.write(j);
               md.update((byte) j);
               window = "";
            }
            // Compare currently calculated digest to the one read from the file
            if (MessageDigest.isEqual(((MessageDigest) md.clone()).digest(), digest)) {
               return true;
            }
         }
      } catch (Exception e) { // TODO catch specific exceptions
         System.out.println(e.getMessage());
         System.exit(1);
      }
      return false;
   }

   /**
    * Convert a int to a string of bits
    * 123 becomes "01111011"
    * 255 becomes "11111111"
    *
    * @param c
    * @return The string of bits
    */
   private static String getBits(int c) {
      /*
       * There is a static method in Integer that does almost the same thing, but it
       * "helpfully" removes leading zeros.
       */
      StringBuilder output = new StringBuilder();
      for (int i = 7; i >= 0; i--) {
         output.append((c >> i) & 1);
      }
      return output.toString();
   }
}