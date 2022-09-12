package com.telpo.tps550.api.demo.led;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telpo.tps550.api.InternalErrorException;
import com.telpo.tps550.api.StringTooLongException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.fingerprint.FingerPrint;
import com.telpo.tps550.api.led.Led;
import com.telpo.tps550.api.led.LedPowerManager;
import com.telpo.tps550.api.util.LEDUtil;
import com.telpo.tps550.api.util.PowerUtil;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

public class LedActivity extends Activity {
	
	LinearLayout color_led_layout,tps575_led_layout,secondLedLayout;
	EditText setContent;
	private TextView thick_contrast,fine_contrast,fine_level;
	LEDUtil ledUtil;
	Button openSecondLed;
	boolean openingLed = false;
	
	int deviceType;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_led);
		
		color_led_layout = (LinearLayout) findViewById(R.id.color_led_layout);
		tps575_led_layout = (LinearLayout) findViewById(R.id.tps575_led_layout);
		setContent = (EditText) findViewById(R.id.setContent);
		fine_level = (TextView) findViewById(R.id.fine_level);
		setContent.setText("TELPO");
		thick_contrast = (TextView) findViewById(R.id.thick_contrast);
		fine_contrast = (TextView) findViewById(R.id.fine_contrast);
		ledUtil = new LEDUtil(LedActivity.this);
		deviceType = SystemUtil.getDeviceType();
		if(deviceType == StringUtil.DeviceModelEnum.TPS506.ordinal()){
			Button opengreen = (Button) findViewById(R.id.opengreen);
			opengreen.setText("open red");
			Button closegreen = (Button) findViewById(R.id.closegreen);
			closegreen.setText("close red");
			Button openblue = (Button) findViewById(R.id.openred);
			openblue.setText("open blue");
			Button closeblue = (Button) findViewById(R.id.closered);
			closeblue.setText("close blue");
		}
		
		if(deviceType == StringUtil.DeviceModelEnum.TPS575.ordinal() || 
				"TPS950".equals(SystemUtil.getInternalModel())) {
			color_led_layout.setVisibility(View.GONE);
			tps575_led_layout.setVisibility(View.VISIBLE);
		}else if(deviceType == StringUtil.DeviceModelEnum.TPS650.ordinal() || 
				deviceType == StringUtil.DeviceModelEnum.TPS681.ordinal()) {
			color_led_layout.setVisibility(View.GONE);
			secondLedLayout.setVisibility(View.VISIBLE);
		}
		
		openSecondLed = (Button) findViewById(R.id.openSecondLed);
		openSecondLed.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openingLed = !openingLed;
				if(openingLed) {
					openSecondLed.setText("关闭数码管显示屏");
					FingerPrint.digitalTube(1);
				}else {
					openSecondLed.setText("开启数码管显示屏");
					FingerPrint.digitalTube(0);
				}
			}
		});
	}
	
	
	int thickLevel = 3;
	public void thick_minus(View view) {
		thickLevel--;
		if(thickLevel<0)
			thickLevel = 0;
		LedPowerManager.tps575_setThickContrast(thickLevel);
		thick_contrast.setText("quick set:"+thickLevel);
	}
	
	public void thick_add(View view) {
		thickLevel++;
		if(thickLevel>7)
			thickLevel = 7;
		LedPowerManager.tps575_setThickContrast(thickLevel);
		thick_contrast.setText("quick set: "+thickLevel);
	}
	
	int fineLevel = 0x0A;
	public void fine_minus(View view) {
		fineLevel--;
		if(fineLevel<0x00)
			fineLevel = 0x00;
		LedPowerManager.tps575_setFineContrast(fineLevel);
		fine_contrast.setText("slow set: "+fineLevel);
	}
	
	public void fine_add(View view) {
		fineLevel++;
		if(fineLevel>0x3f)
			fineLevel = 0x3f;
		LedPowerManager.tps575_setFineContrast(fineLevel);
		fine_contrast.setText("slow set:"+fineLevel);
	}
	
	int backlightLevel = 1;
	public void level_sub(View view){
		backlightLevel--;
		if(backlightLevel<1){
			backlightLevel = 1;
		}
		LedPowerManager.tps575_adjustBackLight(backlightLevel);
		fine_level.setText("backlight level:"+backlightLevel);
	}
	
	public void level_add(View view){
		backlightLevel++;
		if(backlightLevel>4){
			backlightLevel = 4;
		}
		LedPowerManager.tps575_adjustBackLight(backlightLevel);
		fine_level.setText("backlight level:"+backlightLevel);
	}
	
	private void reset() {
		thickLevel = 3;
		LedPowerManager.tps575_setThickContrast(thickLevel);
		thick_contrast.setText("quick set:"+thickLevel);
		fineLevel = 10;
		LedPowerManager.tps575_setFineContrast(fineLevel);
		fine_contrast.setText("slow set:"+fineLevel);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(deviceType == StringUtil.DeviceModelEnum.TPS575.ordinal()){
			LedPowerManager.tps575_setSecondScreenLcdClose();
			reset();
		}
	}
	
	public void close(View view) {
		backlightLevel = 1;
		fine_level.setText("backlight level:"+backlightLevel);
		LedPowerManager.tps575_setSecondScreenLcdClose();
		reset();
	}
	
	public void showText(View view) {
		final String content = setContent.getText().toString();
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					LedPowerManager.tps575_setSecondScreen(ledUtil.getTextC51(content));
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void showTwoLineText_m_m(View view) {
		final String content = setContent.getText().toString();
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					LedPowerManager.tps575_setSecondScreen(ledUtil.getTextC51("Text", LEDUtil.LOCATION_MIDDLE, "123456    123456", LEDUtil.LOCATION_MIDDLE));
				} catch (StringTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void showTwoLineText_m_r(View view) {
		final String content = setContent.getText().toString();
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					LedPowerManager.tps575_setSecondScreen(ledUtil.getTextC51("CHINA", LEDUtil.LOCATION_MIDDLE, content, LEDUtil.LOCATION_RIGHT));
				} catch (StringTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void showTwoLineText_r_m(View view) {
		final String content = setContent.getText().toString();
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					LedPowerManager.tps575_setSecondScreen(ledUtil.getTextC51("CHINA", LEDUtil.LOCATION_RIGHT, content, LEDUtil.LOCATION_MIDDLE));
				} catch (StringTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void showTwoLineText_r_r(View view) {
		final String content = setContent.getText().toString();
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					LedPowerManager.tps575_setSecondScreen(ledUtil.getTextC51("CHINA", LEDUtil.LOCATION_RIGHT,content, LEDUtil.LOCATION_RIGHT));
				} catch (StringTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void showBitmap(View view){
		try {
			LedPowerManager.tps575_setSecondScreen(getBytes4File(this, "syhbyte.bin"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private static byte[] getBytes4File(Context context, String filePath) throws IOException {  
		  
        InputStream in = null;  
        BufferedInputStream buffer = null;  
        DataInputStream dataIn = null;  
        ByteArrayOutputStream bos = null;  
        DataOutputStream dos = null;  
        byte[] bArray = null;  
        try {  
            in = context.getAssets().open(filePath);
            buffer = new BufferedInputStream(in);  
            dataIn = new DataInputStream(buffer);  
            bos = new ByteArrayOutputStream();  
            dos = new DataOutputStream(bos);  
            byte[] buf = new byte[1024];  
            while (true) {  
                int len = dataIn.read(buf);  
                if (len < 0)  
                    break;  
                dos.write(buf, 0, len);  
            }  
            bArray = bos.toByteArray();  
  
        } catch (Exception e) {  
            // TODO Auto-generated catch block
            e.printStackTrace();  
            return null;  
  
        } finally {  
        	
  
            if (in != null)  
                in.close();  
            if (dataIn != null)  
                dataIn.close();  
            if (buffer != null)  
                buffer.close();  
            if (bos != null)  
                bos.close();  
            if (dos != null)  
                dos.close();  
        }  
  
        return bArray;  
    }
	
	public void openBackLight(View view){
		backlightLevel = 1;
		fine_level.setText("backlight level:"+backlightLevel);
		LedPowerManager.tps575_openBackLight();
	}
	
	public void closeBackLight(View view){
		backlightLevel = 1;
		fine_level.setText("backlight level:"+backlightLevel);
		LedPowerManager.tps575_closeBackLight();
	}
	
	public void checkBackLight(View view){
		if(LedPowerManager.tps575_checkLCD()>0){
			Toast.makeText(LedActivity.this, "second lcd connect", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(LedActivity.this, "second lcd disconnect", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void checkMainLcd(View view){
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(LedPowerManager.tps575_checkMainLcd()==0){
					runOnUiThread(new Runnable() {
						
						
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(LedActivity.this, "lcd disconnect", Toast.LENGTH_SHORT).show();
						}
					});
				}else if(LedPowerManager.tps575_checkMainLcd()==1){
					runOnUiThread(new Runnable() {
						
						
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(LedActivity.this, "lcd connect", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
		
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.opengreen:
			/*if(deviceType == StringUtil.DeviceModelEnum.TPS506.ordinal()){
				LedPowerManager.power_red(1);
			}else{
				Led.ledControl(Led.POS_610_GREEN_LED, Led.POS_LED_OPEN);
			}*/
			PowerUtil.inter02LedControl(2, 1);//TPS560用
			break;
		case R.id.closegreen:
			/*if(deviceType == StringUtil.DeviceModelEnum.TPS506.ordinal()){
				LedPowerManager.power_red(0);
			}else{
				Led.ledControl(Led.POS_610_GREEN_LED, Led.POS_LED_CLOSE);
			}*/
			PowerUtil.inter02LedControl(2, 0);//TPS560用
			break;
		case R.id.openred:
			/*if(deviceType == StringUtil.DeviceModelEnum.TPS506.ordinal()){
				LedPowerManager.power_blue(1);
			}else{
				Led.ledControl(Led.POS_610_RED_LED, Led.POS_LED_OPEN);
			}*/
			PowerUtil.inter02LedControl(1, 1);//TPS560用
			break;
		case R.id.closered:
			/*if(deviceType == StringUtil.DeviceModelEnum.TPS506.ordinal()){
				LedPowerManager.power_blue(0);
			}else{
				Led.ledControl(Led.POS_610_RED_LED, Led.POS_LED_CLOSE);
			}*/
			PowerUtil.inter02LedControl(1, 0);//TPS560用
			break;
			
		case R.id.openblue:
			PowerUtil.inter02LedControl(3, 1);//TPS560用
			break;
			
		case R.id.closeblue:
			PowerUtil.inter02LedControl(3, 0);//TPS560用
			break;
		default:
			break;
		}
	}

}
