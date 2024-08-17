package dtu.example.model;

public class PaymentResult {
    private int statusCode;
    private String message;

    public PaymentResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
