INSERT INTO users(id, name, balance)
SELECT i, CONCAT('user', i::TEXT), 0 FROM generate_series(1, 3) AS s(i);