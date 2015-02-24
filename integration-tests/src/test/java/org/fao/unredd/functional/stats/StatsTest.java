package org.fao.unredd.functional.stats;

import static junit.framework.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

import org.fao.unredd.functional.AbstractIntegrationTest;
import org.fao.unredd.functional.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class StatsTest extends AbstractIntegrationTest {

	@Before
	public void installFunctions() throws IOException, SQLException {
		executeStatement("stats-calculation-function-creation.sql",
				"schemaName", testSchema);
		executeStatement("stats-runner-function-creation.sql", "schemaName",
				testSchema);
		executeScript("data.sql", "schemaName", testSchema);
	}

	@Test
	public void testCalculateStats() throws Exception {
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
				+ "'unredd:drc_provinces',"//
				+ "'" + testSchema + ".stats_admin',"//
				+ "'gid',"//
				+ "'" + testSchema + ".stats_cobertura',"//
				+ "'clasificacion',"//
				+ "'fecha',"//
				+ "'" + testSchema + ".stats_results',"//
				+ "'2D'"//
				+ ")");
		SQLExecute("SELECT generar_stats(1);");

		fail();
	}

	@Test
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
