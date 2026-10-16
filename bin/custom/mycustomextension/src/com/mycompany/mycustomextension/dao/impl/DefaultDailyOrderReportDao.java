package com.mycompany.mycustomextension.dao.impl;

import com.mycompany.mycustomextension.dao.DailyOrderReportDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DefaultDailyOrderReportDao implements DailyOrderReportDao
{
    private FlexibleSearchService flexibleSearchService;

    private static final String FIND_ORDERS_QUERY =
            "SELECT {o.pk} " +
                    "FROM {Order AS o} " +
                    "WHERE {o.creationtime} >= ?startDate " +
                    "AND {o.creationtime} < ?endDate " +
                    "ORDER BY {o.creationtime} ASC";

    @Override
    public List<OrderModel> findOrdersForReport(final Date startDate, final Date endDate)
    {
        final FlexibleSearchQuery query =
                new FlexibleSearchQuery(FIND_ORDERS_QUERY);

        query.addQueryParameter("startDate", startDate);
        query.addQueryParameter("endDate", endDate);

        query.setResultClassList(
                Collections.singletonList(OrderModel.class)
        );

        final SearchResult<OrderModel> searchResult =
                flexibleSearchService.search(query);

        return searchResult.getResult();
    }

    public FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(
            final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}