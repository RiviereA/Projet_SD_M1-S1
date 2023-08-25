package serveurpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnexionClient extends Thread {
    private final String typeClient;
    private final String nomClient;
    private final ServerSocket serveurSocket ;
    private final Socket clientSocket ;
    private final BufferedReader in;
    private final PrintWriter out;
    
    public ConnexionClient(String nom, ServerSocket s, Socket c) throws IOException {
        this.nomClient = nom;
        this.serveurSocket = s;
        this.clientSocket = c;
        this.in = new BufferedReader (new InputStreamReader (this.clientSocket.getInputStream()));
        this.out = new PrintWriter(this.clientSocket.getOutputStream());
        
        // Récupération du type de client
        this.typeClient = this.in.readLine();
    }
    
    @Override
    public void run() {
        String msg ;
        try {
            msg = this.in.readLine();
            // Tant que le client est connecté
            while(msg!=null){
                if(ServeurPC.buffer.size() <= 10) {
                    ServeurPC.buffer.add(this.nomClient + " : " + msg);
                } else {
                    this.out.println("Votre message ne peut être envoyé pour le moment");
                    this.out.flush();
                }
                msg = this.in.readLine();
            }
            // Sortie de la boucle si le client a été déconecté
            System.out.println("Client déconecté");
            // Fermeture du flux et de la session socket
            this.out.close();
            this.clientSocket.close();
            this.serveurSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public String TypeClient() {
        return this.typeClient;
    }
    
    public PrintWriter Out() {
        return this.out;
    }
}
