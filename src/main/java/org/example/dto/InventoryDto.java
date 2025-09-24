package org.example.dto;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.example.api.InventoryApi;
import org.example.api.ProductApi;
import org.example.flow.InventoryFlow;
import org.example.flow.ProductFlow;
import org.example.models.data.DaySalesReportData;
import org.example.models.data.InventoryData;
import org.example.models.data.Response;
import org.example.models.form.InventoryForm;
import org.example.pojo.InventoryPojo;
import org.example.utils.BulkResponse;
import org.example.utils.BulkUploadResult;
import org.example.utils.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


@Component

public class InventoryDto {

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private InventoryFlow inventoryFlow;
    @Autowired
    private ProductFlow productFlow;
    @Autowired
    private ProductApi productApi;

    public void add(InventoryForm inventoryForm) throws ApiException {
        UtilMethods.normalizeInventoryForm(inventoryForm);
        UtilMethods.validateInventoryForm(inventoryForm);
        InventoryPojo inventoryPojo = convert(inventoryForm);
        inventoryApi.add(inventoryPojo);
    }


    public List<Response<InventoryForm>> bulkUpload(List<InventoryForm> listOfInventoryForm) throws ApiException {

        List<Response<InventoryForm>> responseList = new ArrayList<>();
        for(InventoryForm inventoryForm: listOfInventoryForm){
            Response<InventoryForm> response = new Response<>();
            response.setData(inventoryForm);
            response.setMessage("success");
            try{
                add(inventoryForm);
            }catch (ApiException e){
                response.setMessage(e.getMessage());
            }
            responseList.add(response);
        }
        return responseList;
    }

    public List<InventoryData> getAll() throws ApiException{
        List<InventoryPojo> inventoryPojoList = inventoryApi.getAll();
        List<InventoryData> inventoryDataList = new ArrayList<>();
        for(InventoryPojo inventoryPojo: inventoryPojoList){
            inventoryDataList.add(convert(inventoryPojo));
        }
        return inventoryDataList;
    }

    public InventoryData getByProductId(Integer productId) throws ApiException{
        InventoryPojo inventoryPojo = inventoryApi.getByProductId(productId);
        if(Objects.isNull(inventoryPojo)){
            throw new ApiException("Out of stock");
        }
        return convert(inventoryPojo);
    }

    public InventoryData getByBarcode(String barcode) throws ApiException {
        Integer productId = inventoryFlow.getProductByBarcode(barcode).getId();
        return getByProductId(productId);
    }

    private InventoryPojo convert(InventoryForm inventoryForm) throws ApiException{
        Integer productId = inventoryFlow.getProductByBarcode(inventoryForm.getBarcode()).getId();

        return DtoHelper.convertInventoryFormToInventoryPojo(inventoryForm, productId);
    }

    private InventoryData convert(InventoryPojo inventoryPojo) throws ApiException{
        String barcode = inventoryFlow.getProductByProductId(inventoryPojo.getProductId()).getBarcode();
        return DtoHelper.convertInventoryPojoToInventoryData(inventoryPojo, barcode);
    }
    public String exportInventory() throws ApiException {

        List<InventoryData> listOfInventoryData = getAll();

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
                    .append("Inventory")
                    .append("</fo:block>")
                    .append("<fo:block space-before=\"5pt\"/>")
                    .append("<fo:table table-layout=\"fixed\" width=\"100%\">")
                    .append("<fo:table-column column-width=\"50%\"/>")
                    .append("<fo:table-column column-width=\"50%\"/>")
                    .append("<fo:table-body>")
                    .append("<fo:table-row background-color=\"#f0f0f0\">")
                    .append("<fo:table-cell><fo:block>Barcode</fo:block></fo:table-cell>")
                    .append("<fo:table-cell><fo:block>Quantity</fo:block></fo:table-cell>")
                    .append("</fo:table-row>");

            // 2️⃣ Add rows from report data
            for (InventoryData data : listOfInventoryData) {
                foContent.append("<fo:table-row>")
                        .append("<fo:table-cell><fo:block>").append(data.getBarcode()).append("</fo:block></fo:table-cell>")
                        .append("<fo:table-cell><fo:block>").append(data.getQuantity()).append("</fo:block></fo:table-cell>")
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
