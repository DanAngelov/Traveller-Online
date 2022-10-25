package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.dtos.*;
import com.example.travelleronline.util.MasterService;
import com.example.travelleronline.util.TokenCoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

@Service
public class UserService extends MasterService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JavaMailSender emailSender;


    UserWithoutPassDTO register(RegisterDTO dto) {
        String password = dto.getPassword();
        if (!password.equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch.");
        }
        password = password.trim();

        validateName(dto.getFirstName());
        validateName(dto.getLastName());
        String email = dto.getEmail().trim();
        validateEmail(email);
        String phone = dto.getPhone();
        validatePhone(phone);
        validatePassword(password);
        validateDateOfBirth(dto.getDateOfBirth());
        validateGender(dto.getGender());

        if (userRepository.findAllByEmail(email).size() > 0) {
            throw new BadRequestException("An user with this e-mail " +
                    "has already been registered.");
        }
        if (userRepository.findAllByPhone(phone).size() > 0) {
            throw new BadRequestException("An user with this phone number " +
                    "has already been registered.");
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(password));
//        user.setVerified(false); TODO bring back
        user.setVerified(true); // TODO erase
        user.setCreatedAt(LocalDateTime.now());
        // TODO setDefaultProfilePic
        userRepository.save(user);
//        sendVerificationEmail(email, user.getUserId()); TODO bring back
        return modelMapper.map(user, UserWithoutPassDTO.class);
    }

    void verifyEmail(String token) {
        int uid = TokenCoder.decode(token);
        if (uid == 0) {
            throw new BadRequestException("URL is wrong. Token not correct.");
        }
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (user.isVerified()) {
            throw new BadRequestException("User is already verified.");
        }
        user.setVerified(true);
        userRepository.save(user);
    }

    UserProfileDTO logIn(LoginDTO dto) {
        String email = dto.getEmail().trim();
        String password = dto.getPassword().trim();
        List<User> users = userRepository.findAllByEmail(email);
        if (users.size() > 1) {
            throw new UnauthorizedException("Problem in the DB - " +
                    "more than one user with the same email."); // This should never happen.
        }
        if (users.size() == 0) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        User user = users.get(0);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        if (!user.isVerified()) {
            throw new UnauthorizedException("You have to verify your email first.");
        }
        return modelMapper.map(user, UserProfileDTO.class);
    }

    UserProfileDTO getById(int uid) {
        User user = getVerifiedUserById(uid);
        return modelMapper.map(user, UserProfileDTO.class);
    }

    List<UserProfileDTO> getAllByName(String name) {
        name = name.toLowerCase().trim();
        if (!name.contains("_")) {
            List<UserProfileDTO> userProfiles =
                    userRepository.findAllByFirstNameLikeOrLastNameLike(name, name).stream()
                        .filter(user -> user.isVerified())
                        .map(user -> modelMapper.map(user, UserProfileDTO.class))
                        .collect(Collectors.toList());
            if (userProfiles.size() == 0) {
                throw new NotFoundException("No such users.");
            }
            return userProfiles;
        }
        else {
            String[] names = name.split("_");
            if (names.length > 2) {
                throw new BadRequestException("Too many spaces in the text.");
            }
            List<User> users = userRepository.findAllByFirstNameLike(names[0]).stream()
                    .filter(user -> user.isVerified())
                    .filter(user -> user.getLastName().equalsIgnoreCase(names[1]))
                    .collect(Collectors.toList());
            users.addAll(
                    userRepository.findAllByLastNameLike(names[1]).stream()
                            .filter(user -> user.isVerified())
                            .filter(user -> user.getLastName().equalsIgnoreCase(names[0]))
                            .collect(Collectors.toList())
            );
            List<UserProfileDTO> userProfiles = users.stream()
                    .map(user -> modelMapper.map(user, UserProfileDTO.class))
                    .collect(Collectors.toList());
            if (userProfiles.size() == 0) {
                throw new NotFoundException("No such users.");
            }
            return userProfiles;
        }
    }

    int subscribe(int sid, int uid) {
        if (sid == uid) {
            throw new BadRequestException("User cannot subscribe to himself.");
        }
        User subscriber = getVerifiedUserById(sid);
        User user = getVerifiedUserById(uid);
        if(user.getSubscribers().contains(subscriber)) {
            user.getSubscribers().remove(subscriber);
        }
        else {
            user.getSubscribers().add(subscriber);
        }
        userRepository.save(user);
        return user.getSubscribers().size();
    }

    List<UserProfileDTO> showSubscribers(int uid) {
        User user = getVerifiedUserById(uid);
        return user.getSubscribers().stream()
                .map(u -> modelMapper.map(u, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

    List<UserProfileDTO> showSubscriptions(int uid) {
        User user = getVerifiedUserById(uid);
        return user.getSubscriptions().stream()
                .map(u -> modelMapper.map(u, UserProfileDTO.class))
                .collect(Collectors.toList());
    }

    void editUserInfo(EditInfoDTO dto, int uid) {
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        LocalDate dateOfBirth = dto.getDateOfBirth();
        char gender = dto.getGender();
        validateName(firstName);
        validateName(lastName);
        validateDateOfBirth(dateOfBirth);
        validateGender(gender);

        User user = getVerifiedUserById(uid);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        userRepository.save(user);
    }

    void editUserPass(EditPassDTO dto, int uid) {
        String newPassword = dto.getNewPassword();
        if (!newPassword.equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch.");
        }
        User user = getVerifiedUserById(uid);
        if (!bCryptPasswordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    void deleteById(int uid) {
        userRepository.deleteById(uid);
    }

    private void sendVerificationEmail(String email, int uid) {
        String token = TokenCoder.encode(uid);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@traveller-online.bg");
        message.setTo(email);
        message.setSubject("Your Traveller-Online Account - Verify Your Email Address");
        message.setText("Please, follow the link bellow in order to verify your email address:\n" +
                "http://localhost:7000/app/verify-email/" + token);
        //http://traveller-online.bg/app/verify-email/...
        emailSender.send(message);
    }

    // user's info and password validations

    private void validateName(String name) {
        if (name.isBlank() || name.length() > 100) {
            throw new BadRequestException("Name is blank or too long.");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new BadRequestException("Invalid email.");
        }
    }

    private void validatePhone(String phone) {
        if (!phone.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
            throw new BadRequestException("Invalid phone number.");
        }
    }

    // Requirements:
    // at least one digit [0-9]
    // at least one lowercase Latin character [a-z]
    // at least one uppercase Latin character [A-Z]
    // at least one special character
    // length: 8-20 symbols
    private void validatePassword(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])" +
                "(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
            throw new BadRequestException("The password is not strong enough.");
        }
    }

    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new BadRequestException("The date of birth is blank.");
        }
        long years = YEARS.between(dateOfBirth, LocalDate.now());
        if (years < 18) {
            throw new BadRequestException("Too young.");
        }
        if (years > 150) {
            throw new BadRequestException("Too old to be alive.");
        }
    }

    private void validateGender(char gender) {
        if (gender != 'm' && gender != 'f' && gender != 'o') {
            throw new BadRequestException("The gender is not valid.");
        }
    }

    // Cron Job

    @Scheduled(fixedRate = 10*24*60*60*1000) // for every 10 days
    public void deleteUsersNotVerified() {
        List<User> usersNotVerified = userRepository.findAllByIsVerified(false).stream()
            .filter(user ->
                Period.between(user.getCreatedAt().toLocalDate(), LocalDate.now()).getDays() >= 10)
            .collect(Collectors.toList());
        userRepository.deleteAllInBatch(usersNotVerified);
    }

}