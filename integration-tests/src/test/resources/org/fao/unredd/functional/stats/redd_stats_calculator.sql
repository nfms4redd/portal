DROP FUNCTION IF EXISTS redd_stats_run (integer, varchar) ;
---
CREATE OR REPLACE FUNCTION redd_stats_run(IN indicators_id integer, IN dbSchema varchar)
  RETURNS bool AS
$BODY$
DECLARE
	indicador RECORD;
BEGIN

FOR indicador IN EXECUTE format('SELECT * FROM %s.redd_stats_metadata WHERE id=%s', dbSchema, indicators_id) LOOP
	RAISE NOTICE 'Procesando grafico %', indicador.title;

	EXECUTE format('DROP TABLE IF EXISTS %s',indicador.table_name_data);
	
	RAISE NOTICE 'Generando tabla de datos %',indicador.title;
	EXECUTE format('CREATE TABLE %s AS SELECT * FROM redd_stats_calculo(''%s.redd_stats_fajas'',''%s'',''%s'',''%s'',''%s'',''%s'')',
		indicador.table_name_data, dbSchema, indicador.table_name_division, indicador.division_field_id, indicador.class_table_name, indicador.class_field_name, indicador.date_field_name);

	END LOOP;

	-- Devolvemos la cantidad de areas calculadas?
	RETURN true;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
---
DROP FUNCTION IF EXISTS redd_stats_calculo (text,text,text,text,text,text);
---
CREATE OR REPLACE FUNCTION redd_stats_calculo(IN fajas_table_name text, IN divisions_table_name text, IN division_id_field_name text, IN classification_table_name text, IN class_field_name text, IN date_field_name text)
  RETURNS TABLE(division_id character varying, variable character varying, fecha date, valor real) AS
$BODY$
DECLARE
	faja RECORD;
	faja_geom geometry;
BEGIN
	DROP TABLE IF EXISTS redd_stats_tmp_areas;
	CREATE TEMP TABLE redd_stats_tmp_areas (division_id varchar, class varchar,fecha_dato date, ha real);
    
	FOR faja IN EXECUTE format('SELECT * FROM %s', fajas_table_name) LOOP

		RAISE NOTICE 'procesando faja %', faja.srid;

		faja_geom := ST_Transform(faja.geom, faja.srid::integer);

		-- Transformamos las divisiones y la clasificacion al CRS de la faja
		RAISE NOTICE 'proyectando al crs de la faja';
		EXECUTE format('CREATE TABLE classification_projected AS SELECT %s as clase, %s as fecha_dato, ST_Transform(geom, %s) AS geom FROM %s', class_field_name,date_field_name, faja.srid, classification_table_name);
		EXECUTE format('CREATE TABLE divisions_projected AS      SELECT %s as    id, ST_Transform(geom, %s) AS geom FROM %s', division_id_field_name, faja.srid, divisions_table_name);

		RAISE NOTICE 'cortando clasificación y divisiones con geometría de la faja';


		-- Cortampos las divisiones y la clasificación (ya proyectadas) con la geometría de la faja proyectada al SRID correspondiente
		CREATE TABLE classification_cut AS SELECT clase,fecha_dato, ST_Intersection(geom, faja_geom) AS geom FROM classification_projected WHERE ST_Intersects(geom, faja_geom);
		CREATE TABLE divisions_cut AS      SELECT    id, ST_Intersection(geom, faja_geom) AS geom FROM divisions_projected      WHERE ST_Intersects(geom, faja_geom);

		RAISE NOTICE 'intersectando divisiones y clasificacion';
		-- Intersectamos divisiones y clasificacio
		CREATE TABLE intersection AS SELECT ST_Intersection(c.geom, d.geom) AS geom, d.id, c.clase, c.fecha_dato FROM classification_cut c, divisions_cut d WHERE ST_Intersects(c.geom, d.geom);

		RAISE NOTICE 'acumulando areas';
		-- Calculamos el área para cada geometría
		INSERT INTO redd_stats_tmp_areas (division_id, class, fecha_dato, ha) SELECT id, clase, fecha_dato, ST_Area(geom) / 10000 AS ha FROM intersection;

		-- Eliminamos las tablas
		DROP TABLE classification_projected, divisions_projected, classification_cut, divisions_cut, intersection;
	END LOOP;

	-- Devolvemos la suma del área de las geometrías para cada departamento y clasificación de cobertura
	RETURN QUERY SELECT ta.division_id, ta.class as variable, ta.fecha_dato as fecha, sum(ta.ha) as valor FROM redd_stats_tmp_areas ta GROUP BY ta.division_id, ta.fecha_dato, ta.class ORDER BY ta.division_id, ta.class, ta.fecha_dato;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
