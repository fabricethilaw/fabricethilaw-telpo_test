package com.telpo.tps550.api.demo.idcard;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.idcard.IdCard;
import com.telpo.tps550.api.idcard.IdentityMsg;
import com.telpo.tps550.api.idcard.ReadCallBack;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestReader extends Activity{

	IdCard idCard;
	TextView showText;
	Button checkID, checkIC, randomIC_ID;
	IdentityMsg msg = null;
	String cardNum = null;
	boolean circleRandom = true;
	
	
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.test_reader);
		showText = (TextView) findViewById(R.id.showText);
		checkID = (Button) findViewById(R.id.checkID);
		checkIC = (Button) findViewById(R.id.checkIC);
		randomIC_ID = (Button) findViewById(R.id.randomIC_ID);
	}
	
	
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		idCard = new IdCard(this);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		circleRandom = false;
		idCard.close();
		System.exit(0);
	}
	
	
	public void checkID(View view){
		setButtonEnable(false);
		msg = null;
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					msg = idCard.checkIdCardOverseas();
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					
					
					public void run() {
						// TODO Auto-generated method stub
						if(msg != null){
							showText.setText("name["+msg.getName()+"]");
						}
						setButtonEnable(true);
					}
				});
			}
		}).start();
	}
	
	public void checkIC(View view){
		setButtonEnable(false);
		cardNum = null;
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				cardNum = idCard.readUIDTypeA();
				runOnUiThread(new Runnable() {
					
					
					public void run() {
						// TODO Auto-generated method stub
						if(cardNum != null){
							showText.setText("cardNum["+cardNum+"]");
						}
						setButtonEnable(true);
					}
				});
			}
		}).start();
	}
	
	public void randomIC_ID(View view){
		setButtonEnable(false);
		circleRandom = true;
		new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				while(circleRandom){
					idCard.checkID_IC(new ReadCallBack() {
						
						
						public void checkIDfailed() {
							// TODO Auto-generated method stub
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showText.setText("checkIDfailed");
								}
							});
						}
						
						
						public void checkIDSuccess(final IdentityMsg msg) {
							// TODO Auto-generated method stub
							circleRandom = false;
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									if(msg != null){
										showText.setText("name["+msg.getName()+"]");
									}
									setButtonEnable(true);
								}
							});
						}
						
						
						public void checkICfailed() {
							// TODO Auto-generated method stub
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									showText.setText("checkICfailed");
								}
							});
						}
						
						
						public void checkICSuccess(final String cardNum) {
							// TODO Auto-generated method stub
							circleRandom = false;
							runOnUiThread(new Runnable() {
								
								
								public void run() {
									// TODO Auto-generated method stub
									if(cardNum != null){
										showText.setText("cardNum["+cardNum+"]");
									}
									setButtonEnable(true);
								}
							});
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
	void setButtonEnable(boolean enabled){
		checkID.setEnabled(enabled);
		checkIC.setEnabled(enabled);
		randomIC_ID.setEnabled(enabled);
	}
}
