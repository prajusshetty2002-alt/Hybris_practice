/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.v2.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.SapPaymentOptionData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import com.mycompany.mycustomwebservices.request.support.impl.PaymentProviderRequestSupportedStrategy;
import com.mycompany.mycustomwebservices.skipfield.SkipCartFieldValueSetter;
import de.hybris.platform.commercewebservicescommons.dto.order.EntryGroupWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPPaymentOptionRequestWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link CartPaymentsController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartPaymentsControllerTest
{
    private static final String FIELDS = "DEFAULt";
    @Mock
    private CartFacade cartFacade;
    @Mock
    private PaymentProviderRequestSupportedStrategy paymentProviderRequestSupportedStrategy;
    @Mock
    private SkipCartFieldValueSetter skipCartFieldValueSetter;
    @Mock
    private DataMapper dataMapper;
    @InjectMocks
    private CartPaymentsController cartPaymentsController;

    @Test
    public void testUpdateSapPaymentOption() throws CommerceCartModificationException
    {
        final CartWsDTO wsDTO = new CartWsDTO();
        wsDTO.setEntryGroups(List.of(new EntryGroupWsDTO()));
        given(dataMapper.map(any(), eq(CartWsDTO.class), anyString())).willReturn(wsDTO);
        SapPaymentOptionData sapPaymentOptionData = new SapPaymentOptionData();
        given(dataMapper.map(any(), eq(SapPaymentOptionData.class))).willReturn(sapPaymentOptionData);
        SAPPaymentOptionRequestWsDTO sapPaymentOptionRequestWsDTO = new SAPPaymentOptionRequestWsDTO();
        cartPaymentsController.updateSapPaymentOption(FIELDS, sapPaymentOptionRequestWsDTO);
        verify(skipCartFieldValueSetter).setValue(FIELDS);
        verify(cartFacade).updateCartSapPaymentOption(sapPaymentOptionData);
    }

}