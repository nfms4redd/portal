.. module:: unredd.install.system_packages

Software prerequisites
======================

The system needs some base applications: Oracle JDK 6, Tomcat 7, Apache 2, PostGIS 2.0 on PostgreSQL 9.1 and Open Foris Geospatial Toolkit.


JDK 6
-----

Download the latest Java SE 6 JDK from Oracle site:

  http://www.oracle.com/technetwork/java/javase/downloads/index.html

The file will be named, for example, ``jdk-6u45-linux-i586.bin`` for 32 bit Linux systems.

Create the directory ``/usr/lib/jvm`` if it doesn't exist::

  sudo mkdir /usr/lib/jvm

Copy the file to ``/usr/lib/jvm``, make it executable, and run it as root::

  chmod +x jdk-6u45-linux-i586.bin  
  sudo cp jdk-6u45-linux-i586.bin /usr/lib/jvm
  cd /usr/lib/jvm
  sudo ./jdk-6u45-linux-i586.bin

Delete the file ``jdk-6u45-linux-i586.bin``::

  sudo rm jdk-6u45-linux-i586.bin

The JDK will be installed under ``jdk1.6.0_xx`` directory.

Make a symbolic link to this installation. From ``/usr/lib/jvm``::

  sudo ln -s jdk1.6.0_45 default-java

Make it the default java alternative::

  sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.6.0_45/bin/java" 1
  sudo update-alternatives --config java

Check the java version::

  java -version

Should read::

  java version "1.6.0_45"
  Java(TM) SE Runtime Environment (build 1.6.0_45-b06)
  Java HotSpot(TM) Client VM (build 20.45-b01, mixed mode, sharing)


.. _unredd-install-tomcat6:

Tomcat 7
--------

Tomcat is a web application container. In the context of the NFMS platform it will contain the administrative interface, the dissemination portal and geoserver. In order to install Tomcat, download the latest tomcat 7 version from:

  http://tomcat.apache.org/download-70.cgi

Select the core binary distribution. The file will be named, for example, ``apache-tomcat-7.0.47.tar.gz``.

As superuser, move the file to ``/var/`` and uncompress it. Make a simpler ``tomcat`` link, so updates are easier in the future::
  
  sudo mv apache-tomcat-7.0.47.tar.gz /var
  cd /var
  sudo tar -xvf apache-tomcat-7.0.47.tar.gz
  sudo ln -s apache-tomcat-7.0.47 tomcat

The directory where tomcat binaries reside is known as ``CATALINA_HOME``. In our case::

  CATALINA_HOME=/var/tomcat

Delete the file ``apache-tomcat-7.0.47.tar.gz``::

  sudo rm apache-tomcat-7.0.47.tar.gz

Setting environment variables
..............................

The different applications that are contained in Tomcat require some custom configuration parameters. In order to do that, the file ``CATALINA_BASE/bin/setenv.sh`` must be edited:

   .. code-block:: sh

     # Java options
     JAVA_OPTS="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=128m -XX:PermSize=64m -XX:+UseConcMarkSweepGC -XX:NewSize=48m -Dorg.geotools.shapefile.datetime=true -DGEOSERVER_DATA_DIR=/var/geoserver/data -DGEOSERVER_LOG_LOCATION=/var/tomcat/logs/geoserver.log -Duser.timezone=GMT -DMINIFIED_JS=true -DPORTAL_CONFIG_DIR=/var/portal"

.. warning:: The two last parameters (MINIFIED_JS and PORTAL_CONFIG_DIR) are not necessary in the staging area because they are specific for the dissemination portal and the staging does not require it.

Configuring tomcat as a service
................................

#. Create ``tomcat7`` user::

	$ sudo useradd tomcat7
	
#. Make all the server tree structure belong to the ``tomcat7`` user::

	$ sudo chown -R tomcat7: /var/tomcat/
	
   .. warning:: It is important to add a slash (/) at the end of ``/var/tomcat/`` because it is a symbolic link and if the slash is not added only the symbolic link gets its owner changed.

