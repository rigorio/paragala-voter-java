package rigor.io.login;

import org.junit.Before;
import org.junit.Test;
import rigor.io.paragala.voter.voting.RandomString;
import rigor.io.paragala.voter.voting.RandomStringGenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RandomGenTest {

  @Before
  public void init() {
    jvmWarmup();
  }

  @Test
  public void testt() {
    int number = 50;

    Long warmpupp = System.currentTimeMillis();
    new RandomStringGenerator().generateCodes(number);
    System.out.println("Warmup " + (System.currentTimeMillis() - warmpupp));

  }

  @Test
  public void genCode() {
    String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
    RandomString gen = new RandomString(10, new SecureRandom(), easy);
    Set<String> randoms = new HashSet<>();
    for (int j = 0; j < 100; j++) {
      for (int i = 0; i < 100; i++) {
        String e = gen.nextString();
        if (!randoms.add(e))
          System.out.println("DIO DA" + j);
      }
    }


    System.out.println(randoms.size());

  }

  public void jvmWarmup() {
    int[] warmup = new int[10000];
    for (int i = 0; i < 10000; i++) {
      warmup[i] = i;
    }
    System.out.println("warm up");
  }

}
