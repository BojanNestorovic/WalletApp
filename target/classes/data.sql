-- Initial data for the Wallet App

-- Insert currencies
INSERT INTO currencies (name, value_to_eur) VALUES 
('RSD', 0.0085),
('EUR', 1.0),
('USD', 0.85),
('GBP', 1.17),
('CHF', 0.92);

-- Insert predefined categories for income
INSERT INTO categories (name, type, predefined, user_id) VALUES 
('Plata', 'INCOME', true, NULL),
('Bonus', 'INCOME', true, NULL),
('Investicije', 'INCOME', true, NULL),
('Pokloni', 'INCOME', true, NULL),
('Ostali prihodi', 'INCOME', true, NULL);

-- Insert predefined categories for expenses
INSERT INTO categories (name, type, predefined, user_id) VALUES 
('Hrana', 'EXPENSE', true, NULL),
('Zabava', 'EXPENSE', true, NULL),
('Domaćinstvo', 'EXPENSE', true, NULL),
('Transport', 'EXPENSE', true, NULL),
('Zdravlje', 'EXPENSE', true, NULL),
('Odeća', 'EXPENSE', true, NULL),
('Edukacija', 'EXPENSE', true, NULL),
('Putovanja', 'EXPENSE', true, NULL),
('Kirija', 'EXPENSE', true, NULL),
('Računi', 'EXPENSE', true, NULL),
('Ostali troškovi', 'EXPENSE', true, NULL);
