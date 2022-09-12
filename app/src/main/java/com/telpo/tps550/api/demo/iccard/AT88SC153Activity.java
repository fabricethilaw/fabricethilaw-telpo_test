package com.telpo.tps550.api.demo.iccard;

import android.app.Activity;

import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.reader.CardReader;
import com.telpo.tps550.api.util.ReaderUtils;
import com.telpo.tps550.api.util.StringUtil;

import amlib.ccid.Reader;
import amlib.ccid.ReaderAT88SC153;
import amlib.ccid.ReaderException;
import amlib.ccid.SCError;
import amlib.hw.HWType;
import amlib.hw.HardwareInterface;
import amlib.hw.ReaderHwException;

public class AT88SC153Activity extends Activity {
	private static final String TAG = "AT88SC153Activity";
	private String mStrMessage;
	private ReaderAT88SC153 m88sc153;
	private HardwareInterface mMyDev;
	private UsbDevice mUsbDev;
	private UsbManager mManager;
	private PendingIntent mPermissionIntent;

	private RadioGroup radioZoneGroup;
	private RadioGroup radioIndexGroup;
	private RadioButton radio_write;
	private RadioButton radio_read;
	private long selectZone = 0L;
	private long indexGroup = 0L;

	private EditText mTextViewResult;

	private EditText mEdtAddrEEPRead;
	private EditText mEdtLenEEPRead;
	private EditText mEdtAddrEEPWrite;
	private EditText mEdtDataEEPWrite;
	private EditText mEdtVerifyPw1;
	private EditText mEdtVerifyPw2;
	private EditText mEdtVerifyPw3;
	
	private ArrayAdapter<String> mZoneAdapter;
	private ArrayAdapter<String> mIndexAdapter;
	private Spinner mZoneSpinner;
	private Spinner mIndexSpinner;
	private static final String ACTION_USB_PERMISSION = "com.alcorlink.smartcardapp.USB_PERMISSION";
	protected static final boolean VERIFY_TYPE_WRITE = false;
	protected static final boolean VERIFY_TYPE_READ = true;
	private static final int MAX_WRITE_LENGTH = 256;
	private String[] addrHex = new String[] {"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
											 "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
											 "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
											 "30"/*写密码剩余次数*/,"31","32","33","34"/*读密码剩余次数*/,"35","36","37","38","39","3A","3B","3C","3D","3E","3F"};
	
