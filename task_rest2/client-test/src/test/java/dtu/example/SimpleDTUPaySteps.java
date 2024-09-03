package dtu.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import dtu.example.model.bank.Account;
import dtu.example.model.bank.AccountCreationRequest;
import dtu.example.model.simpledtupay.Payment;
import dtu.example.model.simpledtupay.ResponseResult;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {
    String cCpr, mCpr;
    String cBId, mBId;
    SimpleDTUPay dtuPay = new SimpleDTUPay();
    ResponseResult response;
    List<Payment> list;
    Account cAcc;
    Account mAcc;

    Bank bank = new Bank();

    @Given("a customer with id {string}")
    public void aCustomerWithId(String cpr) {
        this.cCpr = cpr;
        this.cBId = bank.createAccount(new AccountCreationRequest(1000, cpr, "Cust", "Omer"));
        cAcc = bank.getAccount(this.cBId);
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String cpr) {
        this.mCpr = cpr;
        this.mBId = bank.createAccount(new AccountCreationRequest(1000, cpr, "Mer", "Chant"));
        mAcc = bank.getAccount(this.mBId);
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        response = dtuPay.pay(amount, this.cBId, this.mBId);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @Then("the merchant has {int} kr less in his bank account")
    public void theMerchantHasKrLessInHisBankAccount(int amount) {
        int prevBal = mAcc.getBalance();
        int currBal = bank.getAccount(this.mBId).getBalance();

        assertTrue((prevBal - amount) == currBal);
    }

    @Then("the customer has {int} kr more in his bank account")
    public void theCustomerHasKrMoreInHisBankAccount(int amount) {
        int prevBal = cAcc.getBalance();
        int currBal = bank.getAccount(this.cBId).getBalance();

        assertTrue((prevBal + amount) == currBal);
    }

    @Given("a successful payment of {int} kr from customer {string} to merchant {string}")
    public void aSuccessfulPaymentOfKrFromCustomerToMerchant(int amount, String customerId, String merchantId) {
        response = dtuPay.pay(amount, this.cBId, this.mBId);
        assertTrue(response.isSuccessful());
    }

    @When("the manager asks for a list of payments")
    public void theManagerAsksForAListOfPayments() {
        this.list = dtuPay.list();
    }

    @Then("the list contains the payment where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsThePaymentWhereCustomerPaidKrToMerchant(String customerId, int amount,
            String merchantId) {
        boolean found = false;
        for (Payment payment : this.list) {
            if (payment.getAmount() == amount &&
                    payment.getCustomerId().equals(cBId) &&
                    payment.getMerchantId().equals(mBId)) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Given("an unregistered customer with id {string}")
    public void anUnregisteredCustomerWithId(String cid) {
        this.cBId = UUID.randomUUID().toString();
    }

    @Given("an unregistered merchant with id {string}")
    public void anUnregisteredMerchantWithId(String mid) {
        this.mBId = UUID.randomUUID().toString();
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(this.response.isSuccessful());
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, this.response.getMessage());
    }

    @When("the customer is registered")
    public void theAccountIsRegistered() {
        response = dtuPay.registerCustomer(this.cCpr, this.cBId);
    }

    @Then("the customer registration is successful")
    public void theAccountRegistrationIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @After
    public void removeBankAccounts() {
        bank.deleteAccount(cBId);
        bank.deleteAccount(mBId);
    }
}
