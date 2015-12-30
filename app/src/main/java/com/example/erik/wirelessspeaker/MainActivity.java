package com.example.erik.wirelessspeaker;

import android.app.ListActivity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import java.util.List;
import java.io.File;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

///*
//Filename Filter for Filelist in updateList method
//returns only files with file type ending of ".mp3"
// */
//class Mp3Filter implements FilenameFilter{
//    public boolean accept(File dir, String name){
//        return (name.endsWith(".mp3"));
//    }
//}



public class MainActivity extends ListActivity {
//    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for Bluetooth connection
//    private static String AUDIO_FILE_PATH;  //Audio file path
    private List<String> audioList;  //List for audio file for list selection
    private MediaPlayer mPlayer;  //mediaplayer for playback
//    private Set<BluetoothDevice> devices;  //set for paired Bluetooth devices
//    private ArrayAdapter deviceList;  //list for Bluetooth devices to put in dropdown menu
//    private Spinner btDropDown;  //dropdown menu for Bluetooth devices
//    BluetoothDevice btDevice;  //Bluetooth device object to work with connected device
//    BluetoothSocket btSocket;  //socket to connect with Bluetooth device
//    BluetoothA2dp btRouter;  //A2DP object to send audio files to device
    Button stopButton;  //stop button to stop playback
//    CheckBox connected;  //checkbox to alert user that bluetooth device is connected
    BluetoothAdapter btAdapter;  //adapter to discover bluetooth devices and get info from device
//    IntentFilter deviceFilter;  //intentFilter to monitor bluetooth state
//    BroadcastReceiver btReceiver;  //find avaialable bluetooth devices in area
//    Method connect;  //reflection method to connect A2DP device
    Equalizer btEQ;  //Equalizer to equalize mediaplayer object signal
    TextView trebLevel;  //band level display
    TextView midLevel;  //band level display
    TextView bassLevel;  //band level display
    Button trebUp;  //equalizer button
    Button trebDown;  //equalizer button
    Button midUp;  //equalizer button
    Button midDown;  //equalizer button
    Button bassUp;  //equalizer button
    Button bassDown;  //equalizer button
    short lev[]={-1500, -1200, -900, -600, -300, 0, 300, 600, 900, 1200, 1500};  //Equalizer band gains
    int trebP = 5;  //band gain pointer
    int midP = 5;  //band gain pointer
    int bassP = 5;  //band gain pointer

