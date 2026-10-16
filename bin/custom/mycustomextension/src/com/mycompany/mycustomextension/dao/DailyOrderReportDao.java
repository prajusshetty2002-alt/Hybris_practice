package com.mycompany.mycustomextension.dao;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.Date;
import java.util.List;

public interface DailyOrderReportDao
{
    List<OrderModel> findOrdersForReport(Date startDate, Date endDate);
}