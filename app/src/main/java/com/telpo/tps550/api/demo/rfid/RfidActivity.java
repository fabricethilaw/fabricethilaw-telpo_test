package com.telpo.tps550.api.demo.rfid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.other.BeepManager;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.demo.util.OtherUtils;
import com.telpo.tps550.api.iccard.Picc;

/**
 * Created by linhx on 2015/3/2.
 */
public class RfidActivity extends Activity {

    private Button getSN, m1Test, ulTest, cpuTest;
    BeepManager beepManager;
    private int retryTimes = 0;

    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.rfid_main);
        getSN = (Button) findViewById(R.id.get_sn);
        m1Test = (Button) findViewById(R.id.m1_test);
        ulTest = (Button) findViewById(R.id.ul_test);
        cpuTest = (Button) findViewById(R.id.cpucard_test);

        m1Test.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                Intent intent = new Intent(RfidActivity.this, ConsumeActivity.class);
                startActivity(intent);
            }
        });
        
        ulTest.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                Intent intent = new Intent(RfidActivity.this, UltralightActivity.class);
                startActivity(intent);
            }
        });

        getSN.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                new GetSNTask().execute();
            }
        });
        
        cpuTest.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				startActivity(new Intent(RfidActivity.this, CpuCardActivity.class));
			}
		});
    }

    
	protected void onResume() {
		super.onResume();
		beepManager = new BeepManager(this, R.raw.beep);
	}

	
	protected void onPause() {
		super.onPause();
		beepManager.close();
		beepManager = null;
	}

	private class GetSNTask extends AsyncTask<Void, Void, TelpoException> {
        ProgressDialog dialog;
        byte[] sn = new byte[64];
        byte[] sak = new byte[1];
        byte[] tag = new byte[2];
        int length;

        
        protected void onPreExecute() {
            super.onPreExecute();
            getSN.setEnabled(false);
            m1Test.setEnabled(false);

            dialog = new ProgressDialog(RfidActivity.this);
            dialog.setMessage(getString(R.string.operating));
            dialog.setCancelable(false);
            dialog.show();
        }

        
        protected TelpoException doInBackground(Void... params) {
            TelpoException result = null;
            try{
                Picc.openReader(RfidActivity.this);
                length = Picc.selectCard(sn, sak, tag);
            }catch (TelpoException e){
                e.printStackTrace();
                result = e;
            }finally {
                Picc.closeReader(RfidActivity.this);
            }
            return result;
        }

        
        protected void onPostExecute(TelpoException e) {
            super.onPostExecute(e);
            dialog.dismiss();
            if(e == null){
            	beepManager.playBeepSoundAndVibrate();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RfidActivity.this);
                dialogBuilder.setMessage("SN:" + OtherUtils.byteToHexString(sn, length) + ";SAK:" + OtherUtils.byteToHexString(sak, 1) + ";TAG:" + OtherUtils.byteToHexString(tag, 2));
                dialogBuilder.setPositiveButton(R.string.confirm,null);
                dialogBuilder.create();
                dialogBuilder.show();
                retryTimes = 0;
            }else{
				Toast.makeText(RfidActivity.this, getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
				retryTimes++;
				if(retryTimes>=3) {
				}
            }
            getSN.setEnabled(true);
            m1Test.setEnabled(true);
        }
    }
}