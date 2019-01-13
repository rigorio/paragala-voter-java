package rigor.io.paragala.voter.voting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.paragala.voter.ResponseHub;
import rigor.io.paragala.voter.nominees.Nominee;
import rigor.io.paragala.voter.nominees.NomineeRepository;
import rigor.io.paragala.voter.token.TokenService;
import rigor.io.paragala.voter.voting.machine.VoteBoxService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {

  private VoteBoxService voteBoxService;
  private TokenService tokenService;
  private NomineeRepository nomineeRepository;

  public AdminController(VoteBoxService voteBoxService, TokenService tokenService, NomineeRepository nomineeRepository) {
    this.voteBoxService = voteBoxService;
    this.tokenService = tokenService;
    this.nomineeRepository = nomineeRepository;
  }

  @GetMapping("/votes")
  public ResponseEntity<?> viewVotes(@RequestParam String token) {
    boolean isValid = tokenService.isValid(token);

    if (!isValid)
      return ResponseHub.defaultUnauthorizedResponse();

    return new ResponseEntity<>(voteBoxService.getAllVotes(), HttpStatus.OK);
  }

  @PostMapping("/nominees")
  public ResponseEntity<?> addNominees(@RequestParam String token,
                                       @RequestBody List<Nominee> nominees) {
    return tokenService.isValid(token)
        ? new ResponseEntity<>(nomineeRepository.saveAll(nominees), HttpStatus.OK)
        : ResponseHub.defaultUnauthorizedResponse();
  }

  @GetMapping("/nominees/default")
  public ResponseEntity<?> populateNominees(@RequestParam String token) {
    nomineeRepository.deleteAll();
    return new ResponseEntity<>(nomineeRepository.saveAll(populate()), HttpStatus.OK);
  }

  private List<Nominee> populate() {
    List<Nominee> nominees = new ArrayList<>();
    for (int i = 0; i < titles.length; i++) {
      nominees.add(new Nominee(
          titles[i],
          companies[i],
          categories[i]
      ));
    }
    return nominees;
  }

  private String[] titles = new String[]{
      "Public Eye: Klasrum Kalsada PTV 4 Public Affairs", "Philippine Seas by Atom Araullo",
      "Reel Time: Salat", "\"Di ka Pasisiil by Jeff Canoy and Chiara Zambrano",
      "Chiara Zambrano in TV Patrol", "Maricel Halili in Aksyon Tonite",
      "Karol Di in PTV News", "Sandra Aguinaldo in 24 Oras", "Doris Bigornia in TV Patrol",
      "Ina Andolong in Newsroom", "Susan Enriquez in 24 Oras",
      "Amy Perez in Umagang Kay Ganda", "Love Anover in Unang Hirit",
      "Claire Celdran in New Day", "Dianne Medina in Bagong Pilipinas",
      "Suzie Entrata-Abrera in Unang Hirit", "Winnie Cornejo in Umagang Kay Ganda",
      "Pia Archanghel in Saksi", "Mitzi Borromeo in Newsroom",
      "Dianne Querrer in PTV News", "Vicky Morales in 24 Oras",
      "Mae Ann Los Banos in Aksyon Tonite", "Bernadette Sembrano in TV Patrol",
      "DWBL 91.9", "UFM 105.1", "GV FM 99.1", "DWCL 92.7",
      "RW 95.1", "ABS-CBN Pampanga TV 46", "GNN 44 Infomax 9", "Community TV 3", "CLTV 36", "Kapuso mo, Jessica Soho",
      "IJuander", "My Puhunan", "RATED K", "Doland Castro in TV Patrol",
      "Joseph Morong in 24 Oras", "Raffy Tima in 24 Oras",
      "Renz Ongkiko in Aksyon Tonite", "JM Encinas in PTV News",
      "Jorge Carino in TV Patrol", "David Santos in Newsroom",
      "Tonipet Gaba in Unang Hirit", "Jules Guiang in Bagong Pilipinas",
      "Ivan Mayrina in Unang Hirit", "Anthony Taberna in Umagang Kay Ganda",
      "Andrei Felix in New Day", "Ariel Ureta in Umagang Kay Ganda",
      "Alex Santos in Sentro Balita", "Arnold Clavio in Saksi",
      "Erwin Tulfo in PTV News", "Ted Failon in TV Patrol", "Ed Lingao in Aksyon Tonite",
      "Noli De Castro in TV Patrol", "New Day", "Unang Hirit", "Magandang Buhay", "Bagong Pilipinas",
      "Umagang Kay Ganda", "Alto Broadcasting System - Chronic Broadcasting Network", "TV 5", "Greater Manila Area",
      "People\"s Television", "Saksi",
      "Bandila", "Aksyon Tonite", "24 Oras", "Newsroom",
      "PTV News", "TV Patrol", "Bong Lacson", "Ashley Manabat", "Frederico Pascual"
  };

  private String[] companies = new String[]{
      "PTV 4", "GMA", "GMA", "ABS-CBN", "ABS-CBN", "TV5", "PTV 4", "GMA",
      "ABS-CBN", "CNN Philippines", "GMA", "ABS-CBN", "GMA", "CNN Philippines", "PTV 4", "GMA", "ABS-CBN", "GMA", "CNN Philippines", "PTV 4", "GMA",
      "TV5", "ABS-CBN", "Bright FM", "You\"re on the right side", "Your Good Vibes", "Brigada News FM", "Keni na ka!", "ABS-CBN", "GNN", "CTV 3",
      "CLTV", "GMA", "GMA", "ABS-CBN", "ABS-CBN", "ABS-CBN", "GMA", "GMA", "TV5", "PTV 4", "ABS-CBN", "CNN Philippines", "GMA", "PTV 4", "GMA", "ABS-CBN",
      "CNN Philippines", "ABS-CBN", "PTV 4", "GMA", "PTV 4", "ABS-CBN", "TV5", "ABS-CBN", "CNN Philippines",
      "GMA", "ABS-CBN", "PTV 4", "ABS-CBN", "ABS-CBN", "TV5", "GMA", "PTV 4", "GMA", "ABS-CBN", "TV5", "GMA",
      "CNN Philippines", "PTV 4", "ABS-CBN", "Punto! Central Luzon", "Punto! Central Luzon", "Philippine Star"
  };
  private String[] categories = new String[]{
      "Best Documentary", "Best Documentary", "Best Documentary", "Best Documentary", "Best Female Field Reporter", "Best Female Field Reporter",
      "Best Female Field Reporter", "Best Female Field Reporter", "Best Female Field Reporter", "Best Female Field Reporter", "Best Female Field Reporter",
      "Best Female Morning Show Host", "Best Female Morning Show Host", "Best Female Morning Show Host", "Best Female Morning Show Host", "Best Female Morning Show Host",
      "Best Female Morning Show Host", "Best Female News Anchor", "Best Female News Anchor", "Best Female News Anchor",
      "Best Female News Anchor", "Best Female News Anchor", "Best Female News Anchor", "Best Local Radio Station", "Best Local Radio Station",
      "Best Local Radio Station", "Best Local Radio Station", "Best Local Radio Station", "Best Local Television Station", "Best Local Television Station", "Best Local Television Station",
      "Best Local Television Station", "Best Magazine Show", "Best Magazine Show", "Best Magazine Show", "Best Magazine Show", "Best Male Field Reporter",
      "Best Male Field Reporter", "Best Male Field Reporter", "Best Male Field Reporter", "Best Male Field Reporter", "Best Male Field Reporter", "Best Male Field Reporter",
      "Best Male Morning Show Host", "Best Male Morning Show Host", "Best Male Morning Show Host", "Best Male Morning Show Host", "Best Male Morning Show Host",
      "Best Male Morning Show Host", "Best Male News Anchor", "Best Male News Anchor", "Best Male News Anchor", "Best Male News Anchor", "Best Male News Anchor",
      "Best Male News Anchor", "Best Morning Show", "Best Morning Show", "Best Morning Show",
      "Best Morning Show", "Best Morning Show", "Best National Television Station", "Best National Television Station", "Best National Television Station",
      "Best National Television Station", "Best News Program", "Best News Program", "Best News Program", "Best News Program",
      "Best News Program", "Best News Program", "Best News Program", "Journalist of the Year", "Journalist of the Year", "Journalist of the Year"
  };

}
