.. _bootstrap:

Secuencia de inicio de la aplicación
====================================

La aplicación se inicia generando el fichero ``index.html`` mediante un motor de plantillas. Dicho motor rellena:

- Los tags ``style`` con las referencias a los ficheros .css del directorio de módulos.

- Una llamada al servicio ``config.js`` con el parámetro ``lang`` establecido al valor con el que se accedió a ``index.html`` (si se accedió con ``index.html?lang=es`` se generará la carga de ``config.js?lang=es``).

La llamada al servicio ``config.js`` devuelve un fichero javascript con la configuración de los distintos módulos. La configuración relevante para el inicio de la aplicación es la del módulo ``customization``, que incluye la lista de módulos existente en el fichero ``portal.properties`` en la propiedad ``client.modules``.

El módulo ``customization`` obtiene la lista de módulos de la configuración y se realiza la siguiente secuencia:

#. ``customization`` hace una llamada a ``require`` para cargar los módulos.
#. una vez cargados, se lanza el mensaje :ref:`modules-loaded<modules-loaded>`
#. el evento es escuchado por el módulo ``layers`` que fue cargado en el primer paso. ``layers`` lanza procesa el árbol de capas y lanza los mensajes :ref:`add-group<add-group>` y :ref:`add-layer<add-layer>` correspondientes.
#. ``layers`` lanza el mensaje :ref:`layers-loaded<layers-loaded>`

