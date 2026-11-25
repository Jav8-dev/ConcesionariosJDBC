package bbdd;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de crear las tablas (DDL) y el procedimiento almacenado en MySQL
 * directamente desde Java.
 */
public class GestionTablas {

    /**
     * Opción 2 del menú: crea las tablas propietarios, coches y traspasos si no existen.
     * Si la conexión es MySQL, también crea el procedimiento almacenado sp_coches_por_marca.
     *
     * @param conn Conexión activa a la base de datos
     */
    public static void inicializarTablas(Connection conn) {
        boolean esMySQL = ConexionBBDD.esMySQL();

        String sqlPropietarios;
        String sqlCoches;
        String sqlTraspasos;

        if (esMySQL) {
            sqlPropietarios = """
                    CREATE TABLE IF NOT EXISTS propietarios (
                        id_propietario INT AUTO_INCREMENT PRIMARY KEY,
                        dni VARCHAR(10) NOT NULL UNIQUE,
                        nombre VARCHAR(100) NOT NULL,
                        apellidos VARCHAR(150) NOT NULL,
                        telefono VARCHAR(15)
                    )
                    """;

            sqlCoches = """
                    CREATE TABLE IF NOT EXISTS coches (
                        matricula VARCHAR(10) PRIMARY KEY,
                        marca VARCHAR(50) NOT NULL,
                        modelo VARCHAR(50) NOT NULL,
                        extras VARCHAR(255),
                        precio DECIMAL(10,2) NOT NULL,
                        id_propietario INT NULL,
                        FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
                    )
                    """;

            sqlTraspasos = """
                    CREATE TABLE IF NOT EXISTS traspasos (
                        id_traspaso INT AUTO_INCREMENT PRIMARY KEY,
                        matricula_coche VARCHAR(10) NOT NULL,
                        id_vendedor INT NULL,
                        id_comprador INT NOT NULL,
                        monto_economico DECIMAL(10,2) NOT NULL,
                        FOREIGN KEY (matricula_coche) REFERENCES coches(matricula) ON DELETE CASCADE,
                        FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),
                        FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
                    )
                    """;
        } else {
            // Sintaxis SQLite
            sqlPropietarios = """
                    CREATE TABLE IF NOT EXISTS propietarios (
                        id_propietario INTEGER PRIMARY KEY AUTOINCREMENT,
                        dni VARCHAR(10) NOT NULL UNIQUE,
                        nombre VARCHAR(100) NOT NULL,
                        apellidos VARCHAR(150) NOT NULL,
                        telefono VARCHAR(15)
                    )
                    """;

            sqlCoches = """
                    CREATE TABLE IF NOT EXISTS coches (
                        matricula VARCHAR(10) PRIMARY KEY,
                        marca VARCHAR(50) NOT NULL,
                        modelo VARCHAR(50) NOT NULL,
                        extras VARCHAR(255),
                        precio DECIMAL(10,2) NOT NULL,
                        id_propietario INTEGER NULL,
                        FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
                    )
                    """;

            sqlTraspasos = """
                    CREATE TABLE IF NOT EXISTS traspasos (
                        id_traspaso INTEGER PRIMARY KEY AUTOINCREMENT,
                        matricula_coche VARCHAR(10) NOT NULL,
                        id_vendedor INTEGER NULL,
                        id_comprador INTEGER NOT NULL,
                        monto_economico DECIMAL(10,2) NOT NULL,
                        FOREIGN KEY (matricula_coche) REFERENCES coches(matricula) ON DELETE CASCADE,
                        FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),
                        FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
                    )
                    """;
        }

        try (Statement st = conn.createStatement()) {
            st.execute(sqlPropietarios);
            st.execute(sqlCoches);
            st.execute(sqlTraspasos);
            System.out.println("Tablas 'propietarios', 'coches' y 'traspasos' inicializadas correctamente.");

            if (esMySQL) {
                crearProcedimientoMySQL(st);
            }
        } catch (SQLException e) {
            System.out.println("Error al inicializar las tablas: " + e.getMessage());
        }
    }

    /**
     * Crea el procedimiento almacenado sp_coches_por_marca en MySQL.
     *
     * @param st Statement abierto sobre la conexión MySQL
     * @throws SQLException Si ocurre un error SQL al crear el procedimiento
     */
    private static void crearProcedimientoMySQL(Statement st) throws SQLException {
        st.execute("DROP PROCEDURE IF EXISTS sp_coches_por_marca");
        String sqlProc = """
                CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50))
                BEGIN
                    SELECT matricula, marca, modelo, extras, precio, id_propietario
                    FROM coches
                    WHERE LOWER(marca) = LOWER(p_marca);
                END
                """;
        st.execute(sqlProc);
        System.out.println("Procedimiento almacenado 'sp_coches_por_marca' creado en MySQL.");
    }
}
