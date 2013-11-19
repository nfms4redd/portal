Monday 1.5 + 2.5
=================

Instalar el proyecto en eclipse y entender su estructura interna y su arquitectura

Architecture general
---------------------

* PostGIS + Tomcat que contiene (GeoServer + frontend + backend)

* Repaso por los componentes
	- echar un vistazo al directorio de tomcat
	- ver el directorio de datos de geoserver
	- conectar a la base de datos de postgresql

* Comentar que en este caso vamos a tener otra instancia de tomcat para realizar los desarrollos (es una configuración especial del curso, normalmente se desarrollará en una máquina en local)

Instalación frontend (demo, porque ya está instalado)
-----------------------------------------------------------

Comentar dónde está instalado y hacer una demo de cómo se hace. Se utiliza git y maven

- GIT es como subversion pero mucho más potente. En un futuro podemos hacer un pequeño curso de GIT.

- Maven tiene un objetivo parecido a Ant, pero la aproximación es distinta.

- Demo

	* Descarga e instalación de Eclipse IDE for Java EE Developers
	
	* Arrancar eclipse
	
	* Clonado del proyecto e importación en eclipse.
	
	Right click - import - git - specify 
	
		url: git@github.com:nfms4redd/nfms.git
		Protocol: https
	
	Local destination: The one of the repository
	
	(Mientras descarga explicar: Dónde está tomcat, dónde está geoserver, conexión a postgis e introducción a Maven)
	
	Use the New Project wizard -> Cancel
	
	New Project - Import existing maven project and select the cloned repository (may require installation of some plugins on eclipse that are not necessary for the development of the portal and are done automatically (no es grave si fallan))
	
	Team - Switch to - New branch (simplification)
	
	Cerrar el proyecto raíz

* Ejecución

Botón derecho en "portal" -> Debug on server -> Apache Tomcat

Open firefox to take a look

Arquitectura del frontend
-----------------------------

Arquitectura cliente servidor, donde hay una página web programada en javascript que realiza peticiones al servidor, éste responde generalmente con contenido en JSON y el cliente procesa ese contenido.

Ejemplo: Carga de la aplicación con FireBug y observación en consola de las llamadas (config e indicadores)

	Advertencia: El contenido de las llamadas o incluso las llamadas pueden cambiar porque el proyecto está en desarrollo, lo importante es entender la arquitectura y las llamadas utilizadas.

Vamos a ver el código del servidor. ¿dónde podemos ver qué clase es la que implementa config.js? -> web.xml

Vemos también la llamada a los indicadores (hacemos notar la excepción).

¿Qué hay en el cliente?

	src/main/webapp

	RequireJS
	
	modules
	
	index.html referencia a main.js y a partir de este momento, la aplicación cliente se compone sólo de módulos javascript.
	
	La configuración de módulos en requirejs referencia el directorio "modules"
	
	Dentro de este directorio va a haber un conjunto de módulos que cuyas dependencias de unos en otros forman un grafo. Algunos los hemos desarrollado nosotros y otros son librerías externas. Se puede ver en la configuración que hay que especificarle dónde están las librerías externas
	
	La configuración se sigue de una serie de llamadas "require" que lo que hacen es cargar módulos. Veremos en la parte del cliente cómo se cargan y definen estos módulos.
	
Estructura del proyecto
------------------------

	Explicación de los distintos proyectos
	
	Explicación de la arquitectura típica de un proyecto maven

(Si fuera necesario) Introducción a maven
------------------------------------------

Ejercicio: Crear un proyecto maven

Ejercicio: Usar una dependencia

Ejercicio: Instalar la plataforma

Intro JQuery (a caballo entre lunes y martes)
----------------------------------------------------

(El objetivo es que se entienda, no aprender a usarlo. Se aprenderá a utilizar durante el curso viendo muchos ejemplos. Es importante que si algo se olvida durante el curso se comente para poder seguir durante el mismo entendiendo los ejemplos.)

JQuery nos proporciona:

- selección y manipulación del dom: $()
- utilidades varias: $.xxx() 
	- llamadas ajax

Ejemplo "seleccion"

	Cargar con protocolo file y preguntar: ¿Quién sabe qué hace el código?

	Explicar: nos permite obtener un objeto jquery para actuar sobre el objeto que se pasa como parámetro

Ejemplo "atributos": 

	Preguntar quién sabe qué hace el código.

	Explicar: con .attr podemos cambiar los atributos de los elementos del documento.

	Ejercicio: Cambiar la dirección del enlace
	
	Ejemplo: ¿Y si en lugar de cambiar el atributo lo queremos es leer su valor?
	
	Pregunta: ¿Y si quisiéramos cambiar el texto del enlace?

Ejemplo "contenido": con .html podemos establecer el contenido

	Ejercicio: Cambiar el contenido del span
	
	Ejercicio: Cambiar el contenido del segundo div
	
		Normalmente se suele hacer la selección por id o por clase (mostrar ejemplos)
	
