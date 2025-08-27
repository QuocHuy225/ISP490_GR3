//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.service.DoctorScheduleService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class DoctorScheduleServiceTest {
//    private DoctorScheduleService doctorScheduleService;
//
//    @BeforeEach
//    public void setUp() {
//        doctorScheduleService = new DoctorScheduleService();
//    }
//
//    @Test
//    public void testCreateSchedule() {
//        // TODO: Thay đổi các tham số cho phù hợp với dữ liệu test
//        int doctorId = 1;
//        String workDate = "2025-07-31";
//        boolean isActive = true;
//        String result = doctorScheduleService.createSchedule(doctorId, workDate, isActive);
//        Assertions.assertNotNull(result, "Tạo lịch làm việc thất bại (cần dữ liệu test phù hợp)");
//    }
//}
