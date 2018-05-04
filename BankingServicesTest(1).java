package com.cg.banking.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;

import com.cg.banking.beans.Account;
import com.cg.banking.beans.Address;
import com.cg.banking.beans.Customer;
import com.cg.banking.beans.Transaction;
import com.cg.banking.daoservices.BankingDOAOServicesImpl;
import com.cg.banking.exceptions.AccountBlockedException;
import com.cg.banking.exceptions.AccountNotFoundException;
import com.cg.banking.exceptions.BankingServicesDownException;
import com.cg.banking.exceptions.CustomerNotFoundException;
import com.cg.banking.exceptions.InsufficientAmountException;
import com.cg.banking.exceptions.InvalidAccountTypeException;
import com.cg.banking.exceptions.InvalidAmountException;
import com.cg.banking.exceptions.InvalidPinNumberException;
import com.cg.banking.services.BankingServices;
import com.cg.banking.services.BankingServicesImpl;
import com.cg.banking.utilities.BankingUtility;


public class BankingServicesTest {
	private static BankingServices bankingServices;
	@BeforeClass
	public static void setUpTestEnv(){
		bankingServices=new BankingServicesImpl();
	}
	@Before
	public void setUpMockData(){
		Customer customer1=new Customer(BankingUtility.CUSTOMER_ID_COUNTER++, "Deepak", "Muraree", "acn@sad.com", "asdn43", new Address(111111, "DGP", "WB"), new Address(222222, "DGP", "WB"));
		Customer customer2=new Customer(BankingUtility.CUSTOMER_ID_COUNTER++, "Rahul", "Muraree", "rcn@sad.com", "atdn43", new Address(111111, "DGP", "WB"), new Address(222222, "DGP", "WB"));
		Account account1=new Account(5555, 0, "Savings", BankingUtility.ACCOUNT_STATUS_ACTIVE, 6000, BankingUtility.ACCOUNT_NO_COUNTER++);
		Account account2=new Account(6666, 0, "Current", BankingUtility.ACCOUNT_STATUS_ACTIVE, 9000, BankingUtility.ACCOUNT_NO_COUNTER++);
		Account account3=new Account(7777, 0, "Salary", BankingUtility.ACCOUNT_STATUS_BLOCKED, 19000, BankingUtility.ACCOUNT_NO_COUNTER++);
		Transaction transaction1=new Transaction(BankingUtility.TRANSACTION_ID_COUNTER++, 500, "Withdrawal");
		Transaction transaction2=new Transaction(BankingUtility.TRANSACTION_ID_COUNTER++, 500, "Deposit");
		BankingDOAOServicesImpl.customers.put(customer1.getCustomerId(), customer1);
		BankingDOAOServicesImpl.customers.put(customer2.getCustomerId(), customer2);
		BankingDOAOServicesImpl.customers.get(customer1.getCustomerId()).getAccounts().put(account1.getAccountNo(), account1);
		BankingDOAOServicesImpl.customers.get(customer1.getCustomerId()).getAccounts().put(account3.getAccountNo(), account3);
		BankingDOAOServicesImpl.customers.get(customer2.getCustomerId()).getAccounts().put(account2.getAccountNo(), account2);
		BankingDOAOServicesImpl.customers.get(customer1.getCustomerId()).getAccounts().get(account1.getAccountNo()).getTransactions().put(transaction1.getTransactionId(), transaction1);
		BankingDOAOServicesImpl.customers.get(customer2.getCustomerId()).getAccounts().get(account2.getAccountNo()).getTransactions().put(transaction2.getTransactionId(), transaction2);
	}
	
	
	@Test
	public void getCustomerDetailForValidData(){
		Customer actualCustomer=BankingDOAOServicesImpl.customers.get(1);
		Customer expectedCustomer=new Customer(1, "Deepak", "Muraree", "acn@sad.com", "asdn43", new Address(111111, "DGP", "WB"), new Address(222222, "DGP", "WB"));
		assertEquals(expectedCustomer, actualCustomer);
	}
	
	@Test
	public void getCustomerIdCounterForValidData(){
		int expectedId=3;
		int actualId=BankingUtility.CUSTOMER_ID_COUNTER;
		assertEquals(expectedId, actualId);
	}
	
	
	@Test(expected=CustomerNotFoundException.class)
	public void openAccountForInvalidCustomerId()throws CustomerNotFoundException, BankingServicesDownException, InvalidAmountException, InvalidAccountTypeException{
		bankingServices.openAccount(66, "Savings", 6000);
	}
	
