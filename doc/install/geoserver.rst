.. _unredd-install-stg_geoserver:

Deploy and configure GeoServer
================================

Simply copy the application file ``geoserver.war`` to the tomcat webapps directory. For example::

  sudo cp geoserver.war /var/tomcat/webapps/geoserver.war

This will install and run geoserver instances, accessible in:

  http://localhost/geoserver/


GeoServer data directory
------------------------

We are going to put geoserver related data inside the directory ``/var/geoserver``.

The directory structure inside is as follows:

* **data**: geoserver configuration files
* **logs**: geoserver logs, outside of the datadir, since it needs no backup.

Further details on customization are found in :ref:`geoserver`.

