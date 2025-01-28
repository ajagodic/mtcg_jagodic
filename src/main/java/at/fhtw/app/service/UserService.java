package at.fhtw.app.service;

import at.fhtw.app.model.User;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.UserRepository;
import at.fhtw.app.persistence.repository.UserRepositoryImpl;

import java.util.List;

public class UserService extends AbstractService {

    private UserRepository userRepository = new UserRepositoryImpl(new UnitOfWork());

    public UserService() {
        userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public  boolean  registerUser(User user) {
        if(userRepository.checkUserExists(user.getUsername())) {
            return false;
        }
        userRepository.saveUser(user);
        return true;
    }
    public String loginUser(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Error: Username and password cannot be empty or null.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generiere Token
        return username + "-mtcgToken";
    }

    public boolean editUser(User user) {
        if (user == null) {
            return false;
        } else {
            userRepository.editUserData(user);
        }
        return true;
    }
    public String displayStats(String username){
        if(userRepository.checkUserExists(username)){
            return userRepository.showStats(username);
        }
        return "false";
    }
    public User findUserbyUsername(String username){
        return userRepository.findByUsername(username);
    }
    public String getUSerData(String username){
        if(userRepository.checkUserExists(username)){
            return userRepository.getUserData(username);
        }
        return "false";
    }

    public boolean updateUserData(User user){
        if(userRepository.checkUserExists(user.getUsername())){
            return userRepository.editUserData(user);
        }
        return false;
    }

    public String showScoreboard() {
        List<String> scoreboard = null;
        try {
            scoreboard = userRepository.displayScoreboard();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        StringBuilder result = new StringBuilder();
        for (String entry : scoreboard) {
            result.append(entry).append(System.lineSeparator());
        }
        return result.toString();
    }
    /*public void updateEloWin(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateEloWin(user.getUsername());
        }
    }
    public void upddateEloLoss(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateEloLoss(user.getUsername());
        }
    }*/

    public static boolean checkAuth(String username, String token) {
        if (token == null || !token.startsWith(username)) {
            return false;
        }
        return true;
    }

    /*public boolean isAdmin(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        return token.equals("Bearer %s-mtcgToken".formatted("admin"));
    }*/


}