	@Test(expected=InvalidAmountException.class)
	public void openAccountForInvalidInitBalance()throws CustomerNotFoundException, BankingServicesDownException, InvalidAmountException, InvalidAccountTypeException{
		bankingServices.openAccount(1, "Savings", -700);
	}
	
	@Test(expected=InvalidAccountTypeException.class)
	public void openAccountForInvalidAccountType()throws CustomerNotFoundException, BankingServicesDownException, InvalidAmountException, InvalidAccountTypeException{
		bankingServices.openAccount(1, "Savin", 6000);
	}
	
	@Test
	public void openAccountForValidData()throws CustomerNotFoundException, BankingServicesDownException, InvalidAmountException, InvalidAccountTypeException{
		assertEquals(20000004, bankingServices.openAccount(1, "Savings", 6000));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void depositAmountInvalidCustomerId() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException{
		bankingServices.depositAmount(5, 20000001, 5000);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void depositAmountForInvalidAccountNo() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException{
		bankingServices.depositAmount(1, 20000006, 5000);
	}
	
	@Test(expected=AccountBlockedException.class)
	public void depositAmountForBlockedAccount() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException{
		bankingServices.depositAmount(1, 20000003, 5000);
	}
	
	@Test
	public void depositAmountForValidData() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException{
		assertEquals(11000, bankingServices.depositAmount(1, 20000001, 5000),0.5);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void withdrawAmountForInvalidCustomerId() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.withdrawAmount(6, 20000001, 500, 5555);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void withdrawAmountForInvalidAccountNo() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.withdrawAmount(1, 20000006, 500, 5555);
	}
	
	@Test(expected=InsufficientAmountException.class)
	public void withdrawAmountForInsufficientAmount() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.withdrawAmount(1, 20000001, 50000000, 5555);
	}
	
	@Test
	public void withdrawAmountForValidData() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		assertEquals(5500, bankingServices.withdrawAmount(1, 20000001, 500, 5555), 0.5);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void withdrawAmountForInvalidPin() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.withdrawAmount(1, 20000001, 500, 5525);
	}
	
	@Test(expected=AccountBlockedException.class)
	public void withdrawAmountForBlockedAccount() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.withdrawAmount(1, 20000003, 500, 7777);
	}
	
	@Test(expected=InsufficientAmountException.class)
	public void fundTransferForInsufficientAmount() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.fundTransfer(2, 20000002, 1, 20000001, 500000, 5555);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void fundTransferForInvalidCustomerId() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.fundTransfer(2, 20000002, 6, 20000001, 500, 5555);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void fundTransferForInvalidAccountNo() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.fundTransfer(2, 20000002, 1, 20000007, 500, 5555);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void fundTransferForInvalidPin() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.fundTransfer(2, 20000002, 1, 20000001, 500, 5595);
	}
	
	@Test
	public void fundTransferForValidData() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		assertEquals(true, bankingServices.fundTransfer(2, 20000002, 1, 20000001, 500, 5555));
	}
	
	@Test(expected=AccountBlockedException.class)
	public void fundTransferForBlockedAccount() throws InsufficientAmountException, CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException, AccountBlockedException {
		bankingServices.fundTransfer(2, 20000002, 1, 20000003, 500, 5555);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void getCustomerDetailsForInvalidId() throws CustomerNotFoundException, BankingServicesDownException {
		bankingServices.getCustomerDetails(7);
	}
	
//	@Test
//	public void getCustomerDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException {
//		assertEquals("Customer [customerId=2, firstName=Rahul, lastName=Muraree, emailId=rcn@sad.com, panCard=atdn43, localAddress=Address: pincode=111111, city=DGP, state=WB, homeAddress=Address: pincode=222222, city=DGP, state=WB, accountIdxCounter=0, accounts={20000001=Account [pinNumber=0, pinCounter=0, accountType=savings, status=ACTIVE, accountBalance=4444.0, accountNo=20000001, transactionIdCounter=101, transactionIdxCounter=0, transactions={0=Transaction: transactionId=101, amount=500.0, transactionType=Deposit}], 20000002=Account [pinNumber=0, pinCounter=0, accountType=Current, status=ACTIVE, accountBalance=9500.0, accountNo=20000002, transactionIdCounter=101, transactionIdxCounter=0, transactions={0=Transaction: transactionId=101, amount=500.0, transactionType=Deposit}]}]", bankingServices.getCustomerDetails(2));
//	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void getaccountDetailsForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountDetails(7, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void getaccountDetailsForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountDetails(1, 20000008);
	}
	
	@Test
	public void getaccountDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void generatePinForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.generateNewPin(7, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void generatePinForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.generateNewPin(1, 20000008); 
	}
	
