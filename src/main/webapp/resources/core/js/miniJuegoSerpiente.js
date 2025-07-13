//IMPLEMENTANDO EL PRIMER JUEGO
let resultado = "regular"; // valor temporal que se actualiza al finalizar
let score = 0;
let direction = 'right';
let snake = [{ x: 5, y: 5 }];
let food = { x: 10, y: 10 };

function jugar() {

    stompClient.publish({
        destination: "/app/jugar",
        body: JSON.stringify({ id: mascotaId })
    });
    let nodoEnergia = document.getElementById("valor-energia");
    let valor = parseFloat(nodoEnergia.textContent.replace(/[^\d.]/g, ''));
    if (valor >= "25.00") {
        if (document.getElementById("snakeModal")) return;
        //Se envia al backend estaJugando = true
        stompClient.publish({
            destination: "/app/establecerSiEstaJugando",
            body: JSON.stringify({ id: mascotaId, estaJugando: true})
        });
        // Mostrar el modal
        // CREANDO EL MODAL SOLO CUANDO SE TOCA JUGAR
        const modalHTML = `
<div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
      <div class="modal-header justify-content-between align-items-center">
        <h5 class="modal-title">🐍 Snake Game</h5>
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

        let juegoFinalizado = false; // BANDERA PARA CONTROLAR EL ESTADO DEL USUARIO EN EL JUEGO

        function endGame() {
            if (juegoFinalizado) return;
            juegoFinalizado = true;
            resultado = score >= 50 ? "positivo" : score >= 20 ? "regular" : "negativo";
            clearInterval(gameLoop); // DETENER EL CICLO DE JUEGO ANTES DE PUBLICAR Y SALIR
            stompClient.publish({
                destination: "/app/evaluarResulado",
                // ACA DEBO AGREGAR EL RESULTADO PARA PASARLO AL WEBSOCKET
                body: JSON.stringify({ id: mascotaId })
            });
            console.log(resultado);
            finalizarJuego();
            exitGame();
        }

        function exitGame() {
            if (!juegoFinalizado) {
                resultado = "negativo"; // SE ADVIERTE EL ABANDONO DEL USUARIO
                clearInterval(gameLoop); // DETENER EL CICLO DE JUEGO ANTES DE PUBLICAR Y SALIR
                stompClient.publish({
                    destination: "/app/evaluarResulado",
                    // ACA DEBO AGREGAR EL RESULTADO PARA PASARLO AL WEBSOCKET
                    body: JSON.stringify({ id: mascotaId })
                });

                console.log(resultado);
            }

            finalizarJuego();

            const modal = document.getElementById("snakeModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
            if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
            document.body.classList.remove("modal-open");
            resultado = "regular";
            score = 0;
            direction = 'right';
            snake = [{ x: 5, y: 5 }];
            food = { x: 10, y: 10 };
            juegoFinalizado = false;

        }

        document.addEventListener("keydown", (e) => {
            if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].includes(e.key)) {
                direction = e.key.replace("Arrow", "").toLowerCase();
            }
        });

        const gameLoop = setInterval(draw, 150);
    }
}

function finalizarJuego() {
    stompClient.publish({
        destination: "/app/establecerSiEstaJugando",
        body: JSON.stringify({ id: mascotaId, estaJugando: false})
    });
}
// HASTA ACA