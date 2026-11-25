package util;

import bbdd.ConexionBBDD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Clase encargada de importar coches desde un fichero CSV de forma transaccional.
 */
public class GestionCSV {

    /**
     * Opción 5 del menú: lee el fichero CSV configurado en config.properties (o uno indicado)
     * e inserta todos los coches en una única transacción (o todos o ninguno).
     *
     * @param conn Conexión activa a la base de datos
     * @param teclado Scanner para confirmar o cambiar la ruta del CSV
     */
    public static void importarCSV(Connection conn, Scanner teclado) {
        System.out.println("\n--- IMPORTAR COCHES DESDE CSV ---");
        String rutaPorDefecto = Configuracion.get("ruta.csv");
        System.out.print("Ruta del fichero CSV [Enter para usar '" + rutaPorDefecto + "']: ");
        String ruta = teclado.nextLine().trim();
        if (ruta.isEmpty()) {
            ruta = rutaPorDefecto;
        }

        String sql = "INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES (?, ?, ?, ?, ?, NULL)";

        boolean autoCommitOriginal = true;
        try {
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Iniciamos la transacción

            int contador = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(ruta));
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                // Saltamos la línea de cabecera
                br.readLine();

                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) {
                        continue;
                    }

                    String[] datos = linea.split(";");
                    if (datos.length < 5) {
                        throw new IOException("Línea con formato incorrecto en el CSV: " + linea);
                    }

                    String matricula = datos[0].trim().toUpperCase();
                    String marca = datos[1].trim();
                    String modelo = datos[2].trim();
                    String extras = datos[3].trim();
                    double precio = Double.parseDouble(datos[4].trim().replace(",", "."));

                    ps.setString(1, matricula);
                    ps.setString(2, marca);
                    ps.setString(3, modelo);
                    ps.setString(4, extras);
                    ps.setDouble(5, precio);

                    ps.executeUpdate();
                    contador++;
                }
            }

            conn.commit();
            System.out.println("Importación completada con éxito. Coches insertados: " + contador);

        } catch (IOException e) {
            ConexionBBDD.hacerRollback(conn);
            System.out.println("Error de lectura del archivo CSV (se ha hecho rollback): " + e.getMessage());
        } catch (NumberFormatException e) {
            ConexionBBDD.hacerRollback(conn);
            System.out.println("Error en el formato numérico del precio en el CSV (se ha hecho rollback): " + e.getMessage());
        } catch (SQLException e) {
            ConexionBBDD.hacerRollback(conn);
            if (ConexionBBDD.esErrorClaveDuplicada(e)) {
                System.out.println("Error: Alguna matrícula del CSV ya existe en la BBDD. Se ha deshecho toda la importación (rollback).");
            } else {
                System.out.println("Error SQL durante la importación (se ha hecho rollback): " + e.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autoCommit: " + e.getMessage());
            }
        }
    }
}
