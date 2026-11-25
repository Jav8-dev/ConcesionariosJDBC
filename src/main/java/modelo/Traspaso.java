package modelo;

/**
 * Representa un traspaso (venta o cambio de propietario) de un coche.
 */
public class Traspaso {

    private int idTraspaso;
    private String matriculaCoche;
    private Integer idVendedor; // null si lo vende el concesionario
    private int idComprador;
    private double montoEconomico;

    /**
     * Constructor vacío.
     */
    public Traspaso() {
    }

    /**
     * Constructor con los datos del traspaso.
     *
     * @param matriculaCoche Matrícula del coche traspasado
     * @param idVendedor ID del propietario vendedor (null si era del concesionario)
     * @param idComprador ID del propietario comprador
     * @param montoEconomico Importe económico de la operación
     */
    public Traspaso(String matriculaCoche, Integer idVendedor, int idComprador, double montoEconomico) {
        this.matriculaCoche = matriculaCoche;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
        this.montoEconomico = montoEconomico;
    }

    public int getIdTraspaso() {
        return idTraspaso;
    }

    public void setIdTraspaso(int idTraspaso) {
        this.idTraspaso = idTraspaso;
    }

    public String getMatriculaCoche() {
        return matriculaCoche;
    }

    public void setMatriculaCoche(String matriculaCoche) {
        this.matriculaCoche = matriculaCoche;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public double getMontoEconomico() {
        return montoEconomico;
    }

    public void setMontoEconomico(double montoEconomico) {
        this.montoEconomico = montoEconomico;
    }

    @Override
    public String toString() {
        return "Traspaso{" +
                "idTraspaso=" + idTraspaso +
                ", matriculaCoche='" + matriculaCoche + '\'' +
                ", idVendedor=" + idVendedor +
                ", idComprador=" + idComprador +
                ", montoEconomico=" + montoEconomico +
                '}';
    }
}
