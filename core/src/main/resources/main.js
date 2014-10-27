require.config({
	baseUrl : "modules",
	// uncomment this line for debugging purposes in order to bust cache
	urlArgs : "bust=" + (new Date()).getTime(),
	$paths,
	$shim,
	waitSeconds : 15
});

require([ "customization" ]);