#. Create the file ``/etc/init.d/ubuntuTomcatRunner.sh`` with this content:

 .. code-block:: sh

     #!/bin/sh
     #
     # /etc/init.d/tomcat7 -- startup script for the Tomcat 7 servlet engine
     #
     # Written by Miquel van Smoorenburg <miquels@cistron.nl>.
     # Modified for Debian GNU/Linux  by Ian Murdock <imurdock@gnu.ai.mit.edu>.
     # Modified for Tomcat by Stefan Gybas <sgybas@debian.org>.
     # Modified for Tomcat6 by Thierry Carrez <thierry.carrez@ubuntu.com>.
     # Additional improvements by Jason Brittain <jason.brittain@mulesoft.com>.
     # Adapted to run multiple tomcat instances for UN-REDD NFMS platform.
     
     set -e
     
     DESC="NFMS4REDD Tomcat"
     CATALINA_BASE=/var/tomcat/
     PATH=/bin:/usr/bin:/sbin:/usr/sbin
     DEFAULT=/etc/default/tomcat
     JVM_TMP=$CATALINA_BASE/temp
     
     if [ -r $CATALINA_BASE/bin/setenv.sh ]; then
             . $CATALINA_BASE/bin/setenv.sh
     fi
     
     if [ `id -u` -ne 0 ]; then
        echo "You need root privileges to run this script"
        exit 1
     fi
     
     # Make sure tomcat is started with system locale
     if [ -r /etc/default/locale ]; then
        . /etc/default/locale
        export LANG
     fi
     
     . /lib/lsb/init-functions
     
     if [ -r /etc/default/rcS ]; then
        . /etc/default/rcS
     fi
     
     
     # The following variables can be overwritten in $DEFAULT
     
     # Run Tomcat as this user ID and group ID
     TOMCAT7_USER=tomcat7
     TOMCAT7_GROUP=tomcat7
     
     # The first existing directory is used for JAVA_HOME (if JAVA_HOME is not
     # defined in $DEFAULT)
     JDK_DIRS="/usr/lib/jvm/default-java"
     
     # Look for the right JVM to use
     for jdir in $JDK_DIRS; do
         if [ -r "$jdir/bin/java" -a -z "${JAVA_HOME}" ]; then
        JAVA_HOME="$jdir"
         fi
     done
     export JAVA_HOME
     
     # Directory where the Tomcat 7 binary distribution resides
     CATALINA_HOME=/var/tomcat
     
     # Use the Java security manager? (yes/no)
     TOMCAT7_SECURITY=no
     
     # Default Java options
     # Set java.awt.headless=true if JAVA_OPTS is not set so the
     # Xalan XSL transformer can work without X11 display on JDK 1.4+
     # It also looks like the default heap size of 64M is not enough for most cases
     # so the maximum heap size is set to 128M
     if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="-Djava.awt.headless=true -Xmx128M"
     fi
     
     # End of variables that can be overwritten in $DEFAULT
     
     # overwrite settings from default file
     #if [ -f "$DEFAULT" ]; then
     #  . "$DEFAULT"
     #fi
     
     if [ ! -f "$CATALINA_HOME/bin/bootstrap.jar" ]; then
        log_failure_msg "$SERVICE is not installed"
        exit 1
     fi
     
     POLICY_CACHE="$CATALINA_BASE/work/catalina.policy"
     
     if [ -z "$CATALINA_TMPDIR" ]; then
        CATALINA_TMPDIR="$JVM_TMP"
     fi
     
     # Set the JSP compiler if set in the tomcat7.default file
     if [ -n "$JSP_COMPILER" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dbuild.compiler=\"$JSP_COMPILER\""
     fi
     
     SECURITY="no"
     if [ "$TOMCAT7_SECURITY" = "yes" ]; then
        SECURITY="-security"
     fi
     
     # Define other required variables
     CATALINA_PID="/var/run/$SERVICE.pid"
     CATALINA_SH="$CATALINA_HOME/bin/catalina.sh"
     
     # Look for Java Secure Sockets Extension (JSSE) JARs
     if [ -z "${JSSE_HOME}" -a -r "${JAVA_HOME}/jre/lib/jsse.jar" ]; then
         JSSE_HOME="${JAVA_HOME}/jre/"
     fi
     
     catalina_sh() {
        # Escape any double quotes in the value of JAVA_OPTS
        JAVA_OPTS="$(echo $JAVA_OPTS | sed 's/\"/\\\"/g')"
     
        AUTHBIND_COMMAND=""
        if [ "$AUTHBIND" = "yes" -a "$1" = "start" ]; then
           JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
           AUTHBIND_COMMAND="/usr/bin/authbind --deep /bin/bash -c "
        fi
     
        # Define the command to run Tomcat's catalina.sh as a daemon
        # set -a tells sh to export assigned variables to spawned shells.
        TOMCAT_SH="set -a; JAVA_HOME=\"$JAVA_HOME\"; source \"$DEFAULT\"; \
           CATALINA_HOME=\"$CATALINA_HOME\"; \
           CATALINA_BASE=\"$CATALINA_BASE\"; \
           JAVA_OPTS=\"$JAVA_OPTS\"; \
           CATALINA_PID=\"$CATALINA_PID\"; \
           CATALINA_TMPDIR=\"$CATALINA_TMPDIR\"; \
           LANG=\"$LANG\"; JSSE_HOME=\"$JSSE_HOME\"; \
           cd \"$CATALINA_BASE\"; \
           \"$CATALINA_SH\" $@"
     
        if [ "$AUTHBIND" = "yes" -a "$1" = "start" ]; then
           TOMCAT_SH="'$TOMCAT_SH'"
        fi
     
        # Run the catalina.sh script as a daemon
        set +e
        touch "$CATALINA_PID" "$CATALINA_BASE"/logs/catalina.out
        #chown -R $TOMCAT7_USER:$TOMCAT7_USER $CATALINA_BASE
        chown $TOMCAT7_USER "$CATALINA_PID" "$CATALINA_BASE"/logs/catalina.out
        start-stop-daemon --start -b -u "$TOMCAT7_USER" -g "$TOMCAT7_GROUP" \
           -c "$TOMCAT7_USER" -d "$CATALINA_TMPDIR" -p "$CATALINA_PID" \
           -x /bin/bash -- -c "$AUTHBIND_COMMAND $TOMCAT_SH"
        status="$?"
        set +a -e
        return $status
     }
     
     case "$1" in
       start)
        if [ -z "$JAVA_HOME" ]; then
           log_failure_msg "no JDK found - please set JAVA_HOME"
           exit 1
        fi
     
        if [ ! -d "$CATALINA_BASE/conf" ]; then
           log_failure_msg "invalid CATALINA_BASE: $CATALINA_BASE"
           exit 1
        fi
     
        log_daemon_msg "Starting $DESC" "$SERVICE"
        if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
           --user $TOMCAT7_USER --exec "$JAVA_HOME/bin/java" \
           >/dev/null; then
     
           # Regenerate POLICY_CACHE file
     #     umask 022
     #     echo "// AUTO-GENERATED FILE from /etc/tomcat7/policy.d/" \
     #        > "$POLICY_CACHE"
     #     echo ""  >> "$POLICY_CACHE"
     #     cat $CATALINA_BASE/conf/policy.d/*.policy \
     #        >> "$POLICY_CACHE"
     
           # Remove / recreate JVM_TMP directory
           rm -rf "$JVM_TMP"
           mkdir -p "$JVM_TMP" || {
              log_failure_msg "could not create JVM temporary directory"
              exit 1
           }
           chown $TOMCAT7_USER "$JVM_TMP"
     
           catalina_sh start $SECURITY
           sleep 5
              if start-stop-daemon --test --start --pidfile "$CATALINA_PID" --user $TOMCAT7_USER --exec "$JAVA_HOME/bin/java" \
              >/dev/null; then
              echo $?
              if [ -f "$CATALINA_PID" ]; then
                 rm -f "$CATALINA_PID"
              fi
              log_end_msg 1
           else
              log_end_msg 0
           fi
        else
                log_progress_msg "(already running)"
           log_end_msg 0
        fi
        ;;
       stop)
        log_daemon_msg "Stopping $DESC" "$SERVICE"
     
        set +e
        if [ -f "$CATALINA_PID" ]; then
           start-stop-daemon --stop --pidfile "$CATALINA_PID" \
              --user "$TOMCAT7_USER" \
              --retry=TERM/20/KILL/5 >/dev/null
           if [ $? -eq 1 ]; then
              log_progress_msg "$SERVICE is not running but pid file exists, cleaning up"
           elif [ $? -eq 3 ]; then
              PID="`cat $CATALINA_PID`"
              log_failure_msg "Failed to stop $SERVICE (pid $PID)"
              exit 1
           fi
           rm -f "$CATALINA_PID"
           rm -rf "$JVM_TMP"
        else
           log_progress_msg "(not running)"
        fi
        log_end_msg 0
        set -e
        ;;
        status)
        set +e
        start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
           --user "$TOMCAT7_USER" \
           >/dev/null 2>&1
        if [ "$?" = "0" ]; then
     
           if [ -f "$CATALINA_PID" ]; then
               log_success_msg "$SERVICE is not running, but pid file exists."
              exit 1
           else
               log_success_msg "$SERVICE is not running."
              exit 3
           fi
        else
           log_success_msg "$SERVICE is running with pid `cat $CATALINA_PID`"
        fi
        set -e
             ;;
       restart|force-reload)
        if [ -f "$CATALINA_PID" ]; then
           $0 stop
           sleep 1
        fi
        $0 start
        ;;
       try-restart)
             if start-stop-daemon --test --start --pidfile "$CATALINA_PID" \
           --user $TOMCAT7_USER --exec "$JAVA_HOME/bin/java" \
           >/dev/null; then
           $0 start
        fi
             ;;
       *)
        log_success_msg "Usage: $0 {start|stop|restart|try-restart|force-reload|status}"
        exit 1
        ;;
     esac
     
     exit 0


