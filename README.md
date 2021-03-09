# iw

Material para la asignatura de Ingeniería Web, edición 2020-21, de la Facultad de Informática UCM

Puedes consultar también plantillas de años pasados:

   - En el [2019-20](https://github.com/manuel-freire/iw/tree/version-del-curso-2019-20), usábamos HyperSQL en lugar de H2. Las clases de modelo eran más verbosas, porque las anotaciones de entidad estaban en los métodos (y no en los atributos), y no usábamos Lombok.
   - En el [2018-19](https://github.com/manuel-freire/iw1819), los websockets no eran obligatorios, y no usaban todavía STOMP
   - Cursos [2016-17 y 2017-18](https://github.com/manuel-freire/iw-1718)
   - Curso [2015-16](https://github.com/manuel-freire/iw-1516), utilizanod por primera vez Spring Boot

## Contenido

* en [/doc/](https://github.com/manuel-freire/iw/tree/master/doc) tienes las transparencias, en Markdown. Puedes leerlas tal cual están (es texto), o convertirlas a PDF u otro formato usando, por ejemplo, [Pandoc](https://pandoc.org). Tengo un [script](https://github.com/manuel-freire/fdi-utils) en python llamado `markdown-to-beamer` que es el que uso para generar las transparencias que subo a Campus Virtual y uso en clase.

* en [/demo](https://github.com/manuel-freire/iw/tree/master/demo) está el proyecto de demostración explicado en el [tutorial](https://github.com/manuel-freire/iw/blob/master/doc/05-tutorial.md)

* en [/plantilla](https://github.com/manuel-freire/iw1/tree/master/plantilla) está la plantilla recomendada para los proyectos de este año. Sobre un proyecto "desde cero", por ejemplo el visto en el tutorial, añade:

    - Perfiles para mantener una BD H2 en memoria o en disco
    - Seguridad con múltiples roles definidos, y persistiendo usuarios vía BD
    - Controladores con métodos para
        * crear usuarios programáticamente
        * subir y bajar ficheros de forma segura
    - Una clase auxiliar para configurar a dónde se suben los ficheros que se suben
    - WebSockets con STOMP funcionando
