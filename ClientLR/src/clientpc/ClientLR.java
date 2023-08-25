package clientpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientLR {

    public static void main(String[] args) {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in); //pour lire les saisies clavier
        final String type;

        try {
            // Connexion au serveur local ou distant selon l'IP fournie sur le port 5000
            clientSocket = new Socket("127.0.0.1",5000);

            // flux de sortie
            out = new PrintWriter(clientSocket.getOutputStream());
            // flux d'entrée
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            // On commence par demander à l'utilisateur de de définir si le client est un Lecteur(L) ou un Rédacteur(R)
            String s = "";
            while(!s.equals("L") && !s.equals("R")) {
                s = sc.nextLine();
            }
            // transmisssion du type de client au serveur
            type = s;
            out.println(s);
            out.flush();
            
            // Création de Threads pour l'exécution de tâches en parallèle
            
            // Seul les Rédacteurs peuvent envoyer un message au serveur
            if (type.equals("R")) {
                Thread envoyer = new Thread(new Runnable() {
                    String msg;
                    @Override
                    public void run() {
                        msg = sc.nextLine();
                        out.println(msg);
                        out.flush();
                    }
                });
                envoyer.start();
            }
            
            // Seul les Lecteurs peuvent recevoir des messages
            if (type.equals("L")) {
                Thread recevoir = new Thread(new Runnable() {
                    String msg;
                    @Override
                    public void run() {
                        try {
                            msg = in.readLine();
                            while(msg!=null){
                                System.out.println(msg);
                                msg = in.readLine();
                            }
                            System.out.println("Serveur déconnecté");
                            out.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
                recevoir.start();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
