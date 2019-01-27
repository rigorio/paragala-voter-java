package rigor.io.login;

import org.junit.Test;

import java.time.LocalDateTime;

public class LocalDateTimeTest {

  @Test
  public void testDateTime() {
    LocalDateTime now = LocalDateTime.now();
    System.out.println(now);
    LocalDateTime start = LocalDateTime.parse("2017-04-23T09:00");

    String owari = "2020-12-22T23:00";
    LocalDateTime end = LocalDateTime.parse(owari);
    System.out.println(now.isBefore(end) && now.isAfter(start));
    System.out.println(start);
  }

}
