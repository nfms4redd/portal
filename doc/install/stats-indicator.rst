.. module:: unredd.install.portal

Install the statistics utility
===============================

The statistics utility will generate the data used for the reports about the coverage of the forest mask (or any other variable)
along time for the individual features of the classifying layer.

Simply unzip the zip file on the /var folder::

	$ cd /var
	$ unzip stats-indicator.zip
	
After the decompression it should be possible to execute the ``stats-indicator.sh`` script inside::

	$ /var/stats-indicator/stats-indicator.sh
	usage: stats-indicator.sh -l <layer-name> -r <root-folder>
	 -l         Name of the layer to use for the calculation of the stats
	            indicators
	 -r <arg>   Root of the layer folder structure

In order to verify that the indicator has been correctly installed it can be executed. In order to execute it is necessary to invoke the full path and with admin privileges::

	$ sudo /var/stats-indicator/stats-indicator.sh
	usage: stats-indicator.sh -l <layer-name> -r <root-folder>
	 -l         Name of the layer to use for the calculation of the stats
	            indicators
	 -r <arg>   Root of the layer folder structure
