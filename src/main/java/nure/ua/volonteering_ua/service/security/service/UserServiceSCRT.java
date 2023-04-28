package nure.ua.volonteering_ua.service.security.service;


import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volonteering_ua.dto.AuthorizationDto;
import nure.ua.volonteering_ua.exeption.CustomException;
import nure.ua.volonteering_ua.model.System_Status;
import nure.ua.volonteering_ua.model.user.Role;
import nure.ua.volonteering_ua.model.user.SocialCategory;
import nure.ua.volonteering_ua.model.user.User;
import nure.ua.volonteering_ua.repository.role.RoleRepository;
import nure.ua.volonteering_ua.repository.user.UserRepository;
import nure.ua.volonteering_ua.service.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserServiceSCRT {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
//  private final VolunteerRepository volunteerRepository;
//  private final CustomerRepository customerRepository;


    @Autowired
    public UserServiceSCRT(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//  public VolunteerGetDto signUpVolunteer(User user) {
//    Pattern passWordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$");
//    Matcher matcherPassword = passWordPattern.matcher(user.getPassword());
//    if (userRepository.existsByUserName(user.getUserName())) {
//      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
//    } else if (!matcherPassword.matches()) {
//      throw new CustomException("Password should contain at least one capital letter, one lowercase letter, special character," +
//              "length should be more or equals 8", HttpStatus.BAD_REQUEST);
//    } else {
//      Role roleUser = roleRepository.findByName("ROLE_VOLUNTEER");
//      user.setPassword(passwordEncoder.encode(user.getPassword()));
//      user.setRole(roleUser);
//      user.setStatus(Status.ACTIVE);
//      User registeredUser = userRepository.save(user);
//      log.info("IN register - user: {} successfully registered", registeredUser);
//      Volunteer volunteer = new Volunteer(registeredUser);
//      return VolunteerGetDto.toDto(volunteerRepository.save(volunteer));
//    }
//  }
//
//  public CustomerGetDto signUpCustomer(User user) {
//    Pattern passWordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$");
//    Matcher matcherPassword = passWordPattern.matcher(user.getPassword());
//    if (!matcherPassword.matches()) {
//      throw new CustomException("Password should contain at least one capital letter, one lowercase letter, special character," +
//              "length should be more or equals 8", HttpStatus.BAD_REQUEST);
//    } else {
//      Role roleUser = roleRepository.findByName("ROLE_CUSTOMER");
//      user.setPassword(passwordEncoder.encode(user.getPassword()));
//      user.setRole(roleUser);
//      user.setStatus(Status.ACTIVE);
//      User registeredUser = userRepository.save(user);
//      log.info("IN register - user: {} successfully registered", registeredUser);
//      Customer customer = new Customer(user);
//      return CustomerGetDto.toDto(customerRepository.save(customer));
//    }
//  }

    //TODO add donation Details
    public Map<Object, Object> signUpOrganizationAdmin(User user) {
        Pattern passWordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$");
        Matcher matcherPassword = passWordPattern.matcher(user.getPassword());
        return Stream.of(user)
                .filter(u -> !userRepository.existsByUserName(u.getUserName()))
                .filter(u -> matcherPassword.matches())
                .peek(u -> {
                    Role roleUser = roleRepository.findByName("ROLE_ORGANIZATION_ADMIN");
                    u.setPassword(passwordEncoder.encode(u.getPassword()));
                    u.setRole(roleUser);
                    u.setEmail(u.getEmail());
                    u.setStatus(System_Status.ACTIVE);
                    u.setSocialCategory(SocialCategory.NO_CATEGORY);
                    userRepository.save(u);
                })
                .findFirst()
                .map(u -> {
                    log.info("IN register - user: {} successfully registered", u);
                    String token = jwtTokenProvider.createToken(u.getUserName(), new ArrayList<>(Collections.singletonList(u.getRole())));
                    String userNameSignedIn = u.getUserName();
                    Map<Object, Object> response = new HashMap<>();
                    response.put("username", userNameSignedIn);
                    response.put("token", token);
                    return response;
                })
                .orElseThrow(() -> new CustomException("Invalid username or password", HttpStatus.BAD_REQUEST));
    }


    public void blockComplexAdmin(String userName) {
        Optional<User> userDb = userRepository.findUserByUserName(userName);
        userDb.ifPresentOrElse(
                user -> {
                    user.setStatus(System_Status.NONACTIVE);
                    userRepository.save(user);
                },
                () -> {
                    throw new CustomException("There is no user with specified name", HttpStatus.NOT_FOUND);
                }
        );
    }

    public Map<Object, Object> signIn(AuthorizationDto requestDto) throws AuthenticationException {
        String username = requestDto.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));

        User user = userRepository.findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + "wasn't found"));

        if (user.getStatus() == System_Status.NONACTIVE) {
            throw new CustomException("You've been blocked - contact the Admin of service", HttpStatus.BAD_REQUEST);
        }

        log.info("IN signIn - user: {} successfully signedIN", userRepository.findUserByUserName(username));
        String token = jwtTokenProvider.createToken(username, new ArrayList<>(Collections.singletonList(user.getRole())));

        return ImmutableMap.of(
                "username", username,
                "token", token,
                "role", user.getRole().getName()
        );
    }

    public User getCurrentLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " wasn't found, you should authorize firstly"
                ));
    }


}
