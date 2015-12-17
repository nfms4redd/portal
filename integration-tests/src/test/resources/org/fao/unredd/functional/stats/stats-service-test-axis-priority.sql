-- Igual que el juego de datos base, pero con las prioridades intercambiadas

-- Chart
INSERT INTO integration_tests.redd_stats_charts VALUES (
	default,
    'Cobertura forestal',
    'Evolución de la cobertura forestal por provincia',
    'bosques:provincias',
    'id_provinc',
    'name_provinc',
    'integration_tests.cobertura_forestal_provincias',
    'pcia_id',
    'anio',
    null,
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
	'line',
	2
);
INSERT INTO integration_tests.redd_stats_variables VALUES (
	default,
	(select currval('integration_tests.redd_stats_charts_id_seq')),
    'Cobertura',
    'Hectáreas',
    2,
	'Bosque cultivado',
    'sup_cultivado',
    'line',
    1
);

-- Data
CREATE TABLE integration_tests.cobertura_forestal_provincias(
    pcia_id varchar,
    n_prov varchar,
    sup_nativo real,
    sup_cultivado real,
    anio date
);

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 'Provincia 1', 100, 1000, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 'Provincia 1', 98, 1100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('1', 'Provincia 1', 78, 1050, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 'Provincia 2', 590, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 'Provincia 2', null, 0, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('2', 'Provincia 2', 208, 50, '1/1/2005');

INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 'Provincia 3', 2000, 0, '1/1/1990');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 'Provincia 3', 2300, 100, '1/1/2000');
INSERT INTO integration_tests.cobertura_forestal_provincias VALUES ('3', 'Provincia 3', 2500, 50, '1/1/2005');
