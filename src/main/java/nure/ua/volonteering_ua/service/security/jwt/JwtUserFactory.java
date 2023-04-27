package nure.ua.volonteering_ua.service.security.jwt;



import nure.ua.volonteering_ua.model.System_Status;
import nure.ua.volonteering_ua.model.user.Role;
import nure.ua.volonteering_ua.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user){
        return new JwtUser(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(Collections.singletonList(user.getRole()))),
                user.getStatus().equals(System_Status.ACTIVE),
                user.getUpdatedAt()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles){
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}
