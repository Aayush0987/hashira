import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

   // ✅ Inner class that was missing
   static class ParsedInput {
      int n;
      int k;
      List<BigInteger> roots = new ArrayList<>();
   }

   static BigInteger parseInBase(String var0, int var1) {
      var0 = var0.trim().toLowerCase();
      BigInteger var2 = BigInteger.ZERO;
      BigInteger var3 = BigInteger.valueOf((long)var1);

      for(int var4 = 0; var4 < var0.length(); ++var4) {
         char var5 = var0.charAt(var4);
         int var6 = Character.digit(var5, var1);
         if (var6 < 0) {
            throw new IllegalArgumentException("Invalid digit '" + var5 + "' for base " + var1);
         }
         var2 = var2.multiply(var3).add(BigInteger.valueOf((long)var6));
      }
      return var2;
   }

   static List<BigInteger> polyMul(List<BigInteger> var0, List<BigInteger> var1) {
      int var2 = var0.size();
      int var3 = var1.size();
      ArrayList<BigInteger> var4 = new ArrayList<>(Collections.nCopies(var2 + var3 - 1, BigInteger.ZERO));

      for(int var5 = 0; var5 < var2; ++var5) {
         for(int var6 = 0; var6 < var3; ++var6) {
            var4.set(var5 + var6, var4.get(var5 + var6).add(var0.get(var5).multiply(var1.get(var6))));
         }
      }
      return var4;
   }

   static BigInteger polyEval(List<BigInteger> var0, BigInteger var1) {
      BigInteger var2 = BigInteger.ZERO;
      for(int var3 = var0.size() - 1; var3 >= 0; --var3) {
         var2 = var2.multiply(var1).add(var0.get(var3));
      }
      return var2;
   }

   static List<BigInteger> polyFromRoots(List<BigInteger> var0) {
      List<BigInteger> poly = new ArrayList<>();
      poly.add(BigInteger.ONE);

      for (BigInteger root : var0) {
         List<BigInteger> factor = Arrays.asList(root.negate(), BigInteger.ONE);
         poly = polyMul(poly, factor);
      }
      return poly;
   }

   static ParsedInput parseInput(String var0) {
      ParsedInput var1 = new ParsedInput();
      Matcher var2 = Pattern.compile("\"k\"\\s*:\\s*(\\d+)").matcher(var0);
      if (!var2.find()) throw new IllegalArgumentException("k not found");
      var1.k = Integer.parseInt(var2.group(1));

      Matcher var3 = Pattern.compile("\"n\"\\s*:\\s*(\\d+)").matcher(var0);
      if (!var3.find()) throw new IllegalArgumentException("n not found");
      var1.n = Integer.parseInt(var3.group(1));

      // Find all numbered entries in the JSON (not necessarily sequential)
      Pattern entryPattern = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{[^}]*\\}");
      Matcher entryMatcher = entryPattern.matcher(var0);
      
      while (entryMatcher.find()) {
         String entryKey = entryMatcher.group(1);
         String var7 = entryMatcher.group();
         
         Matcher var8 = Pattern.compile("\"base\"\\s*:\\s*\"(\\d+)\"").matcher(var7);
         Matcher var9 = Pattern.compile("\"value\"\\s*:\\s*\"([0-9a-zA-Z]+)\"").matcher(var7);

         if (var8.find() && var9.find()) {
            int var10 = Integer.parseInt(var8.group(1));
            String var11 = var9.group(1);
            if (var10 >= 2 && var10 <= 36) {
               BigInteger var12 = parseInBase(var11, var10);
               var1.roots.add(var12);
               continue;
            }
            throw new IllegalArgumentException("Unsupported base: " + var10 + " in entry " + entryKey);
         }
         throw new IllegalArgumentException("base/value missing in entry " + entryKey);
      }
      return var1;
   }

   public static void main(String[] args) throws Exception {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String line;
      while((line = br.readLine()) != null) {
         sb.append(line).append('\n');
      }
      br.close();

      String input = sb.toString();
      ParsedInput parsed = parseInput(input);

      int degree = parsed.k - 1;
      if (degree <= 0) {
         System.out.println("k must be >= 1. Got k=" + parsed.k);
         return;
      }
      if (parsed.roots.size() < degree) {
         System.out.println("Not enough roots to build degree " + degree + " polynomial. Need " + degree + ", got " + parsed.roots.size());
         return;
      }

      List<BigInteger> chosen = parsed.roots.subList(0, degree);
      List<BigInteger> poly = polyFromRoots(chosen);

      System.out.println("Parsed:");
      System.out.println("  n = " + parsed.n + ", k = " + parsed.k + "  => degree m = " + degree);
      for (int i = 0; i < parsed.roots.size(); ++i) {
         System.out.println("  root[" + (i + 1) + "] = " + parsed.roots.get(i));
      }

      System.out.println("\nMonic polynomial coefficients (highest degree -> constant):");
      StringBuilder coeffs = new StringBuilder();
      for (int i = poly.size() - 1; i >= 0; --i) {
         coeffs.append(poly.get(i));
         if (i != 0) coeffs.append(" ");
      }
      System.out.println(coeffs.toString());

      System.out.println("\nP(x) = " + pretty(poly));

      if (parsed.roots.size() > degree) {
         System.out.println("\nVerification on extra roots:");
         for (int i = degree; i < parsed.roots.size(); ++i) {
            BigInteger root = parsed.roots.get(i);
            BigInteger val = polyEval(poly, root);
            System.out.println("  P(" + root + ") = " + val + (val.equals(BigInteger.ZERO) ? "  [OK]" : "  [FAIL]"));
         }
      }
   }

   static String pretty(List<BigInteger> poly) {
      StringBuilder sb = new StringBuilder();
      int deg = poly.size() - 1;
      for (int i = deg; i >= 0; --i) {
         BigInteger coeff = poly.get(i);
         if (!coeff.equals(BigInteger.ZERO)) {
            if (sb.length() > 0) {
               sb.append(coeff.signum() >= 0 ? " + " : " - ");
            } else if (coeff.signum() < 0) {
               sb.append("-");
            }
            BigInteger absCoeff = coeff.abs();
            if (i == 0) {
               sb.append(absCoeff);
            } else if (i == 1) {
               if (!absCoeff.equals(BigInteger.ONE)) sb.append(absCoeff).append("*");
               sb.append("x");
            } else {
               if (!absCoeff.equals(BigInteger.ONE)) sb.append(absCoeff).append("*");
               sb.append("x^").append(i);
            }
         }
      }
      if (sb.length() == 0) sb.append("0");
      return sb.toString();
   }
}