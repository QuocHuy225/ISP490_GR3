// Load existing items for invoice edit mode
function initializeExistingItems() {
    // This will be populated by JSP
    if (typeof window.invoiceItemsData !== 'undefined') {
        const allItems = window.invoiceItemsData;
        
        // Separate items by receipt number
        const receipt1Items = allItems.filter(item => item.receiptNumber === 1);
        const receipt2Items = allItems.filter(item => item.receiptNumber === 2);
        
        // Load receipt 1 items
        receipt1Items.forEach(item => {
            addItemRow('receipt1', item.itemType, item.itemId, item.quantity);
        });
        
        // Load receipt 2 items if any
        if (receipt2Items.length > 0) {
            document.getElementById('enableSecondReceipt').checked = true;
            toggleSecondReceipt();
            
            receipt2Items.forEach(item => {
                addItemRow('receipt2', item.itemType, item.itemId, item.quantity);
            });
        }
    }
} 