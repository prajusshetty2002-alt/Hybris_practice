/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.mapping.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.SAPOrderAttachmentData;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPOrderAttachmentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPOrderAttachmentsWsDTO;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.webservicescommons.dto.SortWsDTO;
import de.hybris.platform.webservicescommons.mapping.FieldSelectionStrategy;
import de.hybris.platform.webservicescommons.mapping.config.FieldMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderAttachmentSortMapperTest
{
	private static final String NOT_MAPPED_FIELD = "invalidField:asc";
	private static final String SAP_ORDER_ATTACHMENT_FIELD_MAPPER_BEAN = "sapOrderAttachmentFieldMapper";
	private static final String INPUT_FIELD_ASC = "mappedField:asc";
	private static final String MAPPED_FIELD = "mappedField";
	private static final String ORIGINAL_FIELD = "originalField";
	private static final String ORIGINAL_FIELD_ASC = "originalField:asc";
	private static final String ATTACHMENT_ID = "1";


	@Spy
	@InjectMocks
	private OrderAttachmentSortMapper sapOrderAttachmentSortMapper;

	@Mock
	private FieldMapper fieldMapper;

	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private MappingContext context;
	@Mock
	FieldSelectionStrategy fieldSelectionStrategy;
	@Mock
	private MapperFacade mapperFacade;

	@Before
	public void initialize()
	{
		sapOrderAttachmentSortMapper.setApplicationContext(applicationContext);
		when(fieldSelectionStrategy.shouldMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
	}

	@Test
	public void testMapSortWithBlankInput()
	{
		final String result = sapOrderAttachmentSortMapper.mapSort(StringUtils.EMPTY);
		assertEquals(StringUtils.EMPTY, result);
	}

	@Test
	public void testMapSortWithValidInputAndMapping()
	{
		final Map<String, String> fieldMappings = new HashMap<>();
		fieldMappings.put(ORIGINAL_FIELD, MAPPED_FIELD);
		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);

		final String result = sapOrderAttachmentSortMapper.mapSort(INPUT_FIELD_ASC);

		assertEquals(ORIGINAL_FIELD_ASC, result);
	}

	@Test
	public void testMapSortWithValidInputAndNoMapping()
	{
		final Map<String, String> fieldMappings = new HashMap<>();
		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);

		final String input = INPUT_FIELD_ASC;
		final String result = sapOrderAttachmentSortMapper.mapSort(input);

		assertEquals(INPUT_FIELD_ASC, result);
	}

	@Test
	public void testMapSortWithValidInputAndNoSortingDirection()
	{
		final Map<String, String> fieldMappings = new HashMap<>();
		fieldMappings.put(ORIGINAL_FIELD, MAPPED_FIELD);
		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);

		final String input = MAPPED_FIELD;
		final String result = sapOrderAttachmentSortMapper.mapSort(input);

		assertEquals(ORIGINAL_FIELD, result);
	}

	@Test
	public void testMapSortWithValidInputAndMappingButNoSortInput()
	{
		final Map<String, String> fieldMappings = new HashMap<>();
		fieldMappings.put(ORIGINAL_FIELD, MAPPED_FIELD);
		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);


		final String input = NOT_MAPPED_FIELD;
		final String result = sapOrderAttachmentSortMapper.mapSort(input);

		assertEquals(NOT_MAPPED_FIELD, result);
	}

	@Test
	public void getSapOrderAttachmentFiledMappingsShouldReturnMappings()
	{
		final Map<String, String> fieldMappings = new HashMap<>();
		fieldMappings.put(MAPPED_FIELD, ORIGINAL_FIELD);
		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);

		final Map<String, String> result = sapOrderAttachmentSortMapper.getOrderAttachmentFiledMappings();
		assertEquals(fieldMappings, result);
	}


	@Test
	public void mapAtoBShouldMapAttachments()
	{
		final SAPOrderAttachmentData sapSapOrderAttachmentData = new SAPOrderAttachmentData();
		sapSapOrderAttachmentData.setSapAttachmentId(ATTACHMENT_ID);
		final SearchPageData searchPageData = new SearchPageData();
		searchPageData.setResults(Collections.singletonList(sapSapOrderAttachmentData));
		final SAPOrderAttachmentsWsDTO sapOrderAttachmentsWsDTO = new SAPOrderAttachmentsWsDTO();
		final SAPOrderAttachmentWsDTO sapOrderAttachmentWsDTO = new SAPOrderAttachmentWsDTO();
		sapOrderAttachmentWsDTO.setSapAttachmentId(ATTACHMENT_ID);
		sapOrderAttachmentsWsDTO.setSapAttachments(Collections.singletonList(sapOrderAttachmentWsDTO));
		when(mapperFacade.mapAsList(Mockito.anyList(), Mockito.any(), Mockito.any(MappingContext.class)))
				.thenReturn(Collections.singletonList(sapOrderAttachmentWsDTO));
		sapOrderAttachmentSortMapper.mapAtoB(searchPageData, sapOrderAttachmentsWsDTO, context);

		assertNotNull(sapOrderAttachmentsWsDTO.getSapAttachments());
		assertEquals(ATTACHMENT_ID, sapOrderAttachmentsWsDTO.getSapAttachments().get(0).getSapAttachmentId());
	}

	@Test
	public void mapAtoBShouldUpdateSorts()
	{
		final SAPOrderAttachmentsWsDTO sapOrderAttachmentsWsDTO = new SAPOrderAttachmentsWsDTO();
		final SortWsDTO sortWsDTO = new SortWsDTO();
		sortWsDTO.setCode(MAPPED_FIELD);
		sapOrderAttachmentsWsDTO.setSorts(Arrays.asList(sortWsDTO));
		final SearchPageData searchPageData = new SearchPageData();
		searchPageData.setResults(null);
		final Map<String, String> fieldMappings = new HashMap<>();
		fieldMappings.put(MAPPED_FIELD, ORIGINAL_FIELD);

		sapOrderAttachmentSortMapper.setApplicationContext(mockedApplicationContext(fieldMapper));
		when(fieldMapper.getFieldMapping()).thenReturn(fieldMappings);

		sapOrderAttachmentSortMapper.mapAtoB(searchPageData, sapOrderAttachmentsWsDTO, context);
		assertNotNull(sapOrderAttachmentsWsDTO.getSorts());
		assertEquals(ORIGINAL_FIELD, sapOrderAttachmentsWsDTO.getSorts().get(0).getCode());

	}

	private ApplicationContext mockedApplicationContext(final FieldMapper fieldMapper)
	{
		when(applicationContext.getBean(SAP_ORDER_ATTACHMENT_FIELD_MAPPER_BEAN, FieldMapper.class)).thenReturn(fieldMapper);
		return applicationContext;
	}

}
