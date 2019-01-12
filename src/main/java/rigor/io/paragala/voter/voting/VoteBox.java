package rigor.io.paragala.voter.voting;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoteBox {

  private VoteFormRepository voteFormRepository;
  private NomineeRepository nomineeRepository;


  public VoteBox(VoteFormRepository voteFormRepository, NomineeRepository nomineeRepository) {
    this.voteFormRepository = voteFormRepository;
    this.nomineeRepository = nomineeRepository;
  }

  public void vote(VoteForm voteForm) {
    voteFormRepository.save(voteForm);
  }

  public List<VoteForm> viewVotes() {
    return voteFormRepository.findAll();
  }

  public List<Nominee> viewNominees() {
    return nomineeRepository.findAll();
  }

  public Map<String, Object> viewFilter(@Nullable String school,
                         @Nullable String category) {
    List<Nominee> nominees = viewNominees();
    Map<String, Object> perCategory = new HashMap<>();
    for (String c : viewCategories()) {
      Map<String, Object> nomineeMap = new HashMap<>();
      for (Nominee nominee : nominees) {
        if (nominee.getCategory().equals(c)) {
          nomineeMap.put("title", nominee.getTitle());
          nomineeMap.put("company", nominee.getCompany());
          nomineeMap.put("tally", nominee.getVotes().size());
        }
      }
      perCategory.put("category", nomineeMap);
    }

    return perCategory;
  }

  public List<String> viewCategories() {
    return new ArrayList<>();
  }

}
