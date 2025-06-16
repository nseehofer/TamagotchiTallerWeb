INSERT INTO Usuario(id, email, password, rol, activo,nombre)
VALUES
    (null, 'test@unlam.edu.ar', 'test', 'ADMIN', true,'SR administrador'),
    (null, 'user@unlam.edu.ar', 'user', 'USER', true,'User');
INSERT INTO  Mascota(id,energia,estaVivo,felicidad,hambre,higiene,nombre,salud,ultimaAlimentacion,ultimaHigiene,ultimaSiesta,usuario_id)
VALUES
    (null,100,true,100,100,100,'Tamagotcha',100,NOW(),NOW(),NOW(),1),
    (null,100,false,100,100,100,'Elk Adaver',100,NOW(),NOW(),NOW(),1),
    (null,100,true,100,100,100,'Mascota de usuario',100,NOW(),NOW(),NOW(),2);