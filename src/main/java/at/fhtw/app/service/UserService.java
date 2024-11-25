package at.fhtw.app.service;

import at.fhtw.app.model.User;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.UserRepository;
import at.fhtw.app.persistence.repository.UserRepositoryImpl;

public class UserService extends AbstractService {

    private UserRepository userRepository = new UserRepositoryImpl(new UnitOfWork());

    public UserService() {
        userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public  boolean  registerUser(User user) {
        if(userRepository.findByUsername(user.getUsername()) != null){
            return false;
        }
        userRepository.saveUser(user);
        return true;
    }
    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = username + "-mtcgToken"; // Generiere Token
            return token;
        }
        return null; // Login fehlgeschlagen
    }

}
