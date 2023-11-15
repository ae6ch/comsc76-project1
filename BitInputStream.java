import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream implements AutoCloseable{

   private FileInputStream input;

   public BitInputStream(String fileName) {
      try {
         this.input = new FileInputStream(fileName);
      } catch (Exception e) {
         System.out.println("File not found");
      }
   }
   public BitInputStream(FileInputStream fh) {
      this.input = fh;
   }


   public String printBits(byte c) {
      StringBuilder sb = new StringBuilder();
      for (int i = 7; i >= 0; i--) { 
         sb.append(c>>i &1);
      }
         return sb.toString();

   }

   public void close() {
      try {
         input.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
