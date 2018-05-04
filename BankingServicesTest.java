package com.cg.banking.test;



import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cg.banking.beans.Account;
import com.cg.banking.beans.Address;
import com.cg.banking.beans.Customer;
import com.cg.banking.beans.Transaction;
import com.cg.banking.daoservices.BankingDAOServices;
import com.cg.banking.daoservices.BankingDAOServicesImpl;
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
import com.cg.banking.utility.BankingUtility;

public class BankingServicesTest {
	static BankingServices bankingServices;
	static BankingDAOServices bankingDAOServices;
	

	@BeforeClass
	public static void setUpTestEnv() {
		bankingServices=new BankingServicesImpl();
		bankingDAOServices=new BankingDAOServicesImpl();
	}
	
	@Before
	public void setUpMockData() {
		BankingUtility.CUSTOMER_ID_COUNTER=111;
		BankingUtility.ACCOUNT_ID_COUNTER=1;
		BankingUtility.TRANSACTION_ID_COUNTER=1;		
			
		BankingDAOServicesImpl.customers.put(BankingUtility.CUSTOMER_ID_COUNTER,new Customer("abhi", "ch", "abhi@abcd.com", "a1", new Address(123456, "hyd", "tel"), new Address(147852, "pune", "mhr")));
		
		//Account account1=new Account(1234, 0, "Savings", "Active", 10000);
		BankingDAOServicesImpl.customers.get(BankingUtility.CUSTOMER_ID_COUNTER).getAccounts().put(BankingUtility.ACCOUNT_ID_COUNTER,new Account(1234, 0, "Savings", "Active", 10000));
		
		//Transaction transaction1= new Transaction(BankingUtility.TRANSACTION_ID_COUNTER, 5000, "Deposit");
		BankingDAOServicesImpl.customers.get(BankingUtility.CUSTOMER_ID_COUNTER++).getAccounts().get(BankingUtility.ACCOUNT_ID_COUNTER++).getTransactions().put(BankingUtility.TRANSACTION_ID_COUNTER++, new Transaction(BankingUtility.TRANSACTION_ID_COUNTER, 5000, "Deposit"));
				
		
	}
	
