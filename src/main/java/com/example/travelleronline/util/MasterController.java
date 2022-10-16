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

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDto handleBadRequest(Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMessage(e.getMessage());
        dto.setStatus(HttpStatus.BAD_REQUEST.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ErrorDto handleUnauthorized(Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMessage(e.getMessage());
        dto.setStatus(HttpStatus.UNAUTHORIZED.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDto handleNotFound(Exception e){
        ErrorDto dto = new ErrorDto();
        dto.setMessage(e.getMessage());
        dto.setStatus(HttpStatus.NOT_FOUND.value());
        dto.setTime(LocalDateTime.now());
        return dto;
    }

}