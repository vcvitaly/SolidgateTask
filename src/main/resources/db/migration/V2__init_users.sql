INSERT INTO users(id, name, balance)
SELECT i, CONCAT('user', i::TEXT), 0 FROM generate_series(1, 1000000) AS s(i);