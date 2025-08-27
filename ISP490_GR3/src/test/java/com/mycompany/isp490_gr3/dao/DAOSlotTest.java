//package com.mycompany.isp490_gr3.dao;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//public class DAOSlotTest {
//    private DAOSlot daoSlot;
//
//    @BeforeEach
//    public void setUp() {
//        daoSlot = new DAOSlot();
//    }
//
//    @Test
//    public void testHandleAddSlot() throws Exception {
//        // TODO: Thay đổi các tham số cho phù hợp với dữ liệu test
//        int doctorId = 1;
//        LocalDate date = LocalDate.now().plusDays(1);
//        LocalTime start = LocalTime.of(10, 0);
//        int duration = 30;
//        int maxPatients = 4;
//        boolean result = false;
//        try {
//            result = daoSlot.insertSlotWithValidation(doctorId, date, start, duration, maxPatients);
//        } catch (Exception e) {
//            // Có thể fail nếu dữ liệu không hợp lệ
//        }
//        Assertions.assertTrue(result, "Thêm slot thất bại (cần dữ liệu test phù hợp)");
//    }
//}
