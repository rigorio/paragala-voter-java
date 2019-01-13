package rigor.io.paragala.voter.voting.machine;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteBoxService {

  private VoteFormRepository voteFormRepository;
  private NomineeRepository nomineeRepository;
  private VoterRepository voterRepository;

  public VoteBoxService(VoteFormRepository voteFormRepository, NomineeRepository nomineeRepository, VoterRepository voterRepository) {
    this.voteFormRepository = voteFormRepository;
    this.nomineeRepository = nomineeRepository;
    this.voterRepository = voterRepository;
  }

  public void vote(List<Nominee> nominees, Voter voter) {
    Long voterId = voter.getId();
    List<VoteForm> voteForms = new ArrayList<>();
    for (Nominee nominee: nominees) {
      voteForms.add(new VoteForm(voterId, nominee.getId()));
    }
    voteFormRepository.saveAll(voteForms);
  }

}
