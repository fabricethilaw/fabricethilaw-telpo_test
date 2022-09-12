package com.telpo.tps550.api.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.other.BeepManager;
import com.telpo.tps550.api.demo.decode.DecodeActivity;
import com.telpo.tps550.api.demo.hardreader.HardReaderActivity;
import com.telpo.tps550.api.demo.iccard.IccActivityNew;
import com.telpo.tps550.api.demo.iccard.PsamActivity;
import com.telpo.tps550.api.demo.idcard.BluetoothIdCardActivity;
import com.telpo.tps550.api.demo.idcard.TwoInOneReaderActivity;
import com.telpo.tps550.api.demo.ir.IrActivity;
import com.telpo.tps550.api.demo.led.LedActivity;
import com.telpo.tps550.api.demo.megnetic.MegneticActivity;
import com.telpo.tps550.api.demo.moneybox.MoneyBoxActivity;
import com.telpo.tps550.api.demo.nfc.NfcActivity;
import com.telpo.tps550.api.demo.nfc.NfcActivity_tps537;
import com.telpo.tps550.api.demo.nfc.NfcActivity_tps900;
import com.telpo.tps550.api.demo.ocr.OcrIdCardActivity;
import com.telpo.tps550.api.demo.printer.PrinterActivity;
import com.telpo.tps550.api.demo.printer.PrinterActivitySY581;
import com.telpo.tps550.api.demo.printer.UsbPrinterActivity;
import com.telpo.tps550.api.demo.rfid.RfidActivity;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.hardreader.HardReader;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

public class MainActivity extends Activity {
	private static final String TAG = "TELPO_SDK";
	private int Oriental = -1;
	private Button BnPrint, BnQRCode, psambtn, magneticCardBtn, rfidBtn, pcscBtn, identifyBtn, 
	               moneybox, irbtn, ledbtn, decodebtn, nfcbtn, hardreaderbtn;
	private BeepManager mBeepManager;
	private static int printerCheck = -1;
	private static final String FILE_NAME = "/sdcard/tpsdk/printerVersion.txt";
	ProgressDialog checkDialog;
	