#. Create the file ``/etc/init.d/tomcat7``. It will contain the INIT block, the service name, and a description. The file contents would be:

     .. code-block:: sh

      #!/bin/sh
      ### BEGIN INIT INFO
      # Provides:          tomcat7
      # Required-Start:    $local_fs $remote_fs $network
      # Required-Stop:     $local_fs $remote_fs $network
      # Should-Start:      $named
      # Should-Stop:       $named
      # Default-Start:     2 3 4 5
      # Default-Stop:      0 1 6
      # Description:       Start Tomcat7.
      ### END INIT INFO

      . /etc/init.d/ubuntuTomcatRunner.sh

#. Make the file created in ``/etc/init.d/`` executable::

    $ sudo chmod +x /etc/init.d/ubuntuTomcatRunner.sh /etc/init.d/tomcat7

#. Launch tomcat::

	$ sudo service tomcat7 start
	
#. Check tomcat is up visiting ``http://localhost:8080/`` with a web browser.


Make services start at boot time
--------------------------------

Install ``chkconfig``::

  $ sudo apt-get install chkconfig

Hack to make chkconfig work under ubuntu 12.04::

  $ sudo ln -s /usr/lib/insserv/insserv /sbin/insserv

Add all of the services::

  $ sudo chkconfig -s tomcat7 on

