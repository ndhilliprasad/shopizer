package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.CreatePaymentOrderResponse;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.ModuleConfig;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;

public class InstamojoPayment implements PaymentModule {

	private static final String STATUS_FAILED = "FAILED";

	private static final String TRANSACTION_STATUS = "STATUS";

	public static final String PAYMENT_URL = "PAYMENT_URL";

	private static final String STATUS_SUCCESS = "successful";

	private static final String PAYMENT_ID = "PAYMENT_ID";

	public static final String TRANSACTION_ID = "transaction_id";

	private static final Logger LOGGER = LoggerFactory.getLogger(InstamojoPayment.class);

	@Inject
	private CoreConfiguration coreConfiguration;

	@Override
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store)
			throws IntegrationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction initTransaction(MerchantStore store, Customer customer, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {

		return null;
	}

	public Transaction initInstamojoTransaction(MerchantStore store, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {
		// TODO Auto-generated method stub

		String transactioId = UUID.randomUUID().toString();
		CreatePaymentOrderResponse createPaymentOrderResponse;

		try {
			Instamojo api = getInstamojoApi(configuration, module);
			PaymentOrder order = buildPaymentOrder(transactioId, store, payment, amount);
			createPaymentOrderResponse = api.createNewPaymentOrder(order);
		} catch (Exception e) {
			LOGGER.error("Failed to process the payment through innstamojo", e);
			throw new IntegrationException(e);
		}

		Transaction transaction = new Transaction();

		transaction.setAmount(amount);
		transaction.setTransactionDate(new Date());
		transaction.setTransactionType(TransactionType.INIT);
		transaction.setPaymentType(PaymentType.INSTAMOJO);
		transaction.getTransactionDetails().put(PAYMENT_URL,
				createPaymentOrderResponse.getPaymentOptions().getPaymentUrl());
		transaction.getTransactionDetails().put(TRANSACTION_ID, transactioId);

		return transaction;
	}

	public Transaction getPaymentDetails(String transactionId, IntegrationConfiguration configuration,
			IntegrationModule module) throws IntegrationException {
		
		ModuleConfig moduleConfig = module.getModuleConfigs().get(configuration.getEnvironment());

		PaymentOrderDetailsResponse paymentOrderDetailsResponse;

		try {
			// gets the reference to the instamojo api
			Instamojo api = getInstamojoApi(configuration, module);
			paymentOrderDetailsResponse = api.getPaymentOrderDetailsByTransactionId(transactionId);
		} catch (Exception e) {
			LOGGER.error("Failed to process the payment through instamojo", e);
			throw new IntegrationException(e);
		}

		Transaction transaction = new Transaction();

		transaction.setTransactionDate(new Date());
		transaction.setTransactionType(TransactionType.AUTHORIZECAPTURE);
		transaction.setPaymentType(PaymentType.INSTAMOJO);
		transaction.setAmount(new BigDecimal(paymentOrderDetailsResponse.getAmount()));

		Map<String, String> transactionDetails = new HashMap<>();
		transactionDetails.put(TRANSACTION_ID, transactionId);

		com.instamojo.wrapper.model.Payment[] payments = paymentOrderDetailsResponse.getPayments();

		if (payments == null || payments.length == 0) {
			throw new IntegrationException("Unable to find the transaction status");
		}

		if (!STATUS_SUCCESS.equalsIgnoreCase(payments[0].getStatus())) {
			throw new IntegrationException("Payment was not Succesful");
		}

		transactionDetails.put(TRANSACTION_STATUS, STATUS_SUCCESS);
		transactionDetails.put(PAYMENT_ID, payments[0].getId());
		transaction.setTransactionDetails(transactionDetails);

		return transaction;
	}

	private Instamojo getInstamojoApi(IntegrationConfiguration configuration, IntegrationModule module)
			throws ConnectionException {

		Map<String, String> keys = configuration.getIntegrationKeys();
		ModuleConfig moduleConfig = module.getModuleConfigs().get(configuration.getEnvironment());

		return InstamojoImpl.getApi(keys.get("clientId"), keys.get("clientSecret"), moduleConfig.getConfig1(),
				moduleConfig.getConfig2());
	}

	@Override
	public Transaction authorize(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction capture(MerchantStore store, Customer customer, Order order, Transaction capturableTransaction,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction authorizeAndCapture(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		
		com.salesmanager.core.model.payments.InstamojoPayment instamojoPayment = (com.salesmanager.core.model.payments.InstamojoPayment) payment;
		return getPaymentDetails(instamojoPayment.getTransactionId(), configuration, module);
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction, Order order,
			BigDecimal amount, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	private PaymentOrder buildPaymentOrder(String transactionId, MerchantStore store, Payment payment,
			BigDecimal amount) {
		PaymentOrder order = new PaymentOrder();

		Map<String, String> paymentMetaData = payment.getPaymentMetaData();

		order.setName(paymentMetaData.get("name"));
		order.setEmail(paymentMetaData.get("email"));
		order.setPhone(paymentMetaData.get("phone"));
		order.setCurrency(payment.getCurrency().getCode());
		order.setAmount(amount.doubleValue());
		order.setDescription("This is a test transaction.");

		StringBuilder returnUrl = new StringBuilder().append(coreConfiguration.getProperty("SHOP_SCHEME", "http"))
				.append("://").append(store.getDomainName())
				.append(coreConfiguration.getProperty("CONTEXT_PATH", "sm-shop")).append(Constants.SHOP_URI)
				.append("/instamojo/checkout").append(coreConfiguration.getProperty("URL_EXTENSION", ".html"));

		order.setRedirectUrl(returnUrl.toString());

		order.setTransactionId(transactionId);
		return order;
	}

}
