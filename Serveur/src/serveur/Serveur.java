/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author ar948052
 */
public class Serveur {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final ServerSocket serveurSocket  ;
        final Socket clientSocket ;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc=new Scanner(System.in);

        try {
            serveurSocket = new ServerSocket(5000);
            clientSocket = serveurSocket.accept();
            
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
            
            Thread envoi= new Thread(new Runnable() {
               String msg;
               @Override
                public void run() {
                   while(true) {
                      msg = sc.nextLine();
                      out.println(msg);
                      out.flush();
                    }
               }
            });
            envoi.start();

            Thread recevoir= new Thread(new Runnable() {
               String msg ;
               @Override
               public void run() {
                    try {
                        msg = in.readLine();
                        //tant que le client est connecté
                        while(msg!=null){
                           System.out.println("Client : "+msg);
                           msg = in.readLine();
                        }
                        //sortir de la boucle si le client a déconecté
                        System.out.println("Client déconecté");
                        //fermer le flux et la session socket
                        out.close();
                        clientSocket.close();
                        serveurSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
              }
           });
           recevoir.start();
        }catch (IOException e) {
           e.printStackTrace();
        }
    }
    
}
