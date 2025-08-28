package com.Android1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Android1 extends Activity {
    private BluetoothAdapter bluetoothAdapter;
    private static final String SIGNAL = "HelloBluetooth"; // 要发送的信号内容
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.textView);
        Button btnSendBluetooth = findViewById(R.id.btnSendBluetooth);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnSendBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    tv.setText("设备不支持蓝牙");
                } else {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                        tv.setText("蓝牙已打开，准备发送信号");
                        return;
                    }
                    // 查找已配对设备并发送信号
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            boolean sent = sendSignal(device, SIGNAL);
                            if (sent) {
                                tv.setText("信号已发送到: " + device.getName());
                                return;
                            }
                        }
                        tv.setText("未能发送信号到任何设备");
                    } else {
                        tv.setText("没有已配对的蓝牙设备");
                    }
                }
            }
        });
    }

    // 发送信号到指定蓝牙设备
    private boolean sendSignal(BluetoothDevice device, String signal) {
        BluetoothSocket socket = null;
        OutputStream outStream = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outStream = socket.getOutputStream();
            outStream.write(signal.getBytes());
            outStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (outStream != null) outStream.close();
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }
}
