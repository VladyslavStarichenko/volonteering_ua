package nure.ua.volunteering_ua.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.user.SocialCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAuthenticationDto extends AuthenticationDto {
    private String address;
    private SocialCategory socialCategory;
}