	@Test
	public void testForValidCustomerID() throws BankingServicesDownException {
	assertEquals(112,bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852));
	}
	
	@Test
	public void testForInValidCustomerID() throws CustomerNotFoundException,BankingServicesDownException{
		assertNotEquals(114,bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852));
	}

	@Test
	public void testOnAccountNoForValidCustomerID() throws InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, BankingServicesDownException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		assertEquals(2,bankingServices.openAccount(customerId,"Savings", 10000));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnAccountNoForInValidCustomerIDValidAccountNoValidAmount() throws InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, BankingServicesDownException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		assertEquals(2,bankingServices.openAccount(256,"Savings", 10000));
	}
	
	@Test(expected=InvalidAccountTypeException.class)
	public void testOnAccountNoForValidCustomerIDInValidAccountNoValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		assertEquals(2,bankingServices.openAccount(customerId,"Child", 10000));
	}
	
	@Test(expected=InvalidAmountException.class)
	public void testOnAccountNoForValidCustomerIDValidAccountNoInValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException{
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		assertEquals(2,bankingServices.openAccount(customerId,"Savings", -250));
	}
	
	@Test
	public void testOnDepositAmountForValidCustomerIDValidAccountNoValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		assertEquals(15000, bankingServices.depositAmount(customerId, accountNo, 5000), 0);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnDepositAmountForInValidCustomerIDValidAccountNoValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.depositAmount(100, accountNo, 5000);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnDepositAmountForValidCustomerIDInValidAccountNoValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.depositAmount(customerId, 123, 5000);
	}
	
	@Test(expected=InvalidAmountException.class)
	public void testOnDepositAmountForValidCustomerIDValidAccountNoInValidAmount() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.depositAmount(customerId, accountNo, -250);
	}
	
	@Test
	public void testOnWithdrawAmountForValidCustomerIDValidAccountNoValidAmountValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		assertEquals(9000, bankingServices.withdrawAmount(customerId, accountNo,1000, pinNumber), 0);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnWithdrawAmountForInValidCustomerIDValidAccountNoValidAmountValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.withdrawAmount(120, accountNo,1000, pinNumber);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnWithdrawAmountForValidCustomerIDInValidAccountNoValidAmountValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.withdrawAmount(customerId,-256,1000, pinNumber);
	}
	
	@Test(expected=InvalidAmountException.class)
	public void testOnWithdrawAmountForValidCustomerIDValidAccountNoInValidAmountValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.withdrawAmount(customerId, accountNo,-1000, pinNumber);
	}
	
	@Test(expected=InsufficientAmountException.class)
	public void testOnWithdrawAmountForValidCustomerIDValidAccountNoInSuffAmountValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.withdrawAmount(customerId, accountNo,20000, pinNumber);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void testOnWithdrawAmountForValidCustomerIDValidAccountNoValidAmountInValidPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.withdrawAmount(customerId, accountNo,1000,-100);
	}
	
	@Test
	public void testOnFundTransferForVCustomerIDFromVAccNoFromVCustomerIDToVAccNoToVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, accountNoFrom, customerIdTo, accountNoTo, 1000, pinNumber);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnFundTransferForInVCustomerIDFromVAccNoFromVCustomerIDToVAccNoToVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(-100, accountNoFrom, customerIdTo, accountNoTo, 1000, pinNumber);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnFundTransferForVCustomerIDFromInVAccNoFromVCustomerIDToVAccNoToVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, -20, customerIdTo, accountNoTo, 1000, pinNumber);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnFundTransferForVCustomerIDFromVAccNoFromInVCustomerIDToVAccNoToVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, accountNoFrom, -100, accountNoTo, 1000, pinNumber);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnFundTransferForVCustomerIDFromVAccNoFromVCustomerIDToInVAccNoToVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom,accountNoFrom, customerIdTo, -20, 1000, pinNumber);
	}
	
	@Test(expected=InvalidAmountException.class)
	public void testOnFundTransferForVCustomerIDFromVAccNoFromVCustomerIDToVAccNoToInVAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, accountNoFrom, customerIdTo, accountNoTo, -50, pinNumber);
	}
	
	@Test(expected=InsufficientAmountException.class)
	public void testOnFundTransferForVCustomerIDFromVAccNoFromVCustomerIDToVAccNoToInSuffAmountVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, accountNoFrom, customerIdTo, accountNoTo, 30000, pinNumber);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void testOnFundTransferForVCustomerIDFromVAccNoFromVCustomerIDToVAccNoToVAmountInVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InsufficientAmountException, InvalidPinNumberException, AccountBlockedException {
		int customerIdFrom=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoFrom=bankingServices.openAccount(customerIdFrom,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerIdFrom, accountNoFrom);
		int customerIdTo=bankingServices.acceptCustomerDetails("akash", "gupta", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNoTo=bankingServices.openAccount(customerIdTo,"Current", 5000);
		bankingServices.fundTransfer(customerIdFrom, accountNoFrom, customerIdTo, accountNoTo, 1000, -100);
	}
	
	@Test
	public void testOnBalanceEnquiryForVCustomerIDVAccNoVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.showBalance(customerId, accountNo, pinNumber);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnBalanceEnquiryForInVCustomerIDVAccNoVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.showBalance(20, accountNo, pinNumber);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnBalanceEnquiryForVCustomerIDInVAccNoVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.showBalance(customerId, 40, pinNumber);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void testOnBalanceEnquiryForVCustomerIDVAccNoInVPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int pinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.showBalance(customerId, accountNo, 0);
	}
	
	@Test
	public void testOnChangeAccountPinForVCustomerIDVAccNoVOldPinVNewPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int oldPinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.changeAccountPin(customerId, accountNo, oldPinNumber, 1001);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnChangeAccountPinForInVCustomerIDVAccNoVOldPinVNewPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int oldPinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.changeAccountPin(20, accountNo, oldPinNumber, 1001);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnChangeAccountPinForVCustomerIDInVAccNoVOldPinVNewPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int oldPinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.changeAccountPin(customerId, 20, oldPinNumber, 1001);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void testOnChangeAccountPinForVCustomerIDInVAccNoInVOldPinVNewPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int oldPinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.changeAccountPin(customerId, accountNo, -20, 1001);
	}
	
	@Test(expected=InvalidPinNumberException.class)
	public void testOnChangeAccountPinForVCustomerIDInVAccNoInVOldPinInVNewPin() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, InvalidPinNumberException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		int oldPinNumber=bankingServices.generateNewPin(customerId, accountNo);
		bankingServices.changeAccountPin(customerId, accountNo, oldPinNumber,-20);
	}
			
	@Test
	public void testOnAccountStatusForValidCustomerIDValidAccountNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.accountStatus(customerId, accountNo);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnAccountStatusForInValidCustomerIDValidAccountNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.accountStatus(-300, accountNo);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnAccountStatusForValidCustomerIDInValidAccountNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException, AccountBlockedException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.accountStatus(customerId,- 250);
	}
	
	@Test
	public void testonGetAccountAllTransactionForVCustomerIdVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getAccountAllTransaction(customerId, accountNo);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testonGetAccountAllTransactionForInVCustomerIdVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getAccountAllTransaction(20, accountNo);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testonGetAccountAllTransactionForVCustomerIdInVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getAccountAllTransaction(customerId, 20);
	}
	
	@Test
	public void testOnGetAccountDetailsForVCustomerIDVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		assertEquals(BankingDAOServicesImpl.customers.get(customerId).getAccounts().get(accountNo),bankingServices.getAccountDetails(customerId, accountNo));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnGetAccountDetailsForInVCustomerIDVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getAccountDetails(20, accountNo);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnGetAccountDetailsForVCustomerIDInVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getAccountDetails(customerId, -10);
	}
	
	@Test
	public void testOnGetCustomerAllAccountDetailsForVCustomerID() throws BankingServicesDownException, CustomerNotFoundException, InvalidAmountException, InvalidAccountTypeException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getcustomerAllAccountDetails(customerId);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnGetCustomerAllAccountDetailsForInVCustomerID() throws BankingServicesDownException, CustomerNotFoundException, InvalidAmountException, InvalidAccountTypeException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getcustomerAllAccountDetails(20);
	}
	
	@Test
	public void testOnGetCustomerDetailsForVCustomerID() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		assertEquals(BankingDAOServicesImpl.customers.get(customerId),bankingServices.getCustomerDetails(customerId));
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnGetCustomerDetailsForInVCustomerID() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException {
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.getCustomerDetails(20);
	}
	
	@Test
	public void testOnCloseAccountForVCustomerIDVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException{
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.closeAccount(customerId, accountNo);
	}
	
	@Test(expected=CustomerNotFoundException.class)
	public void testOnCloseAccountForInVCustomerIDVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException{
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.closeAccount(20, accountNo);
	}
	
	@Test(expected=AccountNotFoundException.class)
	public void testOnCloseAccountForVCustomerIDInVAccNo() throws BankingServicesDownException, InvalidAmountException, CustomerNotFoundException, InvalidAccountTypeException, AccountNotFoundException{
		int customerId=bankingServices.acceptCustomerDetails("abhi", "ch", "abhi@abcd.com", "a1", "hyd", "tel",123456, "pune", "mhr",147852);
		long accountNo=bankingServices.openAccount(customerId,"Savings", 10000);
		bankingServices.closeAccount(customerId, 10);
	}
	
	@After
	public void  tearDownMockData() {
		BankingDAOServicesImpl.customers.clear();
		BankingUtility.CUSTOMER_ID_COUNTER=111;
		BankingUtility.ACCOUNT_ID_COUNTER=1;
		BankingUtility.TRANSACTION_ID_COUNTER=1;		
	}
	
	@AfterClass
	public static void tearDownTestEnv() {
		bankingServices=null;
		bankingDAOServices=null;
	}
}
