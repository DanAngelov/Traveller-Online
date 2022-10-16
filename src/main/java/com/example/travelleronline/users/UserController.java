package com.example.travelleronline.users;

import com.example.travelleronline.util.MasterController;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.users.dtos.UserDtoEmailPass;
import com.example.travelleronline.users.dtos.UserDtoNoPass;
import com.example.travelleronline.users.dtos.UserDtoProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController extends MasterController {

    private static final String PASSWORD_REQUIREMENTS =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    // Requirements:
    // at least one digit [0-9]
    // at least one lowercase Latin character [a-z]
    // at least one uppercase Latin character [A-Z]
    // at least one special character
    // length: 8-20 symbols
    private static final String UNSAFE_PASSWORD_MESSAGE =
            "The password is not strong enough.";

    @Autowired
    private UserRepository userRepository;

    //TODO deleteAllUsers ?

    @PostMapping("/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDtoNoPass register(@RequestBody User u) {
        if (userRepository.findAllByEmail(u.getEmail()).size() > 0) {
            throw new BadRequestException("An user with this e-mail " +
                    "has already been registered.");
        }
        if (!u.getPassword().matches(PASSWORD_REQUIREMENTS)) {
            throw new BadRequestException(UNSAFE_PASSWORD_MESSAGE);
        }
        if (u.getGender() != 'm' && u.getGender() != 'f' && u.getGender() != 'o') {
            throw new BadRequestException("User's gender is not valid.");
        }
        u.setVerified(false);
        LocalDateTime createdAt = LocalDateTime.now();
        u.setCreatedAt(createdAt);
        userRepository.save(u);
        // TODO send verification email !!!
        return modelMapper.map(u, UserDtoNoPass.class);
    }

    @PutMapping("/login-by-email") //TODO ? PUT; also login-by-phone?
    public UserDtoProfile login(@RequestBody UserDtoEmailPass emailPass, HttpSession s) {
        if (s.getAttribute("logged").equals(true)) {
            throw new BadRequestException("The user is already logged in.");
        }
        boolean successfulLogin = false;
        User user = userRepository.findAllByEmail(emailPass.getEmail()).get(0);
        if (user != null) {
            if (user.getPassword().equals(emailPass.getPassword())) {
                successfulLogin = true;
            }
        }
        if (successfulLogin) {
            s.setAttribute("logged", true);
            return modelMapper.map(user, UserDtoProfile.class);
        }
        else {
            throw new UnauthorizedException("Invalid username or password.");
        }
    }

    @PutMapping("/{id}/logout")
    public UserDtoProfile logout(@PathVariable long id, HttpSession s) {
        if (s.getAttribute("logged").equals(false)) {
            throw new BadRequestException("The user is already logged out.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("There is no such user."));
        s.setAttribute("logged", true); //TODO terminate session
        return modelMapper.map(user, UserDtoProfile.class);
    }


    @GetMapping("/{id}")
    public UserDtoProfile getById(@PathVariable long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no such user."));
        if (!user.isVerified()) {
            throw new NotFoundException("The user is not verified.");
        }
        return modelMapper.map(user, UserDtoProfile.class);
    }

    @DeleteMapping("/{id}")
    public UserDtoNoPass deleteById(@PathVariable long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("There is no such user."));
        return modelMapper.map(user, UserDtoNoPass.class);
    }

}