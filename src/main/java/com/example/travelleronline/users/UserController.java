package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.users.dtos.*;
import com.example.travelleronline.util.MasterController;
import com.example.travelleronline.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController extends MasterController {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";
    @Autowired
    private UserService userService;

    //TODO ? endpoints for admins - deleteAllUsers, getAllUsers

    // Front-end(originally): Avoid spaces at the beginning and at the end.
    // Front-end(afterwards): A verification email has been sent to: ...
    // ...You have ten days to verify your email.
    @PostMapping("/app/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public WithoutPassDTO register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PutMapping(value = "/app/verify-email", params = {"token"})
    public void verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
    }

    @PostMapping("/app/login")
    public ProfileDTO login(@RequestBody LoginDTO dto, HttpServletRequest req) {
        HttpSession session = req.getSession();;
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new BadRequestException("The user was already logged in. Session terminated.");
        }
        ProfileDTO result = userService.login(dto);
        logUser(req, result.getId());
        return result;
    }

    @PostMapping("/users/logout")
    public void logout(HttpSession session) {
        if (session.getAttribute(LOGGED) == null || !(boolean) session.getAttribute(LOGGED)) {
            throw new BadRequestException("The user is already logged out.");
        } // TODO ? Is this needed?
        session.invalidate();
    }

    @GetMapping("/users/{uid}")
    public ProfileDTO getById(@PathVariable int uid) {
        return userService.getById(uid);
    }

    @GetMapping(value = "/users/search-by-name", params = {"name"})
    public List<ProfileDTO> getAllByName(@RequestParam String name) {
        return userService.getAllByName(name);
    }

    @PutMapping("/users/{uid}/edit-info")
    public void editUserInfo(@RequestBody EditUserInfoDTO dto, @PathVariable int uid,
                             HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        validateLoggedIn(session, ip);
        validateIsSameUser(session, uid);
        userService.editUserInfo(dto, uid);
    }

    @PutMapping("/users/{uid}/edit-password")
    public void editUserPass(@RequestBody EditUserPassDTO dto, @PathVariable int uid,
                             HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        validateLoggedIn(session, ip);
        validateIsSameUser(session, uid);
        userService.editUserPass(dto, uid);
    }

    @PutMapping("/users/{uid}/edit-user-photo") // using form-data
    public String editUserPhoto(@RequestParam(value = "image") MultipartFile image,
                              @PathVariable int uid, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        validateLoggedIn(session, ip);
        validateIsSameUser(session, uid);
        return userService.editUserPhoto(uid, image);

    }

    @DeleteMapping("/users/{uid}")
    public void deleteById(@PathVariable int uid, HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        validateLoggedIn(session, ip);
        userService.deleteById(uid);
    }

    private void logUser(HttpServletRequest req, int uid) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, uid);
        session.setAttribute(REMOTE_ADDRESS, ip);
    }

    private void validateLoggedIn(HttpSession session, String ip) {
        if (session.getAttribute(LOGGED) == null ||
                !(boolean) session.getAttribute(LOGGED) ||
                session.getAttribute(REMOTE_ADDRESS) == null ||
                !session.getAttribute(REMOTE_ADDRESS).equals(ip)) {
            throw new UnauthorizedException("You should log in first.");
        }
    }

    private void validateIsSameUser(HttpSession session, int uid) {
        if (uid != (int) session.getAttribute(USER_ID)) {
            throw new UnauthorizedException("You are not the same user.");
        }
    }

}