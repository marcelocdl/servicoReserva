package br.com.poli.so.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class LogService {

    private static int tamBuf = 1200;
    private static byte[] logBuffer = new byte[tamBuf];
    private Socket Socket;
    private InetAddress ipReserva;
    Semaphore vazio = new Semaphore(tamBuf);
    Semaphore cheio = new Semaphore(0);
    Semaphore mutex = new Semaphore(1);
    private static int iStatic;
    private static File file = new File("./logs/log.txt");
    private String requisicao;
    private String dataHora;

    public LogService(Socket socket, String requisicao, String dataHora) {
        this.dataHora = dataHora;
        this.requisicao = requisicao;
        Socket = socket;

        try {
            if (file.createNewFile()) {
                System.out.println("Arquivo de LOG criado: " + file.getName());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new Thread(new LogService.ProduzLog()).start();
        new Thread(new LogService.ArmazenaLog()).start();

    }

    private class ProduzLog implements Runnable {

        @Override
        public void run() {

            ipReserva = Socket.getInetAddress();
            String ipString = ipReserva.toString();
            String[] endereco = requisicao.split("Host");
            String[] parts = requisicao.split("\\?|&");
            String nome = ServerService.getStringNome(requisicao).replace("+", " ");
            String poltrona = "";

            for(String part : parts){
                if (part.contains("num=")) {
                    poltrona = part.substring(4, 6);
                    break;
                }
            }
            String logString = "\n Nome da Reserva: "+ nome +
                    "\n Poltrona: "+ poltrona +
                    "\n Ip:"  + ipString +
                    "\n Host: "+endereco[1].substring(1, 16)+
                    "\n Método: " + endereco[0] +
                    " Data e hora: "+  dataHora +
                    "\n ********** \n";

            byte[] logByte = logString.getBytes();
            try {
                vazio.acquire(logByte.length);
                mutex.acquire();
            } catch (InterruptedException e) {
            }
            System.out.println("** LOG PRODUZIDO **");
            for (int i = 0; i < logByte.length; i++) {
                logBuffer[iStatic] = logByte[i];
                iStatic++;
            }
            mutex.release();
            cheio.release();

        }

    }

    private class ArmazenaLog implements Runnable {

        @Override
        public void run() {

            try {
                cheio.acquire();
                mutex.acquire();
            } catch (InterruptedException e) {
                System.out.printf("Error -> "+ e.getMessage());
            }

            String logString = new String(logBuffer, 0, iStatic);
            int tamanhoUsado = iStatic;
            System.out.println("Tamanho buffer -> "+iStatic);
            iStatic = 0;
            logBuffer = new byte[tamBuf];

            System.out.println("** LOG CONSUMIDO E ARMAZENADO! **");
            mutex.release();
            vazio.release(tamanhoUsado);

            try {
                FileWriter myWriter = new FileWriter("./logs/log.txt", true);
                myWriter.write(logString);
                myWriter.close();
            } catch (IOException e) {
                System.out.println("Erro ao salvar dados -> "+e.getMessage());
            }


        }

    }
}