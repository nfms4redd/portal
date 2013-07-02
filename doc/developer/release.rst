Releasing a new version
==========================

.. warning:: to verify

#. Create a release branch::

	$ git checkout -b release-[NUMBER] develop
	[modifications]
	$ git push -u origin release-[NUMBER]

#. Once the development for the release is finished: prepare the release. The maven release plugin does not match exactly the process we need so it is advisable to change the versions in the pom.xml's to stable versions manually. 

#. Merge release back to master::

	$ git checkout master 
	$ git merge --no-ff release-2.0 
	$ git tag -a version-2.0
	$ git push --tags

#. Merge release back to develop::

	$ git checkout develop 
	$ git merge --no-ff release-2.0 
	$ git push

#. Then in develop, change the pom.xml's back to SNAPSHOTS of the next version.
	
#. Deploy the packets with maven::

	$ git checkout master
	$ mvn deploy

