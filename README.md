# iw1920
Material para la asignatura de Ingeniería Web, edición 2019-20, de la Facultad de Informática UCM

## Contenido

* en [/doc/](https://github.com/manuel-freire/iw1920/tree/master/doc) tienes las transparencias, en Markdown. Puedes leerlas tal cual están (es texto), o convertirlas a PDF u otro formato usando, por ejemplo, [Pandoc](https://pandoc.org). Tengo un [script](https://github.com/manuel-freire/fdi-utils) en python llamado `markdown-to-beamer` que es el que uso para generar las transparencias que subo a Campus Virtual y uso en clase.

* en [/demo](https://github.com/manuel-freire/iw1920/tree/master/demo) está el proyecto de demostración explicado en el [tutorial](https://github.com/manuel-freire/iw1920/blob/master/doc/05-tutorial.md)

* en [/plantilla](https://github.com/manuel-freire/iw1920/tree/master/plantilla) está la plantilla recomendada para los proyectos de este año. Sobre un proyecto "desde cero", por ejemplo el visto en el tutorial, añade:

    - Perfiles para mantener una BD HyperSQL en memoria o en disco
    - Seguridad con múltiples roles definidos, y persistiendo usuarios vía BD
    - Controladores con métodos para
        * crear usuarios programáticamente
        * subir y bajar ficheros de forma segura
    - Una clase auxiliar para configurar a dónde se suben los ficheros que se suben
    - WebSockets funcionando
