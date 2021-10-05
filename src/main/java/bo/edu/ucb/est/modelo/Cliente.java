package bo.edu.ucb.est.modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String idUsuario;
    private String nombre;
    private String pinSeguridad;
    private List<Cuenta> cuentas;
    
    public Cliente(String idUsuario,String nombre, String pinSeguridad) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.pinSeguridad = pinSeguridad;
        this.cuentas = new ArrayList();
    }

    /**
     * Este metodo permite agregar una cuenta a un cliente existente
     * @param cuenta objeto de tipo Cuenta que se le agrega al cliente.
     */
    public void agregarCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }

    public String getPinSeguridad() {
        return pinSeguridad;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", pinSeguridad='" + pinSeguridad + '\'' +
                ", cuentas=" + cuentas +
                '}';
    }
}
