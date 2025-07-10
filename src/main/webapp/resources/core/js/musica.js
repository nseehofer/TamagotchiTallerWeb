
    const audioPlayer = document.getElementById("audio-player");
    const playButton = document.getElementById("play-btn");
    const pauseButton = document.getElementById("pause-btn");

    playButton.addEventListener("click", () => {
    audioPlayer.play().catch(error => console.log("Autoplay bloqueado:", error));
});

    pauseButton.addEventListener("click", () => {
    audioPlayer.pause();
});
