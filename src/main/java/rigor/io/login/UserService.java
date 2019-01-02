package rigor.io.login;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private List<User> users;

    public UserService() {
        users = new ArrayList<>();
        users.add(new User(1L, "rigorio1", "1231"));
        users.add(new User(2L, "rigorio2", "1232"));
        users.add(new User(3L, "rigorio3", "1233"));
        users.add(new User(4L, "rigorio4", "1234"));
    }

    public boolean isAuthorized(User user) {
        return users.stream().parallel()
                .anyMatch(user1 -> user1.getUsername().equals(user.getUsername()) && user1.getPassword().equals(user.getPassword()));
    }

}
