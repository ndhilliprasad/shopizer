package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
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
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;

import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

public class InstamojoPayment implements PaymentModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstamojoPayment.class);

	@Inject
	private PricingService pricingService;

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
		// TODO Auto-generated method stub

		PaymentOrder order = buildPaymentOrder(store, customer, amount);
		
		Map<String,String> keys = configuration.getIntegrationKeys();
		
		Instamojo api = null;

		try {
			// gets the reference to the instamojo api
			api = InstamojoImpl.getApi(keys.get("clientId"), keys.get("clientSecret"), keys.get("apiEndpoint"), keys.get("oauthEndpoint"));
			CreatePaymentOrderResponse createPaymentOrderResponse = api.createNewPaymentOrder(order);
		} catch (Exception e) {
			LOGGER.error("Failed to process the payment through innstamojo", e);
			throw new IntegrationException(e);
		}

		return null;
	}

	private PaymentOrder buildPaymentOrder(MerchantStore store, Customer customer, BigDecimal amount) {
		PaymentOrder order = new PaymentOrder();

		StringBuilder builder = new StringBuilder(customer.getBilling().getFirstName()).append(' ')
				.append(customer.getBilling().getLastName());

		order.setName(builder.toString());
		order.setEmail(customer.getEmailAddress());
		order.setPhone(customer.getBilling().getTelephone());
		order.setCurrency(store.getCurrency().getCode());
		order.setAmount(amount.doubleValue());
		order.setDescription("This is a test transaction.");

		StringBuilder returnUrl = new StringBuilder().append(coreConfiguration.getProperty("SHOP_SCHEME", "http"))
				.append("://").append(store.getDomainName()).append("/")
				.append(coreConfiguration.getProperty("CONTEXT_PATH", "sm-shop")).append(Constants.SHOP_URI)
				.append("/instamojo/checkout").append(coreConfiguration.getProperty("URL_EXTENSION", ".html"));

		order.setRedirectUrl(returnUrl.toString());
		
		String transactioId = UUID.randomUUID().toString();
		
		order.setTransactionId(transactioId);
		return order;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction, Order order,
			BigDecimal amount, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

}
