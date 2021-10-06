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

public class AtmBot extends TelegramLongPollingBot {
    private int numeroDeCuenta = 100;
    private final Banco banco = new Banco("De la fortuna");
    private final String[] mensajesBienvenida = {"🤑", "Bienvenido al Banco de la Fortuna \uD83C\uDF40", "He notado que aún no eres cliente, procedamos a registrarte", "¿Cuál es tu nombre completo?"};
    private final String[] mensajesBienvenidaExisteCliente = {"Hola de nuevo 😊 ", "Solo por seguridad, ¿Cuál es tu PIN?", "\uD83D\uDD10"};
    private final String[] monedas = {"Dólares", "Bolivianos"};
    private final String[] tipoCuentas = {"Caja de ahorros", "Cuenta corriente"};
    Map<Long, Integer> listaUsuarios = new HashMap<>();
    Map<Long, String[]> listaOpcionesSeleccionadas = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Mensaje recibido: " + update.toString());
        if (update.hasMessage()) {
            Message mensajeEntrante = update.getMessage();
            String idChat = mensajeEntrante.getChatId().toString();
            Long idChatLong = mensajeEntrante.getChatId();
            Cliente clienteActual = banco.obtenerCliente(idChat);
            listaUsuarios.putIfAbsent(idChatLong, 1);
            int mensaje = listaUsuarios.get(idChatLong);
            String nombreEstablecido;
            switch (mensaje) {
                case 1:
                    if (clienteActual != null) {
                        enviarMensajes(idChat, mensajesBienvenidaExisteCliente);
                    } else {
                        enviarMensajes(idChat, mensajesBienvenida);
                    }
                    listaUsuarios.put(idChatLong, 2);
                    break;
                case 2:
                    String mensaje2 = mensajeEntrante.getText();
                    if (clienteActual != null) {
                        if (!banco.verificarCuenta(idChat, mensaje2)) {
                            enviarMensajes(idChat, new String[]{"Lo siento, el pin es incorrecto", "😯"});
                            enviarMensajes(idChat, mensajesBienvenidaExisteCliente);
                        } else {
                            menu(idChat);
                            listaUsuarios.put(idChatLong, 3);
                        }
                    } else {
                        listaOpcionesSeleccionadas.put(idChatLong, new String[]{mensaje2});
                        enviarMensajes(idChat, new String[]{"Por favor elige un PIN de seguridad 🔑 este te será requerido cada que ingreses al sistema"});
                        listaUsuarios.put(idChatLong, 3);
                    }
                    break;
                case 3:
                    nombreEstablecido = listaOpcionesSeleccionadas.get(idChatLong)[0];
                    if (clienteActual != null) {
                        mensajePresionarBoton(idChat);
                        menu(idChat);
                    } else {
                        String pin = mensajeEntrante.getText();
                        Cliente cliente = new Cliente(idChat, nombreEstablecido, pin);
                        banco.agregarCliente(cliente);
                        enviarMensajes(idChat, new String[]{"Genial " + cliente.getNombre() + "\nTe hemos registrado correctamente", "🤩"});
                        enviarMensajes(idChat, mensajesBienvenidaExisteCliente);
                        listaUsuarios.put(idChatLong, 2);
                    }
                    break;
                case 4:
                    mensajePresionarBoton(idChat);
                    botones("\uD83D\uDCB0 Seleccione la moneda: ", idChat, monedas, monedas);
                    break;
                case 5:
                    mensajePresionarBoton(idChat);
                    botones("\uD83E\uDDEE Seleccione el tipo de cuenta", idChat, tipoCuentas, tipoCuentas);
                    break;
                case 6:
                    mensajePresionarBoton(idChat);
                    listaCuentas(clienteActual);
                    break;
                case 7:
                    try {
                        int flag = 0;
                        double monto = Double.parseDouble(update.getMessage().getText());
                        Cuenta cuentaSeleccionada = banco.obtenerCliente(idChat).getCuentas().get(Integer.parseInt(listaOpcionesSeleccionadas.get(idChatLong)[0]));
                        int opcion = Integer.parseInt(listaOpcionesSeleccionadas.get(idChatLong)[1]);
                        if (opcion == 0) {
                            if (!cuentaSeleccionada.retirar(monto)) {
                                if (cuentaSeleccionada.getSaldo() == 0) {
                                    enviarMensajes(idChat, new String[]{"Su cuenta no tiene saldo", "Intente con otra cuenta", "😓"});
                                    menu(idChat);
                                    listaUsuarios.put(idChatLong, 3);
                                } else {
                                    enviarMensajes(idChat, new String[]{"El monto no debe exceder su saldo, ni ser negativo", "Intente nuevamente", "😔"});
                                }
                                flag = 1;
                            }
                        } else if (opcion == 1) {
                            if (!cuentaSeleccionada.depositar(monto)) {
                                enviarMensajes(idChat, new String[]{"El monto no debe ser negativo", "Intente nuevamente", "😔"});
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            enviarMensajes(idChat, new String[]{"Transacción realizada correctamente!", "🥳"});
                            listaUsuarios.put(idChatLong, 3);
                            menu(idChat);
                        }
                    } catch (Exception e) {
                        enviarMensajes(idChat, new String[]{"Ingrese el monto correctamente", "😡"});
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            Long idChatLong = update.getCallbackQuery().getMessage().getChatId();
            String idChat = idChatLong + "";
            listaUsuarios.putIfAbsent(idChatLong, 1);
            int mensaje = listaUsuarios.get(idChatLong);
            Cliente clienteActual = banco.obtenerCliente(idChat);
            switch (mensaje) {
                case 3:
                    String opcionMenu = update.getCallbackQuery().getData();
                    listaOpcionesSeleccionadas.put(idChatLong, new String[]{opcionMenu});
                    if (opcionMenu.equals("1") || opcionMenu.equals("2") || opcionMenu.equals("3")) {
                        if (listaCuentas(clienteActual)) {
                            listaUsuarios.put(idChatLong, 6);
                        }
                    } else if (opcionMenu.equals("4")) {
                        botones("\uD83D\uDCB0 Seleccione la moneda: ", idChat, monedas, monedas);
                        listaUsuarios.put(idChatLong, 4);
                    } else if(opcionMenu.equals("5")){
                        enviarMensajes(idChat,new String[] {"Adiós "+clienteActual.getNombre()+" 👋","La próxima vez que quieras hablar conmigo presiona /start o solo manda un mensaje","😉"});
                        listaUsuarios.put(idChatLong,1);
                    }else{
                        enviarMensajes(idChat, mensajesBienvenidaExisteCliente);//menu
                        listaUsuarios.put(idChatLong, 2);
                    }
                    break;
                case 4:
                    String moneda = update.getCallbackQuery().getData();
                    listaOpcionesSeleccionadas.put(idChatLong, new String[]{moneda});
                    botones("\uD83E\uDDEE Seleccione el tipo de cuenta", idChat, tipoCuentas, tipoCuentas);
                    listaUsuarios.put(idChatLong, 5);
                    break;
                case 5:
                    String monedaElegida = listaOpcionesSeleccionadas.get(idChatLong)[0];
                    String tipoCuenta = update.getCallbackQuery().getData();
                    String numeroCuenta = numeroDeCuenta + "";
                    banco.obtenerCliente(idChat).agregarCuenta(new Cuenta(monedaElegida, numeroCuenta, tipoCuenta, 0));
                    enviarMensajes(idChat, new String[]{"Se le ha creado una cuenta " + "(" + tipoCuenta + ") " + "en " + monedaElegida +
                            " con saldo cero, cuyo numero es " + numeroCuenta, "🥳"});
                    numeroDeCuenta++;
                    menu(idChat);
                    listaUsuarios.put(idChatLong, 3);
                    break;
                case 6:
                    int nroCuenta = Integer.parseInt(update.getCallbackQuery().getData());
                    Cliente cliente = banco.obtenerCliente(idChat);
                    Cuenta cuentaSeleccionada = cliente.getCuentas().get(nroCuenta - 1);
                    String tipoMoneda = " $";
                    if (cuentaSeleccionada.getMoneda().equals("Bolivianos")) {
                        tipoMoneda = " Bs";
                    }
                    enviarMensajes(idChat, new String[]{"El saldo actual es " + cuentaSeleccionada.getSaldo() + " " + tipoMoneda});
                    int opcionRetirar = 0;
                    String opcionMenu1 = listaOpcionesSeleccionadas.get(idChatLong)[0];
                    switch (opcionMenu1) {
                        case "1":
                            menu(idChat);
                            listaUsuarios.put(idChatLong, 3);
                            break;
                        case "2":
                            enviarMensajes(idChat, new String[]{"Ingrese el monto a retirar"});
                            listaUsuarios.put(idChatLong, 7);
                            break;
                        case "3":
                            enviarMensajes(idChat, new String[]{"Ingrese el monto a depositar"});
                            listaUsuarios.put(idChatLong, 7);
                            opcionRetirar = 1;
                            break;
                    }
                    listaOpcionesSeleccionadas.put(idChatLong, new String[]{nroCuenta - 1 + "", opcionRetirar + ""});
                    break;
                default:
                    enviarMensajes(idChat, new String[]{"Ingrese los datos correctamente"});
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "atm_naomi_bot";
    }

    @Override
    public String getBotToken() {
        return "2047368272:AAFbG-sb2ByB2Hl13pd0CtaGNAt1db3oVOg";
    }

    private void menu(String idChat) {
        String[] mensajesMenu = {"\uD83E\uDDFE Ver saldo", "\uD83D\uDCB3 Retirar dinero", "\uD83D\uDCB8 Depositar dinero", "➕ Crear cuenta", "\uD83D\uDEAA Salir"};
        String[] mensajesMenuQuery = {"1", "2", "3", "4", "5"};
        botones("Bienvenid@ " + banco.obtenerCliente(idChat).getNombre() + "🤠\nElija una opcion", idChat, mensajesMenu, mensajesMenuQuery);
    }

    private void mensajePresionarBoton(String idChat){
        enviarMensajes(idChat, new String[]{"Presione una de las opciones", "😉"});
    }

    private boolean listaCuentas(Cliente cliente) {
        boolean flag = false;
        if (cliente.getCuentas().size() == 0) {
            enviarMensajes(cliente.getIdUsuario(), new String[]{"Usted no tiene cuentas registradas, cree una primero :)"});
            menu(cliente.getIdUsuario());
        } else {
            ArrayList<String> listaContenido = new ArrayList<>();
            ArrayList<String> listaQueries = new ArrayList<>();
            for (int i = 0; i < cliente.getCuentas().size(); i++) {
                Cuenta cuenta = cliente.getCuentas().get(i);
                listaContenido.add((i + 1) + ". Cuenta " + cuenta.getNroCuenta() + "-->" + cuenta.getTipo());
                listaQueries.add(i + 1 + "");
            }
            botones("Seleccione una cuenta", cliente.getIdUsuario(), listaContenido.toArray(new String[0]), listaQueries.toArray(new String[0]));
            flag = true;
        }
        return flag;
    }

    private void enviarMensajes(String idChat, String[] mensajes) {
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

    public void botones(String mensajeInicial, String idChat, String[] mensajes, String[] dataCallBackQuery) {
        SendMessage message = new SendMessage();
        message.setChatId(idChat);
        message.setText(mensajeInicial);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        message.setReplyMarkup(markupInline);
        for (int i = 0; i < mensajes.length; i++) {
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
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