Ejemplo "nuevos": con .append y .appendTo podemos anidar elementos (mostrar ambas posibilidades)

	Ejemplo: Mostrar todo en una línea
	
	Ejercicio: Ponerlo todo en una línea usando body.append en lugar de div.appendTo

	Explicar la diferencia entre $("div"), $("<div/>") y $(document)
	
	Ejercicio: Eliminar todos los elementos y dejar en el body sólo el texto "esta página está vacía" (se pretende mostrar la dinámica de buscar en internet)
	
	Ejercicio: Crear la página inicial (hola mundo y el enlace) únicamente por programación
	
Ejemplo "estilos":

Ejemplo "eventos":

	Ejercicio: Crear una página que contenga img1.jpg y que mientras el ratón está en ella se cambie por img2

Ejemplo "ajax": Nos permite realizar llamadas al servidor y obtener la respuesta sin necesidad de recargar la página. JQuery pone a disposición el método $.ajax() y versiones de conveniencia más simples: $.get(), $.getScript(), $.getJSON(), $.post() y $().load().
	
	Comentar la restricción de dominio y decir que la veremos cuando tengamos un mapa y pidamos información, o consultando algún servicio externo

Tuesday 2.5 + 2.5
==================

Repaso interactivo día anterior
---------------------------------

* Exploración del resto de web.xml
	Context Listener
	Lang filter: procesa el parametro "lang" de cualquier petición y lo transforma en el atributo "locale" de la request.
	Indicators: Devuelve los indicadores que hay en el sistema para una capa dada.
	Error management: Veremos con más detalle cómo utilizarlo. Servlet para gestionar los errores.
	Proxy: Lo veremos cuando veamos OpenLayers.

* Repaso módulos

	En general, un módulo se compone de:
		- dependencias (nombre y variable con el valor de retorno)
		- variables locales del módulo
		- funciones privadas
		- inicialización
		- valor de retorno
	
	Ejemplos
		- Show toolbar initializing graphic components and listening for add-layer events
		- Show map creating the component and listening for events that other components will send
		- Show layout creating the HTML and returning an object
		- Show layer-list reacting to the add-layer event
		- Show main asking the server for the layers and triggering add-layer events
		- Show communication adding the "lang" parameter to every call and managing the errors

* Repaso jquery

Intro OpenLayers (dar tiempo a ver requirejs y poder así repasarlo el miércoles)
-----------------------------------------------------------------------------------

1) Create the HTML structure on any folder and access through file:// protocol

2) Create a map
	- Usamos 900913 porque es la que se usa en el portal
	
- Ejercicio: cambiar la proyección a 4326
- Ejercicio: eliminar algunos controles y ver cuáles son los cambios que operan en el mapa

3) Add some layers
	- Las de tasmania funcionan siempre, las de ecuador se quedaron en 503 durante un tiempo

4) Focus the map in the layers
	- Comment the zoom levels
	
Ejercicio: add the layers of ecuador and focus on Ecuador: http://www.geoportaligm.gob.ec/nacional/wms y igm:lim_costanero, igm:limite, igm:provincias, igm:represas, igm:rio_doble, igm:ferrocarril, igm:vias, igm:poblados, igm:aeropuertos, igm:islas

	function newIEELayer(name, wmsName) {
		return new OpenLayers.Layer.WMS(name, "http://www.geoportaligm.gob.ec/nacional/wms", {
			layers : wmsName,
			transparent : true
		}, {
			"attribution" : "Información del Instituto Espacial Ecuatoriano"
		})
	}
		map.addLayer(newIEELayer("Límite costanero", "igm:lim_costanero"));
		map.addLayer(newIEELayer("Límite continental", "igm:limite"));
		map.addLayer(newIEELayer("Provincias", "igm:provincias"));
		map.addLayer(newIEELayer("Represas", "igm:represas"));
		map.addLayer(newIEELayer("Ríos", "igm:rio_doble"));
		map.addLayer(newIEELayer("Ferrocarril", "igm:ferrocarril"));
		map.addLayer(newIEELayer("Vías", "igm:vias"));
		map.addLayer(newIEELayer("Poblados", "igm:poblados"));
		map.addLayer(newIEELayer("Aeropuertos", "igm:aeropuertos"));
		map.addLayer(newIEELayer("Islas", "igm:islas"));

5) Show coordinates
	
6) Show coordinates in lat/lon
	- Easy way to get the coordinates to center
	- Ejercicio: centrar en Italia

7) Algún otro control chulo (layer switcher, scale)

8) Control para consultar información de la capa y mostrarla por la consola
	- Explicar el problem de cross domain: Create a folder in portal and access it through the HTTP protocol
	
9) Custom click con temperatura

