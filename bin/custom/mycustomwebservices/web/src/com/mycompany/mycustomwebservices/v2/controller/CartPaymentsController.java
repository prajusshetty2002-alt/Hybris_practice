/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.v2.controller;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.SapPaymentOptionData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPPaymentOptionRequestWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import com.mycompany.mycustomwebservices.exceptions.InvalidPaymentInfoException;
import com.mycompany.mycustomwebservices.exceptions.NoCheckoutCartException;
import com.mycompany.mycustomwebservices.exceptions.UnsupportedRequestException;
import com.mycompany.mycustomwebservices.skipfield.SkipCartFieldValueSetter;
import com.mycompany.mycustomwebservices.request.support.impl.PaymentProviderRequestSupportedStrategy;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Tag(name = "Cart Payments")
public class CartPaymentsController extends BaseCommerceController
{
	private static final String PAYMENT_MAPPING = "accountHolderName,cardNumber,cardType,cardTypeData(code),expiryMonth,expiryYear,issueNumber,startMonth,startYear,subscriptionId,defaultPaymentInfo,saved,billingAddress(titleCode,firstName,lastName,line1,line2,town,postalCode,country(isocode),region(isocode),defaultAddress,phone)";

	@Resource(name = "paymentProviderRequestSupportedStrategy")
	private PaymentProviderRequestSupportedStrategy paymentProviderRequestSupportedStrategy;


	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "skipCartFieldValueSetter")
	private SkipCartFieldValueSetter skipCartFieldValueSetter;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PutMapping(value = "/{cartId}/paymentOption")
	@ResponseBody
	@Operation(operationId = "updateSapPaymentOption", summary = "Set the payment option for the cart.", description = "Specifies which option the consumer selected to make the payment for the cart.")
	@Parameter(name = "baseSiteId", description = "The base site identifier.", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "userId", description = "User identifier or one of the literals : 'current' for currently authenticated user", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "cartId", description = "Cart identifier: cart code for logged-in user", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public CartWsDTO updateSapPaymentOption(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestBody SAPPaymentOptionRequestWsDTO sapPaymentOptionRequestWsDTO) throws CommerceCartModificationException
	{
		skipCartFieldValueSetter.setValue(fields);
		SapPaymentOptionData sapPaymentOptionData = getDataMapper().map(sapPaymentOptionRequestWsDTO, SapPaymentOptionData.class);
		cartFacade.updateCartSapPaymentOption(sapPaymentOptionData);
		return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PostMapping(value = "/{cartId}/paymentdetails", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createCartPaymentDetails", summary = "Creates details of a new credit card payment for the cart.", description = "Creates the details of a new credit card payment and assigns it to the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO createCartPaymentDetails(@Parameter(description = """
			Request body parameter that contains details such as the name on the card (accountHolderName), the card number (cardNumber), the card type (cardType.code), \
			the month of the expiry date (expiryMonth), the year of the expiry date (expiryYear), whether the payment details should be saved (saved), whether the payment details \
			should be set as default (defaultPaymentInfo), and the billing address (billingAddress.firstName, billingAddress.lastName, billingAddress.titleCode, billingAddress.country.isocode, \
			billingAddress.line1, billingAddress.line2, billingAddress.town, billingAddress.postalCode, billingAddress.region.isocode)
			
			The DTO is in XML or .json format.
			""", required = true) @RequestBody final PaymentDetailsWsDTO paymentDetails,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws InvalidPaymentInfoException, NoCheckoutCartException, UnsupportedRequestException
	{
		paymentProviderRequestSupportedStrategy.checkIfRequestSupported("addPaymentDetails");
		validatePayment(paymentDetails);
		CCPaymentInfoData paymentInfoData = getDataMapper().map(paymentDetails, CCPaymentInfoData.class, PAYMENT_MAPPING);
		paymentInfoData = addPaymentDetailsInternal(paymentInfoData).getPaymentInfo();
		return getDataMapper().map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PutMapping(value = "/{cartId}/paymentdetails")
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "replaceCartPaymentDetails", summary = "Sets credit card payment details for the cart.", description = "Sets credit card payment details for the specified cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartPaymentDetails(
			@Parameter(description = "Payment details identifier.", required = true) @RequestParam final String paymentDetailsId)
			throws InvalidPaymentInfoException
	{
		setPaymentDetailsInternal(paymentDetailsId);
	}

	protected void validatePayment(final PaymentDetailsWsDTO paymentDetails) throws NoCheckoutCartException
	{
		if (!getCheckoutFacade().hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}
		validate(paymentDetails, "paymentDetails", getPaymentDetailsDTOValidator());
	}
}
