/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.ApiMessageData;
import de.hybris.platform.commerceservices.service.data.ApiMessageKind;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.util.Config;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


/**
 * Test suite for {@link PlaceOrderCartValidator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PlaceOrderCartValidatorTest
{
	private static final String MIN_QTY_VIOLATION_MESSAGE_CODE = "below_min_quantity";
	@Mock
	private CartData cartData;
	private PlaceOrderCartValidator validator;

	@Before
	public void setUp() {
		validator = new PlaceOrderCartValidator();
	}

	@Test
	public void testValidateCartQuantityViolationWhenToggleProductQuantityLimitIsTrue() {
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CommerceServicesConstants.TOGGLE_PRODUCT_QUANTITY_LIMIT, false)).thenReturn(true);
			when(cartData.getPaymentInfo()).thenReturn(mock(CCPaymentInfoData.class));
			when(cartData.isCalculated()).thenReturn(true);
			when(cartData.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
			ApiMessageData violationDetailData = new ApiMessageData();
			violationDetailData.setKind(ApiMessageKind.WARNING);
			violationDetailData.setCode(MIN_QTY_VIOLATION_MESSAGE_CODE);
			when(cartData.get_messages()).thenReturn(Collections.singletonList(violationDetailData));
			Errors errors = new BeanPropertyBindingResult(cartData, "cartData");
			validator.validate(cartData, errors);
			assertTrue(errors.hasErrors());
			assertEquals("cart.violateQuantityLimit", errors.getAllErrors().get(0).getCode());
		}
	}

	@Test
	public void testValidateCartWithNullDataWhenToggleProductQuantityLimitIsTrue() {
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CommerceServicesConstants.TOGGLE_PRODUCT_QUANTITY_LIMIT, false)).thenReturn(true);
			when(cartData.getPaymentInfo()).thenReturn(mock(CCPaymentInfoData.class));
			when(cartData.isCalculated()).thenReturn(true);
			when(cartData.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
			ApiMessageData violationDetailData = new ApiMessageData();
			violationDetailData.setKind(ApiMessageKind.WARNING);
			violationDetailData.setCode(MIN_QTY_VIOLATION_MESSAGE_CODE);
			when(cartData.get_messages()).thenReturn(Collections.singletonList(null));
			Errors errors = new BeanPropertyBindingResult(cartData, "cartData");
			validator.validate(cartData, errors);
			assertTrue(errors.hasErrors());
		}
	}

	@Test
	public void testValidationWithoutErrorsWhenToggleProductQuantityLimitIsTrue() {
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CommerceServicesConstants.TOGGLE_PRODUCT_QUANTITY_LIMIT, false)).thenReturn(true);
			when(cartData.getPaymentInfo()).thenReturn(mock(CCPaymentInfoData.class));
			when(cartData.isCalculated()).thenReturn(true);
			when(cartData.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
			Errors errors = new BeanPropertyBindingResult(cartData, "cartData");
			validator.validate(cartData, errors);
			assertFalse(errors.hasErrors());
		}
	}

	@Test
	public void testValidateCartQuantityViolationWhenToggleProductQuantityLimitIsFalse() {
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CommerceServicesConstants.TOGGLE_PRODUCT_QUANTITY_LIMIT, false)).thenReturn(false);
			when(cartData.getPaymentInfo()).thenReturn(mock(CCPaymentInfoData.class));
			when(cartData.isCalculated()).thenReturn(true);
			when(cartData.getDeliveryMode()).thenReturn(mock(DeliveryModeData.class));
			Errors errors = new BeanPropertyBindingResult(cartData, "cartData");
			validator.validate(cartData, errors);
			assertFalse(errors.hasErrors());
		}
	}
}