package rigor.io.paragala.voter.voting;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class VoteForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Getter(AccessLevel.PRIVATE)
    private String uniqueId;
    @Getter(AccessLevel.PRIVATE)
    private String voterCode;
    private String school;

    @OneToMany(targetEntity = Nominee.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Nominee> nominees;

    public VoteForm() {
    }

    public VoteForm(String name, String uniqueId, String voterCode, String school) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.voterCode = voterCode;
        this.school = school;
    }
}
