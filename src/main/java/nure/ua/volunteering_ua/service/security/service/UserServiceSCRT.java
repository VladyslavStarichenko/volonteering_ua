package nure.ua.volunteering_ua.service.security.service;


import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import nure.ua.volunteering_ua.dto.auth.AuthorizationDto;
import nure.ua.volunteering_ua.dto.customer.CustomerGetDto;
import nure.ua.volunteering_ua.dto.user.UserGetDto;
import nure.ua.volunteering_ua.dto.user.UserPageResponse;
import nure.ua.volunteering_ua.dto.volunteer.VolunteerGetDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.mapper.*;
import nure.ua.volunteering_ua.model.System_Status;
import nure.ua.volunteering_ua.model.user.*;
import nure.ua.volunteering_ua.repository.customer.CustomerRepository;
import nure.ua.volunteering_ua.repository.role.RoleRepository;
import nure.ua.volunteering_ua.repository.user.UserRepository;
import nure.ua.volunteering_ua.repository.volunteer.VolunteerRepository;
import nure.ua.volunteering_ua.service.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final VolunteerRepository volunteerRepository;
    private final CustomerRepository customerRepository;
    private final VolunteeringMapper volunteeringMapper;
    private final CustomerMapper customerMapper;
    private final UserPageResponseMapper userPageResponseMapper;
    private final UserMapper userMapper;


    @Autowired
    public UserServiceSCRT(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, VolunteerRepository volunteerRepository, CustomerRepository customerRepository, UserPageResponseMapper userPageResponseMapper, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.volunteerRepository = volunteerRepository;
        this.userPageResponseMapper = userPageResponseMapper;
        this.userMapper = userMapper;
        this.customerMapper = new CustomerMapper(new RequestMapper());
        this.volunteeringMapper = new VolunteeringMapper();
        this.customerRepository = customerRepository;
    }

    public VolunteerGetDto signUpVolunteer(User user) {
        user.setSocialCategory(SocialCategory.NO_CATEGORY);
        validateUser(user);
        User registeredUser = registerUser(user, "ROLE_VOLUNTEER");
        Volunteer volunteer = volunteerRepository.save(new Volunteer(registeredUser));
        return volunteeringMapper.apply(volunteer);
    }

    public CustomerGetDto signUpCustomer(User user, String address, SocialCategory socialCategory) {
        user.setSocialCategory(socialCategory);
        validateUser(user);
        User registeredUser = registerUser(user, "ROLE_CUSTOMER");
        Customer customer = customerRepository.save(new Customer(registeredUser,address));
        return customerMapper.apply(customer);
    }

    public Map<Object, Object> signUpOrganizationAdmin(User user) {
        validateUser(user);
        user.setSocialCategory(SocialCategory.NO_CATEGORY);
        User registeredUser = registerUser(user, "ROLE_ORGANIZATION_ADMIN");
        registeredUser.setEmail(user.getEmail());
        userRepository.save(registeredUser);
        String token = jwtTokenProvider.createToken(registeredUser.getUserName(), new ArrayList<>(Collections.singletonList(registeredUser.getRole())));
        String userNameSignedIn = registeredUser.getUserName();
        Map<Object, Object> response = new HashMap<>();
        response.put("username", userNameSignedIn);
        response.put("token", token);
        response.put("role", registeredUser.getRole().getName());
        return response;
    }

    private void validateUser(User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!isValidPassword(user.getPassword())) {
            throw new CustomException("Password should contain at least one capital letter, one lowercase letter, special character," +
                    "length should be more or equals 8", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isValidPassword(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return passwordMatcher.matches();
    }

    private User registerUser(User user, String roleName) {
        User registeredUser = new User();
        registeredUser.setEmail(user.getEmail());
        registeredUser.setSocialCategory(user.getSocialCategory());
        registeredUser.setUserName(user.getUserName());
        registeredUser.setPassword(passwordEncoder.encode(user.getPassword()));
        registeredUser.setRole(roleRepository.findByName(roleName));
        registeredUser.setStatus(System_Status.ACTIVE);
        registeredUser.setSocialCategory(user.getSocialCategory());
        return userRepository.save(registeredUser);
    }


    public void blockUser(String userName) {
        Optional<User> userDb = userRepository.findUserByUserName(userName);
        userDb.ifPresentOrElse(
                user -> {
                    userRepository.updateUserStatus(System_Status.NONACTIVE.name(), userName);
                },
                () -> {
                    throw new CustomException("There is no user with specified name", HttpStatus.NOT_FOUND);
                }
        );
    }

    public void unBlockUser(String userName) {
        Optional<User> userDb = userRepository.findUserByUserName(userName);
        userDb.ifPresentOrElse(
                user -> {
                    userRepository.updateUserStatus(System_Status.ACTIVE.name(), userName);
                },
                () -> {
                    throw new CustomException("There is no user with specified name", HttpStatus.NOT_FOUND);
                }
        );
    }

    public UserGetDto getUserById(UUID id) {
        return userMapper.apply(userRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(
                                "There is no product with specified id",
                                HttpStatus.NOT_FOUND
                        )
                ));
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

        if(user.getRole().getName().equals("ROLE_VOLUNTEER")){
            Volunteer volunteer = volunteerRepository.getByUser_UserName(user.getUserName()).orElseThrow(
                    () -> new CustomException("There is an error with login in", HttpStatus.BAD_REQUEST)
            );
            return ImmutableMap.of(
                    "volunteer_id", volunteer.getId(),
                    "username", username,
                    "token", token,
                    "role", user.getRole().getName(),
                    "email", user.getEmail()
            );
        }
        else if(user.getRole().getName().equals("ROLE_CUSTOMER")){
            Customer customer = customerRepository.findByUser(user).orElseThrow(
                    () -> new CustomException("There is an error with login in", HttpStatus.BAD_REQUEST)
            );
            return ImmutableMap.of(
                    "customer", customer.getId(),
                    "username", username,
                    "token", token,
                    "role", user.getRole().getName(),
                    "email", user.getEmail()
            );
        }

        else{
            return ImmutableMap.of(
                    "volunteer_id", user.getId(),
                    "username", username,
                    "token", token,
                    "role", user.getRole().getName(),
                    "email", user.getEmail());
        }




    }

    public User getCurrentLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " wasn't found, you should authorize firstly"
                ));
    }

    public UserPageResponse getAllUsers (int pageNumber, int sizeOfPage, String sortBy){
        Pageable pageable = PageRequest.of(pageNumber, sizeOfPage, Sort.by(Sort.Order.asc(sortBy)));
        return userPageResponseMapper.apply(userRepository.findAll(pageable));
    }




}
