package modelo;

/**
 * Representa un coche gestionado por el concesionario.
 */
public class Coche {

    private String matricula;
    private String marca;
    private String modelo;
    private String extras;
    private double precio;
    private Integer idPropietario; // null si pertenece al concesionario

    /**
     * Constructor vacío.
     */
    public Coche() {
    }

    /**
     * Constructor con los datos principales del coche.
     *
     * @param matricula Matrícula única del vehículo
     * @param marca Marca del vehículo
     * @param modelo Modelo del vehículo
     * @param extras Extras o equipamiento separados por '|'
     * @param precio Precio del vehículo
     * @param idPropietario ID del propietario o null si es del concesionario
     */
    public Coche(String matricula, String marca, String modelo, String extras, double precio, Integer idPropietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Integer getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Integer idPropietario) {
        this.idPropietario = idPropietario;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras='" + extras + '\'' +
                ", precio=" + precio +
                ", idPropietario=" + idPropietario +
                '}';
    }
}
