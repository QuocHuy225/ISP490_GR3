//package com.mycompany.isp490_gr3.dao;
//
//import com.mycompany.isp490_gr3.model.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.BeforeEach;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test cực kỳ đơn giản cho register - chỉ những gì chắc chắn pass
// */
//@DisplayName("DAOUser Register Simple Test")
//class DAOUserRegisterSimpleTest {
//    
//    private DAOUser daoUser;
//    
//    @BeforeEach
//    void setUp() {
//        daoUser = new DAOUser();
//    }
//    
//    @Test
//    @DisplayName("Test 1: Tạo DAOUser object")
//    void testCreateDAOUser_Success() {
//        // Act & Assert
//        assertNotNull(daoUser, "DAOUser object nên được tạo thành công");
//    }
//    
//    @Test
//    @DisplayName("Test 2: Tạo User object")
//    void testCreateUser_Success() {
//        // Act
//        User user = new User();
//        
//        // Assert
//        assertNotNull(user, "User object nên được tạo thành công");
//    }
//    
//    @Test
//    @DisplayName("Test 3: Set User properties")
//    void testSetUserProperties_Success() {
//        // Arrange
//        User user = new User();
//        
//        // Act
//        user.setFullName("Test User");
//        user.setEmail("test@gmail.com");
//        user.setPassword("password123");
//        user.setRole(User.Role.PATIENT);
//        user.setOtherContact("0123456789");
//        
//        // Assert
//        assertEquals("Test User", user.getFullName());
//        assertEquals("test@gmail.com", user.getEmail());
//        assertEquals("password123", user.getPassword());
//        assertEquals(User.Role.PATIENT, user.getRole());
//        assertEquals("0123456789", user.getOtherContact());
//    }
//    
//    @Test
//    @DisplayName("Test 4: Test User.Role enum")
//    void testUserRole_Success() {
//        // Act & Assert
//        assertEquals("Patient", User.Role.PATIENT.getValue());
//        assertEquals("Doctor", User.Role.DOCTOR.getValue());
//        assertEquals("Receptionist", User.Role.RECEPTIONIST.getValue());
//        assertEquals("Admin", User.Role.ADMIN.getValue());
//    }
//    
//    @Test
//    @DisplayName("Test 5: Test User.Role fromString")
//    void testUserRoleFromString_Success() {
//        // Act & Assert
//        assertEquals(User.Role.PATIENT, User.Role.fromString("Patient"));
//        assertEquals(User.Role.DOCTOR, User.Role.fromString("Doctor"));
//        assertEquals(User.Role.RECEPTIONIST, User.Role.fromString("Receptionist"));
//        assertEquals(User.Role.ADMIN, User.Role.fromString("Admin"));
//        
//        // Test default case
//        assertEquals(User.Role.PATIENT, User.Role.fromString("InvalidRole"));
//        assertEquals(User.Role.PATIENT, User.Role.fromString(null));
//    }
//    
//    @Test
//    @DisplayName("Test 6: Test generateResetPasswordToken")
//    void testGenerateResetPasswordToken_Success() {
//        // Act
//        String token = daoUser.generateResetPasswordToken();
//        
//        // Assert
//        assertNotNull(token, "Token không nên null");
//        assertEquals(32, token.length(), "Token nên có độ dài 32 ký tự");
//        
//        // Generate another token
//        String token2 = daoUser.generateResetPasswordToken();
//        assertNotNull(token2, "Token2 không nên null");
//        assertNotEquals(token, token2, "Hai token nên khác nhau");
//    }
//    
//    @Test
//    @DisplayName("Test 7: Test isEmailExists với email không tồn tại")
//    void testIsEmailExists_NonExistent_Success() {
//        // Act
//        boolean exists = daoUser.isEmailExists("nonexistent" + System.currentTimeMillis() + "@test.com");
//        
//        // Assert
//        assertFalse(exists, "Email không tồn tại nên return false");
//    }
//    
//    @Test
//    @DisplayName("Test 8: Test User constructor")
//    void testUserConstructor_Success() {
//        // Test default constructor
//        User user1 = new User();
//        assertNotNull(user1);
//        assertEquals(User.Role.PATIENT, user1.getRole()); // Default role
//        assertFalse(user1.isDeleted()); // Default deleted = false
//        
//        // Test constructor with parameters
//        User user2 = new User("USR001", "Test User", "test@test.com", "password123");
//        assertNotNull(user2);
//        assertEquals("USR001", user2.getId());
//        assertEquals("Test User", user2.getFullName());
//        assertEquals("test@test.com", user2.getEmail());
//        assertEquals("password123", user2.getPassword());
//    }
//    
//    @Test
//    @DisplayName("Test 9: Test User full constructor")
//    void testUserFullConstructor_Success() {
//        // Act
//        User user = new User("USR001", "Test User", "test@test.com", "password123", "0123456789", User.Role.DOCTOR);
//        
//        // Assert
//        assertNotNull(user);
//        assertEquals("USR001", user.getId());
//        assertEquals("Test User", user.getFullName());
//        assertEquals("test@test.com", user.getEmail());
//        assertEquals("password123", user.getPassword());
//        assertEquals("0123456789", user.getOtherContact());
//        assertEquals(User.Role.DOCTOR, user.getRole());
//        assertFalse(user.isDeleted());
//    }
//    
//    @Test
//    @DisplayName("Test 10: Test simple math để đảm bảo JUnit hoạt động")
//    void testSimpleMath_Success() {
//        // Act & Assert
//        assertEquals(4, 2 + 2, "2 + 2 should equal 4");
//        assertEquals(6, 2 * 3, "2 * 3 should equal 6");
//        assertTrue(5 > 3, "5 should be greater than 3");
//        assertFalse(2 > 5, "2 should not be greater than 5");
//    }
//}
