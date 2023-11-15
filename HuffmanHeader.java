import java.io.Serializable;
import java.security.MessageDigest;

/**
 * A serializable class to store the header information for a Huffman compressed
 * file
 * 
 */
public class HuffmanHeader implements Serializable {
   String digestAlgorithm; // the algorithm used to create the digest
   byte[] digest; // the digest of the file
   String[] codes; // the codes for each character, index is the character value

   public HuffmanHeader(String digestAlgorithm, byte[] digest, String[] codes) {
      this.codes = codes;
      this.digest = digest;
      this.digestAlgorithm = digestAlgorithm;
   }
}