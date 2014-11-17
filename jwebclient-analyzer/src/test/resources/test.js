({
	baseUrl : "${project.build.directory}/requirejs/nfms/modules",
	$paths,
	$shim,
	out: "${basedir}/src/main/webapp/optimized/portal.js",
	name : "main",
	deps: [$deps],
})
