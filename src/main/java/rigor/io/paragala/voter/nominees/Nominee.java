package rigor.io.paragala.voter.nominees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nominee {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String company;
  private String category;

  public Nominee(String name, String company, String category) {
    this.name = name;
    this.company = company;
    this.category = category;
  }

}
