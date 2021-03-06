package kaushal28.file;

import android.app.Activity;
import android.content.Context;
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
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private String wholePath;
    private boolean xceptionFlag = false;
    private Socket socket;
    private String hostName,canonicalHostname;
    private String givenName;

    first(Context context, Activity act, String path, String fullPath){
        this.context = context;
        this.activity = act;
        this.filePath = path;
        this.wholePath = fullPath;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);



        //Setting listView to display IP Addresses Which are available to send our files!
        listView = (ListView)activity.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                a );



        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //now we have host names like Xender so change following line. Get Ipaddress instead of host names.
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

       // filePath = filePath.replace("%20"," ");

        files.add(new File(wholePath));
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
            socket = new Socket(destinationAddress,5004);
//            socket.setReuseAddress(true);

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
            xceptionFlag = true;
            e.printStackTrace();
        }

        Log.i("===end of start ====", "==");
        try{
            if(!socket.isClosed()){
                socket.close();
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(xceptionFlag){
            Toast.makeText(context,"Something went wrong.",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context,"files Sent Successfully!!",Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String> getClientList() {

        final ArrayList<String> arr = new ArrayList<>(25);

        Thread thread = new Thread(new Runnable() {



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

                                //added afterwards for receiving names of available clients..
                                //but by adding this names to array list, the ip addresses is lost. so do something.
                                try {
                                    Socket socket = new Socket();
                                    //receive from port 5006 and timeout is 5s.
                                    socket.connect(new InetSocketAddress(ipAddress, 5006), 5000);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    givenName = reader.readLine();
                                    reader.close();
                                    socket.close();
                                    Log.i("TAG", givenName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Assigning values to final array or array list is perfectly fine.
                                //here add ipAddress to see working transfer.
                                arr.add(givenName);
                                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                                hostName = inetAddress.getHostName();
                                canonicalHostname = inetAddress.getCanonicalHostName();

                              //  Toast.makeText(context,hostName+canonicalHostname,Toast.LENGTH_LONG).show();

                            }

                        }

                    }

                } catch (Exception e) {
                    xceptionFlag = true;
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        xceptionFlag = true;
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
            xceptionFlag = true;
            e.printStackTrace();
        }
        return arr;

    }
}
