package br.com.poli.so.server;

import br.com.poli.so.model.Reserva;
import br.com.poli.so.service.LogService;
import br.com.poli.so.service.ServerService;
import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Requisicao implements Runnable{

    public Requisicao(ServerSocket ss, Integer succes, Semaphore mutex, ArrayList<Reserva> reservas) throws IOException, InterruptedException {

        Socket socket = ss.accept();

        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[2048];

        int lenread = is.read(buffer);
        String req = new String(buffer, 0, lenread);

        String[] lines = req.split("\n");
        String[] lineZero = lines[0].split(" ");

        OutputStream os = socket.getOutputStream();

        File f = new File("arquivos_html" + File.separator + lineZero[1]);

        if (lineZero[1].equals("/")) {
            f = new File("arquivos_html"+ File.separator + "index.html");


        } else if (lineZero[1].startsWith("/solicitar")) {
            f = new File("arquivos_html" + File.separator + "solicitar.html");


        } else if (lineZero[1].startsWith("/finalizar")) {
            mutex.acquire();
            String nome = ServerService.getStringNome(lineZero[1]).replace("+", " ");
            Integer numAssento = ServerService.getIntPoltrona(lineZero[1]);
            String data = ServerService.getDateTime();

            if (!reservas.isEmpty()) {
                if (ServerService.verificarLugares(numAssento, reservas)) {
                    succes = 0;
                    System.out.println("[RESERVADO]");
                } else {
                    System.out.println("[NOVA RESERVA E JA EXISTIA LOG]");
                    Reserva novaReserva = new Reserva(numAssento, true, nome, data);
                    reservas.add(novaReserva);
                    new LogService(socket, req, data);
                    succes = 1;
                }
            } else {
                System.out.println("NOVA RESERVA");
                Reserva novaReserva = new Reserva(numAssento, true, nome, data);
                reservas.add(novaReserva);
                new LogService(socket, req, data);
                succes = 1;
            }

            mutex.release();
            f = new File("arquivos_html" + File.separator + "index.html");


        } else if (lineZero[1].equals("/js/index.js")) {
            String js = ServerService.montaJS(reservas);
            os.write(js.getBytes());

        } else if (lineZero[1].equals("/js/solicitar.js")) {
            String js = ServerService.montaJsSolicitacao(reservas);
            os.write(js.getBytes());

        }
        if (f.exists() && !lineZero[1].equals("/js/index.js") && !lineZero[1].equals("/js/solicitar.js")) {
            FileInputStream fin = new FileInputStream(f);
            String mimeType = Files.probeContentType(f.toPath());

            //monta o head http
            os.write(("HTTP/1.1 200 OK\n" +
                    "Content-Type: " + mimeType + ";charset=UTF-8\n\n").getBytes());

            if (lineZero[1].startsWith("/finalizar")) { //verifica se houve sucesso ou falha na solicitacao do pedido
                switch (succes) {
                    case 1:
                        os.write("<script type='text/javascript'>alert('Seu pedido de reserva foi realizado com sucesso!!')</script>".getBytes(StandardCharsets.UTF_8));
                        break;
                    case 0:
                        os.write("<script type='text/javascript'>alert('ERRO! Não foi possível realizar a reserva, selecione outra poltrona e tente novamente!')</script>".getBytes(StandardCharsets.UTF_8));
                        break;
                    default:
                }
            }
            lenread = fin.read(buffer);
            while (lenread > 0) {
                os.write(buffer, 0, lenread);
                lenread = fin.read(buffer);
            }
        }
        os.flush();
        socket.close();

    }

    @Override
    public void run() {

    }
}
