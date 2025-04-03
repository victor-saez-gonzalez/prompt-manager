-- Insert users
INSERT INTO users (id, email, name, password, provider, provider_id) VALUES
('11111111-1111-1111-1111-111111111111', 'john.doe@example.com', 'John Doe', 'hashedpassword1', 'LOCAL', 'localid1'),
('22222222-2222-2222-2222-222222222222', 'jane.smith@example.com', 'Jane Smith', 'hashedpassword2', 'LOCAL', 'localid2'),
('33333333-3333-3333-3333-333333333333', 'alice.jones@example.com', 'Alice Jones', 'hashedpassword3', 'LOCAL', 'localid3'),
('44444444-4444-4444-4444-444444444444', 'bob.martin@example.com', 'Bob Martin', 'hashedpassword4', 'LOCAL', 'localid4'),
('55555555-5555-5555-5555-555555555555', 'charlie.brown@example.com', 'Charlie Brown', 'hashedpassword5', 'LOCAL', 'localid5'),
('66666666-6666-6666-6666-666666666666', 'diana.wilson@example.com', 'Diana Wilson', 'hashedpassword6', 'LOCAL', 'localid6'),
('77777777-7777-7777-7777-777777777777', 'eva.davis@example.com', 'Eva Davis', 'hashedpassword7', 'LOCAL', 'localid7'),
('88888888-8888-8888-8888-888888888888', 'frank.miller@example.com', 'Frank Miller', 'hashedpassword8', 'LOCAL', 'localid8'),
('99999999-9999-9999-9999-999999999999', 'grace.taylor@example.com', 'Grace Taylor', 'hashedpassword9', 'LOCAL', 'localid9'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'hannah.moore@example.com', 'Hannah Moore', 'hashedpassword10', 'LOCAL', 'localid10');

-- Insert categories (relacionadas con el primer usuario)
INSERT INTO category (id, name, description, user_id) VALUES
('11111111-1111-1111-1111-000000000001', 'Utilities', 'Tools and helpers', '11111111-1111-1111-1111-111111111111'),
('22222222-2222-2222-2222-000000000002', 'Creativity', 'Writing and art prompts', '11111111-1111-1111-1111-111111111111'),
('33333333-3333-3333-3333-000000000003', 'Productivity', 'Time management and focus', '11111111-1111-1111-1111-111111111111'),
('44444444-4444-4444-4444-000000000004', 'Health', 'Fitness, nutrition, and wellness', '11111111-1111-1111-1111-111111111111'),
('55555555-5555-5555-5555-000000000005', 'Technology', 'Programming, gadgets, and innovation', '11111111-1111-1111-1111-111111111111'),
('66666666-6666-6666-6666-000000000006', 'Travel', 'Adventure, trips, and destinations', '11111111-1111-1111-1111-111111111111'),
('77777777-7777-7777-7777-000000000007', 'Lifestyle', 'Home, habits, and personal growth', '11111111-1111-1111-1111-111111111111'),
('88888888-8888-8888-8888-000000000008', 'Business', 'Entrepreneurship, startups, and finance', '11111111-1111-1111-1111-111111111111'),
('99999999-9999-9999-9999-000000000009', 'Education', 'Learning, courses, and resources', '11111111-1111-1111-1111-111111111111'),
('aaaaaaaa-aaaa-aaaa-aaaa-00000000000a', 'Entertainment', 'Movies, games, and hobbies', '11111111-1111-1111-1111-111111111111');
