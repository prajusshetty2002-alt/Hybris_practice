/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.v2.controller;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPSavedCartRequestWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import com.mycompany.mycustomwebservices.requestfrom.RequestFromValueSetter;
import com.mycompany.mycustomwebservices.skipfield.SkipSaveCartResultFieldValueSetter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mycompany.mycustomwebservices.v2.controller.SaveCartController.TOGGLE_SAVECARTCONTROLLER_CLONESAVEDCART_HOOK_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SaveCartControllerTest
{
	@InjectMocks
	private SaveCartController saveCartController;
	@Mock
	private SaveCartFacade saveCartFacade;
	@Mock
	private DataMapper dataMapper;
	@Mock
	private SkipSaveCartResultFieldValueSetter skipSaveCartResultFieldValueSetter;
	@Mock
	private RequestFromValueSetter requestFromValueSetter;

	@Test
	public void testSaveCart() throws CommerceSaveCartException
	{
		String cartId = "cartId";
		SAPSavedCartRequestWsDTO savedCart = new SAPSavedCartRequestWsDTO();
		savedCart.setName("mycart");
		savedCart.setDescription("mycart description");
		saveCartController.doCartSave(cartId, savedCart, "cartId");
		verify(saveCartFacade).saveCart(any());
		verify(requestFromValueSetter).setRequestFrom(any());
	}

	@Test
	public void testCopySavedCart_hook_enabled() throws CommerceSaveCartException
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(TOGGLE_SAVECARTCONTROLLER_CLONESAVEDCART_HOOK_ENABLED, false)).thenReturn(true);
			String cartId = "cartId";
			SAPSavedCartRequestWsDTO savedCart = new SAPSavedCartRequestWsDTO();
			savedCart.setName("mycart");
			savedCart.setDescription("mycart description");
			saveCartController.copySavedCart(cartId, savedCart, "cartId");
			ArgumentCaptor<CommerceSaveCartParameterData> captor = ArgumentCaptor.forClass(CommerceSaveCartParameterData.class);
			verify(saveCartFacade, times(1)).cloneSavedCart(captor.capture());
			verify(requestFromValueSetter).setRequestFrom(any());

			CommerceSaveCartParameterData parameterData = captor.getValue();
			Assert.assertNotNull(parameterData);
			Assert.assertTrue(parameterData.isEnableHooks());
		}
	}

	@Test
	public void testCopySavedCart_hook_disabled() throws CommerceSaveCartException
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(TOGGLE_SAVECARTCONTROLLER_CLONESAVEDCART_HOOK_ENABLED, false)).thenReturn(false);
			String cartId = "cartId";
			SAPSavedCartRequestWsDTO savedCart = new SAPSavedCartRequestWsDTO();
			savedCart.setName("mycart");
			savedCart.setDescription("mycart description");
			saveCartController.copySavedCart(cartId, savedCart, "cartId");
			ArgumentCaptor<CommerceSaveCartParameterData> captor = ArgumentCaptor.forClass(CommerceSaveCartParameterData.class);
			verify(saveCartFacade, times(1)).cloneSavedCart(captor.capture());
			verify(requestFromValueSetter).setRequestFrom(any());

			CommerceSaveCartParameterData parameterData = captor.getValue();
			Assert.assertNotNull(parameterData);
			Assert.assertFalse(parameterData.isEnableHooks());
		}
	}


	@Test
	public void testClonesavedcart_hook_enabled() throws CommerceSaveCartException
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(TOGGLE_SAVECARTCONTROLLER_CLONESAVEDCART_HOOK_ENABLED, false)).thenReturn(true);
			String cartId = "cartId";
			saveCartController.doCartClone(cartId, "name","description", "cartId");

			ArgumentCaptor<CommerceSaveCartParameterData> captor = ArgumentCaptor.forClass(CommerceSaveCartParameterData.class);
			verify(saveCartFacade, times(1)).cloneSavedCart(captor.capture());
			verify(requestFromValueSetter).setRequestFrom(any());

			CommerceSaveCartParameterData parameterData = captor.getValue();
			Assert.assertNotNull(parameterData);
			Assert.assertTrue(parameterData.isEnableHooks());
			Assert.assertEquals("name", parameterData.getName());
			Assert.assertEquals("description", parameterData.getDescription());
			Assert.assertEquals(cartId, parameterData.getCartId());
		}
	}

	@Test
	public void testClonesavedcart_hook_disabled() throws CommerceSaveCartException
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(TOGGLE_SAVECARTCONTROLLER_CLONESAVEDCART_HOOK_ENABLED, false)).thenReturn(false);
			String cartId = "cartId";
			saveCartController.doCartClone(cartId, "name","description", "cartId");
			ArgumentCaptor<CommerceSaveCartParameterData> captor = ArgumentCaptor.forClass(CommerceSaveCartParameterData.class);
			verify(saveCartFacade, times(1)).cloneSavedCart(captor.capture());
			verify(requestFromValueSetter).setRequestFrom(any());

			CommerceSaveCartParameterData parameterData = captor.getValue();
			Assert.assertNotNull(parameterData);
			Assert.assertFalse(parameterData.isEnableHooks());
			Assert.assertEquals("name", parameterData.getName());
			Assert.assertEquals("description", parameterData.getDescription());
			Assert.assertEquals(cartId, parameterData.getCartId());
		}
	}
}
