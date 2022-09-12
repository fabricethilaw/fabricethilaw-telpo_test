package com.telpo.tps550.api.demo.otherdemo;

import com.common.devicestatus.sdk.DeviceStatus;
import com.common.rgbled.PowerUtil;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.fingerprint.FingerPrint;
import com.telpo.tps550.api.led.LedPowerManager;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.util.CheckC1Bprinter;
import com.telpo.tps550.api.util.ShellUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PowerControlDemo extends Activity{

	TextView showResult;
	Button printerPowerOn;
	
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.power_control_demo);
		showResult = (TextView) findViewById(R.id.showResult);
		printerPowerOn = (Button) findViewById(R.id.printerPowerOn);
		
		//tps990hRGBled();
	}
	
	public void red_on(View view){
		FingerPrint.fingerPrintPower(1);
		//PowerUtil.setLedPower(PowerUtil.RED, PowerUtil.OPEN);
	}
	
	public void red_off(View view){
		FingerPrint.fingerPrintPower(0);
		//PowerUtil.setLedPower(PowerUtil.RED, PowerUtil.CLOSE);
	}
	
	public void green_on(View view){
		PowerUtil.setLedPower(PowerUtil.GREEN, PowerUtil.OPEN);
	}
	
	public void green_off(View view){
		PowerUtil.setLedPower(PowerUtil.GREEN, PowerUtil.CLOSE);
	}
	
	public void blue_on(View view){
		PowerUtil.setLedPower(PowerUtil.BLUE, PowerUtil.OPEN);
	}
	
	public void blue_off(View view){
		PowerUtil.setLedPower(PowerUtil.BLUE, PowerUtil.CLOSE);
	}
	
	public void printerPowerOn(View view){
		try {
			ThermalPrinter.start(this);
		} catch (TelpoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void printerPowerOff(View view){
		ThermalPrinter.stop();
	}
	
	
	//上下电154，读状态158
	public void A6High(View view){
		ShellUtils.execCommand("echo 134 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+134+"/direction", true);
		ShellUtils.execCommand("echo 1 > /sys/class/gpio/gpio"+134+"/value", true);
	}
	
	public void A6Low(View view){
		ShellUtils.execCommand("echo 134 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+134+"/direction", true);
		ShellUtils.execCommand("echo 0 > /sys/class/gpio/gpio"+134+"/value", true);
	}
	
	public void A7High(View view){
		ShellUtils.execCommand("echo 135 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+135+"/direction", true);
		ShellUtils.execCommand("echo 1 > /sys/class/gpio/gpio"+135+"/value", true);
	}
	
	public void A7Low(View view){
		ShellUtils.execCommand("echo 135 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+135+"/direction", true);
		ShellUtils.execCommand("echo 0 > /sys/class/gpio/gpio"+135+"/value", true);
	}
	
	public void relayOn(View view){
		ShellUtils.execCommand("echo 87 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio87/direction", true);
		ShellUtils.execCommand("echo 1 > /sys/class/gpio/gpio87/value", true);
	}
	
	public void relayOff(View view){
		ShellUtils.execCommand("echo 87 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio87/direction", true);
		ShellUtils.execCommand("echo 0 > /sys/class/gpio/gpio87/value", true);
	}
	
	//70---RGB摄像头
	public void camRGBPowerOn(View view){
		FingerPrint.tps980DoubleCamPower(1,1);
	}
	
	public void camRGBPowerOff(View view){
		FingerPrint.tps980DoubleCamPower(1,0);
	}
	
	//71---IR摄像头
	public void camIRPowerOn(View view){
		FingerPrint.tps980DoubleCamPower(2,1);
	}
	
	public void camIRPowerOff(View view){
		FingerPrint.tps980DoubleCamPower(2,0);
	}
	
	
	public void powerOn154(View view){
		ShellUtils.execCommand("echo 154 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+154+"/direction", true);
		ShellUtils.execCommand("echo 1 > /sys/class/gpio/gpio"+154+"/value", true);
	}
	
	public void powerOff154(View view){
		ShellUtils.execCommand("echo 154 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo out > /sys/class/gpio/gpio"+154+"/direction", true);
		ShellUtils.execCommand("echo 0 > /sys/class/gpio/gpio"+154+"/value", true);
	}
	
	public void status158(View view){
		ShellUtils.execCommand("echo 158 > /sys/class/gpio/export", true);
		ShellUtils.execCommand("echo in > /sys/class/gpio/gpio158/direction", true);
		String result = ShellUtils.execCommand("cat /sys/class/gpio/gpio158/value", true).successMsg;
		try{
			if(!"".equals(result)){
				showResult.setText("微波状态:"+Integer.valueOf(result));
			}else{
				showResult.setText("微波状态:error");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	SeekBar seekBarR,seekBarG;
	ToggleButton toggleButtonB;
	int r = 0;
	int g = 0;
	int b = 0;
	private void tps990hRGBled(){
		
		seekBarR = (SeekBar) findViewById(R.id.seekbarR);
		seekBarG = (SeekBar) findViewById(R.id.seekbarG);
		toggleButtonB = (ToggleButton) findViewById(R.id.toggleButtonB);
		
		seekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        
	        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
	            // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
	        	new Thread(new Runnable() {
					public void run() {
						r = progress;
			        	LedPowerManager.tps990h_control(new byte[]{(byte) r, (byte) g, (byte) b});
					}
				}).start();
	        }

	        
	        public void onStartTrackingTouch(SeekBar seekBar) {

	        }

	        
	        public void onStopTrackingTouch(SeekBar seekBar) {

	        }
	    });
		
		seekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        
	        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
	            // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
	        	new Thread(new Runnable() {
					public void run() {
						g = progress;
			        	LedPowerManager.tps990h_control(new byte[]{(byte) r, (byte) g, (byte) b});
					}
				}).start();
	        }

	        
	        public void onStartTrackingTouch(SeekBar seekBar) {

	        }

	        
	        public void onStopTrackingTouch(SeekBar seekBar) {

	        }
	    });
		
		toggleButtonB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					new Thread(new Runnable() {
						public void run() {
							b = 1;
				        	LedPowerManager.tps990h_control(new byte[]{(byte) r, (byte) g, (byte) b});
						}
					}).start();
				}else{
					new Thread(new Runnable() {
						public void run() {
							b = 0;
				        	LedPowerManager.tps990h_control(new byte[]{(byte) r, (byte) g, (byte) b});
						}
					}).start();
				}
			}
		});
	}
}