	boolean isreading;
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isreading = false;
	}
	
	int deviceType = SystemUtil.getDeviceType();
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		if (-1 == Oriental) {
//			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//				Oriental = 0;
//			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//				Oriental = 1;
//			}
//		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		
		Log.d("tagg", "system:"+deviceType+"stringuril:"+StringUtil.DeviceModelEnum.TPS651.ordinal());
		
		BnPrint = (Button) findViewById(R.id.print_test);
		BnQRCode = (Button) findViewById(R.id.qrcode_verify);
		magneticCardBtn = (Button) findViewById(R.id.magnetic_card_btn);
		rfidBtn = (Button) findViewById(R.id.rfid_btn);
		pcscBtn = (Button) findViewById(R.id.pcsc_btn);
		identifyBtn = (Button) findViewById(R.id.identity_btn);
		moneybox = (Button) findViewById(R.id.moneybox_btn);
		irbtn = (Button) findViewById(R.id.ir_btn);
		ledbtn = (Button) findViewById(R.id.led_btn);
		psambtn = (Button) findViewById(R.id.psam);
		decodebtn = (Button) findViewById(R.id.decode_btn);
		nfcbtn = (Button) findViewById(R.id.nfc_btn);
		mBeepManager = new BeepManager(this, R.raw.beep);
		hardreaderbtn = (Button) findViewById(R.id.hardreader_btn);
		
		//MoneyBox
		moneybox.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, MoneyBoxActivity.class));
			}
		});
		
		//Barcode And Qrcode
		BnQRCode.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				if (checkPackage("com.telpo.tps550.api")) {
					Intent intent = new Intent();
					intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture");
					try {
						startActivityForResult(intent, 0x124);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		//Print
		BnPrint.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.printer_type_select));
				dialog.setNegativeButton(getString(R.string.printer_type_common), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialogInterface, int i) {
						if (deviceType == StringUtil.DeviceModelEnum.TPS650.ordinal()
								&& !(Build.MODEL.equals("MTDP-618A") || Build.MODEL.equals("TPS650M"))) {
							if(getFileContent(FILE_NAME) != null && getFileContent(FILE_NAME).equals("SY581")) {
								startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
							}else {
								startActivity(new Intent(MainActivity.this, PrinterActivity.class));
							}
						}else if((deviceType == StringUtil.DeviceModelEnum.TPS650T.ordinal() && SystemUtil.tps650t_is_sy581) ||
								deviceType == StringUtil.DeviceModelEnum.TPS680.ordinal() ||
								deviceType == StringUtil.DeviceModelEnum.C1B.ordinal() ||
								deviceType == StringUtil.DeviceModelEnum.TPS650P.ordinal() ||
								"C1".equals(SystemUtil.getInternalModel())){
							if(deviceType == StringUtil.DeviceModelEnum.C1B.ordinal()){
								SystemUtil.checkPrinter581(MainActivity.this);
							}
							startActivity(new Intent(MainActivity.this, PrinterActivitySY581.class));
						}else{
							startActivity(new Intent(MainActivity.this, PrinterActivity.class));
						}
					}
				});
				dialog.setPositiveButton(getString(R.string.printer_type_usb), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialogInterface, int i) {
						startActivity(new Intent(MainActivity.this, UsbPrinterActivity.class));
//						startActivity(new Intent(MainActivity.this, UsbPrintTest.class));
						
					}
				});
				dialog.show();
			}
		});
		
		//Magnetic Card
		magneticCardBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, MegneticActivity.class));
			}
		});
		
		//RFID
		rfidBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, RfidActivity.class));
			}
		});

		//IC Card
		pcscBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, IccActivityNew.class));
			}
		});

		//IR
		irbtn.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, IrActivity.class));
			}
		});
		
		//Led
		ledbtn.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, LedActivity.class));
			}
		});

		//ID Card
		identifyBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle(getString(R.string.idcard_xzgn));
				dialog.setMessage(getString(R.string.idcard_xzsfsbfs));

				dialog.setNegativeButton(getString(R.string.idcard_sxtsb), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialogInterface, int i) {
						//use camera
						startActivity(new Intent(MainActivity.this, OcrIdCardActivity.class));
					}
				});
				dialog.setPositiveButton(getString(R.string.idcard_dkqsb), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialogInterface, int i) {
						//use ID Card reader
						//startActivity(new Intent(MainActivity.this, IdCardActivity.class));
						AlertDialog.Builder idcard_dialog = new AlertDialog.Builder(MainActivity.this);
						idcard_dialog.setTitle(getString(R.string.idcard_xzgn));
						idcard_dialog.setMessage(getString(R.string.idcard_xzsfsbfs));
						idcard_dialog.setNegativeButton(getString(R.string.idcard_hqsfzxx), new DialogInterface.OnClickListener() {
							
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								startActivity(new Intent(MainActivity.this, /*IdCardActivity*/TwoInOneReaderActivity.class));
							}
						});
						idcard_dialog.setPositiveButton(getString(R.string.idcard_bt_hqsfzxx), new DialogInterface.OnClickListener() {
							
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								startActivity(new Intent(MainActivity.this, BluetoothIdCardActivity.class));
							}
						});
						idcard_dialog.show();
					}
				});
				dialog.show();

			}

		});
		
		//PSAM Card
		psambtn.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, PsamActivity.class));
			}
		});		

		//laser qrcode
		decodebtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, DecodeActivity.class));
			}
		});
		
		//NFC
		nfcbtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
			    if(deviceType == StringUtil.DeviceModelEnum.TPS900.ordinal() && 
			    		!"TPS360S".equals(SystemUtil.getInternalModel())  && 
			    		!"S10A".equals(SystemUtil.getInternalModel())){
			        startActivity(new Intent(MainActivity.this, NfcActivity_tps900.class));
			    }else if("D2".equals(SystemUtil.getInternalModel())){
			    	startActivity(new Intent(MainActivity.this, NfcActivity_tps537.class));
				} else{
			        startActivity(new Intent(MainActivity.this, NfcActivity.class));
			    }
			}
		});
		
		//纭澶� hardreade Scan
		hardreaderbtn.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, HardReaderActivity.class));
			}
		});
		
		if ((deviceType == StringUtil.DeviceModelEnum.TPS650.ordinal()
				&& !(Build.MODEL.equals("MTDP-618A") || Build.MODEL.equals("TPS650M")))) {
			
			checkDialog = new ProgressDialog(MainActivity.this);
			checkDialog.setTitle(getString(R.string.checkPrinterType));
			checkDialog.setMessage(getText(R.string.watting));
			checkDialog.setCancelable(false);
			checkDialog.show();
			
			new Thread(new Runnable() {
				
				
				public void run() {
					// TODO Auto-generated method stub
					File file = new File(FILE_NAME);
					//if (!file.exists() || getFileContent(FILE_NAME) == null) {
						printerCheck = SystemUtil.checkPrinter581(MainActivity.this);
						//writeData();
					//}
					Log.d(TAG, "check read:"+getFileContent(FILE_NAME));
					runOnUiThread(new Runnable() {
						public void run() {
							checkDialog.dismiss();
							if(printerCheck == 1){
								return;
							}
							String printerModel = getFileContent(FILE_NAME);
							if(getFileContent(FILE_NAME) == null) {
								AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
								dlg.setTitle(getString(R.string.checkPrinterType));
								dlg.setMessage(getString(R.string.check_error));
								dlg.setCancelable(false);
								dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialogInterface, int i) {
										java.lang.System.exit(0);
									}
								});
								try {
									dlg.show();
								} catch (Exception e) {
									// TODO: handle exception
								}
							}else {
								AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
								dlg.setTitle(getString(R.string.checkPrinterType));
								dlg.setMessage(printerModel);
								dlg.setCancelable(false);
								dlg.setPositiveButton("纭畾", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialogInterface, int i) {
										
									}
								});
								try {
									dlg.show();
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					});
				}
			}).start();
		}
	}

	private boolean checkPackage(String packageName) {
		PackageManager manager = this.getPackageManager();
		Intent intent = new Intent().setPackage(packageName);
		List<ResolveInfo> infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		if (infos == null || infos.size() < 1) {
			return false;
		}
		return true;
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x124) {
			if (resultCode == 0) {
				if (data != null) {
					try {
						mBeepManager.playBeepSoundAndVibrate();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					String qrcode = data.getStringExtra("qrCode");
					//change(qrcode);
					Toast.makeText(MainActivity.this, "Scan result:" + qrcode, Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				Toast.makeText(MainActivity.this, "Scan Failed", Toast.LENGTH_LONG).show();
			}
			
		}
	}

	
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(Oriental);
		
		
	}
	
	@SuppressLint("NewApi") 
	private static void openAirplaneModeOn(Context context,boolean enabling) {  
	    
		Settings.Global.putInt(context.getContentResolver(),  
		                     Settings.Global.AIRPLANE_MODE_ON,enabling ? 1 : 0);  
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
		intent.putExtra("state", enabling);  
		context.sendBroadcast(intent);

	}

	
	protected void onDestroy() {
		super.onDestroy();
		mBeepManager.close();
		mBeepManager = null;
	}
	
	private static String getFileContent(String file_name) {    
        String filePath = file_name;
        String fileContent = null;
        try {    
            File file = new File(filePath);    
            if (file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file));    
                BufferedReader br = new BufferedReader(isr);    
                String lineTxt = null;    
                while ((lineTxt = br.readLine()) != null) {  
                	fileContent = lineTxt; 
                }
                isr.close();    
                br.close();    
            }else {
                Log.e(TAG, "can not find file");
                file.createNewFile();
            }    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return fileContent;    
    }
	
	public static Bitmap getLoacalBitmap(String url) {
        if (url != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(url);
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fis = null;
                }
            }
        } else {
            return null;
        }
    }
	
	String[] portNum = new String[20];
	String[] productNum = new String[20];
	String[] readerNum = new String[4];
	
}
