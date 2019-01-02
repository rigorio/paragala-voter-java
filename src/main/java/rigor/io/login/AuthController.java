package rigor.io.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private TokenService tokenService;
    private UserService userService;

    public AuthController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping("/private")
    public ResponseEntity<?> privateEndpoint(@RequestParam(required = false, value = "token") String token) {
        boolean valid = tokenService.isValid(token);
        if (valid) {
            return new ResponseEntity<>("I'm a private endpoint", HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userService.isAuthorized(user)
                ? new ResponseEntity<>(tokenService.create(user), HttpStatus.OK)
                : new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam(required = false, value = "token") String token) {
        tokenService.delete(token);
        return new ResponseEntity<>("Logged out", HttpStatus.OK);
    }

}
