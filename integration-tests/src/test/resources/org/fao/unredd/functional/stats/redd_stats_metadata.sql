CREATE TABLE redd_stats_metadata (
	id serial NOT NULL,
	name character varying,
	title character varying,
	subtitle character varying,
	description character varying,
	y_label character varying,
	units character varying,
	tooltipsdecimals integer,
	-- Nombre de la capa del portal para la cual se visualizara el indicador
	layer_name character varying,
	-- Tabla con divisiones/regiones para el calculo de las estadisticas
	table_name_division character varying,
	-- Campo identificador de la tabla de divisiones, debe coincidir con el de
	-- la capa del portal en geoserver para ser visualizado
	division_field_id character varying,
	-- Tabla con clasificaciones
	class_table_name character varying,
	-- Campo de tabla de clasificaciones a utilizar
	class_field_name character varying,
	-- Campo de fecha en caso de tabla multitemporal
	date_field_name character varying,
	-- Nombre de la tabla de destino de los datos estadisticos calculados
	table_name_data character varying,
	-- Tipo de grafico: 2D, 3D
	graphic_type character varying,
	CONSTRAINT indicators_metadata_pkey PRIMARY KEY (id)
) WITH ( OIDS=FALSE )