	private String readCount = null;//验证正确为FF
	private String writeCount = null;//验证正确为FF
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at88sc153);
		initView();
		CardReader cardReader = new CardReader(AT88SC153Activity.this);
		mUsbDev = cardReader.getAT88SC153Device();
		setupViews();
		try {
			mMyDev = new HardwareInterface(HWType.eUSB, this.getApplicationContext());

		}catch(Exception e){
			mStrMessage = "Get Exception : " + e.getMessage();
			Log.e(TAG, mStrMessage);
			return;
		}
		// Get USB manager
		mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		toRegisterReceiver();
		if (mUsbDev == null)
			mTextViewResult.setText("No Device been selected");
		
		radio_write.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEdtVerifyPw1.setText("");
				mEdtVerifyPw2.setText("");
				mEdtVerifyPw3.setText("");
			}
		});
		radio_read.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEdtVerifyPw1.setText("40");
				mEdtVerifyPw2.setText("57");
				mEdtVerifyPw3.setText("88");
			}
		});
	}

	private void initView() {
		
		mEdtAddrEEPRead = (EditText) findViewById(R.id.addr_edt);
		mEdtLenEEPRead = (EditText) findViewById(R.id.num_edt);
		mTextViewResult = (EditText)findViewById(R.id.read_result_edt);
		mEdtAddrEEPWrite = (EditText)findViewById(R.id.write_addr_edt);
		mEdtDataEEPWrite = (EditText)findViewById(R.id.write_content_edt);
		mEdtVerifyPw1 = (EditText) findViewById(R.id.psc_edt1);
		mEdtVerifyPw2 = (EditText) findViewById(R.id.psc_edt2);
		mEdtVerifyPw3 = (EditText) findViewById(R.id.psc_edt3);
		radio_write = (RadioButton) findViewById(R.id.radioWrite);
		radio_read = (RadioButton) findViewById(R.id.radioRead);
		
		radioZoneGroup = (RadioGroup) findViewById(R.id.radioZoneGroup);
		radioZoneGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
					switch (checkedId) {
			        case R.id.zone_0:
			            selectZone = 0L;
			            break;
			        case R.id.zone_1:
			        	selectZone = 1L;
			            break;
			        case R.id.zone_2:
			        	selectZone = 2L;
			            break;
			        case R.id.zone_config:
			            selectZone = 3L;
			            break;
			        default:
			            break;
			        }
				}
			});
		
		
		radioIndexGroup = (RadioGroup) findViewById(R.id.radioIndexGroup);
		radioIndexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
					switch (checkedId) {
			        case R.id.radioIndex0:
			        	indexGroup = 0L;
			            break;
			        case R.id.radioIndex1:
			        	indexGroup = 1L;
			            break;
			        default:
			            break;
			        }
				}
			});
		}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			try {
				closeReaderUp();
				mMyDev.Close();
			} catch (Exception e) {
				mStrMessage = "Get Exception : " + e.getMessage();
				//mTextViewResult.setText( mStrMessage);
			}
			AT88SC153Activity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	//Button open
	public void openReader(View view){
		if (mUsbDev != null)
			mManager.requestPermission(mUsbDev, mPermissionIntent);
		else
		{
			Log.e(TAG,"selected not found");
			mTextViewResult.setText("Device not found");
		}
	}
	
	//Button close
	public void closeReader(View view) {
		new CloseTask().execute();
	}

	//Button powerOff
	public void powerOff(View view){
		int ret;
		ret =	poweroff();
		if (ret == SCError.READER_SUCCESSFUL)
			mTextViewResult.setText("power off successfully");
		else if (SCError.maskStatus(ret) == SCError.READER_NO_CARD){
			mTextViewResult.setText("Card Absent");
		}
		else
		{
            showResult("PowerOff", ret);
		}
	}
	//Button powerOn
	public void powerOn(View view) {
		int ret;
		ret =	poweron();
		if (ret == SCError.READER_SUCCESSFUL)
		{
			mTextViewResult.setText("power on successfully");
			
		}
		else if (SCError.maskStatus(ret) == SCError.READER_NO_CARD){
			mTextViewResult.setText("Card Absent");
		}
		else
		{
            showResult("PowerOn", ret);
		}
	}

	//Button write
	public void writeData(View view){
		
		byte []pAddr = new byte[1];
		byte []pWrite = new byte[MAX_WRITE_LENGTH];
		int []pIntReturnLen = new int[1];

		int result;
		byte bZone;
		bZone = (byte) getSpinnerSelect(selectZone);
		//get addr
		result = getEditTextHex(mEdtAddrEEPWrite, pAddr, 2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		//get data
		result = getEditTextHex(mEdtDataEEPWrite, pWrite, MAX_WRITE_LENGTH*2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}


		byte writeLen = (byte)pIntReturnLen[0];
		Log.e("TAG", "pAddr[0]"+pAddr[0]);
		result = m88sc153.AT88SC153Cmd_WriteEeprom(
				bZone,
				pAddr[0],
				writeLen,
				pWrite );
		showResult("WriteEEPROM", result);
	}
	
	public void writeCircle(View view) {
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				write_circle(mEdtAddrEEPWrite.getText().toString(), mEdtDataEEPWrite.getText().toString());
				
				//write_circle("00", "00000000FF00000018031208203773240A0063630000214E1803120820377300371030313030353930303338120000F3000000000000000000000000FF000000");
				
				runOnUiThread(new Runnable() {
					
					
					public void run() {
						// TODO Auto-generated method stub
						mTextViewResult.setText("write over");
					}
				});
			}
		}).start();
	}
	
	private void write_circle(String writeAddr,String writeHex) {
		if((writeHex.length()%2)!=0) {
			mTextViewResult.setText("data.length wrong");
			return;
		}
		int addrIndex = 0;
		for(int i=0;i<addrHex.length;i++) {
			if(writeAddr.equals(addrHex[i])) {
				addrIndex = i;
				break;
			}
		}
		Log.d(TAG, "addrHex[addrIndex]:"+addrHex[addrIndex]);
		String[] hexPosition = new String[writeHex.length()/2];
		for(int i=0;i<writeHex.length()/2;i++) {
			hexPosition[i] = writeHex.substring(2*i,2*i+2);
		}
		StringBuffer sBuffer = new StringBuffer();
		for(int i=0;i<hexPosition.length;i++) {
			sBuffer.append(hexPosition[i]+"["+i+"],");
		}
		Log.d(TAG, "---------hexPosition:"+sBuffer.toString());
		for(int i=0;i<writeHex.length()/2;i++) {
			byte []pAddr = new byte[1];
			byte []pWrite = new byte[MAX_WRITE_LENGTH];
			int []pIntReturnLen = new int[1];
			int result;
			byte bZone;
			bZone = (byte) getSpinnerSelect(selectZone);
			
			//get addr
			result = getEditTextHex(addrHex[addrIndex+i], pAddr, 2, pIntReturnLen);
			if (result != 0)
			{
				return;
			}
			//get data
			result = getEditTextHex(hexPosition[i], pWrite, MAX_WRITE_LENGTH*2, pIntReturnLen);
			if (result != 0)
			{
				return;
			}
			byte writeLen = (byte)pIntReturnLen[0];
			Log.e(TAG, "pAddr[0]"+pAddr[0]);
			result = m88sc153.AT88SC153Cmd_WriteEeprom(
					bZone,
					pAddr[0],
					writeLen,
					pWrite );
		}
	}

	//Button readData
	public void readData(View view){
		
		byte []pAddr = new byte[1];
        byte []pReadLen = new byte[1];
		byte []pReadData = new byte[MAX_WRITE_LENGTH];
		int []pIntReturnLen = new int[1];
		
		int result;
		byte bZone;
		bZone = (byte) getSpinnerSelect(selectZone);
		
		//get addr
		result = getEditTextHex(mEdtAddrEEPRead, pAddr, 2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		//get length
		result = getEditTextHex(mEdtLenEEPRead, pReadLen, 3,pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		
		result = m88sc153.AT88SC153Cmd_ReadEeprom(
				bZone,
				pAddr[0],
				(byte)pReadLen[0],
				pReadData,
				pIntReturnLen);

		if (result == 0)
		{
			Log.d(TAG,"result, pIntReturnLen="+pIntReturnLen[0]);
			//shows data
			showBufResult(pIntReturnLen[0], pReadData);
		}
		else
			showResult("ReadEEPROM", result);
	}

	//Button verify password
	public void verifyPassword(View view){
		
		boolean function;
		byte bPwIndex = 0;
		int []pIntReturnLen = new int[1];
		int result ;
		byte []pPw1 = new byte[1];
		byte []pPw2 = new byte[1];
		byte []pPw3 = new byte[1];

		/*To ge tindex*/
		function = getRadioRWSelect();/*true for read*/
		bPwIndex = (byte) getSpinnerSelect(indexGroup);
		result = getEditTextHex(mEdtVerifyPw1, pPw1, 2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		result = getEditTextHex(mEdtVerifyPw2, pPw2, 2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		result = getEditTextHex(mEdtVerifyPw3, pPw3, 2, pIntReturnLen);
		if (result != 0)
		{
			return;
		}
		if (function == true)
			Log.d(TAG, "READ");
		else
			Log.d(TAG, "Write");
		Log.d(TAG, Integer.toHexString(0x000000ff&pPw1[0]) +"-"+ Integer.toHexString(0x000000ff&pPw2[0])+"-"+Integer.toHexString(0x000000ff&pPw3[0]) + "pw-"+bPwIndex);
		result = m88sc153.AT88SC153Cmd_VerifyPassword(bPwIndex, function, pPw1[0], pPw2[0], pPw3[0]);
		if(radio_read.isChecked()) {
			readCount = readDataAll(3L).substring(104, 106);
			if(readCount.equals("FF"))
				result = 0;
			else
				result = 2;
		}else {
			writeCount = readDataAll(3L).substring(96, 98);
			if(writeCount.equals("FF"))
				result = 0;
			else
				result = 2;
		}
		
		showResult("VerifyPassWd", result);
	}

	
	protected void onDestroy() {
		Log.e(TAG,"onDestroy");
		unregisterReceiver(mReceiver);
		if (mMyDev != null)
		{
			mMyDev.Close();
		}
		super.onDestroy();
	}

	private int closeReaderUp(){
		Log.d(TAG, "Closing reader...");
		int ret = 0;

		if (m88sc153 != null)
		{
			ret = m88sc153.close();
		}
		return ret;
	}

	private class CloseTask extends AsyncTask <Void, Void, Integer> {

		
		protected void onPreExecute() {
			
		}
		
		
		protected Integer doInBackground(Void... params) {
			int status = 0;
			try {
				do{
					status = closeReaderUp();
				}while(SCError.maskStatus(status) == SCError.READER_CMD_BUSY);

			} catch (Exception e) {
				mStrMessage = "Get Exception : " + e.getMessage();
				//mTextViewResult.setText( mStrMessage);
				return -1;
			}

			return status;
		}

		
		protected void onPostExecute(Integer result) {
			if (result != 0) {
				mTextViewResult.setText("Close fail: "+ SCError.errorCode2String(result) +". " + mStrMessage);
				Log.e(TAG, "Close fail: "+ SCError.errorCode2String(result));
			}else{
				mTextViewResult.setText("Close successfully");
				Log.e(TAG,"Close successfully");
			}
			mMyDev.Close();
		}
	}

	//EditText 字符转十六进制
	private int getEditTextHex(EditText edt, byte []pBuf, int length, int []pReturnLen)
	{
		byte []pByteArrary;
		String strText;
		int len;
		strText = edt.getText().toString();
		len = strText.length() ;
		Log.d(TAG, "get length="+len);
		if (len > length || len == 0 || (len%2 !=0))
		{
			mStrMessage = "Wrong length in text Fields ";
			Log.e(TAG, mStrMessage);
			mTextViewResult.setText(mStrMessage);
			return -1;
		}
		pByteArrary = toByteArray(strText);//EditeText的十六进制字符串转byte[]
		Log.d(TAG, "pByteArrary length="+pByteArrary.length);
		//把EditeText的十六进制字符串转byte[]后复制到pBuf
		System.arraycopy(pByteArrary, 0, pBuf, 0, pByteArrary.length);
		pReturnLen[0] = pByteArrary.length;
		Log.d(TAG, "pBuf[0]=0x"+Integer.toHexString(pBuf[0]));
		return 0;
	}
	
	private int getEditTextHex(String writeAddr, byte []pBuf, int length, int []pReturnLen)
	{
		byte []pByteArrary;
		String strText;
		int len;
		strText = writeAddr;
		len = strText.length() ;
		Log.d(TAG, "get length="+len);
		if (len > length || len == 0 || (len%2 !=0))
		{
			mStrMessage = "Wrong length in text Fields ";
			Log.e(TAG, mStrMessage);
			mTextViewResult.setText(mStrMessage);
			return -1;
		}
		pByteArrary = toByteArray(strText);//EditeText的十六进制字符串转byte[]
		Log.d(TAG, "pByteArrary length="+pByteArrary.length);
		//把EditeText的十六进制字符串转byte[]后复制到pBuf
		System.arraycopy(pByteArrary, 0, pBuf, 0, pByteArrary.length);
		pReturnLen[0] = pByteArrary.length;
		Log.d(TAG, "pBuf[0]=0x"+Integer.toHexString(pBuf[0]));
		return 0;
	}

	private boolean getRadioRWSelect()
	{
		if (radio_write.isChecked() == true) {
			return VERIFY_TYPE_WRITE;
		}
		else {
			return VERIFY_TYPE_READ;
		}
	}

	private int getSpinnerSelect(long zoneNum){
		long index;
		index = zoneNum;
		return (int)index;
	}

	private int getSlotStatus(){
		int ret = SCError.READER_NO_CARD;
		byte []pCardStatus = new byte[1];

		/*detect card hotplug events*/
		ret = m88sc153.getCardStatus(pCardStatus);
		if (ret == SCError.READER_SUCCESSFUL) {
			if (pCardStatus[0] == Reader.SLOT_STATUS_CARD_ABSENT) {
				ret = SCError.READER_NO_CARD;
			} else if (pCardStatus[0] == Reader.SLOT_STATUS_CARD_INACTIVE) {
				ret = SCError.READER_CARD_INACTIVE;
			} else
				ret = SCError.READER_SUCCESSFUL;
		}

		return ret;
	}

	private int InitReader()
	{
		int Status = 0;
		boolean init;//
		Log.d(TAG, "InitReader");
		try {
			init = mMyDev.Init(mManager, mUsbDev);
			if (!init){
				Log.e(TAG, "Device init fail");
				return -1;
			}
		} catch (ReaderHwException e) {
			mStrMessage = "Get Exception : " + e.getMessage();
			return -1;
		}

		try {
			m88sc153 = new ReaderAT88SC153(mMyDev);
			Status = m88sc153.open();
		}
		catch (ReaderException e)
		{
			mStrMessage = "Get Exception : " + e.getMessage();
			Log.d(TAG, "InitReader fail "+ mStrMessage);
			return -1;
		}
		catch (IllegalArgumentException e)
		{
			mStrMessage = "Get Exception : " + e.getMessage();
			Log.d(TAG, "InitReader fail "+ mStrMessage);
			return -1;
		}

		return Status;
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Broadcast Receiver");

			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							onDevPermit(device);
						}
					} else {
						Log.d(TAG, "Permission denied for device " + device.getDeviceName());
					}
				}

			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

				Log.d(TAG, "Device Detached");
				onDetach(intent);

				synchronized (this) {
				}
			}
		}/*end of onReceive(Context context, Intent intent) {*/
	};
	
	private class OpenTask extends AsyncTask <UsbDevice, Void, Integer> {

		
		protected Integer doInBackground(UsbDevice... params) {
			int status = 0;
			status = InitReader() ;
			if ( status != 0){
				Log.e(TAG, "fail to initial reader");
				return status;
			}

			return status;
		}

		
		protected void onPostExecute(Integer result) {
			if (result != 0) {
				mTextViewResult.setText("Open fail: "+ SCError.errorCode2String(result)+". " + mStrMessage);
				Log.e(TAG,"Open fail: "+ SCError.errorCode2String(result)+". " + mStrMessage);
			}else{
				mTextViewResult.setText("Open successfully");
				Log.e(TAG,"Open successfully");
				//onOpenButtonSetup();
			}
		}
	}

	private void onDevPermit(UsbDevice dev){
		try {
			new OpenTask().execute(dev);
		}
		catch(Exception e){
			mStrMessage = "Get Exception : " + e.getMessage();
			Log.e(TAG, mStrMessage);
		}
	}

	private void onDetach(Intent intent){
		UsbDevice   udev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if (udev != null ) {
			if (udev.equals(mUsbDev) ){
				closeReaderUp();
				mMyDev.Close();
				AT88SC153Activity.this.finish();
			}
		}
		else {
			Log.d(TAG,"usb device is null");
		}

	}

	private int poweron(){
		int result = SCError.READER_SUCCESSFUL;
		Log.d(TAG,"poweron");
		//check slot status first
		result = getSlotStatus();
		switch (result)
		{
		case SCError.READER_NO_CARD:
			mTextViewResult.setText("Card Absent");
			Log.d(TAG,"Card Absent");
			return SCError.READER_NO_CARD;
		case SCError.READER_CARD_INACTIVE:
		case SCError.READER_SUCCESSFUL:
			break;
		default://returns other error case
			return result;
		}

		result = m88sc153.setPower(Reader.CCID_POWERON);
		Log.d(TAG,"power on exit");
		return result;
	}

	private int poweroff(){
		int result =  SCError.READER_SUCCESSFUL;
		Log.d(TAG,"poweroff");
		result = getSlotStatus();
		switch (result)
		{
		case SCError.READER_NO_CARD:
			mTextViewResult.setText("Card Absent");
			Log.d(TAG,"Card Absent");
			return SCError.READER_NO_CARD;
		case SCError.READER_CARD_INACTIVE:
		case SCError.READER_SUCCESSFUL:
			break;
		default://returns other error case
			return result;
		}
		//----------poweroff card------------------
		result = m88sc153.setPower(Reader.CCID_POWEROFF);

		return result;
	}

	public void setupViews(){
		Log.d(TAG,"setupViews"); 
		radio_read.setChecked(true);
	}

	private void showBufResult(int len, byte[]pBuf) {
		String s;
		s = byte2String(pBuf, len);
		mTextViewResult.setText(s); 
		Log.d(TAG, allData());
	}

	private void showResult(String cmd, int result)
	{
		String  msg, ccidErrCode;
		if (result == SCError.READER_SUCCESSFUL)
		{
			mTextViewResult.setText(cmd + " Success");
			return;
		}

		msg = new String("Fail:"+SCError.errorCode2String(result)+ "("+Integer.toHexString(result)+")");

        if (SCError.maskStatus(result) == SCError.READER_CMD_FAIL)
		{
			ccidErrCode = Integer.toHexString(m88sc153.getCmdFailCode() & 0x0000ffff);
			msg = cmd + " " +msg + "(" + ccidErrCode +")";
		}
        mTextViewResult.setText(msg);
	}
	
	private void toRegisterReceiver(){
		// Register receiver for USB permission
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mReceiver, filter);
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
			if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f') {
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
	
	public static String byte2String(byte[] buffer, int bufferLength) {

		String bufferString = "";
		String dbgString = "";

		for (int i = 0; i < bufferLength; i++) {

			String hexChar = Integer.toHexString(buffer[i] & 0xFF);
			if (hexChar.length() == 1) {
				hexChar = "0" + hexChar;
			}

			if (i % 16 == 0) {
				if (dbgString != "") {
					//	                    Log.d(LOG_TAG, dbgString);
					bufferString += dbgString;
					dbgString = "\n";
				}
			}

			dbgString += hexChar.toUpperCase() + " ";
		}

		if (dbgString != "") {
			//	        	Log.d(LOG_TAG, dbgString);
			bufferString += dbgString;
		}

		return bufferString;
	}

	public String readDataAll(long zoneNum){
		
		byte []pAddr = new byte[1];
	    byte []pReadLen = new byte[1];
		byte []pReadData = new byte[MAX_WRITE_LENGTH];
		int []pIntReturnLen = new int[1];
		
		int result;
		byte bZone;
		bZone = (byte) getSpinnerSelect(zoneNum);
		
		//get addr
		result = getEditTextHex(mEdtAddrEEPRead, pAddr, 2, pIntReturnLen);
		if (result != 0)
		{
			return null;
		}
		//get length
		result = getEditTextHex(mEdtLenEEPRead, pReadLen, 3,pIntReturnLen);
		if (result != 0)
		{
			return null;
		}
		
		result = m88sc153.AT88SC153Cmd_ReadEeprom(
				bZone,
				pAddr[0],
				(byte)pReadLen[0],
				pReadData,
				pIntReturnLen);
	
		if (result == 0)
		{
			Log.d(TAG,"result, pIntReturnLen="+pIntReturnLen[0]);
			//shows data
			return ReaderUtils.byte2HexString(pReadData, 0, pIntReturnLen[0]);
		}
		else 
			return null;
	}

	private String allData() {
		String s1 = readDataAll(0L);
		String s2 = readDataAll(1L);
		String s3 = readDataAll(2L);
		String s4 = readDataAll(3L);
		return s1+s2+s3+s4;
	}
}

