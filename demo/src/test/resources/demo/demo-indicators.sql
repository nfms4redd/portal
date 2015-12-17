CREATE SCHEMA indicators;

CREATE TABLE indicators.indicators_metadata (
	id identity PRIMARY KEY, 
	name varchar(255), 
	title varchar(255), 
	subtitle varchar(255), 
	description varchar(255), 
	y_label varchar(255), 
	units varchar(255), 
	tooltipsdecimals integer, 
	layer_name varchar(255), 
	table_name_division varchar(255), 
	division_field_id varchar(255), 
	class_table_name varchar(255), 
	class_field_name varchar(255), 
	date_field_name varchar(255), 
	table_name_data varchar(255)
);

INSERT INTO indicators.indicators_metadata VALUES (
	null, 
	'primero', 
	'Gráfico 1', 
	'Gráfico de deforestación', 
	'Aca va la descripcion del grafico', 
	'Valores absolutos', 
	'Ha.', 
	2, 
	'unredd:drc_provinces', 
	'gis.departamentos', 
	'OBJECTID', 
	'stb_wgs84', 
	'clasificacion', 
	'fecha', 
	'indicators.stb_cobertura'
);

CREATE TABLE indicators.stb_cobertura (
	division_id varchar(255),
	class varchar(255),
	fecha_result date,
	ha real,
	objectid integer
);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 1);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 1);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 2);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 2);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 3);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 3);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 4);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 4);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 5);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 5);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 6);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 6);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 7);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 7);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 8);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 8);

INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2006-01-01', 238.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '2002-01-01', 200.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Tierras forestales', '1998-01-01', 178.484, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2006-01-01', 38.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '2002-01-01', 48.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras forestales', '1998-01-01', 68.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2006-01-01', 78.4, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '2002-01-01', 88.884, 9);
INSERT INTO indicators.stb_cobertura VALUES ('10021', 'Otras tierras', '1998-01-01', 80.884, 9);





