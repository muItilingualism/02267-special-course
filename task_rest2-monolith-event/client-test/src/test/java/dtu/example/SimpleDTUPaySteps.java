package dtu.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import dtu.example.model.bank.Account;
import dtu.example.model.bank.AccountCreationRequest;
import dtu.example.model.bank.User;
import dtu.example.model.simpledtupay.Payment;
import dtu.example.model.simpledtupay.ResponseResult;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SimpleDTUPaySteps {
    String cCpr, mCpr;
    String cBankId, mBankId;
    String cId, mId;
    SimpleDTUPay dtuPay = new SimpleDTUPay();
    ResponseResult response;
    List<Payment> list;
    Account cBankAccount, mBankAccount;

    Bank bank = new Bank();

    @Given("a customer with id {string}")
    public void aCustomerWithId(String cpr) {
        this.cCpr = cpr;
        this.cBankId = bank.createAccount(new AccountCreationRequest(1000, new User(cpr, "Cust", "Omer")));
        this.cId = dtuPay.registerCustomer(this.cCpr, this.cBankId).getMessage();
        cBankAccount = bank.getAccount(this.cBankId);
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String cpr) {
        this.mCpr = cpr;
        this.mBankId = bank.createAccount(new AccountCreationRequest(1000, new User(cpr, "Mer", "Chant")));
        this.mId = dtuPay.registerMerchant(this.mCpr, this.mBankId).getMessage();
        mBankAccount = bank.getAccount(this.mBankId);
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        response = dtuPay.pay(amount, this.cId, this.mId);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @Then("the merchant has {int} kr less in his bank account")
    public void theMerchantHasKrLessInHisBankAccount(int amount) {
        int prevBal = mBankAccount.getBalance();
        int currBal = bank.getAccount(this.mBankId).getBalance();

        assertTrue((prevBal - amount) == currBal);
    }

    @Then("the customer has {int} kr more in his bank account")
    public void theCustomerHasKrMoreInHisBankAccount(int amount) {
        int prevBal = cBankAccount.getBalance();
        int currBal = bank.getAccount(this.cBankId).getBalance();

        assertTrue((prevBal + amount) == currBal);
    }

    @Given("a successful payment of {int} kr from customer {string} to merchant {string}")
    public void aSuccessfulPaymentOfKrFromCustomerToMerchant(int amount, String customerId, String merchantId) {
        response = dtuPay.pay(amount, this.cId, this.mId);
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
                    payment.getCustomerId().equals(cId) &&
                    payment.getMerchantId().equals(mId)) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Given("an unregistered customer with id {string}")
    public void anUnregisteredCustomerWithId(String cid) {
        this.cBankId = UUID.randomUUID().toString();
    }

    @Given("an unregistered merchant with id {string}")
    public void anUnregisteredMerchantWithId(String mid) {
        this.mBankId = UUID.randomUUID().toString();
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
    public void theCustomerIsRegistered() {
        response = dtuPay.registerCustomer(this.cCpr, this.cBankId);
    }

    @Then("the customer registration is successful")
    public void theCustomerRegistrationIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @Then("the customer registration is unsuccessful")
    public void theCustomerRegistrationIsUnsuccessful() {
        assertFalse(response.isSuccessful());
    }

    @When("the merchant is registered")
    public void theMerchantIsRegistered() {
        response = dtuPay.registerMerchant(this.mCpr, this.mBankId);
    }

    @Then("the merchant registration is successful")
    public void theMerchantRegistrationIsSuccessful() {
        assertTrue(response.isSuccessful());
    }

    @Then("the merchant registration is unsuccessful")
    public void theMerchantRegistrationIsUnsuccessful() {
        assertFalse(response.isSuccessful());
    }

    @After
    public void removeBankAccounts() {
        bank.deleteAccount(cBankId);
        bank.deleteAccount(mBankId);
    }
}
