package rigor.io.login;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    private Map<String, User> tokens = new HashMap<>();

    public String create(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return token;
    }

    public void delete(String token) {
        tokens.remove(token);
    }

    public boolean isValid(String token) {
        return tokens.get(token) != null;
    }

}
