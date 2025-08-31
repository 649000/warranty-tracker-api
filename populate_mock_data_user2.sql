-- Insert mock products for user 2
INSERT INTO warranty_tracker.products (name, brand, model_number, created_at, updated_at) VALUES
('iPhone 14 Pro', 'Apple', 'A2650', NOW(), NOW()),
('Galaxy S23 Ultra', 'Samsung', 'SM-S918U', NOW(), NOW()),
('Xbox Series X', 'Microsoft', 'XBXS-X', NOW(), NOW()),
('iPad Pro M2', 'Apple', 'M2IP2023', NOW(), NOW()),
('QLED TV 55"', 'Samsung', 'QN55Q80CAFXZA', NOW(), NOW()),
('Digital Camera', 'Canon', 'EOS-R5', NOW(), NOW()),
('Bluetooth Speaker', 'JBL', 'Charge-5', NOW(), NOW()),
('Gaming Desktop', 'Alienware', 'Aurora-R15', NOW(), NOW()),
('Fitness Tracker', 'Fitbit', 'Charge-6', NOW(), NOW()),
('Smart Home Hub', 'Google', 'Nest-Hub-2', NOW(), NOW());

-- Insert mock user products (linking user ID 2 with products)
INSERT INTO warranty_tracker.user_products (
    serial_number, purchase_date, purchase_price, purchase_location, 
    receipt_number, notes, created_at, updated_at, product_id, user_id
) VALUES
('SN-APPLE-IP14P-002', '2023-09-15', 1099.00, 'Apple Store Singapore', 
 'RCP-2023-003', 'Trade-in discount applied', NOW(), NOW(), 11, 2),
 
('SN-SAMSUNG-S23U-002', '2023-10-20', 1299.00, 'Samsung Flagship Store', 
 'RCP-2023-004', 'Pre-order model', NOW(), NOW(), 12, 2),
 
('SN-MICROSOFT-XBXS-002', '2023-11-10', 599.00, 'Harvey Norman', 
 'RCP-2023-005', 'Holiday special edition', NOW(), NOW(), 13, 2),
 
('SN-APPLE-IPADM2-002', '2023-12-05', 1199.00, 'Apple Online Store', 
 'RCP-2023-006', 'Education discount applied', NOW(), NOW(), 14, 2),
 
('SN-SAMSUNG-QLED55-002', '2023-08-30', 1499.00, 'Best Denki', 
 'RCP-2023-007', 'Free wall mount included', NOW(), NOW(), 15, 2),
 
('SN-CANON-EOSR5-002', '2023-07-25', 3899.00, 'Cathay Photo', 
 'RCP-2023-008', 'Professional photography bundle', NOW(), NOW(), 16, 2),
 
('SN-JBL-CHARGE5-002', '2023-06-14', 179.00, 'Courts', 
 'RCP-2023-009', 'Father''s Day gift', NOW(), NOW(), 17, 2);

-- Insert mock warranties (linking to companies 55-74 and user products for user 2)
INSERT INTO warranty_tracker.warranties (
    start_date, end_date, warranty_period, warranty_type, notes, status, 
    created_at, updated_at, user_id, company_id, product_id, user_product_id
) VALUES
('2023-09-15', '2025-09-15', 24, 'Manufacturer Warranty', 
 'Standard Apple warranty with optional AppleCare+', 'ACTIVE', 
 NOW(), NOW(), 2, 55, 11, 8),
 
('2023-10-20', '2025-10-20', 24, 'Manufacturer Warranty', 
 'Samsung standard warranty with accidental damage coverage', 'ACTIVE', 
 NOW(), NOW(), 2, 56, 12, 9),
 
('2023-11-10', '2025-11-10', 24, 'Manufacturer Warranty', 
 'Microsoft Xbox warranty', 'ACTIVE', 
 NOW(), NOW(), 2, 61, 13, 10),
 
('2023-12-05', '2025-12-05', 24, 'Manufacturer Warranty', 
 'Apple limited warranty with AppleCare+ option', 'ACTIVE', 
 NOW(), NOW(), 2, 55, 14, 11),
 
('2023-08-30', '2024-08-30', 12, 'Manufacturer Warranty', 
 'Samsung standard TV warranty', 'ACTIVE', 
 NOW(), NOW(), 2, 56, 15, 12),
 
('2023-07-25', '2025-07-25', 24, 'Manufacturer Warranty', 
 'Canon standard warranty', 'ACTIVE', 
 NOW(), NOW(), 2, 69, 16, 13),
 
('2023-06-14', '2025-06-14', 24, 'Manufacturer Warranty', 
 'JBL audio product warranty', 'ACTIVE', 
 NOW(), NOW(), 2, 70, 17, 14);

-- Insert mock claims for user 2's warranties
INSERT INTO warranty_tracker.claims (
    claim_date, status, reference_number, issue_description, resolution_details, 
    created_at, updated_at, warranty_id
) VALUES
-- Claim for iPhone 14 Pro warranty
(NOW() - INTERVAL '8 days', 'SUBMITTED', 'CLM-2024-007', 
 'Battery not holding charge properly', 'Awaiting assessment from authorized service center', 
 NOW(), NOW(), 8),

-- Claim for Galaxy S23 Ultra warranty
(NOW() - INTERVAL '3 days', 'PROCESSING', 'CLM-2024-008', 
 'Camera lens error', 'Device received at service center, diagnostics in progress', 
 NOW(), NOW(), 9),

-- Claim for Xbox Series X warranty
(NOW() - INTERVAL '12 days', 'APPROVED', 'CLM-2024-009', 
 'Controller connectivity issues', 'Replacement controller approved and shipped', 
 NOW(), NOW(), 10),

-- Claim for iPad Pro M2 warranty
(NOW() - INTERVAL '1 day', 'SUBMITTED', 'CLM-2024-010', 
 'Screen has dead pixels', 'Awaiting diagnostic appointment', 
 NOW(), NOW(), 11),

-- Claim for completed warranty (QLED TV)
(NOW() - INTERVAL '18 days', 'COMPLETED', 'CLM-2024-011', 
 'Backlight failure', 'Backlight replaced under warranty, case closed', 
 NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 12),

-- Claim for Canon camera warranty
(NOW() - INTERVAL '10 days', 'DENIED', 'CLM-2024-012', 
 'Lens fungus - not covered under standard warranty', 'Claim denied as damage not covered', 
 NOW(), NOW(), 13);
