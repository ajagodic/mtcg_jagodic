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
    public boolean editUser(User user) {
        if (user == null) {
            return false;
        } else {
            userRepository.editUserData(user);
        }
        return true;
    }
    public String displayStats(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            return userRepository.showStats(user.getUsername());
        }
        return "false";
    }
    public User findUserbyUsername(String username){
        return userRepository.findByUsername(username);
    }

    public void updateStats(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.editUserData(user);
        }
    }
    public void updateWin(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateWin(user.getUsername());
        }
    }
    public void updateLoss(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateLoss(user.getUsername());
        }
    }
    public void updateEloWin(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateEloWin(user.getUsername());
        }
    }
    public void upddateEloLoss(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            userRepository.updateEloLoss(user.getUsername());
        }
    }


}
