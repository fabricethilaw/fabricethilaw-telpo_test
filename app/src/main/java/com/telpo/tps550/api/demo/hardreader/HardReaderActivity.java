package com.telpo.tps550.api.demo.hardreader;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.hardreader.HardReader;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class HardReaderActivity extends Activity {
	final private static String TAG = "HardReaderActivity";
	final private static int DISCOVERY_DATA = 1;
	private  HardReader mHardReader;
	private TextView tvDataShow, circleCountShow;
	private EditText etCircleCount;
	private boolean circleTest = false;
	private int successCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardreader);
        
        initView();
    }
    
    private void initView(){
    	tvDataShow =  (TextView) findViewById(R.id.data_show);
    	etCircleCount = (EditText)findViewById(R.id.circle_num);
    	circleCountShow = (TextView) findViewById(R.id.circleCountShow);
    }
    
    private Handler handler = new Handler(){
  	   public void handleMessage(Message msg){
 		   super.handleMessage(msg);
 		   switch (msg.what){
 			   case DISCOVERY_DATA:
 					byte[] buf = new byte[1024];
 					int count = 0;
 					try {
 						count = mHardReader.get(buf);
 					} catch (SecurityException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 					if(count!=0){
	 					String srt = null;
	 				    try {
							 srt = new String(buf,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d(TAG, "String == null!");
						}
	 				    if(circleTest){
	 				    	successCount++;
	 				    	circleCountShow.setText("扫描成功["+successCount+"]");
	 				    }
	 				    	
	 				   tvDataShow.setText(srt);
 					}
				break;
 		   }
 	   }
     };
     
     public void openHardreader(View view){
    	 try {
				mHardReader.open();
				Toast.makeText(HardReaderActivity.this, "打开成功", Toast.LENGTH_LONG).show();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TelpoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     }
     
     public void closeHardreader(View view){
    	 try {
				mHardReader.close();
				circleTest = false;
				successCount = 0;
				circleCountShow.setText("");
				findViewById(R.id.scan_hardreader).setEnabled(true);
				Toast.makeText(HardReaderActivity.this, "关闭成功", Toast.LENGTH_LONG).show();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TelpoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     }
     
     public void scanHardreader(View view){
    	 tvDataShow.setText("");
    	 mHardReader.startScan();
     }
     
     
     public void circleTest(View view){
    	 findViewById(R.id.scan_hardreader).setEnabled(false);
    	 circleTest = true;
    	 final int count = Integer.valueOf(etCircleCount.getText().toString());
    	 new Thread(new Runnable() {
    		 int testCount = 0;
    		 @Override
    		 public void run() {
    			 // TODO Auto-generated method stub
    			 while(circleTest && testCount < count){
    				 mHardReader.startScan();
			    	
    				 runOnUiThread(new Runnable() {								
    					 public void run() {
							 // TODO Auto-generated method stub
    						 tvDataShow.setText("");
    					 }
    				 });
			    	
    				 testCount++;

    				 Log.d(TAG, "循环执行[" + testCount + "]");
    				 try {
    					 Thread.sleep(1000);
    				 } catch (InterruptedException e) {
    					 // TODO Auto-generated catch block
    					 e.printStackTrace();
    				 }
    			 }
    			 
    			 runOnUiThread(new Runnable() {
 					
 					public void run() {
 						// TODO Auto-generated method stub
 						findViewById(R.id.scan_hardreader).setEnabled(true); 
 					}
 				});
    			 
    		 }
				 
    	 }).start();
    	 
     }
     
     @Override
     protected void onResume(){
     	super.onResume();
     	Log.i(TAG,"onResume ===");
     	if(mHardReader == null){
     		Log.i(TAG,"mHardReader ===");
     		mHardReader = new HardReader(handler);//初始化
     	}
     } 
  
     @Override
     protected void onStop(){
     	super.onStop();
     	Log.i(TAG,"onStop ===");
     	try {
			mHardReader.close();
		} catch (TelpoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
}
