% Despegando tu aplicación web
% (manuel.freire@fdi.ucm.es)
% 2024.02.25

## Objetivo

> Despliegue en contenedor docker

## Desplegando una aplicación web

- En un servidor físico dedicado
	+ requiere todo un servidor: espacio, alimentación, etcétera
	+ fácil de instalar: sólo hay un usuario, y eres tú
	+ requiere saber administrar el servidor: sistema operativo, actualizaciones, ...
- En un servidor físico compartido
	+ aprovecha mejor recursos
	+ problemas de comunidad de vecinos: reparto de recursos, seguridad entre aplicaciones web, ...
	+ administrador del servidor físico != administradores de cada web
- En un servidor virtual
	+ desacopla administración de servidores físicos de aplicaciones en sí
	+ "servidor dedicado" para sus usuarios: resuelve problemas de seguridad y compartición de recursos

## Contenedores vs máquinas virtuales estándar

- VM: *programa* ("hypervisor") que es capaz de ejecutar *sistemas operativos*
	+ VmServer, VirtualBox, Qemu, ...
	+ La VM *anfitriona* (host) emula hardware para los sistemas operativos *huesped* (guest)
	+ Cada huésped puede tener instalado sus propias aplicaciones
	+ VM no puede compartir RAM entre huéspedes, ya que cada uno piensa que está en su propio HW físico.
- Contenedor: *programa* que es capaz de compartir un único *sistema operativo*
	+ Docker
	+ Cada imágen (~VMs sobre núcleo del SO anfitrión) aislada de vecinas
	+ Menor aislamiento, pero también menor consumo de recursos: SO base compartido, RAM compartida
- También llamadas VMs pesadas vs ligeras

## Más terminología devops en mundo virtualizado

- Orquestación: un director, mucha orquesta (VMs)
	- Docker compose: muchos contenedores colaborando juntos en un mismo host
	- Docker swarm: contenedores en clúster
	- Puppet / Chef: operaciones sobre muchas máquinas a la vez; no exclusivo a VMs
	- Kubernetes (k8s): como swarm, pero a mayor escala todavía.
- Escalabilidad y emergencia
	- Escalabilidad requiere redundancia: con suficientes recursos que fallan ocasionalmente, será frecuente que haya fallos. 
	- Redundancia y recuperación de fallos requieren:
		+ configuración resistente a caídas: distribuida y con garantías de consistencia
		+ detección de caídas, y capacidad para reemplazar elementos caídos con copias frescas
	- Entramos en terreno de k8s y gestión de nubes (AWS, Azure, ...)

## Devops en IW 2023-24

- Contenedores docker a disposición desde laboratorios
	+ basta reservar en la web de petición de recursos de laboratorio
	+ seleccionad contenedores de IW 2024
- Acceso vía
	+ SSH por navegador en `https://guacamole.containers.fdi.ucm.es`
	+ puerto 80 de vuestra aplicación expuesto en `https://vmXXX.containers.fdi.ucm.es`; sois "root" en vuestras VMs. *With great power comes great responsibility*
	+ sólo `/tmp` y `/root` son escribibles; resto de sistema *read-only* (porque así ahorramos espacio)

## Agradecimientos

Infraestructura y configuración gracias a los esfuerzos de Iván Martínez

![https://www.e-ucm.es/people/ivan/](img/imartinez.png "Iván Martínez Ortiz"){ height=70% }

## Cómo entrar

- Usuarios ahora de la forma **`vmXXX`** 

~~~{.txt}
(ejemplo de curso muy anterior)
grupo           usuario  url ssh (vía jumphost)
g5-datacar      vm031     vm031.swarm.test
g8-tututor      vm032     vm032.swarm.test
g6-achapter     vm033     vm033.swarm.test
g2-restaurantes vm034	 vm034.swarm.test
g1-eataway      vm035     vm035.swarm.test
g7-snakewatch   vm036 	 vm036.swarm.test
g4-forgemeals   vm037     vm037.swarm.test
~~~

## Tmux: terminales que no eliminan su contenido

- Si lanzas un programa sin tmux, y cierras la sesión, el programa muere\footnote{a no ser que el programa haga cosas raras o uses herramientas tipo `nohup`}
- Tmux es muy sencillo de usar, y preserva los logs que genere tu programa:
	+ `tmux new -s iw` crea sesión "iw" (sólo si no la habías creado ya)
	+ `<ctrl+b> <d>` desconecta la sesión (haz esto antes de salir)
	+ `tmux a -t iw` se reconecta a la sesión "iw" (haz esto para volver a entrar)

## Cambios en la configuración

- Si quieres acceder a la aplicación vía `https://vmXXX.containers.fdi.ucm.es`, necesitarás que se ejecute en el puerto 80. 
- Toca esta línea en tu `application.properties` antes de desplegar:

~~~{.txt}
# 8080 está bien para pruebas; pero necesitais desplegar en el 80
server.port=80
~~~

- Esto sólo es posible como `root`; afortunadamente, en este contenedor *sois root*.

## Acceso vía SSH + Jumphost

- Posible acceder vía ssh, para mayor comodidad si vas a copiar ficheros, montar vía `fuse`, etcétera
- Acceso sólo vía [jump server](https://en.wikipedia.org/wiki/Jump_server): sistema dedicado que hace de intermediario entre red interna e internet (otra opción: [bastion host](https://en.wikipedia.org/wiki/Bastion_host))

~~~{.txt}
(Paso 1: primera pestaña; requiere VPN ó estar en UCM)

ssh -L 2222:vmXXX.swarm.test:22 -N hop@containers.fdi.ucm.es
hop@containers.fdi.ucm.es's password: hop2024

(Paso 2: SIN cerrar anterior)

ssh root@localhost -p 2222
root@vmxx.swarm.test's password: <tu password en VM>
vmXXX#
~~~

## Jumphost y utilidades gráficas

Una vez conectado el jumphost (Paso 1), posible usar SFTP/SSH (ver Paso 2) para transferir ficheros

* Lo más fácil es usar Ctrl+Alt+Shift y transferirlos vía Guacamole
* Lo siguiente más fácil es montar una consola para que ssh funcione "en local", y luego usar cualquier utilidad gráfica
* Y también puedes modificar el script `deploy.py`

## Scripts de utilidad

* Usar `mvn` en el contenedor es lento y feo 
* Para subir una aplicación completa, tienes que subir
	- el código
	- la base de datos
	- los archivos asociados
* La aplicación `deploy.py` (nueva en el repositorio de la plantilla) te ayuda con todo esto
* Pero no lanza la aplicación: tienes que lanzarla usando `SPRING_PROFILES_ACTIVE=container java -jar *.jar` desde TMUX

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

