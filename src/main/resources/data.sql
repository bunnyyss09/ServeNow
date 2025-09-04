-- ServeNow Database Initialization Script
-- This script creates default roles and categories for the Local Service Finder platform

-- Insert default roles
INSERT IGNORE INTO roles (name, description, created_at, updated_at, is_active) VALUES
('CUSTOMER', 'Regular customer who books services', NOW(), NOW(), true),
('PROVIDER', 'Service provider who offers services', NOW(), NOW(), true),
('ADMIN', 'Platform administrator with full access', NOW(), NOW(), true),
('MODERATOR', 'Platform moderator with limited admin access', NOW(), NOW(), true);

-- Insert default categories
INSERT IGNORE INTO categories (name, description, slug, sort_order, is_featured, created_at, updated_at, is_active) VALUES
-- Top-level categories
('Home Services', 'All home-related services including cleaning, maintenance, and repairs', 'home-services', 1, true, NOW(), NOW(), true),
('Health & Wellness', 'Healthcare, fitness, beauty, and wellness services', 'health-wellness', 2, true, NOW(), NOW(), true),
('Education & Training', 'Educational services, tutoring, and professional training', 'education-training', 3, true, NOW(), NOW(), true),
('Technology Services', 'IT support, software development, and tech consulting', 'technology-services', 4, true, NOW(), NOW(), true),
('Business Services', 'Professional business services and consulting', 'business-services', 5, false, NOW(), NOW(), true),
('Event Services', 'Event planning, catering, entertainment, and party services', 'event-services', 6, true, NOW(), NOW(), true),
('Transportation', 'Moving, delivery, and transportation services', 'transportation', 7, false, NOW(), NOW(), true),
('Personal Care', 'Beauty, grooming, and personal care services', 'personal-care', 8, true, NOW(), NOW(), true);

-- Get category IDs for subcategories (MySQL specific)
SET @home_services_id = (SELECT id FROM categories WHERE slug = 'home-services' LIMIT 1);
SET @health_wellness_id = (SELECT id FROM categories WHERE slug = 'health-wellness' LIMIT 1);
SET @education_training_id = (SELECT id FROM categories WHERE slug = 'education-training' LIMIT 1);
SET @technology_services_id = (SELECT id FROM categories WHERE slug = 'technology-services' LIMIT 1);
SET @business_services_id = (SELECT id FROM categories WHERE slug = 'business-services' LIMIT 1);
SET @event_services_id = (SELECT id FROM categories WHERE slug = 'event-services' LIMIT 1);
SET @transportation_id = (SELECT id FROM categories WHERE slug = 'transportation' LIMIT 1);
SET @personal_care_id = (SELECT id FROM categories WHERE slug = 'personal-care' LIMIT 1);

-- Insert subcategories for Home Services
INSERT IGNORE INTO categories (name, description, slug, parent_category_id, sort_order, is_featured, created_at, updated_at, is_active) VALUES
('House Cleaning', 'Regular house cleaning, deep cleaning, and maid services', 'house-cleaning', @home_services_id, 1, true, NOW(), NOW(), true),
('Plumbing', 'Plumbing repairs, installations, and maintenance', 'plumbing', @home_services_id, 2, true, NOW(), NOW(), true),
('Electrical Work', 'Electrical repairs, installations, and troubleshooting', 'electrical-work', @home_services_id, 3, true, NOW(), NOW(), true),
('HVAC Services', 'Heating, ventilation, and air conditioning services', 'hvac-services', @home_services_id, 4, false, NOW(), NOW(), true),
('Painting', 'Interior and exterior painting services', 'painting', @home_services_id, 5, true, NOW(), NOW(), true),
('Carpentry', 'Furniture assembly, custom woodwork, and repairs', 'carpentry', @home_services_id, 6, false, NOW(), NOW(), true),
('Landscaping', 'Lawn care, gardening, and outdoor maintenance', 'landscaping', @home_services_id, 7, true, NOW(), NOW(), true),
('Pest Control', 'Pest inspection, extermination, and prevention services', 'pest-control', @home_services_id, 8, false, NOW(), NOW(), true);

-- Insert subcategories for Health & Wellness
INSERT IGNORE INTO categories (name, description, slug, parent_category_id, sort_order, is_featured, created_at, updated_at, is_active) VALUES
('Personal Training', 'Fitness coaching and personal training sessions', 'personal-training', @health_wellness_id, 1, true, NOW(), NOW(), true),
('Massage Therapy', 'Therapeutic and relaxation massage services', 'massage-therapy', @health_wellness_id, 2, true, NOW(), NOW(), true),
('Nutrition Consulting', 'Diet planning and nutritional guidance', 'nutrition-consulting', @health_wellness_id, 3, false, NOW(), NOW(), true),
('Mental Health', 'Counseling and therapy services', 'mental-health', @health_wellness_id, 4, false, NOW(), NOW(), true),
('Yoga Instruction', 'Yoga classes and personal instruction', 'yoga-instruction', @health_wellness_id, 5, true, NOW(), NOW(), true);

-- Insert subcategories for Education & Training
INSERT IGNORE INTO categories (name, description, slug, parent_category_id, sort_order, is_featured, created_at, updated_at, is_active) VALUES
('Academic Tutoring', 'Subject-specific tutoring for students', 'academic-tutoring', @education_training_id, 1, true, NOW(), NOW(), true),
('Language Learning', 'Foreign language instruction and practice', 'language-learning', @education_training_id, 2, true, NOW(), NOW(), true),
('Music Lessons', 'Instrument and vocal music instruction', 'music-lessons', @education_training_id, 3, true, NOW(), NOW(), true),
('Professional Development', 'Career coaching and professional skills training', 'professional-development', @education_training_id, 4, false, NOW(), NOW(), true),
('Test Preparation', 'Standardized test prep and exam coaching', 'test-preparation', @education_training_id, 5, false, NOW(), NOW(), true);

-- Insert subcategories for Technology Services
INSERT IGNORE INTO categories (name, description, slug, parent_category_id, sort_order, is_featured, created_at, updated_at, is_active) VALUES
('Computer Repair', 'Hardware troubleshooting and computer repairs', 'computer-repair', @technology_services_id, 1, true, NOW(), NOW(), true),
('Software Development', 'Custom software and application development', 'software-development', @technology_services_id, 2, false, NOW(), NOW(), true),
('IT Support', 'Technical support and system administration', 'it-support', @technology_services_id, 3, true, NOW(), NOW(), true),
('Web Design', 'Website design and development services', 'web-design', @technology_services_id, 4, true, NOW(), NOW(), true),
('Data Recovery', 'Data recovery and backup services', 'data-recovery', @technology_services_id, 5, false, NOW(), NOW(), true);

-- Insert sample admin user (password should be hashed in real implementation)
-- Note: This is for development only - use proper password hashing in production
INSERT IGNORE INTO users (
    first_name, last_name, email, password, phone_number, 
    address, city, state, postal_code, country,
    is_email_verified, is_phone_verified, account_non_expired, 
    account_non_locked, credentials_non_expired, enabled,
    created_at, updated_at, is_active
) VALUES (
    'System', 'Administrator', 'admin@servenow.com', 
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewUkfOARUPYKsE36', -- 'admin123'
    '+1-555-0001',
    '123 Admin Street', 'Admin City', 'AC', '12345', 'USA',
    true, true, true, true, true, true,
    NOW(), NOW(), true
);

-- Assign ADMIN role to the admin user
SET @admin_user_id = (SELECT id FROM users WHERE email = 'admin@servenow.com' LIMIT 1);
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ADMIN' LIMIT 1);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (@admin_user_id, @admin_role_id);