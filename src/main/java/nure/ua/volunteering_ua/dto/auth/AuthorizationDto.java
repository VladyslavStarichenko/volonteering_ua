package nure.ua.volunteering_ua.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationDto {

    private String username;
    private String password;
}
