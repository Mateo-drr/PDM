package com.example.pdmg2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BLE {
    private static final String TAG = " ";
    private static final int MAX_TRIES = 10;
    private boolean blockscan = false;
    private int nrTries = 0;
    private Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
    private boolean commandQueueBusy;
    private boolean isRetrying;
    private Runnable bluetoothCommand;
    private BluetoothGattCharacteristic characteristic;
    private List<BluetoothGattCharacteristic> charact;
    private List<BluetoothGattService> services;

    public String getS() {
        if (s != null) {
            //Read();
            return s;
        }else
            return "busy";
    }

    private String s;
    private boolean GOT_SERVICES = false;
    public BluetoothDevice getDevice() {
        return device;
    }
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
    };
    private final Handler bleHandler = new Handler(Looper.getMainLooper());
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    private Activity mact;
    private TextView rd;

    public BLE(Activity mact){
        this.mact = mact;
    };


    @SuppressLint("NewApi")
    private ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) //lowpower balanced lowlatency opportunistic
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(0)
            .build();

    //private BluetoothDevice device = bluetoothAdapter.getRemoteDevice("12:34:56:AA:BB:CC"); //MAC ADRESS TODO

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Scan() {
        // Check if the peripheral is cached or not
        //blockscan = false;

        //if (!blockscan) {
        if (device != null) {
            int deviceType = device.getType();
            if (deviceType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                Log.d(TAG, "The peripheral is not cached");
            } else {
                Log.d(TAG, "The peripheral is cached");
                BluetoothGatt gatt = device.connectGatt(mact, true, bluetoothGattCallback, TRANSPORT_LE);
            }
        }

        if (scanner != null) {
            String[] names = new String[]{"LoPy"};
            List<ScanFilter> filters = null;
            if (names != null) {
                filters = new ArrayList<>();
                for (String name : names) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setDeviceName(name)
                            .build();
                    filters.add(filter);
                }
            }


            scanner.startScan(filters, scanSettings, scanCallback); //filters, scansettings, callback

                /*if(blockscan){
                    scanner.stopScan(scanCallback);
                }

                 */

            Log.d(TAG, "scan started");
        } else {
            Log.e(TAG, "could not get scanner object");
        }
        //}
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult_in");

            //TextView t = findViewById(R.id.textView);
            //t.setText(result.toString());

            if (!result.toString().isEmpty()) {
                device = result.getDevice();
                Log.d(TAG, "IMHERE!" + device.getUuids() + device.getAlias() + device.getName());
                //false-> connect immediately,
                gatt = device.connectGatt(mact, false, myCallBack, TRANSPORT_LE);
                gatt.discoverServices();
                Log.d(TAG, "scan started");
                /*
                if (GOT_SERVICES) {
                    readCharacteristic(servicescopy.get(0).getCharacteristic(UUID.fromString("363531344-3231-3039-3837-363534336261")));
                }*/
            } else
                Log.d(TAG, "try again");

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

            /*//if(!blockscan) {
                // Ignore for now
                Log.d(TAG, "in2");
                //Log.d(TAG, String.valueOf(results.size()));

                StringBuilder builder = new StringBuilder();
                for (ScanResult details : results) {
                    builder.append(details + "\n");
                }


            //}*/

        }
        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }

    };

    private final BluetoothGattCallback myCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            //super.onConnectionStateChange(device, status, newState);
            if (status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "GATT_SUCCESS and STATE_CONNECTED " + device.getName());
                    int bondstate = device.getBondState();

                    // Take action depending on the bond state
                    if (bondstate == BOND_NONE || bondstate == BOND_BONDED) {
                        Log.d(TAG, "BONDSTATE 10/12 = " + bondstate);
                        // Connected to device, now proceed to discover it's services but delay a bit if needed

                        //DELAY -> NECESSARY FOR OLDER VERSIONS OF ANDRIOD
                        int delayWhenBonded = 1000;
                        /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                            delayWhenBonded = 1000;
                        }*/
                        final int delay;
                        //if (bondstate == BOND_BONDED)
                        delay = delayWhenBonded;
                        //else delay = 0;
                        //END DELAY

                        boolean result = gatt.discoverServices();
                        if (!result) {
                            Log.e(TAG, "discoverServices failed to start");
                            gatt.close();
                            gatt.disconnect();
                            return;
                        }

                        //boolean result = gatt.discoverServices();
                        Runnable discoverServicesRunnable = () -> {
                            //Log.d(TAG, "services discovery started");
                            Log.d(TAG, String.format(Locale.ENGLISH, "discovering services of '%s' with delay of %d ms", device.getName(), delay)); //?
                            //final List<BluetoothGattService> services = gatt.getServices();
                            services = gatt.getServices();

                            //ADITIONAL SERVICES PROCESSING
                            StringBuilder builder = new StringBuilder();
                            for (BluetoothGattService details : services) {
                                builder.append(details + "\n");
                            }
                            //TextView t2 = findViewById(R.id.textView2);
                            //t2.setText(builder.toString());
                            //END ADDITIONAL

                            Log.i(TAG, String.format(Locale.ENGLISH, "discovered %d services for '%s'", services.size(), device.getName()));

                            //SERVICES
                            //(0).getCharacteristic(UUID.fromString("363531344-3231-3039-3837-363534336261")));

                        };
                        bleHandler.postDelayed(discoverServicesRunnable, delay);

                    } else if (bondstate == BOND_BONDING) {
                        // Bonding process in progress, let it complete
                        //blockscan =true;
                        Log.i(TAG, "waiting for bonding to complete");
                        //do {
                        //Log.i(TAG, "waiting for bonding to complete");
                        //Log.i(TAG, String.format(Locale.ENGLISH,"discovered %d services for '%s'", services.size(), devv.getName()));
                        //}while(bondstate != BOND_BONDED || bondstate == BOND_BONDING);
                        //discoverServicesRunnable();
                        //Log.i(TAG, "bonding complete");
                    }
                }
            } else {
                // An error happened...figure out what happened!...
                gatt.close();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            // Perform some checks on the status field
            if (status != GATT_SUCCESS) {
                Log.e(TAG, String.format(Locale.ENGLISH, "ERROR: Read failed for characteristic: %s, status %d", characteristic.getUuid(), status));
                completedCommand();
                return;
            }
            Log.d(TAG, "completing command...");
            // Characteristic has been read so processes it...
            // We done, complete the command

            s = new String(characteristic.getValue(), StandardCharsets.UTF_8);
            Log.d(TAG, s);
            rd.setText(s);
            completedCommand();
        }
    };

    public boolean readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (gatt == null) {
            Log.e(TAG, "ERROR: Gatt is 'null', ignoring read request");
            return false;
        }

        // Check if characteristic is valid
        if (characteristic == null) {
            Log.e(TAG, "ERROR: Characteristic is 'null', ignoring read request");
            return false;
        }

        // Check if this characteristic actually has READ property
        if ((characteristic.getProperties() & PROPERTY_READ) == 0) {
            Log.e(TAG, "ERROR: Characteristic cannot be read");
            return false;
        }

        // Enqueue the read command now that all checks have been passed

        boolean result = commandQueue.add(new Runnable() {
            @Override
            public void run() {
                if(!gatt.readCharacteristic(characteristic)) {
                    Log.e(TAG, String.format("ERROR: readCharacteristic failed for characteristic: %s", characteristic.getUuid()));
                    completedCommand();
                } else {
                    Log.d(TAG, String.format("reading characteristic <%s>", characteristic.getUuid()));
                    nrTries++;
                }
            }
        });

        Log.d(TAG, "new queue runnable" + result);
        if (result) {
            nextCommand();
        } else {
            Log.e(TAG, "ERROR: Could not enqueue read characteristic command");
        }
        return result;
    }

    private void nextCommand() {
        // If there is still a command being executed then bail out
        if (commandQueueBusy) {
            Log.d(TAG, "busy");
            commandQueue.clear();
            commandQueueBusy = false;
            return;
        }

        // Check if we still have a valid gatt object
        if (gatt == null) {
            Log.e(TAG, String.format("ERROR: GATT is 'null' for peripheral '%s', clearing command queue", device.getAddress()));
            commandQueue.clear();
            commandQueueBusy = false;
            return;
        }

        // Execute the next command in the queue
        if (commandQueue.size() > 0) {
            final Runnable bluetoothCommand = commandQueue.peek();
            commandQueueBusy = true;
            nrTries = 0;

            bleHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bluetoothCommand.run();
                    } catch (Exception ex) {
                        Log.e(TAG, String.format("ERROR: Command exception for device '%s'", device.getName()), ex);
                    }
                }
            });

            commandQueue.clear();
            commandQueueBusy = false;
        }
    }

    private void completedCommand() {
        commandQueueBusy = false;
        isRetrying = false;
        commandQueue.poll();
        nextCommand();
    }

    private void retryCommand() {
        commandQueueBusy = false;
        Runnable currentCommand = commandQueue.peek();
        if (currentCommand != null) {
            if (nrTries >= MAX_TRIES) {
                // Max retries reached, give up on this one and proceed
                Log.v(TAG, "Max number of tries reached");
                commandQueue.poll();
            } else {
                isRetrying = true;
            }
        }
        nextCommand();
    }

    public void Write(String autosend){
        String wData = null;
        if(autosend == null) {
            TextView txtwrite = (TextView) this.mact.findViewById(R.id.txt_write);
            wData = txtwrite.getText().toString();
        }else{
            wData = autosend;
        }
        final byte[] bytesToWrite = wData.getBytes();
        Log.d(TAG, "onClickWrite");

        for (BluetoothGattService serv : services) {
            //get charact from each service
            charact = serv.getCharacteristics();
            for(BluetoothGattCharacteristic cc : charact){
                //check if theres one thats not null
                if (cc != null){
                    Log.d(TAG, cc.toString());
                    characteristic = cc;
                    break;
                }
            }
        }


        Log.d(TAG, String.valueOf(charact.size()) + characteristic.toString());


        characteristic.setValue(bytesToWrite);
        characteristic.setWriteType(WRITE_TYPE_DEFAULT);
        if (!gatt.writeCharacteristic(characteristic)) {
            Log.e(TAG, String.format("ERROR: writeCharacteristic failed for characteristic: %s", characteristic.getUuid()));
            completedCommand();
        } else {
            Log.d(TAG, String.format("writing <%s> to characteristic <%s>", wData, characteristic.getUuid()));
            nrTries++;
        }
    }

    public void Read() {
        if(characteristic != null){
            rd = (TextView)this.mact.findViewById(R.id.txtV_read);
            boolean started_read = readCharacteristic(characteristic);
            Log.d(TAG, "Start reading " + started_read);
            //gatt.readCharacteristic(characteristic);
            //if(!s.isEmpty()) {
                //TextView rd = (TextView)this.mact.findViewById(R.id.txtV_read);
                //rd.setText(s);
            //}
        }else
            Log.d(TAG, "characteristic null");
    }



}
