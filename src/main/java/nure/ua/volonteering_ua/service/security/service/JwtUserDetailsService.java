package nure.ua.volonteering_ua.service.security.service;


import lombok.extern.slf4j.Slf4j;
import nure.ua.volonteering_ua.model.user.User;
import nure.ua.volonteering_ua.repository.user.UserRepository;
import nure.ua.volonteering_ua.service.security.jwt.JwtUser;
import nure.ua.volonteering_ua.service.security.jwt.JwtUserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    @Autowired
    public JwtUserDetailsService( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUserName(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user.get());
        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
        return jwtUser;
    }}
