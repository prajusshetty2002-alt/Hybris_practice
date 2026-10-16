/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;

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
public class PaymentDetailsDTOValidatorTest
{
	@InjectMocks
	private PaymentDetailsDTOValidator validator = new PaymentDetailsDTOValidator();

	@Mock
	private Validator paymentAddressValidator;

	@Mock
	private Errors errors;

	@Test
	public void shouldRejectWhenStartDateAfterExpirationDate() {
		PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();
		paymentDetails.setStartMonth("12");
		paymentDetails.setStartYear("2023");
		paymentDetails.setExpiryMonth("11");
		paymentDetails.setExpiryYear("2023");

		validator.validate(paymentDetails, errors);

		verify(errors).rejectValue("startMonth", "payment.startDate.invalid");
	}

	@Test
	public void shouldRejectWhenRequiredFieldsAreEmpty() {
		PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();

		validator.validate(paymentDetails, errors);

		verify(errors, times(1)).rejectValue("accountHolderName", "field.required", null, null);
		verify(errors, times(1)).rejectValue("cardType.code", "field.required", null, null);
		verify(errors, times(1)).rejectValue("cardNumber", "field.required", null, null);
		verify(errors, times(1)).rejectValue("expiryMonth", "field.required", null, null);
		verify(errors, times(1)).rejectValue("expiryYear", "field.required", null, null);
	}

	@Test
	public void shouldValidatePaymentAddress() {
		PaymentDetailsWsDTO paymentDetails = new PaymentDetailsWsDTO();
		paymentDetails.setStartMonth("11");
		paymentDetails.setStartYear("2023");
		paymentDetails.setExpiryMonth("12");
		paymentDetails.setExpiryYear("2023");
		paymentDetails.setAccountHolderName("John Doe");
		CardTypeWsDTO cardTypeWsDTO = new CardTypeWsDTO();
		cardTypeWsDTO.setCode("VISA");
		paymentDetails.setCardType(cardTypeWsDTO);
		paymentDetails.setCardNumber("4111111111111111");

		validator.validate(paymentDetails, errors);

		verify(paymentAddressValidator).validate(paymentDetails, errors);
	}
}
