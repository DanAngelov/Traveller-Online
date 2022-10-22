package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.dtos.PostDTO;
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

    // Front-end(originally): Avoid spaces at the beginning and at the end.
    // Front-end(afterwards): A verification email has been sent to: ...
    // ...You have ten days to verify your email.
    @PostMapping("/app/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public WithoutPassDTO register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PutMapping(value = "/app/verify-email/{token}")
    public void verifyEmail(@PathVariable String token) {
        userService.verifyEmail(token);
    }

    @PostMapping("/app/login")
    public ProfileDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req) {
        HttpSession session = req.getSession();;
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new BadRequestException("The user was already logged in. Session terminated.");
        }
        ProfileDTO result = userService.logIn(dto);
        logUser(req, result.getId());
        return result;
    }

    @PutMapping("/app/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

//    @GetMapping("/users/news-feed")
//    public List<PostDTO> showNewsFeed(HttpServletRequest req) {   //TODO ??? postDTO
//
//    }

    @GetMapping(value = "/users/search", params = {"name"})
    public List<ProfileDTO> getAllByName(@RequestParam String name) {
        return userService.getAllByName(name);
    }

    @GetMapping("/users/{uid}")
    public ProfileDTO getById(@PathVariable int uid) { //TODO in profileDTO number of subscribers
        return userService.getById(uid);
    }

    // unsubscribes after following visit
    @PutMapping("/users/{uid}/subscribe")
    public int subscribe(@PathVariable int uid, HttpServletRequest req) {
        validateLoggedIn(req);
        HttpSession session = req.getSession();
        if (session.getAttribute(USER_ID) == null) {
            throw new BadRequestException("Subscriber's id is not in session.");
        }
        int sid = (int) session.getAttribute(USER_ID); // subscriber's id
        return userService.subscribe(sid, uid);
    }

//    @GetMapping("/users/show-subscribers")
//    public List<ProfileDTO> showSubscribers(HttpServletRequest req) {
//        //TODO ? need?
//    }

    @GetMapping("/users/show-subscriptions")
//    public List<ProfileDTO> showSubscriptions(HttpServletRequest req) {
//        //TODO
//    }


    @PutMapping("/users/{uid}/edit-info")
    public void editUserInfo(@RequestBody EditUserInfoDTO dto, @PathVariable int uid,
                             HttpServletRequest req) {
        validateLoggedIn(req);
        validateUser(req.getSession(), uid);
        userService.editUserInfo(dto, uid);
    }

    @PutMapping("/users/{uid}/edit-password")
    public void editUserPass(@RequestBody EditUserPassDTO dto, @PathVariable int uid,
                             HttpServletRequest req) {
        validateLoggedIn(req);
        validateUser(req.getSession(), uid);
        userService.editUserPass(dto, uid);
    }

    @PutMapping("/users/{uid}/edit-user-photo") // using form-data
    public String editUserPhoto(@RequestParam(value = "file") MultipartFile image,
                              @PathVariable int uid, HttpServletRequest req) {
        validateLoggedIn(req);
        validateUser(req.getSession(), uid);
        return userService.editUserPhoto(uid, image);
    }

    @DeleteMapping("/users/{uid}")
    public void deleteById(@PathVariable int uid, HttpServletRequest req) {
        validateLoggedIn(req);
        validateUser(req.getSession(), uid);
        userService.deleteById(uid);
    }

    private void logUser(HttpServletRequest req, int uid) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, uid);
        session.setAttribute(REMOTE_ADDRESS, ip);
    }

    public void validateLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        if (session.getAttribute(LOGGED) == null ||
                !(boolean) session.getAttribute(LOGGED) ||
                session.getAttribute(REMOTE_ADDRESS) == null ||
                !session.getAttribute(REMOTE_ADDRESS).equals(ip)) {
            throw new UnauthorizedException("You should log in first.");
        }
    }

    public void validateUser(HttpSession session, int uid) {
        if (uid != (int) session.getAttribute(USER_ID)) {
            session.invalidate();
            throw new UnauthorizedException("You are not the same user.");
        }
    }

}