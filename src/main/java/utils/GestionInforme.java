package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase encargada de generar el fichero de informe resumen del concesionario
 * consultando la base de datos.
 */
public class GestionInforme {

    /**
     * Opción 12 del menú: genera el fichero informe_concesionario.txt (según config.properties)
     * con el total de coches, el listado agrupado por marca y el extra más repetido.
     *
     * @param conn Conexión activa a la base de datos
     */
    public static void generarInforme(Connection conn) {
        String rutaInforme = Configuracion.get("ruta.informe");
        if (rutaInforme == null || rutaInforme.isBlank()) {
            rutaInforme = "informe_concesionario.txt";
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaInforme))) {
            pw.println("=========================================");
            pw.println("      INFORME DEL CONCESIONARIO");
            pw.println("=========================================");

            // 1. Número total de coches (COUNT(*))
            int totalCoches = obtenerTotalCoches(conn);
            pw.println("1. Total de coches: " + totalCoches);
            pw.println("-----------------------------------------");

            // 2. Listado de coches agrupados por marca
            pw.println("2. Coches agrupados por marca:");
            escribirCochesPorMarca(conn, pw);
            pw.println("\n-----------------------------------------");

            // 3. Extra o equipamiento que más se repite
            pw.println("3. Extra o equipamiento más repetido:");
            escribirExtraMasRepetido(conn, pw);
            pw.println("=========================================");

            System.out.println("Informe generado correctamente en: " + rutaInforme);
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero de informe: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al consultar los datos para el informe: " + e.getMessage());
        }
    }

    /**
     * Obtiene el número total de coches mediante SELECT COUNT(*).
     *
     * @param conn Conexión activa
     * @return Número total de coches
     * @throws SQLException Si ocurre un error en la consulta
     */
    private static int obtenerTotalCoches(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM coches";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Consulta los coches ordenados por marca y los escribe agrupados en el informe.
     *
     * @param conn Conexión activa
     * @param pw PrintWriter del fichero de salida
     * @throws SQLException Si ocurre un error en la consulta
     */
    private static void escribirCochesPorMarca(Connection conn, PrintWriter pw) throws SQLException {
        String sql = "SELECT marca, matricula, modelo, precio, extras FROM coches ORDER BY marca, matricula";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            String marcaActual = "";
            boolean hayCoches = false;

            while (rs.next()) {
                hayCoches = true;
                String marca = rs.getString("marca");
                if (!marca.equalsIgnoreCase(marcaActual)) {
                    marcaActual = marca;
                    pw.println("\nMarca: " + marcaActual);
                }

                pw.printf("   * Matrícula: %s | Modelo: %s | Precio: %.2f € | Extras: %s%n",
                        rs.getString("matricula"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getString("extras"));
            }

            if (!hayCoches) {
                pw.println("No hay coches registrados.");
            }
        }
    }

    /**
     * Calcula qué extra se repite más veces entre todos los coches y lo escribe en el informe.
     *
     * @param conn Conexión activa
     * @param pw PrintWriter del fichero de salida
     * @throws SQLException Si ocurre un error en la consulta
     */
    private static void escribirExtraMasRepetido(Connection conn, PrintWriter pw) throws SQLException {
        String sql = "SELECT extras FROM coches WHERE extras IS NOT NULL AND extras <> ''";
        Map<String, Integer> contadorExtras = new HashMap<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String extrasTexto = rs.getString("extras");
                if (extrasTexto == null || extrasTexto.isBlank()) {
                    continue;
                }

                // Los extras en el CSV vienen separados por '|'
                String[] listaExtras = extrasTexto.split("\\|");
                for (String extra : listaExtras) {
                    String limpio = extra.trim();
                    if (!limpio.isEmpty()) {
                        contadorExtras.put(limpio, contadorExtras.getOrDefault(limpio, 0) + 1);
                    }
                }
            }
        }

        String extraMasRepetido = null;
        int maxVeces = 0;

        for (Map.Entry<String, Integer> entrada : contadorExtras.entrySet()) {
            if (entrada.getValue() > maxVeces) {
                maxVeces = entrada.getValue();
                extraMasRepetido = entrada.getKey();
            }
        }

        if (extraMasRepetido != null) {
            pw.println("El extra más frecuente es: " + extraMasRepetido + " (" + maxVeces + " veces)");
        } else {
            pw.println("No hay ningún extra registrado.");
        }
    }
}
