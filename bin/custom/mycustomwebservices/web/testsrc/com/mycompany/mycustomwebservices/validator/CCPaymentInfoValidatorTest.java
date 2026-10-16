/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CCPaymentInfoValidatorTest
{
	@InjectMocks
	private CCPaymentInfoValidator validator = new CCPaymentInfoValidator();

	@Mock
	private Validator paymentAddressValidator;

	@Mock
	private Errors errors;

	@Test
	public void shouldRejectWhenStartDateAfterExpirationDate() {
		CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		paymentInfo.setStartMonth("12");
		paymentInfo.setStartYear("2023");
		paymentInfo.setExpiryMonth("11");
		paymentInfo.setExpiryYear("2023");

		validator.validate(paymentInfo, errors);

		verify(errors).rejectValue("startMonth", "payment.startDate.invalid");
	}

	@Test
	public void shouldRejectWhenRequiredFieldsAreEmpty() {
		CCPaymentInfoData paymentInfo = new CCPaymentInfoData();

		validator.validate(paymentInfo, errors);

		verify(errors, times(1)).rejectValue("accountHolderName", "field.required", null, null);
		verify(errors, times(1)).rejectValue("cardType", "field.required", null, null);
		verify(errors, times(1)).rejectValue("cardNumber", "field.required", null, null);
		verify(errors, times(1)).rejectValue("expiryMonth", "field.required", null, null);
		verify(errors, times(1)).rejectValue("expiryYear", "field.required", null, null);
	}

	@Test
	public void shouldValidatePaymentAddress() {
		CCPaymentInfoData paymentInfo = new CCPaymentInfoData();
		paymentInfo.setStartMonth("01");
		paymentInfo.setStartYear("2023");
		paymentInfo.setExpiryMonth("2");
		paymentInfo.setExpiryYear("2023");
		paymentInfo.setAccountHolderName("John Doe");
		paymentInfo.setCardType("VISA");
		paymentInfo.setCardNumber("4111111111111111");

		validator.validate(paymentInfo, errors);

		verify(paymentAddressValidator).validate(paymentInfo, errors);
	}

}
