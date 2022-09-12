package com.telpo.tps550.api.demo.rfid;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.demo.util.OtherUtils;
import com.telpo.tps550.api.iccard.Picc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CpuCardActivity extends Activity {
	
	private EditText mEditText;
	private Button mButton;
	private String tv_apdu_command;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_cpucard);
		initUI();
	}
	
	private void initUI(){
		mEditText = (EditText) findViewById(R.id.et_cpucard_apdu_command);
		mButton = (Button) findViewById(R.id.bt_cpucard_send_apdu_command);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				tv_apdu_command = mEditText.getText().toString();
				if((tv_apdu_command == null) || (tv_apdu_command.length() < 1)){
					Toast.makeText(CpuCardActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
					return;
				}
				new SendApduTask().execute();
			}
		});
	}
	
    private class SendApduTask extends AsyncTask<Void, Void, TelpoException> {
        ProgressDialog dialog;
        byte[] sn = new byte[64];
        byte[] sak = new byte[1];
        byte[] tag = new byte[2];
        byte[] rcv = new byte[64];
        int length;

        
        protected void onPreExecute() {
            super.onPreExecute();
            mButton.setEnabled(false);

            dialog = new ProgressDialog(CpuCardActivity.this);
            dialog.setMessage(getString(R.string.operating));
            dialog.setCancelable(false);
            dialog.show();
        }

        
        protected TelpoException doInBackground(Void... params) {
            TelpoException result = null;
            try{
                Picc.openReader(CpuCardActivity.this);
                Picc.selectCard(sn, sak, tag);
                Picc.enterCpuModel();
                byte[] apduCMD = toByteArray(tv_apdu_command);
                length = Picc.command(apduCMD, apduCMD.length, rcv);
            }catch (TelpoException e){
                e.printStackTrace();
                result = e;
            }finally {
                Picc.closeReader(CpuCardActivity.this);
            }
            return result;
        }

        
        protected void onPostExecute(TelpoException e) {
            super.onPostExecute(e);
            dialog.dismiss();
            if(e == null){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CpuCardActivity.this);
                dialogBuilder.setMessage(getString(R.string.send_APDU_success) + OtherUtils.byteToHexString(rcv,length));
                dialogBuilder.setPositiveButton(R.string.confirm,null);
                dialogBuilder.create();
                dialogBuilder.show();
            }else{
				Toast.makeText(CpuCardActivity.this, getString(R.string.operation_fail), Toast.LENGTH_LONG).show();
            }
            mButton.setEnabled(true);
        }
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
			if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
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

}
