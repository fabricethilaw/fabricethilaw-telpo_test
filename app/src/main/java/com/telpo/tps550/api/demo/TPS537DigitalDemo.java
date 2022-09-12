package com.telpo.tps550.api.demo;

import com.telpo.tps550.api.util.PowerUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class TPS537DigitalDemo extends Activity{
	
	SeekBar seekbar;
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.tps573digitaldemo);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setMax(255);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
            	new Thread(new Runnable() {
					
					
					public void run() {
						// TODO Auto-generated method stub
						PowerUtil.TPS537setLedLight(progress);
					}
				}).start();
            }

            
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
	}
	
	int digital1 = -1;
	public void digital1(View view){
		PowerUtil.TPS537digital(1, digital1);
		digital1++;
		if(digital1 == 10){
			digital1 = -1;
		}
	}
	int point1 = 0;
	public void point1(View view){
		PowerUtil.TPS537digital(2, point1);
		point1++;
		if(point1 == 2){
			point1 = 0;
		}
	}
	
	int digital2 = -1;
	public void digital2(View view){
		PowerUtil.TPS537digital(3, digital2);
		digital2++;
		if(digital2 == 10){
			digital2 = -1;
		}
	}
	int point2 = 0;
	public void point2(View view){
		PowerUtil.TPS537digital(4, point2);
		point2++;
		if(point2 == 2){
			point2 = 0;
		}
	}
	
	int digital3 = -1;
	public void digital3(View view){
		PowerUtil.TPS537digital(5, digital3);
		digital3++;
		if(digital3 == 10){
			digital3 = -1;
		}
	}
	int point3 = 0;
	public void point3(View view){
		PowerUtil.TPS537digital(6, point3);
		point3++;
		if(point3 == 2){
			point3 = 0;
		}
	}

}
