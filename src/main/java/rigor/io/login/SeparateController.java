package rigor.io.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/separate")
public class SeparateController {

    private TokenService tokenService;
    private UserService userService;

    public SeparateController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/private")
    public ResponseEntity<?> privateEndpoint(@RequestParam(required = false, value = "token") String token) {
        return tokenService.isValid(token)
                ? new ResponseEntity<>("It worked eyyy", HttpStatus.OK)
                : new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

}
