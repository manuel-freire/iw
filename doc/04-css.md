% Introducción a css
% (manuel.freire@fdi.ucm.es)
% 2019.02.03

## Objetivo

> CSS básico y marcado estructural en HTML

## Introducción

- CSS permite estilar páginas web. Hay dos grandes tipos de estilado:
    + *qué va dónde* - la disposición (*layout*) de la página
    + *cómo se pinta cada cosa* - el estilo visual de los elementos
- Pero necesita poder referirse a **fragmentos** de estas páginas para indicar qué estilos usar **dónde**
- Veremos tanto
    + Estructuración de páginas web para facilitar su estilado
    + CSS: estilando/decorando elementos
    + CSS: disposición/colocación de elementos

## Estructuras estilables en HTML

- todos los elementos (`<p>`, `<body>`, `<img>`) son directamente estilables... 
    + ... pero no todo lo que quieres estilar son elementos sueltos
    + ... puedes querer agrupar cosas para estilarlas (colocar / decorar) juntas.

- elementos *sin representación visual*, que sólo sirven para **estructurar**:
    + `<span>`: marca un fragmento de texto dentro de otro \
    (por ejemplo, una palabra o expresión dentro de un párrafo). 
    + `<div>`: marca un conjunto de elementos para estilarlos juntos \
    (por ejemplo, un título con varios párrafos).

- con html5, más tipos de `<div>`s:    
    + `<header>` - para cabeceras
    + `<section>` - para conjuntos de contenidos-principales \
    (ej.: en un periódico, la *sección de deportes*)
    + `<article>` - para elementos de contenido-principal \
    (ej.: en un periódico, una noticia; en un blog, una actualización)
    + `<aside>` - para cosas relacionadas con el contenido principal
    + `<nav>` - para navegar por el sitio
    + `<footer>` - para pies de página

## Identificadores y clases

- Todas las etiquetas HTML aceptan los atributos `id` y `class`
- El valor de `id` 
    + **no puede estar duplicado** en ningún otro elemento del mismo documento
    + no puede contener espacios
- El valor de `class`
    + puede estar duplicado cuantas veces desees
    + si contiene espacios, **cada palabra es el nombre de una clase distinta** \
    (y que el elemento pertenece a *todas* esas clases). 
    + El orden de los nombres de clase es *indiferente*.
    
~~~{.html}
    <!-- clases "uno", "dos", "tres", con id "principal" -->
    <div class="uno dos tres" id="principal"> ... </div>
    
    <!-- clases "uno" y "dos", con id "secundario" -->
    <span class="dos uno" id="secundario"> ... </span>

    <!-- error, id duplicado -->
    <div id="secundario"> ... </div>    
~~~

## CSS

- Cascading Style Sheet = Hojas de Estilo en Cascada
    + Definen *estilos* a aplicar a *fragmentos*, identificados por...

~~~{.css}
a { … }               /* tipo: todos los enlaces */
li > a { … }          /* jerarquía: <a>s 1eros hijos de un <li>*/
button#borra_12 { … } /* identificador: botón con id="borra_12" */
button.borrar { … }   /* clase: botones con class="borrar" */
/* ... y combinaciones de los anteriores */
~~~

- Ventajas
    + Separación de Presentación y Contenido
        + Posible cambiar presentación sin tocar contenido, y viceversa
        + Posible aplicar la misma hoja CSS a muchas páginas: consistencia y concisión\
        (en HTML-con-estilos, cada elemento tendría que llevar su estilo…)
        + Posible aplicar distintas hojas CSS a una misma página: distintas presentaciones\
        (ejemplo: pantalla grande, dispositivo móvil, papel, …)
    + CSS permite especificar muchos más estilos que HTML-con-estilos


## Historia

