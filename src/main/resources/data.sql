INSERT INTO Usuario(id, email, password, rol, activo,nombre, pais, provincia,monedas)
VALUES
    (null, 'test@unlam.edu.ar', '$2a$10$Qfr6YND2AHuFcAWeJyvtDOWC2l1PANiwfEbgaQ64Sj/vnU1wEQMES', 'ADMIN', true,'SR administrador','Argentina','Buenos Aires', 100.00),
    (null, 'user@unlam.edu.ar', '$2a$10$1XP36WR.QWSJwRqI2LYCr.1ETya9bNSUFT6UzqbKkFqGNAguLxOv.', 'USER', true,'User','Bolivia','Sucre', 0.00);
INSERT INTO  Mascota(id,energia,estaVivo, estaEnfermo, felicidad,hambre,higiene,nombre,salud,ultimaAlimentacion,ultimaHigiene,ultimaSiesta,usuario_id,estaAbrigada, estaDormido, estaJugando, tipo)
VALUES
    (null,100,true, false, 100,100,100,'Tamagotcha',100,NOW(),NOW(),NOW(),1,false, false, false,'perro'),
    (null,100,false, false,100,100,100,'Elk Adaver',100,NOW(),NOW(),NOW(),1,false, false, false,'capybara'),
    (null,100,true, false,100,100,100,'Mascota de usuario',100,NOW(),NOW(),NOW(),2,false, false, false,'gato');