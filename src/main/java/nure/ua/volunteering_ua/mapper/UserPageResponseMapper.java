package nure.ua.volunteering_ua.mapper;

import nure.ua.volunteering_ua.dto.user.UserPageResponse;
import nure.ua.volunteering_ua.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserPageResponseMapper implements Function<Page<User>, UserPageResponse> {

    private final UserMapper userMapper;

    @Autowired
    public UserPageResponseMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserPageResponse apply(Page<User> users) {
        return new UserPageResponse(
                users.getContent()
                        .stream()
                        .map(userMapper)
                        .collect(Collectors.toList()),
                users.getNumber(),
                users.getContent().size(),
                users.getTotalElements(),
                users.getTotalPages()
        );
    }

    public UserPageResponse map(Page<Optional<User>> users, List<User> usersList) {
        return new UserPageResponse(
                usersList
                        .stream()
                        .map(userMapper)
                        .collect(Collectors.toList()),
                users.getNumber(),
                users.getContent().size(),
                users.getTotalElements(),
                users.getTotalPages()
                );
    }
}
