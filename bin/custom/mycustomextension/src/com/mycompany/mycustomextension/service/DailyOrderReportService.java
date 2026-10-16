package com.mycompany.mycustomextension.service;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;

public interface DailyOrderReportService
{
    List<OrderModel> getOrdersForDate(Date startDate, Date endDate);
}