Módulos importantes
====================

Entre los módulos más importantes existentes en la plataforma podemos destacar:

- message-bus: Implementación del patrón message bus (Ver :ref:`message-bus`)
- communication: Facilita la comunicación con el servidor
- error: Gestión centralizada de los errores
- layout: Crea el layout de la página
- i18n: Contiene las cadenas de los ficheros de traducción .properties
- map: Crea el mapa principal
- layers: Lee la configuración de capas y lanza eventos ``add-layer`` y ``add-group``
- customization: Carga el resto de módulos

Message-bus
-----------

De los módulos anteriores, el más importante es ``message-bus``

**Función principal:** Ofrecer dos métodos para mandar y escuchar mensajes al/del bus. La documentación de las dos funciones puede encontrarse en el código fuente: https://github.com/nfms4redd/nfms/blob/develop/portal/src/main/webapp/modules/message-bus.js

**Valor de retorno:** Un objeto con dos propiedades ``send`` y ``listen`` que permiten respectivamente enviar y escuchar mensajes.

**Mensajes enviados:** Ninguno. El módulo se encarga de procesar y canalizar los eventos, pero no inicia ni escucha ninguno. 

**Mensajes escuchados:** Ninguno.