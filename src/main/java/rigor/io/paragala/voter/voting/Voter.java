package rigor.io.paragala.voter.voting;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String uniqueId;
    private String voterCode;
    private String school;
    private boolean status;
//    private List<VoteForm> votes;


    public Voter(String name, String uniqueId, String voterCode, String school, boolean status) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.voterCode = voterCode;
        this.school = school;
        this.status = status;
    }

    public boolean canVote(){
        return status;
    }

}
