function jugar() {

const modalHTML = `
<div class="modal fade show" id="menuJuegoModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
      <div class="modal-header justify-content-between align-items-center">
        <h5 class="modal-title">Seleccion de juegos</h5>
        <button type="button" class="btn-exit-custom" onclick="cerrarMenuDeJuego()">SALIR</button>
      </div>
      <div class="modal-body text-center">
        <p class="leyenda-juego">Selecciona el juego</p>
	<button type="button" onClick="jugarSnake()"> Viborita </button>
	<button type="button" id="tetris" onClick="jugarTetris()"> Tetris</button>
	<button type="button" id="memotest" onClick="jugarMemoTest()"> MemoTest </button>
	<button type="button" onClick="jugarTateti()"> TaTeTi</button>
	<button type="button" onClick="jugarAhorcado()"> Ahorcado</button>
	<button type="button" onClick="jugarSimon()"> Simon Dice</button>
	<button type="button" onClick="jugarPiedra()"> Piedra Papel Tijera</button>
	<button type="button" onClick="jugarSopa()"> Sopa de Letras</button>
	<button type="button" onClick="jugarPalabras()"> Palabras Desordenadas</button>
	<button type="button" onClick="jugarNumeros()"> Adivina el numero</button>
      </div>
    </div>
  </div>
</div>`;

document.body.insertAdjacentHTML('beforeend', modalHTML);
document.getElementById("snakeModal").classList.add("show");
document.body.classList.add("modal-open");
}

function cerrarMenuDeJuego(){
    const modal = document.getElementById("menuJuegoModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
    if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
document.body.classList.remove("modal-open");
}