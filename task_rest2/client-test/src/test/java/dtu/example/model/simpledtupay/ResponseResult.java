package dtu.example.model.simpledtupay;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseResult {
    private int statusCode;
    private String message;

    public boolean isSuccessful() {
        return statusCode == 200;
    }

    public String getMessage() {
        return message;
    }
}
