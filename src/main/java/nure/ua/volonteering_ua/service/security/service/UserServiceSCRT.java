package nure.ua.volonteering_ua.service.security.service;


import lombok.extern.slf4j.Slf4j;
import nure.ua.volonteering_ua.dto.AuthorizationDto;
import nure.ua.volonteering_ua.exeption.CustomException;
import nure.ua.volonteering_ua.model.System_Status;
import nure.ua.volonteering_ua.model.user.Role;
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

  public Map<Object, Object> signUpOrganizationAdmin(User user) {
    Pattern passWordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$");
    Matcher matcherPassword = passWordPattern.matcher(user.getPassword());
    if (userRepository.existsByUserName(user.getUserName())) {
      throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    } else if (!matcherPassword.matches()) {
      throw new CustomException("Password should contain at least one capital letter, one lowercase letter, special character," +
              "length should be more or equals 8", HttpStatus.BAD_REQUEST);
    } else {
      Role roleUser = roleRepository.findByName("ROLE_ORGANIZATION_ADMIN");
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      user.setRole(roleUser);
      user.setEmail(user.getEmail());
      user.setStatus(System_Status.ACTIVE);
      User registeredUser = userRepository.save(user);
      log.info("IN register - user: {} successfully registered", registeredUser);
      String token = jwtTokenProvider.createToken(user.getUserName(), new ArrayList<>(Collections.singletonList(user.getRole())));
      String userNameSignedIn = user.getUserName();
      Map<Object, Object> response = new HashMap<>();
      response.put("username", userNameSignedIn);
      response.put("token", token);
      return response;
    }
  }

  public void blockComplexAdmin(String userName) {
    userRepository.findUserByUserName(userName)
            .ifPresent(user -> user.setStatus(System_Status.NONACTIVE));
  }

  public Map<Object, Object> signIn(AuthorizationDto requestDto) throws AuthenticationException {
    try {
      String username = requestDto.getUsername();
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
      Optional<User> user = userRepository.findUserByUserName(username);

      if (!user.isPresent()) {
        throw new UsernameNotFoundException("User with username: " + username + "wasn't found");
      }
      if (user.get().getStatus() == System_Status.NONACTIVE) {
        throw new CustomException("You've been blocked - conctact the Admin of service", HttpStatus.BAD_REQUEST);
      }
      log.info("IN signIn - user: {} successfully signedIN", userRepository.findUserByUserName(username));
      String token = jwtTokenProvider.createToken(username, new ArrayList<>(Collections.singletonList(user.get().getRole())));

      Map<Object, Object> response = new HashMap<>();
      response.put("username", username);
      response.put("token", token);
      response.put("role", user.get().getRole().getName());
      return response;
    } catch (AuthenticationException exception) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  public User getCurrentLoggedInUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userRepository.findUserByUserName(username).isPresent()) {
      return userRepository.findUserByUserName(username).get();
    }
    throw new UsernameNotFoundException("User with username: " + username + "wasn't found, you should authorize firstly") {
    };
  }


}
