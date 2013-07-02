Indicators
============

In order to produce indicators a folder structure is created. This folder structure mirrors the workspace/layer structure in GeoServer so that there is one folder representing GeoServer workspaces that contains subfolders representing layers, as it can be seen in the following example::

	/
	|- unredd
		\- provinces
	\- test
		\- railway 

In this example, GeoServer is expected to have the layers ``test:roads`` and ``test:railway``.

.. warning::
   The choice of using folders instead of a database to keep additional information about the layers is justified by the fact that

   * It is easier to understand and to develop since there is no need to learn JDBC or any JPA library.
   * It is easier to build the structure by hand and see the results

   However, there are some drawbacks of not using a database. First, two of the ACID (http://en.wikipedia.org/wiki/ACID) properties are very relevant and should be taken into account by the code developed: 

   * Atomicity: the operations done in the folders should be done completely or at all in order to let the folders always in a consistent status.
   * Isolation: two simultaneous operations on the same folder should result in the same as executing them sequentially.

   So far, the operations to be done to the folders are not very complex and the folder structure is a suitable solution. However it is likely that the system will evolve and using a database may become interesting. Therefore it would be wise to access the folders through an interface that is now implemented using folders and that can be implemented in the future accessing a database easily.

This structure is only necessary for the layers for which an indicator is desired. Inside the layer folder it is possible to find the following directories: 

* ``configuration``: subfolder to store any configuration elements
* ``work``: subfolder to store permanently any derived data necessary for calculations
* ``output``: subfolder to store the results that will be presented to the user of the system when he asks for a certain indicator of the layer)

For example, in order to calculate two indicators for ``test:roads`` and ``test:railway`` it is necessary to place some configuration on the layers::

	/
	|- unredd
		\- provinces
			\- configuration
	\- test
		\- railway 
			\- configuration

The concrete configuration depends on the concrete indicator that is being calculated. Finally, after calculating two indicators, the structure could look like this one::

	/
	|- unredd
		|- provinces
			|- configuration
			\- output
				\- coverage
		\- forest_mask
			\- work
	\- test
		|- elevation
			\- work
		\- railway 
			|- configuration
			\- output
				\- topographic_profile

Where a new ``output`` folder has appeared on the existing layers containing the results and two new folders related to the ``test:elevation`` and ``unredd:forest_mask`` have been created just to associate the temporal files stored in ``work`` with the layer.

Coverage indicator
-------------------

The system implements a layer statistics indicator that produces statistics about the coverage of a variable in the different zones of the layer. It takes as input the name of the layer, the root of the layer configuration tree ("/" in the examples) and the GeoServer data folder. As a result it generates a folder in the output folder of the layer containing a result for each individual zone in the layer: the surface that is covered by the time mosaic in each timestamp.

In order to execute this indicator, all the layers involved must be registered in GeoServer, the coverage variable to measure must be an ImageMosaic and the layer the outputs are produced for must be a vector layer, either a Shapefile, a directory of Shapefiles or a PostGIS layer.

In order to create a layer of provinces so that the portal user gets charts with the evolution of the area of forest in the province along time the following steps have to be followed. It is assumed that the layer is called ``unredd:provinces``, the layer containing the forest mask is called ``unredd:forest_mask`` and all the involved layers are loaded in GeoServer:

#. Create the folder for the provinces layer with just a ``configuration`` folder::

	/
	|- unredd
		\- provinces
			\- configuration

#. Place the configuration data in the ``configuration`` folder. For the coverage statistics it must be a file called ``zonal-statistics.xml`` containing information about the field that identifies the provinces in the layer and about the layers that contain the variables to calculate::

	<?xml version="1.0"?>
	<zonal-statistics xmlns="http://www.nfms.unredd.fao.org/zonal-statistics">
		<variable layer="unredd:forest_mask">
			<zone-id-field>id</zone-id-field>
			<presentation-data>
				<title>Data evolution</title>
				<date-format>yyyy</date-format>
			</presentation-data>
		</variable>
	</zonal-statistics>

   For more information on the format: TODO

#. Produce the indicator. In order to execute the portal a command like this one should be executed::

	$ sudo /var/stats-indicator/stats-indicator.sh --layer unredd:provinces --conf /var/portal/indicators -gsdata /var/geoserver/data 

   This execution should finish with the message: The indicator was generated successfully. Otherwise it is possible to get more information about the problem in ``/var/stats-indicator/stats-indicator.log

Internally, the process to calculate the statistics can be described roughly as follows:

#. The process takes the layer name as a parameter
#. Analyzing GeoServer data directory it obtains the folder where the data is stored.
#. The ``zonal-statistics.xml`` file existing in the layer ``configuration`` folder is read to get the information about the time mosaics to use in the process and the field to identify unique zones in the layer. 
#. The snapshots are ordered and processed individually to actually produce the data.

The data produced will appear in the ``output/result.xml`` file that, along with ``output/metadata.properties`` will be consumed by the portal to render some output to the user.



