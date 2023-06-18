package nure.ua.volunteering_ua.dto.auth;

import lombok.*;
import nure.ua.volunteering_ua.model.user.SocialCategory;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAuthenticationDto extends AuthenticationDto {
    private String address;
    private SocialCategory socialCategory;
}
