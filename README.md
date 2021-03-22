# PROGRAMA 2. IMPLEMENTACIÓN DE SERVICIOS REST PARA LA BASE DE DATOS controlescolar.

Este proyecto será usado tanto para el Programa 2 como para la Práctica 9. En la práctica 9, bajo la guía del maestro se implementarán los servicios REST para las tablas municipio y estudiante, las cuales son un subconjunto de lo que se hará para el programa 2. Se verá durante la práctica, que con ayuda de ciertas anotaciones en la clases de entidad, el API del Java Empresarial puede crear código para acceder a la tabla correspondiente de la  base de datos y hacer cierta validaciones (en Java Empresarial se usa el API JPA - Java Persistence API) y también se verá como instalar configurar el servidor de aplicaciones Glassfish, como instalar la aplicación web en Glassfish y como permitir el acceso remoto usando ngrok.

## PREREQUISITOS PARA ESTA PRACTICA

1. Tener instalado el Glassfish 5.1.0, que se puede bajar de [Página de Glassfish](https://projects.eclipse.org/projects/ee4j.glassfish/downloads) (la versión Full Profile)
2. Tener instalada la versión 8 de Java (Glassfish solo funciona para esa versión), que se puede bajar de [Página de Java 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) y configurarla como predeterminada (usando la variable de entorno JAVA_HOME)
3. Configurar IntellijIDEA para usar Glassfish (si es que utilizará Intellij para desarrollar el proyecto)
4. Para probar sus servicios se sugiere usar algun cliente REST como por ejemplo Postman, que se puede bajar la [Pagina de Postman](https://www.postman.com/downloads/) 
4. Para realizar las pruebas desde Github será necesario tener ngrok, el cual se puede bajar de [Página de ngrok](https://dashboard.ngrok.com/get-started/setup), además de haberlo configurado de acuerdo a lo que se especifica en la sección [CONFIGURACION DE ACCESO A SU COMPUTADORA PARA LAS PRUEBAS EN GITHUB](#configuracion-de-acceso-a-su-computadora-para-las-pruebas-en-github) y que se demostrará en las sesiones de la semana del 22 al 26 de marzo.

## COPIA DEL REPOSITORIO REMOTO EN SU COMPUTADORA LOCAL

Como primer paso, será necesario crear una copia local del repositorio remoto creado en Github al aceptar la tarea. Para ello, es necesario hacer los siguientes pasos:
1. Entrar a la página cuyo URL les fue proporcionado al aceptar la tarea, en tal página dé click en el botón Code y copie el URL que aparece en el cuadro de texto de nombre **Clone with HTTPS** (comienza con *https://*)

2. Si usarás IntellijIDEA entonces haz los siguientes pasos:
   - Abre IntelliJ IDEA e indica que harás un clon local de tu repositorio:
   - Si no tienes ningún repositorio abierto selecciona la opción **Get From Version Control** de la Ventana de Bienvenida, o si tienes un proyecto abierto, puedes entrar al menú **VCS**  y seleccionar la opción **Get From VCS**
   - En el cuadro de diálogo que aparece:
     - Selecciona Git
     - Pega el URL que copiaste en el paso 1  en el cuadro de texto **URL**
     - Selecciona en Directory la carpeta donde lo colocarás, es importante que crees una nueva carpeta o se colocará (da click en el icono de carpeta , navega a donde lo quieres colocar y da click en el icono de New Folder para crear una nueva carpeta)
     - Da click en **Clone**
     - Si te pide usuario y clave de Github proporciona esos datos
     - Después de unos segundos tendrás listo tu un clon de tu repositorio listo para trabajar en Intellij IDEA

3. Si NO usarás IntellijIDEA y harás todo desde la ventana de comandos usando un editor de texto tal como Sublime haz los siguientes pasos:

   - En una consola de Git Bash en Windows (o en una terminal en Linux o Mac), cree una carpeta donde quiera que se contengan sus prácticas del semestre (si es que aún no la has creado) y colócate en tal carpeta. La carpeta la puedes crear desde el Git Bash o terminal Linux/Mac usando el comando `mkdir` (o con el explorador de archivos de su sistema operativo) y en la consola de Git Bash o terminal de Linux/Mac te puedes cambiar a la carpeta mencionada usando el comando `cd`
   - Clone el repositorio privado dando el comando `git clone URL programa_02`
   (donde **URL** es el URL que copió en el paso 1)\
   Este comando creará dentro de la carpeta creada en el paso 2) una subcarpeta de nombre **programa_02** donde estará una copia local del repositorio remoto.\
   Los comandos posteriores de git tendrán que darse desde tal carpeta, por tanto, cámbiate a la carpeta usando `cd programa_02`

## MODIFICACIÓN DEL CÓDIGO PROPORCIONADO

Una vez que tengas el repositorio local, el trabajo principal consiste en crear las clases para proporcionar los servicios REST correspondientes a las tablas **estudiante** y **municipio** de la base de datos **controlescolar**, tales servicios serán representados por las clases EstudianteREST y MunicipioREST, ambas a colocarse en el paquete **poo2.progs.servicios**. Estas clases heredarán de la clase **RESTAbstracto<T>** la cual ya implementa la funcionalidad básica de la interface **DaoWeb<T>** (que es una interface muy similar a la interface **Dao<T>** usada en el Programa 1, pero con cambios para adaptarse a la forma en que un servicio REST regresa los resultados a través de Internet). En el método **agregaRecursosREST** de la clase **AplicacionConfig** deberá ponerse código para agregar las clases que representan a los servicios REST al Set que recibe como argumento.

La ruta para el servicio de estudiante deberá ser ***urlbase*/estudiante** y para el servicio municipio deberá ser ***urlbase*/municipio/{ide}** donde {ide} representa que se dará el ID del estado del cual se desean los municipios.

Las operaciones para agregar, actualizar o eliminar alguna estudiante deberán primero validar si es posible realizar la operación solicitada, de acuerdo a las reglas indicadas en el Programa 1, y si no es posible, deberán regresar "false".  

La lista de requerimientos completa que deben cumplirse para el programa 02 y Practica 9 los puedes encontrar en el archivo **POO2_Progr2EneJun2021.pdf**

## CONFIGURACION INICIAL

**Antes de hacer los cambios en el código haga las siguientes modificaciones**:

1. En el archivo index.jsp, incluido dentro de la subcarpeta **webapp** en la sección **main** del proyecto, deberá sustituir las NNNNNNNNNNNNN por su nombre completo y las XXXXXXXX por su matrícula, lo cual también deberá realizar en el archivo **datosmysql.properties** ubicado dentro de la subcarpeta **resources** en la sección **main**

2. En el archivo build.gradle modifique el valor de version para que se use su matrícula

3. Este paso depende del sistema operativo en que te encuentres:
   - Si estás usando Windows:
     - Edita el archivo asenv.bat, que se encuentra en la carpeta RUTA\glassfish5\glassfish\config (la RUTA depende de donde hayas desempacado el Glassfish) y agrega una linea como la siguiente (donde RUTA_AL_JAVA8 es la ruta a donde tengas instalado Java 8 en tu computadora)
       set AS_JAVA=RUTA_AL_JAVA8
   - Si estás usando Linux o Mac:
     - Edita el archivo asenv.conf, que se encuentra en la carpeta RUTA/glassfish5/glassfish/config (la RUTA depende de donde hayas desempacado el Glassfish) y agrega una linea como la siguiente (donde RUTA_AL_JAVA8 es la ruta a donde tengas instalado Java 8 en tu computadora)
       AS_JAVA=RUTA_AL_JAVA8

4. Este paso depende de si estarás usando Intellij IDEA o no:
   - Si estarás usando Intellij IDEA:
     - Asegurate de estar usando la versión 8 de Java (en el menu File-> Project Structure, Sección Project, Opción Project SDK)
     - Agrega una configuración de ejecución para poder poner a correr el Glassfish, para ello:
       - Dé click en Add Configuration...
       - D+e click en el +
       - Seleccione GlassFish Server -> Local
       - En el cuadro de diálogo que aparece seleccione domain1 en Server Domain, marca Preserve Sessions Across Redeployment, en la pestaña Deployment dé click en +, selecciona Artifact y selecciona RESTControlEscolar-XXXXXXXX.war (donde las XXXXXXXX ya debieran aparecer como su matricula), de click en OK, regrese a la pestaña Server y seleccione Redeploy en On 'Update' Action, de click en OK

   - Si estarás trabajando de manera manual (no usando IntelliJ IDEA):
     - Asegurate de tener a la versión 8 de Java como tu version predeterminada (agrega una variable de entorno a tu sistema operativo de nombre JAVA_HOME que tenga como valor la carpeta donde tienes instalado Java 8)

5. Agrega a tu variable PATH la ruta `RUTA\glassfish5\bin` (cambia las `\` por / en Linux o MAC) donde RUTA representa donde colocaste la carpeta de Glassfish

## CALIFICACIÓN PARA LA PRÁCTICA 09

Habrá un archivo para realizar las pruebas dentro de la sección **test**, en el paquete **poo2.prac09.main** que es la clase **testServiciosREST**, la cual realizará pruebas sobre los servicios REST para estudiante y municipio. El puntaje asignada a cada elemento de la prueba lo puedes encontrar en el archivo **POO2_Progr2EneJun2021.pdf**

## CALIFICACIÓN PARA EL PROGRAMA 2

Habrá un archivo para realizar las pruebas dentro de la sección **test**, en el paquete **poo2.progs.main** que es la clase **testServiciosREST**, la cual realizará pruebas sobre todos los servicios REST, Y deberá irse actualizando conforme lo indique el profesor, la clase incluida de manera inicial hace solo las pruebas de los servicios REST de estudiante y municipio. 

## COMO EJECUTAR LAS PRUEBAS DE MANERA LOCAL

Para ejecutar las pruebas de tu programa en tu computadora local:

1. Si estás usando Intellij IDEA selecciona **testServiciosREST** dentro de la sección test (en el paquete poo2.prac09.main si estás probando lo correspondiente a la práctica 9 o en el paquete poo2.progs.main si estás probando lo correspondinete al programa 2), dale click con el botón derecho y selecciona **Run** (la opción tendrá un triángulo verde ), te mostrará el resultado de las pruebas y la calificación obtenida.

2. Si estás trabajando fuera de Intellij IDEA:
   - Deberás hacer el **deploy** (instalación de la aplicación en Glassfish) realizando los siguiente pasos, se asume que hiciste todos los pasos de la sección de  [CONFIGURACION INICIAL](#configuracion-inicial) :
     - Iniciar el servidor Glassfish si es que aun no lo haz hecho,  dando el comando `asadmin start-domain`. Espera unos segundos en lo que arranca el servidor de Glassfish (no debe marcarte ningun error)
     - Compila tu proyecto, dando el comando `gradle war` estando ubicado en la carpeta del proyecto. Esto creará el archivo RESTControlEscolar-XXXXXXXX.war (donde XXXXXXXX ya debiera ser tu matricula) en la carpeta build\libs contenida en la carpeta del proyecto.
     - Si ya habías instalado la aplicación web y vas a probar una versión modificada, debes primero desinstalar la versión previa que se encuentra en  Glassfish, dando el comando `asadmin undeploy RESTControlEscolar-XXXXXXXX`
     - Instala en Glassfish la versión más reciente de tu aplicación web ejecutando el comando `asadmin deploy RUTAWAR\RESTControlEscolar-XXXXXXXX.war` donde RUTAWAR es la ubicación del archivo RESTControlEscolar-XXXXXXXX.war. Deberías ver un mensaje de que se pudo instalar de manera exitosa.
   - Ejecuta las pruebas deseadas dando el siguiente comando desde la carpeta del proyecto:
     - `gradle test --tests poo2.prac09.main.TestServiciosREST` para ejecutar las pruebas de la Práctica 09
     - `gradle test --tests poo2.progs.main.TestServiciosREST` para ejecutar las pruebas del Programa 2
   
## COMO SE CALIFICARA SU PRACTICA 09 Y PROGRAMA 2 EN GITHUB

El archivo de pruebas será ejecutado de manera automática en el Github al hacer push, aunque debido a la naturaleza del proyecto, el servidor Glassfish deberá estar corriendo en su computadora local y Github se comunicará con su servidor Glassfish y MySQL  por lo cual será necesario que tenga corriendo ngrok de acuerdo a lo que se demostrará en las sesiones del 22 al 26 de marzo y deberá haber colocado los datos correspondientes para que desde Github se pueda hacer conexión a su computadora cambiando los datos del archivo **datosmysql.properties**

## CONFIGURACION DE ACCESO A SU COMPUTADORA PARA LAS PRUEBAS EN GITHUB

Para efectos de que Github pueda ejecutar las pruebas, el servidor de Glassfish y MySQL a usar serán los de su computadora, por lo cual, será necesario configurar el software ngrok para permitir el acceso a Github a tales servicios de su computadora.

Para configurar ngrok, es necesario hacer lo siguiente:
1. Crear una cuenta en [el sitio web de ngrok](https://dashboard.ngrok.com/signup).
2. Entre al [dashboard de ngrok](https://dashboard.ngrok.com/get-started/your-authtoken) y copie el token de autorización que aparece (necesario primero hacer login)
3. En una terminal o ventana de comandos, coloquese en el directorio donde desempaco el ngrok y ejecute el siguiente comando `ngrok authtoken <SUTOKEN>` donde `<SUTOKEN>` se sustituye por el token de autorización copiado del paso anterior. Esto creará un archivo de configuración en su computadora de nombre ngrok.yml ubicado en (NOMUSUARIO se sustituye por su nombre de usuario):
   - C:\Users\NOMUSUARIO\.ngrok2\ngrok.yml (en Windows) 
   - /home/NOMUSUARIO/.ngrok2/ngrok.yml   (Linux)
   - /Users/NOMUSUARIO/.ngrok2/ngrok.yml  (MacOS)
4. Abra el archivo de configuración creado en el paso anterior y agregue las siguientes líneas (en el archivo ya debería haber una línea como la siguiente `authtoken: Hhshbshss64647SSHSHSY4636hy6346376373` :
```
tunnels:
  glassfish:
    proto: http
    bind-tls: false
    addr: 8080
  mysql:
    proto: tcp
    addr: 3306
```   

Una vez configurado, ahora cada vez que quiera ejecutas las pruebas en Github (que se ejecutan automáticamente al hacer un push) deberá:
1. Poner a correr ngrok ejecutando el siguiente comando en una terminal o ventana de comandos, ubicada donde desempaco ngrok: `ngrok start --all`
2. Aparecerá un texto como el siguiente en la ventana de comandos:
```
ngrok by @inconshreveable                                (Ctrl+C to quit)   

Session Status          online
Account                 NOMBRE DE USUARIO (Plan: Free)
Version                 2.3.35
Region                  United States (us)
Web Interface           http://127.0.0.1:4040
Forwarding              http://IDALEATORIO.ngrok.io -> http://localhost:8080
Forwarding              tcp://NUM.tcp.ngrok.io:NUMPUERTO -> localhost:3306
```
Las dos lineas forwarding especifican los datos requeridos para que Github se pueda conectar a su servidor de Glassfish y de MySQL.

3. Estos datos deben ser colocados en el archivo **datosmysql.properties** que se encuentra en la seccion main\resources:
   - El valor **NUM.tcp.ngrok.io** debe asignarse a la llave ubicacionmysql_remota
   - El valor **NUMPUERTO** debe asignarse a la llave puertomysql_remota
   - El valor **IDALEATORIO.ngrok.io** debe asignarse a la llave ubicacionglassfish_remota
   - La llave puertoglassfish_remota siempre valdrá 80

4. Una vez hechos los cambios en datosmysql.properties, haga un commit indicando que se hicieron los cambios para reflejar los datos de acceso para la fecha y hora en que se haga el commit, haga un push y mientras no terminen las pruebas que se hacen en el Github deje corriendo ngrok

5. Una vez que se terminen las pruebas, cierre el ngrok dando Ctrl-C en la ventana de comandos donde lo puso a correr

## NOTAS IMPORTANTES

1. Cada vez que completes un método hazle deploy al servicio (automático en Intellij al darle click en ejecutar la configuración de Glassfish) y ejecuta la prueba para verificar que las pruebas del método completado son exitosas

2. Una vez vez que completes un método y verifiques que pasa las pruebas haz un Commit usando el comando que corresponda de acuerdo a la forma en que estés trabajando en el proyecto (desde Intellij IDEA o desde la ventana de comandos). Asegúrate de incluir en el commit los archivos involucrados (usando git add en la terminal de comandos o seleccionandolo en IntelliJ) y de teclear un mensaje que describa los cambios realizados de manera clara y concisa (debe ser un mensaje que permita a otras personas darse cuenta de lo realizado).

3. Haz git push regularmente (no con cada commit, cada hora, por ejemplo), para mantener una copia de tu trabajo. Si el propósito es verificar que las pruebas funcionen en el Github, debes entonces haber hecho lo indicado en la sección de [CONFIGURACION DE ACCESO A SU COMPUTADORA PARA LAS PRUEBAS EN GITHUB](#configuracion-de-acceso-a-su-computadora-para-las-pruebas-en-github). Si solo quieres guardar una copia de tu trabajo, no necesitas hacer lo que se menciona en tal sección.  

8. Cada vez que subas tu proyecto al repositorio privado con un push, se aplicarán automáticamente las pruebas sobre tu código para verificar si funciona correctamente. Recuerda que en la página de tu repositorio en la sección **Pull Requests**, se encuentra una subsección de nombre **Feedback**, donde podrás encontrar los resultados de las pruebas en la pestaña denominada **Check** (expandiendo la parte que dice **Run education/autograding@v1**), y cualquier comentario general que el profesor tenga sobre tu código en la pestaña **Conversation**. 
