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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
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

/**
 * Classe BLE, tem todas as funções necessarias para estabelecer a ligação com um dispositivo
 * É necessaria versão minima marshmallow!
 * O codigo é baseado de: https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BLE implements Serializable {

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
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {};
    private final Handler bleHandler = new Handler(Looper.getMainLooper());
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
    private Activity mact;
    private TextView rd;

    /**
     * BLE constructor
     * @param mact
     */
    public BLE(Activity mact){
        this.mact = mact;
    };

    /**
     * BLE configurações da pesquisa
     * Modo de baixa latência
     * Está a procura de só um dispositivo
     */
    @SuppressLint("NewApi")
    private ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) //lowpower balanced lowlatency opportunistic
            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(0)
            .build();

    /**
     * Iniciar scan dos dispositvos BLE com as configurações definidas,
     * continua a conexão se encontra ao dispositivo "LoPy"
     */
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

    /**
     * Callback do Scan, é obtido o objeto BluetoothDevice e a seguir são pedidas seus serviços
     * onScanResult é utilizado por ter a configuração CALLBACK_TYPE_FIRST_MATCH
     */
    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "onScanResult_in");
            if (!result.toString().isEmpty()) {
                device = result.getDevice();
                Log.d(TAG, "IMHERE!" + device.getUuids() + device.getAlias() + device.getName());
                //false-> connect immediately,
                gatt = device.connectGatt(mact, false, myCallBack, TRANSPORT_LE);
                gatt.discoverServices();
                Log.d(TAG, "scan started");
            } else
                Log.d(TAG, "try again");
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {


        }
        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }
    };

    /**
     * Callback da descoberta dos serviços
     * É verificado o estado do BLuetoothGatt, logo é colocado um delay para o codigo dentro do runnable
     * Dentro do runnable são obtidos os serviços
     */
    private final BluetoothGattCallback myCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            if (status == GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "GATT_SUCCESS and STATE_CONNECTED " + device.getName());
                    int bondstate = device.getBondState();
                    // Take action depending on the bond state
                    if (bondstate == BOND_NONE || bondstate == BOND_BONDED) {
                        Log.d(TAG, "BONDSTATE 10/12 = " + bondstate);
                        // Connected to device, now proceed to discover it's services but delay a bit if needed
                        int delayWhenBonded = 1000;
                        final int delay;
                        delay = delayWhenBonded;
                        boolean result = gatt.discoverServices();
                        if (!result) {
                            Log.e(TAG, "discoverServices failed to start");
                            gatt.close();
                            gatt.disconnect();
                            return;
                        }
                        Runnable discoverServicesRunnable = () -> {
                            Log.d(TAG, String.format(Locale.ENGLISH, "discovering services of '%s' with delay of %d ms", device.getName(), delay)); //?
                            services = gatt.getServices();
                            //ADITIONAL SERVICES PROCESSING
                            StringBuilder builder = new StringBuilder();
                            for (BluetoothGattService details : services) {
                                builder.append(details + "\n");
                            }
                            Log.i(TAG, String.format(Locale.ENGLISH, "discovered %d services for '%s'", services.size(), device.getName()));
                        };
                        bleHandler.postDelayed(discoverServicesRunnable, delay);

                    } else if (bondstate == BOND_BONDING) {
                        Log.i(TAG, "waiting for bonding to complete");
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
            //rd.setText(s);
            completedCommand();
        }
    };

    /**
     * Método para ler as características do serviço selecionado
     * @param characteristic caraterística selecionada
     * @return
     */
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

    /**
     * Método para terminar a leitura das caraterísticas, continua no complete command
     */
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

    /**
     * Terminar a leitura ou escita dos dados
     */
    private void completedCommand() {
        commandQueueBusy = false;
        isRetrying = false;
        commandQueue.poll();
        nextCommand();
    }

    /*
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

     */

    /**
     * Método para escrever ao dispositivo utilizado nas diferentes activities, dentro do serviço
     * @param autosend string utilizada nas activities para enviar os comandos que
     *                 a seguir quando de ser lido do dispositivo ele devole o valor da grandeza correta
     *                 't' -> medir a temperatura
     *                 'h' -> medir a humidade
     *                 'l' -> medir luminosidade
     *                 'on' -> liga o led verde
     *                 'off' -> desliga o led
     */
    public void Write(String autosend){
        String wData = autosend;
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

    /**
     * Metodo para ler do dispositivo utilizado nas diferentes activities
     * Se a caraterística não é null chama a função readCharateristic
     */
    public void Read() {
        if(characteristic != null){
            boolean started_read = readCharacteristic(characteristic);
            Log.d(TAG, "Start reading " + started_read);
        }else
            Log.d(TAG, "characteristic null");
    }

    /**
     * Metodo para obter o valor lido previamente
     * @return String com os dados
     */
    public String getS() {
        if (s != null) {
            //Read();
            return s;
        }else
            return "busy";
    }
}
