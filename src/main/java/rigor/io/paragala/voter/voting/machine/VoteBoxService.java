package rigor.io.paragala.voter.voting.machine;

import org.springframework.stereotype.Service;
import rigor.io.paragala.voter.details.SchoolRepository;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.voting.Voter;
import rigor.io.paragala.voter.voting.VoterRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * HACKER MAN
 * every thing here is illegal
 * please don't be like me
 */
@Service
public class VoteBoxService {

  private VoteFormRepository voteFormRepository;
  private NomineeRepository nomineeRepository;
  private VoterRepository voterRepository;
  private SchoolRepository schoolRepository;

  public VoteBoxService(VoteFormRepository voteFormRepository, NomineeRepository nomineeRepository, VoterRepository voterRepository, SchoolRepository schoolRepository) {
    this.voteFormRepository = voteFormRepository;
    this.nomineeRepository = nomineeRepository;
    this.voterRepository = voterRepository;
    this.schoolRepository = schoolRepository;
  }

  public List<Map<String, Object>> bySchool(String s) {
    List<VoteForm> allVotes = voteFormRepository.findAll();
    List<Map<String, Object>> resultsBySchool = new ArrayList<>();
    List<Nominee> nominees = nomineeRepository.findAll();
    for (Nominee nominee : nominees) {
      int tally = 0;
      Map<String, Object> result = new HashMap<>();
      for (VoteForm voteForm : allVotes) {
        Voter voter = voterRepository.findById(voteForm.getVoterId()).get();
        if (voter.getSchool().equals(s)) {
          if (voteForm.getNomineeId().equals(nominee.getId())) {
            tally++;
            result.put("title", nominee.getTitle());
            result.put("company", nominee.getCompany());
            result.put("tally", tally);
            result.put("category", nominee.getCategory());
            resultsBySchool.add(result);
          }
        }
      }
    }
    return resultsBySchool;
  }

  public List<Map<String, Object>> getAllVotes() {
    List<FullNomineeTally> fullNomineeTallies = getFullNomineeTallies();

    List<Map<String, Object>> finalDecision = new ArrayList<>();
    for (String c : getCategories()) {
      for (FullNomineeTally fullNomineeTally : fullNomineeTallies) {
        Map<String, Object> nominee = new HashMap<>();
        if (fullNomineeTally.getCategory().equals(c)) {
          nominee.put("title", fullNomineeTally.getTitle());
          nominee.put("company", fullNomineeTally.getCompany());
          nominee.put("tally", fullNomineeTally.getTally());
          nominee.put("category", c);
          finalDecision.add(nominee);
        }
      }
    }
    return finalDecision;
  }

  public List<FullNomineeTally> getFullNomineeTallies() {
    List<VoteForm> voteForms = voteFormRepository.findAll();
    List<Long> nomineeIds = nomineeRepository.findAll().stream()
        .map(Nominee::getId)
        .collect(Collectors.toList());
    List<NomineeTally> nomineeTallies = new ArrayList<>();
    for (Long nId : nomineeIds) {
      long tally = voteForms.stream()
          .filter(voteForm -> voteForm.getNomineeId().equals(nId))
          .count();
      nomineeTallies.add(new NomineeTally(nId, tally));
    }

    List<FullNomineeTally> fullNomineeTallies = new ArrayList<>();

    for (NomineeTally nomineeTally : nomineeTallies) {
      Nominee nominee = nomineeRepository.findById(nomineeTally.getNomineeId()).get();
      System.out.println(nominee);
      fullNomineeTallies.add(FullNomineeTally.builder()
                                 .tally(nomineeTally.getTally())
                                 .title(nominee.getTitle())
                                 .company(nominee.getCompany())
                                 .category(nominee.getCategory())
                                 .build());
    }
    return fullNomineeTallies;
  }

  private List<String> getCategories() {
    return new ArrayList<>(Arrays.asList("Best Documentary", "Best Female Field Reporter",
                                         "Best Female Morning Show Host", "Best Female News Anchor",
                                         "Best Local Radio Station", "Best Local Television Station",
                                         "Best Magazine Show", "Best Male Field Reporter",
                                         "Best Male Morning Show Host", "Best Male News Anchor",
                                         "Best Morning Show", "Best National Television Station",
                                         "Best News Program", "Journalist of the Year"));
  }

  public void vote(List<Nominee> nominees, Voter voter) {
    Long voterId = voter.getId();
    voter.denyVoting();
    voterRepository.save(voter);
    List<VoteForm> voteForms = new ArrayList<>();
    List<Nominee> completeNominees = new ArrayList<>();
    List<Nominee> all = nomineeRepository.findAll();
    for (Nominee nominee : nominees) {
      completeNominees.add(
          all.stream().filter(n ->
                                  n.getTitle().equals(nominee.getTitle()) &&
                                      n.getCategory().equals(nominee.getCategory()))
              .findAny().get()
                          );
    }
    for (Nominee nominee : completeNominees) {
      voteForms.add(new VoteForm(voterId, nominee.getId()));
    }
    voteFormRepository.saveAll(voteForms);
  }


  public void vote(List<Long> votes) {
    for (Long id : votes) {
      Optional<Nominee> byId = nomineeRepository.findById(id);
      if (byId.isPresent()) {
        Nominee nominee = byId.get();
        nominee.setTally(nominee.getTally() + 1);
        nomineeRepository.save(nominee);
      }
    }

    nomineeRepository.findAll().forEach(System.out::println);
  }
}
