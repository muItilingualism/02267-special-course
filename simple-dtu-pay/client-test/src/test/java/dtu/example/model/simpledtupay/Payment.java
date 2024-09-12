package dtu.example.model.simpledtupay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private int amount;
    private String customerId;
    private String merchantId;
}