//	@Test
//	public void generatePinDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
//		assertEquals(1530, bankingServices.generateNewPin(1, 20000001));
//	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void changeAccountPinForInvalidCustomerId() throws CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException {
		bankingServices.changeAccountPin(8, 20000001, 5555, 9999);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void changeAccountPinForInvalidAccountNo() throws CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException {
		bankingServices.changeAccountPin(1, 20000090, 5555, 9999); 
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void changeAccountPinForInvalidPin() throws CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException {
		bankingServices.changeAccountPin(1, 20000001, 5577, 9999);
	}
	
	@Test
	public void changeAccountPinForValidData() throws CustomerNotFoundException, AccountNotFoundException, InvalidPinNumberException, BankingServicesDownException {
		assertEquals(true, bankingServices.changeAccountPin(1, 20000001, 5555, 9999));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void getCustomerAllAccountDetailsForInvalidId() throws CustomerNotFoundException, BankingServicesDownException {
		bankingServices.getcustomerAllAccountDetails(8);
	}
	
	@Test
	public void getCustomerAllAccountDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException {
		bankingServices.getcustomerAllAccountDetails(1);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void getAllAccountTransactionDetailsForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountAllTransaction(8, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void getAllAccountTransactionDetailsForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountAllTransaction(1, 20000008);
	}
	
	@Test
	public void getAllAccountTransactionDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountDetails(1, 20000001);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void getAllTransactionDetailsForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountAllTransaction(5, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void getAllTransactionDetailsForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountAllTransaction(1, 20000007);
	}
	
	@Test
	public void getAllTransactionDetailsForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.getAccountAllTransaction(1, 20000001);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void accountStatusForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		bankingServices.accountStatus(5, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void accountStatusForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		bankingServices.accountStatus(1, 20000089);
	}
	
	@Test(expected=AccountBlockedException.class)
	public void accountStatusForBlockedAccount() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException {
		bankingServices.accountStatus(1, 20000003);
	}
	
	@Test
	public void accountStatusForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		assertEquals("ACTIVE", bankingServices.accountStatus(1, 20000001));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void closeAccountForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.closeAccount(7, 20000001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void closeAccountForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		bankingServices.closeAccount(1, 20000005);
	}
	
	@Test
	public void closeAccountForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException {
		assertEquals(true, bankingServices.closeAccount(1, 20000003));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void showBalanceForInvalidCustomerId() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		bankingServices.showBalance(6, 20000001, 5555);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void showBalanceForInvalidAccountNo() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		bankingServices.showBalance(1, 20000007, 5555);
	}
	
	@Test(expected=AccountBlockedException.class)
	public void showBalanceForBlockedAccount() throws CustomerNotFoundException, AccountNotFoundException, BankingServicesDownException, AccountBlockedException {
		bankingServices.showBalance(1, 20000003, 7777);
	}
	
	@Test
	public void showBalanceForValidData() throws CustomerNotFoundException, BankingServicesDownException, AccountNotFoundException, AccountBlockedException {
		assertEquals(6000, bankingServices.showBalance(1, 20000001, 5555), 0.5);
	}
	
	@After
	public void tearDownMockData(){
		BankingDOAOServicesImpl.customers.clear();
		Customer.accounts.clear();
		Account.transactions.clear();
		BankingUtility.ACCOUNT_NO_COUNTER=20000001l;
		BankingUtility.CUSTOMER_ID_COUNTER=1;
		BankingUtility.TRANSACTION_ID_COUNTER=101;
	}
	@AfterClass
	public static void tearDownTestEnv(){
		bankingServices=null;
	}
}
