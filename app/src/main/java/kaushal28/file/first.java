package kaushal28.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kaushal28 on 13/12/16.
 */

public class first extends AsyncTask<Void,Void,Void> {

    private ArrayList<String> a;
    private ListView listView;
    private Context context;
    private Activity activity;
    private String destinationAddress="-1";
    private String filePath;


    first(Context context, Activity act, String path){
        this.context = context;
        this.activity = act;
        this.filePath = path;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);



        //Setting listView to display IP Addresses Which are available to send our files!
        listView = (ListView)activity.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                a );



        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                destinationAddress = (String)adapterView.getItemAtPosition(position);

            }
        });


    }

    @Override
    protected Void doInBackground(Void... voids) {


        System.out.println("array list");
        ArrayList<File> files = new ArrayList<>();
        System.out.println("about to create.");




        //Add files (Music or whatever) to array list to send them!
//        files.add(new File("mnt/sdcard/Download/ab.mp3"));
//        files.add(new File("mnt/sdcard/Download/bh.mp3"));
//        files.add(new File("mnt/sdcard/Download/bc.mp3"));

        filePath = filePath.replace("%20"," ");

        files.add(new File("/mnt/sdcard/Download/"+filePath));
        System.out.println("file created..");
        try {


            //Receiving IP addresses which are available to send our files(Music)!!
            a = getClientList();


            //update the UI to display the received IP addresses!!
            publishProgress();


            //busy waiting for user to select appropriate IP address to send files!
            while (destinationAddress.equals("-1")){

            }

            //User has selected something, It's time to send files there!
            Socket socket = new Socket(destinationAddress,5004);
            System.out.println("Connecting...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println(files.size());
            //write the number of files to the server
            dos.writeInt(files.size());
            dos.flush();


            //write file size
            for(int i = 0;i< files.size();i++){
                int file_size = Integer.parseInt(String.valueOf(files.get(i).length()));
                dos.writeLong(file_size);
                dos.flush();
            }

            //write file names
            for(int i = 0 ; i < files.size();i++){
                dos.writeUTF(files.get(i).getName());
                dos.flush();
            }

            //buffer for file writing, to declare inside or outside loop?
            int n = 0;
            byte[]buf = new byte[4092];
            //outer loop, executes one for each file
            for(int i =0; i < files.size(); i++){

                System.out.println(files.get(i).getName());
                //create new fileinputstream for each file
                FileInputStream fis = new FileInputStream(files.get(i));

                //write file to dos
                while((n =fis.read(buf)) != -1){
                    dos.write(buf,0,n);
                    dos.flush();

                }
                //should i close the dataoutputstream here and make a new one each time?
            }
            //or is this good?
            dos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.i("===end of start ====", "==");


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context,"files Sent Successfully!!",Toast.LENGTH_LONG).show();
    }

    public ArrayList<String> getClientList() {

        final ArrayList<String> arr = new ArrayList<>(25);

        Thread thread = new Thread(new Runnable() {


            int counter = 0;
            @Override
            public void run() {
                BufferedReader br = null;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] splitted = line.split(" +");

                        if (splitted != null && splitted.length >= 4) {

                            String ipAddress = splitted[0];
                            String macAddress = splitted[3];

                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);  // this is network call so we cant do that on UI thread, so i(kaushal28) take background thread.
                            if (isReachable) {
                                Log.d("Device Information", ipAddress + " : "
                                        + macAddress);

                                //Assigning values to final array or array list is perfectly fine.
                                arr.add(ipAddress);
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        //Wait util thread is completed. And then return array. Otherwise it'll return null array or array list or what ever.
        try{
            thread.join();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return arr;

    }


}
