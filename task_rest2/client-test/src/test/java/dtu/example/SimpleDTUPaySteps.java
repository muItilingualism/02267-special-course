package dtu.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import dtu.example.model.Payment;
import dtu.example.model.PaymentResult;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {
    String cid, mid;
    SimpleDTUPay dtuPay = new SimpleDTUPay();
    PaymentResult response;
    List<Payment> list;

    @Given("a customer with id {string}")
    public void aCustomerWithId(String cid) {
        this.cid = cid;
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String mid) {
        this.mid = mid;
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        response = dtuPay.pay(amount, this.cid, this.mid);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(response.getStatusCode() == 200);
    }

    @Given("a successful payment of {int} kr from customer {string} to merchant {string}")
    public void aSuccessfulPaymentOfKrFromCustomerToMerchant(int amount, String customerId, String merchantId) {
        response = dtuPay.pay(amount, customerId, merchantId);
        assertTrue(response.getStatusCode() == 200);
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        this.list = dtuPay.list();
    }

    @Then("the list contains the payment where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsThePaymentWhereCustomerPaidKrToMerchant(String customerId, int amount,
            String merchantId) {
        Payment payment = this.list.get(0);

        assertEquals(amount, payment.getAmount());
        assertEquals(customerId, payment.getCustomerId());
        assertEquals(merchantId, payment.getMerchantId());
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertTrue(this.response.getStatusCode() != 200);
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, this.response.getMessage());
    }
}
