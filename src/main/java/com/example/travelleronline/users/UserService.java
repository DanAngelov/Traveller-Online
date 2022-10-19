package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.users.dtos.LoginDTO;
import com.example.travelleronline.users.dtos.ProfileDTO;
import com.example.travelleronline.users.dtos.RegisterDTO;
import com.example.travelleronline.users.dtos.WithoutPassDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class UserService extends MasterService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public WithoutPassDTO register(RegisterDTO dto) {
        String password = dto.getPassword();
        if (!password.equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords mismatch.");
        }
        String firstName = dto.getFirstName();
        validateName(firstName);
        String lastName = dto.getLastName();
        validateName(lastName);
        String email = dto.getEmail();
        validateEmail(email);
        validatePhone(dto.getPhone());
        validatePassword(password);
        validateDateOfBirth(dto.getDateOfBirth());
        validateGender(dto.getGender());
        if (userRepository.findAllByEmail(email).size() > 0) {
            throw new BadRequestException("An user with this e-mail " +
                    "has already been registered.");
        }
        if (userRepository.findAllByPhone(dto.getPhone()).size() > 0) {
            throw new BadRequestException("An user with this phone number " +
                    "has already been registered.");
        }
        User user = modelMapper.map(dto, User.class);
        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();
        user.setFirstName(firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
        user.setLastName(lastName.substring(0, 1).toUpperCase() + lastName.substring(1));
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        // TODO send verification email !!!
        return modelMapper.map(user, WithoutPassDTO.class);
    }

    ProfileDTO login(LoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        List<User> users = userRepository.findAllByEmail(email);
        if (users.size() > 1) {
            throw new UnauthorizedException("Problem in the DB - " +
                    "more than one user with the same email."); // This should never happen.
        }
        else if (users.size() == 1) {
            User user = users.get(0);
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                return modelMapper.map(user, ProfileDTO.class);
            }
            else {
                throw new UnauthorizedException("Invalid email or password.");
            }
        }
        else {
            throw new UnauthorizedException("Invalid email or password.");
        }
    }

    // registration validations

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

}