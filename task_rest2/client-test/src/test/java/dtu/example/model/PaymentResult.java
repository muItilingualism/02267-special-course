package dtu.example.model;

public class PaymentResult {
    private int statusCode;
    private String message;

    public PaymentResult(int statusCode, String message) {
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
