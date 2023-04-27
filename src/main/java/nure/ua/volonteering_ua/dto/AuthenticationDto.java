package nure.ua.volonteering_ua.dto;


import lombok.Data;
import nure.ua.volonteering_ua.model.user.User;

@Data
public class AuthenticationDto {

    private String username;
    private String password;
    private String email;

    public User toUser(){
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }
}
