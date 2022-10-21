package com.example.travelleronline.users;

import com.example.travelleronline.users.dtos.RegisterDTO;
import com.example.travelleronline.util.MasterController;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.users.dtos.LoginDTO;
import com.example.travelleronline.users.dtos.WithoutPassDTO;
import com.example.travelleronline.users.dtos.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
public class UserController extends MasterController {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    @Autowired
    private UserService userService;

    //TODO endpoint for admins - deleteAllUsers ?

    @PostMapping("/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public WithoutPassDTO register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    // TODO verification of email endpoint

    @PostMapping("/login")
    public ProfileDTO login(@RequestBody LoginDTO dto, HttpSession s) {
        if (s.getAttribute(LOGGED) != null && s.getAttribute(LOGGED).equals(true)) {
            s.invalidate();
            throw new BadRequestException("The user was already logged in. Session terminated.");
        }
        ProfileDTO result = userService.login(dto);
        s.setAttribute(LOGGED, true);
        s.setAttribute(USER_ID, result.getUserId());
        return result;
    }

    @PostMapping("/logout")
    public void logout(HttpSession s) {
        if (s.getAttribute(LOGGED) == null || !s.getAttribute(LOGGED).equals(true)) {
            throw new BadRequestException("The user is already logged out.");
        }
        s.invalidate();
    }

}