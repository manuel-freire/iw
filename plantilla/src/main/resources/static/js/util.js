"use strict"

/**
 * Este fichero, `js`, contiene funciones que te pueden resultar útiles para 
 * - generar datos de prueba
 * - validar datos
 * - operar con el DOM
 *
 * Fuera de las prácticas, lee la licencia: dice lo que puedes hacer con él:
 * lo que quieras siempre y cuando
 * - no digas que eres el autor original
 * - no me eches la culpa si algo no funciona como esperas
 *
 * @Author manuel.freire@fdi.ucm.es
 */

export function one(selector) {
    return document.querySelector(selector);
}

export function all(selector) {
    return document.querySelectorAll(selector);
}

export function add(selector, html) {
    one(selector).insertAdjacentHTML("beforeend", html);
}

export function clean(selector) {
    all(selector).forEach(o => o.innerHTML = '')
}

export const UPPER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
export const LOWER = 'abcdefghijklmnopqrstuvwxyz';
export const DIGITS = '01234567890';

/**
 * Escapes special characters to prevent XSS/breakage when generating HTML
 * via, say, insertAdjacentHTML or insertHTML.
 * 
 * (see https://stackoverflow.com/a/9756789/15472)
 * 
 * @param {string} s
 */
export function escape(s) {
    return ('' + s) /* Forces the conversion to string. */
        .replace(/\\/g, '\\\\') /* This MUST be the 1st replacement. */
        .replace(/\t/g, '\\t') /* These 2 replacements protect whitespaces. */
        .replace(/\n/g, '\\n')
        .replace(/\u00A0/g, '\\u00A0') /* Useful but not absolutely necessary. */
        .replace(/&/g, '\\x26') /* These 5 replacements protect from HTML/XML. */
        .replace(/'/g, '\\x27')
        .replace(/"/g, '\\x22')
        .replace(/</g, '\\x3C')
        .replace(/>/g, '\\x3E');
}

/**
 * Quote attribute values to prevent XSS/breakage
 * 
 * (see https://stackoverflow.com/a/9756789/15472)
 * 
 * @param {string} s
 * @param {boolean|undefined} preserveCR (por defecto false) para permitir `\n`
 */
export function quoteattr(s, preserveCR) {
    preserveCR = preserveCR ? '&#13;' : '\n';
    return ('' + s) /* Forces the conversion to string. */
        .replace(/&/g, '&amp;') /* This MUST be the 1st replacement. */
        .replace(/'/g, '&apos;') /* The 4 other predefined entities, required. */
        .replace(/"/g, '&quot;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        /*
        You may add other replacements here for HTML only 
        (but it's not necessary).
        Or for XML, only if the named entities are defined in its DTD.
        */
        .replace(/\r\n/g, preserveCR) /* Must be before the next replacement. */
        .replace(/[\r\n]/g, preserveCR);
}

/**
 * Lanza excepción si el parámetro no existe como clave en el objeto pasado como segundo valor
 * @param {string} a
 * @param {*} enumeration, un objeto
 */
export function checkEnum(a, enumeration) {
    const valid = Object.values(enumeration);
    if (a === undefined) {
        return;
    }
    if (valid.indexOf(a) === -1) {
        throw Error(
            "Invalid enum value " + a +
            ", expected one of " + valid.join(", "));
    }
}

/**
 * Genera un entero aleatorio entre min y max, ambos inclusive
 * @param {Number} min 
 * @param {Number} max 
 */
export function randomInRange(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min
}

/**
 * Devuelve un carácter al azar de la cadena pasada como argumento
 * @param {string} alphabet 
 */
export function randomChar(alphabet) {
    return alphabet.charAt(Math.floor(Math.random() * alphabet.length));
}

/**
 * Devuelve una cadena de longitud `count` extraida del alfabeto pasado como
 * segundo argumento
 * @param {number} count 
 * @param {(string|undefined)} alphabet, por defecto alfanuméricos con mayúsculas y minúsculas
 */
export function randomString(count, alphabet) {
    const n = count || 5;
    const valid = alphabet || UPPER + LOWER + DIGITS;
    return new Array(n).fill('').map(() => this.randomChar(valid)).join('');
}

/**
 * Devuelve un identificador tipo DNI al azar (8 caracteres + letra)
 */
export function generateDni(number) {
    const nr = number ?
        number.padStart(8, '0').substring(0, 8) :
        new Array(8).fill('').map(() => this.randomChar(DIGITS)).join('');
    const pos = nr % 23;
    return "" + nr + "TRWAGMYFPDXBNJZSQVHLCKET".substring(pos, pos + 1);
}

/**
 * Devuelve true si y sólo si el DNI es válido (8 caracteres + letra)
 */
export function isValidDni(dni) {
    if (!/^[0-9]{8}[A-Z]$/.test(dni)) {
        return false;
    }
    return generateDni(dni.substring(0, 8)) == dni
}

/**
 * Genera una palabra, opcionalmente empezando por mayúsculas
 * 
 * @param {number} count longitud
 * @param {(boolean|undefined)} capitalized, por defecto false; si true, 1er caracter en mayuscula
 */
export function randomWord(count, capitalized) {
    return capitalized ?
        this.randomChar(UPPER) + this.randomString(count - 1, LOWER) :
        this.randomString(count, LOWER);
}

/**
 * Genera palabras al azar, de forma configurable
 * 
 * @param {number} wordCount a generar
 * @param {(boolean|undefined)} allCapitalized si todas deben empezar por mayúsculas (por defecto, sólo 1a)
 * @param {(string|undefined)} delimiter delimitador a usar (por defecto, espacio)
 */
export function randomText(wordCount, allCapitalized, delimiter) {
    let words = [this.randomWord(5, true)]; // primera empieza en mayusculas
    for (let i = 1; i < (wordCount || 1); i++) words.push(this.randomWord(5, allCapitalized));
    return words.join(delimiter || ' ');
}

/**
 * Devuelve algo al azar de un array
 * 
 * @param {[*]} array 
 */
export function randomChoice(array) {
    return array[Math.floor(Math.random() * array.length)];
}

/**
 * Genera una fecha al azar entre 2 dadas
 * https://stackoverflow.com/a/19691491
 * 
 * @param {string} fechaIni, en formato válido para `new Date(fechaIni)`
 * @param {number} maxDias 
 */
export function randomDate(fechaIni, maxDias) {
    let dia = new Date(fechaIni);
    dia.setDate(dia.getDate() - randomInRange(1, maxDias));
    return dia;
}

/**
 * Devuelve n elementos no-duplicados de un array
 * de https://stackoverflow.com/a/11935263/15472
 *
 * @param {[*]} array 
 * @param {size} cuántos elegir (<= array.length)
 */
export function randomSample(array, size) {
    var shuffled = array.slice(0),
        i = array.length,
        temp, index;
    while (i--) {
        index = Math.floor((i + 1) * Math.random());
        temp = shuffled[index];
        shuffled[index] = shuffled[i];
        shuffled[i] = temp;
    }
    return shuffled.slice(0, size);
}

/**
 * Genera hasta n parejas no-repetidas de elementos de dos arrays
 * los elementos deben ser números, o texto que no contenga el separador
 * 
 * @param {number} count 
 * @param {[(string|number)]} as 
 * @param {[(string|number)]} bs 
 * @param {string|undefined} separator a usar, por defecto `,`
 */
export function randomPairs(count, as, bs, separator) {
    separator = separator || ",";
    const pairs = new Set();
    let retries = 0;
    while (pairs.size < count && retries < 100) {
        let p = `${randomChoice(as)}${separator}${randomChoice(bs)}`;
        if (pairs.has(p)) {
            retries++;
        } else {
            pairs.add(p);
        }
    }
    return Array.from(pairs).map(p => p.split(separator).map(s => +s));
}

/**
 * Llena un array con el resultado de llamar a una funcion varias veces
 * 
 * @param {number} count 
 * @param {Function} f 
 */
export function fill(count, f) {
    // new Array(count).map(f) fails: map only works on existing indices
    return new Array(count).fill().map(f)
}

// top-100 nombres de nacidos (top-50 chicas y top-50 chicos) en 2002 según https://www.ine.es
export const randomFirstNames = [
    "Alba", "Andrea", "Sara", "Ana",
    "Nerea", "Claudia", "Cristina", "Marina", "Elena", "Irene", "Natalia", "Carla",
    "Carmen", "Nuria", "Ainhoa", "Patricia", "Julia", "Angela", "Rocio", "Sandra",
    "Raquel", "Sofia", "Alicia", "Clara", "Noelia", "Miriam", "Alejandra", "Eva",
    "Isabel", "Silvia", "Celia", "Lorena", "Ines", "Beatriz", "Mireia", "Laia",
    "Lidia", "Carlota", "Blanca", "Ariadna", "Adriana", "Anna", "Carolina",
    "Monica", "Ana Maria", "Veronica",
    "Alejandro", "Pablo", "Daniel", "David", "Adrian", "Javier", "Alvaro", "Sergio",
    "Carlos", "Jorge", "Mario", "Raul", "Diego", "Manuel", "Miguel", "Ivan",
    "Antonio", "Juan", "Ruben", "Victor", "Alberto", "Jesus", "Marc", "Oscar",
    "Angel", "Francisco", "Jose", "Alex", "Marcos", "Jaime", "Ismael", "Luis",
    "Francisco Javier", "Miguel Angel", "Pedro", "Samuel", "Cristian", "Pau",
    "Andres", "Iker", "Jose Antonio", "Guillermo", "Ignacio", "Rafael", "Fernando",
    "Jose Manuel", "Nicolas", "Gonzalo", "Gabriel", "Hugo", "Joel"
];
// top-100 apellidos nacionales, misma fuente
export const randomLastNames = [
    "Garcia", "Rodriguez", "Gonzalez", "Fernandez", "Lopez", "Martinez", "Sanchez",
    "Perez", "Gomez", "Martin", "Jimenez", "Hernandez", "Ruiz", "Diaz", "Moreno",
    "Muñoz", "Alvarez", "Romero", "Gutierrez", "Alonso", "Navarro", "Torres",
    "Dominguez", "Vazquez", "Ramos", "Ramirez", "Gil", "Serrano", "Molina", "Blanco",
    "Morales", "Suarez", "Ortega", "Castro", "Delgado", "Ortiz", "Marin", "Rubio",
    "Nuñez", "Sanz", "Medina", "Iglesias", "Castillo", "Cortes", "Garrido", "Santos",
    "Guerrero", "Lozano", "Cano", "Mendez", "Cruz", "Prieto", "Flores", "Herrera",
    "Peña", "Leon", "Marquez", "Gallego", "Cabrera", "Calvo", "Vidal", "Campos", "Vega",
    "Reyes", "Fuentes", "Carrasco", "Diez", "Caballero", "Aguilar", "Nieto", "Santana",
    "Pascual", "Herrero", "Montero", "Gimenez", "Hidalgo", "Lorenzo", "Vargas",
    "Ibañez", "Santiago", "Duran", "Benitez", "Ferrer", "Arias", "Mora", "Carmona",
    "Vicente", "Crespo", "Soto", "Roman", "Rojas", "Pastor", "Velasco", "Saez",
    "Parra", "Moya", "Bravo", "Soler", "Gallardo", "Esteban"
];

/**
 * Convierte un valor en único dentro de un conjunto usando un sufijo numérico
 * 
 * @param {string} str a hacer unico mediante un sufijo numérico que se puede incrementar
 * @param {Map<String, number>} prev con los valores previos
 */
export function unique(str, prev) {
    if (!prev.has(str)) {
        prev.set(str, 1);
        return str;
    } else {
        const next = prev.get(str);
        prev.set(str, next + 1);
        return `${str}${next}`;
    }
}


/**
 * Devuelve la primera posición donde una función devuelve válido en un array
 * @param {[*]} array 
 * @param {function} condition 
 * @param {function} callback
 */
export function doWhere(array, condition, callback) {
    for (let i = 0; i < array.length; i++) {
        if (condition(array[i])) {
            if (callback) callback(array, i);
        }
    }
}

/**
 * Elimina el elemento de la primera posición donde una función devuelve válido en un array
 * @param {[*]} array 
 * @param {function} fn 
 */
export function rmWhere(array, condition) {
    doWhere(array, condition, (a, i) => a.splice(i, 1));
}

/**
 * Returns elements in one array but not in the other
 */
export function inOneButNotAnother(as, bs) {
    return as.filter(a => bs.indexOf(a) == -1);
}

/**
 * Returns a deep clone of a serializable object
 */
export function clone(o) {
    return JSON.parse(JSON.stringify(o));
}

/**
 * Devuelve "true" si el objeto corresponde al patrón (= mismos valores en mismas propiedades)
 * 
 * @param {Object} objeto
 * @param {Object} pattern
 */
export function sameAs(o, pattern) {
    for (let [k, v] of Object.entries(pattern)) {
        if (o[k] !== v) return false;
    }
    return true;
}