.. _unredd-install-stg_geoserver:

Deploy and configure GeoServer
================================

To install GeoServer you need the geoserver.war file that can be downloaded from http://geoserver.org/display/GEOS/Stable choosing *Web Archive* as Download format. 

Simply copy the application file ``geoserver.war`` to the tomcat webapps directory. For example::

  sudo cp geoserver.war /var/tomcat/webapps/geoserver.war

This will install and run geoserver instances, accessible in:

  http://localhost/geoserver/

GeoServer data directory
------------------------

We are going to put geoserver related data inside the directory ``/var/geoserver``.

Create the directory ``/var/geoserver/data``::

  $ sudo mkdir /var/geoserver

Copy the GeoServer data directory that is expanded from the war file into /var/geoserver::

  $ sudo cp -R /var/tomcat/webapps/geoserver/data /var/geoserver

As geoserver is an application run by tomcat, it is necessary that the user ``tomcat6`` has write access to this folder so the easiest is to change the folder owner to belong to ``tomcat6``::

  $ sudo chown -R tomcat6: /var/geoserver/data
  
After this, it is necessary to restart the ``tomcat6`` service to make Geoserver reinitialize and access its new data folder.

The directory structure inside is as follows:

* **data**: geoserver configuration files
* **logs**: geoserver logs, outside of the datadir, since it needs no backup.

Further details on customization are found in :ref:`geoserver`.
