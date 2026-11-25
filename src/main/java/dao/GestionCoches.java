package dao;

import bbdd.ConexionBBDD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Clase con las operaciones CRUD y consultas sobre la tabla coches,
 * además de la llamada al procedimiento almacenado en MySQL.
 */
public class GestionCoches {

    /**
     * Opción 4 del menú: pide los datos de un coche por consola y lo inserta
     * sin propietario (pertenece inicialmente al concesionario).
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para leer los datos
     */
    public static void insertarCoche(Connection conn, Scanner teclado) {
        System.out.println("\n--- INSERTAR COCHE ---");
        System.out.print("Introduce la matrícula: ");
        String matricula = teclado.nextLine().trim().toUpperCase();

        if (matricula.isEmpty()) {
            System.out.println("Error: La matrícula no puede estar vacía.");
            return;
        }

        System.out.print("Introduce la marca: ");
        String marca = teclado.nextLine().trim();

        System.out.print("Introduce el modelo: ");
        String modelo = teclado.nextLine().trim();

        System.out.print("Introduce los extras separados por '|' (o pulsa Enter si no tiene): ");
        String extras = teclado.nextLine().trim();

        Double precio = leerDecimal(teclado, "Introduce el precio: ");
        if (precio == null) {
            return;
        }

        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ps.setString(2, marca);
            ps.setString(3, modelo);
            ps.setString(4, extras);
            ps.setDouble(5, precio);

            ps.executeUpdate();
            System.out.println("Coche insertado correctamente (sin propietario): " + matricula);
        } catch (SQLException e) {
            if (ConexionBBDD.esErrorClaveDuplicada(e)) {
                System.out.println("Error: Ya existe un coche con la matrícula " + matricula);
            } else {
                System.out.println("Error al insertar el coche: " + e.getMessage());
            }
        }
    }

    /**
     * Opción 6 del menú: muestra por consola todos los coches que NO tienen propietario.
     *
     * @param conn Conexión activa a la base de datos
     */
    public static void listarCochesConcesionario(Connection conn) {
        System.out.println("\n--- COCHES DEL CONCESIONARIO (SIN PROPIETARIO) ---");
        String sql = "SELECT matricula, marca, modelo, extras, precio FROM coches WHERE id_propietario IS NULL";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            boolean hayCoches = false;
            while (rs.next()) {
                hayCoches = true;
                System.out.printf("Matrícula: %s | Marca: %s | Modelo: %s | Extras: %s | Precio: %.2f €%n",
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("extras"),
                        rs.getDouble("precio"));
            }

            if (!hayCoches) {
                System.out.println("No hay coches disponibles en el concesionario.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coches del concesionario: " + e.getMessage());
        }
    }

    /**
     * Opción 7 del menú: muestra por consola todos los coches que SÍ tienen propietario,
     * indicando el DNI del mismo mediante un JOIN.
     *
     * @param conn Conexión activa a la base de datos
     */
    public static void listarCochesPropietarios(Connection conn) {
        System.out.println("\n--- COCHES CON PROPIETARIO ---");
        String sql = """
                SELECT c.matricula, c.marca, c.modelo, c.extras, c.precio, p.dni, p.nombre, p.apellidos
                FROM coches c
                INNER JOIN propietarios p ON c.id_propietario = p.id_propietario
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            boolean hayCoches = false;
            while (rs.next()) {
                hayCoches = true;
                System.out.printf("Matrícula: %s | Marca: %s | Modelo: %s | Precio: %.2f € | Propietario DNI: %s (%s %s)%n",
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"));
            }

            if (!hayCoches) {
                System.out.println("No hay coches asignados a propietarios.");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar coches con propietario: " + e.getMessage());
        }
    }

    /**
     * Opción 8 del menú: solicita una matrícula y actualiza los demás datos del coche.
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para leer los nuevos datos
     */
    public static void modificarCoche(Connection conn, Scanner teclado) {
        System.out.println("\n--- MODIFICAR COCHE ---");
        System.out.print("Introduce la matrícula del coche a modificar: ");
        String matricula = teclado.nextLine().trim().toUpperCase();

        try {
            if (!existeCoche(conn, matricula)) {
                System.out.println("No se encontró ningún coche con la matrícula " + matricula);
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el coche: " + e.getMessage());
            return;
        }

        System.out.print("Nueva marca: ");
        String marca = teclado.nextLine().trim();

        System.out.print("Nuevo modelo: ");
        String modelo = teclado.nextLine().trim();

        System.out.print("Nuevos extras (separados por '|'): ");
        String extras = teclado.nextLine().trim();

        Double precio = leerDecimal(teclado, "Nuevo precio: ");
        if (precio == null) {
            return;
        }

        String sql = "UPDATE coches SET marca = ?, modelo = ?, extras = ?, precio = ? WHERE UPPER(matricula) = UPPER(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, marca);
            ps.setString(2, modelo);
            ps.setString(3, extras);
            ps.setDouble(4, precio);
            ps.setString(5, matricula);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Coche modificado correctamente.");
            } else {
                System.out.println("No se pudo modificar el coche.");
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar el coche: " + e.getMessage());
        }
    }

    /**
     * Opción 9 del menú: solicita una matrícula y elimina el coche de la base de datos.
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para leer la matrícula
     */
    public static void borrarCoche(Connection conn, Scanner teclado) {
        System.out.println("\n--- BORRAR COCHE ---");
        System.out.print("Introduce la matrícula del coche a borrar: ");
        String matricula = teclado.nextLine().trim().toUpperCase();

        String sql = "DELETE FROM coches WHERE UPPER(matricula) = UPPER(?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Coche borrado correctamente: " + matricula);
            } else {
                System.out.println("No existe ningún coche con la matrícula " + matricula);
            }
        } catch (SQLException e) {
            System.out.println("Error al borrar el coche: " + e.getMessage());
        }
    }

    /**
     * Opción 11 del menú: ejecuta el procedimiento almacenado sp_coches_por_marca (solo MySQL).
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para pedir la marca a consultar
     */
    public static void ejecutarProcedimientoAlmacenado(Connection conn, Scanner teclado) {
        if (!ConexionBBDD.esMySQL()) {
            System.out.println("Esta opción solo está disponible cuando estás conectado a MySQL (SQLite no soporta procedimientos almacenados).");
            return;
        }

        System.out.println("\n--- PROCEDIMIENTO ALMACENADO: COCHES POR MARCA ---");
        System.out.print("Introduce la marca a buscar: ");
        String marca = teclado.nextLine().trim();

        String sql = "{call sp_coches_por_marca(?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, marca);

            try (ResultSet rs = cs.executeQuery()) {
                boolean hayResultados = false;
                while (rs.next()) {
                    hayResultados = true;
                    System.out.printf("Matrícula: %s | Marca: %s | Modelo: %s | Extras: %s | Precio: %.2f €%n",
                            rs.getString("matricula"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getString("extras"),
                            rs.getDouble("precio"));
                }
                if (!hayResultados) {
                    System.out.println("No se encontraron coches de la marca: " + marca);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar el procedimiento almacenado (asegúrate de haber inicializado las tablas en MySQL): " + e.getMessage());
        }
    }

    /**
     * Comprueba si existe un coche con la matrícula indicada.
     *
     * @param conn Conexión activa
     * @param matricula Matrícula a comprobar
     * @return true si existe, false si no
     * @throws SQLException Si ocurre un error en la consulta
     */
    public static boolean existeCoche(Connection conn, String matricula) throws SQLException {
        String sql = "SELECT 1 FROM coches WHERE UPPER(matricula) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Método auxiliar para leer un número decimal desde consola controlando NumberFormatException.
     *
     * @param teclado Scanner para leer la entrada
     * @param mensaje Mensaje que se muestra al usuario
     * @return El número leído, o null si el formato no es válido
     */
    public static Double leerDecimal(Scanner teclado, String mensaje) {
        System.out.print(mensaje);
        String texto = teclado.nextLine().trim().replace(",", ".");
        try {
            double valor = Double.parseDouble(texto);
            if (valor < 0) {
                System.out.println("Error: El importe no puede ser negativo.");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Error: Debes introducir un número decimal válido.");
            return null;
        }
    }
}
