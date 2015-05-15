CREATE TABLE redd_stats_charts (
	id serial PRIMARY KEY,
	title character varying,
	subtitle character varying,
	layer_name character varying,
	division_field_id character varying,
	table_name_data character varying,
	data_table_id_field character varying,
	data_table_date_field character varying,
	data_table_date_field_format character varying
) WITH ( OIDS=FALSE );

CREATE TABLE redd_stats_variables (
	id serial NOT NULL PRIMARY KEY,
	chart_id integer,
	y_label character varying,
	units character varying,
	tooltipsdecimals integer,
	variable_name character varying,
	data_table_variable_field character varying,
	graphic_type character varying
);
