package com.example.whenwhere.Util;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class CustomExceptionHandler {

    public HttpStatus getStatus(Exception e){
        try{
            String message = e.getMessage();
            Integer code = Integer.parseInt(message.split(" ")[0]);

            if(code == 400){
                return HttpStatus.BAD_REQUEST;
            }
            else if(code == 401){
                return HttpStatus.UNAUTHORIZED;
            }
            else if(code == 403){
                return HttpStatus.FORBIDDEN;
            }
            else if(code == 404){
                return HttpStatus.NOT_FOUND;
            }
            else{
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }catch(Exception ex){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    // 에러 메세지 입력 시 '_'로 이어지게 해야 함
    public String getMessage(Exception e){
        try{
            String message = e.getMessage();
            String detail = message.split(" ")[2];
            return detail;
        }catch(Exception ex){
            return "EMPTY";
        }
    }
}
