function jugarMemoTest() {

    const cerrarSinJugar = () => {
        estaJugando = false;
        const modal = document.getElementById("snakeModal");
        if (modal) modal.remove();
        document.body.classList.remove("modal-open");
    }

    let nodoEnergia = document.getElementById("valor-energia");
    let valor = parseFloat(nodoEnergia.textContent.replace(/[^\d.]/g, ''));
    if (valor <= 25.00) {
        cerrarSinJugar();
        return;
    }

    estaJugando = true;
    stompClient.publish({
        destination: "/app/jugar",
        body: JSON.stringify({ id: mascotaId })
    });

    document.getElementById('memotest').blur();
    const modalHTML = `
    <div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
                <div class="modal-header justify-content-between align-items-center">
                    <h5 class="modal-title">Memotest</h5>
                    <button type="button" class="btn-exit-custom" id="cerrar">SALIR</button>
                </div>
                <div class="modal-body text-center">
                    <p class="leyenda-juego">Encontra todos los pares</p>
                    <div class="memotest-board" id="memotest-board"></div>
                    <p class="mt-3" id="estado-memotest">Intentos: 0</p>
                </div>
            </div>
        </div>
    </div>`;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    document.getElementById("snakeModal").classList.add("show");
    document.body.classList.add("modal-open");

    function inicializarMemotest() {
        const imagenesAnimales = [
            '../img/animal1.png', '../img/animal2.png', '../img/animal3.png', '../img/animal4.png',
            '../img/animal5.png', '../img/animal6.png', '../img/animal7.png', '../img/animal8.png'
        ];
        const imagenReverso = '../img/reverso.png';

        const board = document.getElementById('memotest-board');
        let intentos = 0;
        let aciertos = 0;
        document.getElementById('estado-memotest').textContent = 'Intentos: 0';

        let cartas = [...imagenesAnimales, ...imagenesAnimales];
        cartas.sort(() => 0.5 - Math.random());

        let primeraCarta = null;
        let segundaCarta = null;
        let puedeJugar = true;

        cartas.forEach((imgSrc, index) => {
            const card = document.createElement('div');
            card.classList.add('memotest-card');
            card.dataset.index = index;
            card.dataset.imagen = imgSrc;

            const img = document.createElement('img');
            img.src = imagenReverso;
            img.classList.add('img-memo');

            card.appendChild(img);
            board.appendChild(card);

            card.addEventListener('click', () => {
                if (!puedeJugar || card.classList.contains('matched') || card.classList.contains('flipped')) return;

                img.src = imgSrc;
                card.classList.add('flipped');

                if (!primeraCarta) {
                    primeraCarta = card;
                } else {
                    segundaCarta = card;
                    puedeJugar = false;
                    intentos++;
                    document.getElementById('estado-memotest').textContent = 'Intentos: ' + intentos;

                    if (primeraCarta.dataset.imagen === segundaCarta.dataset.imagen) {
                        primeraCarta.classList.add('matched');
                        segundaCarta.classList.add('matched');
                        aciertos++;
                        reiniciarSeleccion();

                        if (aciertos === imagenesAnimales.length) {
                            setTimeout(() => {
                                finalizarMemoTest(intentos);
                            }, 800);
                        }
                    } else {
                        setTimeout(() => {
                            primeraCarta.querySelector('img').src = imagenReverso;
                            segundaCarta.querySelector('img').src = imagenReverso;
                            primeraCarta.classList.remove('flipped');
                            segundaCarta.classList.remove('flipped');
                            reiniciarSeleccion();
                        }, 1000);
                    }
                }
            });
        });

        function reiniciarSeleccion() {
            primeraCarta = null;
            segundaCarta = null;
            puedeJugar = true;
        }
    }

    function finalizarMemoTest(intentos) {
        estaJugando = false;
        let resultado;

        if (intentos <= 15) {
            resultado = "positivo";
        } else if (intentos <= 25) {
            resultado = "regular";
        } else {
            resultado = "negativo";
        }

        console.log("Resultado del MemoTest:", resultado);

        // Modal de recompensa 🎉
        let cantidad = resultado === "positivo" ? 50 :
                       resultado === "regular" ? 25 : 0;

        const overlay = document.createElement("div");
        overlay.style.position = "fixed";
        overlay.style.top = 0;
        overlay.style.left = 0;
        overlay.style.width = "100vw";
        overlay.style.height = "100vh";
        overlay.style.backgroundColor = "rgba(0, 0, 0, 0.7)";
        overlay.style.zIndex = "9998";
        overlay.id = "tama-overlay";

        const modal = document.createElement("div");
        modal.style.position = "fixed";
        modal.style.top = "40%";
        modal.style.left = "50%";
        modal.style.transform = "translate(-50%, -50%)";
        modal.style.padding = "2rem";
        modal.style.backgroundColor = "#000";
        modal.style.color = "#fff";
        modal.style.border = "3px solid #09a1a1";
        modal.style.fontFamily = "'Press Start 2P', cursive";
        modal.style.fontSize = "14px";
        modal.style.textAlign = "center";
        modal.style.zIndex = "9999";
        modal.style.borderRadius = "8px";
        modal.style.boxShadow = "0 0 15px #09a1a1";
        modal.id = "memo-coins-modal";
        modal.innerHTML = `
            GANASTE <span class="tama-gold-bounce">${cantidad}</span> TAMA COINS
        `;

        const style = document.createElement("style");
        style.textContent = `
            @keyframes bounce {
                0%   { transform: scale(1); }
                30%  { transform: scale(1.3); }
                50%  { transform: scale(0.9); }
                70%  { transform: scale(1.1); }
                100% { transform: scale(1); }
            }
            .tama-gold-bounce {
                color: #ffd700;
                animation: bounce 1.2s ease;
                display: inline-block;
                text-shadow: 0 0 3px #fff000;
            }
        `;
        document.head.appendChild(style);
        document.body.appendChild(overlay);
        document.body.appendChild(modal);

        setTimeout(() => {
            modal.remove();
            overlay.remove();
            style.remove();
        }, 3000);

        stompClient.publish({
            destination: "/app/evaluarResultado",
            body: JSON.stringify({ id: mascotaId, resultado: resultado })
        });

        const modalExistente = document.getElementById("snakeModal");
        if (modalExistente) modalExistente.remove();
        document.body.classList.remove("modal-open");
    }

    document.getElementById('cerrar').addEventListener('click', function (event) {
        cerrar();
    });

    const cerrar = () => {
        resultado = "negativo";
        console.log(resultado);
        stompClient.publish({
            destination: "/app/evaluarResultado",
            body: JSON.stringify({ id: mascotaId, resultado: resultado })
        });
        estaJugando = false;
        const modal = document.getElementById("snakeModal");
        if (modal) modal.remove();
        document.body.classList.remove("modal-open");
    }

    inicializarMemotest();
}