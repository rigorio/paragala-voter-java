package rigor.io.paragala.voter.voting;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteBox {

  private VoteFormRepository voteFormRepository;


  public VoteBox(VoteFormRepository voteFormRepository) {
    this.voteFormRepository = voteFormRepository;
  }

  public void vote(VoteForm voteForm) {
    voteFormRepository.save(voteForm);
  }

  public List<VoteForm> viewVotes() {
    return voteFormRepository.findAll();
  }

}
