package rigor.io.paragala.voter.voting;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * please someone take me out on a date
 */
@Service
public class DatingService {

  private LocalDateTime start;
  private LocalDateTime end;

  public DatingService() {
    start = LocalDateTime.parse("2018-01-01T00:00"); // default values
    end = LocalDateTime.parse("2099-12-22T23:00");
  }

  public void setStartEnd(String s, String e){
    start = LocalDateTime.parse(s.replace("Z", "")); // for some reason, there was a letter z
    end = LocalDateTime.parse(e.replace("Z", ""));   // idk why
  }

  public String getStartDateTime() {
    return start.toString();
  }

  public String getEndDateTime() {
    return end.toString();
  }

  public boolean isAllowed() {
    LocalDateTime now = LocalDateTime.now();
    return now.isAfter(start) && now.isBefore(end);
  }

  public String setStart(String time) {
    start = LocalDateTime.parse(time);
    return time;
  }

  public String setEnd(String time) {
    end = LocalDateTime.parse(time);
    return time;
  }
}
