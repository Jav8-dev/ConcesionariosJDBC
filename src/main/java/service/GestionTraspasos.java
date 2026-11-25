package service;

import utils.ConexionBBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

/**
 * Clase encargada de gestionar los traspasos de vehículos entre el concesionario
 * o propietarios de forma transaccional.
 */
public class GestionTraspasos {

    /**
     * Opción 10 del menú: solicita el DNI del comprador, la matrícula del coche
     * y el monto económico. Verifica que comprador y coche existen, registra el traspaso
     * y actualiza el propietario del coche dentro de una transacción.
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para leer los datos
     */
    public static void realizarTraspaso(Connection conn, Scanner teclado) {
        System.out.println("\n--- REALIZAR TRASPASO ---");
        System.out.print("Introduce el DNI del comprador: ");
        String dniComprador = teclado.nextLine().trim().toUpperCase();

        System.out.print("Introduce la matrícula del coche: ");
        String matricula = teclado.nextLine().trim().toUpperCase();

        Double monto = GestionCoches.leerDecimal(teclado, "Introduce el monto económico: ");
        if (monto == null) {
            return;
        }

        boolean autoCommitOriginal = true;
        try {
            // 1. Verificar que el comprador existe
            Integer idComprador = GestionPropietarios.obtenerIdPorDni(conn, dniComprador);
            if (idComprador == null) {
                System.out.println("Error: No existe ningún propietario registrado con el DNI " + dniComprador);
                return;
            }

            // 2. Verificar que el coche existe y obtener su propietario actual (id_vendedor)
            Integer idVendedor = obtenerPropietarioActualCoche(conn, matricula);
            if (idVendedor != null && idVendedor.equals(-1)) {
                System.out.println("Error: No existe ningún coche con la matrícula " + matricula);
                return;
            }

            if (idVendedor != null && idVendedor.equals(idComprador)) {
                System.out.println("Error: El comprador ya es el propietario actual de este coche.");
                return;
            }

            // 3. Operación transaccional
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String sqlInsertTraspaso = "INSERT INTO traspasos (matricula_coche, id_vendedor, id_comprador, monto_economico) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertTraspaso)) {
                psInsert.setString(1, matricula);
                if (idVendedor == null) {
                    psInsert.setNull(2, Types.INTEGER);
                } else {
                    psInsert.setInt(2, idVendedor);
                }
                psInsert.setInt(3, idComprador);
                psInsert.setDouble(4, monto);
                psInsert.executeUpdate();
            }

            String sqlUpdateCoche = "UPDATE coches SET id_propietario = ? WHERE UPPER(matricula) = UPPER(?)";
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateCoche)) {
                psUpdate.setInt(1, idComprador);
                psUpdate.setString(2, matricula);
                psUpdate.executeUpdate();
            }

            conn.commit();
            System.out.println("Traspaso realizado correctamente. Nuevo propietario DNI: " + dniComprador);

        } catch (SQLException e) {
            ConexionBBDD.hacerRollback(conn);
            System.out.println("Error durante el traspaso (se ha hecho rollback): " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autoCommit: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene el id_propietario actual de un coche.
     *
     * @param conn Conexión activa
     * @param matricula Matrícula del coche
     * @return El id_propietario si tiene dueño, null si pertenece al concesionario,
     *         o -1 si el coche no existe en la tabla.
     * @throws SQLException Si ocurre un error SQL
     */
    private static Integer obtenerPropietarioActualCoche(Connection conn, String matricula) throws SQLException {
        String sql = "SELECT id_propietario FROM coches WHERE UPPER(matricula) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1; // El coche no existe
                }
                int id = rs.getInt("id_propietario");
                return rs.wasNull() ? null : id;
            }
        }
    }
}
