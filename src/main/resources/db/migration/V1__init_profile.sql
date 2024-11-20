INSERT INTO profiles
(id, name, surname, email, password, photo_id, role, status, created_date, visible)
VALUES (2,
        'Admin',
        'Admin',
        'admin@gmail.com',
        '$2a$10$fnTizqtnjzeg4yiwun4Y0OXOaR7Y8hsyRpHsNkdmKglC2S0nIzJNK',
        null, -- Assuming no photo is attached
        'ROLE_ADMIN',
        'ACTIVE',
        now(), -- Current timestamp
        true -- Visible is set to true
       );
