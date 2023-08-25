package serveurpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServeurLR {
    
    public static void main(String[] args) {
        final ServerSocket serveurSocket;
        Socket clientSocket ;
        ArrayList<ConnexionClient> listeLecteurs = new ArrayList();
        ArrayList<ConnexionClient> listeRedacteurs = new ArrayList();

        try {
            // Utilisation du port 5000 pour que les clients puissent se connecter
            serveurSocket = new ServerSocket(5000);
            
            int nbC = 0;
            while(true) {
                // Le serveur autorise un client à se connecter
                clientSocket = serveurSocket.accept();
                
                // Création d'un objet ConnexionClient pour pouvoir gérer les clients connectés
                nbC++;
                ConnexionClient cc = new ConnexionClient(nbC, clientSocket);
                if(cc.TypeClient().equals("L")) {
                    listeLecteurs.add(cc);
                    listeLecteurs.get(listeLecteurs.indexOf(cc)).start();
                    //System.out.println("Connexion établie avec le Lecteur Client" + nbC);
                } else {
                    listeRedacteurs.add(cc);
                    listeRedacteurs.get(listeRedacteurs.indexOf(cc)).start();
                    //System.out.println("Connexion établie avec le Redacteur Client" + nbC);
                }
            }
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
    }
}
