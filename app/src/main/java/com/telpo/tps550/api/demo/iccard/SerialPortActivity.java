package com.telpo.tps550.api.demo.iccard;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.telpo.tps550.api.DeviceAlreadyOpenException;
import com.telpo.tps550.api.serial.Serial;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;
import android.util.Log;

/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class SerialPortActivity extends Activity {

    
    protected Serial mSerialPort;
    protected OutputStream mOutputStream;
    public InputStream mInputStream;
    protected ReadThread mReadThread;
    private static final String TAG = "SerialPortActivity";
    private int n = 0;

    class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];

                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size, n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SerialPortActivity.this.finish();
            }
        });
        b.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	int deviceType = SystemUtil.getDeviceType();
	        if(deviceType == StringUtil.DeviceModelEnum.TPS462.ordinal()
	        		||deviceType == StringUtil.DeviceModelEnum.TPS468.ordinal()) {
	        	mSerialPort = new Serial("/dev/ttyS3", 115200, 0);
	        }else if(deviceType == StringUtil.DeviceModelEnum.TPS390A.ordinal()){
	        	mSerialPort = new Serial("/dev/ttyMT0", 115200, 0);
	        }else {
	        	mSerialPort = new Serial("/dev/ttyS0", 115200, 0);
	        }
            
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            //DisplayError(R.string.error_configuration);
        } catch (DeviceAlreadyOpenException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size,final int n);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        mSerialPort = null;
        try
        {
            mOutputStream.close();
            mInputStream.close();
        } catch (IOException e) {
        }
    }
    
    protected void SerialProtReset(int Baudrate)
    {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        mSerialPort = null;
        try
        {
            mOutputStream.close();
            mInputStream.close();
        } catch (IOException e) {
        }

        try {
        	int deviceType = SystemUtil.getDeviceType();
        	if(deviceType == StringUtil.DeviceModelEnum.TPS462.ordinal()) {
        		mSerialPort = new Serial("/dev/ttyS3", Baudrate, 0);
	        }else if(deviceType == StringUtil.DeviceModelEnum.TPS390A.ordinal()){
	        	mSerialPort = new Serial("/dev/ttyMT0", Baudrate, 0);
	        }else {
	        	mSerialPort = new Serial("/dev/ttyS0", Baudrate, 0);
	        }
        	
        	//mSerialPort = new SerialPort(new File("/dev/ttyS0"), Baudrate, 0);//********************************************new file 
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            //DisplayError(R.string.error_configuration);
        } catch (DeviceAlreadyOpenException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Baudrate Change :" + Baudrate);
    }
}
