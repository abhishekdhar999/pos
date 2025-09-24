package org.example.dto;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.example.api.SalesReportApi;
import org.example.flow.OrderFlow;
import org.example.flow.ReportsFlow;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.SalesReportData;
import org.example.models.form.DaySalesReportsForm;
import org.example.models.form.ExportFilterDailyReports;
import org.example.models.form.SalesReportFilterForm;
import org.example.pojo.DaySalesReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.example.dto.DtoHelper.*;

@Component
public class ReportsDto {
    @Autowired
    private OrderFlow orderFlow;
    @Autowired
    private ReportsFlow reportsFlow;
    @Autowired
    private SalesReportApi salesReportApi;

    public List<DaySalesReportData> getDaysSalesReports(DaySalesReportsForm form) throws ApiException{

        List<DaySalesReportPojo> daySalesReportsPojo = salesReportApi.getDaySalesReports(form);
       return convertDaySalesReportPojoToDaySalesReportData(daySalesReportsPojo);

    }

   public void generateDayReports() throws ApiException {
       ZonedDateTime dateTime = ZonedDateTime.now();
       ZonedDateTime startDate = dateTime.minusDays(1).with(LocalTime.of(0,0,0));
       ZonedDateTime endDate = dateTime.minusDays(1).with(LocalTime.of(23,59,59));
       DaySalesReportsForm daySalesReportsForm =  createDaySalesReportForm(startDate,endDate);
      reportsFlow.generateDayReport(daySalesReportsForm);
   }

   public String getDaySalesReportsBetweenDates(ExportFilterDailyReports form, HttpServletResponse response) throws ApiException, IOException {
       ZonedDateTime startDate = ZonedDateTime.parse(form.getStartDate());
       ZonedDateTime endDate = ZonedDateTime.parse(form.getEndDate());
     List<DaySalesReportPojo> daySalesReportPojos = salesReportApi.getDaySalesReportsBetweenDates(startDate,endDate);

     if(Objects.isNull(daySalesReportPojos)){
         throw new ApiException("error generating the sales report data");
     }
     List<DaySalesReportData> daySalesReportDataList =  convertDaySalesReportPojoToDaySalesReportData(daySalesReportPojos);

       try {
           // 1️⃣ Build FO dynamically
           StringBuilder foContent = new StringBuilder();
           foContent.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">")
                   .append("<fo:layout-master-set>")
                   .append("<fo:simple-page-master master-name=\"A4\" ")
                   .append("page-height=\"29.7cm\" page-width=\"24cm\" margin=\"1cm\">")
                   .append("<fo:region-body/>")
                   .append("</fo:simple-page-master>")
                   .append("</fo:layout-master-set>")
                   .append("<fo:page-sequence master-reference=\"A4\">")
                   .append("<fo:flow flow-name=\"xsl-region-body\">")
                   .append("<fo:block font-size=\"12pt\" font-weight=\"bold\" text-align=\"center\">")
                   .append("Day Sales Report")
                   .append("</fo:block>")
                   .append("<fo:block space-before=\"5pt\"/>")
                   .append("<fo:table table-layout=\"fixed\" width=\"100%\">")
                   .append("<fo:table-column column-width=\"40%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-body>")
                   .append("<fo:table-row background-color=\"#f0f0f0\">")
                   .append("<fo:table-cell><fo:block>Date</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Orders</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Items</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Revenue</fo:block></fo:table-cell>")
                   .append("</fo:table-row>");

           // 2️⃣ Add rows from report data
           for (DaySalesReportData data : daySalesReportDataList) {
               foContent.append("<fo:table-row>")
                       .append("<fo:table-cell><fo:block>").append(data.getDate()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getInvoicedOrdersCount()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getInvoicedItemsCount()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getTotalRevenue()).append("</fo:block></fo:table-cell>")
                       .append("</fo:table-row>");
           }

           foContent.append("</fo:table-body>")
                   .append("</fo:table>")
                   .append("</fo:flow>")
                   .append("</fo:page-sequence>")
                   .append("</fo:root>");

           // 3️⃣ Generate PDF in memory
           ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
           FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
           Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutStream);

