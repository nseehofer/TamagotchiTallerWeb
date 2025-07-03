INSERT INTO Usuario(id, email, password, rol, activo,nombre, pais, provincia)
VALUES
    (null, 'test@unlam.edu.ar', 'test', 'ADMIN', true,'SR administrador','Argentina','Buenos Aires'),
    (null, 'user@unlam.edu.ar', 'user', 'USER', true,'User','Bolivia','Sucre');
INSERT INTO  Mascota(id,energia,estaVivo, estaEnfermo, felicidad,hambre,higiene,nombre,salud,ultimaAlimentacion,ultimaHigiene,ultimaSiesta,usuario_id)
VALUES
    (null,100,true, false, 100,100,100,'Tamagotcha',100,NOW(),NOW(),NOW(),1),
    (null,100,false, false,100,100,100,'Elk Adaver',100,NOW(),NOW(),NOW(),1),
    (null,100,true, false,100,100,100,'Mascota de usuario',100,NOW(),NOW(),NOW(),2);