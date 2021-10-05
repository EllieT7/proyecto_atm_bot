/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est.modelo;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author ecampohermoso
 */
public class Cliente {
    private String nombre;
    private String codigoCliente;
    private String pinSeguridad;
    private List<Cuenta> cuentas;
    
    public Cliente(String nombre, String codigoCliente, String pinSeguridad) {
        this.nombre = nombre;
        this.codigoCliente = codigoCliente;
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
    
    public String getNombre() {
        return nombre;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public String getPinSeguridad() {
        return pinSeguridad;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }
}
