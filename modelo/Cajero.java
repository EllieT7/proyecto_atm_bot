/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.edu.ucb.est.modelo;

import bo.edu.ucb.est.iu.Pantalla;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ecampohermoso
 */
public class Cajero {
    
    private final Pantalla pantallaError;
    private final Banco banco;
    private Cliente cliente;
    

    public Cajero(Banco banco) {
        this.banco = banco;
        // Las siguientes son pantallas dinamicas, es decir su contenido
        // va a cambiar con el comportamiento del programa
        
        // Iniciamos pantalla de error.
        pantallaError = new Pantalla("Ocurrio un error");        
        // Corremos primera pantalla
        
    }
    
    public void iniciarCajero() {
        boolean salir = false;
        while(!salir) {
            // Primero mostramos la pantalla de ingreso
            Pantalla pantallaIngreso = construirPantallaIngreso();
            List<Object> credenciales = pantallaIngreso.desplegar(); // Obtenemos las credenciales
            
            // Puede retornar pantalla de error o menu de opciones
            Pantalla resultadoValidarCredenciales = controladorValidarCredenciales(credenciales);
            if (resultadoValidarCredenciales.getTitulo().equals("Cajero ATM")) {
                //Se muestra el menu de opciones
                menu(resultadoValidarCredenciales);

            } else {
                // Es error y se muestra el mensaje de error
                resultadoValidarCredenciales.desplegar();
            }

        }
    }
    public void menu(Pantalla pantallaMenu){
        int opcion;
        do{
            List<Object> opcionListado = pantallaMenu.desplegar();
            opcion = (Integer) opcionListado.get(0);
            if (opcion == 1) { // Ver saldo
                verSaldo();
            } else if (opcion == 2){ // Depositar
                operacionBanco(1);
            }else if(opcion ==3){
                operacionBanco(-1);
            }
            opcionListado.clear();
        }while(opcion < 4);
        System.exit(0);
    }

 
    
    /**
     * Este metodo valida las credenciales ingresadas por el usuario, entonces
     * existen opciones.
     *  1. Las credenciales sean válidas.: Retorna la pantalla de menú principal
     *  2. LAs credenciales sean inválidas: Retorna la pantalla de error
     * @param credenciales
     * @return 
     */
    private Pantalla controladorValidarCredenciales(List<Object> credenciales) {
        Pantalla resultado = null;
        cliente = banco.buscarClientePorCodigo( (String) credenciales.get(0), 
                (String) credenciales.get(1));
        if (cliente == null) { // Significa que las credenciales son incorrectas
            List contenido = new ArrayList();
            contenido.add("No se encontró al usuario.");
            pantallaError.setContenido(contenido);
            pantallaError.desplegar();
            resultado = pantallaError;
        } else {
            resultado = construirPantallaPrincipal();
        }
        return resultado;
    }
    
    private Pantalla construirPantallaIngreso() {
        // Inicialización de pantallas y configuración.
        Pantalla pantallaIngreso = new Pantalla("Cajero automático");
        List ingresoContenido = new ArrayList();
        ingresoContenido.add(" Bienvenido al sistema, por favor ingrese su credenciales");
        pantallaIngreso.setContenido(ingresoContenido);
        pantallaIngreso.definirDatoEntrada("Código de usuario: ", "String");
        pantallaIngreso.definirDatoEntrada("PIN: ", "String");
        return pantallaIngreso;
    }
    
    private Pantalla construirPantallaPrincipal() {
        Pantalla pantallaMenuPrincipal  = new Pantalla("Cajero ATM");
        List menuPrincipalContenido = new ArrayList();
        menuPrincipalContenido.add(" Elija una de las siguientes opciones:");
        menuPrincipalContenido.add(" 1. Ver saldo.");
        menuPrincipalContenido.add(" 2. Depositar dinero.");
        menuPrincipalContenido.add(" 3. Retirar dinero.");
        menuPrincipalContenido.add(" 4. Salir");
        menuPrincipalContenido.add(" ");
        pantallaMenuPrincipal.setContenido(menuPrincipalContenido);
        pantallaMenuPrincipal.definirDatoEntrada("Seleccione una opción: ", "Integer");
       return pantallaMenuPrincipal;
    }
    
    private void verSaldo() {
        int indiceCuenta = listaCuentas();
        Cuenta cuenta = cliente.getCuentas().get(indiceCuenta - 1);
        Pantalla pantallaVerSaldo = new Pantalla("Ver saldo");
        List<String> contenidoVerSaldo = new ArrayList();
        contenidoVerSaldo.add("Cliente: " + cliente.getNombre());
        contenidoVerSaldo.add("Nro Cuenta: " + cuenta.getNroCuenta());
        contenidoVerSaldo.add("Saldo: " + cuenta.getMoneda() + " " + cuenta.getSaldo());
        pantallaVerSaldo.setContenido(contenidoVerSaldo);
        pantallaVerSaldo.desplegar();
    }
    private int listaCuentas(){
        List<String> listadoCuentasContenido = new ArrayList<>();
        listadoCuentasContenido.add(" Elija una sus cuentas:");
        for ( int i = 0 ; i < cliente.getCuentas().size() ; i ++ ) {
            Cuenta cuenta = cliente.getCuentas().get(i);
            listadoCuentasContenido.add( (i + 1) + " " + cuenta.getNroCuenta()
                    + " " + cuenta.getTipo());
        }
        Pantalla pantallaListadoCuentas = new Pantalla("Sus cuentas");
        pantallaListadoCuentas.definirDatoEntrada("Seleccione una opción: ", "Integer");
        pantallaListadoCuentas.setContenido(listadoCuentasContenido);
        Integer indiceCuenta;
        do{
            List<Object> datosIntroducidos = pantallaListadoCuentas.desplegar(); // Retorna la cuenta que eligí
            indiceCuenta = (Integer) datosIntroducidos.get(0);
            datosIntroducidos.clear();
        }while(indiceCuenta <=0 || indiceCuenta>cliente.getCuentas().size());
        return indiceCuenta;
    }

    private void operacionBanco(int opcion){
        int indiceCuenta = listaCuentas();
        Cuenta cuenta = cliente.getCuentas().get(indiceCuenta - 1);
        Pantalla pantallaSaldoActual = new Pantalla("Saldo Actual:");
        List<String> contenidoPantallaSaldoActual = new ArrayList<>();
        contenidoPantallaSaldoActual.add(cuenta.getSaldo()+"");
        pantallaSaldoActual.setContenido(contenidoPantallaSaldoActual);
        boolean flag;
        if(opcion == -1){
            pantallaSaldoActual.definirDatoEntrada("Ingrese el monto a retirar: ","Double");
        }else{
            pantallaSaldoActual.definirDatoEntrada("Ingrese el monto a depositar: ", "Double");
        }
        do{
            flag = true;
            List<Object> datosIntroducidos = pantallaSaldoActual.desplegar();
            Double monto = (Double) datosIntroducidos.get(0);
            if (opcion==-1){
                if(!cuenta.retirar(monto)){
                    pantallaError.desplegar();
                    flag = false;
                }
            }else{
                if(!cuenta.depositar(monto)){
                    pantallaError.desplegar();
                    flag = false;
                }
            }
            datosIntroducidos.clear();
        }while(!flag);

        System.out.println("Transacción realizada exitosamente");
    }
    
}
