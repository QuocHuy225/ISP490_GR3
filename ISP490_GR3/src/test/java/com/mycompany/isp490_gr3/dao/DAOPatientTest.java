//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.Patient;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.sql.Date;
//
//public class DAOPatientTest {
//    private DAOPatient daoPatient;
//
//    @BeforeEach
//    public void setUp() {
//        daoPatient = new DAOPatient();
//    }
//
//    @Test
//    public void testAddPatient() {
//        Patient patient = new Patient();
//        patient.setFullName("Test Patient");
//        patient.setGender(1);
//        patient.setDob(Date.valueOf("2000-01-01"));
//        patient.setPhone("0123456789");
//        patient.setCccd("123456789");
//        patient.setAddress("Test Address");
//        boolean result = false;
//        try {
//            result = daoPatient.addPatient(patient);
//        } catch (Exception e) {
//            // Có thể fail nếu dữ liệu không hợp lệ
//        }
//        Assertions.assertTrue(result, "Thêm bệnh nhân thất bại (cần dữ liệu test phù hợp)");
//    }
//}
