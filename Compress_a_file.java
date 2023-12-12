import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;


  public class Compress_a_File {
  public static void main(String[] args) {
    String hashAlgorithm = "SHA-256";
    if ((args.length != 2) && (args.length != 3)) {
      System.out.println("Usage: java Compress infile outfile [hash]");
      System.out.println("  infile is the file to be compressed");
      System.out.println("  outfile is the compressed file");
      System.out.println("  hash algorithm is the hash algorithm to use");
      System.out.println("  valid hash algorithms are: " + getHashAlgorithms());
      System.exit(1);
    }
    File inFile = new File(args[0]);
    if (!inFile.exists()) {
      System.out.println("File " + args[0] + " does not exist");
      System.exit(1);
    }
    if (!inFile.isFile()) {
      System.out.println("File " + args[0] + " is not a file");
      System.exit(1);
    }
    if (!inFile.canRead()) {
      System.out.println("File " + args[0] + " cannot be read");
      System.exit(1);
    }
    File outFile = new File(args[1]);
    if (outFile.exists()) {
      System.out.println("File " + args[1] + " already exists");
      System.exit(1);
    }
    if (outFile.isDirectory()) {
      System.out.println("File " + args[1] + " is a directory");
      System.exit(1);
    }
 
    if(args.length == 3) { // Test if the hash algorithm is valid
      try { 
        hashAlgorithm = args[2];
        MessageDigest.getInstance(hashAlgorithm); // no need to store the MessageDigest object, we just want to see if it throws an exception
      } catch(NoSuchAlgorithmException e){
        System.out.println(e.getMessage());
        System.exit(1);
      }
    } 

    HuffmanCompress h = new HuffmanCompress(args[0], args[1],hashAlgorithm);
    h.writeCompressedThread();


   }

   /**
    * Get a list of the MessageDigest algorithms available
    * 
    * @return string of available hash algorithms
    */
   private static String getHashAlgorithms() {
      ArrayList<String> hashAlgorithms = new ArrayList<>();
      Provider[] providers = Security.getProviders();
      for (Provider provider : providers) {
            // Get a list of the MessageDigest algorithms for each provider
            Enumeration<Object> algorithms = provider.keys();
            while (algorithms.hasMoreElements()) {
                String algorithm = (String) algorithms.nextElement();
                if (algorithm.startsWith("MessageDigest.")) {
                    hashAlgorithms.add(algorithm.substring("MessageDigest.".length()).split(" ")[0]);
                }
              }
      }
      return hashAlgorithms.toString();
    }
  }