    /*
    Method that defines what happens when the app is started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Checks that device has bluetooth and that it is on and acts according
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null){
            Toast.makeText(getApplicationContext(), "No Bluetooth detected.", Toast.LENGTH_LONG).show();
            finish();
        }else {
            if (!btAdapter.isEnabled()) {
                btTurnOn();
            }
        }
        //initialize all variables
        initialize();
        //updates the audio file selection list
        //updateList();

        //Manual addition of audio files to list from raw folder to work on my LG G2 device
        audioList.add(Uri.parse("android.resource://com.example.erik.wirelessspeaker/"+ R.raw.heartbreaker).toString());
        audioList.add(Uri.parse("android.resource://com.example.erik.wirelessspeaker/"+ R.raw.freqsongshort).toString());
        audioList.add(Uri.parse("android.resource://com.example.erik.wirelessspeaker/"+ R.raw.noisysong).toString());
        audioList.add(Uri.parse("android.resource://com.example.erik.wirelessspeaker/"+ R.raw.noisysongshort).toString());
        ArrayAdapter<String> finalList = new ArrayAdapter<>(this, R.layout.list_item, audioList);
        setListAdapter(finalList);

        //disable stop button until playback starts
        stopButton.setEnabled(false);


//----------------------------------------Failed Bluetooth code------------------------------------
//        //setup spinner
//        deviceList.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        btDropDown.setAdapter(deviceList);
//
//        //when user selects bluetooth device from dropdown menu connects to that device
//        btDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View v,
//                                       int position, long id) {
// if(!btDropDown.getItemAtPosition(position).equals("Select")) {
//
//
//                //get mac address of selected bluetooth device and set
//                //Bluetooth device object to selected device
//                String deviceSelect=btDropDown.getSelectedItem().toString();
//                String btMac = deviceSelect.substring(deviceSelect.indexOf("\n")+1);
//                btDevice = btAdapter.getRemoteDevice(btMac);
//
//
//
//
//     //get UUID to connect bluetooth socket
//     ParcelUuid[] parc = btDevice.getUuids();
//     UUID btPID = parc[0].getUuid();
//     String PID=btPID.toString();
//         //try to connect to device as bluetooth socket
//         try {
//
//             btSocket = btDevice.createRfcommSocketToServiceRecord(btPID);
//         } catch (IOException e) {
//             Toast.makeText(getApplicationContext(), "Could not connect to " + btDevice.getName(), Toast.LENGTH_SHORT).show();
//
//         }
//         btAdapter.cancelDiscovery();  //cancel discovery stage to increase efficiency
//         try {
//             btSocket.connect();
//         } catch (IOException e) {
//             Toast.makeText(getApplicationContext(), "Connection Attempt Fail.", Toast.LENGTH_LONG).show();
//
//             try {
//                 btSocket.close();
//             } catch (IOException f) {
//                 Toast.makeText(getApplicationContext(), "Failed to close connection", Toast.LENGTH_LONG).show();
//
//             }
//
//         }
//     //Debugging check if bluetooth socket is connected
//     Toast.makeText(getApplicationContext(), "Device is...", Toast.LENGTH_SHORT).show();
//     if(btSocket.isConnected()){
//         Toast.makeText(getApplicationContext(), "Connected on " + PID, Toast.LENGTH_LONG).show();
//     }else{
//         Toast.makeText(getApplicationContext(), "NOT connected", Toast.LENGTH_LONG).show();
//     }
// }
//               //Failed attempt at reflection to connect to A2DP device
//                BluetoothProfile.ServiceListener btListener = new BluetoothProfile.ServiceListener() {
//                    @Override
//                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
//                        btRouter = (BluetoothA2dp) proxy;
//                        try {
//                            connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
//                        } catch (NoSuchMethodException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            connect.invoke(btRouter);
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        } catch (InvocationTargetException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onServiceDisconnected(int profile) {
//
//                    }
//                };
//
//            btAdapter.getProfileProxy(getApplicationContext(), btListener, BluetoothProfile.A2DP);
//
//            }
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//
//
//            }
//
//        });



//        //  start discovery process when discover button us pressed
//        btDiscovery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btStartDiscovery();
//
//            }
//
//
//        });
//-----------------------------------------------end---------------------------------------------------
        //Stop playback when stop button is pressed
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer.isPlaying()) {
                    mPlayer.stop();
                    stopButton.setEnabled(false);
                }
            }


        });
        //decrease band gain when button is pressed
        //disable button at lowest level
        //enable up button if it is disabled
        trebDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!trebUp.isEnabled()){
                    trebUp.setEnabled(true);
                }
                trebP--;
                trebLevel.setText(trebP*10 + "%");
                btEQ.setBandLevel((short)3, lev[trebP]);
                btEQ.setBandLevel((short)4, lev[trebP]);
                if(trebP==0){
                    trebDown.setEnabled(false);
                }
            }


        });

        //increase band gain when button is pressed
        //disable button at highest level
        //enable down button if it is disabled
        trebUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!trebDown.isEnabled()){
                    trebDown.setEnabled(true);
                }
                trebP++;
                trebLevel.setText(trebP*10 + "%");
                btEQ.setBandLevel((short)3, lev[trebP]);
                btEQ.setBandLevel((short)4, lev[trebP]);
                if(trebP==10){
                    trebUp.setEnabled(false);
                }
            }


        });

        //decrease band gain when button is pressed
        //disable button at lowest level
        //enable up button if it is disabled
        midDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!midUp.isEnabled()){
                    midUp.setEnabled(true);
                }
                midP--;
                midLevel.setText(midP*10 + "%");
                btEQ.setBandLevel((short)2, lev[midP]);
                if(midP==0){
                    midDown.setEnabled(false);
                }
            }


        });

        //increase band gain when button is pressed
        //disable button at highest level
        //enable down button if it is disabled
        midUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!midDown.isEnabled()){
                    midDown.setEnabled(true);
                }
                midP++;
                midLevel.setText(midP*10 + "%");
                btEQ.setBandLevel((short)2, lev[midP]);
                if(midP==10){
                    midUp.setEnabled(false);
                }
            }


        });

        //decrease band gain when button is pressed
        //disable button at lowest level
        //enable up button if it is disabled
        bassDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bassUp.isEnabled()){
                    bassUp.setEnabled(true);
                }
                bassP--;
                bassLevel.setText(bassP*10 + "%");
                btEQ.setBandLevel((short)0, lev[bassP]);
                btEQ.setBandLevel((short)1, lev[trebP]);
                if(bassP==0){
                    bassDown.setEnabled(false);
                }
            }


        });

        //increase band gain when button is pressed
        //disable button at highest level
        //enable down button if it is disabled
        bassUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bassDown.isEnabled()){
                    bassDown.setEnabled(true);
                }
                bassP++;
                bassLevel.setText(bassP*10 + "%");
                btEQ.setBandLevel((short)0, lev[trebP]);
                btEQ.setBandLevel((short)1, lev[bassP]);
                if(bassP==10){
                    bassUp.setEnabled(false);
                }
            }


        });
    }

//    //start discovery of available bluetooth devices
//    private void btStartDiscovery() {
//        if(btAdapter.isDiscovering()){
//            btAdapter.cancelDiscovery();
//        }
//        deviceList.clear();
//        deviceList.add("Select");
//        btAdapter.startDiscovery();
//
//    }

    //turn on bluetooth in local device
    private void btTurnOn() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
    }

    //start playback when audio file in list is selected
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id_){
        try{
            mPlayer.reset();
            mPlayer.setDataSource(getApplicationContext(), Uri.parse(list.getItemAtPosition(position).toString()));
            mPlayer.prepare();
            mPlayer.start();
            stopButton.setEnabled(true);
        }catch(IOException e) {
            Log.v(getString(R.string.app_name), e.getMessage());
        }
    }

//    //updates audio file list with mp3 files
//    private void updateList(){
//    File home = new File(AUDIO_FILE_PATH);
//        if(home.listFiles(new Mp3Filter()).length>0){
//            for(File file : home.listFiles(new Mp3Filter())){
//                audioList.add(file.getName());
//            }
//            ArrayAdapter<String> finalList = new ArrayAdapter<String>(this, R.layout.list_item, audioList);
//            setListAdapter(finalList);
//        }
//    }

//    //find previously paired bluetooth devices
//    public void findPairedDevices(){
//        devices = btAdapter.getBondedDevices();
//        if(devices.size()>0){
//            for(BluetoothDevice item: devices){
//                deviceList.add(item.getName() + "\n" + item.getAddress());
//            }
//        }
//    }

//When app is paused unregister bluetooth connection
@Override
protected void onPause(){
    super.onPause();
//unregisterReceiver(btReceiver);
}

//if user turns off bluetooth closes app
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be Enabled.", Toast.LENGTH_LONG).show();
            finish();
        }
}

//initialized all variables
private void initialize(){
//    AUDIO_FILE_PATH = "android.resource://com.example.erik.wirelessspeaker/";
    stopButton = (Button) findViewById(R.id.stopButton);
    mPlayer = new MediaPlayer();
    audioList = new ArrayList<>();
//    deviceList = new ArrayAdapter(this, android.R.layout.simple_spinner_item, 0);
//    deviceFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//    final Button btDiscovery = (Button) findViewById(R.id.SearchBtn);
//    connected = (CheckBox) findViewById(R.id.connectBox);
//    btDropDown = (Spinner) findViewById(R.id.btDropDown);
//    btDropDown.setEnabled(false);
//    btDropDown.setPrompt("Select Device");
//    btReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String deviceAction = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(deviceAction)){
//                BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                deviceList.add(newDevice.getName() + "\n" + newDevice.getAddress());
//            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(deviceAction)){
//                Toast.makeText(getApplicationContext(), "Searching for Bluetooth Devices.", Toast.LENGTH_LONG).show();
//                btDropDown.setEnabled(false);
//            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(deviceAction)){
//                btDropDown.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "Finished searching for devices.", Toast.LENGTH_SHORT).show();
//            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(deviceAction)){
//                if(btAdapter.getState()==btAdapter.STATE_OFF){
//                btTurnOn();
//                }
//            }
//        }
//    };
//    registerReceiver(btReceiver, deviceFilter);
//    IntentFilter updateFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//    registerReceiver(btReceiver, updateFilter);
//    updateFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//    registerReceiver(btReceiver, updateFilter);
//    updateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//    registerReceiver(btReceiver, updateFilter);
    btEQ = new Equalizer(7, mPlayer.getAudioSessionId());
    btEQ.setEnabled(true);
    trebLevel = (TextView) findViewById(R.id.TrebleLevel);
    midLevel = (TextView) findViewById(R.id.MidLevel);
    bassLevel = (TextView) findViewById(R.id.BassLevel);
    trebUp=(Button)findViewById(R.id.BtnTrebUp);
    trebDown=(Button)findViewById(R.id.BtnTrebDown);
    midUp=(Button)findViewById(R.id.BtnMidUp);
    midDown=(Button)findViewById(R.id.BtnMidDown);
    bassUp=(Button)findViewById(R.id.BtnBassUp);
    bassDown=(Button)findViewById(R.id.BtnBassDown);
    btEQ.setBandLevel((short)0, (short)0);
    btEQ.setBandLevel((short)2, (short)0);
    btEQ.setBandLevel((short)4, (short)0);
}
//When user closed app stop mediaplyer object
@Override
    protected void onDestroy(){
    if(mPlayer.isPlaying()){
        mPlayer.stop();
        mPlayer.release();
        mPlayer=null;
    }





}









}







