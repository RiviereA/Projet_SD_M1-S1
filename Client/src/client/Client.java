/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author ar948052
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);//pour lire à partir du clavier

        try {
           /*
           * les informations du serveur ( port et adresse IP ou nom d'hote
           * 127.0.0.1 est l'adresse local de la machine
           */
           clientSocket = new Socket("127.0.0.1",5000);
           //clientSocket = new Socket("172.31.18.235",5000);

           //flux pour envoyer
           out = new PrintWriter(clientSocket.getOutputStream());
           //flux pour recevoir
           in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

           Thread envoyer = new Thread(new Runnable() {
               String msg;
                @Override
                public void run() {
                  while(true){
                    msg = sc.nextLine();
                    out.println(msg);
                    out.flush();
                  }
               }
           });
           envoyer.start();

          Thread recevoir = new Thread(new Runnable() {
              String msg;
              @Override
              public void run() {
                 try {
                   msg = in.readLine();
                   while(msg!=null){
                      System.out.println("Serveur : "+msg);
                      msg = in.readLine();
                   }
                   System.out.println("Serveur déconecté");
                   out.close();
                   clientSocket.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
              }
          });
          recevoir.start();

        } catch (IOException e) {
             e.printStackTrace();
        }
    }
    
}
