package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Clase con las operaciones relacionadas con la tabla propietarios.
 */
public class GestionPropietarios {

    /**
     * Opción 3 del menú: solicita los datos de un nuevo propietario por consola
     * y lo da de alta en la base de datos.
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para leer los datos por consola
     */
    public static void registrarPropietario(Connection conn, Scanner teclado) {
        System.out.println("\n--- REGISTRAR PROPIETARIO ---");
        System.out.print("Introduce el DNI: ");
        String dni = teclado.nextLine().trim().toUpperCase();

        System.out.print("Introduce el nombre: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Introduce los apellidos: ");
        String apellidos = teclado.nextLine().trim();

        System.out.print("Introduce el teléfono (opcional): ");
        String telefono = teclado.nextLine().trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
            System.out.println("Error: DNI, nombre y apellidos son obligatorios.");
            return;
        }

        String sql = "INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, nombre);
            ps.setString(3, apellidos);
            if (telefono.isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, telefono);
            }

            ps.executeUpdate();
            System.out.println("Propietario registrado correctamente con DNI: " + dni);
        } catch (SQLException e) {
            if (esErrorClaveDuplicada(e)) {
                System.out.println("Error: Ya existe un propietario registrado con el DNI " + dni);
            } else {
                System.out.println("Error al registrar propietario: " + e.getMessage());
            }
        }
    }

    /**
     * Busca el id_propietario a partir de su DNI.
     *
     * @param conn Conexión activa
     * @param dni DNI del propietario a buscar
     * @return El id_propietario si existe, o null si no se encuentra
     * @throws SQLException Si ocurre un error en la consulta
     */
    public static Integer obtenerIdPorDni(Connection conn, String dni) throws SQLException {
        String sql = "SELECT id_propietario FROM propietarios WHERE UPPER(dni) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_propietario");
                }
            }
        }
        return null;
    }

    /**
     * Método auxiliar para detectar si una SQLException se debe a una clave única/primaria duplicada
     * tanto en MySQL como en SQLite.
     *
     * @param e Excepción SQL capturada
     * @return true si es un error de duplicado o restricción UNIQUE/PRIMARY KEY
     */
    public static boolean esErrorClaveDuplicada(SQLException e) {
        String sqlState = e.getSQLState();
        String mensaje = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        return "23000".equals(sqlState) || mensaje.contains("unique") || mensaje.contains("duplicate") || mensaje.contains("primary key");
    }
}
