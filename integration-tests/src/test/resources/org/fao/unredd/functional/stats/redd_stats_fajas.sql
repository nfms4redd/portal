-- Fajas en 900913, no tiene ningún sentido, es suficiente con que la única faja contenga todos los datos
CREATE TABLE redd_stats_fajas (gid serial, srid integer, geom geometry(POLYGON, 4326));
INSERT INTO redd_stats_fajas (srid, geom) VALUES (900913, ST_Transform(ST_geomFromText('POLYGON((-10 -10, 10 -10, 10 10, -10 10, -10 -10))', 900913), 4326));
