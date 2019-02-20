package rigor.io.paragala.voter.voting;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * please someone take me out on a date
 */
@Service
public class DatingService {

  private LocalDate start;
  private LocalDate end;

  public DatingService() {
    start = LocalDate.parse("2018-01-01"); // default values
    end = LocalDate.parse("2099-12-22");
  }

  public void setStartEnd(String s, String e){
    start = LocalDate.parse(s); // for some reason, there was a letter z
    end = LocalDate.parse(e);   // idk why
  }

  public String getStartDateTime() {
    return start.toString();
  }

  public String getEndDateTime() {
    return end.toString();
  }

  public boolean isAllowed() {
    LocalDate now = LocalDate.now();
    LocalDate start = this.start;
    LocalDate end = this.end;
    return now.isAfter(start) && now.isBefore(end);
  }

  public String setStart(String time) {
    start = LocalDate.parse(time);
    return time;
  }

  public String setEnd(String time) {
    end = LocalDate.parse(time);
    return time;
  }
}
