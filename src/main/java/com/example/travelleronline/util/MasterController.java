package com.example.travelleronline.util;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public abstract class MasterController {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_ADDRESS = "remote_address";

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDto handleBadRequest(BadRequestException e){
        return buildErrorInfo(e,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ErrorDto handleUnauthorized(UnauthorizedException e){
        return buildErrorInfo(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDto handleNotFound(NotFoundException e){
        return buildErrorInfo(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorDto handleAllOther(Exception e){
        return buildErrorInfo(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDto buildErrorInfo(Exception e, HttpStatus status) {
        ErrorDto dto = new ErrorDto();
        dto.setStatus(status.value());
        dto.setMessage(e.getMessage());
        dto.setTime(LocalDateTime.now());
        e.printStackTrace(); //TODO add to log file
        return dto;
    }

}