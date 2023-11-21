package com.example.whenwhere.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@ToString
public class ResponseDto {

    private String message;

    private Object data;

    private HttpStatus status;

    // 메세지만 응답하는 객체
    public void setResponse(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
    // 바디 값을 포함하여 응답하는 객체
    public void setResponse(String message, Object data, HttpStatus status){
        this.message = message;
        this.data = data;
        this.status = status;
    }
}
