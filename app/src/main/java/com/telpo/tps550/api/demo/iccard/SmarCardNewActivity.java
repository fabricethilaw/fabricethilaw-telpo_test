package com.telpo.tps550.api.demo.iccard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.demo.idcard.LogCatHelper;
import com.telpo.tps550.api.fingerprint.FingerPrint;
import com.telpo.tps550.api.magnetic.MagneticCard;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
import com.telpo.tps550.api.reader.SLE4428Reader;
import com.telpo.tps550.api.reader.SmartCardReader;
import com.telpo.tps550.api.util.PowerUtil;
import com.telpo.tps550.api.util.ShellUtils;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

public class SmarCardNewActivity extends Activity
{
	private SmartCardReader reader;
	SLE4428Reader readerOpen;
	private Button readButton, protocolbtn, buttonAPDU;
	
	private Button poweronButton;
	private Button poweroffButton;
	private Button openButton;
	private Button closeButton;
	private EditText mEditTextApdu;
	private TextView textReader;
	private LinearLayout psamlayout;
	ProgressDialog dialog;
	int deviceType;
	
	Handler handler = new Handler()
    {

		
		public void handleMessage(Message msg)
		{
            switch (msg.what) {
			case 1:
				if(openButton.isEnabled())
					openButton.performClick();
				handler.sendEmptyMessageDelayed(2, 3000);
				break;
			case 2:
				if(poweronButton.isEnabled())
					poweronButton.performClick();
				handler.sendEmptyMessageDelayed(3, 1000);
				break;
			case 3:
				if(readButton.isEnabled())
					readButton.performClick();
				handler.sendEmptyMessageDelayed(4, 1000);
				break;
			case 4:
				if(protocolbtn.isEnabled())
					protocolbtn.performClick();
				handler.sendEmptyMessageDelayed(5, 1000);
				break;
			case 5:
				if(buttonAPDU.isEnabled())
					buttonAPDU.performClick();
				handler.sendEmptyMessageDelayed(6, 1000);
				break;
			case 6:
				if(poweroffButton.isEnabled())
					poweroffButton.performClick();
				handler.sendEmptyMessageDelayed(7, 1000);
				break;
			case 7:
				if(closeButton.isEnabled())
					closeButton.performClick();
				handler.sendEmptyMessageDelayed(1, 1000);
				break;

			default:
				break;
			}
		}
    };
    
    boolean idcardPower;
    public void idcardPowerC10(View view){
    	idcardPower = !idcardPower;
    	if(idcardPower){
    		PowerUtil.c10ioctl(1, 1);
    	}else{
    		PowerUtil.c10ioctl(1, 0);
    	}
    }
    
