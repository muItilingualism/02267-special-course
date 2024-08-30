package dtu.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import dtu.example.model.AccountCreationRequest;
import dtu.example.model.Payment;
import dtu.example.model.PaymentResult;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {
    String cid, mid;
    SimpleDTUPay dtuPay = new SimpleDTUPay();
    PaymentResult response;
    List<Payment> list;

    Bank bank = new Bank();

    @Given("a customer with id {string}")
    public void aCustomerWithId(String cid) {
        this.cid = bank.createAccount(new AccountCreationRequest(1000, cid, "Cust", "Omer"));
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String mid) {
        this.mid = bank.createAccount(new AccountCreationRequest(1000, mid, "Mer", "Chant"));
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        response = dtuPay.pay(amount, this.cid, this.mid);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @Given("a successful payment of {int} kr from customer {string} to merchant {string}")
    public void aSuccessfulPaymentOfKrFromCustomerToMerchant(int amount, String customerId, String merchantId) {
        response = dtuPay.pay(amount, this.cid, this.mid);
        assertTrue(response.isSuccessful());
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        this.list = dtuPay.list();
    }

    @Then("the list contains the payment where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsThePaymentWhereCustomerPaidKrToMerchant(String customerId, int amount,
            String merchantId) {
        Payment payment = this.list.get(1); // FIXME dependant on scenario order.

        assertEquals(amount, payment.getAmount());
        assertEquals(cid, payment.getCustomerId());
        assertEquals(mid, payment.getMerchantId());
    }

    @Given("an unregistered customer with id {string}")
    public void anUnregisteredCustomerWithId(String cid) {
        this.cid = UUID.randomUUID().toString();
    }

    @Given("an unregistered merchant with id {string}")
    public void anUnregisteredMerchantWithId(String mid) {
        this.mid = UUID.randomUUID().toString();
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(this.response.isSuccessful());
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, this.response.getMessage());
    }

    @After
    public void removeBankAccounts() {
        bank.deleteAccount(cid);
        bank.deleteAccount(mid);
    }
}
