define([ "message-bus", "layout", "jquery", "toolbar" ], function(bus, layout, $) {

	var groups = [];

	bus.listen("add-group", function(evt, groupData) {
		groups.push(groupData);
	});

	var btnNewLayer = $("<a href='#'/>").html("Nueva capa").appendTo($("#" + layout.toolbarId));
	btnNewLayer.attr("id", "new-layer-button");
	btnNewLayer.addClass("blue_button lang_button");
	btnNewLayer.click(function() {
		var div = $("<div/>");
		var form = $("<form/>").appendTo(div);
		var fieldset = $("<fieldset/>").appendTo(form);
		fieldset.append($("<label/>").attr("for", "layerName").html("Nombre:"));
		var txtLayerName = $("<input/>").attr("name", "layerName").attr("type", "text");
		txtLayerName.addClass("add-layer-control text ui-widget-content ui-corner-all");
		fieldset.append(txtLayerName);
		fieldset.append($("<label/>").attr("for", "url").html("URL Servidor:"));
		var txtURL = $("<input/>").attr("name", "url").attr("type", "text");
		txtURL.val("http://mesonet.agron.iastate.edu/cgi-bin/wms/nexrad/n0r-t.cgi?");
		txtURL.addClass("add-layer-control text ui-widget-content ui-corner-all");
		fieldset.append(txtURL);
		fieldset.append($("<label/>").attr("for", "wmsName").html("Nombre WMS:"));
		var txtWMSName = $("<input/>").attr("name", "wmsName").attr("type", "text");
		txtWMSName.val("nexrad-n0r-wmst");
		txtWMSName.addClass("add-layer-control text ui-widget-content ui-corner-all");
		fieldset.append(txtWMSName);
		fieldset.append($("<label/>").attr("for", "groupId").html("Grupo:"));
		var cmbGroups = $("<select/>").attr("name", "groupId");
		cmbGroups.addClass("add-layer-control ui-widget-content ui-corner-all");
		for (var i = 0; i < groups.length; i++) {
			var optGroup = $("<option/>").attr("value", groups[i].id).html(groups[i].name);
			cmbGroups.append(optGroup);
		}
		fieldset.append(cmbGroups);
		div.dialog({
			height : "auto",
			width : 350,
			modal : true,
			buttons : {
				"Add new layer" : function() {
					bus.send("add-layer", {
						"id" : "unique-id-" + (new Date()).getTime(),
						"groupId" : cmbGroups.val(),
						"url" : txtURL.val(),
						"wmsName" : txtWMSName.val(),
						"name" : txtLayerName.val(),
						"visible" : "true"
					});
					$(this).dialog("close");
				},
				Cancel : function() {
					$(this).dialog("close");
				}
			}
		});
	});

});