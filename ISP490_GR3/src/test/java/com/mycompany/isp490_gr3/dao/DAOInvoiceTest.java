//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.Invoice;
//import com.mycompany.isp490_gr3.model.InvoiceItem;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.util.ArrayList;
//import java.util.List;
//import java.math.BigDecimal;
//
//public class DAOInvoiceTest {
//    private DAOInvoice daoInvoice;
//
//    @BeforeEach
//    public void setUp() {
//        daoInvoice = new DAOInvoice();
//    }
//
//    @Test
//    public void testAddInvoice() {
//        Invoice invoice = new Invoice();
//        // TODO: set các trường cần thiết cho invoice, ví dụ:
//        invoice.setMedicalRecordId("MR000001");
//        invoice.setPatientId(1);
//        invoice.setDoctorId(1);
//        invoice.setTotalServiceAmount(BigDecimal.valueOf(100000));
//        invoice.setTotalSupplyAmount(BigDecimal.valueOf(50000));
//        invoice.setTotalAmount(BigDecimal.valueOf(150000));
//        invoice.setDiscountAmount(BigDecimal.ZERO);
//        invoice.setFinalAmount(BigDecimal.valueOf(150000));
//        invoice.setCreatedBy("test");
//        invoice.setUpdatedBy("test");
//        List<InvoiceItem> items = new ArrayList<>();
//        // Có thể thêm item mẫu nếu cần
//        boolean result = false;
//        try {
//            result = daoInvoice.addInvoice(invoice, items);
//        } catch (Exception e) {
//            // Có thể fail nếu dữ liệu không hợp lệ
//        }
//        Assertions.assertTrue(result, "Thêm hóa đơn thất bại (cần dữ liệu test phù hợp)");
//    }
//}
