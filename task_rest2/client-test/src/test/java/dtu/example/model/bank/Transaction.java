package dtu.example.model.bank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private int id;
    private String debtor;
    private String creditor;
    private int amount;
    private int balance;
    private String description;
    private String time;
}
