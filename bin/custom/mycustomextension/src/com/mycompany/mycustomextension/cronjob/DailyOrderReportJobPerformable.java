package com.mycompany.mycustomextension.cronjob;

import com.mycompany.mycustomextension.service.DailyOrderReportService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import com.mycompany.mycustomextension.model.DailyOrderReportCronJobModel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.DataInputStream;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;




public class DailyOrderReportJobPerformable extends AbstractJobPerformable<DailyOrderReportCronJobModel>
{
    private static final Logger LOG =
            Logger.getLogger(DailyOrderReportJobPerformable.class);

    private DailyOrderReportService dailyOrderReportService;
    private EmailService emailService;

    @Override
    public PerformResult perform(final DailyOrderReportCronJobModel cronJob)
    {
        LOG.info("Starting Daily Order Report CronJob: "
                + cronJob.getCode());

        try
        {
            // ---------------------------------------------------------
            // 1. Calculate today's date range
            // ---------------------------------------------------------

            final Date startOfDay = getStartOfToday();
            final Date startOfNextDay = getStartOfNextDay();

            LOG.info("Fetching orders between "
                    + startOfDay
                    + " and "
                    + startOfNextDay);

            // ---------------------------------------------------------
            // 2. Fetch today's orders
            // ---------------------------------------------------------

            final List<OrderModel> orders = dailyOrderReportService.getOrdersForDate(startOfDay, startOfNextDay);

            LOG.info("Number of orders found: " + orders.size());

            // ---------------------------------------------------------
            // 3. Generate Excel
            // ---------------------------------------------------------

            final byte[] excelData =
                    generateExcel(orders);

            // ---------------------------------------------------------
            // 4. Save Excel file
            // ---------------------------------------------------------

            final String fileName =
                    "Daily_Orders_Report_" + getDateString()
                            + "_" + System.currentTimeMillis() + ".xlsx";

            final File outputFile =
                    saveExcelFile(
                            cronJob.getOutputDirectory(),
                            fileName,
                            excelData);

            LOG.info("Excel file created: "
                    + outputFile.getAbsolutePath());

            // ---------------------------------------------------------
            // 5. Get recipient email
            // ---------------------------------------------------------

            final String recipientEmail =
                    cronJob.getRecipientEmail();

            if (recipientEmail == null
                    || recipientEmail.trim().isEmpty())
            {
                LOG.error("Recipient email is not configured.");

                return new PerformResult(
                        CronJobResult.FAILURE,
                        CronJobStatus.FINISHED);
            }

            // ---------------------------------------------------------
            // 6. Send email
            // ---------------------------------------------------------

            sendEmail(
                    recipientEmail,
                    excelData,
                    fileName);

            LOG.info("Daily Order Report email sent successfully.");

            return new PerformResult(
                    CronJobResult.SUCCESS,
                    CronJobStatus.FINISHED);
        }
        catch (Exception e)
        {
            LOG.error(
                    "Error while executing Daily Order Report CronJob",
                    e);

            return new PerformResult(
                    CronJobResult.ERROR,
                    CronJobStatus.FINISHED);
        }
    }

    /**
     * Returns today's start time.
     */
    protected Date getStartOfToday()
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Returns tomorrow's start time.
     */
    protected Date getStartOfNextDay()
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    /**
     * Creates XLSX file in memory.
     */
    protected byte[] generateExcel(
            final List<OrderModel> orders)
            throws Exception
    {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream())
        {
            final Sheet sheet =
                    workbook.createSheet("Daily Orders");

            // Header
            final Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("Order Code");

            header.createCell(1)
                    .setCellValue("Customer Email ID");

            // Data
            int rowNumber = 1;

            for (final OrderModel order : orders)
            {
                final Row row =
                        sheet.createRow(rowNumber++);

                row.createCell(0)
                        .setCellValue(
                                order.getCode() != null
                                        ? order.getCode()
                                        : "");

                row.createCell(1)
                        .setCellValue(
                                getCustomerEmail(order));
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * Gets customer email.
     * This assumes customer UID contains the email.
     */
    protected String getCustomerEmail(
            final OrderModel order)
    {
        if (order.getUser() instanceof CustomerModel customer)
        {

            return customer.getUid() != null
                    ? customer.getUid()
                    : "";
        }

        return "";
    }

    /**
     * Saves XLSX to configured directory.
     */
    protected File saveExcelFile(
            final String directoryPath,
            final String fileName,
            final byte[] excelData)
            throws Exception
    {
        final File directory =
                new File(directoryPath);

        if (!directory.exists())
        {
            if (!directory.mkdirs())
            {
                throw new IllegalStateException(
                        "Unable to create directory: "
                                + directoryPath);
            }
        }

        final File outputFile =
                new File(directory, fileName);

        try (FileOutputStream outputStream =
                     new FileOutputStream(outputFile))
        {
            outputStream.write(excelData);
        }

        return outputFile;
    }

    /**
     * Sends email with Excel attachment.
     */
    protected void sendEmail(
            final String recipientEmail,
            final byte[] excelData,
            final String fileName)
            throws Exception
    {
        final EmailAddressModel toAddress =
                emailService.getOrCreateEmailAddressForEmail(
                        recipientEmail,
                        recipientEmail);


        final String senderEmail =
                "prajwalstalight@gmail.com";

        final EmailAddressModel fromAddress =
                emailService.getOrCreateEmailAddressForEmail(
                        senderEmail,
                        "Order Report");

        final EmailAttachmentModel attachment =
                emailService.createEmailAttachment(
                        new DataInputStream(new ByteArrayInputStream(excelData)),
                        fileName,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        final EmailMessageModel emailMessage =
                emailService.createEmailMessage(
                        Collections.singletonList(toAddress),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        fromAddress,
                        senderEmail,
                        "Daily Order Report",
                        "Please find the attached Daily Order Report.",
                        Collections.singletonList(attachment));

        emailService.send(emailMessage);
    }

    protected String getDateString()
    {
        final Calendar calendar =
                Calendar.getInstance();

        return String.format(
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setDailyOrderReportService(
            final DailyOrderReportService dailyOrderReportService)
    {
        this.dailyOrderReportService =
                dailyOrderReportService;
    }

    public void setEmailService(
            final EmailService emailService)
    {
        this.emailService = emailService;
    }
}