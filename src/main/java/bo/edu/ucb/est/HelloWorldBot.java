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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloWorldBot extends TelegramLongPollingBot {
    private static final String botUserName = "atm_naomi_bot";
    private static final String token = "2047368272:AAFbG-sb2ByB2Hl13pd0CtaGNAt1db3oVOg";
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
    private String [] mensajesMenu = {"Ver saldo","Retirar dinero","Depositar dinero","Crear cuenta","Salir"};
    private String [] mensajesMenuQuerys={"1","2","3","4","5"};
    Map<Cliente, Integer> listaUsuarios = new HashMap<Cliente, Integer>();
    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("Mensaje recibido: "+update.toString());
        if (update.hasMessage()) {
            Message mensajeEntrante = update.getMessage();
            String idChat = mensajeEntrante.getChatId().toString();
            Cliente clienteActual = banco.obtenerCliente(idChat);
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
                            botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
                            mensaje++;
                        }
                    }else{
                        enviarMensajes(idChat,new String[] {"Por favor elige un PIN de seguridad, este te será requerido cada que ingreses al sistema"});
                        mensaje++;
                    }
                    break;
                case 3:
                    if(clienteActual!=null){
                        opcionMenu = update.getMessage().getText();
                        if(opcionMenu.equals("1")||opcionMenu.equals("2")||opcionMenu.equals("3")) {
                            listaCuentas(clienteActual);
                            mensaje=6;
                        }else if(opcionMenu.equals("4")) {
                            String [] lista = {"Dólares","Bolivianos"};
                            botones("\uD83D\uDCB0 Seleccione la moneda: ",idChat,lista,lista);
                            mensaje=4;
                        }else {
                            botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
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
                    botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
                    mensaje=3;
                    break;
                case 6:
                    int nroCuenta = Integer.parseInt(update.getMessage().getText().toString());
                    Cliente cliente = banco.obtenerCliente(idChat);
                    cuentaSeleccionada = cliente.getCuentas().get(nroCuenta-1);
                    enviarMensajes(idChat,new String[] {"El saldo actual es: "+cuentaSeleccionada.getSaldo()});
                    switch (opcionMenu){
                        case "1":
                            botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
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
                    botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
                    break;
            }
        }
        else if(update.hasCallbackQuery()) {
            String idChat = update.getCallbackQuery().getMessage().getChatId()+"";
            Cliente clienteActual = banco.obtenerCliente(idChat);
            switch (mensaje){
                case 3:
                    opcionMenu = update.getCallbackQuery().getData();
                    if(opcionMenu.equals("1")||opcionMenu.equals("2")||opcionMenu.equals("3")) {
                        listaCuentas(clienteActual);
                        mensaje=6;
                    }else if(opcionMenu.equals("4")) {
                        String [] lista = {"Dólares","Bolivianos"};
                        botones("\uD83D\uDCB0 Seleccione la moneda: ",idChat,lista,lista);
                        mensaje=4;
                    }else {
                        enviarMensajes(idChat,mensajesBienvenidaExisteCliente);
                        mensaje=2;
                    }
                    break;
                case 4:
                    moneda = update.getCallbackQuery().getData();
                    String listaMensajes[] ={"Caja de ahorros","Cuenta corriente"};
                    botones("Seleccione el tipo de cuenta",idChat,listaMensajes,listaMensajes);
                    mensaje=5;
                    break;
                case 5:
                    tipoCuenta = update.getCallbackQuery().getData();
                    String numeroCuenta = numeroDeCuenta+"";
                    banco.obtenerCliente(idChat).agregarCuenta(new Cuenta(moneda,numeroCuenta,tipoCuenta,0));
                    enviarMensajes(idChat,new String[] {"Se le ha creado una cuenta en "+moneda+" con saldo cero, cuyo numero es "+numeroCuenta});
                    numeroDeCuenta++;
                    botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
                    mensaje=3;
                    break;
                case 6:
                    int nroCuenta = Integer.parseInt(update.getCallbackQuery().getData());
                    Cliente cliente = banco.obtenerCliente(idChat);
                    cuentaSeleccionada = cliente.getCuentas().get(nroCuenta-1);
                    enviarMensajes(idChat,new String[] {"El saldo actual es: "+cuentaSeleccionada.getSaldo()});
                    switch (opcionMenu){
                        case "1":
                            botones("Bienvenido "+nombre+"\nElija una opcion",idChat,mensajesMenu,mensajesMenuQuerys);
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

            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void listaCuentas(Cliente cliente){
        ArrayList<String> listaContenido = new ArrayList<>();
        ArrayList<String> listaQuerys = new ArrayList<>();
        for ( int i = 0 ; i < cliente.getCuentas().size() ; i ++ ) {
            Cuenta cuenta = cliente.getCuentas().get(i);
            listaContenido.add((i + 1) + ". Cuenta " + cuenta.getNroCuenta() + "-->" + cuenta.getTipo());
            listaQuerys.add(i+1+"");
        }
        botones("Seleccione una cuenta",cliente.getIdUsuario(),listaContenido.toArray(new String[listaContenido.size()]),listaQuerys.toArray(new String[listaQuerys.size()]));
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
    public void botones(String mensajeInicial,String idChat, String [] mensajes, String [] dataCallBackQuery){
        SendMessage message = new SendMessage();
        message.setChatId(idChat);
        message.setText(mensajeInicial);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        message.setReplyMarkup(markupInline);
        for (int i=0;i<mensajes.length;i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            String datoMensaje = mensajes[i];
            String dataCallBack = dataCallBackQuery[i];
            InlineKeyboardButton boton = new InlineKeyboardButton();
            boton.setText(datoMensaje);
            boton.setCallbackData(dataCallBack);
            rowInline.add(boton);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
