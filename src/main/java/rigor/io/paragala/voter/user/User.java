package rigor.io.paragala.voter.user;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String username;
  @Getter(AccessLevel.NONE)
  private String password;
  private boolean superAdmin;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
