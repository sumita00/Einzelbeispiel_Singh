package com.example.einzelbeispiel_singh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView martikelNr;
    private TextView antwortFromServer;
    private TextView berechnungresult;
    private Button sendButton;
    private Button berechnen;

    private Server server;
    private Handler handler = new Handler();

    public void getAntwortFromServer(View view) {
        server = new Server();
        new Thread(server).start();

        try {
            server.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        martikelNr = (TextView) findViewById(R.id.martikelNr);
        antwortFromServer = (TextView) findViewById(R.id.antwortFromServer);
        berechnungresult = (TextView) findViewById(R.id.berechnungresult);
        sendButton = (Button) findViewById(R.id.button3);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
    }

    public String getAlternierndeQuerSummeInfo(String martikelnummer){ //Ausgabe der Berechnung
        String summeInfos = "";
        ArrayList<Integer> martikelNrZiffern = getMartikelNrZiffern(martikelnummer);

        int result = getAlternierendQuersumme(martikelNrZiffern);

        if(result % 2 == 0){
            summeInfos = "Summe ist eine gerade Zahl";
        }
        else{
            summeInfos = "Summe ist eine ungerade Zahl";
        }
        return summeInfos;
    }

    public int getAlternierendQuersumme(ArrayList<Integer> martikelNr){ //Berechnung
        int result = martikelNr.get(0);

        for(int i = 1; i < martikelNr.size(); i++){
            if(i%2 != 0){
                result = result -  martikelNr.get(i);
            }
            else{
                result = result +  martikelNr.get(i);
            }
        }
        return result;
    }

    class Server extends Thread {

        private String input = martikelNr.getText().toString();
        private String output = "";


        @Override
        public void run() { //Verbindung
            Socket socket = null;
            DataOutputStream outputStream = null;
            BufferedReader bufferedReader = null;
            String hostName = "se2-isys.aau.at";
            int port = 53212;

            try {
                socket = new Socket(hostName, port);

                outputStream = new DataOutputStream(socket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if (input != null && input != "") {
                    outputStream.writeBytes(input + '\n');
                } else {
                    Log.e("Input error", "Error");
                }

                output = bufferedReader.readLine();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        antwortFromServer.setText(output);
                    }
                });

                socket.close();
            } catch (UnknownHostException u) {
                Log.i("Exception", "Something went wrong with binding :/");
                u.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }
}

