Releasing a new version
==========================

#. Create a release branch::

	$ git checkout -b release-[NUMBER] develop

#. Prepare the release with maven::

	$ mvn release:prepare


