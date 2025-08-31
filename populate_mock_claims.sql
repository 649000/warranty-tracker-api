-- Insert mock claims for the existing warranties
INSERT INTO warranty_tracker.claims (
    claim_date, status, reference_number, issue_description, resolution_details, 
    created_at, updated_at, warranty_id
) VALUES
-- Claim for iPhone 15 Pro warranty
(NOW() - INTERVAL '10 days', 'SUBMITTED', 'CLM-2024-001', 
 'Screen cracked after accidental drop', 'Awaiting assessment from authorized service center', 
 NOW(), NOW(), 1),

-- Claim for Galaxy S24 Ultra warranty
(NOW() - INTERVAL '5 days', 'PROCESSING', 'CLM-2024-002', 
 'Water damage - phone not turning on', 'Device received at service center, diagnostics in progress', 
 NOW(), NOW(), 2),

-- Claim for PlayStation 5 warranty
(NOW() - INTERVAL '15 days', 'APPROVED', 'CLM-2024-003', 
 'Controller not responding intermittently', 'Replacement controller approved and shipped', 
 NOW(), NOW(), 3),

-- Claim for MacBook Air M3 warranty
(NOW() - INTERVAL '3 days', 'SUBMITTED', 'CLM-2024-004', 
 'Battery draining quickly, not holding charge', 'Awaiting diagnostic appointment', 
 NOW(), NOW(), 4),

-- Claim for completed warranty (OLED TV)
(NOW() - INTERVAL '20 days', 'COMPLETED', 'CLM-2024-005', 
 'Screen flickering issue', 'Screen replaced under warranty, case closed', 
 NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 5),

-- Claim for GoPro warranty
(NOW() - INTERVAL '12 days', 'DENIED', 'CLM-2024-006', 
 'Water damage - not covered under standard warranty', 'Claim denied as damage not covered', 
 NOW(), NOW(), 6);
