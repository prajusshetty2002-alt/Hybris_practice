/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomwebservices.v2.controller;

import de.hybris.platform.commercefacades.order.data.SAPOrderAttachmentContentDto;
import de.hybris.platform.commercefacades.order.data.SAPOrderAttachmentData;
import de.hybris.platform.commercefacades.order.orderattachment.OrderAttachmentFacade;
import de.hybris.platform.commercewebservicescommons.dto.order.SAPOrderAttachmentsWsDTO;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import com.mycompany.mycustomwebservices.mapping.mappers.OrderAttachmentSortMapper;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RequestMapping(value = "/{baseSiteId}/users/{userId}/orders")
@Controller
@ApiBaseSiteIdAndUserIdParam
@Tag(name = "Orders")
public class OrderAttachmentsController extends BaseCommerceController
{

	@Resource(name = "sapOrderAttachmentFacade")
	private OrderAttachmentFacade orderAttachmentFacade;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;


	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "sapOrderAttachmentsSortMapper")
	private OrderAttachmentSortMapper sapOrderAttachmentSortMapper;


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@GetMapping(value = "/{code}/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheControl(directive = CacheControlDirective.PRIVATE)
	@ResponseBody
	@Operation(operationId = "getUserOrderAttachments", summary = "Get attachments for an order.", description = "Returns attachments based on a specific order code. The response contains "
			+ "list of attachment information.")
	@ApiResponse(responseCode = "200", description = "List of attachments of an order")
	@ApiBaseSiteIdAndUserIdParam
	public SAPOrderAttachmentsWsDTO getUserOrderAttachments(
			@Parameter(description = "Order Code", required = true) @PathVariable final String code,
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = "0") final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = "20") final int pageSize,
			@Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (StringUtils.isNotBlank(sort))
		{
			sort = getOrderAttachmentSortMapper().mapSort(sort);
		}

		final SearchPageData<SAPOrderAttachmentData> searchPageDataInput = webPaginationUtils.buildSearchPageData(sort, currentPage,
				pageSize, true);
		final SearchPageData<SAPOrderAttachmentData> attachmentsList = orderAttachmentFacade.getAttachmentsList(code,
				searchPageDataInput);
		return dataMapper.map(attachmentsList, SAPOrderAttachmentsWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@GetMapping(value = "/{code}/attachments/{attachmentId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	@Operation(operationId = "getUserOrderAttachmentBinary", summary = "Get binary attachment of an order", description = "Get content attachment of an order. "
			+ "The response will be a binary file with an appropriate `Content-Type` header.")
	@ApiBaseSiteIdAndUserIdParam
	@ApiResponse(responseCode = "200", description = "OK", content =
	{ @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, array = @ArraySchema(schema = @Schema(type = "string", format = "byte"))) })
	public ResponseEntity<byte[]> getUserOrderAttachmentContent(
			@Parameter(description = "Order Code", required = true) @PathVariable final String code,
			@Parameter(description = "Attachment ID", required = true) @PathVariable final String attachmentId)
	{

		final SAPOrderAttachmentContentDto attachmentContent = orderAttachmentFacade.getAttachmentContent(code, attachmentId);
		if (attachmentContent != null && attachmentContent.getSapInputStream() != null)
		{
			try (InputStream inputStream = attachmentContent.getSapInputStream())
			{
				//note: using streaming types like InputStreamResource for response body with combination 
				// with some content types like 'application/json' causes issues with Jaxb2HttpMessageConverter,
				// that's why byte[] is used
				byte[] bytes = inputStream.readAllBytes();

				return ResponseEntity.ok().contentLength(bytes.length)
						.contentType(MediaType.parseMediaType(attachmentContent.getSapMimeType())).body(bytes);
			}
			catch (IOException e)
			{
				throw new SystemException("Error reading attachment content", e);
			}
		}
		throw new NotFoundException("Attachment with id = %s of order %s is not found.".formatted(attachmentId, code),
				"The attachment with the given id does not have any data", attachmentId);
	}

	public OrderAttachmentSortMapper getOrderAttachmentSortMapper()
	{
		return sapOrderAttachmentSortMapper;
	}

	public void setOrderAttachmentSortMapper(final OrderAttachmentSortMapper sapOrderAttachmentSortMapper)
	{
		this.sapOrderAttachmentSortMapper = sapOrderAttachmentSortMapper;
	}
}
