package bo.edu.ucb.est;

import bo.edu.ucb.est.modelo.Banco;
import bo.edu.ucb.est.modelo.Cliente;
import bo.edu.ucb.est.modelo.Cuenta;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldBot extends TelegramLongPollingBot {
    private int numeroDeCuenta = 100;
    private int mensaje=1;
    private String nombre;
    private String pin;
    private Banco banco = new Banco("De la fortuna");
    private String moneda;
    private String tipoCuenta;
    private String opcionMenu;
    private int opcionRetirar;
    private Cuenta cuentaSeleccionada;
    private String [] mensajesBienvenida = {"Bienvenido al Banco de la Fortuna. 🌿","He notado que aún no eres cliente, procedamos a registrarte","¿Cuál es tu nombre completo?"};
    private String [] mensajesBienvenidaExisteCliente = {"Hola de nuevo","Solo por seguridad, ¿Cuál es tu PIN?"};
    private String [] mensajesMenu = {"Bienvenido","Elige una opcion:","\n1.Ver saldo\n2.Retirar dinero\n3.Depositar dinero\n4.Crear cuenta\n5.Salir"};

    @Override
    public String getBotUsername() {
        return "atm_naomi_bot";
    }

    @Override
    public String getBotToken() {
        return "2047368272:AAFbG-sb2ByB2Hl13pd0CtaGNAt1db3oVOg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message mensajeEntrante = update.getMessage();
        System.out.println(mensajeEntrante.getText()+"\nMensaje recibido: "+update.toString());
        String idChat = mensajeEntrante.getChatId().toString();
        Cliente clienteActual = banco.obtenerCliente(idChat);
        if(update.hasMessage()) { // Verificamos que tenga mensaje
            switch (mensaje){
                case 1:
                    if(clienteActual!=null){
                        enviarMensajes(idChat,mensajesBienvenidaExisteCliente);
                    }else{
                        enviarMensajes(idChat,mensajesBienvenida);
                    }
                    mensaje++;
                    break;
                case 2:
                    nombre = mensajeEntrante.getText();
                    if(clienteActual!=null){
                        String pinExiste=update.getMessage().getText();
                        Cliente cli = banco.buscarClientePorCodigo(idChat,pinExiste);
                        if(cli==null){
                            enviarMensajes(idChat,new String[] {"Lo siento, el codigo es incorecto"});
                            enviarMensajes(idChat,mensajesBienvenidaExisteCliente);
                        }else{
                            enviarMensajes(idChat,mensajesMenu);
                        }
                    }else{
                        enviarMensajes(idChat,new String[] {"Por favor elige un PIN de seguridad, este te será requerido cada que ingreses al sistema"});
                    }
                    mensaje++;
                    break;
                case 3:
                    if(clienteActual!=null){
                        opcionMenu = update.getMessage().getText();
                        if(opcionMenu.equals("1")||opcionMenu.equals("2")||opcionMenu.equals("3")) {
                            enviarMensajes(idChat,new String[] {listaCuentas(banco.obtenerCliente(idChat))});
                            mensaje=6;
                        }else if(opcionMenu.equals("4")) {
                            enviarMensajes(idChat,new String[] {"Seleccione la moneda:\n1. Dolares\n2. Bolivianos"});
                            mensaje=4;
                        }else {
                            enviarMensajes(idChat,mensajesMenu);
                            mensaje=3;
                        }
                    }else{
                        pin = update.getMessage().getText();
                        enviarMensajes(idChat,new String[] {"Genial "+nombre+"\nTe hemos registrado correctamente 🥳"});
                        Cliente cliente = new Cliente(update.getMessage().getChatId().toString(),nombre,pin);
                        banco.agregarCliente(cliente);
                        mensaje=1;
                    }
                    break;
                case 4:
                    try{
                        int tipoMoneda = Integer.parseInt(update.getMessage().getText());
                        if(tipoMoneda==1||tipoMoneda==2){
                            if(tipoMoneda==1){
                                moneda="dolares";
                            }else{
                                moneda="bolivianos";
                            }
                        }
                    }catch(NumberFormatException e){
                        enviarMensajes(idChat,new String[] {"Ingrese la opcion correcta"});
                    }
                    mensaje=5;
                    enviarMensajes(idChat,new String[] {"Seleccione el tipo de cuenta:\n1. Caja de ahorros\n2. Cuenta corriente"});
                    mensaje=5;
                    break;
                case 5:
                    try{
                        int tipo = Integer.parseInt(update.getMessage().getText());
                        if(tipo==1||tipo==2){
                            if(tipo==1){
                                tipoCuenta="Caja de ahorros";
                            }else{
                                tipoCuenta="Cuenta corriente";
                            }
                        }
                    }catch(NumberFormatException e){
                        enviarMensajes(idChat,new String[] {"Ingrese la opcion correcta"});
                    }
                    String numeroCuenta = numeroDeCuenta+"";
                    banco.obtenerCliente(idChat).agregarCuenta(new Cuenta(moneda,numeroCuenta,tipoCuenta,0));
                    enviarMensajes(idChat,new String[] {"Se le ha creado una cuenta en "+moneda+" con saldo cero, cuyo numero es "+numeroCuenta});
                    numeroDeCuenta++;
                    enviarMensajes(idChat,mensajesMenu);
                    mensaje=3;
                    break;
                case 6:
                    int nroCuenta = Integer.parseInt(update.getMessage().getText().toString());
                    Cliente cliente = banco.obtenerCliente(idChat);
                    cuentaSeleccionada = cliente.getCuentas().get(nroCuenta-1);
                    enviarMensajes(idChat,new String[] {"El saldo actual es: "+cuentaSeleccionada.getSaldo()});
                    switch (opcionMenu){
                        case "1":
                            enviarMensajes(idChat,mensajesMenu);
                            mensaje = 3;
                            break;
                        case "2":
                            enviarMensajes(idChat,new String[] {"Ingrese el monto a retirar"});
                            mensaje=7;
                            opcionRetirar = 0;
                            break;
                        case "3":
                            enviarMensajes(idChat,new String[] {"Ingrese el monto a depositar"});
                            mensaje=7;
                            opcionRetirar=1;
                            break;
                    }
                    break;
                case 7:
                    Double monto = Double.parseDouble(update.getMessage().getText().toString());
                    if(opcionRetirar==0){
                        cuentaSeleccionada.retirar(monto);
                    }else if(opcionRetirar==1){
                        cuentaSeleccionada.depositar(monto);
                    }
                    enviarMensajes(idChat,new String[] {"Transaccion realizada correctamente!"});
                    mensaje=3;
                    enviarMensajes(idChat,mensajesMenu);
                    break;
            }
        }
    }

    private String listaCuentas(Cliente cliente){
        String listadoCuentasContenido = "Seleccione una opción:";
        for ( int i = 0 ; i < cliente.getCuentas().size() ; i ++ ) {
            Cuenta cuenta = cliente.getCuentas().get(i);
            listadoCuentasContenido +=( "\n"+(i + 1) + ". Cuenta " + cuenta.getNroCuenta()
                    + "-->" + cuenta.getTipo());
        }
        return listadoCuentasContenido;
    }
    private void enviarMensajes(String idChat, String [] mensajes){
        SendMessage mensajeAEnviar = new SendMessage();
        mensajeAEnviar.setChatId(idChat);
        for (String mensaje : mensajes) {
            mensajeAEnviar.setText(mensaje);
            try {
                execute(mensajeAEnviar);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
