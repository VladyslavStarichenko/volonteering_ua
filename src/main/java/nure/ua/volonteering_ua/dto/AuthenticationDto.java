package nure.ua.volonteering_ua.dto;


import lombok.Data;
import nure.ua.volonteering_ua.model.user.User;

import java.util.function.Function;

@Data
public class AuthenticationDto {

    private String username;
    private String password;
    private String email;


    public User toUser() {
        User user = new User();
        user.setUserName(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        return user;
    }
}
