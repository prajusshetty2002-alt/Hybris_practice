package com.mycompany.mycustomwebservices.v2.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.SAPOrderAttachmentContentDto;
import de.hybris.platform.commercefacades.order.data.SAPOrderAttachmentData;
import de.hybris.platform.commercefacades.order.orderattachment.OrderAttachmentFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPOrderAttachmentsWsDTO;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import com.mycompany.mycustomwebservices.mapping.mappers.OrderAttachmentSortMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderAttachmentsControllerTest
{

	private static final String ORDER_CODE = "testOrder123";
	private static final String ATTACHMENT_ID = "attachment456";
	private static final String MIME_TYPE = "application/pdf";

	@Mock
	private OrderAttachmentFacade orderAttachmentFacade;

	@Mock
	private WebPaginationUtils webPaginationUtils;

	@Mock
	private OrderAttachmentSortMapper orderAttachmentSortMapper;

	@Mock
	private DataMapper dataMapper;

	@InjectMocks
	private OrderAttachmentsController controller;


	@Test
	public void testGetUserOrderAttachments()
	{
		// Given
		final SearchPageData<SAPOrderAttachmentData> searchPageData = new SearchPageData<>();
		searchPageData.setResults(Collections.emptyList());
		given(orderAttachmentFacade.getAttachmentsList(anyString(), any())).willReturn(searchPageData);
		given(dataMapper.map(any(), any(), anyString())).willReturn(new SAPOrderAttachmentsWsDTO());

		// When
		final SAPOrderAttachmentsWsDTO result = controller.getUserOrderAttachments(ORDER_CODE, 0, 20, "sapFileName", "basic");

		// Then
		assertNotNull(result);
		verify(orderAttachmentFacade).getAttachmentsList(anyString(), any());
		verify(dataMapper).map(any(), any(), anyString());
		verify(orderAttachmentSortMapper).mapSort(any());
	}

	@Test
	public void testGetUserOrderAttachmentContentSuccess()
	{
		// Given
		final InputStream inputStream = new ByteArrayInputStream("Test data".getBytes());
		final SAPOrderAttachmentContentDto contentDto = new SAPOrderAttachmentContentDto();
		contentDto.setSapInputStream(inputStream);
		contentDto.setSapMimeType(MIME_TYPE);

		given(orderAttachmentFacade.getAttachmentContent(anyString(), anyString())).willReturn(contentDto);

		// When
		final ResponseEntity<byte[]> response = controller.getUserOrderAttachmentContent(ORDER_CODE,
				ATTACHMENT_ID);

		// Then
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(MediaType.valueOf(MIME_TYPE), response.getHeaders().getContentType());
	}

	@Test(expected = NotFoundException.class)
	public void testGetUserOrderAttachmentContentNotFound()
	{
		// Given
		given(orderAttachmentFacade.getAttachmentContent(anyString(), anyString())).willReturn(null);

		// When
		controller.getUserOrderAttachmentContent(ORDER_CODE, ATTACHMENT_ID);
	}
}
