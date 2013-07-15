GeoServer: Publicación de datos raster
===============================================

Almacen de datos GeoTIFF
------------------------

* En la página "Almacenes de datos", hacer clic en "Agregar nuevo almacén".

Los datos raster para el taller se encuentran en formato GeoTIFF.
A diferencia de los datos vectoriales, no tenemos un almacén de tipo
"Directory of spatial files" para datos raster, así que deberemos crear
un almacén distinto para cada una de las capas.

Comencemos con el primer fichero: Una clasificación de coberturas forestales.

* Escoger "GeoTIFF" bajo "Origenes de datos raster".
* En el formulario, utilizar "unredd" como espacio de nombres, y "forest_cover" como nombre de la capa. Opcionalmente, agregar una descripción.
* Clicar en "Buscar..." en "Parámetros de conexión", y navegar hasta el fichero :file:`/home/unredd/Desktop/pry_workshop_data/raster/forest_cover_1990.tif`.
* Clicar en "Guardar".

Se presentará una nueva página con una *"lista"* de las capas a publicar: Sólo aparece un elemento, "forest_cover_1990", puesto que el almacén sólo contempla un fichero GeoTIFF.


Publicación de una capa GeoTIFF
-------------------------------

Desde esta página,

* Clicar en "Publicación" para publicar.

Se presentará una página para rellenar los datos sobre la capa, similar a la que ya vimos para la creación de capas vectoriales.

En esta ocasión, GeoServer ha detectado automáticamente el sistema de referencia de coordenadas de la capa GeoTIFF.
A diferencia de las capas vectoriales, no hará falta declarar manualmente el SRS y los encuadres, que ya tienen la información necesaria.

* Clicar en "Guardar".

* Previsualizar la nueva capa "forest_cover_1990" en OpenLayers.

En la misma página de previsualización, clicando sobre cada una de estas áreas, obtenemos una información numérica, ``PALETTE_INDEX``. Se
distinguen cinco valores distintos: Área sin datos (amarillo), Bosque Atlántico (verde), Bosque Chaqueño (azul), Superficie no 
forestal (magenta), y Masas de Agua (rojo). Esta combinación de colores de alto contraste permite distinguir claramente
cada clase, pero obviamente no es la que mejor se asocia visualmente con el significado de cada categoría.

Simbolización Raster
--------------------

Podemos asociar cada uno de los valores a un nuevo color que represente mejor cada clase:

