import java.io.File;
public class Decompress_a_file {
   public static void main(String[] args) {
      if ((args.length != 2)) {
         System.out.println("Usage: java decompress input_file output_file");
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


      HuffmanDecompress h = new HuffmanDecompress(args[0], args[1]);
     h.write();
   }
}