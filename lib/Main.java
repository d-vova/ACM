// MAIN SOLUTION TEMPLATE ////////////////////////////////////////////////
// When competition begins, you should:
//    - create new java project with name "ACM"
//    - add class "Main" and type in the following code

import java.io.*;
import java.util.*;

public class Main {
   static String name = "";
   static Scanner in;
   static ArrayList<String> out = new ArrayList<String>();

   static String join(String delim, Object[] a) {
      String d = "", r = "";
      for (Object o: a) { r += d + o; d = delim; }
      return r;
   }

   static void solution() {
      //out.add(name);
      while(in.hasNext() && !in.hasNext("")) {
      }
   }

   public static void main(String[] args) throws Exception {
      in = new Scanner(new FileReader(name+".in"));
      solution();
      PrintWriter pw = new PrintWriter(new FileWriter(name+".out"));
      pw.print(join("\n", out.toArray()));
      pw.close();
      //System.out.print(join("\n", out.toArray()));
      //System.out.print(checkOut());
   }
   
   // OPTIONAL ///////////////////////////////////////////////////////////////
   // Greatest Common Divisor and Least Common Multiple
   static long gcd(long a, long b) { while (b > 0) { long t = b; b = a % b; a = t; } return a; }
   static long lcm(long a, long b) { return a * b / gcd(a, b); }
      
   // Sieve of Eratosthenes to mark prime numbers
   public static boolean[] primeSieve(int n) {
      boolean[] prime = new boolean[n];
      Arrays.fill(prime, true);
      if (n > 0) prime[0] = false; if (n > 1) prime[1] = false;
      for (int i = 2; i < Math.sqrt(n); i++) for (int j = 2; i*j < n; j++) prime[i*j] = false;
      return prime;
   }
   
   static boolean checkOut() throws Exception {
      FileReader rout = new FileReader(name+".out");
      FileReader rsmpl = new FileReader(name+".smpl");
      return rout.read() == rsmpl.read();
   }
   // END OF OPTIONAL ////////////////////////////////////////////////////////
}

// Good job!!!
//    - press <CTRL>-A and then <CTRL>-C to copy the template
//    - for each problem in the set:
//       - click on "ACM" project folder in Package Explorer
//       - File > New > File   =>  name it "<problem>.in"
//       - File > New > File   =>  name it "<problem>.out"
//       - File > New > Class  =>  name it "<problem>"
//       - press <CTRL>-A and then <CTRL>-V to paste the template
//       - change "Main" and value of "name" to "<problem>"
// Excellent!!!
//    - add example input to "<problem>.in" files
//    - check for accuracy after all "<problem>.in" files are finished
// Almost there!!!
//    - add class "G" and type in everything before "TOOLS" section
//    - double check for accuracy
