define([ "module" ], function(module) {
	
	var translations = module.config();
	document.title = translations["title"];
	
	return translations;
});