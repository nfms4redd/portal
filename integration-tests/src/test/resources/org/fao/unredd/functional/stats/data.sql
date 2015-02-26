drop table if exists $schemaName.stats_admin;
create table $schemaName.stats_admin (gid integer, geom geometry(POLYGON, 4326));
insert into $schemaName.stats_admin VALUES (1, ST_Transform(ST_geomFromText('POLYGON((0 0, 2 0, 2 2, 0 2, 0 0))', 900913), 4326));
insert into $schemaName.stats_admin VALUES (2, ST_Transform(ST_geomFromText('POLYGON((2 0, 4 0, 4 2, 2 2, 2 0))', 900913), 4326));
insert into $schemaName.stats_admin VALUES (3, ST_Transform(ST_geomFromText('POLYGON((0 2, 2 2, 2 4, 0 4, 0 2))', 900913), 4326));
insert into $schemaName.stats_admin VALUES (4, ST_Transform(ST_geomFromText('POLYGON((2 2, 4 2, 4 4, 2 4, 2 2))', 900913), 4326));

drop table if exists $schemaName.stats_cobertura;
create table $schemaName.stats_cobertura (gid serial, clasificacion varchar, fecha date, geom geometry(POLYGON, 4326));
insert into $schemaName.stats_cobertura (clasificacion, fecha, geom) VALUES ('a', '2000-01-01', ST_Transform(ST_geomFromText('POLYGON((0 0, 3 0, 3 3, 0 3, 0 0))', 900913), 4326));
insert into $schemaName.stats_cobertura (clasificacion, fecha, geom) VALUES ('a', '2005-01-01', ST_Transform(ST_geomFromText('POLYGON((0 0, 2 0, 2 3, 0 3, 0 0))', 900913), 4326));