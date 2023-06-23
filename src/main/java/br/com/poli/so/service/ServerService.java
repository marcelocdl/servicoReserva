package br.com.poli.so.service;

import br.com.poli.so.model.Reserva;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ServerService {

    public static boolean verificarLugares(Integer numAssento, ArrayList<Reserva> res){
        for (Reserva reservas : res) {

            if (reservas.getNumPoltrona()==numAssento){
                return true;
            }
        }
        return false;
    }

    public static String getStringNome(String nome){
        String[] finUrl = nome.split("=");
        String[] nomeRet = finUrl[1].split("&");
        return nomeRet[0].toString();
    }
    public static Integer getIntPoltrona(String num){
        String[] finUrl = num.split("=");
        return Integer.parseInt(finUrl[2].toString());
    }

    public static  String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String montaJS(ArrayList<Reserva> reservas){
        String header = getHeader();
        System.out.println("Montando página JS");
        String body = "";

        for (Reserva r : reservas) {
            if(r.isReservado()) {
                body=body+" document.getElementById('" + r.getNumPoltrona() + "').classList.add('ocupado')\n";
                body = body+ " $('#pedido').append('<tr> <th>"+r.getNumPoltrona()+"</th><td>"+r.getNomePessoa()+"</td><td>"+r.getDataReserva()+"</td></tr>')\n";
            }
        }

        return header.concat(body);
    }
    public static String montaJsSolicitacao(ArrayList<Reserva> reservas){
        String header = getHeader();
        System.out.println("Monta js");
        String body = "";

        for (Reserva r : reservas) {
            if(r.isReservado()) {
                body=body+" $('#"+r.getNumPoltrona()+"').remove(); ";
                body=body+" $('.icon"+r.getNumPoltrona()+"').addClass('ocupado'); ";
                System.out.println(r.getNumPoltrona());
            }
        }
        return header.concat(body);
    }
    public static String getHeader(){
        return "HTTP/1.1 200 OK\n Content-Type: text/javascript;charset=UTF-8\n\n";
    }

}
