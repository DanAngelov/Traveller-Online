package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.users.dtos.*;
import com.example.travelleronline.util.MasterService;
import com.example.travelleronline.util.TokenCoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends MasterService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JavaMailSender emailSender;


    public WithoutPassDTO register(RegisterDTO dto) {
        String password = dto.getPassword();
        if (!password.equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch.");
        }
        password = password.trim();

        validateName(dto.getFirstName());
        validateName(dto.getLastName());
        String email = dto.getEmail();
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
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        // TODO setDefaultProfilePic
        userRepository.save(user);
        sendConfirmationEmail(email, user.getUserId());
        return modelMapper.map(user, WithoutPassDTO.class);
    }

    public void verifyEmail(String token) {
        int uid = TokenCoder.decode(token);
        if (uid == 0) {
            throw new NotFoundException("URL is wrong. Token not correct.");
        }
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("URL is wrong. Token not correct."));
        if (user.isVerified()) {
            throw new BadRequestException("User is already verified.");
        }
    }

    ProfileDTO login(LoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword(); //TODO ? should I trim it?
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
        return modelMapper.map(user, ProfileDTO.class);
    }

    public ProfileDTO getById(int uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("There is no such user."));
        if (!user.isVerified()) {
            throw new BadRequestException("The user is not verified.");
        }
        return modelMapper.map(user, ProfileDTO.class);
    }

    public List<ProfileDTO> getAllByName(String name) {
        name = name.trim();
        if (!name.contains(" ")) {
            List<ProfileDTO> userProfiles =
                    userRepository.findAllByFirstNameOrLastName(name, name).stream()
                    .map(user -> modelMapper.map(user, ProfileDTO.class))
                    .collect(Collectors.toList());
            if (userProfiles.size() == 0) {
                throw new NotFoundException("No such users.");
            }
            return userProfiles;
        }
        else {
            String[] names = name.split(" ");
            if (names.length > 2) {
                throw new BadRequestException("Too many spaces in the text.");
            }
            List<User> users = userRepository.findAllByFirstName(names[0]).stream()
                    .filter(user -> user.getLastName().equals(names[1]))
                    .collect(Collectors.toList());
            users.addAll(
                    userRepository.findAllByLastName(names[1]).stream()
                            .filter(user -> user.getLastName().equals(names[0]))
                            .collect(Collectors.toList())
            );
            List<ProfileDTO> userProfiles = users.stream()
                    .map(user -> modelMapper.map(user, ProfileDTO.class))
                    .collect(Collectors.toList());
            if (userProfiles.size() == 0) {
                throw new NotFoundException("No such users.");
            }
            return userProfiles;
        }
    }

    public void editUserInfo(EditUserInfoDTO dto, int uid) {
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        LocalDate dateOfBirth = dto.getDateOfBirth();
        char gender = dto.getGender();
        validateName(firstName);
        validateName(lastName);
        validateDateOfBirth(dateOfBirth);
        validateGender(gender);

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        userRepository.save(user);
    }

    public void editUserPass(EditUserPassDTO dto, int uid) {
        String newPassword = dto.getNewPassword();
        if (!newPassword.equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch.");
        }
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (!bCryptPasswordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public String editUserPhoto(int uid, MultipartFile image) {
        //TODO
        return null;
    }

    public void deleteById(int uid) {
        userRepository.deleteById(uid);
    }

    private void sendConfirmationEmail(String email, int uid) {
        String token = TokenCoder.encode(uid);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dan.angelov93@gmail.com"); //noreply@traveller-online.bg
        message.setTo(email);
        message.setSubject("Your Traveller-Online Account - Verify Your Email Address");
        message.setText("Please, follow the link bellow in order to verify your email address:\n" +
                "http://traveller-online.bg/app/verify-email?token=" + token);
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
        if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\\\\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+\" +\n\"(\\\\\\\\.[A-Za-z0-9-]+)*(\\\\\\\\.[A-Za-z]{2,100})$")) {
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
        int years = Period.between(dateOfBirth, LocalDate.now()).getYears();
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

    @Scheduled(cron = "0 0 0 1/11/21 * ?")
    public void deleteUsersNotVerified() {
        List<User> usersNotVerified = userRepository.findAllByIsVerified(false);
        userRepository.deleteAllInBatch(usersNotVerified);
    }

}