CREATE OR REPLACE FUNCTION generar_stats(IN indicators_id integer)
  RETURNS bool AS
$BODY$
DECLARE
	indicador RECORD;
BEGIN

FOR indicador IN EXECUTE format('SELECT * FROM $schemaName.indicators_metadata WHERE id=%s', indicators_id) LOOP
	RAISE NOTICE 'Procesando grafico %', indicador.name;

	EXECUTE format('DROP TABLE IF EXISTS %s',indicador.table_name_data);
	
	RAISE NOTICE 'Generando tabla de datos %',indicador.title;
	EXECUTE format('CREATE TABLE %s AS SELECT * FROM calculo_cobertura(''$schemaName.stats_fajas'',''%s'',''%s'',''%s'',''%s'',''%s'')',
		indicador.table_name_data, indicador.table_name_division, indicador.division_field_id, indicador.class_table_name, indicador.class_field_name, indicador.date_field_name);

	END LOOP;

	-- Devolvemos la cantidad de areas calculadas?
	RETURN true;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