Check their status::

  chkconfig --list


Apache 2
--------

Apache HTTP server will be used to redirect the different tomcat applications to accessible URLs under the standard HTTP port (80). This mapping will use the proxy_ajp Apache extension.

In Ubuntu systems, we can use the package managed Apache. Install it with apt-get command::

  sudo apt-get install apache2

Enable the proxy and proxy_ajp modules::

  sudo a2enmod proxy proxy_ajp

Restart the server::

  sudo service apache2 restart

Accessing http://localhost should display an **It works!** message.

AJP proxying
............

Configurations to connect to all backend webapp throught AJP are
in ``/etc/httpd/conf.d/proxy_ajp.conf``.

Create the file ``/etc/apache2/mods-available/proxy_ajp.conf`` and define the redirections to the various tomcat applications::

  # Don't rewrite hostname
  ProxyPreserveHost on

  # Staging and dissemination proxy rules
  ProxyPass        /geoserver   ajp://localhost:8009/geoserver
  ProxyPassReverse /geoserver   ajp://localhost:8009/geoserver
  ProxyPassReverse /geoserver/  ajp://localhost:8009/geoserver/

  # Proxy rules for the staging area
  ProxyPass        /admin   ajp://localhost:8009/admin
  ProxyPassReverse /admin   ajp://localhost:8009/admin
  ProxyPassReverse /admin/  ajp://localhost:8009/admin/

  # Proxy rules for the dissemination area
  ProxyPass        /portal   ajp://localhost:8009/portal
  ProxyPassReverse /portal   ajp://localhost:8009/portal
  ProxyPassReverse /portal/  ajp://localhost:8009/portal/


