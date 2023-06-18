package nure.ua.volunteering_ua.mapper.user;

import nure.ua.volunteering_ua.dto.user.UserGetDto;
import nure.ua.volunteering_ua.model.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserMapper implements Function<User, UserGetDto> {

    @Override
    public UserGetDto apply(User user) {
        return new UserGetDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getSocialCategory(),
                user.getStatus()
        );
    }
}
