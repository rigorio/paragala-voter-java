package rigor.io.paragala.voter.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class School {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String school;

  public School(String school) {
    this.school = school;
  }
}