Create a link in mods-enabled::

  $ sudo ln -s /etc/apache2/mods-available/proxy_ajp.conf /etc/apache2/mods-enabled/proxy_ajp.conf

Restart Apache server::

  $ sudo service apache2 restart


GDAL
----

There are two alternatives to install GDAL. The first one, for Ubuntu based systems, uses the UbuntuGIS packages. This method manages all the needed dependencies, and provides installers for other Open Source GIS applications such as Grass, Mapserver, PostGIS, or Quantum GIS.

The second alternative is to manually buildg and install from the GDAL sources, which allows more control over the optional modules, the GDAL version, and the binaries location.


A. Using UbuntuGIS repository
.............................

Add the ubuntugis-stable repository, and update packages::

  sudo apt-get install python-software-properties
  sudo add-apt-repository ppa:ubuntugis/ppa
  sudo apt-get update

Install gdal binaries and python utilities::

  sudo apt-get install gdal-bin python-gdal

Check the version::

  gdalinfo --version


B. Building from source
.......................

We’ll build and install it from the sources::

  wget http://download.osgeo.org/gdal/gdal-1.8.1.tar.gz

Using an unprivileged account, untar the tar.gz
and enter into the created ``gdal`` dir. Then, build::

  ./autogen.sh
  ./configure  --with-python
  make

Get root privs and then::

  make install
  
In order to use python-gdal libs, you have to issue::

  export PYTHONPATH=/usr/local/lib64/python2.4/site-packages/
  export LD_LIBRARY_PATH=/usr/local/lib/ 

before running python scripts (e.g. ``gdal_merge.py``).

PostGIS
-------

In Ubuntu, use the package manager to install PostgreSQL 9.1 and other prerequisites needed for PostGIS building::

  sudo apt-get install build-essential postgresql-9.1 postgresql-server-dev-9.1 libxml2-dev libgeos-dev proj postgresql-9.1-postgis

.. note:: References:

   http://postgis.net/docs/manual-2.0/postgis_installation.html

You will need these PostGIS databases:

geoserver 
   DB for GeoServer vector layers.
app 
   DB for storing portal application data, such as feedback reports, on the dissemination system.
   
.. warning:: The set of databases that are necessary on the system depends on the concrete subsystem that is being installed. In concrete, the staging subsystem does not require the app database since it does not contain the portal.


Create users
............

Different users will be used for the various databases. Use the following instructions in psql console, setting the passwords as needed. Again, the *app* user is only necessary in dissemination.

app (replace ``------`` for the password for the user ``app``)::

  $ sudo -u postgres psql -c "CREATE USER app LOGIN PASSWORD '------' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE"
  
geoserver (replace ``------`` for the password for the user ``geoserver``)::

  $ sudo -u postgres psql -c "CREATE USER geoserver LOGIN PASSWORD '------' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE"


Create databases
................

Again, the *app* user is only necessary in dissemination.

app::

	$ sudo -u postgres createdb -O app app

geoserver::

	$ sudo -u postgres createdb -O geoserver geoserver
	$ sudo -u postgres psql -d geoserver -c "CREATE EXTENSION postgis;"
	


Configure PostgreSQL access
...........................

Configuration file is in ``/etc/postgresql/9.1/main/pg_hba.conf``::

   
   # TYPE  DATABASE        USER            ADDRESS                 METHOD
   # Database administrative login by Unix domain socket
   local   all             postgres                                peer
     
   # "local" is for Unix domain socket connections only
   local   all             all                                     md5
   
   # IPv4 local connections:
   host    all             all             127.0.0.1/32            md5
   # IPv6 local connections:
   host    all             all             ::1/128                 md5


Then, reboot the posgresql service::

  sudo service postgresql restart
  
Autostart
.........

Postgres does not start automatically by default. Activate it with the ``chkconfig`` command::

  sudo chkconfig -s postgresql on

Open Foris Geospatial Toolkit
-----------------------------------

