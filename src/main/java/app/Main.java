package app;

import bbdd.ConexionBBDD;
import bbdd.GestionTablas;
import dao.GestionCoches;
import dao.GestionPropietarios;
import dao.GestionTraspasos;
import util.GestionCSV;
import util.GestionInforme;

import java.util.Scanner;

/**
 * Clase principal con el menú de texto para gestionar el concesionario
 * utilizando JDBC con MySQL o SQLite.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        int opcion = -1;

        do {
            mostrarMenu();
            System.out.print("Selecciona una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, introduce un número válido.");
                continue;
            }

            if (opcion >= 2 && opcion <= 12 && !ConexionBBDD.estaConectado()) {
                System.out.println("Error: Primero debes conectar a una base de datos (Opción 1).");
                continue;
            }

            switch (opcion) {
                case 1 -> ConexionBBDD.conectarMenu(scanner);
                case 2 -> GestionTablas.inicializarTablas(ConexionBBDD.getConexion());
                case 3 -> GestionPropietarios.registrarPropietario(ConexionBBDD.getConexion(), scanner);
                case 4 -> GestionCoches.insertarCoche(ConexionBBDD.getConexion(), scanner);
                case 5 -> GestionCSV.importarCSV(ConexionBBDD.getConexion(), scanner);
                case 6 -> GestionCoches.listarCochesConcesionario(ConexionBBDD.getConexion());
                case 7 -> GestionCoches.listarCochesPropietarios(ConexionBBDD.getConexion());
                case 8 -> GestionCoches.modificarCoche(ConexionBBDD.getConexion(), scanner);
                case 9 -> GestionCoches.borrarCoche(ConexionBBDD.getConexion(), scanner);
                case 10 -> GestionTraspasos.realizarTraspaso(ConexionBBDD.getConexion(), scanner);
                case 11 -> GestionCoches.ejecutarProcedimientoAlmacenado(ConexionBBDD.getConexion(), scanner);
                case 12 -> GestionInforme.generarInforme(ConexionBBDD.getConexion());
                case 0 -> {
                    ConexionBBDD.cerrarConexion();
                    System.out.println("Saliendo del programa...");
                }
                default -> System.out.println("Opción no válida.");
            }

        } while (opcion != 0);
    }

    /**
     * Muestra por consola las opciones disponibles del menú.
     */
    private static void mostrarMenu() {
        System.out.println("\n=== GESTOR DEL CONCESIONARIO (JDBC) ===");
        System.out.println("1. Conectar BBDD (SQLite o MySQL)");
        System.out.println("2. Inicializar/Crear Tablas");
        System.out.println("3. Registrar Propietario");
        System.out.println("4. Insertar Coche");
        System.out.println("5. Importar Coches (CSV)");
        System.out.println("6. Listar Coches (Concesionario)");
        System.out.println("7. Listar Coches (Propietarios)");
        System.out.println("8. Modificar Coche");
        System.out.println("9. Borrar Coche");
        System.out.println("10. Realizar Traspaso");
        System.out.println("11. Ejecutar Procedimiento Almacenado (Solo MySQL)");
        System.out.println("12. Generar Informe Resumen");
        System.out.println("0. Salir");
    }
}
