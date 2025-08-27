//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.Medicine;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.math.BigDecimal;
//
//public class DAOWarehouseTest {
//    private DAOWarehouse daoWarehouse;
//
//    @BeforeEach
//    public void setUp() {
//        daoWarehouse = new DAOWarehouse();
//    }
//
//    @Test
//    public void testAddMedicine() {
//        Medicine medicine = new Medicine();
//        medicine.setMedicineName("Test Medicine");
//        medicine.setUnitOfMeasure("viên");
//        medicine.setUnitPrice(new BigDecimal("10000"));
//        medicine.setStockQuantity(100);
//        boolean result = daoWarehouse.addMedicine(medicine);
//        Assertions.assertTrue(result, "Thêm thuốc thất bại");
//    }
//}
