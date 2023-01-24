% Introducción a html
% (manuel.freire@fdi.ucm.es)
% 2023.01.24

## Objetivo

> HTML y cómo usarlo, sin entrar en 
> interacción con JS, CSS, ni eventos

## Introducción

- HTML todavía tiene algunas etiquetas y atributos que describen presentación en lugar de estructura
    + **Presentación**: cómo mostrar las cosas (colores, alineamientos, posiciones, tamaños, etcétera)
    + **Estructura**: qué *papel* desempeñan las cosas (encabezado, elemento de lista, párrafo, título de imagen, …)
    + **Contenido**: el texto o recurso a presentar
    + **Comportamiento**: cómo reacciona frente a distintos eventos

- - -

- Usaremos HTML sólo para *estructura* y *contenido*
    + **CSS para presentación**
        - Con marcado HTML, hay que repetir estilos en *cada* elemento
        - Con .css separado, modularidad \
        Y usando el mismo desde varios sitios, reutilización.
    + **JS para comportamiento**
        - Con marcado HTML, hay que repetir comportamientos en *cada* elemento
        - Con .js separado, modularidad \
        Y usando el mismo desde varios sitios, reutilización.

## HTML

- **H**yper**t**ext **M**arkup **L**anguage
    + Hypertext: hipertexto; esencialmente, **texto con _enlaces_**\
    a otros textos/recursos
    + Markup: **marcado** mediante *etiquetas*, para especificar *estructura* y *formato*\footnote{excepto porque ya \emph{no} marcamos formato - del formato se encarga el CSS}
- Enlaces y recursos: 

~~~{.html}
<a href="cuenca.html">enlace a Cuenca</a>
<link rel="stylesheet" type="text/css" href="estilos.css"/>
<script type="text/javascript" src="comportamiento.js"></script>
<img alt="icono para rollover" src="icono.png" />
~~~

- - -

~~~{.html}
<button id="b1" type="submit">Enviar</button> // ejemplo 
~~~

- Marcado y etiquetas:
    + Anatomía

        * **`<`**: comienzo de la etiqueta
        * **`button`**: nombre de etiqueta
        * **`id="b1" type="submit"`**: atributos
        * **`>`**: fin del comienzo; ahora viene el contenido
        * **`Enviar`**: contenido. Posible aceptar texto, etiquetas (posiblemente sólo algunas), o combinaciones de texto y etiquetas. Algunas etiquetas no aceptan nada. 
        * **`</button>`**: cierre de etiqueta
    + Muchos atributos opcionales (id, class, …)
    + Cierre opcional en ciertas etiquetas (`<p>`, `<img>`, `<li>`, …)
    + A veces, posible usar "autocerrado" 

~~~{.html}
<img src="logo.jpg"/>     // equivale a <img src="logo.jpg"></img>
<script src="algo.js" />  // no funciona: <script> no autocerrable
~~~

- Tienen que estar correctamente anidadas 

## Detectando marcado erróneo

