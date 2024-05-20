--Добавление юзера и индивидуала
WITH
    new_country AS (INSERT INTO person.countries (created, updated, name, alpha2, alpha3, status)
    VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'RUSSIA', 'RU', 'RUS', 'ACTIVE')
    RETURNING id),
    new_address AS ( INSERT INTO person.addresses (created, updated, country_id, address, zip_code, archived, city, state)
    VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM new_country), '123 Main St', '12345', CURRENT_TIMESTAMP, 'Anytown', 'CA')
    RETURNING id),
    new_user AS (INSERT INTO person.users (secret_key, created, updated, first_name, last_name, verified_at, archived_at, status, filled, address_id)
    VALUES ('your_secret_key', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', TRUE,(SELECT id from new_address))
    RETURNING id)
    INSERT INTO person.individuals (user_id, created, updated, passport_number, phone_number, email, verified_at, archived_at, status)
    VALUES ((SELECT id from new_user),CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'passport_number_value','1234567890', 'john.doe@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'ACTIVE');