Portal: Configuración de capas raster
==========================================

.. note::

	=================  ================================================
	Fecha              Autores
	=================  ================================================             
	1 Diciembre 2012    * Oscar Fonts (oscar.fonts@geomati.co)
	24 Junio 2013		* Fernando González (fernando.gonzalez@fao.org)
	=================  ================================================	

	©2013 FAO Forestry 
	
	Excepto donde quede reflejado de otra manera, la presente documentación se halla bajo licencia : Creative Commons (Creative Commons - Attribution - Share Alike: http://creativecommons.org/licenses/by-sa/3.0/deed.es)

El proceso de configuración de capas raster en el portal es exactamente el mismo que para las capas vectoriales, ya que
tanto unas como otras se exponen a través de la interfaz WMS, que no hace diferencia entre raster y vector.

Creación de capas temporales
--------------------------------

Es posible añadir capas con dimensión temporal de la manera habitual añadiendo un nuevo elemento en "layers" con las siguientes características:

  * Identificador: "landsat"
  * Etiqueta: "${landsat}"
  * Nombre de la capa landsat en GeoServer.
  * Formato de imagen: "image/jpeg"

**Novedad**: Puesto que esta capa dispone de dimensión temporal, debemos indicar al Portal los tiempos para los que se dispone de información.
Esto lo realizaremos mediante el atributo "wmsTime" con los años 1990, 2000, 2005 y 2010. Así pues:

  * Añadir un nuevo atributo "wmsTime": "1990-01-01T00:00:00.000Z,2000-01-01T00:00:00.000Z,2005-01-01T00:00:00.000Z,2010-01-01T00:00:00.000Z"

* Añadir el contexto para esta capa, con las siguientes características:

  * Identificador: "landsat"
  * Etiqueta: "${landsat}"
  * Capa: ["landsat"]
  * Inicialmente desactivada al cargar la página

* Añadir un nuevo grupo con las siguientes características:

  * Etiqueta: "${base_layers}"
  * Un único contexto: "landsat"

Al finalizar, el Portal deberá contener los nuevos elementos. Se deberá apreciar cómo, además del nuevo grupo de Capas Base, ha aparecido
un nuevo control que permite seleccionar el año de visualización.

* Comprobar que, al desplazar el control de tiempo, se actualizan los contenidos de la capa "landsat".
