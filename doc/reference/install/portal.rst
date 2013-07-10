.. module:: unredd.install.portal

Deploy and configure dissemination Portal
=========================================

Simply copy the application file ``unredd-portal.war`` to the tomcat webapps directory. For example::

  $ sudo cp unredd-portal.war /var/tomcat/webapps/portal.war

This will install and run the portal with the default (sample) configuration, accessible in:

  http://localhost/portal/


Portal configuration directory
------------------------------

To customize the portal for a new country, you need to create a new ``PORTAL_CONFIG_DIR``. An example directory is shipped with the portal application and can be found under ``/var/tomcat/portal/webapps/portal/WEB-INF/default_config``. Use it as an example to build your own configuration directory.

In order to use it as a starting point copy it to the portal configuration directory::

  $ sudo mkdir /var/portal
  $ sudo cp -R /var/tomcat/webapps/portal/WEB-INF/default_config/* /var/portal/

Further details on customization are found in :ref:`unredd-portal-customize`.