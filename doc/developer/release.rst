Releasing a new version
==========================

#. Create a release branch::

	$ git checkout -b release-[NUMBER] develop
	[modifications]
	$ git push -u origin release-[NUMBER]

#. Prepare the release with maven::

	$ mvn release:prepare


