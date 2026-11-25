package modelo;

/**
 * Representa a un propietario (cliente) del concesionario.
 */
public class Propietario {

    private int idPropietario;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;

    /**
     * Constructor vacío.
     */
    public Propietario() {
    }

    /**
     * Constructor con los datos del propietario.
     *
     * @param dni DNI único del propietario
     * @param nombre Nombre del propietario
     * @param apellidos Apellidos del propietario
     * @param telefono Teléfono de contacto
     */
    public Propietario(String dni, String nombre, String apellidos, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Propietario{" +
                "idPropietario=" + idPropietario +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
