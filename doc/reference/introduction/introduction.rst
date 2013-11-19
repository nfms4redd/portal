============
Introduction
============

The objective of UN-REDD is to create an infrastructure that helps in the transparent dissemination of forest change, by processing and publishing forest (remote sensing) data, validated and open for public feedback of people in the monitored areas.

This manual describes the dissemination system, its architecture, components and use.


System Overview
---------------

The system includes:

- A dissemination portal where time dependent data can be visualized and some statistics and reports can be obtained.
- An administrative portal where the administrators prepare and publish the data that is available in the dissemination portal. This preparation includes:

	- Data preprocessing to improve visualization performance.
	- Precomputing statistics and other results related to the data loaded in the system for better performance and scaling
	
The system makes use well known open source projects, as GeoServer and OpenLayers, as well as of OGC standards in order to guarantee the technological independence of the users that deploy the system and to provide them with a tool that can seamlessly interoperate with other systems.

The following video gives an overview of the functionalities of the dissemination portal: TODO

.. raw:: html

        <iframe width="420" height="315" src="http://www.youtube.com/embed/" frameborder="0" allowfullscreen></iframe> 

System Architecture
-------------------

TODO

The dissemination subsystem can be split into several blocks:

#. raw data ingestion and pre-processing (GeoBatch)
#. internal data storage (GeoStore)
#. geographic data publishing (GeoServer)
#. statistics publishing (WPS)
#. user front-end (Portal interface)
#. management of the data flows (Admin interface)


.. figure:: ../../workshops/es/_static/deployment.png
   :align: center
   :scale: 75 %

   System Architecture


Geographic Data Publishing with *GeoServer*
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

GeoServer is an open source software server that allows the publishing of geospatial data from any major spatial data source using open standards from OGC and ISO.


Geographic Data Caching with *GeoWebCache*
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

GeoWebCache is a web application used to cache map tiles coming from a variety of sources such as OGC Web Map Service (WMS).

It is used to improve the GeoServer workload for WMS service.

Storing vector data in PostGIS
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

TODO