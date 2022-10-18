package com.example.travelleronline.util;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public abstract class MasterController {

    @Autowired
    protected ModelMapper modelMapper;

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDto handleBadRequest(Exception e){
        return buildErrorInfo(e,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ErrorDto handleUnauthorized(Exception e){
        return buildErrorInfo(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDto handleNotFound(Exception e){
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
        return dto;
    }

}