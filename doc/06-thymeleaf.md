% Thymeleaf
% (manuel.freire@fdi.ucm.es)
% 2019.02.18

## Objetivo

> Usar el motor de templates Thymeleaf

## Introducción

¿Qué es más mantenible?

~~~{.php}
<?php
echo "<section>\n"
echo "<h1> bienvenido, " . nombre . "</h1>\n"
echo "<section>\n"
~~~

~~~{.html}
<section>
<h1> bienvenido, <?php echo $nombre  ?></h1>
</section>
~~~

~~~{.html}
<section>
<h1> bienvenido, <span th:text="${nombre}">Luis</span></h1>
</section>
~~~

## Lenguajes de plantillas

> Plantilla (de documento): un documento base sobre el que
> se añadirán o modificarán elementos para obtener documentos
> finales personalizados

- Ventajas
    + mucho más similar al formato destino
    + ... quizá hasta el punto de poder usar las mismas herramientas
    + permite (y obliga a) separación de responsabilidades
- Inconvenientes
    + tienes que aprender un nuevo lenguaje
    + no tienes la misma flexibilidad que si usases un lenguaje "de verdad"

## Thymeleaf

- Thyme = tomillo, de la familia de la menta
- [Thymeleaf](https://www.thymeleaf.org/) = hoja de tomillo, y motor de templates Java
    - intenta que sus templates sean documentos cuasi-válidos por sí mismos
    - se basa en el uso de atributos "th:" para reescribir fragmentos
    - pero permite estilar las plantillas sin necesidad de instanciarlas

~~~{.html}
<table>
    <thead>
        <tr> <!-- th:text reemplaza texto -->
        <th th:text="#{msgs.headers.name}">Name</th>
        <th th:text="#{msgs.headers.price}">Price</th>
        </tr>
    </thead>
    <tbody>  <!-- th:each repite elemento -->
        <tr th:each="prod: ${allProducts}">
        <td th:text="${prod.name}">Oranges</td>
        <td th:text="${#numbers.formatDecimal(prod.price, 1, 2)}">
            0.99
        </td>
        </tr>
    </tbody>
</table>
~~~
    
## Modos de template

- html - nuestro uso principal
- xml - tienen que ser documentos bien-formados
- text - útil para correos y otros documentos generales
- javascript - similar a text, pero con elementos js-específicos
- css - también similar a text, pero con elementos de css
- raw (= crudo) - no hace nada

## Texto internacionalizado (i18n) con `#`

- **i18n** resume *internacionalization*, el proceso de soportar múltiples idiomas 
- usa **`#{`**_clave_**`}`** para acceder (vía *clave*) al valor internacionalizado correspondiente

~~~{.html}
<p th:text="#{home.welcome}">Bienvenue a l'Karmomètre!</p>
~~~

~~~{.properties}
# src/main/resources/Messages_en.properties
home.welcome = Welcome to Karmometro!

# src/main/resources/Messages_es.properties
home.welcome = ¡Bienvenido al Karmómetro!
~~~

## Texto del modelo con `$`

- usa **`${`**_clave_**`}`** para acceder (vía *clave*) al valor del modelo correspondiente
- los métodos que gestionan peticiones (`@GetMapping`, `@PostMapping`, `@RequestMapping`, ...) aceptan un argumento `Model model`
- usa `model.addAttribute(`*clave*,*valor*`)` para añadir pares clave-valor.

~~~{.java}
@GetMapping("/admin")
public String admin(Model model, Principal principal) {
    model.addAttribute("name", principal.getName()); /* <-- */
    return "index";
}
~~~

~~~{.html}
<span th:text="${name}">Arturo</span>
~~~

## Más orígenes para `$`

- **`${x}`** del modelo, o del `HttpServletRequest` actual.
- **`${param.x}`** de un parámetro de la petición; puede tener múltiples valores
- **`${session.x}`** de la sesión
    - cualquier método que gestiona peticiones (`@GetMapping`, `@PostMapping`, `@RequestMapping`, ...) acepta un argumento `HttpSession session`
- **`${application.x}`** del contexto del servlet

~~~{.java}
@GetMapping("/admin")
public String admin(HttpSession session) {
    session.setAttribute("role", "admin"); /* <-- */
    return "index";
}
~~~

## Texto que contiene marcado

- elige entre `th:text` y `th:utext` según si quieres o no escapar html. 

~~~{.properties}
# hay marcas html en esta cadena
home.welcome=Welcome to our <b>fantastic</b> grocery store!
~~~

~~~{.html}
<!-- por defecto, th:text escapa el texto -->
<p th:text="#{home.welcome}">Bienvenido</p>
<!-- si no quieres, usa th:utext -->
<p th:utext="#{home.welcome}">Bienvenido</p>
~~~

Tras thymeleaf ...

~~~{.html}
<p>Welcome to our &lt;b&gt;fantastic&lt;/b&gt; grocery store!</p>
<p>Welcome to our <b>fantastic</b> grocery store!</p>
~~~

## Enlaces con `@`

- Thymeleaf genera enlaces relativos al *contexto* de tu aplicación web
- Es decir, el 1er `/` representa la URL de tu aplicación

~~~{.html}
  <!-- se remplaza con th:href -->
  <link rel="stylesheet" 
    th:href="@{/css/main.css}" 
    href="../static/css/main.css" type="text/css"/>
~~~

## Fragmentos con `~`

- puedes usar `th:insert` para evitar reemplazar (`th:replace`)

~~~{.html}
<nav th:replace="fragments/nav.html :: nav">Nav goes here</nav>
~~~

- en el documento origen, `th:fragment="nombre"` permite nombrar fragmentos para poder incluirlos.

~~~{.html}
<!-- en fragments/nav.html ... -->
<nav th:fragment="nav">
    <a class="logo" href="" th:href="@{/}"></a>
    <span class="sitename">Karmómetro</span>
    <!-- ... -->
</nav>
<!-- ... -->
~~~

## Magia entre `{}`

- literales
    - `'texto'``entre comillas simples; puedes escaparlo una comilla con `\`
    - `123` (números) tal cual
    - `true` y `false` como ellos mismos, booleanos
    - `null` también está disponible
- operadores
    - `+` concatena textos
    - en un contexto delimitado por `|`, ni siquiera hace falta
    - `+`, `-`, `*`, `%`, `/`, etcétera siguen haciendo lo que hacían en Java.
    - las comparaciones también funcionan, pero tienes que **escapar** `<` y `>` (vía `&lt;` y `&gt;`). Como queda feo, puedes usar `ge` para `<=`, etcétera.
    - también tienes el operador ternario

~~~{.html}
<span th:text="'Welcome to our application, ' + ${user.name} + '!'">
<span th:text="|Welcome to our application, ${user.name}!|">
<tr th:class="${row.even}? (${row.first}? 'first' : 'even') : 'odd'">
~~~

## Usando `*` para operar sobre un objeto previo

~~~{.html}
<div th:object="${book}"> <!-- objeto a usar con '*'   -->
  ...
  <span th:text="*{title}">...</span> <!-- uso con '*' -->
  ...
</div>
~~~

... equivale a ...

~~~{.html}
<div th:object="${book}">
  ...
  <span th:text="${book.title}">...</span>
  ...
</div>
~~~

- todo lo que funciona con `${...}` es aplicable a `*{...}`

## Más magia en un `${}`

- por debajo, Thymeleaf usa [Spring Expression Language](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions-properties-arrays) \
(ó OGNL, si fuera de Spring) para evaluar expresiones
- incluido el *operador Elvis*: `x?.y` -- evalua *y* si *x* es no-*null*; ó devuelve *null*

~~~{.java}
// Java                                Spring EL
   x.getAlgo().isCosaBooleana();    // x.algo.cosaBooleana
   "abc".substring(1,3);            // 'abc'.substring(1,3)
   x == null ? null : x.getAlgo();  // x?.algo
   mapaDeX.get(y);                  // mapaDeX[y]
   listaDeX.get(y);                 // listaDeX[y]
   arrayDeX[y];                     // arrayDeX[y]
   Arrays.stream(enteros)           // enteros.?[#this<3]
    .filter(p -> p < 3).collect(Collectors.toList())                                    
   Arrays.stream(enteros)           // enteros.![#this*2]
    .map(n -> 2*n).collect(Collectors.toList())                                    
   Arrays.stream(personas)          // personas.![dni]
    .map(p -> p.getDni()).collect(Collectors.toList())
                                    
~~~

## Preprocesamiento de expresiones

- expresiones entre `__` se evalúan antes que el resto, y se reemplazan literalmente. Así,

~~~
    #{selection.__${sel.code}__}

    ... equivale a ...

    #{selection.resultado}
~~~

## Más etiquetas `th:`

- **`th:attr`** permite reemplazar atributos. Pero es **feo**
    - hay que usar comas para más de un par atributo/valor
    - y produce templates poco legibles
- por tanto, [*muchas* variantes para distitnos tipos de atributo](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-value-to-specific-attributes)

~~~{.html}
<img src="../../images/gtvglogo.png" 
    th:attr="src=@{/images/gtvglogo.png},title=#{logo},alt=#{logo}" />
<!-- resultado, remplazando src, title, alt -->     
<img src="/gtgv/images/gtvglogo.png" 
    title="Logo de Good Thymes" alt="Logo de Good Thymes" />
~~~

\tiny
th:abbr 	th:accept 	th:accept-charset
th:accesskey 	th:action 	th:align
th:alt 	th:archive 	th:audio
th:autocomplete 	th:axis 	th:background
th:bgcolor 	th:border 	th:cellpadding
th:cellspacing 	th:challenge 	th:charset
th:cite 	th:class 	th:classid
th:codebase 	th:codetype 	th:cols
th:colspan 	th:compact 	th:content
th:contenteditable 	th:contextmenu 	th:data
th:datetime 	th:dir 	th:draggable
th:dropzone 	th:enctype 	th:for
th:form 	th:formaction 	th:formenctype
th:formmethod 	th:formtarget 	th:fragment
th:frame 	th:frameborder 	th:headers
th:height 	th:high 	th:href
th:hreflang 	th:hspace 	th:http-equiv
th:icon 	th:id 	th:inline
th:keytype 	th:kind 	th:label
th:lang 	th:list 	th:longdesc
th:low 	th:manifest 	th:marginheight
th:marginwidth 	th:max 	th:maxlength
th:media 	th:method 	th:min
th:name 	th:onabort 	th:onafterprint
th:onbeforeprint 	th:onbeforeunload 	th:onblur
th:oncanplay 	th:oncanplaythrough 	th:onchange
th:onclick 	th:oncontextmenu 	th:ondblclick
th:ondrag 	th:ondragend 	th:ondragenter
th:ondragleave 	th:ondragover 	th:ondragstart
th:ondrop 	th:ondurationchange 	th:onemptied
th:onended 	th:onerror 	th:onfocus
th:onformchange 	th:onforminput 	th:onhashchange
th:oninput 	th:oninvalid 	th:onkeydown
th:onkeypress 	th:onkeyup 	th:onload
th:onloadeddata 	th:onloadedmetadata 	th:onloadstart
th:onmessage 	th:onmousedown 	th:onmousemove
th:onmouseout 	th:onmouseover 	th:onmouseup
th:onmousewheel 	th:onoffline 	th:ononline
th:onpause 	th:onplay 	th:onplaying
th:onpopstate 	th:onprogress 	th:onratechange
th:onreadystatechange 	th:onredo 	th:onreset
th:onresize 	th:onscroll 	th:onseeked
th:onseeking 	th:onselect 	th:onshow
th:onstalled 	th:onstorage 	th:onsubmit
th:onsuspend 	th:ontimeupdate 	th:onundo
th:onunload 	th:onvolumechange 	th:onwaiting
th:optimum 	th:pattern 	th:placeholder
th:poster 	th:preload 	th:radiogroup
th:rel 	th:rev 	th:rows
th:rowspan 	th:rules 	th:sandbox
th:scheme 	th:scope 	th:scrolling
th:size 	th:sizes 	th:span
th:spellcheck 	th:src 	th:srclang
th:standby 	th:start 	th:step
th:style 	th:summary 	th:tabindex
th:target 	th:title 	th:type
th:usemap 	th:value 	th:valuetype
th:vspace 	th:width 	th:wrap
th:xmlbase 	th:xmllang 	th:xmlspace

## Iterando con `th:each`

~~~{.html}
<tr th:each="prod: ${prods}">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
</tr>
~~~

- aquí, *`${prods}`* puede evaluar a 
    - cualquier `Iterable`, `Enumeration`, `Iterator`
        - `List`, `Set`, ...
        - la BD os devuelve subclases de `List`, que son **directamente iterables**.
    - cualquier `Map` - evaluará los correspondientes `Map.Entry`
        - `e.key`: la clave (`e.getKey()`)
        - `e.value`: el valor (`e.getValue()`)
    - arrays

## Propiedades adicionales en un `th:each`

~~~{.html}
<algo th:each="v, i: ${datos}"> <!-- ojo con la i -->
~~~

- a través de la **`i`** (si la defines), thymeleaf da acceso a
    - `index` (de $0$ a $n-1$, con $n$ tamaño)
    - `count` (de $1$ a $n$)
    - `size` ($= n$)
    - `current` (elemento actual)
    - `odd`, `first`, `last`: booleanas, indican si éste es
        - un elemento impar (`odd`) o no
        - el primer (`first`) ó último (`last`) elemento

~~~{.html}
<tr th:each="${xs}">
    <td th:text="|${index} ${count} ${size} ${current}|"
    th:class="|c${odd}-${first}-${last}|">hola!</td>
</tr>
<!-- resultado con ["uno", "dos", "tres", "cuatro", "cinco"] -->
<tr><td class="ctrue-true-false">0 1 5 uno</td></tr>
<tr><td class="cfalse-false-false">1 2 5 dos</td></tr>
<tr><td class="ctrue-false-false">2 3 5 tres</td></tr>
<tr><td class="cfalse-false-false">3 4 5 cuatro</td></tr>
<tr><td class="ctrue-false-true">4 5 5 cinco</td></tr>
~~~

## Codicionales con `th:if` y `tf:unless`

~~~{.html}
<!-- este enlace sólo se genera si hay comentarios -->
<a href="comments.html"
   th:href="@{/product/comments(prodId=${prod.id})}" 
   th:if="${not #lists.isEmpty(prod.comments)}">view</a>
~~~

- para `th:if`,
    + `null` es falso, otros obetos verdaderos
    + los booleanos son lo que sean
    + 0 es falso, y cualquier otro número es verdadero
    + `"false"`, `"off"` y `"no"` son falsos, y otras cadenas verdaderas

- `th:unless` hace justo lo contrario: si es *falso*, se muestra

## Codicionales con `th:switch`

~~~{.html}
<div th:switch="${user.role}">
  <p th:case="'admin'">User is an administrator</p>
  <p th:case="#{roles.manager}">User is a manager</p>
  <p th:case="*">I have no idea what the user is</p> <!-- ! -->
</div>
~~~

## Referencias

- [Tutorial básico de thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html), de su página
- [Spring Expression Language](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions-language-ref), también llamado SpEL, muy similar a OGNL
- [Object Graph Navigation Language](https://commons.apache.org/proper/commons-ognl/language-guide.html), base de SpEL

# Fin

## ¿dudas?

![](img/cc-by-sa-4.png "Creative Commons Attribution-ShareAlike 4.0 International License"){ width=25% }

This work is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/)
