//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.MedicalRecord;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.sql.Date;
//
//public class DAOMedicalRecordTest {
//    private DAOMedicalRecord daoMedicalRecord;
//
//    @BeforeEach
//    public void setUp() {
//        daoMedicalRecord = new DAOMedicalRecord();
//    }
//
//    @Test
//    public void testAddMedicalRecord() {
//        MedicalRecord record = new MedicalRecord();
//        // TODO: set các trường cần thiết cho record, ví dụ:
//        record.setPatientId(1);
//        record.setDoctorId(1);
//        record.setCreatedBy("test");
//        record.setUpdatedBy("test");
//        
//        boolean result = false;
//        try {
//            result = daoMedicalRecord.addMedicalRecord(record);
//        } catch (Exception e) {
//            // Có thể fail nếu dữ liệu không hợp lệ
//        }
//        Assertions.assertTrue(result, "Thêm hồ sơ bệnh án thất bại (cần dữ liệu test phù hợp)");
//    }
//}
