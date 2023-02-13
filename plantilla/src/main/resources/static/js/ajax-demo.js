/*
 * Ejemplos de uso de JS para interaccionar con servidores
 * y añadir interactividad a la página.
 *
 * Estos ejemplos se usan desde templates/user.html
 */

// envio de mensajes con AJAX
let b = document.getElementById("sendmsg");
b.onclick = (e) => {
    e.preventDefault();
    go(b.parentNode.action, 'POST', {
            message: document.getElementById("message").value
        })
        .then(d => console.log("happy", d))
        .catch(e => console.log("sad", e))
}

// cómo pintar 1 mensaje (devuelve html que se puede insertar en un div)
function renderMsg(msg) {
    console.log("rendering: ", msg);
    return `<div>${msg.from} @${msg.sent}: ${msg.text}</div>`;
}

// pinta mensajes viejos al cargarse, via AJAX
let messageDiv = document.getElementById("mensajes");
go(config.rootUrl + "/user/received", "GET").then(ms =>
    ms.forEach(m => messageDiv.insertAdjacentHTML("beforeend", renderMsg(m))));

// y aquí pinta mensajes según van llegando
if (ws.receive) {
    const oldFn = ws.receive; // guarda referencia a manejador anterior
    ws.receive = (m) => {
        oldFn(m); // llama al manejador anterior
        messageDiv.insertAdjacentHTML("beforeend", renderMsg(m));
    }
}

// ver https://openlibrary.org/dev/docs/api/books
// no requieren "api key", pero necesitas 1 consulta adicional por autor
function fetchBookData(isbn, targetImg) {
    go(`https://openlibrary.org/isbn/${isbn}.json`, "GET", {}, {}).then(bookInfo => {
        authorLookups = bookInfo.authors.map(a =>
            go(`https://openlibrary.org${a.key}.json`, "GET", {}, {}));
        console.log(`title: ${bookInfo.title}`);
        //targetImg.src = `https://covers.openlibrary.org/b/id/${bookInfo.covers[0]}-M.jpg`;
        readImageUrlData(`https://covers.openlibrary.org/b/id/${bookInfo.covers[0]}-M.jpg`, targetImg);
        Promise.all(authorLookups).then(authorInfos => {
            for (let a of authorInfos) {
                console.log(`Author: ${a.name}`);
            }
        });
    })
}

// ver https://www.omdbapi.com/
// requieren API key, pero se puede conseguir de forma gratuita
// (no uses mucho la que hay ahí abajo, por favor!)
function fetchMovieData(imdb, targetImg) {
    go(`http://www.omdbapi.com/?i=${imdb}&apikey=174a19fd`, "GET", {}, {}).then(movieInfo => {
        console.log(`title: ${movieInfo.Title}`);
        // targetImg.src = movieInfo.Poster;
        readImageUrlData(movieInfo.Poster, targetImg)
    })
}

// click en boton de cargar datos libro
document.querySelector("#fetchBook").onclick = e => {
    let isbn = document.querySelector("#isbn").value;
    console.log("fetching ", isbn);
    fetchBookData(isbn, document.querySelector("#portada"));
};
// click en boton de cargar datos peli
document.querySelector("#fetchMovie").onclick = e => {
    let imdb = document.querySelector("#imdb").value;
    console.log("fetching ", imdb);
    fetchMovieData(imdb, document.querySelector("#poster"));
};
// click en botones de "usar como foto de perfil"
document.querySelectorAll(".perfilable").forEach(o => {
    o.onclick = e => {
        e.preventDefault();
        let url = o.parentNode.action;
        let img = o.parentNode.parentNode.querySelector("img");
        postImage(img, url, "photo").then(() => {
            let cacheBuster = "?" + new Date().getTime();
            document.querySelector("a.nav-link>img.iwthumb").src = url + cacheBuster;
        });
}});

// refresca previsualizacion cuando cambias imagen
document.querySelector("#f_avatar").onchange = e => {
    let img = document.querySelector("#avatar");
    let fileInput = document.querySelector("#f_avatar");
    console.log(img, fileInput);
    readImageFileData(fileInput.files[0], img);
};
// click en boton de enviar avatar
document.querySelector("#postAvatar").onclick = e => {
    e.preventDefault();
    let url = document.querySelector("#postAvatar").parentNode.action;
    let img = document.querySelector("#avatar");
    let file = document.querySelector("#f_avatar");
    postImage(img, url, "photo").then(() => {
        let cacheBuster = "?" + new Date().getTime();
        document.querySelector("a.nav-link>img.iwthumb").src = url + cacheBuster;
    });
};