=====  ======================  =========================
Valor  Clase                   Nuevo color deseado
=====  ======================  =========================
0      Área sin datos          Transparente
1      Bosque Atlántico        Verde oscuro (#005700)
2      Bosque Chaqueño         Verde claro (#01E038)
3      Superficie no forestal  Amarillo pálido (#FFFF9C)
4      Masa de Agua            Azul (#3938FE)
=====  ======================  =========================

A partir de esta tabla, crearemos un estilo SLD para la capa ráster.

* En la página "Estilos", "Agregar un nuevo estilo".
* Asignarle el nombre "forest_mask".
* Dejar el "Espacio de nombres" en blanco.

En lugar de escribir el SLD desde cero, podemos utilizar la opción "Copiar de un estilo existente".

* Utilizar "Copiar de un estilo existente" para cargar el estilo "raster".
* Sustituir el contenido de ``RasterSymbolizer`` por este otro:

.. code-block:: xml

    <ColorMap type="values">
        <ColorMapEntry quantity="1" label="Bosque Atlantico" color="#005700" opacity="1"/>
        <ColorMapEntry quantity="2" label="Bosque Chaco" color="#01E038" opacity="1"/>
        <ColorMapEntry quantity="3" label="Zona no boscosa" color="#FFFF9C" opacity="1"/>
        <ColorMapEntry quantity="4" label="Masa de agua" color="#3938FE" opacity="1"/>
    </ColorMap>

Este mapa de color asigna, a cada posible valor, un color y una etiqueta personalizada. El valor "0" (Área sin datos), al no aparecer en el mapa, se representará como transparente.

* "Validar" el nuevo SLD, "Enviar", y asignar como estilo por defecto a la capa "forest_cover_1990" (en la pestaña "Publicación").
* Previsualizar de nuevo la capa:


Publicación de un mosaico Raster temporal
-----------------------------------------

Vamos a publicar una capa ráster con una imagen satelital RGB que pueda usarse como capa base de referencia.

En lugar de un solo fichero GeoTIFF, en esta ocasión disponemos de cuatro imagenes correspondientes a cuatro años distintos: 1990, 2000, 2005 y 2010.

Vamos a publicar las cuatro imágenes en como una sola capa, componiendo un "mosaico temporal".

* En la página "Almacenes de datos", hacer clic en "Agregar nuevo almacén".
* Escoger "ImageMosaic" bajo "Origenes de datos raster".
* Utilizaremos "landsat" como nombre para el almacen de datos.
* Este tipo de almacen no dispone de la utilidad "Buscar..." para indicar la localización de los datos, así que tendremos que escribirla a mano::

    file:///home/unredd/Desktop/pry_workshop_data/raster/landsat/

* Clicar en "Guardar", y luego en "publicación" en la página siguiente.
* Ir a la pestaña "dimensions", para habilitar la dimensión "Time". Escoger "List" como opción de presentación.
* "Guardar" y previsualizar la capa.


Cómo se define la dimensión temporal
....................................

Si abrimos los contenidos de :file:`pry_workshop_data/raster/landsat`, observamos los siguientes ficheros GeoTIFF, que contienen las imágenes para cada instante:

:file:``landsat_1990.tif``
:file:``landsat_2000.tif``
:file:``landsat_2005.tif``
:file:``landsat_2010.tif``

Vemos que el nombre de todos los ficheros comienza por las mismas 8 letras ``landsat_``, y que terminan con cuatro cifras indicando el año. De algún modo debemos indicar a GeoServer cómo están formados estos nombres, para que pueda extraer la información temporal a partir de ellos.

Esto se realiza mediante una serie de ficheros de `properties`:

  :file:`timeregex.properties`, cuyo contenido es::

    regex=[0-9]{4}

  Indica que la dimensión temporal está formada por 4 cifras.

  :file:`indexer.properties`, cuyo contenido es::

    TimeAttribute=time
    Schema=the_geom:Polygon,location:String,time:java.util.Date
    PropertyCollectors=TimestampFileNameExtractorSPI[timeregex](time)

  Indica que la marca temporal será obtenida aplicando `timeregex`, y se almacenará en un índice como atributo `time`.

.. note:: Para saber más...

   * Documentación técnica NFMS: `GeoServer > Advanced Raster data preparation and configuration > Adding an Image Mosaic to GeoServer <http://nfms4redd.org/doc/html/geoserver/raster_data/mosaic.html>`_
   * `Página sobre expresiones regulares <http://www.regular-expressions.info/>`_.

Consumo del servicio temporal
------------------------------

Ahora que tenemos una capa temporal publicada podemos pasar a formar a consumirla con algún cliente estándar. Desafortunadamente gvSIG no es capaz de consumir la
capa y QGIS no tiene soporte para la dimensión temporal. Sin embargo, es posible obtener las imágenes en los distintos instantes símplemente
utilizando el navegador web. Para ello, las llamadas que se hacen deben incluir el parámetro *TIME*, como en los siguientes ejemplos::

	http://168.202.48.83/geoserver/ows?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&BBOX=-13.910569,12.090411,5.395932,32.233551&
		TIME=2000&CRS=EPSG:4326&WIDTH=923&HEIGHT=885&LAYERS=capacitacion:test&STYLES=&FORMAT=image/png&DPI=96&TRANSPARENT=TRUE

	http://168.202.48.83/geoserver/ows?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&BBOX=-13.910569,12.090411,5.395932,32.233551&
		TIME=2005&CRS=EPSG:4326&WIDTH=923&HEIGHT=885&LAYERS=capacitacion:test&STYLES=&FORMAT=image/png&DPI=96&TRANSPARENT=TRUE

	http://168.202.48.83/geoserver/ows?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&BBOX=-13.910569,12.090411,5.395932,32.233551&
		TIME=2010&CRS=EPSG:4326&WIDTH=923&HEIGHT=885&LAYERS=capacitacion:test&STYLES=&FORMAT=image/png&DPI=96&TRANSPARENT=TRUE

Optimización de GeoTIFF para su publicación
-------------------------------------------

Los datos raster generalmente contienen una gran cantidad de información, mucha más de la que se puede mostrar en una pantalla de una sola vez.
Para que GeoServer pueda gestionar esta gran cantidad de datos de forma eficiente en diferentes situaciones, es necesario prestar atención a su optimización.

Imaginemos que queremos mostrar por pantalla una imagen raster de 10.000 x 10.000 píxeles.
Puesto que la resolución de la pantalla es limitada, sólamente será capaz de mostrar,
como máximo, un 1% de los píxeles totales del raster.

En lugar de leer todo el ráster, debemos incorporar mecanismos en que no sea necesario leer completamente todos los datos cada vez que visualizamos el ráster, sino sólamente a la porción de información que podemos visualizar. Esto se hace de dos modos:

* En situación de "zoom in", es conveniente poder acceder sólo a la porción de imagen que se va a mostrar, descartando el resto.
* En situación de "zoom out", es conveniente disponer de una o varias copias del ráster a resoluciones menores.

El formato interno de los ficheros GeoTIFF se puede procesar y prepararlo para estas dos situaciones.

Para ello utilizaremos las librerías GDAL desde la línea de comandos.
En concreto, veremos las utilidades ``gdalinfo``, ``gdal_translate`` y  ``gdaladdo``.

gdalinfo
........

Proporciona información sobre ficheros ráster.

* Abrir una consola (terminal).
* Acceder al directorio que contiene las imágenes landsat::

    cd pry_workshop_data/raster/landsat/

* Ejecutar ``gdalinfo`` sobre la imagen de 1990::

    gdalinfo landsat_1990.tif

Obtendremos información sobre el tamaño del fichero, el sistema de coordenadas, y la manera en que están codificadas las diferentes bandas internamente.

En concreto, observamos::

  Band 1 Block=3069x1 Type=Byte, ColorInterp=Red
  Band 2 Block=3069x1 Type=Byte, ColorInterp=Green
  Band 3 Block=3069x1 Type=Byte, ColorInterp=Blue

Esto significa que la imagen está guardada en "tiras" de 1px de alto.


gdal_translate
..............

Para optimizar el acceso en situaciones de "zoom in", podemos cambiar esta codificación interna
para que almacene la información en bloques cuadrados de 512x512 píxeles. Ejecutar::

  gdal_translate -co "TILED=YES" -co "BLOCKXSIZE=512" -co "BLOCKYSIZE=512" landsat_1990.tif landsat_1990_tiled.tif

Veamos la información en la nueva imagen::

  gdalinfo landsat_1990_tiled.tif

Ahora obtenemos::

  Band 1 Block=512x512 Type=Byte, ColorInterp=Red
  Band 2 Block=512x512 Type=Byte, ColorInterp=Green
  Band 3 Block=512x512 Type=Byte, ColorInterp=Blue


gdaladdo
........

Para optimizar el acceso en situaciones de "zoom out", podemos añadir, internamente, una serie de imágenes a menor resolución::

  gdaaddo landsat_1990_tiled.tif 2 4 8

Ejecutando de nuevo gdalinfo, observamos que para cada banda aparece esta nueva información::

  Overviews of mask band: 1535x1535, 768x768, 384x384


La ventaja de utilizar la línea de comandos es que se puede crear un *script*  para automatizar
este procesado y aplicarlo masivamente a un gran conjunto de ficheros siempre que sea necesario.


.. note:: Para saber más...

   * Documentación técnica NFMS: `GeoServer > Advanced Raster data preparation and configuration > Processing with GDAL <http://nfms4redd.org/doc/html/geoserver/raster_data/processing.html>`_
   * `GDAL Utilities <http://www.gdal.org/gdal_utilities.html>`_.

