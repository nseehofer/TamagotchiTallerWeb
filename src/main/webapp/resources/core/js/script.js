let mascotaId = document.getElementById("mascota-id").value;
const mensajeEnfermedad = document.getElementById("mensaje-enfermedad");
const mainContainer = document.getElementById("main-container");
let estaDormido = false;


//actualiza datos cada un minuto
setInterval(actualizarDatosMascota, 20000);

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
        let valorMonedasActualizado = contenido.monedas;





        if (!estaVivo) {
            window.location.href = `/${basePath}/mascota/cementerio?id=${mascotaId}`;
        } else {
            const valorHambre = document.getElementById("valor-hambre");
            const valorHigiene = document.getElementById("valor-higiene");
            const valorEnergia = document.getElementById("valor-energia");
            const valorFelicidad = document.getElementById("valor-felicidad");
            const valorMonedas = document.getElementById("valor-monedas");

            valorHambre.textContent = valorActualizadoDelHambre + '%';
            valorHigiene.textContent = valorHigieneActualizado + '%';
            valorEnergia.textContent = valorEnergiaActualizado + '%';
            valorFelicidad.textContent = valorFelicidadActualizado + '%';
            valorMonedas.textContent = '$' + valorMonedasActualizado;

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

    // Toggle visual estado del botón primero
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