Open Foris Geospatial Toolkit (OFT) is a a collection of prototype command-line utilities for processing of geographical data that is used in the context of the NFMS portal to create statistics about the forest coverage along time, for example. More information can be found here: http://km.fao.org/OFwiki/index.php/Open_Foris_Toolkit.

To install OFT it is necessary to install first some packages::

	$ sudo apt-get install gdal-bin libgdal1-dev libgsl0-dev libgsl0ldbl python-gdal python-scipy python-tk perl
	
Then, download OFT::

    $ wget http://foris.fao.org/static/geospatialtoolkit/releases/OpenForisToolkit.run

Make it executable::

    $ sudo chmod u+x OpenForisToolkit.run

and execute it::

    $ sudo ./OpenForisToolkit.run

This last command will show a license and will ask for agreement::

	Verifying archive integrity... All good.
	Uncompressing Open Foris Toolkit installer...........................................................................................................................................
	Installing new versions of OpenForis Toolkit tools and removing the old ones
	=========================================================================
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
	OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
	THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
	DEALINGS IN THE SOFTWARE.
	
	I have read and accepted the license terms available at
	http://km.fao.org/OFwiki/index.php/LICENSE
	=========================================================================
	1) Agree
	2) Disagree
	#? 1

It is just necessary to type "1" and hit ENTER. After this, the rest of the installation will follow::

	Checking for gcc compiler
	Checking for g++ compiler
	Creating log dir
	/home/nfms/.of-toolkit/log.1.4
	ok
	Checking for gdal and gsl libs
	
	looking for GDAL libs
	DEBUG
	Using -L/usr/lib -lgdal
	Looking for GSL libs
	Using -L/usr/lib -lgsl -lgslcblas -lm
	Installing new versions of bash scripts
	Installing new versions of python scripts
	Installing new versions of awk scripts
	Installing new versions R scripts
	Installing new versions of additional c and python libs
	Installing new versions of executables
	Compiled and installed 24/24 c programs
	Copying licences
	Done


Native JAI
----------

Download JAI from the JAI download page: http://download.java.net/media/jai/builds/release/1_1_3/.

Choose the appropriate file:

  * *i586* for the 32 bit systems
  * *amd64* for the 64 bit ones

In both cases chose the JDK version, not the JRE one. For example in case of a 32bit system::

	$ cd /tmp
	$ wget http://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-i586-jdk.bin

Make the file executable with ``chmod`` and execute it from the directory containing the JDK ::

	$ chmod u+x /tmp/jai-1_1_3-lib-linux-i586-jdk.bin 
	$ cd /usr/lib/jvm/default-java
	$ sudo /tmp/jai-1_1_3-lib-linux-i586-jdk.bin 

Accept the license

Finally, remove the .bin file::

  sudo rm /tmp/jai-1_1_3-lib-linux-i586-jdk.bin


Download JAI Image I/O from http://download.java.net/media/jai-imageio/builds/release/1.1.

As above, choose the appropriate file:

  * *i586* for the 32 bit systems
  * *amd64* for the 64 bit ones

For example in case of a 32bit system::

	$ cd /tmp
	$ wget http://download.java.net/media/jai-imageio/builds/release/1.1/jai_imageio-1_1-lib-linux-i586-jdk.bin

Make the file executable with ``chmod`` and execute it from the directory containing the JDK ::

	$ chmod u+x jai_imageio-1_1-lib-linux-i586-jdk.bin
	$ cd /usr/lib/jvm/default-java
	$ sudo /tmp/jai_imageio-1_1-lib-linux-i586-jdk.bin
	 
If you get the following error after the last command::

  tail: cannot open `+215' for reading: No such file or director

execute the following two commands::

	$ sed s/+215/-n+215/ /tmp/jai_imageio-1_1-lib-linux-amd64-jdk.bin > /tmp/jai_imageio-1_1-lib-linux-amd64-jdk-fixed.bin
	$ chmod u+x /tmp/jai_imageio-1_1-lib-linux-i586-jdk-fixed.bin 
	$ sudo /tmp/jai_imageio-1_1-lib-linux-i586-jdk-fixed.bin 

Remove the .bin files::

	$ sudo rm /tmp/jai_imageio-1_1-lib-linux-i586-jdk.bin
	$ sudo rm /tmp/jai_imageio-1_1-lib-linux-amd64-jdk-fixed.bin
