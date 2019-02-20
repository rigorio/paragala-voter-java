package rigor.io.paragala.voter.nominees;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Nominee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Parsed(index=1)
  private String title;
  @Parsed(index = 2)
  private String category;
  @Parsed(index = 0)
  private String company;
  @Parsed(index = 3)
  private Long tally = 0L; //hack

  public Nominee(String title, String company, String category) {
    this.title = title;
    this.company = company;
    this.category = category;
  }

}
