package edu.utep.cs.cs4330.smartlock2;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class lockActivity extends AppCompatActivity {
    Boolean isLock = false;
    Boolean isBluetooth = false;
    private Socket socket;
    Boolean isConnected = false;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button wifiLock = (Button) findViewById(R.id.wifiLock);
        final ImageButton lockDisplay = (ImageButton) findViewById(R.id.checkLock); //Lock for bluetooth
        final ImageButton blueDisplay = (ImageButton) findViewById(R.id.checkBluetooth);//Toggle Bluetooth on/off
        final Button connect = (Button) findViewById(R.id.connect);
        final Button doorStatus = (Button)findViewById(R.id.doorStatus);
        final TextView ipAddress = (TextView) findViewById(R.id.ipAddress);
        final TextView portNumber = (TextView) findViewById(R.id.portNumber);
        final TextView status = (TextView) findViewById(R.id.status);
        final String lockMessage = "Lock";
        final String unlockMessage = "Unlock";
        final BluetoothServerSocket[] blueSocket = new BluetoothServerSocket[1];


        /**Used to change lock image on Lock Button*/
        String uriLock = "@drawable/locked";  // where myresource (without the extension) is the file
        int sourceLock = getResources().getIdentifier(uriLock, null, getPackageName());
        final Drawable lock = getResources().getDrawable(sourceLock);

        /**Used to change unlock image on Lock button*/
        String uriULock = "@drawable/unlocked";  // where myresource (without the extension) is the file
        int sourceULock = getResources().getIdentifier(uriULock, null, getPackageName());
        final Drawable unlock = getResources().getDrawable(sourceULock);

        /**Used to change the On image for bluetooth button*/
        String uriBlueOn = "@drawable/bluetooth_on";  // where myresource (without the extension) is the file
        int sourceBlueOn = getResources().getIdentifier(uriBlueOn, null, getPackageName());
        final Drawable BlueOn = getResources().getDrawable(sourceBlueOn);

        /**Used to change the Off image for bluetooth button*/
        String uriBlueOff = "@drawable/bluetooth_off";  // where myresource (without the extension) is the file
        int sourceBlueOff = getResources().getIdentifier(uriBlueOff, null, getPackageName());
        final Drawable BlueOff = getResources().getDrawable(sourceBlueOff);

        /**Sets buttons to appropriate text depending on if the door is locked or not*/
        checkLockDisplays();



        wifiLock.setEnabled(false);
        doorStatus.setEnabled(false);

        blueDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBluetooth) {
                    blueDisplay.setBackgroundColor(Color.GRAY);
                    isBluetooth = false;
                    blueDisplay.setImageDrawable(BlueOff);
                    BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
                    bt.disable();
                } else {
                    blueDisplay.setBackgroundColor(Color.BLUE);
                    isBluetooth = true;
                    blueDisplay.setImageDrawable(BlueOn);
                    BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
                    bt.enable();

                }

            }
        });

        lockDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBluetooth) {
                    if (isLock) { //if locked, UNLOCK
                        isLock = false;
                        lockDisplay.setBackgroundColor(Color.GREEN);
                        lockDisplay.setImageDrawable(null);
                        lockDisplay.setImageDrawable(unlock);
                        lockDisplay.setScaleType(ImageView.ScaleType.FIT_END);
                        lockDisplay.setAdjustViewBounds(true);
                        lockDisplay.invalidate();
                        toast("Unlocked");
                        sendBlueData(unlockMessage);
                        checkLockDisplays();
                    } else {
                        isLock = true;
                        lockDisplay.setBackgroundColor(Color.RED);
                        //lockDisplay.setBackgroundResource(R.drawable.unlocked);
                        lockDisplay.setImageDrawable(null);
                        lockDisplay.setImageDrawable(lock);
                        //lockDisplay.setBackgroundResource(R.drawable.unlocked);
                        lockDisplay.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        lockDisplay.setAdjustViewBounds(true);
                        lockDisplay.invalidate();
                        toast("Locked");
                        sendBlueData(lockMessage);
                        checkLockDisplays();
                    }
                } else {
                    toast("Please Turn On Bluetooth");
                }
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected) {
                    connectToServer(ipAddress.getText().toString(), portNumber.getText().toString());
                    isConnected = true;
                    connect.setText("Disconnect");
                }else{
                    sendMessage("Disconnect");
                    socket = null;
                    isConnected = false;
                    doorStatus.setEnabled(false);
                    wifiLock.setEnabled(false);
                    connect.setText("Connect");
                    status.setText("Disconnected");
                }
            }
        });

        wifiLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ipAddress.getText()) != null || (portNumber.getText()) != null) {
                    if (isLock) {
                        isLock = false;
                        wifiLock.setText("Press to Unlock");

                        wifiLock.setBackgroundColor(Color.RED);
                        sendMessage(unlockMessage);
                        checkLockDisplays();


                    } else {
                        isLock = true;
                        wifiLock.setText("Press to Lock");

                        wifiLock.setBackgroundColor(Color.GREEN);
                        sendMessage(lockMessage);
                        checkLockDisplays();


                    }
                } else {
                    toast("Please enter WIFI values");
                }
            }
        });

        doorStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("IsLocked");
                checkLockDisplays();


            }
        });


    }

    private void sendBlueData(String message){

    }

    protected void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void checkLockDisplays() {
        final Button wifiLock = (Button) findViewById(R.id.wifiLock);
        final ImageButton lockDisplay = (ImageButton) findViewById(R.id.checkLock);
        String uriLock = "@drawable/locked";  // where myresource (without the extension) is the file
        int sourceLock = getResources().getIdentifier(uriLock, null, getPackageName());
        final Drawable lock = getResources().getDrawable(sourceLock);
        String uriULock = "@drawable/unlocked";  // where myresource (without the extension) is the file
        int sourceULock = getResources().getIdentifier(uriULock, null, getPackageName());
        final Drawable unlock = getResources().getDrawable(sourceULock);


        if (isLock) {
            wifiLock.setText("Press to Unlock");
            wifiLock.setBackgroundColor(Color.RED);
            lockDisplay.setBackgroundColor(Color.RED);
            lockDisplay.setImageDrawable(lock);
            wifiLock.invalidate();
            lockDisplay.invalidate();
        } else {
            wifiLock.setText("Press to Lock");
            wifiLock.setBackgroundColor(Color.GREEN);
            lockDisplay.setBackgroundColor(Color.GREEN);
            lockDisplay.setImageDrawable(unlock);
            wifiLock.invalidate();
            lockDisplay.invalidate();
        }


        final ImageButton blueDisplay = (ImageButton) findViewById(R.id.checkBluetooth);
        String uriBlueOn = "@drawable/bluetooth_on";  // where myresource (without the extension) is the file
        int sourceBlueOn = getResources().getIdentifier(uriBlueOn, null, getPackageName());
        final Drawable BlueOn = getResources().getDrawable(sourceBlueOn);
        String uriBlueOff = "@drawable/bluetooth_off";  // where myresource (without the extension) is the file
        int sourceBlueOff = getResources().getIdentifier(uriBlueOff, null, getPackageName());
        final Drawable BlueOff = getResources().getDrawable(sourceBlueOff);
        isBluetooth = isBluetoothEnabled();

        if (isBluetooth) {
            blueDisplay.setBackgroundColor(Color.BLUE);
            blueDisplay.setImageDrawable(BlueOn);
            blueDisplay.invalidate();
        } else {
            blueDisplay.setBackgroundColor(Color.GRAY);
            blueDisplay.setImageDrawable(BlueOff);
            blueDisplay.invalidate();
        }
    }

    private boolean isBluetoothEnabled(){
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        return bt != null && bt.isEnabled();
    }



    private void connectToServer(final String host, final String inPort) {
        final TextView status = (TextView) findViewById(R.id.status);
        final Button wifiLock = (Button) findViewById(R.id.wifiLock);
        final Button doorStatus = (Button)findViewById(R.id.doorStatus);
        new Thread(new Runnable()  {
            @Override
            public void run() {
                int port = Integer.parseInt(inPort);
                socket = createSocket(host, port);
                if (socket != null) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("Connected to Server");
                                wifiLock.setEnabled(true);
                                doorStatus.setEnabled(true);
                            }
                        });
                        readMessage(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Socket createSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (Exception e) {
            Log.d("TAG---", e.toString());
        }
        return null;
    }

    private void readMessage(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while (true) {
            final String msg = in.readLine();

            //must remember to save what you sent do miss stuff with those coordinates
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView status = (TextView) findViewById(R.id.status);
                    status.setText(msg);
                    checkMessage(msg);
                    checkLockDisplays();
                }
            });
        }
    }

    private void sendMessage(String msg) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println(msg);
        out.flush();
    }

    private void checkMessage(String messge){
        if(messge.equals("Door is currently UNLOCKED")){
            isLock = false;

        }else if(messge.equals("Door is currently LOCKED")){
            isLock = true;

        }
        //checkLockDisplays();


    }
}