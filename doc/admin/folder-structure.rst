Folder structure
=================

The layers that are to be published in the system that will be used to show any indicator such as statistics must be stored in a tree structure such as the one described below::

	/
	|- data (Subfolder to store the actual data)
	|- configuration (Subfolder to store all configuration required to create indicators)
	|- temp (Subfolder to store any derived data necessary for calculations)
	\- output (Subfolder to store the results that will be presented to the user of the system when he asks for a certain indicator of the layer)

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
		|- temp
		\- output

   The services of the dissemination portal will look for the indicators output in the ``output`` folder so the indicators can be produced manually and placed there easily. If the indicators are to be produced automatically, it is possible to use all the other folders: ``configuration`` and ``temp``.

#. Place the configuration data in the ``configuration`` folder. In our case it could be a file ``zonal-statistics.xml`` containing information about the field in the layer that identifies the unique zones and about the layers that contain the variables to calculate. Could be something similar to::

	<?xml version="1.0"?>
	<zonal-statistics>
		<zone-id-field>name</zone-id-field>
		<variable layer="unredd:forest-mask"/>
		<variable layer="unredd:forest-classification"/>
	</zonal-statistics>

#. Produce the indicator. This depends on the tools used to get the indicator calculated. Normally it should imply the execution of some program that reads the previous configuration, accesses the data, may produce the first time some permanent derived result in ``temp`` and will produce the output data in the ``output`` folder. 





