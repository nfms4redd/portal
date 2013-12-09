Introducción a servlet 3.0
==========================

La parte servidor del portal está construida sobre la especificación Servlet 3.0. En este contexto se pueden crear principalmente tres tipos de objetos: filtros, servlets y escuchadores de contexto o *ContextListeners*.

Servlets
---------

Los servlets son classes que heredan de ``javax.servlet.http.HttpServlet``::

	package org.fao.unredd;
	
	import javax.servlet.http.HttpServlet;
	
	public class HolaMundoServlet extends HttpServlet {
		
	}

El objetivo principal del servlet es proporcionar una respuesta a una petición HTTP. Si por ejemplo queremos retornar una petición GET deberemos de implementar el método ``doGet``::

	package org.fao.unredd;
	
	import java.io.IOException;
	
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	
	public class HolaMundoServlet extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			String nombre = req.getParameter("nombre");
	
			resp.getWriter().println("Hola mundo, " + nombre);
		}
	
	}

Podemos observar que es posible utilizar la instancia de ``HttpServletRequest`` que se pasa en el parámetro ``req`` para acceder a los parámetros de la petición GET, en este caso ``nombre``; y que podemos escribir la respuesta a través de la instancia de ``HttpServletResponse``.

Por último se necesario especificar al sistema en qué URL se debe acceder a dicho servlet. Para ello hay que registrarlo en el fichero web.xml de la siguiente manera::

	<servlet>
		<servlet-name>holamundo-servlet</servlet-name>
		<servlet-class>org.fao.unredd.HolaMundoServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>holamundo-servlet</servlet-name>
		<url-pattern>/holamundo</url-pattern>
	</servlet-mapping>

En el elemento ``servlet`` se le dice al sistema que hay un servlet de nombre ``holamundo-servlet`` y que es implementado por la clase ``org.fao.unredd.HolaMundoServlet`` (vista más arriba), mientras que en el segundo elemento se establece la URL en la que se puede acceder al servlet. Con esta configuración sería posible acceder al servlet en una URL similar a esta::

	http://localhost:8080/app/holamundo

.. note::

	La especificación Servlet 3.0 permite la inclusión de anotaciones en la clase que implementa el servlet para especificar la misma información que contiene el web.xml, de tal manera que el fichero no es necesario. Sin embargo, por simplicidad se evita el uso de anotaciones de este tipo en general. 

Filtros
---------




