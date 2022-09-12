package com.telpo.tps550.api.demo.idcard;

import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.idcard.IdentityMsg;
import com.telpo.tps550.api.idcard.ReadCallBack;
import com.telpo.tps550.api.idcard.T2OReader;
import com.telpo.tps550.api.idcard.T2OReaderCallBack;
import com.telpo.tps550.api.util.ReaderUtils;
import com.telpo.tps550.api.util.StringUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TwoInOneReaderActivity extends Activity{
	
	T2OReader t2oReader;
	EditText blocknum,password,newdata;
	TextView showResult;
	ImageView headImg;
	boolean hasOpenReader;
	boolean isUsb;
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.test_layout);
		Log.d("tagg", "onCreate");
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("tagg", "onStart");
		t2oReader = new T2OReader();
		blocknum = (EditText) findViewById(R.id.blocknum);
		password = (EditText) findViewById(R.id.password);
		newdata = (EditText) findViewById(R.id.newdata);
		showResult = (TextView) findViewById(R.id.showResult);
		headImg = (ImageView) findViewById(R.id.headImg);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("tagg", "onStop");
		isCircleRead = false;
		t2oReader.closeReader();
		t2oReader = null;
		System.exit(0);
	}
	
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("tagg", "onDestroy");
	}
	
	public void circleReadID(View view){
		findViewById(R.id.circleReadID).setEnabled(false);
		findViewById(R.id.circleReadIDnoFinger).setEnabled(false);
		new ReadIDTask().execute();
	}
	
	public void circleReadIDnoFinger(View view){
		findViewById(R.id.circleReadID).setEnabled(false);
		findViewById(R.id.circleReadIDnoFinger).setEnabled(false);
		new ReadIDnoFingerTask().execute();
	}
	
	//boolean hasReader = false;
	class ReadIDTask extends AsyncTask<Void, Void, Void>{

		IdentityMsg msg;
		Bitmap bitmap;
		boolean hasReader = false;
		long startTime = 0;
		long endTime = 0;
		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startTime = System.currentTimeMillis();
			//if(!hasReader){
				try {
					hasReader = t2oReader.openReader(TwoInOneReaderActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			//}
			
		}

		
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			if(hasReader){
				try {
					msg = t2oReader.checkIDCard();
					endTime = System.currentTimeMillis();
					if(msg != null){
						bitmap = t2oReader.decodeIDImage(msg.getHead_photo());
						//bitmap = t2oReader.old32DecodeIDImage(msg.getHead_photo());
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				return null;
			}
			
			return null;
		}
		
		
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(msg != null){
				String name = msg.getName();
				String sex = msg.getSex();
				String nation = msg.getNation();
				String born = msg.getBorn();
				String address = msg.getAddress();
				String apartment = msg.getApartment();
				String period = msg.getPeriod();
				String no = msg.getNo();
				String finger = ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg));
				
				showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
												born + "\n" + address + "\n" + apartment + "\n" + 
												period + "\n" + no + "\n" + finger);
			}else{
				showResult.setText("read fail ...");
			}
			showResult.append("\n读卡时间["+(endTime - startTime)+"]");
			
			if(bitmap != null){
				headImg.setImageBitmap(bitmap);
			}
			
			t2oReader.closeReader();
			
			new ReadIDTask().execute();
			//findViewById(R.id.circleReadID).setEnabled(true);
		}
	}
	
	class ReadIDnoFingerTask extends AsyncTask<Void, Void, Void>{

		IdentityMsg msg;
		Bitmap bitmap;
		boolean hasReader = false;
		long startTime = 0;
		long endTime = 0;
		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startTime = System.currentTimeMillis();
			//if(!hasReader){
				try {
					hasReader = t2oReader.openReader(TwoInOneReaderActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			//}
			
		}

		
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			if(hasReader){
				try {
					msg = t2oReader.checkIDCard(false);
					endTime = System.currentTimeMillis();
					if(msg != null){
						bitmap = t2oReader.decodeIDImage(msg.getHead_photo());
						//bitmap = t2oReader.old32DecodeIDImage(msg.getHead_photo());
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}else{
				return null;
			}
			
			return null;
		}
		
		
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(msg != null){
				String name = msg.getName();
				String sex = msg.getSex();
				String nation = msg.getNation();
				String born = msg.getBorn();
				String address = msg.getAddress();
				String apartment = msg.getApartment();
				String period = msg.getPeriod();
				String no = msg.getNo();
				String finger = ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg));
				
				showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
												born + "\n" + address + "\n" + apartment + "\n" + 
												period + "\n" + no + "\n" + finger);
			}else{
				showResult.setText("read fail ...");
			}
			showResult.append("\n读卡时间["+(endTime - startTime)+"]");
			
			if(bitmap != null){
				headImg.setImageBitmap(bitmap);
			}
			
			t2oReader.closeReader();
			
			new ReadIDnoFingerTask().execute();
			//findViewById(R.id.circleReadID).setEnabled(true);
		}
	}
	
	public void openReaderUSB(View view){
		if(hasOpenReader){
			showResult.setText("请先关闭读卡器");
		}else{
			boolean isOpenSuccess = false;
			if(t2oReader.isUSBReader(TwoInOneReaderActivity.this)){
				isOpenSuccess = t2oReader.openReader(TwoInOneReaderActivity.this);
			}Log.d("T2OReader", "isOpenSuccess["+isOpenSuccess+"]");
			
			if(isOpenSuccess){
				hasOpenReader = true;
				isUsb = true;
				showResult.setText("打开读卡器成功");
			}else{
				showResult.setText("打开读卡器失败");
			}
		}
		
	}
	
	public void openReaderSerial(View view){
		if(hasOpenReader){
			showResult.setText("请先关闭读卡器");
		}else{
			boolean isOpenSuccess = false;
			isOpenSuccess = t2oReader.openReader();
			if(isOpenSuccess){
				hasOpenReader = true;
				isUsb = false;
				showResult.setText("打开读卡器成功");
			}else{
				showResult.setText("打开读卡器失败");
			}
		}
	}
	
	public void closeReader(View view){
		hasOpenReader = false;
		t2oReader.closeReader();
	}
	
	
	
	public void checkSN(View view){
		showResult.setText("模块SN:"+t2oReader.getIDSam());
	}
	
	public void checkPhyDDR(View view){
		showResult.setText("二代证物理地址:"+StringUtil.toHexString(t2oReader.getIDPhyAddr()));
	}
	
	
	public void ramRead(View view){
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					t2oReader.checkID_IC(new ReadCallBack() {
						
						
						public void checkIDfailed() {
							// TODO Auto-generated method stub
							Log.d("tagg", "check id failed");
						}
						
						
						public void checkIDSuccess(IdentityMsg msg) {
							// TODO Auto-generated method stub
							Log.d("tagg", "check id success + "+msg.getName());
						}
						
						
						public void checkICfailed() {
							// TODO Auto-generated method stub
							Log.d("tagg", "check ic failed");
						}
						
						
						public void checkICSuccess(String cardNum) {
							// TODO Auto-generated method stub
							Log.d("tagg", "check ic success + "+cardNum);
						}
					});
				}
			}
		}).start();
	}
	
	public void update(View view){
		if(hasOpenReader){
			showResult.setText("升级中...");
			new Thread(new Runnable() {
				
				
				public void run() {
					// TODO Auto-generated method stub
					if(isUsb){
						if(t2oReader.usbModuleUpdate(TwoInOneReaderActivity.this)){
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showResult.setText("升级成功");
								}
							});
						}else{
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showResult.setText("升级失败");
								}
							});
						}
					}else{
						Log.d("tagg", "serial update start");
						t2oReader.serialModuleUpdate(new T2OReaderCallBack() {
							
							
							public void updateComplete(final boolean isUpdateSuccess) {
								// TODO Auto-generated method stub
								runOnUiThread(new Runnable() {
									
									
									public void run() {
										// TODO Auto-generated method stub
										if(isUpdateSuccess){
											showResult.setText("升级成功");
										}else{
											showResult.setText("升级失败");
										}
									}
								});
							}
						});
					}
				}
			}).start();
		}else{
			showResult.setText("请先打开读卡器");
		}
	}
	
	public void checkVersion(View view){
		showResult.setText("模块版本:"+t2oReader.getVersion());
	}
	
	public void find(View view){
		if(t2oReader.findIDCard()){
			showResult.setText("寻卡成功");
		}else{
			showResult.setText("寻卡失败");
		}
	}
	
	public void select(View view){
		if(t2oReader.selectIDCard()){
			showResult.setText("选卡成功");
		}else{
			showResult.setText("选卡失败");
		}
	}
	
	public void read(View view){
		if(t2oReader.readIDCard()!=null){
			showResult.setText("读卡成功");
		}else{
			showResult.setText("读卡失败");
		}
	}
	
	IdentityMsg msg = null;   
	public void check(View view){
		
		checkIDcard(true);
		
	}
	
	boolean isCircleRead = false;
	long successCount;
	long totalCount;
	public void checkWhile(View view){
		isCircleRead = true;
		totalCount = 0;
		successCount = 0;
		findViewById(R.id.checkWhile).setEnabled(false);
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				while(isCircleRead){
					totalCount++;
					checkIDcard(true);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public void check32(View view){
		checkIDcard(false);
	}
	
	private void checkIDcard(boolean needFringerInfo){
		
		long startTime = System.currentTimeMillis();
		msg = t2oReader.checkIDCard(needFringerInfo);
		final long endTime = System.currentTimeMillis() - startTime;
		
		if(msg!=null){
			successCount++;
			final String name = msg.getName();
			final String sex = msg.getSex();
			final String nation = msg.getNation();
			final String born = msg.getBorn();
			final String address = msg.getAddress();
			final String apartment = msg.getApartment();
			final String period = msg.getPeriod();
			final String no = msg.getNo();
			final StringBuffer finger = new StringBuffer();
			if(needFringerInfo){
				finger.append(ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg)));
			}
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					showResult.setText(name + "\n" + sex + "\n" + nation + "\n" + 
							born + "\n" + address + "\n" + apartment + "\n" + 
							period + "\n" + no + "\n" + finger.toString() + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
					headImg.setImageBitmap(t2oReader.decodeIDImage(t2oReader.getIDImage(msg)));
				}
			});
			
			//headImg.setImageBitmap(t2oReader.old32DecodeIDImage(msg.getHead_photo()));
		}else{
			runOnUiThread(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					showResult.setText("读取二代证失败" + "\n读卡时间:" + endTime + "\n成功次数["+successCount+"] 失败次数["+(totalCount - successCount)+"]");
				}
			});
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				checkIDcard(true);
				handler.sendEmptyMessageDelayed(1, 500);
				break;

			default:
				break;
			}
		};
	};
	
	public void getIDFinger(View view){
		showResult.setText(ReaderUtils.get_finger_info(TwoInOneReaderActivity.this, t2oReader.getIDFinger(msg)));
	}
	
	public void typeAUID(View view){
		long startTime = System.currentTimeMillis();
		String uid = t2oReader.readUIDTypeA();
		showResult.setText("typeA uid:"+uid+" time["+(System.currentTimeMillis() - startTime)+"]");
		
		
	}
	
	public void sendAPDU(View view){
		t2oReader.sendAPDU(StringUtil.toBytes("00a404000e325041592e5359532e444446303100"));
	}
	
	public void verifyTypeA(View view){
		if(t2oReader.passwordCheckTypeA(blocknum.getText().toString(),password.getText().toString())){
			showResult.setText("keyA验证成功");
		}else{
			showResult.setText("keyA验证失败");
		}
	}
	
	public void verifyTypeB(View view){
		if(t2oReader.passwordCheckTypeA(blocknum.getText().toString(),password.getText().toString(),T2OReader.KEYB)){
			showResult.setText("keyB验证成功");
		}else{
			showResult.setText("keyB验证失败");
		}
	}
	
	public void readTypeA(View view){
		showResult.setText("区块数据:"+t2oReader.readDataTYPEA());
	}
	
	public void writeTypeA(View view){
		if(t2oReader.writeDataTypeA(newdata.getText().toString())){
			showResult.setText("写入成功");
		}else{
			showResult.setText("写入失败");
		}
	}
}