           // Use FOP transformer, not basic XML transformer
           TransformerFactory factory = TransformerFactory.newInstance();
           Transformer transformer = factory.newTransformer();
           Source src = new StreamSource(new StringReader(foContent.toString()));
           Result res = new SAXResult(fop.getDefaultHandler()); // Use SAXResult with FOP handler
           transformer.transform(src, res);

           // 4️⃣ Convert PDF bytes to Base64 string
           System.out.println("string" + Base64.getEncoder().encodeToString(pdfOutStream.toByteArray()));
           return Base64.getEncoder().encodeToString(pdfOutStream.toByteArray());

       } catch (Exception e) {
           throw new ApiException("Failed to generate PDF", e);
       }
   }
   public List<SalesReportData> getSalesReport(SalesReportFilterForm salesReportFilterForm) throws ApiException {
        normalizeSalesReportFilterForm(salesReportFilterForm);
        return reportsFlow.getSalesReport(salesReportFilterForm);
   }

   public String exportSalesReport(SalesReportFilterForm salesReportFilterForm) throws ApiException {
       normalizeSalesReportFilterForm(salesReportFilterForm);
       List<SalesReportData> listOfSalesReportData = reportsFlow.getSalesReport(salesReportFilterForm);
       try {
           // 1️⃣ Build FO dynamically
           StringBuilder foContent = new StringBuilder();
           foContent.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">")
                   .append("<fo:layout-master-set>")
                   .append("<fo:simple-page-master master-name=\"A4\" ")
                   .append("page-height=\"29.7cm\" page-width=\"25cm\" margin=\"1cm\">")
                   .append("<fo:region-body/>")
                   .append("</fo:simple-page-master>")
                   .append("</fo:layout-master-set>")
                   .append("<fo:page-sequence master-reference=\"A4\">")
                   .append("<fo:flow flow-name=\"xsl-region-body\">")
                   .append("<fo:block font-size=\"12pt\" font-weight=\"bold\" text-align=\"center\">")
                   .append("Sales Report")
                   .append("</fo:block>")
                   .append("<fo:block space-before=\"2pt\"/>")
                   .append("<fo:table table-layout=\"fixed\" width=\"100%\">")
                   .append("<fo:table-column column-width=\"40%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-column column-width=\"20%\"/>")
                   .append("<fo:table-body>")
                   .append("<fo:table-row background-color=\"#f0f0f0\">")
                   .append("<fo:table-cell><fo:block>Barcode</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Client</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Quantity</fo:block></fo:table-cell>")
                   .append("<fo:table-cell><fo:block>Revenue</fo:block></fo:table-cell>")
                   .append("</fo:table-row>");

           // 2️⃣ Add rows from report data
           for (SalesReportData data : listOfSalesReportData) {
               foContent.append("<fo:table-row>")
                       .append("<fo:table-cell><fo:block>").append(data.getProductBarcode()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getClient()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getQuantity()).append("</fo:block></fo:table-cell>")
                       .append("<fo:table-cell><fo:block>").append(data.getRevenue()).append("</fo:block></fo:table-cell>")
                       .append("</fo:table-row>");
           }

           foContent.append("</fo:table-body>")
                   .append("</fo:table>")
                   .append("</fo:flow>")
                   .append("</fo:page-sequence>")
                   .append("</fo:root>");

           // 3️⃣ Generate PDF in memory
           ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
           FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
           Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfOutStream);

           // Use FOP transformer, not basic XML transformer
           TransformerFactory factory = TransformerFactory.newInstance();
           Transformer transformer = factory.newTransformer();
           Source src = new StreamSource(new StringReader(foContent.toString()));
           Result res = new SAXResult(fop.getDefaultHandler()); // Use SAXResult with FOP handler
           transformer.transform(src, res);

           // 4️⃣ Convert PDF bytes to Base64 string
           System.out.println("string" + Base64.getEncoder().encodeToString(pdfOutStream.toByteArray()));
           return Base64.getEncoder().encodeToString(pdfOutStream.toByteArray());

       } catch (Exception e) {
           throw new ApiException("Failed to generate PDF", e);
       }
   }


}
