package org.fao.unredd.functional.stats;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.fao.unredd.functional.AbstractIntegrationTest;
import org.fao.unredd.functional.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class StatsTest extends AbstractIntegrationTest {

	@Test
	public void testCalculateStats() throws Exception {
		String layerName = "unredd:provinces";
		SQLExecute("INSERT INTO " + testSchema
				+ ".redd_stats_metadata ("
				+ "name, "//
				+ "title, "//
				+ "subtitle, "//
				+ "description, "//
				+ "y_label, "//
				+ "units, "//
				+ "tooltipsdecimals, "//
				+ "layer_name, "//
				+ "table_name_division, " //
				+ "division_field_id,"//
				+ "class_table_name, " //
				+ "class_field_name," //
				+ "date_field_name," //
				+ "table_name_data," //
				+ "graphic_type" //
				+ ")VALUES("
				+ "'Cobertura forestal',"//
				+ "'Cobertura forestal',"//
				+ "'Evolución de la cobertura forestal',"//
				+ "'Muestra la evolución de la cobertura forestal a lo largo de los años',"//
				+ "'Cobertura',"//
				+ "'km²',"//
				+ "2,"//
				+ "'" + layerName + "',"//
				+ "'" + testSchema + ".stats_admin',"//
				+ "'gid',"//
				+ "'" + testSchema + ".stats_cobertura',"//
				+ "'clasificacion',"//
				+ "'fecha',"//
				+ "'" + testSchema + ".stats_results',"//
				+ "'2D'"//
				+ ")");
		Integer indicatorId = (Integer) SQLQuery("select id from " + testSchema
				+ ".redd_stats_metadata;");
		SQLExecute("SELECT redd_stats_run(" + indicatorId + ", '" + testSchema
				+ "');");

		// Check total coverage
		Float sum = (Float) SQLQuery("SELECT sum(valor) from " + testSchema
				+ ".stats_results");
		assertTrue(Math.abs(sum - 0.0015) < 0.00001);

		// Get indicators must return 1 entry
		CloseableHttpResponse ret = GET("indicators", "layerId", layerName);
		assertEquals(200, ret.getStatusLine().getStatusCode());
		JSONArray indicators = (JSONArray) JSONSerializer.toJSON(IOUtils
				.toString(ret.getEntity().getContent()));
		assertEquals(indicators.size(), 1);

		ret = GET("indicator", "indicatorId", indicators.getJSONObject(0)
				.getString("id"), "layerId", layerName, "objectId", "1");
		assertEquals(200, ret.getStatusLine().getStatusCode());
		assertTrue(ret.getEntity().getContentType().getValue()
				.contains("text/html"));
	}

	@Test
	@Ignore
	public void testIndicators() throws Exception {
		SQLExecute("INSERT INTO " + testSchema
				+ ".indicators_metadata ("
				+ "name, "//
				+ "title, "//
				+ "subtitle, "//
				+ "description, "//
				+ "y_label, "//
				+ "units, "//
				+ "tooltipsdecimals, "//
				+ "layer_name, "//
				+ "table_name_division, " //
				+ "division_field_id,"//
				+ "graphic_type"//
				+ ")VALUES("
				+ "'Cobertura forestal',"//
				+ "'Cobertura forestal',"//
				+ "'Evolución de la cobertura forestal',"//
				+ "'Muestra la evolución de la cobertura forestal a lo largo de los años',"//
				+ "'Cobertura',"//
				+ "'km²',"//
				+ "2,"//
				+ "'unredd:drc_provinces',"//
				+ "'" + testSchema + ".prueba_admin',"//
				+ "'gid',"//
				+ "'2D'"//
				+ ")");
		SQLExecute("CREATE TABLE " + testSchema + ".cobertura_forestal ("
				+ "division_id varchar,"//
				+ "class varchar,"//
				+ "fecha_result date,"//
				+ "ha real"//
				+ ");");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'bosque virgen', '2010-1-1', 300);");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'bosque nativo', '2010-1-1', 400);");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'no bosque', '2010-1-1', 500);");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'bosque virgen', '2010-1-2', 350);");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'bosque nativo', '2010-1-2', 300);");
		SQLExecute("INSERT INTO " + testSchema + ".cobertura_forestal VALUES ("
				+ "'1', 'no bosque', '2010-1-2', 200);");
		fail();
	}
}
