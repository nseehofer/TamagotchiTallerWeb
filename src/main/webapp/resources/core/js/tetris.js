
function jugarTetris(){

const cerrarSinJugar = () =>{
	estaJugando = false;
	const modal = document.getElementById("snakeModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
	if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
	document.body.classList.remove("modal-open");
}

let nodoEnergia = document.getElementById("valor-energia");
let valor = parseFloat(nodoEnergia.textContent.replace(/[^\d.]/g, ''));
if (valor <= "25.00"){
	cerrarSinJugar();
}

estaJugando = true;
stompClient.publish({
	destination: "/app/jugar",
	body: JSON.stringify({ id: mascotaId })
});

document.getElementById('tetris').blur();
const modalHTML = `
<div class="modal fade show" id="snakeModal" tabindex="-1" aria-modal="true" role="dialog" style="display: block; background-color: rgba(0,0,0,0.8);">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-dark text-white border border-warning" style="font-family: 'Press Start 2P', monospace;">
      <div class="modal-header justify-content-between align-items-center">
        <h5 class="modal-title">Tetris Game</h5>
        <button type="button" class="btn-exit-custom" id="cerrar">SALIR</button>
      </div>
      <div class="modal-body text-center">
	  <p class="leyenda-juego">  'C':cambiar pieza, 'espacio': fastDrop!</p>
    <div>
        <button class="btn btn-danger" id="start" onClick="startGame()">Start</button>
        <button class="btn btn-success" id="pause" onclick="pauseGame()">Pause</button>
        <button class="btn btn-success" id="soundOnOff">Sound off/on</button>
    </div>        
    <section class="playground">
        <div id="gameInfo">
            <canvas id="holdPieceCanvas" width="200" height="100" style="border: solid 1px;">
            </canvas>
            <br>
            <strong>Score: <span id="score"></span></strong>
            <br>
            <strong>Line Counter <span id="lineCounter"></span></strong>
            <h2 id="level">Level: </h2>
        </div>
        <div>

            <canvas id="canvas" width="200" height="100">
            </canvas>
        </div>
        <div>
            <canvas id="nextPiecesCanvas" width="200" height="100" style="border:1px solid #000000;">
            </canvas>
        </div>
    </section>
      </div>
    </div>
  </div>
</div>`;
document.body.insertAdjacentHTML('beforeend', modalHTML);

document.getElementById("snakeModal").classList.add("show");
document.body.classList.add("modal-open");

const BLOCK_SIZE = 18;

const BOARD_WIDTH= 10;
const BOARD_HEIGHT= 22;

const NEXT_PIECES_BOARD_WIDTH= 6;
const NEXT_PIECES_BOARD_HEIGHT= 15;

const HOLD_PIECE_BOARD_WIDTH= 6;
const HOLD_PIECE_BOARD_HEIGHT= 5;

const piece = {
		color : 'red',
    position: {x:4, y:0},
    shape: [
      ['red','red'],
      ['red','red']
    ]
};

const pieces = [

	[
    ['yellow','yellow'],
    ['yellow','yellow']
	],

 	[
		['lightblue','lightblue','lightblue','lightblue']
	],

	[
    [0,'magenta',0],
    ['magenta','magenta','magenta']
	],

	[
    [0,'green','green'],
    ['green','green',0]
	],

	[
    ['red','red',0],
    [0,'red','red']
	],

	[
    ['orange',0],
    ['orange',0],
    ['orange','orange']
	],

	[
    [0,'blue'],
    [0,'blue'],
    ['blue','blue']
	],

]

let randomizedPieces;

/* Randomize array in-place using Durstenfeld shuffle algorithm !!!!!!!*/ 
const fillRandomizedBag = () =>{
	randomizedPieces = pieces.slice();

	for(let i= randomizedPieces.length-1; i > 0; i--){
		let j = Math.floor(Math.random() * (i+1));
		let temp = randomizedPieces[i];
		randomizedPieces[i] = randomizedPieces[j];
		randomizedPieces[j] = temp;
	}
}

let nextPieces = [];

let holdedPiece= [];
let holdUsedInThisTurn= false;

const fillNextPieces = () => {
	nextPieces= randomizedPieces.splice(0,3);
}

const firstPiece = () =>{
	piece.shape = randomizedPieces.splice(0,1)[0];
}

const pickPieceFromNextPieces = () =>{
	piece.shape =nextPieces.splice(0,1)[0];
}

const refillNextPieces = () =>{
	if(randomizedPieces.length > 0){
		nextPieces.push(randomizedPieces.splice(0,1)[0]);
	}else{
	fillRandomizedBag();
	refillNextPieces();
	}
}

const level ={
  stage:0,
  dropTime:200
}
//const audio= new window.Audio('Tetris.mp3');
const audio= new window.Audio(document.getElementById("tetrisMusic").value);
audio.loop=true;
let playingMusic = false;

let isRunning = false;
let refreshInterval;
let randomIndex;

let levelElement =document.getElementById('level');

let score = 0;
const scoreElement= document.getElementById('score');

const startButton = document.getElementById('start');
const pauseButton = document.getElementById('pause');
const soundOnOff = document.getElementById('soundOnOff');

let lineCounter = 0;
let totalLineCounter = 0;
const totalLineCounterElement= document.getElementById('lineCounter');

//Game canvas
const canvas = document.getElementById("canvas");
const context = canvas.getContext("2d");

canvas.width= BLOCK_SIZE * BOARD_WIDTH;
canvas.height= BLOCK_SIZE * BOARD_HEIGHT;
context.scale(BLOCK_SIZE, BLOCK_SIZE);

//Next pieces canvas
const nextPiecesCanvas = document.getElementById("nextPiecesCanvas");
const nextPiecesContext = nextPiecesCanvas.getContext("2d");

nextPiecesCanvas.width= BLOCK_SIZE * 6;
nextPiecesCanvas.height= BLOCK_SIZE * 15;
nextPiecesContext.scale(BLOCK_SIZE, BLOCK_SIZE);

//Hold piece canvas 
const holdPieceCanvas = document.getElementById("holdPieceCanvas");
const holdPieceContext = holdPieceCanvas.getContext("2d");

holdPieceCanvas.width= BLOCK_SIZE * 6;
holdPieceCanvas.height= BLOCK_SIZE * 5;
holdPieceContext.scale(BLOCK_SIZE, BLOCK_SIZE);

const createBoard = (width,height) => {
  return Array(height).fill(0).map(() => Array(width).fill(0))
}

const board = createBoard(BOARD_WIDTH, BOARD_HEIGHT);
const nextPiecesBoard = createBoard(NEXT_PIECES_BOARD_WIDTH,NEXT_PIECES_BOARD_HEIGHT);
const holdPieceBoard = createBoard(HOLD_PIECE_BOARD_WIDTH,HOLD_PIECE_BOARD_HEIGHT);

const drawHoldPiece = () =>{
	holdPieceContext.fillStyle = '#000';
	holdPieceContext.fillRect(0,0,holdPieceCanvas.width,holdPieceCanvas.height);

	for (let i = 0; i < holdPieceBoard.length; i++){
		for (let j = 0; j < holdPieceBoard[i].length; j++){
	  	if (holdPieceBoard[i][j] == 0){
	    	holdPieceContext.fillStyle = holdPieceBoard[i][j];
	    	holdPieceContext.fillRect(j, i, 1 ,1);

	  		holdPieceContext.lineWidth=0.1;
				holdPieceContext.strokeStyle='black';
				holdPieceContext.strokeRect(j,i,1,1);
	  	}
		}
	}

	for (let index = 0; index < holdedPiece.length; index++){
		for (let j = 0; j < holdedPiece[index].length; j++){
		  if (holdedPiece[index][j] !== 0){
		    holdPieceContext.fillStyle = holdedPiece[index][j];
		    holdPieceContext.fillRect(j + 1, index + 1, 1 ,1);
		    
				holdPieceContext.lineWidth=0.1;
				holdPieceContext.strokeStyle='black';
				holdPieceContext.strokeRect(j + 1, index + 1,1,1);
		  } 
		}   
	}

};

const drawNextPieces = () =>{
	nextPiecesContext.fillStyle = '#000';
	nextPiecesContext.fillRect(0,0,nextPiecesCanvas.width,nextPiecesCanvas.height);

	for (let i = 0; i < nextPiecesBoard.length; i++){
		for (let j = 0; j < nextPiecesBoard[i].length; j++){
	  	if (nextPiecesBoard[i][j] == 0){
	    	nextPiecesContext.fillStyle = nextPiecesBoard[i][j];
	    	nextPiecesContext.fillRect(j, i, 1 ,1);

	  		nextPiecesContext.lineWidth=0.1;
				//nextPiecesContext.strokeStyle='white';
				nextPiecesContext.strokeRect(j,i,1,1);
	  	}
		}
	}

	let axisAcum= 2;
	nextPieces.forEach((shape, index)=>{

	for (let index = 0; index < shape.length; index++){
		for (let j = 0; j < shape[index].length; j++){
		  if (shape[index][j] !== 0){
		    nextPiecesContext.fillStyle = shape[index][j];
		    nextPiecesContext.fillRect(j + 2 , index + axisAcum , 1 ,1);

				nextPiecesContext.lineWidth=0.1;
				nextPiecesContext.strokeStyle='black';

				nextPiecesContext.strokeRect(j + 2,index + axisAcum ,1,1);
		  } 
		}

	}
			axisAcum+=4;
	});

}

const draw = () =>{
	context.fillStyle = '#000';
	context.fillRect(0,0,canvas.width,canvas.height);

	for (let i = 0; i < board.length; i++){
		for (let j = 0; j < board[i].length; j++){
	  	if (board[i][j] !== 0){
	    	context.fillStyle = board[i][j];
	    	context.fillRect(j, i, 1 ,1);

	  		context.lineWidth=0.1;
				context.strokeStyle='black';
				context.strokeRect(j,i,1,1);
	  	}
		}
	}

	for (let index = 0; index < piece.shape.length; index++){
		for (let j = 0; j < piece.shape[index].length; j++){
		  if (piece.shape[index][j] !== 0){
		    context.fillStyle = piece.shape[index][j];
		    context.fillRect(j + piece.position.x, index + piece.position.y, 1 ,1);
		    
				context.lineWidth=0.1;
				context.strokeStyle='black';
				context.strokeRect(j + piece.position.x,index + piece.position.y,1,1);
		  } 
		}   
	}

};

const holdPiece = () =>{

	if(holdedPiece.length > 0){
		let auxForSwap = holdedPiece;
		holdedPiece = piece.shape;
		piece.shape = auxForSwap;
		holdUsedInThisTurn= true;
	}else{
		holdedPiece = piece.shape;
		pickPieceFromNextPieces();
  	refillNextPieces();
  	holdUsedInThisTurn= true;
	}
	piece.position.x= Math.floor(4);
  piece.position.y= 0;
}

const checkCollision = () => {
    return piece.shape.find((row,y) => {
        return row.find((value,x)=>{
          return (value != 0 &&  board[ y + piece.position.y]?.[ x + piece.position.x] != 0);
        })
      }
    )
}

const solidifyPiece = () => {
	piece.shape.forEach((row, y)=>{
	  row.forEach((value,x)=>{
	    if( value != 0 ){
	      board[ y + piece.position.y][ x + piece.position.x] = value;
	    }
	  }
		)
	})

  //random piece
  randomIndex = Math.floor(Math.random() * pieces.length);
  //piece.shape= pieces[randomIndex];
  pickPieceFromNextPieces();
  refillNextPieces();
  //piece.color= pieces[randomIndex];
	//RESET POSITION
  piece.position.x= Math.floor(4);
  piece.position.y= 0;
  if (checkCollision()) {
  	endGame();
  	/*
    window.alert('Game Over!!');
    board.forEach((row)=> row.fill(0))
    score=0;
    */
  }

}

const removeRows = () =>{
	let rowsToRemove =[];
  board.forEach((row,y)=>{
    if(row.every(value => value !== 0 )){
      rowsToRemove.push(y);
    }
  })
  
  rowsToRemove.forEach(y =>{
    board.splice(y,1)
    const newRow = Array(BOARD_WIDTH).fill(0);
    board.unshift(newRow)

    score+=10;
    scoreElement.innerHTML =score;
    
    totalLineCounter+=1;
    totalLineCounterElement.innerHTML = totalLineCounter;
    lineCounter+=1;
    if(lineCounter===5){
      	level.stage+=1;
      	levelElement.innerHTML= `Level: ${level.stage}`;
      if(level.dropTime>100){
      	level.dropTime-=100;
      	clearInterval(refreshInterval);
      	refreshInterval = setInterval(pieceFall, level.dropTime);
      }
      lineCounter=0;
    }
  })
  rowsToRemove=[];
}

let eventHolder = () => {

	if (!isRunning){
		document.removeEventListener('keydown',handleKeyEvents);
	}else{
		document.addEventListener('keydown',handleKeyEvents)
	}
}

let handleKeyEvents = (Event) =>{

  if (Event.key === 'ArrowLeft'){
    
    piece.position.x--;
    if(checkCollision()){
      piece.position.x++;
    }
  };

  if (Event.key === 'ArrowRight'){
    piece.position.x++;
    if(checkCollision()){
      piece.position.x--;
    }
  }

	if (Event.key === 'ArrowDown'){
	piece.position.y++;
		if(checkCollision()){
		  piece.position.y--;
		  solidifyPiece();
		  removeRows();
		  holdUsedInThisTurn=false;
		}
	}

	if (Event.key === 'ArrowUp'){
		const rotated=[];
		for (let i = 0; i < piece.shape[0].length; i++) {
			const row=[];
				for (let j = piece.shape.length - 1 ; j>=0; j--) {
				  row.push(piece.shape[j][i]);
				}
		rotated.push(row);
		}
		const previousShape= piece.shape;
		piece.shape= rotated;
		//mejorar
		if(checkCollision()){
		  piece.shape=previousShape;
		}
	}

  if (Event.key === 'c'){
  	if(!holdUsedInThisTurn){
	  	holdPiece();
			drawHoldPiece();
		}
  }	

  if (Event.key === ' '){
  		//console.log("Space event");
  		while(!checkCollision()){
  			piece.position.y++;
  		}
			piece.position.y--;
			solidifyPiece();
			removeRows();
			holdUsedInThisTurn=false;

  }	
  update();
}

const update = () => {

	if(checkCollision()){
		piece.position.y--;
		solidifyPiece();
		removeRows();
		holdUsedInThisTurn=false;
	}
	draw();
	drawNextPieces();
};

const pieceFall = ()=> {
	piece.position.y++;
	update();
}

const endGame = () =>{
	
	resultado = score >= 50 ? "positivo" : score >= 20 ? "regular" : "negativo";
	console.log(resultado);
	const modal = document.getElementById("snakeModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
	if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
	document.body.classList.remove("modal-open");
	estaJugando = false;
	audio.pause();
	stompClient.publish({
		destination: "/app/evaluarResultado",
		// ACA DEBO AGREGAR EL RESULTADO PARA PASARLO AL WEBSOCKET
		body: JSON.stringify({ id: mascotaId, resultado: resultado })
	});	

	//window.alert('Game Over!!');
	board.forEach((row)=> row.fill(0))
	score = 0;
	scoreElement.innerHTML = score;
	level.stage = 0;
	levelElement.innerHTML = `Level: ${level.stage}`;
	holdedPiece= [];
	level.dropTime = 800;
	lineCounter = 0;
	totalLineCounterElement.innerHTML=lineCounter;
	isRunning = false;
	startButton.style.setProperty('display', 'inline-block');
	pauseButton.style.setProperty('display', 'none');
	fillRandomizedBag();
	fillNextPieces();
	firstPiece();
	gameCore();
}

const pauseGame = () =>{
	isRunning = false;
	startButton.style.setProperty('display', 'inline-block');
	pauseButton.style.setProperty('display', 'none');
	gameCore();
}

const startGame = () =>{
	isRunning = true;
	levelElement.innerHTML= `Level: ${level.stage}`;
	startButton.style.setProperty('display', 'none');
	pauseButton.style.setProperty('display', 'inline-block');
	gameCore();
}

const gameCore = () => {

	if(isRunning){
		draw();
		drawNextPieces();
		drawHoldPiece();
		refreshInterval = setInterval(pieceFall, level.dropTime);
		eventHolder();
	}else{
		eventHolder();
		clearInterval(refreshInterval);
	}
}
document.getElementById('soundOnOff').addEventListener('click', function(event) {
	this.blur();
		handleSound();
});

const handleSound = () =>{
	if(!playingMusic){
		audio.play();
		playingMusic = true;
	}else{
		audio.pause();
		playingMusic = false;
	}
}

startGame();
pauseButton.style.setProperty('display', 'none');
fillRandomizedBag();
firstPiece();
fillNextPieces();
document.getElementById('cerrar').addEventListener('click', function(event) {
	cerrar();
});

const cerrar = () =>{
	resultado = "negativo"; // SE ADVIERTE EL ABANDONO DEL USUARIO
	console.log(resultado);
	stompClient.publish({
	destination: "/app/evaluarResultado",
		body: JSON.stringify({ id: mascotaId, resultado: resultado })
	});
	estaJugando = false;
	audio.pause();
	const modal = document.getElementById("snakeModal"); // VERIFICAMOS EXISTENCIA DEL MODAL
	if (modal) modal.remove(); // SOLO REMOVER SI EXISTE
	document.body.classList.remove("modal-open");
}

}



