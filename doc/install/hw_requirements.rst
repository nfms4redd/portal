.. module:: unredd.install.requirements

Hardware requirements
=====================

System requirements depend very much on the load of the system and the amount of data being managed.


Staging area
------------

The staging area will run at least the following applications:

 * Preprocessing utilities, requiring ~2 GB RAM.
 * GeoServer, requiring ~1 GB RAM.
 * Administrator and dissemination portals, with default memory requirements (128 MB).
 * PostgreSQL, almost negligible if only used to store data

Plus the memory needed by the Operating System and other running services. A minimum of 8 GB of RAM is recommended.

Ingestion and preprocessing of layers are specially demanding in terms of computational power.

Disk requirements depend mainly on the amount of data being ingested and managed.


Dissemination area
------------------

The dissemination area will run at least the following applications:

 * GeoServer, requiring ~1.5 GB RAM.
 * Portal, with default memory requirements (128 MB).
 * PostgreSQL, almost negligible if only used to store data
 
Plus the memory needed by the Operating System and other running services. A minimum of 6 GB of RAM is recommended.

Disk requirements depend mainly on the amount of raster data being published in geoserver, and on the size of the tile cache.
