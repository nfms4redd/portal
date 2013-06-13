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

In order to be able of executing the indicator without the need to specify the full path to the script it is necessary to modify a file called ``.bashrc`` in the *home* folder of the user. At the end of the file add the following lines::

	PATH=$PATH:/var/stats-indicator
	export PATH
	 
In order for this change to be taken into account it is necessary to log out and log in again. After this, it will be possible to execute from any directory the script directly::

	$ stats-indicator.sh
	usage: stats-indicator.sh -l <layer-name> -r <root-folder>
	 -l         Name of the layer to use for the calculation of the stats
	            indicators
	 -r <arg>   Root of the layer folder structure
