Building the system
=====================

Getting the code
------------------

The nfms4redd code is hosted on github. To get a local copy using git, run::

  git clone https://github.com/nfms4redd/nfms.git


Building
--------

Project build process is managed by Maven 2. The project is a multimodule maven project containing the dissemination portal, the staging area Use this command to build, test and create the deployment web archive (war file)::

  mvn install
  
Then get the file in :file:`target/unredd-portal.war`.

Maven can be used also to run the portal. Use::

  mvn jetty:run
  
The portal will be accessible at:

  http://localhost:8181/portal/

To use a custom configuration, edit the :file:`pom.xml` file, and change the ``<portal_config_dir>`` property before running jetty.

.. _debugging_portal_eclipse:

Debugging in eclipse
---------------------

In order to start the application in a J2EE container from eclipse, and therefore being able to debug, it is just necessary to activate the Web Tools Platform support (WTP). By default it is configured on the pom.xml file of the J2EE related projects. Otherwise the -Dwtpversion=2.0 option can be specified in the mvn eclipse:eclipse command::

	$ mvn eclipse:eclipse -Dwtpversion=2.0 

After it, on an eclipse with support for J2EE applications it is possible to right-click the project and select "Debug as" > "Debug on server".

I've experienced some ClassNotFoundExceptions about classes existing on projects referenced by this one. Similar issues has been reported here:

* http://www.eclipse.org/forums/index.php/t/248873/

With no solution yet: https://bugs.eclipse.org/bugs/show_bug.cgi?id=370839

Removing and adding again the dependent project in the "Deployment Assembly" tab of the project properties fixes the issue. I wanted to compare the files in .settings to see the differences before and after, but no luck, the bug is no longer reproducible even after a "mvn eclipse:clean eclipse:eclipse" or after removing the .settings folder.

Running tests
------------------

Two profiles are configured in the pom.xml, one is "unit" and is active by default, excludes the tests
marked with the category "org.fao.test.FunctionalTestSuite". This is an interface
defined in the nfms-utils/common-tests with the only purpose of marking tests as "functional", this is, as
using the system from a functional point of view and probably requiring interaction with other components of
the system (geoserver, geobatch, etc.).

Therefore, by issuing::

	$ mvn test
	
Only the tests that does not require external applications will be run.

The other profile is "all-tests" and does not exclude any test from the build. In order to activate this profile, it
is possible to issue the command::

	$ mvn test -P all-tests
