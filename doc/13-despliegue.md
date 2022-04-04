% Despegando tu aplicación web
% (manuel.freire@fdi.ucm.es)
% 2022.04.04

## Objetivo

> Despliegue en contenedor docker, parte II

## Cambios en infraestructura

- Antes: imagen corriendo en `contenedor.fdi.ucm.es`, con distintos puertos por usuario
	+ puertos raros = no funciona con Wifi Eduroam
- Ahora: imagen expuesta vía puertos estándar:
	+ SSH por navegador en `https://guacamole.containers.fdi.ucm.es`
	+ puerto 80 de vuestra aplicación expuesto en `https://vmXX.containers.fdi.ucm.es`
	+ poder exponer el puerto 80 requiere que seais `root` en el contenedor; pero sólo `/tmp` y `/root` se pueden escribir, resto *read-only*

## Agradecimientos

Infraestructura y configuración gracias a los esfuerzos de Iván Martínez

![https://www.e-ucm.es/people/ivan/](img/imartinez.png "Iván Martínez Ortiz"){ height=70% }

## Cómo entrar: cambios en autenticación

- Usuarios ahora de la forma **`vmXX`** (antes no hacían falta: el puerto de acceso indicaba el usuario)
- **Mismas contraseñas** que en anterior préstamo

~~~{.txt}
grupo           usuario  url ssh (vía jumphost)
g5-datacar      vm31     vm31.swarm.test
g8-tututor      vm32     vm32.swarm.test
g6-achapter     vm33     vm33.swarm.test
g2-restaurantes vm34	 vm34.swarm.test
g1-eataway      vm35     vm35.swarm.test
g7-snakewatch   vm36 	 vm36.swarm.test
g4-forgemeals   vm37     vm37.swarm.test
~~~

## Recordatorio de tmux

- Si lanzas un programa sin tmux, y cierras la sesión, el programa muere\footnote{a no ser que el programa haga cosas raras o uses herramientas tipo `nohup`}
- Tmux es muy sencillo de usar, y preserva los logs que genere tu programa:
	+ `tmux new -s iw` crea sesión "iw" (sólo si no la habías creado ya)
	+ `<ctrl+b> <d>` desconecta la sesión (haz esto antes de salir)
	+ `tmux a -t iw` se reconecta a la sesión "iw" (haz esto para volver a entrar)

## Cambios en la configuración

- Si quieres acceder a la aplicación vía `https://vmXX.containers.fdi.ucm.es`, necesitarás que se ejecute en el puerto 80. 
- Toca esta línea en tu `application.properties` antes de desplegar:

~~~{.txt}
# 8080 está bien para pruebas; pero necesitais desplegar en el 80
server.port=80
~~~

- Esto sólo es posible como `root`; afortunadamente, en este contenedor sois root.

## Acceso vía SSH + Jumphost

- Posible acceder vía ssh, para mayor comodidad si vas a copiar ficheros, montar vía `fuse`, etcétera
- Acceso sólo vía [jump server](https://en.wikipedia.org/wiki/Jump_server): sistema dedicado que hace de intermediario entre red interna e internet (otra opción: [bastion host](https://en.wikipedia.org/wiki/Bastion_host))

~~~{.txt}
local$ ssh -L 2222:vmxx.swarm.test:22 -N hop@containers.fdi.ucm.es
hop@containers.fdi.ucm.es's password: hop2021
jump$ ssh root@vmxx.swarm.test
root@vmxx.swarm.test's password: <tu password>
vmxx#
~~~

## Acceso usando WinSCP: 1/2

![](img/winscp-1.png "Configurando WinSCP"){ height=70% }


## Acceso usando WinSCP: 2/2

![](img/winscp-2.png "Configurando WinSCP"){ height=70% }

# Fin

## ¿?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)

