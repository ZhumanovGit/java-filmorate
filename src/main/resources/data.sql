MERGE INTO genre AS g USING (SELECT 1 AS id, 'Комедия' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (1, 'Комедия');
MERGE INTO genre AS g USING (SELECT 2 AS id, 'Драма' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (2, 'Драма');
MERGE INTO genre AS g USING (SELECT 3 AS id, 'Мультфильм' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (3, 'Мультфильм');
MERGE INTO genre AS g USING (SELECT 4 AS id, 'Триллер' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (4, 'Документальный');
MERGE INTO genre AS g USING (SELECT 5 AS id, 'Документальный' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (5, 'Триллер');
MERGE INTO genre AS g USING (SELECT 6 AS id, 'Боевик' AS name) AS gs ON g.id = gs.id AND g.name = gs.name WHEN NOT MATCHED THEN INSERT (id, name) VALUES (6, 'Боевик');

/*INSERT INTO genre (name) VALUES ('Комедия');
INSERT INTO genre (name) VALUES ('Драма');
INSERT INTO genre (name) VALUES ('Мультфильм');
INSERT INTO genre (name) VALUES ('Триллер');
INSERT INTO genre (name) VALUES ('Документальный');
INSERT INTO genre (name) VALUES ('Боевик');*/

MERGE INTO ratingMPA AS r USING (SELECT 1 AS id, 'G' AS rating_name) AS gs ON g.id = gs.id AND g.rating_name = gs.rating_name WHEN NOT MATCHED THEN INSERT (id, raing_name) VALUES (1, 'G');
MERGE INTO ratingMPA AS r USING (SELECT 2 AS id, 'PG' AS rating_name) AS gs ON g.id = gs.id AND g.rating_name = gs.rating_name WHEN NOT MATCHED THEN INSERT (id, raing_name) VALUES (2, 'PG');
MERGE INTO ratingMPA AS r USING (SELECT 3 AS id, 'PG-13' AS rating_name) AS gs ON g.id = gs.id AND g.rating_name = gs.rating_name WHEN NOT MATCHED THEN INSERT (id, raing_name) VALUES (3, 'PG-13');
MERGE INTO ratingMPA AS r USING (SELECT 4 AS id, 'R' AS rating_name) AS gs ON g.id = gs.id AND g.rating_name = gs.rating_name WHEN NOT MATCHED THEN INSERT (id, raing_name) VALUES (4, 'R');
MERGE INTO ratingMPA AS r USING (SELECT 5 AS id, 'NC-17' AS rating_name) AS gs ON g.id = gs.id AND g.rating_name = gs.rating_name WHEN NOT MATCHED THEN INSERT (id, raing_name) VALUES (5, 'NC-17');

/*INSERT INTO ratingMPA (rating_name) VALUES ('G');
INSERT INTO ratingMPA (rating_name) VALUES ('PG');
INSERT INTO ratingMPA (rating_name) VALUES ('PG-13');
INSERT INTO ratingMPA (rating_name) VALUES ('R');
INSERT INTO ratingMPA (rating_name) VALUES ('NC-17');*/


