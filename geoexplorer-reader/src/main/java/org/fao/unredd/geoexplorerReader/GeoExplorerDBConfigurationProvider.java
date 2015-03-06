package org.fao.unredd.geoexplorerReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.fao.unredd.portal.ConfigurationException;
import org.fao.unredd.portal.DBUtils;
import org.fao.unredd.portal.ModuleConfigurationProvider;
import org.fao.unredd.portal.PersistenceException;
import org.fao.unredd.portal.PortalRequestConfiguration;

public class GeoExplorerDBConfigurationProvider implements
		ModuleConfigurationProvider {

	public GeoExplorerDBConfigurationProvider() {
	}

	@Override
	public Map<String, JSONObject> getConfigurationMap(
			PortalRequestConfiguration configurationContext,
			HttpServletRequest request) throws IOException {
		Map<String, JSONObject> ret = new HashMap<String, JSONObject>();
		try {
			String mapIdParameter = request.getParameter("mapId");
			int mapId;
			if (mapIdParameter != null) {
				try {
					mapId = Integer.parseInt(mapIdParameter);
				} catch (NumberFormatException e) {
					throw new ConfigurationException("mapId must be an integer");
				}
			} else {
				throw new ConfigurationException(
						"mapId parameter must be configured");
			}
			ret.put("geoexplorer-layers", getGeoExplorerLayers(mapId));
		} catch (PersistenceException e) {
			throw new ConfigurationException(
					"Cannot read geoexplorer database", e);
		}
		return ret;
	}

	private JSONObject getGeoExplorerLayers(final int mapId)
			throws PersistenceException {
		String config = DBUtils.processConnection("geoexplorer",
				new DBUtils.ReturningDBProcessor<String>() {

					@Override
					public String process(Connection connection)
							throws SQLException {
						PreparedStatement statement = connection
								.prepareStatement("select config from maps where id=?");
						statement.setInt(1, mapId);
						ResultSet rs = statement.executeQuery();
						if (rs.next()) {
							return rs.getString(1);
						} else {
							return null;
						}
					}
				});

		return (JSONObject) JSONSerializer.toJSON(config);
	}

}
