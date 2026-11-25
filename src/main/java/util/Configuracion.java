package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase auxiliar encargada de cargar el fichero config.properties
 * y devolver los parámetros de configuración de la aplicación.
 */
public class Configuracion {

    private static final Properties propiedades = new Properties();

    static {
        try (InputStream entrada = Configuracion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (entrada == null) {
                System.err.println("Error: No se encontró el archivo config.properties en resources.");
            } else {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
        }
    }

    /**
     * Obtiene el valor asociado a una clave en config.properties.
     *
     * @param clave Nombre de la propiedad (ej. "db.mysql.url")
     * @return Valor configurado como texto, o null si no existe
     */
    public static String get(String clave) {
        return propiedades.getProperty(clave);
    }
}
