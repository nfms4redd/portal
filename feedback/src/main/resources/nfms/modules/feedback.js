define([ "message-bus", "customization", "map", "toolbar", "i18n", "jquery", "jquery-ui", "openlayers", "edit-controls" ],//
function(bus, customization, map, toolbar, i18n, $) {

	var feedbackLayers = new Array();

	// Dialog controls
	var dlg;
	var cmbLayer;
	var lblTimestamp;
	var txtEmail;
	var txtComment;
	var editToolbar;

	var feedbackLayer = new OpenLayers.Layer.Vector("Feedback");

	var btn = $("<a/>").attr("id", "feedback-button").addClass("blue_button").html("Feedback");

	var initializeDialog = function() {
		dlg = $("<div/>").attr("id", "feedback_popup");
		$("<label/>").addClass("feedback-form-left").html("Capa:").appendTo(dlg);
		cmbLayer = $("<select/>").attr("id", "feedback-layer-combo").appendTo(dlg);
		cmbLayer.change(refreshYear);
		lblTimestamp = $("<span/>").appendTo(dlg);

		dlg.append("<br/>");
		$("<label/>").addClass("feedback-form-left").html("Drawing tools:").appendTo(dlg);
		$("<div/>").attr("id", "fb_toolbar").addClass("olControlPortalToolbar").appendTo(dlg);

		dlg.append("<br/>");
		$("<label/>").addClass("feedback-form-left").html("Email:").appendTo(dlg);
		txtEmail = $("<input/>").attr("type", "text").attr("size", "40").appendTo(dlg);

		dlg.append("<br/>");
		$("<label/>").addClass("feedback-form-left").html("Comentario:").appendTo(dlg);
		txtComment = $("<textarea/>").attr("cols", "40").attr("rows", "6").appendTo(dlg);

		dlg.append("<br/>");
		var btnClose = $("<div/>").html("Cerrar").appendTo($("<div/>").addClass("feedback-form-left").appendTo(dlg));
		btnClose.button().click(function() {
			dlg.dialog('close');
		});
		var btnSubmit = $("<div/>").html("Enviar").appendTo(dlg);
		btnSubmit.button().click(function() {
			submit();
		});

		dlg.dialog({
			autoOpen : false,
			closeOnEscape : false,
			width : "auto",
			zIndex : 2000,
			resizable : false,
			position : {
				my : "left top",
				at : "left bottom+40",
				of : btn
			},
			title : i18n["feedback_title"],
			close : deactivateFeedback
		});

		// Need to create after the dialog is in the DOM otherwise the call to
		// getElementById returns null
		editToolbar = new OpenLayers.Control.PortalToolbar(feedbackLayer, {
			div : document.getElementById("fb_toolbar")
		});

	}

	var submit = function() {
		var mailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (cmbLayer.val() == null) {
			bus.send("error", i18n["Feedback.no-layer-selected"]);
		} else if (!mailRegex.test(txtEmail.val())) {
			bus.send("error", i18n["Feedback.invalid-email-address"]);
		} else if (!editToolbar.hasFeatures()) {
			bus.send("error", i18n["Feedback.no-geometries"]);
		} else {
			// Do submit

			var data = {
				"lang" : customization.languageCode,
				"comment" : txtComment.val(),
				"geometry" : editToolbar.getFeaturesAsWKT(),
				"layerName" : cmbLayer.val(),
				"email" : txtEmail.val()
			};

			var timestamp = feedbackLayers[cmbLayer.val()].timestamp;
			if (timestamp != null) {
				data.date = timestamp.getDate() + "/" + (timestamp.getMonth() + 1) + "/" + timestamp.getFullYear();
			}

			bus.send("show-wait-mask", i18n["Feedback.wait"]);

			bus.send("ajax", {
				type : 'POST',
				url : 'create-comment?',
				data : data,
				success : function(data, textStatus, jqXHR) {
					bus.send("info", i18n["Feedback.verify_mail_sent"]);
					dlg.dialog('close');
				},
				errorMsg : i18n["Feedback.submit_error"],
				complete : function() {
					bus.send("hide-wait-mask");
				}
			});
		}
	}

	var activateFeedback = function() {
		if (!btn.hasClass("selected")) {
			if (cmbLayer.find("option").length == 0) {
				bus.send("error", i18n["Feedback.no_layer_visible"]);
			} else {
				$("#button_feedback").addClass('selected');
				txtEmail.val("");
				txtComment.val("");
				bus.send("activate-exclusive-control", editToolbar);
				map.addLayer(feedbackLayer);
				dlg.dialog("open");
			}
		}
	}

	var deactivateFeedback = function() {
		feedbackLayer.removeAllFeatures();
		bus.send("activate-default-exclusive-control");
		editToolbar.deactivate();
		map.removeLayer(feedbackLayer);
		$("#button_feedback").removeClass('selected');
		dlg.dialog("close");
	}

	var refreshYear = function() {
		var text = "";
		var selectedLayer = feedbackLayers[cmbLayer.val()];
		if (selectedLayer != null) {
			timestamp = selectedLayer["timestamp"];
			if (timestamp != null) {
				text = timestamp.getUTCFullYear();
			}
		}
		lblTimestamp.html(text);
	}

	initializeDialog();

	// Install feedback button
	btn.appendTo(toolbar);
	btn.click(function() {
		activateFeedback();
		return false;
	});

	bus.listen("activate-feedback", activateFeedback);
	bus.listen("deactivate-feedback", function() {
		// Enough, the close listener will clean up, as when manually closed
		dlg.dialog("close");
	});

	// Listen events
	bus.listen("layer-visibility", function(event, layerId, visibility) {
		if (layerId in feedbackLayers) {
			feedbackLayers[layerId].visibility = visibility;
			var currentValue = cmbLayer.val();
			cmbLayer.empty();
			for (layerId in feedbackLayers) {
				var layerInfo = feedbackLayers[layerId];
				if (layerInfo["visibility"]) {
					$("<option/>").attr("value", layerId).html(layerInfo.name).appendTo(cmbLayer);
				}
			}
			if (currentValue != null && cmbLayer.find("option[value='" + currentValue + "']").length > 0) {
				cmbLayer.val(currentValue);
			} else {
				var firstOption = cmbLayer.find("option:first").val();
				cmbLayer.val(firstOption);
			}
		}
	});

	bus.listen("reset-layers", function() {
		feedbackLayers = new Array();
	});

	bus.listen("add-layer", function(event, portalLayer) {
		if (portalLayer.isFeedbackEnabled()) {
			feedbackLayers[portalLayer.getId()] = {
				name : portalLayer.getName(),
				visibility : false
			};
		}
	});

	bus.listen("layer-timestamp-selected", function(event, layerId, timestamp) {
		if (layerId in feedbackLayers) {
			feedbackLayers[layerId].timestamp = timestamp;
			refreshYear();
		}
	});
});