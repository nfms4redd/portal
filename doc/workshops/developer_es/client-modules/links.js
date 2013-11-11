define([ "jquery", "message-bus", "i18n" ], function($, bus, i18n) {
	var linkBar = $("<div/>");
	linkBar.attr("id", "link-bar");
	
	var linkMinisterio = $("<a/>").appendTo(linkBar);
	linkMinisterio.html(i18n.ministerio_ambiente);
	linkMinisterio.attr("href", "http://www.ambiente.gob.ec/");
	linkMinisterio.attr("target", "_blank");

	var linkNFMS = $("<a/>").appendTo(linkBar);
	linkNFMS.html(i18n.nfms);
	linkNFMS.attr("href", "http://nfms4redd.org/");
	linkNFMS.attr("target", "_blank");

	$("body").append(linkBar);
});

/*
To be placed in the messages_xx.properties

ministerio_ambiente=Minist\u00e8re \u00e9quatorien de l'environnement
nfms=Syst\u00e8me National de Surveillance des For\u00eats

ministerio_ambiente=Ministerio de ambiente de Ecuador
nfms=Sistema Nacional de Monitoreo de Bosques

ministerio_ambiente=Ministry of the Environment of Ecuador
nfms=National Forest Monitoring System
*/