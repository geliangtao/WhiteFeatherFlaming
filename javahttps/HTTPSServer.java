package com.ss.test;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class HTTPSServer {
    private int port = 443;
    private boolean isServerDone = false;

    public static void main(String[] args){
        HTTPSServer server = new HTTPSServer();
        server.run();
    }

    HTTPSServer(){
    }

    HTTPSServer(int port){
        this.port = port;
    }

    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{
            //keytool -genkey -alias geliangtao -keypass geliangtao -keyalg RSA -keysize 1024 -keystore https.keystore -storepass geliangtao
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(
                    new FileInputStream("F:/SVNPATH/HJPRODSVN/Internet_FinancialCode/trunk/SmartBP/modules/bp-ssimulation/src/test/resources/keys/https.keystore"),
                    "geliangtao".toCharArray());

            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "geliangtao".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km,  tm, null);

            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    // Start to run the server
    public void run(){
        SSLContext sslContext = this.createSSLContext();

        try{
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);

            System.out.println("SSL server started");
            while(!isServerDone){
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

                // Start the server thread
                new ServerThread(sslSocket).start();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Thread handling the socket from client
    static class ServerThread extends Thread {
        private SSLSocket sslSocket = null;

        ServerThread(SSLSocket sslSocket){
            this.sslSocket = sslSocket;
        }

        public void run(){
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

            try{
                // Start handshake
                sslSocket.startHandshake();

                // Get session after the connection is established
                SSLSession sslSession = sslSocket.getSession();

                System.out.println("SSLSession :");
                System.out.println("\tProtocol : "+sslSession.getProtocol());
                System.out.println("\tCipher suite : "+sslSession.getCipherSuite());

                // Start handling application content
                InputStream inputStream = sslSocket.getInputStream();
                OutputStream outputStream = sslSocket.getOutputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println("Inut : "+line);

                    if(line.trim().isEmpty()){
                        break;
                    }
                }
                String response = "{\"token\":\"fdfdfasdfdsfddfasdf\",\"uid\":\"1242312\"}";
                // Write data
                printWriter.print("HTTP/1.1 200 OK\r\nServer: geliangtaoserver/1.0.8.18\r\nContent-Length:"+(response.length()+1) +"\r\n\r\n"+response+"\n");
                printWriter.flush();

                sslSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}