Más información: 

	- Web de OpenLayers
	- http://xeoinquedos.wordpress.com/2011/05/20/material-para-el-curso-de-openlayers/

Wednesday and Thursday 2.5 + 2.5 + 2.5 + 2.5
==============================================

El primer día va desde el cliente al servidor y el segundo al revés.

Modulo en el cliente
---------------------

Intro a requirejs y al event bus
................................

Vamos a mover los componentes que hemos programado en OpenLayers a una aplicación modular similar a la que se está desarrollando en FAO.

Ejemplo "base": Ejercicio ol1 pero creando los dos divs con un módulo main

Variante -jquery: Cargamos jquery y creamos los módulos a continuación

Variante -layout: Dejamos sólo la configuración en main y el layout se mete en el mapa

Ejemplo "mapa": Creamos un módulo que crea un mapa con una capa

Ejercicio: qué pasa si alguien cambia en layour el id del mapa? -> Hacerlo!

	Solución: layout puede devolver un valor -> mapa-id
	
Resumen: Nuevo módulo = crear fichero y añadirlo en la instrucción require de "main"

Ejercicio: Añadir un módulo con los controles de navegación (mapa-nav)

Ejercicio: Añadir un módulo que centre en Ecuador (mapa-center)

Event-bus demo (se puede hacer como ejercicio si hay tiempo)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Ejercicio: Añadir un módulo que ponga en el div de información los controles para añadir una capa (url y nombre) (nueva-capa)

Ejercicio: Añadir un módulo que liste las capas que hay en el mapa (nueva-capa-lista)

Ejercicio: Añadir un control que alerte cuando se añadan más de 3 capas (nueva-capa-limite)

Ejemplo de simplificación con message-bus (nueva-capa-bus)

	- Ejercicio: MOdificar la lista de capas para que ponga el nombre del servidor entre paréntesis

	- ¿Qué ficheros tenemos que modificar si queremos quitar la lista de capas? 

	- Ejemplo: añadimos otro mapa 

Creación de módulos en el portal
......................................

Explicación de los módulos actuales

- i18n

	Ejemplo: título
	
	Ejercicio: Poner abajo a la izquierda enlaces traducidos

- documentación sobre los eventos escuchados

Ejemplo: Creación de un módulo que añada la posición del mapa.

Ejercicio: 
- Creación de un módulo que añada la escala

Ejemplo: Creación de un módulo que instala un botón que abre un diálogo con una URL, un nombre de capa y un grupo en el que añadir la capa.

Ejercicio:
* Creación de un botón que instalar una capa fija creando un grupo fijo

Ejercicio:

* Un módulo que instala una herramienta para obtener la temperatura de la API: http://openweathermap.org/api
	- Preguntar si es a esto a lo que se refieren con sensores

Módulo en el servidor
----------------------

Vamos a hacer los ejercicios en la misma aplicación. luego vemos cómo crear un proyecto con todas nuestras modificaciones.

Comentar las dos versiones existentes: Servlet 2.x y Servlet 3.x

Comentar los objetos que tenemos en el ambiente del servidor

	- Filters: autenticación, gestión de parámetros, codificación de resultados (conversión a json), etc.
	- Servlets: Manejo de las llamadas.
	- ContextListeners: Inicialización y liberación de recursos.

- Ejemplo filtro: lang + communication on the client side

- Ejercicio filtro: sysout logging filter
	(request.getRemoteAddr() + " está accediendo a " + ((HttpServletRequest) request).getRequestURL().toString())

- Servlet: Listado de módulos
	Comentar al final que esto lo hace ya el ConfigServlet

- Servlet: Eliminación de un módulo (modificando el Config para que refresque)

- Ejercicio: Poner una capa no "active" por defecto.

Pregunta: ¿Cómo desplegaríamos todo esto en el servidor? -> Crear un proyecto aaparte, empaquetarlo y desplegarlo.

- Servlet Context: Visualizar la configuración actual.

- Creación de un proyecto maven war en eclipse con todos los ejercicios y despliegue en el servidor

Friday 2.5 + 1.5
================

Ejercicios completos
--------------------

Personalización de indicadores
................................

Creación de un indicador HTML

Creación de un indicador dinámico
...................................

Creación de un servicio que saca información de la base de datos y muestra algúnos datos

Alertas tala fuera de licencias
................................

Creación de herramienta para notificar tala

Validación vía QGIS

Creación de la vista con las alertas de las talas fuera de las zonas de licencias que han sido validadas

Creación de la vista con las alertas por validar que están fuera de una zona de licencia

Carga de las vistas en el sistema

Consulta de estadísticas en el evento mouse over

Conclusiones
=============

¿Cómo seguimos?

 - Soporte técnico desde Roma por skype
 - Demos online con los últimos avances de la plataforma
 - ¿Interesados en hacer algunos componentes generales?


