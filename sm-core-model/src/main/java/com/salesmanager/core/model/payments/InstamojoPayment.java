package com.salesmanager.core.model.payments;

public class InstamojoPayment extends Payment {

	private String transactionId;
	
	public InstamojoPayment(){
		setPaymentType(PaymentType.INSTAMOJO);
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
