package com.example.travelleronline.general;

import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.general.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
@Slf4j
@RestController
public abstract class MasterController {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDTO handleBadRequest(BadRequestException e){
        return buildErrorInfo(e,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ErrorDTO handleUnauthorized(UnauthorizedException e){
        return buildErrorInfo(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDTO handleNotFound(NotFoundException e){
        return buildErrorInfo(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorDTO handleAllOther(Exception e){
        return buildErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO buildErrorInfo(Exception e, HttpStatus status) {
        ErrorDTO dto = new ErrorDTO();
        dto.setStatus(status.value());
        dto.setMessage(e.getMessage());
        dto.setTime(LocalDateTime.now());
        log.error("Error with message={}, stackTrace={}",e.getMessage(),e.getStackTrace());
        return dto;
    }

    protected int getUserId(HttpSession session) {
        if (session.getAttribute(USER_ID) == null) {
            return 0;
        }
        return (int) session.getAttribute(USER_ID);
    }

}