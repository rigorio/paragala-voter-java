package rigor.io.login;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateTimeTest {
  private LocalDate start;
  private LocalDate end;
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

  @Test
  public void asdfasdf(){
    start = LocalDate.parse("2018-01-01"); // default values
    end = LocalDate.parse("2017-01-01");

    System.out.println(end.isBefore(start));
  }

}
