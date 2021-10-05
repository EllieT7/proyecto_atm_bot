/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est.modelo;

/**
 *
 * @author ecampohermoso
 */
public class Cuenta {
    private String moneda;
    private String tipo;
    private String nroCuenta;
    private double saldo;

    public Cuenta(String moneda, String nroCuenta, String tipo, double saldoInicial) {
        this.moneda = moneda;
        this.tipo = tipo;
        this.saldo = saldoInicial;
        this.nroCuenta = nroCuenta;
    }

    public boolean retirar(double monto) {
        boolean resultado = false;
        if (monto > 0 && monto <= saldo) { // verifica que no sea negativo, cero o exceda su saldo
            saldo = saldo - monto;
            resultado = true; // si he podido retirar
        }
        return resultado;
    }
    
    public boolean depositar(double monto) {
        boolean resultado = false;
        if (monto > 0 ) { // verifica que no sea negativo, cero o exceda su saldo
            saldo = saldo + monto;
            resultado = true; // si he podido retirar
        }
        return resultado;
    }
            
    public String getMoneda() {
        return moneda;
    }

    public String getTipo() {
        return tipo;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

}
