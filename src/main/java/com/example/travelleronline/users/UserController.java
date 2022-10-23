package com.example.travelleronline.users;

import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.users.dtos.*;
import com.example.travelleronline.util.MasterController;
import com.example.travelleronline.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserController extends MasterController {

    @Autowired
    private UserService userService;

    // Front-end(originally): Avoid spaces at the beginning and at the end.
    // Front-end(afterwards): A verification email has been sent to: ...
    // ...You have ten days to verify your email.
    @PostMapping("/app/registration")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserWithoutPassDTO register(@RequestBody RegisterDTO dto) {
        return userService.register(dto);
    }

    @PutMapping(value = "/app/verify-email/{token}")
    public void verifyEmail(@PathVariable String token) {
        userService.verifyEmail(token);
    }

    @PostMapping("/app/login")
    public UserProfileDTO logIn(@RequestBody LoginDTO dto, HttpServletRequest req) {
        HttpSession session = req.getSession();;
        if (session.getAttribute(LOGGED) != null && (boolean) session.getAttribute(LOGGED)) {
            session.invalidate();
            throw new BadRequestException("The user was already logged in. Session terminated.");
        }
        UserProfileDTO result = userService.logIn(dto);
        logUser(req, result.getUserId());
        return result;
    }

    @PutMapping("/app/logout")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/users/{uid}")
    public UserProfileDTO getById(@PathVariable int uid, HttpServletRequest req) {
        validateLoggedIn(req);
        return userService.getById(uid);
    }

    @GetMapping(value = "/users/search")
    public List<UserProfileDTO> getAllByName(@RequestParam String name, HttpServletRequest req) {
        validateLoggedIn(req);
        return userService.getAllByName(name);
    }

    // News Feed
    @GetMapping("/news-feed") // TODO ? infinite scroll is correct
    public List<PostDTO> showNewsFeed(HttpServletRequest req, // TODO ??? List<PostDTO>
                                        @RequestParam("days_min") int daysMin,
                                      @RequestParam("days_max") int daysMax) {
        validateLoggedIn(req);
        return userService.showNewsFeed(getUserId(req), daysMin, daysMax);
    }

    // Profile Page
    @GetMapping("/users/{uid}/posts") // TODO ? infinite scroll is correct ??? List<PostDTO>
    public List<PostDTO> showPostsOfUser(HttpServletRequest req, @PathVariable int uid,
                                         @RequestParam("days_min") int daysMin,
                                         @RequestParam("days_max") int daysMax,
                                         @RequestParam("order_by") String orderBy) {
        validateLoggedIn(req);
        return userService.showPostsOfUser(uid, daysMin, daysMax, orderBy);
    }

    // unsubscribes after following visit
    @PutMapping("/users/{uid}/subscribe")
    public int subscribe(@PathVariable int uid, HttpServletRequest req) {
        validateLoggedIn(req);
        int sid = getUserId(req); // subscriber's id
        return userService.subscribe(sid, uid);
    }

    @GetMapping("/users/my-subscribers")
    public List<UserProfileDTO> showSubscribers(HttpServletRequest req) {
        validateLoggedIn(req);
        return userService.showSubscribers(getUserId(req));
    }

    @GetMapping("/users/my-subscriptions")
    public List<UserProfileDTO> showSubscriptions(HttpServletRequest req) {
        validateLoggedIn(req);
        return userService.showSubscriptions(getUserId(req));
    }


    @PutMapping("/users/edit-info")
    public void editUserInfo(@RequestBody EditInfoDTO dto, HttpServletRequest req) {
        validateLoggedIn(req);
        userService.editUserInfo(dto, getUserId(req));
    }

    @PutMapping("/users/edit-password")
    public void editUserPass(@RequestBody EditPassDTO dto, HttpServletRequest req) {
        validateLoggedIn(req);
        userService.editUserPass(dto, getUserId(req));
    }

    @DeleteMapping("/users/delete-user")
    public void deleteById(HttpServletRequest req) {
        validateLoggedIn(req);
        userService.deleteById(getUserId(req));
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

    public int getUserId(HttpServletRequest req) {
        if (req.getSession().getAttribute(USER_ID) == null) {
            return 0;
        }
        return (int) req.getSession().getAttribute(USER_ID);
    }

}