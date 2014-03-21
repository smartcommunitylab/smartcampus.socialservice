/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.social.engine.controllers.rest;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import eu.trentorise.smartcampus.social.engine.beans.Result;

@ControllerAdvice
public class ControllerExceptionHandler {
	
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
    
    // MethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    Result handleMethodNotSupportedException(Exception ex) {
        Result result = new Result(ex, HttpStatus.METHOD_NOT_ALLOWED.value());
        result.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED.toString());
        return result;
    }

}
