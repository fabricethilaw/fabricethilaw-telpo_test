package com.telpo.tps550.api.demo.iccard;

import java.util.HashMap;
import java.util.Iterator;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.idcard.IdCard;
import com.telpo.tps550.api.typea.TAInfo;
import com.telpo.tps550.api.typea.TASectorInfo;
import com.telpo.tps550.api.typea.UsbTypeA;
import com.telpo.tps550.api.util.ShellUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.UiAutomation.AccessibilityEventFilter;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class USBTypeACardActivity extends Activity implements OnClickListener {
	public static String TAG = "USBTypeACardActivity";
	Context mContext;
	Button btn_anthentic, btn_powerOn, btn_powerOFF, btn_connect, btn_readBlock, btn_writeBlock,btn_sendAPDU;
	EditText et_block, et_block_password, et_read_blockNo, et_write_block, et_cardNo;

	private UsbManager tUsbManager;
	private UsbDevice tcard_reader;
	private UsbTypeA treader;
	// private UsbTACard treader1;
	private PendingIntent tPermissionIntent;// related to usb
	private static final String TACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	// private UsbTypeA usbTACard;

	private byte[] Password;
	private byte[] Block;
	private byte[] Newwritedata;

	private TAInfo tinfo;
	private GetTAInfoTask tAsyncTask;
	ReadSectorDataTask readSectorDataTask;
	WriteDataTask writeSectorDataTask;
	private AuthenticateTask authenticateTask;
	private String newwritedata;
	TASectorInfo tsectorinfo;

	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usb_type_a);
		mContext = this;
		tPermissionIntent = PendingIntent.getBroadcast(USBTypeACardActivity.this, 0, new Intent(TACTION_USB_PERMISSION),
				0);
		initView();
	}

	private void initView() {
		btn_powerOn = (Button) findViewById(R.id.btn_powerOn);
		btn_powerOFF = (Button) findViewById(R.id.btn_powerOFF);
		btn_anthentic = (Button) findViewById(R.id.btn_authentic);
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_readBlock = (Button) findViewById(R.id.btn_readBlockNo);
		btn_writeBlock = (Button) findViewById(R.id.btn_writeBlockNo);
		btn_sendAPDU = (Button) findViewById(R.id.btn_sendAPDU);

		et_block = (EditText) findViewById(R.id.et_block);
		et_block_password = (EditText) findViewById(R.id.et_pass_block);
		et_read_blockNo = (EditText) findViewById(R.id.et_read_blockNO);
		et_write_block = (EditText) findViewById(R.id.et_write_blockNO);
		et_cardNo = (EditText) findViewById(R.id.et_cardNo);

		btn_powerOn.setOnClickListener(this);
		btn_powerOFF.setOnClickListener(this);
		btn_anthentic.setOnClickListener(this);
		btn_connect.setOnClickListener(this);
		btn_readBlock.setOnClickListener(this);
		btn_writeBlock.setOnClickListener(this);
		beforeReadCard();
	}
	
	private void beforeReadCard() {
		btn_anthentic.setEnabled(false);
		btn_readBlock.setEnabled(false);
		btn_writeBlock.setEnabled(false);
	}
	
	private void readCardSuccess() {
		et_read_blockNo.setText("");
		btn_anthentic.setEnabled(true);
		btn_readBlock.setEnabled(false);
		btn_writeBlock.setEnabled(false);
	}
	
	private void anthenticSuccess() {
		btn_anthentic.setEnabled(false);
		btn_readBlock.setEnabled(true);
		btn_writeBlock.setEnabled(true);
	}
	
	private void anthenticFailed() {
		btn_anthentic.setEnabled(false);
		btn_readBlock.setEnabled(false);
		btn_writeBlock.setEnabled(false);
	}

	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_powerOn:
			powerOn();
			break;
		case R.id.btn_powerOFF:
			powerOFF();
			break;
		case R.id.btn_sendAPDU:
			new Thread(new Runnable() {
				
				
				public void run() {
					// TODO Auto-generated method stub
					treader.sendAPDU(new byte[] {(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0x96,0x69,0x00,
							0x0A,(byte) 0x80,0x19,(byte) 0xA0,(byte) 0xA4,
							0x00,0x00,0x02,0x3F,0x00,(byte) 0xAA}, new byte[] {0x00,0x00,0x00});
				}
			}).start();
			
			break;
		case R.id.btn_connect:
			
			openUsbTACard();
			if (treader == null) {
				try {
					treader = new UsbTypeA(tUsbManager, tcard_reader);
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_authentic:
			authenticate();
			break;
		case R.id.btn_readCardNo:
			tAsyncTask = new GetTAInfoTask();
			tAsyncTask.execute();
			break;

		case R.id.btn_readBlockNo:
			readSectorDataTask = new ReadSectorDataTask();
			readSectorDataTask.execute();
			break;
		case R.id.btn_writeBlockNo:
			writeSectorDataTask = new WriteDataTask();
			writeSectorDataTask.execute();
			break;

		default:
			break;
		}

	}

	private void powerOn() {
		ShellUtils.execCommand("echo 2 > /dev/telpoio", false);// usb
		ShellUtils.execCommand("echo 3 > /sys/class/telpoio/power_status", false);// usb
		// PosUtil.setIdCardPower(1);
	}

	private void powerOFF() {
		ShellUtils.execCommand("echo 4 > /sys/class/telpoio/power_status", false);// usb
		// PosUtil.setIdCardPower(0);
	}

	private void openUsbTACard() {
		tUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> tdeviceHashMap = tUsbManager.getDeviceList();
		Iterator<UsbDevice> iterator = tdeviceHashMap.values().iterator();

		while (iterator.hasNext()) {
			UsbDevice tusbDevice = iterator.next();
			int tpid = tusbDevice.getProductId();
			int tvid = tusbDevice.getVendorId();
			if ((tpid == IdCard.READER_PID_SMALL && tvid == IdCard.READER_VID_SMALL)
					|| (tpid == IdCard.READER_PID_BIG && tvid == IdCard.READER_VID_BIG
							|| tpid == IdCard.READER_PID_WINDOWS && tvid == IdCard.READER_VID_WINDOWS)) {
				tcard_reader = tusbDevice;
				if (tUsbManager.hasPermission(tusbDevice)) {

					try {
						treader = new UsbTypeA(tUsbManager, tcard_reader);
						Toast.makeText(mContext, "USB授权成功", Toast.LENGTH_SHORT).show();
					} catch (TelpoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				} else {
					tUsbManager.requestPermission(tusbDevice, tPermissionIntent);// ***弹出对话框问是否允许的时�?
					try {
						treader = new UsbTypeA(tUsbManager, tcard_reader);
						Toast.makeText(mContext, "USB授权成功", Toast.LENGTH_SHORT).show();
					} catch (TelpoException e) {
						// TODO Auto-generated catch block
						Toast.makeText(mContext, "USB授权授权失败", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 读取卡号异步线程
	private class GetTAInfoTask extends AsyncTask<Void, Integer, Boolean> {

		
		protected void onPreExecute() {
			super.onPreExecute();
			tinfo = null;
			// start_time = System.nanoTime();
		}

		
		protected Boolean doInBackground(Void... arg) {
			try {
				tinfo = treader.checkTACard();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
			if (tinfo != null) {
				return true;
			}
			return false;
		}

		
		protected void onPostExecute(Boolean result) {
			try {
				super.onPostExecute(result);
				if (result) {
					et_cardNo.setText(tinfo.getNum());
					readCardSuccess();
					Toast.makeText(mContext, "读卡成功", Toast.LENGTH_SHORT).show();
				} else {
					et_cardNo.setText("读卡失败");
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	// 验证扇区密码
	public void authenticate() {

		String password = new String(et_block_password.getText().toString());
		Password = hexStringToBytes(password);
		String block = new String(et_block.getText().toString());
		Block = hexStringToBytes(block);
		if (tcard_reader != null) {
			if (et_block.getText() == null || et_block_password.getText().length() == 0) {
				Toast.makeText(mContext, "请输入扇区密码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (et_block.getText().toString() == null || et_block.getText().toString().length() == 0) {
				Toast.makeText(mContext, "请输入块区号", Toast.LENGTH_SHORT).show();
				return;
			}
			treader.transmitpassword(Password);
			treader.transmitblock(Block);
			// 开启异步，进行扇区密码验证
			authenticateTask = new AuthenticateTask();
			authenticateTask.execute();

		} else {
			Toast.makeText(mContext, "无法连接读卡器", Toast.LENGTH_SHORT).show();
		}
	}

	// 验证扇区密码
	private class AuthenticateTask extends AsyncTask<Void, Integer, Boolean> {

		
		protected void onPreExecute() {
			super.onPreExecute();
		}

		
		protected Boolean doInBackground(Void... arg1) {

			Boolean pwright = new Boolean(false);
			try {
				pwright = treader.checkPW();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
			return pwright;
		}

		
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				Toast.makeText(mContext, "验证成功", Toast.LENGTH_SHORT).show();
				anthenticSuccess();
			} else {
				Toast.makeText(mContext, "验证失败", Toast.LENGTH_SHORT).show();
				anthenticFailed();
			}
		}
	}

	// 读取块区数据
	private class ReadSectorDataTask extends AsyncTask<Void, Integer, Boolean> {

		
		protected void onPreExecute() {
			super.onPreExecute();
		}

		
		protected Boolean doInBackground(Void... arg1) {

			try {
				tsectorinfo = treader.readData();
				if (tsectorinfo != null) {
					return true;
				} else {
					return false;
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		}

		
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				et_read_blockNo.setText(tsectorinfo.getSectorData() + "");
			} else {
				Toast.makeText(mContext, "读取块区数据失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class WriteDataTask extends AsyncTask<Void, Integer, Boolean> {

		
		protected void onPreExecute() {
			super.onPreExecute();
		}

		
		protected Boolean doInBackground(Void... arg2) {
			newwritedata = et_write_block.getText().toString();
			Boolean writeright = new Boolean(false);
			try {
				writeright = treader.writeInData(newwritedata);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
			return writeright;
		}

		
		protected void onPostExecute(Boolean result) {
			try {
				super.onPostExecute(result);
				if (result == null) {
					return;
				} else if (result == true) {
					et_write_block.setText("写入成功");
				} else if (result == false) {
					if (newwritedata == null) {
						et_write_block.setText("写入数据不能为空");
					} else {
						et_write_block.setText("写入失败");
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

}
