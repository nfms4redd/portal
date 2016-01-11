({
	baseUrl : "${project.build.directory}/requirejs/$webResourcesDir/modules",
	$paths,
	$shim,
	out: "${basedir}/src/main/webapp/optimized/portal.js",
	name : "main",
	deps: [$deps],
	inlineText: false
})
