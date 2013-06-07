Folder structure
=================

The layers that are to be published in the system that will be used to show any indicator such as statistics must be stored in a tree structure such as the one described below::

	/
	|- data (Subfolder to store the actual data)
	|- configuration (Subfolder to store all configuration required to create indicators)
	|- work (Subfolder to store any derived data necessary for calculations)
	\- output (Subfolder to store the results that will be presented to the user of the system when he asks for a certain indicator of the layer)

Every layer has to follow this structure since any indicator at any time can reference it and create a ``work`` subfolder for the layer.

.. warning::
   The choice of using folders instead of a database to keep additional information about the layers is justified by the fact that

   * It is easier to understand and to develop since there is no need to learn JDBC or any JPA library.
   * It is easier to build the structure by hand and see the results

   However, there are some drawbacks of not using a database. First, two of the ACID (http://en.wikipedia.org/wiki/ACID) properties are very relevant and should be taken into account by the code developed: 

   * Atomicity: the operations done in the folders should be done completely or at all in order to let the folders always in a consistent status.
   * Isolation: two simultaneous operations on the same folder should result in the same as executing them sequentially.

   So far, the operations to be done to the folders are not very complex and the folder structure is a suitable solution. However it is likely that the system will evolve and using a database may become interesting. Therefore it would be wise to access the folders through an interface that is now implemented using folders and that can be implemented in the future accessing a database easily.

Example
--------

In order to create a system with:

- a forest mask with different timestamps
- a layer of provinces so that the portal user gets charts with the evolution of the area of forest in the province along time

the following steps have to be followed:

#. For each layer create the minimum mandatory tree structure under a folder that represents the new layer::

	forest-classification
		\- data

   and place the data in the ``data`` directory.

#. For each layer that will provide an indicator create the whole tree structure::
	
	provinces
		|- data
		|- configuration
		|- work
		\- output

   The services of the dissemination portal will look for the indicators output in the ``output`` folder so the indicators can be produced manually and placed there easily. If the indicators are to be produced automatically, it is possible to use all the other folders: ``configuration`` and ``work``.

#. Place the configuration data in the ``configuration`` folder. In our case it could be a file ``zonal-statistics.xml`` containing information about the field in the layer that identifies the unique zones and about the layers that contain the variables to calculate. Could be something similar to::

	<?xml version="1.0"?>
	<zonal-statistics>
		<zone-id-field>name</zone-id-field>
		<variable layer="unredd:forest-mask"/>
		<variable layer="unredd:forest-classification"/>
	</zonal-statistics>

#. Produce the indicator. This depends on the tools used to get the indicator calculated. Normally it should imply the execution of some program that reads the previous configuration, accesses the data, may produce the first time some permanent derived result in ``work`` and will produce the output data in the ``output`` folder. 

Layer statistics indicator
--------------------------

The system implements a layer statistics indicator that produces statistics about the coverage of different variables in the different zones of the layer. It takes as input the name of the layer and the configuration file in this layer that references the time mosaics to be used as variable. The output will contain, for each individual zone in the layer, the surface that is covered by the time mosaic in each timestamp.

The process to calculate the statistics can be described roughly as follows:

#. The process takes the layer name as a parameter
#. Using GeoServer RESTful API it obtains the folder where the data is stored, which will be the ``data`` folder under the layer folder (therefore the layer has to be published in GeoServer).
#. The ``zonal-statistics.xml`` file existing in the layer ``configuration`` folder is read to get the information about the time mosaics to use in the process and the field to identify unique zones in the layer. 
#. The snapshots are ordered and processed individually to actually produce the data.

The data produced will appear in the ``output/result.xml`` file that will be consumed by the portal to render some output to the user.

In order to install the indicator in a layer it is necessary to follow these steps:

#. Create the layer folder structure

#. Add the layer to geoserver

#. Create a ``configuration/zonal-statistics.xml`` and populate it like this::

   <?xml version="1.0"?>
   <zonal-statistics xmlns="http://www.nfms.unredd.fao.org/zonal-statistics">
   	<zone-id-field>id</zone-id-field>
   	<variable layer="unredd:temporalMosaic" />
   </zonal-statistics>
   
   which indicates that the field ``id`` identifies the individual zones in the layer and that the layer ``unredd:temporalMosaic`` will be the one to use to get the temporal statistics about coverage.

#. Execute the mosaic specifying the name of the layer as a parameter::

   TODO

#. Verify that the result files have appeared on the ``output`` folder and contains the results.





