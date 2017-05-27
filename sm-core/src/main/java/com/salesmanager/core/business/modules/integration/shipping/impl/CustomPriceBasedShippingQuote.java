package com.salesmanager.core.business.modules.integration.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.system.CustomIntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuoteModule;

public class CustomPriceBasedShippingQuote implements ShippingQuoteModule {

	public final static String MODULE_CODE = "priceBased";
	private final static String PRICE_BASED = "PRICE_BASED";

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomPriceBasedShippingQuote.class);

	@Inject
	private ProductPriceUtils productPriceUtils;

	@Override
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store)
			throws IntegrationException {
		// TODO Auto-generated method stub

	}

	@Override
	public CustomIntegrationConfiguration getCustomModuleConfiguration(MerchantStore store)
			throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ShippingOption> getShippingQuotes(ShippingQuote quote, List<PackageDetails> packages,
			BigDecimal orderTotal, Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale) throws IntegrationException {

		ShippingOption shippingOption = null;

		try {
			String shippingCost = configuration.getIntegrationKeys().get("shippingCost");
			BigDecimal optionPrice = null;

			try {
				optionPrice = new BigDecimal(shippingCost);
			} catch (NumberFormatException exception) {
				LOGGER.error("Unable to convert the priceBased shipping cost : " + shippingCost + " to Number",
						exception);
				optionPrice = new BigDecimal(0);
			}

			shippingOption = new ShippingOption();
			shippingOption.setOptionCode(new StringBuilder().append(PRICE_BASED).toString());
			shippingOption.setOptionId(new StringBuilder().append(PRICE_BASED).toString());

			shippingOption.setOptionPrice(optionPrice);
			shippingOption.setOptionPriceText(productPriceUtils.getStoreFormatedAmountWithCurrency(store, optionPrice));
		} catch (Exception e) {
			throw new IntegrationException(e);
		}

		List<ShippingOption> shippingOptions = new ArrayList<>();
		shippingOptions.add(shippingOption);

		return shippingOptions;
	}

}
