function waitForStomp(cb) {
    const t = setInterval(() => {
        if (ws.stompClient?.connected) { clearInterval(t); cb(); }
    }, 300);
}

waitForStomp(() => {
    ws.stompClient.subscribe(`/topic/gartic/lobby/${lobbyCode}/chat`, (msg) => {
        const { username: from, text } = JSON.parse(msg.body);
        const box = document.getElementById('chat-messages');
        box.insertAdjacentHTML('beforeend',
            `<div class="d-flex gap-2 mb-1">
                <span class="fw-bold text-primary">${from}</span>
                <span>${text}</span>
            </div>`
        );
        box.scrollTop = box.scrollHeight;
        const badge = document.querySelector("#chat-button-badge")
        badge.textContent = parseInt(badge.textContent) + 1
        badge.classList.remove("visually-hidden")
    });

    document.getElementById('chat-send').addEventListener('click', sendChat);
    document.getElementById('chat-input').addEventListener('keydown', e => e.key === 'Enter' && sendChat());
    document.querySelector("#chat-offcanvas").addEventListener('hide.bs.offcanvas', ()=>{
        document.querySelector("#chat-button-badge").classList.add("visually-hidden");
        document.querySelector("#chat-button-badge").textContent = 0;
    })
});

function sendChat() {
    const input = document.getElementById('chat-input');
    if (!input.value.trim()) return;
    ws.stompClient.send(`/gartic/lobby/${lobbyCode}/chat`, {}, JSON.stringify({ username, text: input.value.trim() }));
    input.value = '';
}