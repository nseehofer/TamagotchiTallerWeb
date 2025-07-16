let resultado = "regular"; // valor temporal que se actualiza al finalizar
let score = 0;
let direction = 'right';
let snake = [{ x: 5, y: 5 }];
let food = { x: 10, y: 10 };

function jugarSnake() {

    stompClient.publish({
        destination: "/app/jugar",
        body: JSON.stringify({ id: mascotaId })
    });
    /*
    stompClient.publish({
        destination: "/app/establecerSiEstaJugando",
        body: JSON.stringify({ id: mascotaId, estaJugando: true})
    });

     */
    estaJugando = true;
    let juegoFinalizado = false;

    let nodoEnergia = document.getElementById("valor-energia");
    let valor = parseFloat(nodoEnergia.textContent.replace(/[^\d.]/g, ''));
    if (valor >= "25.00") {
        if (document.getElementById("snakeModal")) return;
        //Se envia al backend estaJugando = true

        // 🔁 REINICIO DE ESTADO PARA CADA EJECUCIÓN DEL JUEGO
        resultado = "regular";
        score = 0;
        direction = 'right';
        snake = [{ x: 5, y: 5 }];
        food = { x: 10, y: 10 };

        // Mostrar el modal
        // CREANDO EL MODAL SOLO CUANDO SE TOCA JUGAR
        const modalHTML = `
<div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
      <div class="modal-header justify-content-between align-items-center">
        <h5 class="modal-title">Viborita</h5>
        <button type="button" class="btn-exit-custom" onclick="exitGame()">SALIR</button>
      </div>
      <div class="modal-body text-center">
        <p class="leyenda-juego">Usa las flechas del teclado para jugar</p>
        <canvas id="snakeCanvas" width="300" height="300" class="img-fluid border border-light"></canvas>
        <p class="mt-3" id="gameStatus">Usá las flechas para jugar</p>
      </div>
    </div>
  </div>
</div>`;

        document.body.insertAdjacentHTML('beforeend', modalHTML);

        document.getElementById("snakeModal").classList.add("show");
        document.body.classList.add("modal-open");

        const canvas = document.getElementById("snakeCanvas");
        const ctx = canvas.getContext("2d");
        const gridSize = 15;

        function draw() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // dibujar comida
            ctx.fillStyle = "#e91e63";
            ctx.fillRect(food.x * gridSize, food.y * gridSize, gridSize, gridSize);

            // dibujar vibora
            ctx.fillStyle = "#4caf50";
            snake.forEach(part => {
                ctx.fillRect(part.x * gridSize, part.y * gridSize, gridSize, gridSize);
            });

            moveSnake();
            checkCollision();
            document.getElementById("gameStatus").innerText = `Puntaje: ${score}`;
        }

        function moveSnake() {
            const head = { ...snake[0] };

            if (direction === 'up') head.y--;
            if (direction === 'down') head.y++;
            if (direction === 'left') head.x--;
            if (direction === 'right') head.x++;

            snake.unshift(head);

            // comida
            if (head.x === food.x && head.y === food.y) {
                score += 10;
                food = { x: Math.floor(Math.random() * 20), y: Math.floor(Math.random() * 20) };
            } else {
                snake.pop();
            }
        }

        function checkCollision() {
            const head = snake[0];
            if (
                head.x < 0 || head.y < 0 ||
                head.x >= canvas.width / gridSize ||
                head.y >= canvas.height / gridSize ||
                snake.slice(1).some(p => p.x === head.x && p.y === head.y)
            ) {
                endGame();
            }
        }

        function mostrarTamaCoinsModal(resultado) {
            let cantidad = 0;
            if (resultado === "positivo") cantidad = 50;
            else if (resultado === "regular") cantidad = 25;
            else cantidad = 0;

            // 🔲 Fondo opaco detrás
            const overlay = document.createElement("div");
            overlay.style.position = "fixed";
            overlay.style.top = 0;
            overlay.style.left = 0;
            overlay.style.width = "100vw";
            overlay.style.height = "100vh";
            overlay.style.backgroundColor = "rgba(0, 0, 0, 0.7)";
            overlay.style.zIndex = "9998";
            overlay.id = "tama-overlay";

            // 🏆 Modal 8-bit
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
            modal.id = "tama-coins-modal";

            // 💰 Animación dorada del número
            modal.innerHTML = `
        GANASTE <span class="tama-gold-bounce">${cantidad}</span> TAMA COINS
    `;

            // 🧩 Insertar estilos dinámicamente
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
        }



        function endGame() {
            if (juegoFinalizado) return;
            juegoFinalizado = true;
            resultado = score >= 50 ? "positivo" : score >= 20 ? "regular" : "negativo";

            mostrarTamaCoinsModal(resultado); // 🎉 MOSTRAR MODAL ANTES DE PUBLICAR

            clearInterval(gameLoop); // DETENER EL CICLO DE JUEGO ANTES DE PUBLICAR Y SALIR
            stompClient.publish({
                destination: "/app/evaluarResultado",
                body: JSON.stringify({ id: mascotaId, resultado: resultado })
            });

            console.log(resultado);
            /*finalizarJuego();*/
            estaJugando = false;
            exitGame();
        }

        function exitGame() {
            if (!juegoFinalizado) {
                resultado = "negativo"; // SE ADVIERTE EL ABANDONO DEL USUARIO

                mostrarTamaCoinsModal(resultado); // 🎉 MOSTRAR MODAL ANTES DE PUBLICAR

                clearInterval(gameLoop); // DETENER EL CICLO DE JUEGO ANTES DE PUBLICAR Y SALIR
                stompClient.publish({
                    destination: "/app/evaluarResultado",
                    body: JSON.stringify({ id: mascotaId, resultado: resultado })
                });
                estaJugando = false;

                console.log(resultado);
            }

            /*finalizarJuego();*/
            const modal = document.getElementById("snakeModal");
            if (modal) modal.remove();
            document.body.classList.remove("modal-open");

            resultado = "regular";
            score = 0;
            direction = 'right';
            snake = [{ x: 5, y: 5 }];
            food = { x: 10, y: 10 };
            juegoFinalizado = false;
        }


        document.addEventListener("keydown", (e) => {
            const key = e.key.replace("Arrow", "").toLowerCase();
            const opposite = {
                up: 'down',
                down: 'up',
                left: 'right',
                right: 'left'
            };

            if (["up", "down", "left", "right"].includes(key) && key !== opposite[direction]) {
                direction = key;
            }
        });


        const gameLoop = setInterval(draw, 150);
    }
}
/*
function finalizarJuego() {
    estaJugando = false;
    stompClient.publish({
        destination: "/app/establecerSiEstaJugando",
        body: JSON.stringify({ id: mascotaId, estaJugando: false})
    });
}
*/
// HASTA ACA