- 1993-6: HyperText Markup Language 1 (borradores circulando desde 1991)
- 1995-11: HTML 2
- 1995-11:  ficheros formularios <input type="file" … />
- 1996-5:  tablas <table> …
- 1996-8:  mapas de imágen en cliente <map name="…"> … CSS 1
- 1997-1:  internacionalización (<… lang="…">, cabeceras)
- 1997-1: HTML 3.2 (w3c)
- 1997-12: HTML 4.0 (strict, transitional, frameset)
- 1998-4: HTML 4.0: corrige errores menores
- 1999-12: HTML 4.01 (strict, transitional, frameset) CSS 2 extiende CSS 1
- 2000-1: XHTML 1.0 ~ HTML 4.0.1 (strict, transitional, frameset)
- 2000-5: ISO HTML
- 2001-5: XHTML 1.1 (basado en strict, modular)
- 2002-2006: XHTML 2.0 (rompe con HTML; descontinuado en 2009)
- 2008: HTML5 + CSS 2.1, corrigiendo CSS2
- 1999-actualidad: CSS3, modular

## Estado de módulos CSS3, según Wikipedia

---------------------------------------------------------------------------------
Module              Specification title                         Status    Date   
------------------- ------------------------------------------- --------- -------
css3-mediaqueries   Media Queries                               Rec       Jun'12

css3-color          Color Module Level 3                        Rec       Jun'18

css3-ui             Basic UI Module Level 3                     Rec       Jun'18

css-fonts-3         Fonts Module Level 3                        Rec       Sep'18

css3-layout         Template Layout Module                      Note      Mar'15

css-cascade-3       Cascading and Inheritance Level 3           CR        May'16

mediaqueries-4      Media Queries Level 4                       CR        Sep'17

css3-background     Backgrounds and Borders Module Level 3      CR        Oct'17

selectors-3         Selectors Level 3                           CR        Jan'18

css3-page           Paged Media Module Level 3                  WD        Mar'13

css3-gcpm           Generated Content for Paged Media Mod.      WD        May'14

css3-content        Generated & Replaced Content Mod.           WD        Jun'16

selectors-4         Selectors Level 4                           WD        Feb'18

css3-multicol       Multi-column Layout Module Level 1          WD        May'18