    boolean qrcodePower;
    public void qrcodePowerC10(View view){
    	qrcodePower = !qrcodePower;
    	if(qrcodePower){
    		PowerUtil.c10ioctl(3, 1);
    	}else{
    		PowerUtil.c10ioctl(3, 0);
    	}
    }
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.smartcard);
		
		deviceType = SystemUtil.getDeviceType();
		
		mEditTextApdu = (EditText) findViewById(R.id.editTextAPDU);
		textReader = (TextView) findViewById(R.id.textReader);
		psamlayout = (LinearLayout) findViewById(R.id.select_psam);
		psamlayout.setVisibility(View.GONE);
		
		reader = new SmartCardReader(SmarCardNewActivity.this);
		
		OnClickListener listener = new OnClickListener()
		{
			
			
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.open_btn:
					Log.d("tagg", "opentask");
					new openTask().execute();
					break;
					
				case R.id.close_btn:
					if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal()) {
						FingerPrint.fingericPower(0);
					}
					if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
						reader.close(1);
					}else {
						reader.close();
					}
					closeButton.setEnabled(false);
					openButton.setEnabled(true);
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(false);
					readButton.setEnabled(false);
					protocolbtn.setEnabled(false);
					buttonAPDU.setEnabled(false);
					textReader.setText("");
					break;
					
				case R.id.read_btn:
					String atrString = null;
					if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
						atrString = reader.getATRString(1);
					}else {
						atrString = reader.getATRString();
					}
					textReader.setText("ATR:" + (TextUtils.isEmpty(atrString.trim()) ? "null" : atrString));
					Toast.makeText(SmarCardNewActivity.this, getString(R.string.get_data_success), Toast.LENGTH_SHORT).show();
					break;
					
				case R.id.poweron_btn:
					if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
						if (reader.iccPowerOn(1))
						{
							poweronButton.setEnabled(false);
							poweroffButton.setEnabled(true);
							readButton.setEnabled(true);
							protocolbtn.setEnabled(true);
							buttonAPDU.setEnabled(true);
						}
						else 
						{
							Toast.makeText(SmarCardNewActivity.this, "ICC power on failed", Toast.LENGTH_SHORT).show();
						}
					}else {
						if (reader.iccPowerOn())
						{
							poweronButton.setEnabled(false);
							poweroffButton.setEnabled(true);
							readButton.setEnabled(true);
							protocolbtn.setEnabled(true);
							buttonAPDU.setEnabled(true);
						}
						else 
						{
							Toast.makeText(SmarCardNewActivity.this, "ICC power on failed", Toast.LENGTH_SHORT).show();
						}
					}
					
					break;
					
				case R.id.poweroff_btn:
					if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
						reader.iccPowerOff(1);
					}else {
						reader.iccPowerOff();
					}
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(true);
					readButton.setEnabled(false);
					protocolbtn.setEnabled(false);
					buttonAPDU.setEnabled(false);
					break;
				case R.id.protocol_btn:
					int proto;
					if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
						proto = reader.getProtocol(1);
					}else {
						proto = reader.getProtocol();
					}
					if (proto == SmartCardReader.PROTOCOL_T0) {
						textReader.setText("protocol: T0");
					} else if (proto == SmartCardReader.PROTOCOL_T1) {
						textReader.setText("protocol: T1");
					} else {
						textReader.setText("protocol: NA");
					}
					Toast.makeText(SmarCardNewActivity.this, getString(R.string.get_protocol_success), Toast.LENGTH_SHORT).show();

					break;
				default:
					break;
				}
			}
		};		
		
		readButton = (Button)findViewById(R.id.read_btn);
		readButton.setOnClickListener(listener);
		readButton.setEnabled(false);
		poweronButton = (Button)findViewById(R.id.poweron_btn);
		poweronButton.setOnClickListener(listener);
		poweronButton.setEnabled(false);
		poweroffButton = (Button)findViewById(R.id.poweroff_btn);
		poweroffButton.setOnClickListener(listener);
		poweroffButton.setEnabled(false);
		openButton = (Button)findViewById(R.id.open_btn);
		openButton.setOnClickListener(listener);
		closeButton = (Button)findViewById(R.id.close_btn);
		closeButton.setOnClickListener(listener);
		closeButton.setEnabled(false);
		protocolbtn = (Button) findViewById(R.id.protocol_btn);
		protocolbtn.setOnClickListener(listener);
		protocolbtn.setEnabled(false);
		buttonAPDU = (Button) findViewById(R.id.buttonAPDU);
		buttonAPDU.setEnabled(false);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(reader!=null){
			reader.close();
		}
    	onDestroy();
	}

	private class openTask extends AsyncTask<Void, Integer, Boolean>
	{
		String internalModel;

		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal() || 
					deviceType == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
				dialog = new ProgressDialog(SmarCardNewActivity.this);
				dialog.setMessage("please wait ...");
				dialog.setCancelable(false);
				dialog.show();
			}
			internalModel = SystemUtil.getInternalModel();
			if("C10P".equals(internalModel) || "C10Y".equals(internalModel) || "C10T".equals(internalModel)){
				openButton.setEnabled(false);
			}
		}
		
		
		protected Boolean doInBackground(Void... params)
		{
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal()) {
				FingerPrint.fingericPower(1);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal() || 
					deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
				return reader.open(1);
			}else {
				return reader.open();
			}
		}
		
		
		protected void onPostExecute(Boolean result)
		{
			if(deviceType == StringUtil.DeviceModelEnum.TPS450C.ordinal() || 
					deviceType == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
				dialog.dismiss();
			}
			if (result)
			{
				openButton.setEnabled(false);
				closeButton.setEnabled(true);
				poweronButton.setEnabled(true);
			}
			else 
			{
				Toast.makeText(SmarCardNewActivity.this, "Open reader failed", Toast.LENGTH_SHORT).show();
				if("C10P".equals(internalModel) || "C10Y".equals(internalModel) || "C10T".equals(internalModel)){
					openButton.setEnabled(true);
				}
			}
		}
	}
	

	public void sendAPDUkOnClick(View view) {
		byte[] pSendAPDU;
		byte[] result;
		int[] pRevAPDULen = new int[1];
		String apduStr;
		pRevAPDULen[0] = 300;
		apduStr = mEditTextApdu.getText().toString();
		pSendAPDU = toByteArray(apduStr);
		if(deviceType == StringUtil.DeviceModelEnum.TPS360IC.ordinal()) {
			result = reader.transmit(pSendAPDU,1);
		}else {
			result = reader.transmit(pSendAPDU);
		}
		textReader.setText(TextUtils.isEmpty(StringUtil.toHexString(result)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(result));
		if (!TextUtils.isEmpty(StringUtil.toHexString(result))) {
			Toast.makeText(SmarCardNewActivity.this, getString(R.string.send_comm_success), Toast.LENGTH_SHORT).show();
		}
	}
	
	private String BCD2Str(byte[] data)
	{
		String string;
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < data.length; i++)
		{
			string = Integer.toHexString(data[i] & 0xFF);
			if (string.length() == 1)
			{
				stringBuilder.append("0");
			}
			
			stringBuilder.append(string.toUpperCase());
			stringBuilder.append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	private byte[] str2BCD(String string)
	{
		int len;
		String str;
		String hexStr = "0123456789ABCDEF";
		
		String s = string.toUpperCase();
		
		len = s.length();
		if ((len % 2) == 1)
		{
			// The length is not an even number, right complement 0
			str = s + "0";
			len = (len + 1) >> 1;
		}
		else 
		{
			str = s;
			len >>= 1;
		}
		
		byte[] bytes = new byte[len];
		byte high;
		byte low;
		
		for (int i = 0, j = 0; i < len; i++, j += 2)
		{
			high = (byte)(hexStr.indexOf(str.charAt(j)) << 4);
			low = (byte)hexStr.indexOf(str.charAt(j + 1));
			bytes[i] = (byte)(high | low);
		}
		
		return bytes;
	}

	public static byte[] toByteArray(String hexString) {

		int hexStringLength = hexString.length();
		byte[] byteArray = null;
		int count = 0;
		char c;
		int i;

		// Count number of hex characters
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
				count++;
			}
		}

		byteArray = new byte[(count + 1) / 2];
		boolean first = true;
		int len = 0;
		int value;
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9') {
				value = c - '0';
			} else if (c >= 'A' && c <= 'F') {
				value = c - 'A' + 10;
			} else if (c >= 'a' && c <= 'f') {
				value = c - 'a' + 10;
			} else {
				value = -1;
			}

			if (value >= 0) {

				if (first) {

					byteArray[len] = (byte) (value << 4);

				} else {

					byteArray[len] |= value;
					len++;
				}

				first = !first;
			}
		}

		return byteArray;
	}
	
	ReadThread readThread;
	private class ReadThread extends Thread {
    	
    	String[] TracData = null;
    	
		
		public void run() {
			MagneticCard.startReading();
			while (!Thread.interrupted()) {
				try{
					TracData = MagneticCard.check(5000);
					runOnUiThread(new Runnable() {
						
						
						public void run() {
							// TODO Auto-generated method stub
							StringBuffer buffer = new StringBuffer();
							for(int i=0; i<3; i++){
			                    if(TracData[i] != null){
			                    	buffer.append(TracData[i]+";");
			                    }
			                }
							Toast.makeText(SmarCardNewActivity.this, buffer.toString(), Toast.LENGTH_LONG).show();
						}
					});
					int ret = MagneticCard.startReading();
				}catch (TelpoException e){
					e.printStackTrace();
				}
			}
		}
    }
	
}
