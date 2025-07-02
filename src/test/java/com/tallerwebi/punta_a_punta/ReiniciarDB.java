package com.tallerwebi.punta_a_punta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReiniciarDB {
    public static void limpiarBaseDeDatos() {
        try {
            String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
            String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
            String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
            String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
            String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

            String sqlCommands = "DELETE FROM Mascota; DELETE FROM Usuario; " +
                "ALTER TABLE Usuario AUTO_INCREMENT = 1; ALTER TABLE Mascota AUTO_INCREMENT = 1; " +
                "INSERT INTO Usuario(id, email, password, rol, activo, nombre, pais, provincia) " +
                "VALUES (null, 'test@unlam.edu.ar', 'test', 'ADMIN', true, 'SR administrador', 'Argentina', 'Buenos Aires'), " +
                "(null, 'user@unlam.edu.ar', 'user', 'USER', true, 'User', 'Bolivia', 'Sucre'); " +
                "INSERT INTO Mascota(id, energia, estaVivo, felicidad, hambre, higiene, nombre, salud, " +
                "ultimaAlimentacion, ultimaHigiene, ultimaSiesta, usuario_id) " +
                "VALUES (null, 100, true, 100, 100, 100, 'Tamagotcha', 100, NOW(), NOW(), NOW(), 1), " +
                "(null, 100, false, 100, 100, 100, 'Elk Adaver', 100, NOW(), NOW(), NOW(), 1), " +
                "(null, 100, true, 100, 100, 100, 'Mascota de usuario', 100, NOW(), NOW(), NOW(), 2);";

            // Construir el comando según si hay contraseña o no
            String comando;
            if (dbPassword.isEmpty()) {
                comando = String.format(
                    "mysql -h %s -P %s -u %s %s -e \"%s\"",
                    dbHost, dbPort, dbUser, dbName, sqlCommands
                );
            } else {
                comando = String.format(
                    "mysql -h %s -P %s -u %s -p\"%s\" %s -e \"%s\"",
                    dbHost, dbPort, dbUser, dbPassword, dbName, sqlCommands
                );
            }

            Process process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", comando });
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Base de datos limpiada exitosamente");
            } else {
                System.err.println("Error al limpiar la base de datos. Exit code: " + exitCode);

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                errorReader.lines().forEach(line -> System.err.println("STDERR: " + line));
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error ejecutando script de limpieza: " + e.getMessage());
            e.printStackTrace();
        }
    }
}