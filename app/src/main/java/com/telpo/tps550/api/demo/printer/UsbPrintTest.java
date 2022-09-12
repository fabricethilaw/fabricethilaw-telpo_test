package com.telpo.tps550.api.demo.printer;

import com.common.rgbled.ShellUtils;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class UsbPrintTest extends Activity{
	
	UsbThermalPrinter usbThermalPrinter;
	Button print,printtxt,printblack,getBatteryStatus;

	int powerValue;
	BatteryReceiver batteryReceiver = null;
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.usbprint_testdemo);
		usbThermalPrinter = new UsbThermalPrinter(UsbPrintTest.this);
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryReceiver = new BatteryReceiver();
		registerReceiver(batteryReceiver, intentFilter);
		print = (Button) findViewById(R.id.print);
		printtxt = (Button) findViewById(R.id.printtxt);
		printblack = (Button) findViewById(R.id.printblack);
		getBatteryStatus = (Button) findViewById(R.id.getBatteryStatus);
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					usbThermalPrinter.start(0);
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					
					
					public void run() {
						// TODO Auto-generated method stub
						print.setEnabled(true);
						printtxt.setEnabled(true);
						printblack.setEnabled(true);
						getBatteryStatus.setEnabled(true);
					}
				});
				
			}
		}).start();
	}
	
	public void print(View view){
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					String pversion,gray,sversion,level,imei = null;
					Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap1);
					Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap2);
					
					usbThermalPrinter.reset();
					usbThermalPrinter.setGray(5);
					gray = "5";
					pversion = usbThermalPrinter.getVersion();
					sversion = Build.DISPLAY;
					level = ""+powerValue;
					TelephonyManager telephonyManager = (TelephonyManager) UsbPrintTest.this.getSystemService(Context.TELEPHONY_SERVICE);
					imei = telephonyManager.getDeviceId();
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
					usbThermalPrinter.addString("---------------------------");
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
					usbThermalPrinter.addString("TP PRINT TEST");
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
					usbThermalPrinter.addString("___________________________\nBlack block Test:\n---------------------------");
					usbThermalPrinter.printString();
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
					usbThermalPrinter.printLogo(bitmap1, false);
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
					usbThermalPrinter.addString("Line Test:\n---------------------------");
					usbThermalPrinter.printString();
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
					usbThermalPrinter.printLogo(bitmap2, false);
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
					usbThermalPrinter.addString("Font Size Test:\n---------------------------");
					usbThermalPrinter.setTextSize(12);
					usbThermalPrinter.addString("PRINT TEST");
					usbThermalPrinter.setTextSize(24);
					usbThermalPrinter.addString("PRINT TEST");
					usbThermalPrinter.setTextSize(36);
					usbThermalPrinter.addString("PRINT TEST");
					usbThermalPrinter.setTextSize(48);
					usbThermalPrinter.addString("PRINT TEST");
					usbThermalPrinter.setTextSize(60);
					usbThermalPrinter.addString("PRINT TEST");
					usbThermalPrinter.setTextSize(24);
					usbThermalPrinter.addString("Characters Test:\n---------------------------\nABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()?……\n\nDevice Base Information:\n---------------------------");
					usbThermalPrinter.addString("Printer Version:\n		"+pversion+"Printer Gray "+gray+
							"\nSoftware Version		"+sversion+"\nBattery Level "+level+" MV\n"+
							"CSQ Value unkonwn\nIMEI "+imei+"\n"+"TPH Temp before print unknown\nTPH Temp after print unknown\n"+
							"---------------------------");
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_RIGHT);
					usbThermalPrinter.addString("Print Test End");
					usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
					usbThermalPrinter.addString("---------------------------");
					usbThermalPrinter.printString();
					
					usbThermalPrinter.setHighlight(true);
					usbThermalPrinter.addString("Characters Test:\n---------------------------\nABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()?……\n\nDevice Base Information:\n---------------------------");
					usbThermalPrinter.printString();
					usbThermalPrinter.setHighlight(false);
					
					usbThermalPrinter.walkPaper(15);
					
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void printtxt(View view){
		try {
			usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
			usbThermalPrinter.addString("\n             烧烤" + "\n---------------------------" + "\n日期：2015-01-01 16:18:20"
					+ "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n---------------------------"
					+ "\n    项目        数量   单价  小计" + "\n秘制烤羊腿    1      56      56"
					+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
					+ "\n秘制烤鸡腿    1      56      56" + "\n烤牛腿            2      50      100"
					+ "\n烤猪蹄            1      200    200" + "\n秘制烤牛腿    1      56      56"
					+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
					+ "\n秘制烤猪腿    1      56      56" + "\n烤火鸡            2      50      100"
					+ "\n烤全牛            1      200    200" + "\n特色烤鸭腿    1      56      56"
					+ "\n烤土鸡            2      50      100" + "\n烤全羊            1      200    200"
					+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
					+ "\n烤全羊            1      200    200" + "\n秘制烤鸡腿    1      56      56"
					+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
					+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
					+ "\n烤全羊            1      200    200" + "\n秘制烤牛筋    1      56      56"
					+ "\n烤土鸡            2      50      100" + "\n烤白鸽            1      200    200"
					+ "\n秘制鸭下巴    1      56      56" + "\n烤火鸡            2      50      100"
					+ "\n烤全牛            1      200    200" + "\n 合计：1000:00元" + "\n----------------------------"
					+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
					+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n");
			usbThermalPrinter.printString();
			usbThermalPrinter.walkPaper(15);
		} catch (TelpoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printblack(View view){
		
		try {
			Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.black);
			usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
			usbThermalPrinter.printLogo(bitmap3, false);
			usbThermalPrinter.walkPaper(15);
		} catch (TelpoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void getBatteryStatus(View view){
		Log.d("tagg", "true["+ShellUtils.execCommand("cat /sys/class/power_supply/ac/online", true).successMsg+"]");
        Log.d("tagg", "false["+ShellUtils.execCommand("cat /sys/class/power_supply/ac/online", false).successMsg+"]");
	}
	
	private class BatteryReceiver extends BroadcastReceiver{
		 
		
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//判断它是否是为电量变化的Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				//获取当前电量
				int level = intent.getIntExtra("level", 0);
				//电量的总刻度
				int scale = intent.getIntExtra("scale", 100);
				//把它转成百分比
				powerValue = ((level*100)/scale);
			}
		}
	}
	
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(batteryReceiver);
		batteryReceiver = null;
		usbThermalPrinter.stop();
	}

}
