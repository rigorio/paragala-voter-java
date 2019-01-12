package rigor.io.paragala.voter.voting;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
public class Nominee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String title;
  private String company;
  private String category;
  @OneToMany(targetEntity = VoteForm.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<VoteForm> votes;

  public Nominee() {
  }

  public Nominee(String title, String company, String category) {
    this.title = title;
    this.company = company;
    this.category = category;
  }
}
