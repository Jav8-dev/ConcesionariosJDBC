package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Gestiona la conexión a la base de datos (MySQL o SQLite) leyendo los datos de config.properties.
 */
public class ConexionBBDD {

    private static Connection conexionActual = null;
    private static String tipoActual = ""; // "MYSQL" o "SQLITE"

    /**
     * Opción 1 del menú: permite al usuario elegir entre SQLite o MySQL,
     * establece la conexión y comprueba que funciona correctamente.
     *
     * @param teclado Scanner para leer la elección del usuario
     */
    public static void conectarMenu(Scanner teclado) {
        System.out.println("\n--- CONECTAR A BASE DE DATOS ---");
        System.out.println("1. SQLite (embebida)");
        System.out.println("2. MySQL (servidor)");
        System.out.print("Elige el gestor (1 o 2): ");

        int opcion;
        try {
            opcion = Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Debes introducir 1 o 2.");
            return;
        }

        if (opcion == 1) {
            conectarSQLite();
        } else if (opcion == 2) {
            conectarMySQL();
        } else {
            System.out.println("Opción no válida.");
        }
    }

    /**
     * Conecta con la base de datos embebida SQLite usando config.properties.
     *
     * @return true si la conexión se realizó con éxito
     */
    public static boolean conectarSQLite() {
        cerrarConexion();
        String driver = Configuracion.get("db.sqlite.driver");
        String url = Configuracion.get("db.sqlite.url");

        try {
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
            conexionActual = DriverManager.getConnection(url);

            // Activamos las claves foráneas en SQLite
            try (Statement st = conexionActual.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }

            if (conexionActual.isValid(3)) {
                tipoActual = "SQLITE";
                System.out.println("Conexión con SQLite establecida y probada correctamente.");
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de SQLite: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar con SQLite: " + e.getMessage());
        }
        return false;
    }

    /**
     * Conecta con el servidor MySQL usando config.properties.
     *
     * @return true si la conexión se realizó con éxito
     */
    public static boolean conectarMySQL() {
        cerrarConexion();
        String driver = Configuracion.get("db.mysql.driver");
        String url = Configuracion.get("db.mysql.url");
        String user = Configuracion.get("db.mysql.user");
        String password = Configuracion.get("db.mysql.password");

        try {
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
            conexionActual = DriverManager.getConnection(url, user, password);

            if (conexionActual.isValid(3)) {
                tipoActual = "MYSQL";
                System.out.println("Conexión con MySQL establecida y probada correctamente.");
                return true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar con MySQL: " + e.getMessage());
        }
        return false;
    }

    /**
     * Devuelve la conexión activa o null si no se ha conectado todavía.
     *
     * @return Objeto Connection activo
     */
    public static Connection getConexion() {
        return conexionActual;
    }

    /**
     * Indica si hay una conexión abierta y válida.
     *
     * @return true si está conectado, false en caso contrario
     */
    public static boolean estaConectado() {
        try {
            return conexionActual != null && !conexionActual.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Indica si la conexión actual es MySQL.
     *
     * @return true si es MySQL, false si es SQLite o no hay conexión
     */
    public static boolean esMySQL() {
        return "MYSQL".equals(tipoActual);
    }

    /**
     * Cierra la conexión actual si está abierta.
     */
    public static void cerrarConexion() {
        if (conexionActual != null) {
            try {
                if (!conexionActual.isClosed()) {
                    conexionActual.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                conexionActual = null;
                tipoActual = "";
            }
        }
    }
}
