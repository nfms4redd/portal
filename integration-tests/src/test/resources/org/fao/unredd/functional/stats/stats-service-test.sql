-- Chart
INSERT INTO integration_tests.redd_stats_charts VALUES (
	default,
    'Cobertura forestal',
    'Evolución de la cobertura forestal por provincia',
    'bosques:provincias',
    'id_provinc',
    'integration_tests.cobertura_forestal_provincias',
    'pcia_id',
    'anio',
    null
);

-- Variables in chart
INSERT INTO integration_tests.redd_stats_variables VALUES (
	default,
	(select currval('integration_tests.redd_stats_charts_id_seq')),
	'Cobertura',
	'Hectáreas',
	2,
	'Bosque nativo',
	'sup_nativo',
	'line'
);
INSERT INTO integration_tests.redd_stats_variables VALUES (
	default,
	(select currval('integration_tests.redd_stats_charts_id_seq')),
    'Cobertura',
    'Hectáreas',
    2,
	'Bosque cultivado',
    'sup_cultivado',
    'line'
);

-- Data
CREATE TABLE integration_tests.cobertura_forestal_provincias(
    pcia_id varchar,
    sup_nativo real,
    sup_cultivado real,
    anio date
);

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 100, 1000, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 98, 1100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 78, 1050, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 590, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', null, 0, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 208, 50, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 2000, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 2300, 100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 2500, 50, '1/1/2005');
