package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.Invoice;
import com.mycompany.isp490_gr3.model.InvoiceItem;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.MedicalSupply;
import com.mycompany.isp490_gr3.model.Medicine;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for handling Invoice-related database operations.
 * Updated to work with two separate invoice_items tables: invoice_items_one and invoice_items_two
 */
public class DAOInvoice {

    private static final Logger LOGGER = Logger.getLogger(DAOInvoice.class.getName());
    private DAOWarehouse daoWarehouse;
    
    public DAOInvoice() {
        this.daoWarehouse = new DAOWarehouse();
    }

    // ===== INVOICE OPERATIONS =====
    
    public List<Invoice> getInvoicesByMedicalRecord(String medicalRecordId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.*, p.full_name as patient_name, mr.id as medical_record_id " +
                     "FROM invoices i " +
                     "LEFT JOIN patients p ON i.patient_id = p.id " +
                     "LEFT JOIN medical_record mr ON i.medical_record_id = mr.id " +
                     "WHERE i.medical_record_id = ? AND i.isdeleted = 0 ORDER BY i.created_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(extractInvoice(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting invoices by medical record: {0}", e.getMessage());
        }

        return invoices;
    }

    public Invoice getInvoiceById(String invoiceId) {
        String sql = "SELECT i.*, p.full_name as patient_name, mr.id as medical_record_id " +
                     "FROM invoices i " +
                     "LEFT JOIN patients p ON i.patient_id = p.id " +
                     "LEFT JOIN medical_record mr ON i.medical_record_id = mr.id " +
                     "WHERE i.invoice_id = ? AND i.isdeleted = 0";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = extractInvoice(rs);
                    // Load invoice items from both tables
                    invoice.setInvoiceItems(getInvoiceItems(invoiceId));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting invoice by id: {0}", e.getMessage());
        }
        return null;
    }

    public boolean addInvoice(Invoice invoice, List<InvoiceItem> items) {
        if (invoice == null || invoice.getMedicalRecordId() == null) {
            throw new IllegalArgumentException("Invoice data is incomplete.");
        }

        // Check if an invoice already exists for this medical record
        List<Invoice> existingInvoices = getInvoicesByMedicalRecord(invoice.getMedicalRecordId());
        if (!existingInvoices.isEmpty()) {
            throw new IllegalStateException("An invoice already exists for this medical record.");
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Kiểm tra tồn kho trước khi tạo invoice
            if (items != null && !items.isEmpty()) {
                String stockError = validateStockAvailability(items);
                if (stockError != null) {
                    LOGGER.log(Level.WARNING, "Stock validation failed: {0}", stockError);
                    throw new IllegalStateException(stockError);
                }
            }

            // Generate invoice ID
            String invoiceId = generateInvoiceId();
            invoice.setInvoiceId(invoiceId);

            // Insert invoice
            String invoiceSql = "INSERT INTO invoices (invoice_id, medical_record_id, patient_id, doctor_id, " +
                               "total_service_amount, total_supply_amount, total_amount, discount_amount, final_amount, " +
                               "notes, created_by, updated_by, isdeleted) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(invoiceSql)) {
                ps.setString(1, invoiceId);
                ps.setString(2, invoice.getMedicalRecordId());
                ps.setInt(3, invoice.getPatientId());
                ps.setObject(4, invoice.getDoctorId());
                ps.setBigDecimal(5, invoice.getTotalServiceAmount());
                ps.setBigDecimal(6, invoice.getTotalSupplyAmount());
                ps.setBigDecimal(7, invoice.getTotalAmount());
                ps.setBigDecimal(8, invoice.getDiscountAmount());
                ps.setBigDecimal(9, invoice.getFinalAmount());
                ps.setString(10, invoice.getNotes());
                ps.setString(11, invoice.getCreatedBy());
                ps.setString(12, invoice.getUpdatedBy());
                ps.setBoolean(13, false);

                ps.executeUpdate();
            }

            // Insert invoice items vào hai bảng riêng biệt và trừ kho
            if (items != null && !items.isEmpty()) {
                addInvoiceItemsToTwoTables(conn, invoiceId, items);
                if (!updateWarehouseStock(items, false)) {
                    throw new SQLException("Failed to update warehouse stock");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
                }
            }
            
            LOGGER.log(Level.SEVERE, "SQL Error adding invoice: {0}", e.getMessage());
            
            // Re-throw specific database errors for better handling
            if (e.getMessage().contains("doesn't exist") || e.getMessage().contains("Table")) {
                throw new IllegalStateException("Database tables are not up to date. Please run the database update script.", e);
            }
            
            return false;
        } catch (IllegalStateException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
                }
            }
            throw e; // Re-throw to be handled by controller
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
                }
            }
            LOGGER.log(Level.SEVERE, "Unexpected error adding invoice: {0}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection: {0}", e.getMessage());
                }
            }
        }
    }

    public boolean updateInvoice(Invoice invoice, List<InvoiceItem> items) {
        if (invoice == null || invoice.getInvoiceId() == null) {
            throw new IllegalArgumentException("Invoice data is incomplete.");
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Lấy danh sách items cũ để hoàn trả kho
            List<InvoiceItem> oldItems = getInvoiceItems(invoice.getInvoiceId());

            // Kiểm tra tồn kho cho items mới
            if (items != null && !items.isEmpty()) {
                String stockError = validateStockAvailabilityForUpdate(oldItems, items);
                if (stockError != null) {
                    throw new IllegalStateException(stockError);
                }
            }

            // Update invoice
            String invoiceSql = "UPDATE invoices SET " +
                               "total_service_amount = ?, total_supply_amount = ?, total_amount = ?, " +
                               "discount_amount = ?, final_amount = ?, " +
                               "notes = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP " +
                               "WHERE invoice_id = ? AND isdeleted = 0";

            try (PreparedStatement ps = conn.prepareStatement(invoiceSql)) {
                ps.setBigDecimal(1, invoice.getTotalServiceAmount());
                ps.setBigDecimal(2, invoice.getTotalSupplyAmount());
                ps.setBigDecimal(3, invoice.getTotalAmount());
                ps.setBigDecimal(4, invoice.getDiscountAmount());
                ps.setBigDecimal(5, invoice.getFinalAmount());
                ps.setString(6, invoice.getNotes());
                ps.setString(7, invoice.getUpdatedBy());
                ps.setString(8, invoice.getInvoiceId());

                ps.executeUpdate();
            }

            // Hoàn trả kho từ items cũ
            if (oldItems != null && !oldItems.isEmpty()) {
                if (!updateWarehouseStock(oldItems, true)) {
                    throw new SQLException("Failed to restore warehouse stock from old items");
                }
            }

            // Delete existing items from both tables and insert new ones
            deleteInvoiceItemsFromBothTables(conn, invoice.getInvoiceId());
            if (items != null && !items.isEmpty()) {
                addInvoiceItemsToTwoTables(conn, invoice.getInvoiceId(), items);
                // Trừ kho cho items mới
                if (!updateWarehouseStock(items, false)) {
                    throw new SQLException("Failed to update warehouse stock for new items");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException | IllegalStateException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
                }
            }
            LOGGER.log(Level.SEVERE, "Error updating invoice: {0}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection: {0}", e.getMessage());
                }
            }
        }
    }

    // ===== INVOICE ITEMS OPERATIONS =====
    
    public List<InvoiceItem> getInvoiceItems(String invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        
        // Get items from invoice_items_one table (receipt 1)
        String sql1 = "SELECT * FROM invoice_items_one WHERE invoice_id = ? ORDER BY id";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql1)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = extractInvoiceItemFromTable(rs, 1);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting invoice items from table one: {0}", e.getMessage());
        }
        
        // Get items from invoice_items_two table (receipt 2)
        String sql2 = "SELECT * FROM invoice_items_two WHERE invoice_id = ? ORDER BY id";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = extractInvoiceItemFromTable(rs, 2);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting invoice items from table two: {0}", e.getMessage());
        }

        return items;
    }
    
    public List<InvoiceItem> getInvoiceItemsByReceipt(String invoiceId, int receiptNumber) {
        List<InvoiceItem> items = new ArrayList<>();
        String tableName = receiptNumber == 1 ? "invoice_items_one" : "invoice_items_two";
        String sql = "SELECT * FROM " + tableName + " WHERE invoice_id = ? ORDER BY id";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = extractInvoiceItemFromTable(rs, receiptNumber);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting invoice items from table {0}: {1}", 
                      new Object[]{tableName, e.getMessage()});
        }
        
        return items;
    }

    private void addInvoiceItemsToTwoTables(Connection conn, String invoiceId, List<InvoiceItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            return;
        }
        
        // Separate items by receipt number
        List<InvoiceItem> receipt1Items = new ArrayList<>();
        List<InvoiceItem> receipt2Items = new ArrayList<>();
        
        for (InvoiceItem item : items) {
            if (item.getReceiptNumber() == 1) {
                receipt1Items.add(item);
            } else if (item.getReceiptNumber() == 2) {
                receipt2Items.add(item);
            }
        }
        
        // Insert items into invoice_items_one table
        if (!receipt1Items.isEmpty()) {
            try {
                String sql1 = "INSERT INTO invoice_items_one (invoice_id, item_type, item_id, item_name, quantity, unit_price, total_amount) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                    for (InvoiceItem item : receipt1Items) {
                        ps.setString(1, invoiceId);
                        ps.setString(2, item.getItemType());
                        ps.setInt(3, item.getItemId());
                        ps.setString(4, item.getItemName());
                        ps.setInt(5, item.getQuantity());
                        ps.setBigDecimal(6, item.getUnitPrice());
                        ps.setBigDecimal(7, item.getTotalAmount());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error inserting into invoice_items_one: {0}", e.getMessage());
                // Check if table exists and provide helpful error message
                if (e.getMessage().contains("doesn't exist") || e.getMessage().contains("Table") || e.getMessage().contains("not found")) {
                    throw new SQLException("Table 'invoice_items_one' does not exist. Please run the database update script first.", e);
                }
                throw e;
            }
        }
        
        // Insert items into invoice_items_two table
        if (!receipt2Items.isEmpty()) {
            try {
                String sql2 = "INSERT INTO invoice_items_two (invoice_id, item_type, item_id, item_name, quantity, unit_price, total_amount) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                    for (InvoiceItem item : receipt2Items) {
                        ps.setString(1, invoiceId);
                        ps.setString(2, item.getItemType());
                        ps.setInt(3, item.getItemId());
                        ps.setString(4, item.getItemName());
                        ps.setInt(5, item.getQuantity());
                        ps.setBigDecimal(6, item.getUnitPrice());
                        ps.setBigDecimal(7, item.getTotalAmount());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error inserting into invoice_items_two: {0}", e.getMessage());
                // Check if table exists and provide helpful error message
                if (e.getMessage().contains("doesn't exist") || e.getMessage().contains("Table") || e.getMessage().contains("not found")) {
                    throw new SQLException("Table 'invoice_items_two' does not exist. Please run the database update script first.", e);
                }
                throw e;
            }
        }
    }

    private void deleteInvoiceItemsFromBothTables(Connection conn, String invoiceId) throws SQLException {
        // Delete from invoice_items_one
        String sql1 = "DELETE FROM invoice_items_one WHERE invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql1)) {
            ps.setString(1, invoiceId);
            ps.executeUpdate();
        }
        
        // Delete from invoice_items_two
        String sql2 = "DELETE FROM invoice_items_two WHERE invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setString(1, invoiceId);
            ps.executeUpdate();
        }
    }

    // ===== SEARCH AND REFERENCE DATA =====
    
    public List<MedicalService> getAllServices() {
        List<MedicalService> services = new ArrayList<>();
        String sql = "SELECT * FROM medical_services WHERE isdeleted = 0 ORDER BY service_name";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MedicalService service = new MedicalService();
                service.setServicesId(rs.getInt("services_id"));
                service.setServiceGroup(rs.getString("service_group"));
                service.setServiceName(rs.getString("service_name"));
                service.setPrice(rs.getBigDecimal("price"));
                services.add(service);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all services: {0}", e.getMessage());
        }

        return services;
    }

    public List<MedicalSupply> getAllSupplies() {
        List<MedicalSupply> supplies = new ArrayList<>();
        String sql = "SELECT * FROM medical_supply WHERE isdeleted = 0 ORDER BY supply_name";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MedicalSupply supply = new MedicalSupply();
                supply.setSupplyId(rs.getInt("supply_id"));
                supply.setSupplyGroup(rs.getString("supply_group"));
                supply.setSupplyName(rs.getString("supply_name"));
                supply.setUnitPrice(rs.getBigDecimal("unit_price"));
                supply.setStockQuantity(rs.getInt("stock_quantity"));
                supplies.add(supply);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all supplies: {0}", e.getMessage());
        }

        return supplies;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM examination_medicines WHERE isdeleted = 0 ORDER BY medicine_name";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Medicine medicine = new Medicine();
                medicine.setExamMedicineId(rs.getInt("exam_medicine_id"));
                medicine.setMedicineName(rs.getString("medicine_name"));
                medicine.setUnitOfMeasure(rs.getString("unit_of_measure"));
                medicine.setUnitPrice(rs.getBigDecimal("unit_price"));
                medicine.setStockQuantity(rs.getInt("stock_quantity"));
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all medicines: {0}", e.getMessage());
        }

        return medicines;
    }

    // ===== UTILITY METHODS =====
    
    private String generateInvoiceId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(invoice_id, 4) AS UNSIGNED)) as max_num FROM invoices WHERE invoice_id LIKE 'INV%'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return String.format("INV%06d", maxNum + 1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating invoice ID: {0}", e.getMessage());
        }
        return "INV000001";
    }

    private Invoice extractInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getString("invoice_id"));
        invoice.setMedicalRecordId(rs.getString("medical_record_id"));
        invoice.setPatientId(rs.getInt("patient_id"));
        invoice.setDoctorId((Integer) rs.getObject("doctor_id"));
        invoice.setInvoiceDate(rs.getTimestamp("invoice_date"));
        invoice.setTotalServiceAmount(rs.getBigDecimal("total_service_amount"));
        invoice.setTotalSupplyAmount(rs.getBigDecimal("total_supply_amount"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        invoice.setFinalAmount(rs.getBigDecimal("final_amount"));
        invoice.setNotes(rs.getString("notes"));
        invoice.setCreatedAt(rs.getTimestamp("created_at"));
        invoice.setUpdatedAt(rs.getTimestamp("updated_at"));
        invoice.setCreatedBy(rs.getString("created_by"));
        invoice.setUpdatedBy(rs.getString("updated_by"));
        invoice.setDeleted(rs.getBoolean("isdeleted"));
        return invoice;
    }

    private InvoiceItem extractInvoiceItemFromTable(ResultSet rs, int receiptNumber) throws SQLException {
        InvoiceItem item = new InvoiceItem();
        item.setId(rs.getInt("id"));
        item.setInvoiceId(rs.getString("invoice_id"));
        item.setReceiptNumber(receiptNumber);
        item.setItemType(rs.getString("item_type"));
        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setTotalAmount(rs.getBigDecimal("total_amount"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
    
    // ===== WAREHOUSE STOCK MANAGEMENT =====
    
    /**
     * Kiểm tra tồn kho có đủ không trước khi tạo invoice
     */
    private String validateStockAvailability(List<InvoiceItem> items) {
        for (InvoiceItem item : items) {
            if ("supply".equals(item.getItemType())) {
                if (!daoWarehouse.hasEnoughSupplyStock(item.getItemId(), item.getQuantity())) {
                    int currentStock = daoWarehouse.getSupplyStockQuantity(item.getItemId());
                    return String.format("Không đủ tồn kho cho vật tư '%s'. Cần: %d, Còn lại: %d", 
                                       item.getItemName(), item.getQuantity(), currentStock);
                }
            } else if ("medicine".equals(item.getItemType())) {
                if (!daoWarehouse.hasEnoughMedicineStock(item.getItemId(), item.getQuantity())) {
                    int currentStock = daoWarehouse.getMedicineStockQuantity(item.getItemId());
                    return String.format("Không đủ tồn kho cho thuốc '%s'. Cần: %d, Còn lại: %d", 
                                       item.getItemName(), item.getQuantity(), currentStock);
                }
            }
        }
        return null; // OK
    }
    
    /**
     * Kiểm tra tồn kho khi cập nhật invoice (tính cả việc hoàn trả items cũ)
     */
    private String validateStockAvailabilityForUpdate(List<InvoiceItem> oldItems, List<InvoiceItem> newItems) {
        // Tạo map để track số lượng thay đổi cho từng item
        java.util.Map<String, Integer> stockChanges = new java.util.HashMap<>();
        
        // Hoàn trả từ items cũ (cộng vào kho)
        if (oldItems != null) {
            for (InvoiceItem item : oldItems) {
                if ("supply".equals(item.getItemType()) || "medicine".equals(item.getItemType())) {
                    String key = item.getItemType() + "_" + item.getItemId();
                    stockChanges.put(key, stockChanges.getOrDefault(key, 0) + item.getQuantity());
                }
            }
        }
        
        // Trừ từ items mới (trừ khỏi kho)
        if (newItems != null) {
            for (InvoiceItem item : newItems) {
                if ("supply".equals(item.getItemType()) || "medicine".equals(item.getItemType())) {
                    String key = item.getItemType() + "_" + item.getItemId();
                    stockChanges.put(key, stockChanges.getOrDefault(key, 0) - item.getQuantity());
                }
            }
        }
        
        // Kiểm tra từng item xem có đủ stock không
        for (java.util.Map.Entry<String, Integer> entry : stockChanges.entrySet()) {
            String[] parts = entry.getKey().split("_");
            String itemType = parts[0];
            int itemId = Integer.parseInt(parts[1]);
            int netChange = entry.getValue(); // Âm = cần trừ, dương = được cộng
            
            if (netChange < 0) { // Cần trừ kho
                int requiredStock = Math.abs(netChange);
                if ("supply".equals(itemType)) {
                    if (!daoWarehouse.hasEnoughSupplyStock(itemId, requiredStock)) {
                        int currentStock = daoWarehouse.getSupplyStockQuantity(itemId);
                        MedicalSupply supply = daoWarehouse.getSupplyById(itemId);
                        String itemName = supply != null ? supply.getSupplyName() : "Unknown";
                        return String.format("Không đủ tồn kho cho vật tư '%s'. Cần: %d, Còn lại: %d", 
                                           itemName, requiredStock, currentStock);
                    }
                } else if ("medicine".equals(itemType)) {
                    if (!daoWarehouse.hasEnoughMedicineStock(itemId, requiredStock)) {
                        int currentStock = daoWarehouse.getMedicineStockQuantity(itemId);
                        Medicine medicine = daoWarehouse.getMedicineById(itemId);
                        String itemName = medicine != null ? medicine.getMedicineName() : "Unknown";
                        return String.format("Không đủ tồn kho cho thuốc '%s'. Cần: %d, Còn lại: %d", 
                                           itemName, requiredStock, currentStock);
                    }
                }
            }
        }
        
        return null; // OK
    }
    
    /**
     * Cập nhật kho hàng cho danh sách items
     * @param items Danh sách items
     * @param isRestore true = hoàn trả (cộng vào kho), false = trừ kho
     * @return true nếu thành công
     */
    private boolean updateWarehouseStock(List<InvoiceItem> items, boolean isRestore) {
        try {
            for (InvoiceItem item : items) {
                if ("supply".equals(item.getItemType())) {
                    int quantityChange = isRestore ? item.getQuantity() : -item.getQuantity();
                    if (!daoWarehouse.updateSupplyStock(item.getItemId(), quantityChange)) {
                        LOGGER.log(Level.SEVERE, "Failed to update supply stock for item: {0}, change: {1}", 
                                 new Object[]{item.getItemName(), quantityChange});
                        return false;
                    }
                } else if ("medicine".equals(item.getItemType())) {
                    int quantityChange = isRestore ? item.getQuantity() : -item.getQuantity();
                    if (!daoWarehouse.updateMedicineStock(item.getItemId(), quantityChange)) {
                        LOGGER.log(Level.SEVERE, "Failed to update medicine stock for item: {0}, change: {1}", 
                                 new Object[]{item.getItemName(), quantityChange});
                        return false;
                    }
                }
                // Service items không cần cập nhật kho
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating warehouse stock: {0}", e.getMessage());
            return false;
        }
    }
} 