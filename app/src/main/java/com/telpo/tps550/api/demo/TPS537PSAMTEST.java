package com.telpo.tps550.api.demo;

import java.util.ArrayList;
import java.util.List;

import com.telpo.tps550.api.reader.SmartCardReader;
import com.telpo.tps550.api.util.StringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class TPS537PSAMTEST extends Activity{

	List<SmartCardReader> readerList = new ArrayList<SmartCardReader>();
	SmartCardReader psam1;
	SmartCardReader psam2;
	SmartCardReader psam3;
	SmartCardReader psam4;
	TextView showResult;
	boolean[] hasOpenReader = new boolean[4];
	boolean[] hasCardPowerOn = new boolean[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tps537_psam_test);
		showResult = (TextView) findViewById(R.id.showResult);
		
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				psam1 = new SmartCardReader(TPS537PSAMTEST.this, SmartCardReader.SLOT_PSAM1);
				readerList.add(psam1);
				psam2 = new SmartCardReader(TPS537PSAMTEST.this, SmartCardReader.SLOT_PSAM2);
				readerList.add(psam2);
				psam3 = new SmartCardReader(TPS537PSAMTEST.this, SmartCardReader.SLOT_PSAM3);
				readerList.add(psam3);
				psam4 = new SmartCardReader(TPS537PSAMTEST.this, SmartCardReader.SLOT_PSAM4);
				readerList.add(psam4);
				runOnUiThread(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						findViewById(R.id.table_layout).setVisibility(View.VISIBLE);
						showResult.setText("");
					}
				});
			}
		}).start();
	}
	
	public void psam1_open(View view){
		open(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_open(View view){
		open(SmartCardReader.SLOT_PSAM2);
	}
	
	public void psam3_open(View view){
		open(SmartCardReader.SLOT_PSAM3);
	}
	
	public void psam4_open(View view){
		open(SmartCardReader.SLOT_PSAM4);
	}
	
	private void open(int slot){
		if(!hasOpenReader[slot-1]){
			boolean openResult = readerList.get(slot-1).open();
			hasOpenReader[slot-1] = openResult;
			if(openResult){
				showResult.setText("psam"+slot+" 打开读卡器成功");
			}else{
				showResult.setText("psam"+slot+" 打开读卡器失败");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器已打开");
		}
	}
	
	public void psam1_poweron(View view){
		poweron(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_poweron(View view){
		poweron(SmartCardReader.SLOT_PSAM2);
	}

	public void psam3_poweron(View view){
		poweron(SmartCardReader.SLOT_PSAM3);
	}

	public void psam4_poweron(View view){
		poweron(SmartCardReader.SLOT_PSAM4);
	}
	
	private void poweron(int slot){
		if(hasOpenReader[slot-1]){
			if(!hasCardPowerOn[slot-1]){
				boolean poweronResult = readerList.get(slot-1).iccPowerOn();
				hasCardPowerOn[slot-1] = poweronResult;
				if(poweronResult){
					showResult.setText("psam"+slot+" 卡片上电成功");
				}else{
					showResult.setText("psam"+slot+" 卡片上电失败");
				}
			}else{
				showResult.setText("psam"+slot+" 卡片已上电");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
		
	}
	
	public void psam1_getATR(View view){
		getATR(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_getATR(View view){
		getATR(SmartCardReader.SLOT_PSAM2);
	}

	public void psam3_getATR(View view){
		getATR(SmartCardReader.SLOT_PSAM3);
	}
	
	public void psam4_getATR(View view){
		getATR(SmartCardReader.SLOT_PSAM4);
	}
	
	private void getATR(int slot){
		if(hasOpenReader[slot-1]){
			if(hasCardPowerOn[slot-1]){
				showResult.setText("psam"+slot+" ATR:"+readerList.get(slot-1).getATRString());
			}else{
				showResult.setText("psam"+slot+" 卡片未上电");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
	}
	
	public void psam1_getProtocol(View view){
		getProtocol(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_getProtocol(View view){
		getProtocol(SmartCardReader.SLOT_PSAM2);
	}

	public void psam3_getProtocol(View view){
		getProtocol(SmartCardReader.SLOT_PSAM3);
	}

	public void psam4_getProtocol(View view){
		getProtocol(SmartCardReader.SLOT_PSAM4);
	}
	
	private void getProtocol(int slot){
		if(hasOpenReader[slot-1]){
			if(hasCardPowerOn[slot-1]){
				int protocol = readerList.get(slot-1).getProtocol();
				if (protocol == SmartCardReader.PROTOCOL_T0) {
					showResult.setText("psam"+slot+" protocol:T0");
				} else if (protocol == SmartCardReader.PROTOCOL_T1) {
					showResult.setText("psam"+slot+" protocol:T1");
				} else {
					showResult.setText("psam"+slot+" protocol:NA");
				}
			}else{
				showResult.setText("psam"+slot+" 卡片未上电");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
		
	}
	
	public void psam1_apdu(View view){
		sendAPDU(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_apdu(View view){
		sendAPDU(SmartCardReader.SLOT_PSAM2);
	}
	
	public void psam3_apdu(View view){
		sendAPDU(SmartCardReader.SLOT_PSAM3);
	}
	
	public void psam4_apdu(View view){
		sendAPDU(SmartCardReader.SLOT_PSAM4);
	}
	
	private void sendAPDU(int slot){
		if(hasOpenReader[slot-1]){
			if(hasCardPowerOn[slot-1]){
				byte[] pSendAPDU = new byte[]{(byte) 0xA0, (byte) 0xA4, 0x00, 0x00, 0x02, 0x3F, 0x00};
				int[] pRevAPDULen = new int[1];
				pRevAPDULen[0] = 300;
				byte[] result = readerList.get(slot-1).transmit(pSendAPDU);
				showResult.setText(TextUtils.isEmpty(StringUtil.toHexString(result)) ? "psam"+slot+" "+getString(R.string.send_APDU_fail) : "psam"+slot+" "+getString(R.string.send_APDU_success) + StringUtil.toHexString(result));
			}else{
				showResult.setText("psam"+slot+" 卡片未上电");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
	}
	
	public void psam1_poweroff(View view){
		poweroff(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_poweroff(View view){
		poweroff(SmartCardReader.SLOT_PSAM2);
	}
	
	public void psam3_poweroff(View view){
		poweroff(SmartCardReader.SLOT_PSAM3);
	}
	
	public void psam4_poweroff(View view){
		poweroff(SmartCardReader.SLOT_PSAM4);
	}
	
	private void poweroff(int slot){
		if(hasOpenReader[slot-1]){
			if(hasCardPowerOn[slot-1]){
				boolean poweroffResult = readerList.get(slot-1).iccPowerOff();
				if(poweroffResult){
					hasCardPowerOn[slot-1] = false;
					showResult.setText("psam"+slot+" 卡片下电成功");
				}else{
					showResult.setText("psam"+slot+" 卡片下电失败");
				}
			}else{
				showResult.setText("psam"+slot+" 卡片未上电");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
	}
	
	public void psam1_close(View view){
		close(SmartCardReader.SLOT_PSAM1);
	}
	
	public void psam2_close(View view){
		close(SmartCardReader.SLOT_PSAM2);
	}
	
	public void psam3_close(View view){
		close(SmartCardReader.SLOT_PSAM3);
	}
	
	public void psam4_close(View view){
		close(SmartCardReader.SLOT_PSAM4);
	}
	
	private void close(int slot){
		if(hasOpenReader[slot-1]){
			boolean closeResult = readerList.get(slot-1).close();
			if(closeResult){
				hasOpenReader[slot-1] = false;
				hasCardPowerOn[slot-1] = false;
				showResult.setText("psam"+slot+" 关闭读卡器成功");
			}else{
				showResult.setText("psam"+slot+" 关闭读卡器失败");
			}
		}else{
			showResult.setText("psam"+slot+" 读卡器未打开");
		}
	}
}
