package serveurpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ConnexionClient extends Thread {
    private final String typeClient;
    private final int numClient;
    private final Socket clientSocket ;
    private final BufferedReader in;
    private final PrintWriter out;
    
    private static volatile File f;
    private volatile FileWriter fw;
    private volatile FileReader fr;
    
    private static volatile int nbLec = 0;
    private static volatile int nbEcr = 0;
    
    private static volatile ArrayList<Integer> listeAttente = new ArrayList();
    
    public ConnexionClient(int num, Socket c) throws IOException {
        this.numClient = num;
        this.clientSocket = c;
        this.in = new BufferedReader (new InputStreamReader (this.clientSocket.getInputStream()));
        this.out = new PrintWriter(this.clientSocket.getOutputStream());
        
        // Récupération du type de client
        this.typeClient = this.in.readLine();
        
        // Ouveture d'un accés au fichier
        this.f = new File("F.txt");
        this.fw = new FileWriter(f, true);
        this.fr = new FileReader(f);
        
        if (listeAttente.isEmpty()) listeAttente.add(0);
    }
    
    @Override
    public void run() {
        if (this.typeClient.equals("R")) {
            String msg ;
            try {
                // Si personne ne lit ou n'écrit dans le fichier et que ce Thread est le plus ancien connecté alors l'exécution peut se poursuivre
                while((nbEcr != 0) && (nbLec != 0) || (listeAttente.indexOf(numClient) != 0)) {
                    if (!listeAttente.contains(numClient)) listeAttente.add(listeAttente.indexOf(0), numClient);
                }
                nbEcr++;
                listeAttente.remove(0);
                //System.out.println("Ecriture " + nbEcr);

                // Ecriture dans le fichier
                msg = in.readLine();
                fw.write(msg + "\n");

                nbEcr--;
                //System.out.println("Ecriture " + nbEcr);

                fw.close();
                fr.close();

                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else if (this.typeClient.equals("L")) {
            String msg;
            try {
                // Si personne n'écrit dans le fichier et que ce Thread est le plus ancien connecté alors l'exécution peut se poursuivre
                while((nbEcr != 0) || (listeAttente.indexOf(numClient) != 0)) {
                    if (!listeAttente.contains(numClient)) listeAttente.add(listeAttente.indexOf(0), numClient);
                }
                nbLec++;
                listeAttente.remove(0);
                //System.out.println("Lecture " + nbLec);

                // Lecture du fichier
                int taille = Math.toIntExact(f.length());
                char buf[] = new char[taille];
                fr.read(buf);
                msg = String.valueOf(buf);

                out.println(msg);
                out.flush();

                nbLec--;
                //System.out.println("Lecture " + nbLec);

                fw.close();
                fr.close();

                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public String TypeClient() {
        return this.typeClient;
    }
    
    public PrintWriter Out() {
        return this.out;
    }
}
