let resultado = "regular"; // valor temporal que se actualiza al finalizar
let score = 0;
let direction = 'right';
let snake = [{ x: 5, y: 5 }];
let food = { x: 10, y: 10 };
let modalMostrado = false; // ✅ NUEVO: flag para prevenir doble modal

function jugarSnake() {
    stompClient.publish({
        destination: "/app/jugar",
        body: JSON.stringify({ id: mascotaId })
    });

    estaJugando = true;
    let juegoFinalizado = false;

    if (document.getElementById("snakeModal")) return;

    resultado = "regular";
    score = 0;
    direction = 'right';
    snake = [{ x: 5, y: 5 }];
    food = { x: 10, y: 10 };
    modalMostrado = false; // ✅ Reiniciamos el flag al iniciar juego

    const modalHTML = `
<div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
      <div class="modal-header justify-content-between align-items-center">
        <h5 class="modal-title">Viborita</h5>
        <button type="button" class="btn-exit-custom" onclick="exitGame()">SALIR</button>
      </div>
      <div class="modal-body p-0">
        <div class="d-flex justify-content-center align-items-center flex-column w-100" style="min-height: 60vh;">
          <p class="leyenda-juego text-center mb-3 pt-3">Usa las flechas del teclado para jugar</p>
          <div class="d-flex justify-content-center align-items-center w-100" style="flex-grow:1;">
            <canvas id="snakeCanvas" width="300" height="300"
              class="border border-light img-fluid mb-3"
              style="max-width: 130vw; height: auto;"></canvas>
          </div>
          <p id="gameStatus" class="text-center">Usá las flechas para jugar</p>
        </div>
      </div>
    </div>
  </div>
</div>
`;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    document.getElementById("snakeModal").classList.add("show");
    document.body.classList.add("modal-open");

    const canvas = document.getElementById("snakeCanvas");
    const ctx = canvas.getContext("2d");
    const gridSize = 15;

    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = "#e91e63";
        ctx.fillRect(food.x * gridSize, food.y * gridSize, gridSize, gridSize);
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
        if (modalMostrado) return; // ✅ Prevenir duplicados
        modalMostrado = true;

        let cantidad = resultado === "positivo" ? 50 : resultado === "regular" ? 25 : 0;
        const overlay = document.createElement("div");
        overlay.style = `position:fixed;top:0;left:0;width:100vw;height:100vh;background-color:rgba(0,0,0,0.7);z-index:9998`;
        overlay.id = "tama-overlay";

        const modal = document.createElement("div");
        modal.style = `position:fixed;top:40%;left:50%;transform:translate(-50%,-50%);
                padding:2rem;background-color:#000;color:#fff;border:3px solid #09a1a1;
                font-family:'Press Start 2P',cursive;font-size:14px;text-align:center;
                z-index:9999;border-radius:8px;box-shadow:0 0 15px #09a1a1`;
        modal.id = "tama-coins-modal";
        modal.innerHTML = `GANASTE <span class="tama-gold-bounce">${cantidad}</span> TAMA COINS`;

        const style = document.createElement("style");
        style.textContent = `
@keyframes bounce {
    0% { transform: scale(1); }
    30% { transform: scale(1.3); }
    50% { transform: scale(0.9); }
    70% { transform: scale(1.1); }
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
        mostrarTamaCoinsModal(resultado);
        clearInterval(gameLoop);
        stompClient.publish({
            destination: "/app/evaluarResultado",
            body: JSON.stringify({ id: mascotaId, resultado: resultado })
        });
        console.log(resultado);
        estaJugando = false;
        exitGame();
    }

    function exitGame() {
        if (!juegoFinalizado) {
            resultado = "negativo";
            mostrarTamaCoinsModal(resultado);
            clearInterval(gameLoop);
            stompClient.publish({
                destination: "/app/evaluarResultado",
                body: JSON.stringify({ id: mascotaId, resultado: resultado })
            });
            estaJugando = false;
            console.log(resultado);
        }

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