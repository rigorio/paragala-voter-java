package rigor.io.paragala.voter.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String username;
  @Getter(AccessLevel.NONE)
  private String password;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
