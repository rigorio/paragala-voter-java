package rigor.io.paragala.voter.register;

import org.springframework.stereotype.Service;

@Service
public class Registrant {

  private RegistrationFormRepository registrationFormRepository;

  public Registrant(RegistrationFormRepository registrationFormRepository) {
    this.registrationFormRepository = registrationFormRepository;
  }

  public void register(RegistrationForm registrationForm) {
    registrationFormRepository.save(registrationForm);
  }
}
