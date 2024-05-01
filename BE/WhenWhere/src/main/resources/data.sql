INSERT INTO authority (authority_name)
SELECT 'ROLE_USER' AS authority_name
WHERE NOT EXISTS (SELECT 1 FROM authority);

INSERT INTO authority (authority_name)
SELECT 'ROLE_ADMIN' AS authority_name
WHERE NOT EXISTS (SELECT 1 FROM authority);

INSERT INTO authority (authority_name)
SELECT 'ROLE_HOST' AS authority_name
WHERE NOT EXISTS (SELECT 1 FROM authority);