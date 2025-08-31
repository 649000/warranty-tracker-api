INSERT INTO warranty_tracker.companies (
    name, contact_phone, contact_email, website, address, 
    claim_process, claim_url, support_hours, return_instructions, 
    created_at, updated_at
) VALUES
-- Apple Singapore
('Apple Singapore', '+65 800 221 1388', 'support@apple.com', 
 'https://www.apple.com/sg/', '27 Prince Edward Road, Singapore 200888', 
 'Visit Apple Store or authorized service provider with proof of purchase', 
 'https://getsupport.apple.com/', '9:00 AM - 10:00 PM daily', 
 'Bring device to Apple Store or authorized service center with receipt', 
 NOW(), NOW()),

-- Samsung Electronics Singapore
('Samsung Electronics Singapore', '+65 1800 726 7864', 'service@samsung.com', 
 'https://www.samsung.com/sg/', '50 Raffles Place, #36-01 Singapore Land Tower, Singapore 048623', 
 'Register online or visit Samsung Service Center with warranty card', 
 'https://www.samsung.com/sg/support/service-request/', '9:00 AM - 6:00 PM (Mon-Fri), 9:00 AM - 1:00 PM (Sat)', 
 'Contact Samsung customer service for return authorization', 
 NOW(), NOW()),

-- Sony Singapore
('Sony Singapore', '+65 6560 0888', 'support@sony.com', 
 'https://www.sony.com.sg/', '1 Scotts Road, #24-10 Shaw Centre, Singapore 228208', 
 'Submit claim online or visit authorized Sony service center', 
 'https://www.sony.com.sg/support/contact', '9:30 AM - 6:00 PM (Mon-Fri), 9:30 AM - 6:00 PM (Sat)', 
 'Return items to Sony service center with original packaging', 
 NOW(), NOW()),

-- LG Electronics Singapore
('LG Electronics Singapore', '+65 1800 852 852 852', 'lgcare@lg.com', 
 'https://www.lg.com/sg', '100 Pasir Panjang Road, #07-01 Mapletree Business City, Singapore 117437', 
 'Call hotline or register online for service appointment', 
 'https://www.lg.com/sg/support', '9:00 AM - 6:00 PM (Mon-Fri), 9:00 AM - 1:00 PM (Sat)', 
 'Contact LG customer service for return procedures', 
 NOW(), NOW()),

-- Panasonic Singapore
('Panasonic Singapore', '+65 1800 227 2642', 'panasonic.care@panasonic.com', 
 'https://www.panasonic.com/sg/', '101 Thomson Road, #27-00 United Square, Singapore 307591', 
 'Submit warranty claim with proof of purchase at authorized service center', 
 'https://www.panasonic.com/sg/support/contact-us.html', '9:00 AM - 5:30 PM (Mon-Fri)', 
 'Bring product to authorized service center with receipt', 
 NOW(), NOW()),

-- Philips Singapore
('Philips Singapore', '+65 1800 227 2642', 'philips.care@philips.com', 
 'https://www.philips.com.sg/', '315 Jalan Ahmad Ibrahim, Singapore 629143', 
 'Register online or contact customer service for repair service', 
 'https://www.philips.com.sg/support', '9:00 AM - 6:00 PM (Mon-Fri)', 
 'Contact Philips customer service for return authorization', 
 NOW(), NOW()),

-- Dell Singapore
('Dell Singapore', '+65 1800 726 7864', 'support@dell.com', 
 'https://www.dell.com/sg/', '80 Pasir Panjang Road, #05-01/02 Mapletree Business City, Singapore 117372', 
 'Register online or call technical support with service tag', 
 'https://www.dell.com/support', '9:00 AM - 6:00 PM (Mon-Fri)', 
 'Contact Dell customer service for return merchandise authorization', 
 NOW(), NOW()),

-- HP Singapore
('HP Singapore', '+65 1800 227 2642', 'support@hp.com', 
 'https://www.hp.com/sg/', '30 Gul Crescent, Singapore 629347', 
 'Submit online support request or call HP support', 
 'https://support.hp.com/sg-en', '9:00 AM - 6:00 PM (Mon-Fri)', 
 'Contact HP customer service for return authorization', 
 NOW(), NOW()),

-- Canon Singapore
('Canon Singapore', '+65 6330 8811', 'canon.care@canon.com', 
 'https://www.canon.com.sg/', '1 HarbourFront Avenue, #09-01 Keppel Bay Tower, Singapore 098632', 
 'Bring product to Canon service center with warranty card', 
 'https://www.canon.com.sg/support', '9:00 AM - 6:00 PM (Mon-Fri), 9:00 AM - 1:00 PM (Sat)', 
 'Contact Canon customer service for return procedures', 
 NOW(), NOW()),

-- Epson Singapore
('Epson Singapore', '+65 6290 1288', 'epson.care@epson.com', 
 'https://www.epson.com.sg/', '331B Jalan Ahmad Ibrahim, Singapore 629143', 
 'Register online or contact Epson support for service', 
 'https://www.epson.com.sg/support', '9:00 AM - 6:00 PM (Mon-Fri)', 
 'Contact Epson customer service for return authorization', 
 NOW(), NOW());