css3-box            basic box model                             WD        Jul'18
---------------------------------------------------------------------------------
(según [la página de CSS de la wikipedia](https://en.wikipedia.org/wiki/Cascading_Style_Sheets), a febrero de 2019)

## Versiones CSS

- CSS 1
    + Fuentes y propiedades de texto
    + Alineamientos, márgenes, bordes, posicionado\
    (esencialmente, lo que hacía HTML por aquellos momentos)
- CSS 2 / 2.1
    + Z-index, posicionamiento absoluto, relativo y fijo
    + Tipos de medios (papel , proyección, pantalla, pda, hablado…)
    + Soporte para textos izquierda-a-derecha (hebreo, árabe, …)
- CSS 3
    + Varios módulos (~40) independientes
    + En Recommendation / Candidate Recommendation:
        * Más selectores
        * Grid, FlexBox (css3-layout, css-flexbox-1)
        * Cursores y navegación multidispositivo (css-ui)
        * Backgrounds & Borders
        * Media Queries, Namespaces
        * Color
    + Desarrollo prosigue en otros muchos modulos
- comprueba en [caniuse.com](caniuse.com) para saber si usar algo o no. \
(ejemplo con selectores css2: [https://caniuse.com/#feat=css-sel2](https://caniuse.com/#feat=css-sel2))

## Formas de incluir CSS en HTML

- **Embebido**, como parte de una etiqueta HTML

~~~{.html}
  <body> <p style="color: red;">Texto en rojo</p> </body>
~~~

- **Interno**, como un elemento HTML de tipo **`<style>`**

~~~{.html}
  <head> <style type="text/css"> p { color: red; } <style> </head>
  <body> <p>Texto en rojo</p> </body>
~~~

- **Externo**, como un fichero referenciado vía **`<link rel="stylesheet">`**
    - *Usad esta variante* (a no ser que tengais muy buena excusa)
    - Sí, a mí también me [parece inconsistente](https://stackoverflow.com/q/14970406/15472) que no haya un `<style src="algo.css">`

~~~{.html}
  <!-- en el html -->
  <head> <link rel="stylesheet" type="text/css" href="ej.css"/> </head>
  <body> <p>Texto en rojo</p> </body>
~~~
~~~{.css}
  /* en ej.css */
  p {color: red}
~~~

## Anatomía de una hoja de estilos

- Un documento CSS contiene **reglas**, que se componen de 
    * un **selector**, que dice sobre qué elementos (y sus descendientes) se aplican
    * parejas **`propiedad: valor;`**, que dicen cómo estilar los elementos seleccionados

~~~{.css}
/* una regla; selector: `body` */
body {
  font-family: Verdana;
  font-size: 1em;
  text-align: justify
}
/* otra regla; selector: `h1, h2` */
h1, h2 {
  color: blue;
}
/* y una regla más; selector: `code.example` */
code.example {
  font-family: Courier;
  font-size: 1em
}
~~~
    
## Sintaxis CSS

- Comentarios: `/* */`, en cualquier sitio

~~~{.css}
    /* soy un comentario */
~~~

- Directivas (*at-rules*):

~~~{.css}
    @import url(general.css) /* <-- como un #include */
    @media print {           /* <-- aplicada sólo al imprimir */
        font-size: 12pt;
    }
~~~

- Reglas típicas:

~~~{.css}
    h1, h2 {                  /* <-- envueltas en { }  */
        color: blue;          /* <-- acaban en `;` */
        font-family: Courier;
    }
~~~

## Tipos de selectores

- etiquetas, tal cual $\Rightarrow$ **etiqueta**

~~~{.css}
    a { ... }  /* <a ...> ... </a>  */
    p { ... }  /* <p> ... </p>      */
~~~

- clases, con `.` $\Rightarrow$  **`.`clase**

~~~{.css}
    a.externo { ... }   /* <a class="externo" ...> ... </a>  */
    p.verso { ... }     /* <p class="verso"> ... </p>        */
~~~

- identificadores, con `#` $\Rightarrow$ **`#`identificador**

~~~{.css}
    a#w3c { ... }       /* <a id="w3c" ...> ... </a>  */
    p#neruda { ... }    /* <p id="neruda"> ... </p>   */
~~~

- pseudo-clases y pseudo-elementos, con `:` $\Rightarrow$ **:pseudo**

~~~{.css}
    a:hover { ... }         /* sólo con el ratón encima */
    p:first-letter { ... }  /* sólo la 1ª letra         */
~~~

## Selectores (en CSS 2.1)

----------------------------------------------------------------------------------
Selector                    Significado 
--------------------------- ------------------------------------------------------
*                           Cualquier elemento

etiqueta                    Los elementos HTML con esa *etiqueta*

E.clase / E#id              **E**s de la clase *clase* / con el id *id*

A E                         **E**s que descienden de un **A**ntecesor

P > E                       **E**s *hijos directos* de un **P**adre

P:first-child               Primeros hijos de un **P**adre



E:link / E:visited          **E**s (enlaces) no-visitados (`link`), \
                            ó visitados (`visited`)

E:active / E:hover /        **E**s siendo pulsados, 
E:focus                     que tienen el puntero encima, o que están enfocados                            

E:lang(L)                    **E**s en una declaración de idioma `lang="L"`

H + E                       **E**s precedidos immediatamente por un **H**ermano

E[foo]                      **E**s con atributo `foo`, sea cual sea su valor

E[foo="V"]                  **E**s con atributo `foo` *igual a* `V`

E[foo~="V"]                 **E**s con atributo `foo` que *contienen palabra* `V`

E[foo|="V"]                 **E**s con atributo `foo` que *empiezan* por `V-`

----------------------------------------------------------------------------------

## Algunos selectores CSS 3

----------------------------------------------------------------------------------
Selector                    Significado 
--------------------------- ------------------------------------------------------
H ~ E                       **E**s precedido por un **H**ermano\
                            (con `+`, hermano precede *inmediatamente*)

E[foo^="V"]                 **E**s con atributo `foo` que *empiezan* por `V`\
                            (con `|=`, empieza por `V-`: ojo con el guión)

E[foo*="V"]                 **E**s con atributo `foo` que *contiene* `V`\
                            (con `~=`, `V` debe ser palabra independiente)                                         
                            
F || E                      **E**s que son celdas en una columna con encabezado F

E:not(S)                    **E**s que *no* serían seleccionados por el selector S

E:is(S)                     **E**s que *sí* serían seleccionados por el selector S

E:has(S)                    **E**s donde, aplicando S, algun sub-elemento sería seleccionado
----------------------------------------------------------------------------------

El [estándar de selectores](https://www.w3.org/TR/selectors) tiene también muchas más pseudo-clases, incluyendo:

- :enabled / :disabled / :default $\Rightarrow$ uso en formularios
- :nth-of-type(n) / :nth-last-of-type(n) $\Rightarrow$  para elegir del principio ó el final de un conjunto de elementos
- :nth-child(n) / :nth-last-child(n) $\Rightarrow$  para elegir del principio ó el final de entre los hijos de un elemento dado

## Ejemplos varios

~~~{.css}
    /* `input` justo detrás de un elemento con id `username` */
    #username + input { ... }    
    
    /* campo `input` que te pide tu `username` */
    input[name="username"] { ... }
    
    /* botones con `id` que empieza por el texto `borrar` */
    button[id^=`borrar`] { ... }
    
    /* un montón de cabeceras distintas */
    h1, h2, h3, h4, h5 { ... }
    
    /* elementos de la clase .form-control (y compañía),
       hijos directos de un .input-group;
       sacado del CSS de Bootstrap v4 */
    .input-group > .form-control,
    .input-group > .form-control-plaintext,
    .input-group > .custom-select,
    .input-group > .custom-file { ... }
~~~

## Practicando con selectores

Un juego serio para practicar selectores: [CSS Diner](https://flukeout.github.io/)

![https://flukeout.github.io/](img/css-diner "whttps://flukeout.github.io/"){ height=90% }

## Prioridad de selectores

- Versión fácil

> "cuanto más específico el selector, más prioridad tiene"

- Versión algo más exacta: prioridad numérica, con $b$ una base *grande*
    * +$b^0$ puntos por *etiqueta* (= 1)
    * +$b^1$ puntos por *clase* (= 100, si $b=100$)
    * +$b^2$ puntos por *id* (= 10000, si $b=100$)

~~~{.css}
                    /* cuentas con b=100 en plan ilustrativo */
	p {...}                   /* 1                           */
	div p span { ... }        /* 3 = 1 + 1 + 1               */
	div .aviso { ... }        /* 101 = 100 + 1               */
	div .aviso p #feo { ... } /* 10102 = 1 + 100 + 1 + 10000 */
~~~

- En caso de *empate*, últimos ganan:
    + la última regla
    + de la última hoja de estilos cargada

## Prioridad de selectores (y 2)

-----------------------------------
P  Origin              Importance
-- ----------------- --------------
1  CSS Transitions   see below

2  user agent        normal

3  user              normal

4  **author**        normal

5  CSS Animations    see below

6  **author**        !important

7  user              !important

8  user agent        !important
-----------------------------------

Prioridad de hojas de estilo, según [CSS Cascade en la MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/Cascade), donde

- user agent: la hoja de estilos del navegador en sí
- user: especificada por usuario del navegador, *casi nadie la usa*
- author: cargada por la página

El atributo `!important` se puede aplicar a cualquier valor CSS:

~~~{.css}
	p { color: blue !important; } /* mis párrafos van EN AZUL */
~~~

## Propiedades y valores

- Unidades de medida
    + `80% ` $\rightarrow$ Porcentaje (sobre el valor heredado)
    + `12pt` $\rightarrow$ 12 puntos (unidad tipográfica)
    + `2em ` $\rightarrow$ 2x tamaño de la fuente actual (heredado)
    + `2ex ` $\rightarrow$ 2x alto de una `x` en la fuente actual (heredado)
    + `12px` $\rightarrow$ 12 píxeles
    + `12mm` $\rightarrow$ Unidad de medida (valen también cm, in, ...)
    + 0  $\rightarrow$ no requiere unidades (pero si quieres ponerlas, puedes)
    
- Colores
    + `red` $\rightarrow$ por nombre; había otros 17 (ahora hay 140), incluyendo `transparent`
    + `#ff0000` $\rightarrow$ en hexadecimal, con 2 carácteres por canal (rr, gg, bb)
    + `rgb(255, 0, 0)` $\rightarrow$ en decimal, por canales, cada uno entre 0 y 255
    + `rgb(100%, 0%, 0%)` $\rightarrow$ en porcentajes, por canal
    + `hsl(0%, 100%, 50%)` $\rightarrow$ en porcentajes, modelo **h**ue, **s**aturation **l**ightness

- URLs: relativas a la URL de la que se ha descargado el CSS:

~~~{.css}   
        /* si se sirve como parte de css/misestilos.css,
           referencia css/images/header-background.jpg */
        div#header { 
            background-image: url('images/header-background.jpg');  
        }    
~~~

## Propiedades condensadas y expandidas

muchas propiedades van agrupadas, y es posible dar valores a todas a la vez:

- todas a la vez = *propiedades condensadas*
- equivalente a una *declaración expandida*, aunque
    + es más rápido usar la v. condensada
    + tienes que saber su *orden de declaración*
    + es menos legible (si no sabes el orden)
    
~~~{.css}   
/* condensado */
button p { 
    font: bold 9px Charcoal;
}

/* expandido */
button p { 
	font-family: Charcoal; 
	font-style: normal; 
	font-variant: normal; 
	font-weight: bold; 
	font-size: 9px; 
	line-height: normal; 
}
~~~

## Propiedades importantes en texto y listas

~~~{.css}   
/* texto */
font-family: arial;   /* verdana, "Times New Roman", ...      */
    /* si varias separadas por comas, la primera que exista   */
font-size: 100%:      /* cualquier medida, mejor si relativa  */
font-style: normal;   /* o italic, para cursiva               */
font-weight: normal;  /* o bold, para negrita                 */
text-decoration: none;/* o overline, line-through, underline  */
    /* mejor no usar subrayados para no confundir con enlaces */
line-height: normal;  /* cualquier medida, mejor si relativa  */
text-align: left;     /* o right, center, justify             */

/* listas */
/* marcadores: square, decimal, lower-alpha, ...          */
list-style-type: circle; 
/* imagen para los marcadores; url(unaimagen.png)         */
list-style-image(none);
/* si el marcador forma va fuera de la lista o no: inside */    
list-style-position: outside;
~~~

*(valores por defecto; otros posibles valores en comentarios)*

## Modelo de cajas

- Todos los elementos se representan usando un *tipo de caja*
    + **block**: forma un bloque independiente, se suelen colocar unos encima de otros, no "fluyen" cuando se reduce el ancho. 
        * Ejemplos típicos: `<p>`, `<div>` ó `<img>`
        * Usad variantes de **`<div>`** para agrupar y estilar conjuntos de bloques
        * puedes cambiar su tamaño vía CSS con `width` / `height`. Su ancho es, si no lo cambias, el 100% del ancho de su contenedor.
    + **inline**: forma un bloque-en-flujo, y se colocan unos junto a otros, como palabras en una frase, "fluyendo" (= cambiando de línea) cuando no queda más espacio horizontal
        * Ejemplos típicos: `<b>`, `<a>`, ó `<span>`
        * Usad variantes de **`<span>`** para agrupar y estilar conjuntos de elementos inline (= cachos de frase).
        * **N**o puedes cambiar su tamaño vía CSS
    + **inline-block**: como un inline, pero no fluye internamente
    + **none**: invisible, no genera caja alguna

- Otros tipos de disposiciones:
    + list-item: para parecer un elemento de una lista
    + table / table-cell / table-column / table-row: para uso en tablas
    + **grid** / **flex**: layout avanzado, \
    tipo GridbagLayout / BoxLayout en Java Swing

- Se puede cambiar el tipo de caja con la propiedad CSS `display`

~~~{.css}
    a { display: none; }        /* ¡ya no se ven los enlaces!      */
    ul, li {display: inline; }  /* ¡elementos de lista sin apilar! */
~~~

## Anatomía de una caja

![partes de una caja y orden de direcciones](img/boxmodel.png "partes de una caja y orden de direcciones"){ width=70% }

- todas las cajas permiten especificar
    + margin: margen con las cajas vecinas. Márgenes vecinos se *colapsan*
    + border: borde de la caja, incluyendo ancho, color, patrón, ...
    + padding: distancia del contenido al borde
- en estas propiedades
    + versión colapsada usa orden "agujas del reloj"
    + versión extendida usa sufijos `-top`, `-right`, `-bottom`, y `-left`

~~~{.css}
div#x {
    margin: 0;                   /* todos a 0 */
    padding: 10px 5px 0 10px;    /* top, right, bottom, left */
    border-top: 1px solid black; /* linea negra como borde superior */
}
~~~

## Colapso de márgenes

![padding no colapsa, margin sí](img/margin-collapse.png "padding no colapsa, margin sí"){ width=50% }

~~~{.css}
h1, p { background: #cccccc; }
h1 { margin: 0 0 15px 0; }     /* colapsa: 20px = max(15, 20) */
p { margin: 20px 0 0 0; }
~~~
~~~{.css}
h1, p { background: #cccccc; }
h1 { padding: 0 0 15px 0; }    /* no colapsan */
p { padding: 20px 0 0 0; }
~~~

## Bordes

- `border` = `border-width` + `border-style` + `border-color`
- Hay una cosa llamada `outline` que son igualicas a los `border`, pero no ocupan lugar "de elemento" (son sólo decoración visual).

~~~{.css}   
#x {                           /* borde de 1 pixel rojo            */
    border-width: 1px;         /* cualquier ancho aqui             */
    border-color: red;         /* cualquier color                  */
    border-style: solid;       /* none, dotted, dashed, double, ...*/
}

#y {
    border: 1px dashed blue;   /* borde de 1 pixel azul, a rayitas */
} 
#z {
    border-top: 1px solid black; /* como un <hr/>, pero en CSS!    */
} 
~~~

## Lienzo, ventana y página

![lienzo, ventana y página](img/canvas-vs-view.png "lienzo, ventana y página"){ width=70% }

- Lienzo vs ventana
    - **Ventana** (window): rectángulo por el que se mira a la página.
    - **Lienzo** (canvas): rectángulo en el que se dibuja el contenido de la página; puede ser mayor que la ventana
- Colocación
    - depende del tamaño de la *ventana*, y define el tamaño del *lienzo*
    - la *caja base* CSS adopta el tamaño de la *ventana* actual

## Posicionamiento clásico

- En el *flujo* normal (*flujo* = algoritmo de colocación estándar, con elementos uno tras otro, o si son bloques, uno sobre otro)
    - Estática (**static**): posición "por defecto", donde les tocaría ir.
    - Relativa (**relative**): desplazados de su posición normal, usando propiedades `top`, `right`, `bottom` y `left`.
    - Flotante (**float**): llevado a uno de los lados de la caja contenedora, pero dejando fluir a otros elementos, que le reservan espacio
- Fuera del flujo:
    - Absoluta (**absolute**): fijos con respecto al lienzo. Se mueven si desplazas la vista.
    - Fija (**fixed**): fijos con respecto a la ventana. Se quedan en el mismo sitio, aunque desplaces la vista.

- - - 

~~~{.html}
<!DOCTYPE html>
<html><head><link rel="stylesheet" href="p.css" type="text/css"></head>
    <body>
        <div id="a"></div><div id="b"></div>
        <p>Lorem ipsum dolor sit amet, ... </p>
        <p>In hendrerit vulputate magna ... </p>
    </body>
</html>
~~~

~~~{.css}
div#a { background: blue; width: 2em; height: 4em;}
div#b { background: red; width: 4em; height: 2em;}
~~~

![static (por defecto)](img/position-static.png "static (por defecto)"){ width=40% }

- - - 

~~~{.html}
<!DOCTYPE html>
<html><head><link rel="stylesheet" href="p.css" type="text/css"></head>
    <body>
        <div id="a"></div><div id="b"></div>
        <p>Lorem ipsum dolor sit amet, ... </p>
        <p>In hendrerit vulputate magna ... </p>
    </body>
</html>
~~~

~~~{.css}
div#a { background: blue; width: 2em; height: 4em;}
div#b { background: red; width: 4em; height: 2em;}
div#a { position: relative; top 2em; } /* <-- azul 2em hacia abajo */
~~~

![caja azul: relative](img/position-relative.png "caja azul: relative"){ width=40% }

- - - 

~~~{.html}
<!DOCTYPE html>
<html><head><link rel="stylesheet" href="p.css" type="text/css"></head>
    <body>
        <div id="a"></div><div id="b"></div>
        <p>Lorem ipsum dolor sit amet, ... </p>
        <p>In hendrerit vulputate magna ... </p>
    </body>
</html>
~~~

~~~{.css}
div#a { background: blue; width: 2em; height: 4em;}
div#b { background: red; width: 4em; height: 2em;}
div { position: float; } /* <-- flotan! */
~~~

![cajas con float](img/position-float.png "cajas con float"){ width=40% }

- - - 

~~~{.html}
<!DOCTYPE html>
<html><head><link rel="stylesheet" href="p.css" type="text/css"></head>
    <body>
        <div id="a"></div><div id="b"></div>
        <p>Lorem ipsum dolor sit amet, ... </p>
        <p>In hendrerit vulputate magna ... </p>
    </body>
</html>
~~~

~~~{.css}
div#a { background: blue; width: 2em; height: 4em;}
div#b { background: red; width: 4em; height: 2em;}
div#a { position: absolute; } /* <-- azul: absolute */
~~~

\begin{figure}[ht]
    \begin{minipage}[b]{0.45\linewidth}
        \centering
        \includegraphics[width=\textwidth]{img/position-absolute-or-fixed.png}
        \caption{antes de hacer scroll}
    \end{minipage}
    \hspace{0.5cm}
    \begin{minipage}[b]{0.45\linewidth}
        \centering
        \includegraphics[width=\textwidth]{img/position-absolute.png}
        \caption{después de hacer scroll}
    \end{minipage}
\end{figure}

- - - 

~~~{.html}
<!DOCTYPE html>
<html><head><link rel="stylesheet" href="p.css" type="text/css"></head>
    <body>
        <div id="a"></div><div id="b"></div>
        <p>Lorem ipsum dolor sit amet, ... </p>
        <p>In hendrerit vulputate magna ... </p>
    </body>
</html>
~~~

~~~{.css}
div#a { background: blue; width: 2em; height: 4em;}
div#b { background: red; width: 4em; height: 2em;}
div#a { position: fixed; } /* <-- azul: fixed */
~~~

\begin{figure}[ht]
    \begin{minipage}[b]{0.45\linewidth}
        \centering
        \includegraphics[width=\textwidth]{img/position-absolute-or-fixed.png}
        \caption{antes de hacer scroll}
    \end{minipage}
    \hspace{0.5cm}
    \begin{minipage}[b]{0.45\linewidth}
        \centering
        \includegraphics[width=\textwidth]{img/position-fixed.png}
        \caption{después de hacer scroll}
    \end{minipage}
\end{figure}

## Float, display, y clear

- Float
    - puede ser 
        * `right` ó `left` (clásico)
        * `inline-start` ó `inline-end` (nuevo), \
        teniendo en cuenta el lado *comienzo* en el idioma actual)
    - su contenido se considera `display: block`\
    (aunque no lo fuera normalmente)
- Se coloca
    - tocando el `padding` del elemento que lo contiene, ó
    - al lado de un `float` previo en esa dirección
- posible forzar espaciado con float anteriores vía `clear`
    - `right`, `left`, `float`: a qué floats anteriores afecta

## Ejemplo de posicionamiento clásico 1

~~~{.css}
html, body {
    margin:0; padding:0; height: 100%; background-color: #366;
}
#capaMadre {
    width: 790px; background-color: #6cc;
    margin: 0 auto; /* centrado */
    position: relative;
    height: auto!important;
    min-height: 100%;
    height: 100%;
}
#cabecera {
    background-color: #c93; text-align: center; height:80px;
}
#cuerpo {
    position: relative; display: block; margin: 20px;
}
#pie {
    position: absolute; bottom: 0; height: 40px;
    background-color: #333; color: #fff; text-align: center;
    width: 100%; clear: both;
}
~~~

