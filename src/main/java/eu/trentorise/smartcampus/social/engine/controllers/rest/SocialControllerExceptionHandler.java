package eu.trentorise.smartcampus.social.engine.controllers.rest;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import eu.trentorise.smartcampus.social.engine.beans.Result;

@ControllerAdvice
public class SocialControllerExceptionHandler {
	
	// IllegalArgument
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Result handleIllegalArgException(Exception ex) {
        Result result = new Result(ex, HttpStatus.BAD_REQUEST.value());
        result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        return result;
    }
    
    // MissingRequestParameter
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Result handleMissingParamException(Exception ex) {
        Result result = new Result(ex, HttpStatus.BAD_REQUEST.value());
        result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        return result;
    }
    
//    @ExceptionHandler(ForbiddenException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ResponseBody
//    Result handleForbiddenResourceException(Exception ex) {
//        Result result = new Result(ex, HttpStatus.FORBIDDEN.value());
//        result.setErrorCode(HttpStatus.FORBIDDEN.toString());
//        return result;
//    }

    // SecurityException
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    Result handleSecurityException(Exception ex) {
        Result result = new Result(ex, HttpStatus.FORBIDDEN.value());
        result.setErrorCode(HttpStatus.FORBIDDEN.toString());
        return result;
    }
    
    // NotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Result handleNotReadableException(Exception ex) {
        Result result = new Result(ex, HttpStatus.BAD_REQUEST.value());
        result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        return result;
    }
    
    // PropertyReferenceException
    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Result handlePropertyReferenceException(Exception ex) {
        Result result = new Result(ex, HttpStatus.BAD_REQUEST.value());
        result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        return result;
    }

}
