package rigor.io.paragala.voter.nominees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Nominee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String title;
  private String category;
  private String company;

  public Nominee(String title, String company, String category) {
    this.title = title;
    this.company = company;
    this.category = category;
  }

}