![Usad Firefox y Ctrl+U (ver fuente) para buscar [marcado erróneo](view-source:https://www.ucm.es/)](img/firefox-view-src.png){ height=60% }

## Elementos y Atributos

- Llamamos **elementos** a las etiquetas. 
- Usad **minúsculas** para elementos y atributos
   + En general, se *acepta* tanto mayúsculas como minúsculas: 

~~~{.html}
      <!-- queda horrible, aunque funcione: -->
      <sCrIpT TYpE="text/javascript"> ... </SCriPT>
      <!-- mejor: siempre minúsculas: -->
      <script type="text/javascript"> ... </script>
~~~

   + Referencia normativa: [Sección `12.1.2 Elements` del estándar](https://html.spec.whatwg.org/#elements-2) viviente de html5

- Los **atributos**, mejor con comillas dobles (`"`ó simples `'`)

~~~{.html}
 <z id="algo" />  // <!-- comillas dobles                          -->
 <z id='algo' />  // <!-- comillas sencillas; equivale a id="algo" -->
 <z id=algo   />  // <!-- sin comillas y *ni espacios*, también    -->
 <z disabled  />  // <!-- sin =, ni valor; equivale a disabled=""  -->
~~~

## Elementos y atributos no-html5

~~~{.html}
<patata patata="algo"></patata>  // ok*, no muestra nada
<patata></div>                   // error: no son pareja
<z />                            // error: no autocerrables
<z>Hola</z>                      // ok*, como div
~~~

- Elementos desconocidos: en general\footnote{/* - en principales navegadores, pero no 100\% estandarizado}, se tratan como `div`
    * equivalentes, pero no intercambiables: cada oveja con su pareja
    * tu página no será "html5 válido" si los usa ...
    * ... a no ser que uses un guión (`<p-atata>`), y te apuntes a 
    [lo último](https://html.spec.whatwg.org/multipage/custom-elements.html#customized-built-in-element)
- Atributos desconocidos: se ignoran (pero disponibles desde JS)
    * mejor usar `data-` atributos: estándares, y más JS-adaptados

## Códigos de escape

- como contenido de **elementos**:
    + `&lt;`  - como una referencia de carácter (usando la [lista oficial](https://html.spec.whatwg.org/#named-character-references))
    + `&#60;` - usando su [código ASCII](https://en.wikipedia.org/wiki/ASCII#Printable_characters), en decimal, a continuación de un `#`
    + `&#x3c;` - lo mismo, pero en hexadecimal, y precedido por `#x`

- como contenido de **atributos**:
    + igual, pero también hay que escapar los cierres (`"` ó `'`, según corresponda)

- en **URLs**:
    + hay que escapar todo lo que no sea `[a-zA-Z-._~]` (ver [rfc3986](https://tools.ietf.org/html/rfc3986)) \
      y si usais `utf8`, esto incluye [acentos o eñes](https://dev.w3.org/html5/spec-LC/urls.html). 
    + se escapa en hexadecimal, prefijando con `%`
    
      normal | en URL | referencia | decimal |   hex  
    --------:|:------:|:----------:|--------:|:------
       `<`   | `%3c`  | `&lt;`     | `&60;`  | `&#x3c;`
       `"`   | `%22`  | `&quot;`   | `&34;`  | `&#x22;` 
       `␣`   | `%20`  | *no tiene* | `&32;`  | `&#x20;` 

## Escapando HTML desde JS

~~~{.js}
  // de https://stackoverflow.com/a/6234804/15472
  function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;"); // "&apos;" no siempre existe
  }

  // y escapeURI(unsafe) ya está definida, para URLs
~~~

¿Y porqué escapamos esos caracteres y no otros?

- `&` evita problemas con escapes
- `<` los evita con etiquetas
- `>`, `"` y `'` los evitan cuando estás dentro de una etiqueta o incluso un atributo

## Comentarios

\large

~~~{.html}
  <!-- esto es un comentario-->

  <!-- 
    este comentario
    tiene varias líneas
   -->
~~~

\normalfont

**NO** anides comentarios; las reglas son algo [raras](https://html.spec.whatwg.org/multipage/syntax.html#comments):

- Empiezan con `<!--`
- Luego viene, opcionalmente, texto, que
    + no puede empezar por `>` ni `->`
    + no puede contener `<!--`, `-->`, ó `--!>`
    + no puede acabar por `<!-`
- Acaban con `-->`

## Estructura de un documento

~~~{.html}
  <!DOCTYPE html>
  <html lang="en-US">
  <head>
    <title>My favorite book</title>
    <style>
    body { color: black; background: white; }
    em { font-style: normal; color: red; }
    </style>
  </head>
  <body>
    <p>My <em>favorite</em> book of all time has <em>got</em> to be
    <cite>A Cat's Life</cite>. It is a book by P. Rahmel that talks
    about the <i lang="la">Felis Catus</i> in modern human society.</p>
  </body>
  </html>
~~~

(ejemplo de [https://html.spec.whatwg.org/#the-style-element](https://html.spec.whatwg.org/#the-style-element))

- - - 

![pantallazo de la web anterior en Chrome 71](img/fav-book2.png "pantallazo de la web anterior en Chrome 71"){ height=90% }

## Partes de la estructura

- `<!DOCTYPE html>` - dice que estamos usando HTML5
- `<html>` - elemento raíz, engloba todo el documento
- `<head>` - encabezado, para el título, estilos, metadatos sobre el documento, ...
    + `<title>` - el título que aparece en la pestaña
    + `<style>` - una hoja de estilos embebida (en formato CSS)
- `<body>` - cuerpo del documento: el contenido de la página. \
    Algunos elementos típicos:
    + `<h1>` - una cabecera
    + `<p>` - un párrafo
    + `<em>` - para dar énfasis; equivale a negrita
    + `<div>` - un fragmento de documento; para poderle poner estilos
    + ...

## La cabecera en más detalle

~~~{.html}
<html lang="en-US">
  <head>
    <title>un título</title>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="my-css-file.css">
    <script src="my-js-file.js"></script>    
  </head>
~~~

+ los idiomas son útiles para buscadores; códigos regionales siguen
  [ISO_639-1](https://en.wikipedia.org/wiki/ISO_639-1)
+ las codificaciones de caracteres evitan confusión en cliente. Usa UTF-8 (sobre todo si desarrollas bajo linux). Más sobre codificaciones en [este estupendo artículo](https://www.joelonsoftware.com/2003/10/08/the-absolute-minimum-every-software-developer-absolutely-positively-must-know-about-unicode-and-character-sets-no-excuses/)
+ los iconos quedan bonitos en las pestañas, junto al título. Puedes crearlos con `gimp` u otro editor de imágenes
+ ejemplos (y más detalles) de la [página de mozilla sobre cabeceras](https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/The_head_metadata_in_HTML)

## Más sobre codificaciones

> Una **codificación de caracteres** convierte una serie de \
> **puntos de código** en un **espacio de códigos** a **caracteres**

* Carácter ~ letra, sílaba, fonema, o incluso emoticono
* Algunas definiciones:
    - code-point: unidad atómica de información. \
    Texto = una sucesión de puntos de código
    - code-space: colección de unidades de codificación
    - code-unit: fragmento de punto de código en una codificación dada. 💩 requiere:
        - 3 unidades de codificación UTF-8 (3 bytes)
        - 1 unidad de codificación UTF-16 (2 bytes)
    - grafema: secuencia de 1 o más puntos de código que se muestran como una unidad gráfica
        - `ä` representable como `a` + `"`
        - ... y como su propio punto de código, porque es un clásico
    - glyph: lo que se pinta en pantalla, sacada de una fuente (*font*) dada

## Codificaciones históricas

* [ASCII](https://en.wikipedia.org/wiki/ASCII), 7 bits, con "carácteres de control"
* [ISO 8859](https://en.wikipedia.org/wiki/ISO/IEC_8859) / CP-1252
    - ISO-8859-1 (Latin-1), later updated to ISO-8859-15 (Latin-9): most western european languages. The update adds the `€` sign and full support for French, Finnish and Estonian.
    - ISO-8859-2 (Latin-2), later updated to ISO-8859-16 (LATIN-10): south-eastern european. The update adds € and changes some symbols.
    - ISO-8859-5 (Latin/Cyrillic) covers Russian and other Cyrillic-alphabet languages.
* [UTF-8](https://en.wikipedia.org/wiki/UTF-8)
    - unos 138k caracteres (v13). Algunos ocupan más bytes, otros menos. La mayoría, 1
    - actualizado periódicamente - ¡fuente de [emojis 🥳](https://unicode.org/emoji/charts/full-emoji-list.html)!

- - -

![Popularidad de codificaciones, según [wikipedia](https://en.wikipedia.org/wiki/UTF-8)](img/encoding-popularity.png)

## Enlaces en html

- Típicamente de salida, a veces "de entrada"

~~~{.html}
  <!-- de salida, tradicional -->
  <a href="destino#ancla"> contenido </a> 
  <!-- de llegada, usando "name" -->
  <a name="ancla"/> 
~~~

- Pueden especificar URLs parciales. Todo lo que falte se asume que es idéntico al origen actual

~~~{.html}
  <!-- base: https://en.wikipedia.org/wiki/Percent-encoding -->
  <a href="//www.ucm.es">Voy a https://www.ucm.es</a>
  <a href="Form_(HTML)">Voy 
    a https://en.wikipedia.org/wiki/Form_(HTML)</a>
  <a href="/wiki/Form_(HTML)">Y yo</a>
  <a href="../wiki/Form_(HTML)">Y yo</a>
  <a href="#Current_standard">Yo voy 
    Percent-encoding#Current_standard</a>
~~~

- Generalmente aparecen subrayados en azul; colores y estilos se pueden cambiar vía CSS
- Pueden contener también imágenes:

~~~{.html}
  <a href="http://www.ucm.es/"><img src="logo.png" /></a>
~~~

## Imágenes

~~~{.html}
  <!-- alt es muy útil para lectores de pantalla -->
  <img src="url_de_imagen" alt="texto_descriptivo"/>
~~~

- No omitais el `alt`:
    + lectores de pantalla
    + navegadores de modo texto
    + texto "tooltip"
    + usado por navegadores para clasificar imágenes
- Formatos recomendados:
    + **.png**: si tiene pocos colores, y los detalles pequeños son importantes. Ej.: capturas de pantalla
    + **.jpg**: si hay más colores y los detalles finos no se ven tanto: todo lo demás
    + **.svg**: para imágenes escalables, tipo gráficas, mapas vectoriales, logotipos...

- - -     

![Comparando formatos de imagen: jpg pixela cuando hay texto, png ocupa mucho en imágenes no-sintéticas](img/png-vs-jpg.png){ height=70% }
    
## Videos y audio

~~~{.html}
<!-- video controlable vía JS -->
<video src="brave.webm" autoplay controls>
 <track kind=subtitles src=brave.en.vtt 
    srclang=en label="English">
 <track kind=subtitles src=brave.de.vtt 
    srclang=de lang=de label="Deutsch">
</video>

<!-- video controlable vía JS -->
<audio src="brave.ogg" autoplay controls/>
~~~

- Muy similar a `<img>`
- Pueden especificarse controles "del navegador", o usar unos propios (vía JS)
- `<track>` permite especificar subtítulos a usar

## Texto 

~~~{.html}
<!-- encabezados, donde 1 es lo más grande -->
<h1>título principal<h1>, … <h6>título pequeñito</h6>

<em>enfatizado</em>     <i>y yo</i>  <!-- énfasis-cursiva -->
<strong>fuerte</strong> <b>y yo</b>  <!-- fuerte-negrita -->

<!-- preserva espacios y saltos de línea -->
<pre>
  x o .
  . x o
  x . o
</pre>

<!-- más raros, pero útiles -->
<code> code </code>   <!-- código (de programa, html, …) -->
<cite> cita </cite>   <!-- una cita de algo -->
<samp> sample </samp> <!-- salida de ejemplo -->
<kbd> keyboard </kbd> <!-- texto que debe escribir el usuario -->
~~~

## Listas

- `<ul>` para no-numeradas, `<ol>` para numeradas
- `<li>` para ítems (cierre opcional)
- Pueden anidarse, mezclarse, estilarse vía CSS,…

~~~{.html}
  <ol>
    <li>rojo</li>
    <li>verde</li>
    <li>azul
       <ul>
          <li>claro</li><li>oscuro</li>
       </ul>
    </li>
  </ol>
~~~
    
## Tablas

- `<table>`
    + `<thead>` (opcional) para las cabeceras
    + `<tbody>` (implícito) para el contenido
    + `<tr>` indica una fila (*row*), 
    + `<td>` para celdas (*data*), ó `<th>` para celdas-cabecera (*header*)
      * atributo rowspan: cuántas filas abarca esta celda
      * atributo colspan: cuántas columnas abarca esta celda

~~~{.html}
  <table>
  <thead>
    <tr><th rowspan="2"><th colspan="2">Media
        <th rowspan="2">Ojos<br>rojos
    <tr><th>altura<th>peso
  </thead>
  <tbody>
    <tr><th>Machos 	<td>1.9<td>0.003<td>40%
    <tr><th>Hembras	<td>1.7<td>0.002<td>43%
  </tbody>
  </table>
~~~

- - - 

![Tabla sencilla](img/html-table.png "Tabla sencilla"){ height=40% }
    
~~~{.html}
<!-- en el <head>, algo de CSS para realzar cabeceras -->
<style>
  th {background-color: #eeeeee;} 
  td {text-align: center;}
</style>
~~~

## Formularios

~~~{.html}
<form action="acción" method="método">… <form>
~~~
- form – formulario en inglés
- acción – URI destino que recibirá el resultado
    + "mailto:manuel.freire@fdi.ucm.es" – envía por correo
    + "http://www.example.com/respuesta.php" – envío a página
- método – si se envía el resultado a una página, puede ser
    + **`GET`**: método por defecto; Restricciones de tamaño
    Para operaciones idempotentes (ej.: cambios de vista). El navegador no solicita confirmación para repetirlo; transparente a los botones de "adelante" y "atrás" del navegador.
    + **`POST`**: permite el envío de más información, y de tipos más sofisticados (por ejemplo,  ficheros). Para operaciones que implican cambios no-reversibles (ej.: confirmaciones). El navegador solicita confirmación antes de reenviar.

- POST vs GET: ¿estás cambiando algo de forma irreversible?
    + Un botón de 'refrescar listado' puede enviar 'GET'
    + uno de 'borrar archivo' (irreversible) sólo debe enviar 'POST'.

## Controles

- Un formulario se compone de controles (campos) individuales
- Cada control contiene atributos 'name' y 'value'

~~~{.html}
  <!-- enviará nombre=nadie -->
  <input type="text" 
    name="nombre"            
    value="nadie"
  />
~~~

- name: clave del campo
- value: valor inicial (se puede omitir; en general, sobreescribible)

## Enviando por GET vs POST 

- Ambos usan la misma codificación: pares *clave*=*valor*, separados por **'&'**
- Post requiere atributo `method="POST"` en `<form>`
- Get no es necesario especificarlo (pero `method="GET"` también funciona)

![Post vs Get](img/post-vs-get.png "Post vs Get"){ height=60% }

## Controles clásicos

~~~{.html}
Texto
 corto       <input type="text">
 contraseñas <input type="password">
 largo       <textarea>           <!-- sin input ni type -->

Botones
 clásicos  <input type="button">  <!-- también type=submit, reset -->
 dedicados <button type="button"> <!-- también type=submit, reset -->

Selección
 múltiple  <input type="checkbox">
 única     <input type="radio">
 Menú      <select>

Fichero   <input type="file">

Invisible <input type="hidden">
~~~

## Controles html5

~~~{.html}
Texto
 search   <input type="search">
 tel      <input type="tel">
 url      <input type="url">
 email    <input type="email">
 number   <input type="number">
 
Texto/tiempo
 time       <input type="time">
 date       <input type="date">
 month      <input type="month">
 week       <input type="week">
 fecha+hora <input type="datetime-local">

Otros
 color    <input type="color">
 range    <input type="range">
~~~

## Botones

- Tres tipos:
    + `submit`: botón de envío; envía el formulario a su destino
    + `reset`: pone todos los campos a sus valores iniciales
    + `button`: no hace nada; útil en combinación con JavaScript
    
- Dos variantes; preferible `<button>`:

~~~{.html}
<!-- ignora su contenido; usa 'valor' como etiqueta -->
<input type="tipo" name="identificador" value="valor" />
<!-- contenido (texto, imágenes, …) como etiqueta -->
<button type="tipo" name="identificador" value="valor">…</button>
~~~ 
(nota: tipo puede ser `submit`, `reset` ó `button`)

- Múltiples botones submit y envío del formulario:
    + Se adjunta el par *name*=*value* sólo del botón que haya hecho el envío
    + Si se envía mediante JavaScript, no se adjunta nada.

- - -

![algunos botones](img/buttons.png "algunos botones"){ width=100% }
    
    
## Selección: radio

![select con `radio`](img/select-radio.png "select con `radio`"){ height=90% }

## Selección: checkbox

![select con `checkbox`](img/select-checkbox.png "select con `checkbox`"){ height=90% }

## Selección: select

`<select name="nombre" multiple size="N"> opciones </select>`

  - `multiple`: opcional; si se especifica se permite selección múltiple
    
  - `size`: número de opciones a mostrar por defecto

`<option value="valor" selected> contenido </option>`

  - *valor*: lo que se enviará si se selecciona. Por defecto, 
    el contenido (pero mejor especificarlo)
  - `contenido`: contenido visual de la opción

- - -

![opciones para combobox/select](img/select-options.png "opciones para combobox/select"){ width=110% }

## Ficheros

`<input type="file" name="nombre" size="N" />`

  - `<form>` debe de tener
    + `method="POST"`
    + `enctype="multipart/form-data"`
  - `size` (opcional): ancho del campo, en caracteres

![subida de ficheros](img/file-upload.png "subida de ficheros"){ width=110% }

# Fin

## ¿dudas?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
