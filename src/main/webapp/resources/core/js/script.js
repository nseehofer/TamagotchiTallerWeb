let mascotaId = document.getElementById("mascota-id").value;

setInterval(actualizarDatosMascota, 60000); //actualiza datos cada un minuto

let basePath = window.location.pathname.split('/')[1];
const brokerPath = `ws://${window.location.hostname}:8080/${basePath}/wschat`;

const stompClient = new StompJs.Client({
    
    brokerURL: brokerPath

});

stompClient.debug = function(str) {
    console.log(str)
 };

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', (m) => {
        let valorFelicidadActualizado = JSON.parse(m.body).felicidad.toFixed(2);
        let valorActualizadoDelHambre = JSON.parse(m.body).hambre.toFixed(2);
        let valorHigieneActualizado = JSON.parse (m.body).higiene.toFixed(2);
        let valorEnergiaActualizado = JSON.parse(m.body).energia.toFixed(2);
        console.log("Mensaje recibido:",valorActualizadoDelHambre);
        console.log("Mensaje recibido:",valorHigieneActualizado)
        console.log("Mensaje recibido:",valorEnergiaActualizado)
        console.log("Mensaje recibido:",valorFelicidadActualizado)
        /*const messagesContainer = document.getElementById("chat-messages");
        const newMessage = document.createElement("p")
        newMessage.textContent = JSON.parse(m.body).content;
        messagesContainer.appendChild(newMessage);
        */
        const valorHambre = document.getElementById("valor-hambre");
        const valorHigiene = document.getElementById("valor-higiene");
        const valorEnergia = document.getElementById("valor-energia");
        const valorFelicidad = document.getElementById("valor-felicidad");
        valorHambre.textContent = valorActualizadoDelHambre + '%';
        valorHigiene.textContent = valorHigieneActualizado + '%';
        valorEnergia.textContent = valorEnergiaActualizado + '%';
        valorFelicidad.textContent = valorFelicidadActualizado + '%';
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
function alimentarMascota(){

    /*let input = document.getElementById("message");
    let message = input.value;*/

    stompClient.publish({
        destination: "/app/alimentar",
        body: JSON.stringify({id: mascotaId})
    });

}

function limpiarMascota() {
    stompClient.publish({
        destination: "/app/limpiar",
        body: JSON.stringify({id: mascotaId})
    });
}

function jugar() {
    stompClient.publish({
        destination: "/app/jugar",
        body: JSON.stringify({id: mascotaId})
    });
}

function dormirMascota() {
    stompClient.publish({
        destination: "/app/dormir",
        body: JSON.stringify({id: mascotaId})
    });
}

function actualizarDatosMascota() {
    stompClient.publish({
        destination: "/app/actualizar",
        body:JSON.stringify({id: mascotaId})
    });
}