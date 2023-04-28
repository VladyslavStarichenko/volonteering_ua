package nure.ua.volonteering_ua.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import nure.ua.volonteering_ua.model.user.User;

import java.util.function.Function;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationDto {

    private String username;
    private String password;
}
