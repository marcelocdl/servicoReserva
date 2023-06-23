package br.com.poli.so.server;

import br.com.poli.so.model.Reserva;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Servidor {

    public static Semaphore mutex = new Semaphore(1);
    public static ArrayList<Reserva> reservas = new ArrayList<>();
    public static int succes = 2;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080, 0, InetAddress.getByName("0.0.0.0"));
        System.out.println("SERVIDOR INICIADO NA PORTA 8080");

        while (true) {
            try {

                new Thread(new Requisicao(serverSocket,succes,mutex,reservas)).start();
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}
