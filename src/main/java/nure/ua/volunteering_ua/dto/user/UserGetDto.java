package nure.ua.volunteering_ua.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.volunteering_ua.model.user.Customer;
import nure.ua.volunteering_ua.model.user.SocialCategory;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetDto {
    private UUID id;
    private String userName;
    private String email;
    private String role;
    private SocialCategory socialCategory;;
}