- - - 

![layout clasico 1](img/classic-layout-1.png "layout clasico 1"){ width=70% }

~~~{.html}
<!-- ... -->
<div id="capaMadre">
    <div id="cabecera">Header</div>
    <div id="cuerpo">Cuerpo</div>
    <div id="pie">Pie</div>
</div>
<!-- ... -->
~~~

- No-responsive (*responsive* = que se adapta al contexto):
    + usa tamaños fijos para ancho de pantalla, alto de cabecera y pie
    + no se adapta a tamaño real de pantalla, ni al de los contenidos

## Ejemplo de posicionamiento clásico 2

~~~{.css}
* {margin:0;padding:0;}
html, body {
    height: 100%;
}
#contenedor {
    width: 100%; height: 100%; margin:0;
}
#col_der, #col_izq, #col_cen {
    height: 100%;
}
#col_der {
    float: right; width: 200px; background-color: #f00;
}
#col_izq {
    float: left; width: 200px; background-color: #f0f;
}
#col_cen {
    background-color: #ccc;
}
~~~

- - - 

![layout clasico 2](img/classic-layout-2.png "layout clasico 2"){ width=70% }

~~~{.html}
<!-- ... -->
<div id="contenedor">
    <div id="col_der">derecha</div>
    <div id="col_izq">izquierda</div>
    <div id="col_cen">centro</div>
