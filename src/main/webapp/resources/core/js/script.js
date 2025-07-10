let mascotaId = document.getElementById("mascota-id").value;


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

            const mensajeEnfermedad = document.getElementById("mensaje-enfermedad");
            if (estaEnfermo) {
                mensajeEnfermedad.classList.remove("d-none");
            } else {
                mensajeEnfermedad.classList.add("d-none");
            }
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

function jugar() {
    stompClient.publish({
        destination: "/app/jugar",
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
    /*const nodeBtnToWrapUp = document.getElementById("btn-abrigar");
    
    nodeBtnToWrapUp.disabled = true;*/

    stompClient.publish({
        destination: "/app/abrigar",
        body: JSON.stringify({ id: mascotaId })
    });
}


