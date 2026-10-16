package com.mycompany.mycustomextension.service.impl;

import com.mycompany.mycustomextension.dao.DailyOrderReportDao;
import com.mycompany.mycustomextension.service.DailyOrderReportService;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;

public class DefaultDailyOrderReportService
        implements DailyOrderReportService
{
    private DailyOrderReportDao dailyOrderReportDao;

    @Override
    public List<OrderModel> getOrdersForDate(
            final Date startDate,
            final Date endDate)
    {
        return dailyOrderReportDao.findOrdersForReport(
                startDate,
                endDate);
    }

    public void setDailyOrderReportDao(
            final DailyOrderReportDao dailyOrderReportDao)
    {
        this.dailyOrderReportDao = dailyOrderReportDao;
    }
}