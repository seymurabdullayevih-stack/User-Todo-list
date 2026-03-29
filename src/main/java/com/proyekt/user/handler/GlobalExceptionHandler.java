package com.proyekt.user.handler;


import com.proyekt.user.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
@ControllerAdvice
// Bütün Controller-lərdən gələn exception-ları mərkəzi yerdə tutur
public class GlobalExceptionHandler {


    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError> handlerBaseException(BaseException baseException, WebRequest request){


        return ResponseEntity.badRequest().body(createApiError(baseException.getMessage(),request));
    }


     public String getHostName(){


         try {
             // InetAddress.getLocalHost() → Local kompüterin IP və host məlumatını verir
             // .getHostName() → Host adını String olaraq qaytarır
             return InetAddress.getLocalHost().getHostName();

         } catch (UnknownHostException e) {
             // Əgər host adı tapılmazsa (nadir hal)
             System.out.println("Xəta oldu! " + e.getMessage());
         }

         return null;
     }

     public <E> ApiError <E> createApiError (E message,WebRequest request){

        ApiError<E> apiError = new ApiError<>();


        apiError.setStatus(HttpStatus.BAD_REQUEST.value());

        Exception<E> exception = new Exception<>();

        exception.setCreateTime(new Date());

        exception.setHostName(getHostName());

        exception.setMessage(message);

        exception.setPath(request.getDescription(false));

        apiError.setException(exception);

        return apiError;



     }
}
