//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.ActualPrescriptionForm;
//import com.mycompany.isp490_gr3.model.ActualPrescriptionMedicine;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DAOActualPrescriptionTest {
//    private DAOActualPrescription daoActualPrescription;
//
//    @BeforeEach
//    public void setUp() {
//        daoActualPrescription = new DAOActualPrescription();
//    }
//
//    @Test
//    public void testAddForm() {
//        ActualPrescriptionForm form = new ActualPrescriptionForm();
//        // TODO: set các trường cần thiết cho form, ví dụ:
//        form.setMedicalRecordId("MR000001");
//        form.setPatientId(1);
//        form.setDoctorId(1);
//        form.setFormName("Đơn thuốc test");
//        form.setCreatedBy("test");
//        form.setUpdatedBy("test");
//        List<ActualPrescriptionMedicine> medicines = new ArrayList<>();
//        // Có thể thêm thuốc mẫu nếu cần
//        boolean result = false;
//        try {
//            result = daoActualPrescription.addForm(form, medicines);
//        } catch (Exception e) {
//            // Có thể fail nếu dữ liệu không hợp lệ
//        }
//        Assertions.assertTrue(result, "Thêm đơn thuốc thất bại (cần dữ liệu test phù hợp)");
//    }
//}
