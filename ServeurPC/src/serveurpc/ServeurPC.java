package serveurpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServeurPC {
    public static volatile ArrayList<String> buffer = new ArrayList();
    
    public static void main(String[] args) {
        final Scanner sc=new Scanner(System.in);
        final ServerSocket serveurSocket;
        Socket clientSocket ;
        ArrayList<ConnexionClient> listeProducteurs = new ArrayList();
        ArrayList<ConnexionClient> listeConsommateurs = new ArrayList();   

        try {
            // Utilisation du port 5000 pour que les clients puissent se connecter
            serveurSocket = new ServerSocket(5000);
            
            // Création d'un Thread d'envoie à destination de tous les consomateurs
            Thread envoyer= new Thread(new Runnable() {
               String msg;
               @Override
                public void run() {
                    while(true) {
                        msg = "";
                        if ((!buffer.isEmpty()) && (!listeConsommateurs.isEmpty())) {
                            msg = buffer.get(0);
                            buffer.remove(0);
                        }
                        if (!msg.equals("") && !listeConsommateurs.isEmpty()) {
                            for (ConnexionClient cc : listeConsommateurs) {
                                System.out.println("transfert de ( " + msg + " )");
                                cc.Out().println(msg);
                                cc.Out().flush();
                            }
                        }
                    }
               }
            });
            envoyer.start();
            
            int nbC = 0;
            while(true) {
                // Le serveur autorise un client à se connecter
                clientSocket = serveurSocket.accept();
                
                // Création d'un objet ConnexionClient pour pouvoir gérer les clients connectés
                nbC++;
                ConnexionClient cc = new ConnexionClient("Client"+nbC, serveurSocket, clientSocket);
                if(cc.TypeClient().equals("P")) {
                    listeProducteurs.add(cc);
                    listeProducteurs.get(listeProducteurs.indexOf(cc)).start();
                    //System.out.println("Connexion établie avec le Producteur Client" + nbC);
                } else {
                    listeConsommateurs.add(cc);
                    //System.out.println("Connexion établie avec le Consommateur Client" + nbC);
                }
            }            
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
    }
}