</div>
<!-- ... -->
~~~

- Algo más "responsive"
    + se convierte en 2, o incluso 1 columna, si no hay suficiente ancho
    + pero sigue usando tamaños fijos para ancho de columnas

## Layouts modernos: flex y grid

* `display: flex`, también llamado *flexbox*
    - para secuencias de elementos organizadas a lo largo de un eje
    - por ejemplo, permite hacer "layouts" de columnas muy fácilmente
    - recuerda al `BoxLayout` de Java Swing
    - hay muchos [ejemplos y detalles de flex en la MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/flex)

* `display: grid`
    - para usar la metáfora de una tabla
    - (pero sin usar una tabla de verdad, porque eso sería semánticamente feo)
    - más complejo, pero potencialmente más potente
    - recuerda al `GridbagLayout` de Java Swing
    - hay muchos [ejemplos y detalles de grid en la MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column)

## Layout de 3 columnas con flex

![https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Flexible_Box_Layout/Basic_Concepts_of_Flexbox](img/flexbox-mdn.png "https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Flexible_Box_Layout/Basic_Concepts_of_Flexbox"){ width=70% }

- añadir más columnas no requiere tocar el CSS
- se adapta al ancho disponible
- posible reflejar dirección de lectura LTR / RTL

## Layout complejo con grid

~~~{.css}
.wrapper {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-gap: 10px;
  grid-auto-rows: minmax(100px, auto);
}
.one { 
  grid-column: 1 / 3; /* comienzo (inclusive) / final (exclusive) */
  grid-row: 1;        /* equivale a 1 / 2; empieza a contar en 1  */
}
/* ... */
~~~

![https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Grid_Layout/Basic_Concepts_of_Flexbox](img/grid-mdn.png "https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Grid_Layout"){ width=40% }

## Depurando layouts con F12

~~~{.css}
/* ... */
div.container {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    grid-template-rows: auto;
    grid-template-areas: 
        "main main . sidebar"
}
div.content { grid-area: main; }
div.sidebar { grid-area: sidebar; }
~~~

~~~{.html}
<!-- ... -->
<div class="container">
<div class="main">
    <!-- ocupo 2 columnas -->
</div>
<div class="sidebar">
    <!-- ocupo columna del final -->
</div>
<!-- ... -->
~~~

- - - 

![depurando un grid](img/grid-f12.png "depurando un grid"){ width=90% }

# Fin

## ¿dudas?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
