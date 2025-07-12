let mascotaId = document.getElementById("mascota-id").value;
const mensajeEnfermedad = document.getElementById("mensaje-enfermedad");
const mainContainer = document.getElementById("main-container");
let estaDormido = false;


//actualiza datos cada un minuto
setInterval(actualizarDatosMascota, 2400000);

let basePath = window.location.pathname.split('/')[1];
const brokerPath = `ws://${window.location.hostname}:8080/${basePath}/wschat`;

const stompClient = new StompJs.Client({

    brokerURL: brokerPath

});

stompClient.debug = function (str) {
    console.log(str)
};

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', (m) => {

        let contenido = JSON.parse(m.body);

        if (typeof contenido === "string") {
            const modalMensaje = document.getElementById("modal-error-mensaje");
            modalMensaje.textContent = contenido;

            const modalBootstrap = new bootstrap.Modal(document.getElementById("modal-error"));
            modalBootstrap.show();

            setTimeout(() => {
                document.activeElement.blur();
                modalBootstrap.hide();
            }, 3000);
            return;

        }

        let valorFelicidadActualizado = contenido.felicidad.toFixed(2);
        let valorActualizadoDelHambre = contenido.hambre.toFixed(2);
        let valorHigieneActualizado = contenido.higiene.toFixed(2);
        let valorEnergiaActualizado = contenido.energia.toFixed(2);
        let estaEnfermo = contenido.estaEnfermo;
        let estaVivo = contenido.estaVivo;
        estaDormido = contenido.estaDormido;





        if (!estaVivo) {
            window.location.href = `/${basePath}/mascota/cementerio?id=${mascotaId}`;
        } else {
            const valorHambre = document.getElementById("valor-hambre");
            const valorHigiene = document.getElementById("valor-higiene");
            const valorEnergia = document.getElementById("valor-energia");
            const valorFelicidad = document.getElementById("valor-felicidad");

            valorHambre.textContent = valorActualizadoDelHambre + '%';
            valorHigiene.textContent = valorHigieneActualizado + '%';
            valorEnergia.textContent = valorEnergiaActualizado + '%';
            valorFelicidad.textContent = valorFelicidadActualizado + '%';


            if (estaEnfermo) {
                mensajeEnfermedad.classList.remove("d-none");
            } else {
                mensajeEnfermedad.classList.add("d-none");
            }

            if (estaDormido) {
                mainContainer.classList.add("modo-noche");
            } else {
                mainContainer.classList.remove("modo-noche");
            }

            const botones = document.querySelectorAll('.button');
            botones.forEach(boton => {
                if (boton.getAttribute('onclick') === 'cambiarEstadoDormidoODespierto()') {
                    boton.disabled = false;
                    boton.style.cursor = 'pointer';
                    boton.style.opacity = '1';
                } else {
                    if (estaDormido) {
                        boton.disabled = true;
                        boton.style.cursor = 'not-allowed';
                        boton.style.opacity = '0.5';
                    } else {
                        boton.disabled = false;
                        boton.style.cursor = 'pointer';
                        boton.style.opacity = '1';
                    }
                }
            });

        }


    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

stompClient.activate();

// Take the value in the ‘message-input’ text field and send it to the server with empty headers.
function alimentarMascota() {

    /*let input = document.getElementById("message");
    let message = input.value;*/

    stompClient.publish({
        destination: "/app/alimentar",
        body: JSON.stringify({ id: mascotaId })
    });

}

function limpiarMascota() {
    stompClient.publish({
        destination: "/app/limpiar",
        body: JSON.stringify({ id: mascotaId })
    });
}

//IMPLEMENTANDO EL PRIMER JUEGO
let resultado = "regular"; // valor temporal que se actualiza al finalizar
let score = 0;
let direction = 'right';
let snake = [{ x: 5, y: 5 }];
let food = { x: 10, y: 10 };

function jugar() {
    if (document.getElementById("snakeModal")) return;
    // Mostrar el modal
    // CREANDO EL MODAL SOLO CUANDO SE TOCA JUGAR
    const modalHTML = `
    <div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
      <div class="modal-dialog modal-dialog-centered" style="max-width: 80vw; height: 80vh; margin: auto;">
        <div class="modal-content bg-dark text-white border border-warning" style="height: 100%; font-family: 'Press Start 2P', monospace; display: flex; flex-direction: column; justify-content: center;">
          <div class="modal-header">
            <h5 class="modal-title">🐍 Snake Game</h5>
            <button type="button" class="btn-close btn-danger" onclick="exitGame()"></button>
          </div>
          <div class="modal-body text-center">
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
            destination: "/app/jugar",
            // ACA DEBO AGREGAR EL RESULTADO PARA PASARLO AL WEBSOCKET
            body: JSON.stringify({ id: mascotaId })
        });
        console.log(resultado);
        exitGame();
    }

    function exitGame() {
        if (!juegoFinalizado) {
            resultado = "negativo"; // SE ADVIERTE EL ABANDONO DEL USUARIO
            clearInterval(gameLoop); // DETENER EL CICLO DE JUEGO ANTES DE PUBLICAR Y SALIR
            stompClient.publish({
                destination: "/app/jugar",
                // ACA DEBO AGREGAR EL RESULTADO PARA PASARLO AL WEBSOCKET
                body: JSON.stringify({ id: mascotaId })
            });
            console.log(resultado);
        }

        const modal = document.getElementById("snakeModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
        if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
        document.body.classList.remove("modal-open");
    }

    document.addEventListener("keydown", (e) => {
        if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].includes(e.key)) {
            direction = e.key.replace("Arrow", "").toLowerCase();
        }
    });

    const gameLoop = setInterval(draw, 150);
}

// HASTA ACA


function curarMascota() {
    stompClient.publish({
        destination: "/app/curarMascota",
        body: JSON.stringify({ id: mascotaId })
    });
}

function dormirMascota() {
    stompClient.publish({
        destination: "/app/dormir",
        body: JSON.stringify({ id: mascotaId })
    });
}

function actualizarDatosMascota() {
    stompClient.publish({
        destination: "/app/actualizar",
        body: JSON.stringify({ id: mascotaId })
    });
}

function abrigarMascota() {
    const btn = document.getElementById('btn-abrigar');

    const isPressed = btn.classList.contains('pressed');

    btn.classList.toggle('pressed');

    const destino = isPressed ? "/app/desabrigar" : "/app/abrigar";

    stompClient.publish({
        destination: destino,
        body: JSON.stringify({ id: mascotaId })
    });
}


function cambiarEstadoDormidoODespierto() {
    if (estaDormido) {
        stompClient.publish({
            destination: "/app/despertar",
            body: JSON.stringify({ id: mascotaId })
        });
    }
    if (!estaDormido) {
        stompClient.publish({
            destination: "/app/dormir",
            body: JSON.stringify({ id: mascotaId })
        });
    }
}


