drop table if exists $schemaName.stats_admin;
create table $schemaName.stats_admin (gid serial, geom geometry(POLYGON, 4326));
insert into $schemaName.stats_admin (geom) VALUES (ST_Transform(ST_geomFromText('POLYGON((0 0, 2 0, 2 2, 0 2, 0 0))', 900913), 4326));
insert into $schemaName.stats_admin (geom) VALUES (ST_Transform(ST_geomFromText('POLYGON((2 0, 4 0, 4 2, 2 2, 2 0))', 900913), 4326));
insert into $schemaName.stats_admin (geom) VALUES (ST_Transform(ST_geomFromText('POLYGON((0 2, 2 2, 2 4, 0 4, 0 2))', 900913), 4326));
insert into $schemaName.stats_admin (geom) VALUES (ST_Transform(ST_geomFromText('POLYGON((2 2, 4 2, 4 4, 2 4, 2 2))', 900913), 4326));

drop table if exists $schemaName.stats_cobertura;
create table $schemaName.stats_cobertura (gid serial, clasificacion varchar, fecha date, geom geometry(POLYGON, 4326));
insert into $schemaName.stats_cobertura (clasificacion, fecha, geom) VALUES ('a', '2000-01-01', ST_Transform(ST_geomFromText('POLYGON((0 0, 3 0, 3 3, 0 3, 0 0))', 900913), 4326));
insert into $schemaName.stats_cobertura (clasificacion, fecha, geom) VALUES ('a', '2005-01-01', ST_Transform(ST_geomFromText('POLYGON((0 0, 2 0, 2 3, 0 3, 0 0))', 900913), 4326));

-- Fajas en 900913, no tiene ningún sentido, es suficiente con que la única faja contenga todos los datos de arriba
drop table if exists $schemaName.stats_fajas;
create table $schemaName.stats_fajas (gid serial, srid integer, geom geometry(POLYGON, 4326));
insert into $schemaName.stats_fajas (srid, geom) VALUES (900913, ST_Transform(ST_geomFromText('POLYGON((-10 -10, 10 -10, 10 10, -10 10, -10 -10))', 900913), 4326));
