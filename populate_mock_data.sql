-- Insert mock products
INSERT INTO warranty_tracker.products (name, brand, model_number, created_at, updated_at) VALUES
('iPhone 15 Pro', 'Apple', 'A2842', NOW(), NOW()),
('Galaxy S24 Ultra', 'Samsung', 'SM-S928U', NOW(), NOW()),
('PlayStation 5', 'Sony', 'CFI-1215A', NOW(), NOW()),
('MacBook Air M3', 'Apple', 'M3MA2023', NOW(), NOW()),
('OLED TV 65"', 'LG', 'OLED65C3PUB', NOW(), NOW()),
('4K Action Camera', 'GoPro', 'CHDHX-901', NOW(), NOW()),
('Wireless Headphones', 'Sony', 'WH-1000XM5', NOW(), NOW()),
('Gaming Laptop', 'ASUS', 'ROG-Zephyrus-G14', NOW(), NOW()),
('Smart Watch', 'Apple', 'A2762', NOW(), NOW()),
('Tablet', 'Samsung', 'SM-X910', NOW(), NOW());

-- Insert mock user products (linking user ID 1 with products)
INSERT INTO warranty_tracker.user_products (
    serial_number, purchase_date, purchase_price, purchase_location, 
    receipt_number, notes, created_at, updated_at, product_id, user_id
) VALUES
('SN-APPLE-IP15P-001', '2024-01-15', 1199.00, 'Apple Store Singapore', 
 'RCP-2024-001', 'Purchased during Chinese New Year sale', NOW(), NOW(), 1, 1),
 
('SN-SAMSUNG-S24U-001', '2024-02-20', 1399.00, 'Samsung Flagship Store', 
 'RCP-2024-002', 'Pre-order model', NOW(), NOW(), 2, 1),
 
('SN-SONY-PS5-001', '2023-12-10', 699.00, 'Harvey Norman', 
 'RCP-2023-001', 'Holiday special edition', NOW(), NOW(), 3, 1),
 
('SN-APPLE-MACM3-001', '2024-03-05', 1499.00, 'Apple Online Store', 
 'RCP-2024-003', 'Education discount applied', NOW(), NOW(), 4, 1),
 
('SN-LG-OLED65-001', '2024-01-30', 2499.00, 'Best Denki', 
 'RCP-2024-004', 'Free wall mount included', NOW(), NOW(), 5, 1),
 
('SN-GOPRO-CHDHX-001', '2023-11-25', 399.00, 'Cathay Photo', 
 'RCP-2023-002', 'Adventure bundle package', NOW(), NOW(), 6, 1),
 
('SN-SONY-WH1000XM5-001', '2024-02-14', 399.00, 'Sony Store', 
 'RCP-2024-005', 'Valentine''s Day gift', NOW(), NOW(), 7, 1);

-- Insert mock warranties (linking to companies 55-74 and user products)
INSERT INTO warranty_tracker.warranties (
    start_date, end_date, warranty_period, warranty_type, notes, status, 
    created_at, updated_at, user_id, company_id, product_id, user_product_id
) VALUES
('2024-01-15', '2026-01-15', 24, 'Manufacturer Warranty', 
 'Standard Apple warranty with optional AppleCare+', 'ACTIVE', 
 NOW(), NOW(), 1, 55, 1, 1),
 
('2024-02-20', '2026-02-20', 24, 'Manufacturer Warranty', 
 'Samsung standard warranty with accidental damage coverage', 'ACTIVE', 
 NOW(), NOW(), 1, 56, 2, 2),
 
('2023-12-10', '2025-12-10', 24, 'Manufacturer Warranty', 
 'Sony PlayStation warranty', 'ACTIVE', 
 NOW(), NOW(), 1, 57, 3, 3),
 
('2024-03-05', '2026-03-05', 24, 'Manufacturer Warranty', 
 'Apple limited warranty with AppleCare+ option', 'ACTIVE', 
 NOW(), NOW(), 1, 55, 4, 4),
 
('2024-01-30', '2025-01-30', 12, 'Manufacturer Warranty', 
 'LG standard TV warranty', 'ACTIVE', 
 NOW(), NOW(), 1, 58, 5, 5),
 
('2023-11-25', '2025-11-25', 24, 'Manufacturer Warranty', 
 'GoPro standard warranty', 'ACTIVE', 
 NOW(), NOW(), 1, 65, 6, 6),
 
('2024-02-14', '2026-02-14', 24, 'Manufacturer Warranty', 
 'Sony audio product warranty', 'ACTIVE', 
 NOW(), NOW(), 1, 57, 7, 7);
