Releasing a new version
==========================

#. Create a release branch::

	$ git checkout -b release-[NUMBER] develop
	[modifications]
	$ git push -u origin release-[NUMBER]

#. Prepare the release with maven::

	$ mvn release:prepare

   .. warning:: Note that during the execution of maven you will have to enter several times your github credentials.
   
   .. warning:: Also note that a new tag will be done on the release-[NUMBER] branch, which will be deleted. This tag should be removed when tagging in master after merging the release branch.
   
   

	[INFO] Scanning for projects...
	[INFO] Reactor build order:
	[INFO]   National Forest Monitoring System
	[INFO]   UNREDD Portal
	[INFO]   UNREDD Administrative Portal
	[INFO]   mosaicStats
	[INFO] ------------------------------------------------------------------------
	[INFO] Building National Forest Monitoring System
	[INFO]    task-segment: [release:prepare] (aggregator-style)
	[INFO] ------------------------------------------------------------------------
	[INFO] [release:prepare {execution: default-cli}]
	[INFO] Verifying that there are no local modifications...
	[INFO]   ignoring changes on: **/pom.xml.backup, **/release.properties, **/pom.xml.branch, **/pom.xml.next, **/pom.xml.releaseBackup, **/pom.xml.tag
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git status
	[INFO] Working directory: /tmp/nfms
	[INFO] Checking dependencies and plugins for snapshots ...
	There are still some remaining snapshot dependencies.
	: Do you want to resolve them now? (yes/no) no: : yes
	Dependency type to resolve,: specify the selection number ( 0:All 1:Project Dependencies 2:Plugins 3:Reports 4:Extensions ): (0/1/2/3) 1: :
	Dependency 'proxy:http_proxy' is a snapshot (1.0.4-SNAPSHOT)
	: Which release version should it be set to? 1.0.4: : 1.0.4-SNAPSHOT
	What version should the dependency be reset to for development? 1.0.4-SNAPSHOT: :
	What is the release version for "National Forest Monitoring System"? (org.fao.unredd:nfms) 1.0: :
	What is the release version for "UNREDD Portal"? (org.fao.unredd:portal) 1.0: :
	What is the release version for "UNREDD Administrative Portal"? (org.fao.unredd:admin-portal) 1.0: :
	What is the release version for "mosaicStats"? (org.fao.unredd:mosaicStats) 1.0: :
	What is SCM release tag or label for "National Forest Monitoring System"? (org.fao.unredd:nfms) nfms-1.0: : version-1.0
	What is the new development version for "National Forest Monitoring System"? (org.fao.unredd:nfms) 1.1-SNAPSHOT: : 2.0-SNAPSHOT
	What is the new development version for "UNREDD Portal"? (org.fao.unredd:portal) 1.1-SNAPSHOT: : 2.0-SNAPSHOT
	What is the new development version for "UNREDD Administrative Portal"? (org.fao.unredd:admin-portal) 1.1-SNAPSHOT: : 2.0-SNAPSHOT
	What is the new development version for "mosaicStats"? (org.fao.unredd:mosaicStats) 1.1-SNAPSHOT: : 2.0-SNAPSHOT
	[INFO] Transforming 'National Forest Monitoring System'...
	[INFO]
	[INFO] [...]
	[INFO] 
	[INFO] [INFO] Cobertura Report generation was successful.
	[INFO] [INFO]
	[INFO] [INFO]
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] [INFO] Reactor Summary:
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] [INFO] National Forest Monitoring System ..................... SUCCESS [1.219s]
	[INFO] [INFO] UNREDD Portal ......................................... SUCCESS [6.375s]
	[INFO] [INFO] UNREDD Administrative Portal .......................... SUCCESS [0.227s]
	[INFO] [INFO] mosaicStats ........................................... SUCCESS [9.152s]
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] [INFO] BUILD SUCCESSFUL
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] [INFO] Total time: 17 seconds
	[INFO] [INFO] Finished at: Fri May 24 16:55:44 CEST 2013
	[INFO] [INFO] Final Memory: 74M/418M
	[INFO] [INFO] ------------------------------------------------------------------------
	[INFO] Checking in modified POMs...
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git add -- pom.xml portal/pom.xml admin-portal/pom.xml mosaicStats/pom.xml
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git status
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git commit --verbose -F /tmp/maven-scm-80584271.commit pom.xml portal/pom.xml admin-portal/pom.xml mosaicStats/pom.xml
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git symbolic-ref HEAD
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git push https://github.com/nfms4redd/nfms.git release-1.0:release-1.0
	[INFO] Working directory: /tmp/nfms
	Username for 'https://github.com': fergonco
	Password for 'https://fergonco@github.com':
	[INFO] Tagging release with the label version-1.0...
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git tag -F /tmp/maven-scm-763472407.commit version-1.0
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git push https://github.com/nfms4redd/nfms.git version-1.0
	[INFO] Working directory: /tmp/nfms
	Username for 'https://github.com': fergonco
	Password for 'https://fergonco@github.com':
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git ls-files
	[INFO] Working directory: /tmp/nfms
	[INFO] Transforming 'National Forest Monitoring System'...
	[INFO] Transforming 'UNREDD Portal'...
	[INFO]   Updating http_proxy to 1.0.4-SNAPSHOT
	[INFO] Transforming 'UNREDD Administrative Portal'...
	[INFO] Transforming 'mosaicStats'...
	[INFO] Not removing release POMs
	[INFO] Checking in modified POMs...
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git add -- pom.xml portal/pom.xml admin-portal/pom.xml mosaicStats/pom.xml
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git status
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git commit --verbose -F /tmp/maven-scm-1950504187.commit pom.xml portal/pom.xml admin-portal/pom.xml mosaicStats/pom.xml
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git symbolic-ref HEAD
	[INFO] Working directory: /tmp/nfms
	[INFO] Executing: /bin/sh -c cd /tmp/nfms && git push https://github.com/nfms4redd/nfms.git release-1.0:release-1.0
	[INFO] Working directory: /tmp/nfms
	Username for 'https://github.com': fergonco
	Password for 'https://fergonco@github.com':
	[INFO] Release preparation complete.
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESSFUL
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 1 minute 15 seconds
	[INFO] Finished at: Fri May 24 16:56:14 CEST 2013
	[INFO] Final Memory: 27M/350M
	[INFO] ------------------------------------------------------------------------

