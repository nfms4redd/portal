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
	'Número de incendios',
	'',
	0,
	'Número de incendios',
	'numinc',
	'line'
);
INSERT INTO integration_tests.redd_stats_variables VALUES (
	default,
	(select currval('integration_tests.redd_stats_charts_id_seq')),
    'Superficie',
    'Hectáreas',
    2,
	'Superficie incendiada',
    'supinc',
    'line'
);
INSERT INTO integration_tests.redd_stats_variables VALUES (
	default,
	(select currval('integration_tests.redd_stats_charts_id_seq')),
    'Superficie',
    'Hectáreas',
    2,
	'Cobertura forestal',
    'sup_bosque',
    'line'
);

-- Data
CREATE TABLE integration_tests.cobertura_forestal_provincias(
    pcia_id varchar,
    numinc real,
    supinc real,
    sup_bosque real,
    anio date
);

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 3, 100, 1000, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 2, 98, 1100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 1, 78, 1050, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 1000, 590, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 200, null, 0, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 10, 208, 50, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 12, 2000, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 14, 2300, 100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 16, 2500, 50, '1/1/2005');
