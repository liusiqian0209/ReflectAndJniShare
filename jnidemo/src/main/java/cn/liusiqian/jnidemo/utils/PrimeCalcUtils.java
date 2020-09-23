package cn.liusiqian.jnidemo.utils;

public class PrimeCalcUtils {
  public static int countPrimeJava(int target) {

    int count = 0;
    for (int i = 2; i <= target; i++) {
      if (isPrime(i)) {
        count++;
      }
    }
    return count;
  }

  private static boolean isPrime(int num) {
    for (int i = 2; i < num; i++) {
      if (num % i == 0) {
        return false;
      }
      if (i * i > num) {
        return true;
      }
    }
    return true;
  }

}
