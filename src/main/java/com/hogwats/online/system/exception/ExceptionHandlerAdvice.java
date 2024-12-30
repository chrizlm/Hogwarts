package com.hogwats.online.system.exception;

import com.hogwats.online.system.Result;
import com.hogwats.online.system.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(ObjectNotFoundException.class)
    Result handleArtifactNotFoundException(ObjectNotFoundException ex){
        return new Result(false, StatusCode.NOT_FOUND, ex.getMessage(),null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Result handleValidationException(MethodArgumentNotValidException ex){
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(errors.size());
        errors.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });
        return Result.builder()
                .flag(false)
                .code(StatusCode.INVALID_ARGUMENT)
                .message("Provided arguments are invalid, see data for details.")
                .data(map)
                .build();
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    Result handleAuthenticationException(Exception ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "Username or password is incorrect", ex.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    Result handleInsufficientAuthenticationException(InsufficientAuthenticationException ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "Login credentials are missing", ex.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    Result handleAccountStatusException(AccountStatusException ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "Username account is abnormal", ex.getMessage());
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    Result handleInvalidBearerTokenException(InvalidBearerTokenException ex){
        return new Result(false, StatusCode.UNAUTHORIZED, "The access token provided is expired, revoked, malformed or invalid for other reasons", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    Result handleAccessDeniedException(Exception ex){
        return new Result(false, StatusCode.FORBIDDEN, "No permission", ex.getMessage());
    }

    //fallback exception
    @ExceptionHandler(Exception.class)
    Result handleOtherException(Exception ex){
        return new Result(false, StatusCode.INTERNAL_SERVER_ERROR, "A server internal error occurred", ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Result handleAccessDeniedException(NoHandlerFoundException ex) {
        return new Result(false, StatusCode.NOT_FOUND, "This API endpoint is not found.", ex.getMessage());
    }
}
