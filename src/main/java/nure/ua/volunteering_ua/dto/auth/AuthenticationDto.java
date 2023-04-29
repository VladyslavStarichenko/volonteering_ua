package nure.ua.volunteering_ua.dto.auth;


import lombok.Data;
import nure.ua.volunteering_ua.model.user.User;

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
