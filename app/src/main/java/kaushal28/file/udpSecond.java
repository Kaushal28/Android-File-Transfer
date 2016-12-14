package kaushal28.file;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by kaushal28 on 13/12/16.
 */

public class udpSecond extends AsyncTask<Void,Void,Void> {

    Context context;

    udpSecond(Context c){
        context = c;
    }



    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Log.i("***** UDP client: ", "starting");
            final String text;
            int server_port = 6668;
            byte[] message = new byte[2048];
            DatagramPacket p = new DatagramPacket(message, message.length);
            DatagramSocket s = new DatagramSocket(server_port);
            Log.i("***** UDP client: ", "about to wait to receive");
            s.receive(p); // blocks until something is received
            Log.i("***** UDP client: ", "received");
            text = new String(message, 0, p.getLength());
            Log.i("*UDP client message: ", text);

            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(context, text,Toast.LENGTH_LONG).show();
                }
            });

            Log.d("Udp tutorial","message:" + text);
            s.close();
        } catch ( SocketException e) {
            Log.i("***** UDP client has: ", "Socket Exception");
        } catch (IOException e){
            Log.i("UDPclient has Exception", "e: " + e);
        }

        return null;
    }
}
