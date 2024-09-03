package dtu.example.model.simpledtupay;

public class ResponseResult {
    private int statusCode;
    private String message;

    public ResponseResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public boolean isSuccessful() {
        return statusCode == 200;
    }

    public String getMessage() {
        return message;
    }
}
