package dtu.example.model;

public class Payment {
    private int amount;
    private String customerId;
    private String merchantId;

    public Payment() {}
    
    public Payment(int amount, String customerId, String merchantId) {
        this.amount = amount;
        this.customerId = customerId;
        this.merchantId = merchantId;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public String getMerchantId() {
        return this.merchantId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCustomerId(String id) {
        this.customerId = id;
    }

    public void setMerchantId(String id) {
        this.merchantId = id;
    }
}
