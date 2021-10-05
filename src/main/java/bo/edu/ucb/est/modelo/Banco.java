/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ecampohermoso
 */
public class Banco {
    private String nombre;
    private List<Cliente> clientes;
    
    public Banco(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<Cliente>();
    }

    public List<Cliente> getClientes() {
        return this.clientes;
    }
    
    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }
    
    public boolean verificarCuenta(String idUsuario, String pin) {
        for ( int i = 0; i < clientes.size(); i++) {
            Cliente cli = clientes.get(i); // Sacando elemento por elemento
            if (cli.getIdUsuario().equals(idUsuario) && cli.getPinSeguridad().equals(pin)) {
                return true;
            }
        }
        return false; //TODO Cambiar la funcionalidad por Optional para evitar NullPointerException
    }
    public Cliente obtenerCliente(String idUsuario){
        Cliente cliente = null;
        for (Cliente datoCliente:clientes) {
            if (datoCliente.getIdUsuario().equals(idUsuario)) {
                cliente=datoCliente;
                break;
            }
        }
        return cliente;
    }
}
