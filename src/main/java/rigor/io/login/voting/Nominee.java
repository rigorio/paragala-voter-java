package rigor.io.login.voting;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Nominee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String company;
    private String category;

    public Nominee() {
    }

    public Nominee(String title, String company, String category) {
        this.title = title;
        this.company = company;
        this.category = category;
    }
}
