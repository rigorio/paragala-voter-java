package rigor.io.login;

import org.junit.Test;

import java.time.LocalDateTime;

public class LocalDateTimeTest {

  @Test
  public void testDateTime() {
    LocalDateTime now = LocalDateTime.now();
    System.out.println(now);
    LocalDateTime start = LocalDateTime.parse("2019-01-27T15:46:00");

    String owari = "2019-03-27T15:46:00";
    LocalDateTime end = LocalDateTime.parse(owari);
    System.out.println(now.isBefore(end) && now.isAfter(start));
    System.out.println(start);
  }

}
