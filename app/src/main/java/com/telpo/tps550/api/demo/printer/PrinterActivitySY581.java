package com.telpo.tps550.api.demo.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.printer.ThermalPrinterSY581;
import com.telpo.tps550.api.serial.Serial;
import com.telpo.tps550.api.util.CheckC1Bprinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrinterActivitySY581 extends Activity {
	private static final String TAG = "PrinterActivitySY581";
	
	private static String printVersion;
	private Bitmap bitmap = null;
	private final int NOPAPER = 3;
	private final int LOWBATTERY = 4;
	private final int PRINTVERSION = 5;
	private final int PRINTBARCODE = 6;
	private final int PRINTQRCODE = 7;
	private final int PRINTPAPERWALK = 8;
	private final int PRINTCONTENT = 9;
	private final int CANCELPROMPT = 10;
	private final int PRINTERR = 11;
	private final int OVERHEAT = 12;
	private final int MAKER = 13;
	private final int PRINTPICTURE = 14;
	private final int EXECUTECOMMAND = 15;
	private final int PRINTLONGPICTURE = 16;
	private final int PRINTLONGTEXT = 17;
	private final int PRINTUNSUPPORTEDCONTENT = 18;
	private final int OPENPRINTERDOOR = 19;
	private final int OVERTIME = 20;

	private LinearLayout print_text, print_pic, print_comm;
	private TextView text_index, pic_index, comm_index, textPrintVersion,wordFont_textview,textview_gray;
	MyHandler handler;
	private EditText editTextLeftDistance, editTextLineDistance, editTextWordFont, editTextPrintGray, editTextBarcode,
			editTextQrcode, editTextPaperWalk, editTextContent, edittext_maker_search_distance,
			edittext_maker_walk_distance, edittext_input_command,set_tem;
	private Button buttonBarcodePrint, buttonPaperWalkPrint, buttonContentPrint, buttonQrcodePrint,
			buttonGetExampleText, buttonGetZhExampleText, buttonClearText, button_maker, button_papercut,
			button_print_picture, button_execute_command, button_print_long_picture, button_print_long_text,
			button_print_georgia,check_status,print_setTem;
	private String Result;
	private Boolean nopaper = false;
	private boolean LowBattery = false;

	public static String barcodeStr;
	public static String qrcodeStr;
	public static int paperWalk;
	public static String printContent;
	private int leftDistance = 0;
	private int lineDistance;
	private int wordFont;
	private int printGray;
	private ProgressDialog progressDialog;
	private final static int MAX_LEFT_DISTANCE = 255;
	ProgressDialog dialog;
	private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.png";
	private String picturePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/222.png";
	private String picturePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/333.png";
	private String picturePath4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/444.png";
	
	Serial mSerialPort;
	
	InputStream mInputStream;
	ReadThread mReadThread;
	boolean doorIsOpen = false;
	boolean isNoPaper = false;
	private boolean printingLongPicture = false;
	private int printingLongText = 0;
	private int countLongPicture = 0;
	private boolean isDialogDismiss = false;
	private boolean circle = false;
	//static boolean xon = false;
	//static boolean xoff = false;
	private String tempVersion = null;
	
	
	public static String printContent1 = "\n             烧烤" + "\n----------------------------"
			+ "\n日期：2015-01-01 16:18:20" + "\n卡号：12378945664" + "\n单号：1001000000000529142"
			+ "\n----------------------------" + "\n  项目    数量   单价  小计" + "\n秘制烤羊腿  1    56    56"
			+ "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
			+ "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
			+ "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100"
			+ "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50"
			+ "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
			+ "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
			+ "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56"
			+ "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
			+ "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
			+ "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
			+ "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
			+ "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n 合计：1000：00元" + "\n----------------------------"
			+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
			+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
	
	class ReadThread extends Thread {
		
        
        public void run() {
            super.run();
            while(!isInterrupted()) {
                try {
                	int size;
                    if (mInputStream != null && mInputStream.available() > 0) {
                    	byte[] buffer = new byte[64];
                        size = mInputStream.read(buffer);
                        
                        Log.d("printtest", StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)));
                        
                        if(size > 0) {
                        	Log.d(TAG, "sy581 receive buffer:"+StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)));
                        	if(StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)).contains("14")) {
                        		tempVersion = StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size));
                        	}
                        }
                        
                        if (size > 0) {
                            byte  name = buffer[0];
                            
                            if(StringUtil.toHexString(Arrays.copyOfRange(buffer, 0, size)).equals("B0B0")) {
                            	Log.d(TAG, "B0B0");
                            	//B0,正常无故障
                            	cancelProgressDialog();
                            }else if(name == (byte)0xa8)
                            { 
                            	Log.d("printtest", "print complete!");
                            	//printTicket(getShoppingReceipt(), false, true);
                            	
                            	/*if(time < 3){
                            		time++;
                            		try {
    									Thread.sleep(500);
    								} catch (InterruptedException e) {
    									// TODO Auto-generated catch block
    									e.printStackTrace();
    								}
                            		printTicket(getShoppingReceipt(), false, true);
                            	}*/
                            	if(printingLongPicture) {
                            		countLongPicture++;
                            		Log.d(TAG, "countLongPicture:"+countLongPicture);
                            		if(countLongPicture == /*3*/1) {
                            			printingLongPicture = false;
                                		countLongPicture = 0;
                                		cancelProgressDialog();
                            		}
                            	}else if(printingLongText==1||printingLongText==2){
                            		if(printingLongText == 1) {
                            			printingLongText = 2;
                            			try {
    										ThermalPrinter.addString("4\n" + getString(R.string.printContent1));
    										ThermalPrinter.addString("5\n" + getString(R.string.printContent1));
    	                    				ThermalPrinter.addString("6\n" + getString(R.string.printContent1));
    	                    				ThermalPrinter.printString();
    									} catch (TelpoException e) {
    										// TODO Auto-generated catch block
    										e.printStackTrace();
    									}
                            		}else if(printingLongText == 2) {
                            			printingLongText = 0;
                            			try {
    										ThermalPrinter.addString("7\n" + getString(R.string.printContent1));
    										ThermalPrinter.addString("8\n" + getString(R.string.printContent1));
    	                    				ThermalPrinter.printString();
    									} catch (TelpoException e) {
    										// TODO Auto-generated catch block
    										e.printStackTrace();
    									}
                            		}
                            	}else {
                            		cancelProgressDialog();
                            	}
                            } 
                            else if(name == (byte)0xa7)
                            { 
                            	ThermalPrinter.errorStop = true;
                            	cancelProgressDialog();
                            	Log.d(TAG, "print cut wrong");
                            }else if(name == (byte)0xa5) {
                            	ThermalPrinter.errorStop = true;
                            	Log.d("tagg", "errorStop["+ThermalPrinter.errorStop+"]");
                            	Log.d(TAG, "print box open!");
                            	cancelProgressDialog();
                            	if(!doorIsOpen && !isNoPaper) {
                            		handler.sendMessage(handler.obtainMessage(OPENPRINTERDOOR, 1, 0, null));
                            	}
                            }
                            else if(name == (byte)0xa1 || name == (byte)0xb2)
                            { 
                            	ThermalPrinter.errorStop = true;
                            	circle = false;
                            	Log.d(TAG, "print no paper");
                            	cancelProgressDialog();
                            	if(!doorIsOpen && !isNoPaper) {
                            		handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                            	}
                            } 
                            else if(name == (byte)0xa3)
                            { 
                            	ThermalPrinter.errorStop = true;
                            	Log.d(TAG, "print over tempreture");
                            	cancelProgressDialog();
                            	handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                            } 
                            else if(name == (byte)0xa2)
                            { 
                            	cancelProgressDialog();
                            	runOnUiThread(new Runnable() {
    								
    								
    								public void run() {
    									// TODO Auto-generated method stub
    									Toast.makeText(PrinterActivitySY581.this, "打印机温度恢复正常!", Toast.LENGTH_LONG).show();
    								}
    							});
                            	Log.d(TAG, "print tempreture usual");
                            }else if(name == (byte)0x11) {
                            	Log.d(TAG, "on:print continue");
                            	ThermalPrinter.xon = true;
                            }else if(name == (byte)0x13) {
                            	Log.d(TAG, "off:print stop");
                            	ThermalPrinter.xon = false;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
	
	private class MyHandler extends Handler {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOPAPER:
				noPaperDlg();
				break;
			case LOWBATTERY:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrinterActivitySY581.this);
				alertDialog.setTitle(R.string.operation_result);
				alertDialog.setMessage(getString(R.string.LowBattery));
				alertDialog.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialogInterface, int i) {
							}
						});
				alertDialog.show();
				break;
			case PRINTVERSION:
				dialog.dismiss();
				if (msg.obj.equals("1")) {
					//textPrintVersion.setText(printVersion);
					int index = printVersion.indexOf("+");
					if(index > 0){
						textPrintVersion.setText("Hardware version:"+printVersion.substring(0, index)+" Software version:"+printVersion.substring(index+1, printVersion.length()));
					}else{
						textPrintVersion.setText(printVersion);
					}
				} else {
					Toast.makeText(PrinterActivitySY581.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
				}
				//power_off.performClick();
				break;
			case PRINTBARCODE:
				new barcodePrintThread().start();
				break;
			case PRINTQRCODE:
				new qrcodePrintThread().start();
				break;
			case PRINTPAPERWALK:
				new paperWalkPrintThread().start();
				break;
			case PRINTCONTENT:
				new contentPrintThread().start();
				break;
			case MAKER:
				new MakerThread().start();
				break;
			case PRINTPICTURE:
				new printPicture().start();
				break;
			case CANCELPROMPT:
				//
				break;
			case EXECUTECOMMAND:
				new executeCommand().start();
				break;
			case OVERHEAT:
				AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(PrinterActivitySY581.this);
				overHeatDialog.setTitle(R.string.operation_result);
				overHeatDialog.setMessage(getString(R.string.overTemp));
				overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialogInterface, int i) {
							}
						});
				overHeatDialog.show();
				break;
			case PRINTLONGPICTURE:
				new printLongPicture().start();
				break;
			case PRINTLONGTEXT:
				new printLongText().start();
				break;
			case PRINTUNSUPPORTEDCONTENT:
				new printUnsupportedText().start();
				break;
			case OPENPRINTERDOOR:
				doorIsOpen = true;
				AlertDialog.Builder openPrinterDoor = new AlertDialog.Builder(PrinterActivitySY581.this);
				openPrinterDoor.setTitle(R.string.operation_result);
				openPrinterDoor.setMessage(getString(R.string.openDoor));
				openPrinterDoor.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialogInterface, int i) {
								doorIsOpen = false;
							}
						});
				openPrinterDoor.show();
				break;
			case OVERTIME:
				if(!isDialogDismiss)
					cancelProgressDialog();
				break;
			case 402:
				if(circle) {
					buttonContentPrint.performClick();
				}
			default:
				Toast.makeText(PrinterActivitySY581.this, "Print Error!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	private void initView() {
		print_text = (LinearLayout) findViewById(R.id.print_text);
		print_pic = (LinearLayout) findViewById(R.id.print_code_and_pic);
		print_comm = (LinearLayout) findViewById(R.id.print_comm);
		text_index = (TextView) findViewById(R.id.index_text);
		pic_index = (TextView) findViewById(R.id.index_pic);
		comm_index = (TextView) findViewById(R.id.index_comm);
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("tagg", "sy581 activity");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.print_text581);
		initView();
		//savepic();
		Log.d("TAG", "sy581 printer activity");
		handler = new MyHandler();
		button_papercut = (Button) findViewById(R.id.button_papercut);
		buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

		IntentFilter pIntentFilter = new IntentFilter();
		pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
		registerReceiver(printReceive, pIntentFilter);

		editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
		editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
		editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
		editTextWordFont.setText("0");
		wordFont_textview = (TextView) findViewById(R.id.wordFont_textview);
		wordFont_textview.setText("0-4");
		editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
		editTextPrintGray.setText("0");
		textview_gray = (TextView) findViewById(R.id.textview_gray);
		textview_gray.setText("0-5");
		editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
		editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
		editTextContent = (EditText) findViewById(R.id.set_content);
		textPrintVersion = (TextView) findViewById(R.id.print_version);
		editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
		edittext_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
		edittext_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
		edittext_input_command = (EditText) findViewById(R.id.edittext_input_command);
		buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);
		set_tem = (EditText) findViewById(R.id.set_tem);
		print_setTem = (Button) findViewById(R.id.print_setTem);
		
		print_setTem.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(set_tem.getText().toString().equals("")) {
					Toast.makeText(PrinterActivitySY581.this,getString(R.string.tem_cannt_null), Toast.LENGTH_LONG).show();
					return;
				}
				int protectTem = Integer.parseInt(set_tem.getText().toString());
				if(protectTem<60 || protectTem>127) {
					Toast.makeText(PrinterActivitySY581.this,getString(R.string.tem_set_wrong), Toast.LENGTH_LONG).show();
					return;
				}
				try {
					ThermalPrinter.setTem(protectTem);
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(PrinterActivitySY581.this, getString(R.string.tem_set_success), Toast.LENGTH_LONG).show();
			}
		});

		buttonQrcodePrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				qrcodeStr = editTextQrcode.getText().toString();
				if (qrcodeStr == null || qrcodeStr.length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this,
								getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.D_barcode_loading));
						progressDialog.setMessage(getString(R.string.generate_barcode_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}

			}
		});
		
		editTextContent.setOnTouchListener(new OnTouchListener() {

			
			public boolean onTouch(View v, MotionEvent arg1) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		buttonBarcodePrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				barcodeStr = editTextBarcode.getText().toString();
				if (barcodeStr == null || barcodeStr.length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		buttonPaperWalkPrint = (Button) findViewById(R.id.print_paperWalk);
		buttonPaperWalkPrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				String exditText;
				exditText = editTextPaperWalk.getText().toString();
				if (exditText == null || exditText.length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.walk_paper_intput_value), Toast.LENGTH_LONG)
							.show();
					return;
				}
				paperWalk = Integer.parseInt(exditText);
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		buttonClearText = (Button) findViewById(R.id.clearText);
		buttonClearText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				editTextContent.setText("");
			}
		});
		buttonGetExampleText = (Button) findViewById(R.id.getPrintExample);
		buttonGetExampleText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String str = "\n-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "-----------------------------\n"
						+ "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "-----------------------------\n"
						+ "-----------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "-----------------------------\n";
				editTextContent.setText(str);
			}
		});

		buttonGetZhExampleText = (Button) findViewById(R.id.getZhPrintExample);
		buttonGetZhExampleText.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String str = "\n             烧烤" + "\n----------------------------" + "\n日期：2015-01-01 16:18:20"
						+ "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n----------------------------"
						+ "\n  项目          数量    单价    小计"
						+ "\n秘制烤羊腿     1       56       56" 
						+ "\n黯然牛排        2       24       48"
						+ "\n烤火鸡           2       50       100" 
						+ "\n炭烧鳗鱼        1       40       40" 
						+ "\n烤全羊           1       200     200"
						+ "\n荔枝树烧鸡     1       50       50" 
						+ "\n冰镇乳鸽        2       23       46" 
						+ "\n秘制烤羊腿     1       56       56" 
						+ "\n黯然牛排        2       24       48"
						+ "\n烤火鸡           2       50       100" 
						+ "\n炭烧鳗鱼        1       40       40" 
						+ "\n烤全羊           1       200     200"
						+ "\n荔枝树烧鸡     1       50       50" 
						+ "\n冰镇乳鸽        2       23       46" 
						+ "\n秘制烤羊腿     1       56       56" 
						+ "\n黯然牛排        2       24       48"
						+ "\n烤火鸡           2       50       100" 
						+ "\n炭烧鳗鱼        1       40       40" 
						+ "\n烤全羊           1       200     200"
						+ "\n荔枝树烧鸡     1       50       50" 
						+ "\n冰镇乳鸽        2       23       46" 
						+ "\n秘制烤羊腿     1       56       56" 
						+ "\n黯然牛排        2       24       48"
						+ "\n烤火鸡           2       50       100" 
						+ "\n炭烧鳗鱼        1       40       40" 
						+ "\n烤全羊           1       200     200"
						+ "\n荔枝树烧鸡     1       50       50" 
						+ "\n冰镇乳鸽        2       23       46" 
						+ "\n秘制烤羊腿     1       56       56" 
						+ "\n黯然牛排        2       24       48"
						+ "\n烤火鸡           2       50       100" 
						+ "\n炭烧鳗鱼        1       40       40" 
						+ "\n烤全羊           1       200     200"
						+ "\n荔枝树烧鸡     1       50       50" 
						+ "\n冰镇乳鸽        2       23       46" 
						+ "\n 合计：1000：00元" + "\n----------------------------"
						+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
						+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
				
				editTextContent.setText(str);
			}
		});

		buttonContentPrint = (Button) findViewById(R.id.print_content);//鏂囧瓧鎵撳嵃鎸夐挳
		buttonContentPrint.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				} else if (lineDistance > 255 || lineDistance < 1) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				} else if (wordFont > 4 || wordFont < 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				} else if (printGray < 0 || printGray > 5) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						//progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
						//		getString(R.string.printing_wait));
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
						if(circle)
							handler.sendEmptyMessageDelayed(402, 5000);
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
				
			}
		});

		button_maker = (Button) findViewById(R.id.button_maker);
		button_maker.setVisibility(View.GONE);
		button_maker.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				if (edittext_maker_search_distance.getText().length() == 0
						|| edittext_maker_walk_distance.getText().length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_search_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_search_distance.getText().toString()) > 255) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_walk_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_walk_distance.getText().toString()) > 255) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.maker),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.maker));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_papercut.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					public void run() {
						try {
							//ThermalPrinter.start(PrinterActivity.this);
							ThermalPrinter.checkStatus();
							ThermalPrinter.paperCut();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							//ThermalPrinter.stop(PrinterActivity.this);
						}
					}
				}).start();
			}
		});

		button_print_picture = (Button) findViewById(R.id.button_print_picture);
		button_print_picture.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_execute_command = (Button) findViewById(R.id.button_execute_command);
		button_execute_command.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				if (edittext_input_command.getText().toString() == null
						|| edittext_input_command.getText().toString().length() == 0) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(EXECUTECOMMAND, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_print_long_picture = (Button) findViewById(R.id.button_print_long_picture);
		button_print_long_picture.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTLONGPICTURE, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_print_long_text = (Button) findViewById(R.id.button_print_long_text);
		button_print_long_text.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTLONGTEXT, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		button_print_georgia = (Button) findViewById(R.id.print_georgia);
		button_print_georgia.setVisibility(View.GONE);
		button_print_georgia.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(PrinterActivitySY581.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				} else if (lineDistance > 255 || lineDistance < 1) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				} else if (wordFont > 4 || wordFont < 1) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				} else if (printGray < 0 || printGray > 12) {
					Toast.makeText(PrinterActivitySY581.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {
					printContent = "აშო, ჩელა, ვიშო ბუსქა,ოუ, ნანა, ოუ ნანა, ნანინა.სი მონობაშ, გეგაფილი,ოუ, ნანა, სკალი ცოდვა, ნანინა.\n"
							+ "მისამღერი:.ოუ, ნანა, ოუ ნანა, ნანინა…სი უჩხონჩხე, სი უგურე,ოუ, ნანა, სკალი ცოდვა, ნანინა.სი კისერი ელაფირი.ოუ, ნანა, სკალი ცოდვა, ნანინა.მისამღერი..";
					editTextContent.setText(printContent);
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						isDialogDismiss = false;
						/*progressDialog = ProgressDialog.show(PrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));*/
						if(progressDialog == null)
							progressDialog = new ProgressDialog(PrinterActivitySY581.this);
						progressDialog.setCancelable(false);
						progressDialog.setTitle(getString(R.string.bl_dy));
						progressDialog.setMessage(getString(R.string.printing_wait));
						progressDialog.show();
						handler.sendMessage(handler.obtainMessage(PRINTUNSUPPORTEDCONTENT, 1, 0, null));
					} else {
						Toast.makeText(PrinterActivitySY581.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		check_status = (Button) findViewById(R.id.check_status);
		check_status.setText("循环打印");
		check_status.setVisibility(View.GONE);
		check_status.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				circle = true;
				if(circle) {
					buttonContentPrint.performClick();
				}
				
			}
		});
		
	}
	
	public void selfCheck(View view){
		ThermalPrinterSY581.selfCheck();
		
		/*for(int i=0;i<3;i++){
		
			printTicket(getShoppingReceipt(), false, true);
			
		}*/
		
	}
	
	
	boolean getVersion;
	
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		dialog = new ProgressDialog(PrinterActivitySY581.this);
		dialog.setTitle(R.string.idcard_czz);
		dialog.setMessage(getText(R.string.watting));
		dialog.setCancelable(false);
		dialog.show();

		new Thread(new Runnable() {

			
			public void run() {
				try {
					ThermalPrinter.start(PrinterActivitySY581.this);
					ThermalPrinter.checkStatus();
					printVersion = ThermalPrinter.getVersion();
					Log.d("tagg", "printVersion:"+printVersion);
					getVersion = true;
					try {
						int deviceType = SystemUtil.getDeviceType();
						if(deviceType == StringUtil.DeviceModelEnum.TPS650T.ordinal()) {
							mSerialPort = new Serial("/dev/ttyS0", 460800, 0);
						}else if(deviceType == StringUtil.DeviceModelEnum.C1B.ordinal()){
							mSerialPort = new Serial(CheckC1Bprinter.checkSerialFromFile(), 460800, 0);
						}else if("C1".equals(SystemUtil.getInternalModel())){
							mSerialPort = new Serial("dev/ttyUSB0", 460800, 0);
						}else if(deviceType == StringUtil.DeviceModelEnum.TPS680.ordinal()){
							mSerialPort = new Serial("/dev/ttyACM0", 460800, 0);
						}else {
							mSerialPort = new Serial("/dev/ttyS4", 460800, 0);
						}
						
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					if(mSerialPort != null)
						mInputStream = mSerialPort.getInputStream();
					mReadThread = new ReadThread();
					mReadThread.start();
				} catch (TelpoException e) {
					e.printStackTrace();
				} finally {
					if (printVersion != null) {
						Message message = new Message();
						message.what = PRINTVERSION;
						message.obj = "1";
						handler.sendMessage(message);
					} else {
						Message message = new Message();
						message.what = PRINTVERSION;
						message.obj = "0";
						handler.sendMessage(message);
					}
				}
			}
		}).start();
	}

	private final BroadcastReceiver printReceive = new BroadcastReceiver() {
		
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
						BatteryManager.BATTERY_STATUS_NOT_CHARGING);
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
				// TPS390 can not print,while in low battery,whether is charging or not charging
				if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()) {
					if (level * 5 <= scale) {
						LowBattery = true;
					} else {
						LowBattery = false;
					}
				} else {
					if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
						if (level * 5 <= scale) {
							LowBattery = true;
						} else {
							LowBattery = false;
						}
					} else {
						LowBattery = false;
					}
				}
			}
			// Only use for TPS550MTK devices
			else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
				int status = intent.getIntExtra("action", 0);
				int level = intent.getIntExtra("level", 0);
				if (status == 0) {
					if (level < 1) {
						LowBattery = true;
					} else {
						LowBattery = false;
					}
				} else {
					LowBattery = false;
				}
			}
		}
	};

	private void noPaperDlg() {
		
		//ThermalPrinter.stop(PrinterActivity.this);
		isNoPaper = true;
		AlertDialog.Builder dlg = new AlertDialog.Builder(PrinterActivitySY581.this);
		dlg.setTitle(getString(R.string.noPaper));
		dlg.setMessage(getString(R.string.noPaperNotice));
		dlg.setCancelable(false);
		dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialogInterface, int i) {
				isNoPaper = false;
			}
		});
		try {
			dlg.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private class paperWalkPrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.walkPaper(paperWalk);
				handler.sendEmptyMessageDelayed(OVERTIME, 1000);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	//涓�缁寸爜
	private class barcodePrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.setGray(printGray);
				Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
				if (bitmap != null) {
					ThermalPrinter.printLogo(bitmap);
				}
				//Thread.sleep(500);
				ThermalPrinter.addString(barcodeStr);
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(100);
				handler.sendEmptyMessageDelayed(OVERTIME, 2000);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class qrcodePrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.setGray(printGray);
				Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
				if (bitmap != null) {
					ThermalPrinter.printLogo(bitmap);
				}
				//Thread.sleep(500);
				ThermalPrinter.addString(qrcodeStr);
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(100);
				handler.sendEmptyMessageDelayed(OVERTIME, 2000);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class contentPrintThread extends Thread {
		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.errorStop = false;
				ThermalPrinter.checkStatus();
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
				ThermalPrinter.setLeftIndent(leftDistance);
				ThermalPrinter.setLineSpace(lineDistance);
				if (wordFont == 0) {
					ThermalPrinter.setFontSize(0);
				} else if (wordFont == 1) {
					ThermalPrinter.setFontSize(1);
				} else if (wordFont == 2) {
					ThermalPrinter.setFontSize(2);
				} else if (wordFont == 3) {
					ThermalPrinter.setFontSize(3);
				} else if (wordFont == 4) {
					ThermalPrinter.setFontSize(4);
				} else if (wordFont == 5) {
					ThermalPrinter.setFontSize(5);
				} else if (wordFont == 6) {
					ThermalPrinter.setFontSize(6);
				} else if (wordFont == 7) {
					ThermalPrinter.setFontSize(7);
				}
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.addString(printContent);
				ThermalPrinter.printString();
		        ThermalPrinter.walkPaper(200);
		        handler.sendEmptyMessageDelayed(OVERTIME, 10000);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class MakerThread extends Thread {

		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
						Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printPicture extends Thread {

		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				
				
				ThermalPrinter.checkStatus();
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
				
				//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.syhlogo);
				//ThermalPrinter.printLogo581Left(ThermalPrinter.ALGIN_LEFT, getBitmap(PrinterActivitySY581.this, R.drawable.syhlogo));
				
				/*Bitmap bitmap = getBitmap(PrinterActivitySY581.this, R.drawable.black);
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, 576, bitmap.getHeight());
				ThermalPrinter.printLogo(bitmap);
				
				ThermalPrinter.walkPaper(200);*/
				
				ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.syhlogo));
				ThermalPrinter.walkPaper(200);
				
				handler.sendEmptyMessageDelayed(OVERTIME, 4000);
				
				
				//巨龙测试打印
				/*while (true && !isNoPaper) {
					ThermalPrinter.errorStop = false;
					ThermalPrinter.checkStatus();
					ThermalPrinter.setGray(printGray);
					ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
					
					Bitmap bitmap = getBitmap(PrinterActivitySY581.this, R.drawable.head);
					ThermalPrinter.printLogo(bitmap);
					
					sleep(2000);
					
					ThermalPrinter.paperCut();
					
					sleep(2000);
				}*/
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class executeCommand extends Thread {

		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.sendCommand(edittext_input_command.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printLongPicture extends Thread {

		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				printingLongPicture = true;
				ThermalPrinter.checkStatus();
				ThermalPrinter.setGray(printGray);
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_MIDDLE);
				//ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.b1));
				ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.b1));
				ThermalPrinter.walkPaper(100);
				
				ThermalPrinter.checkStatus();
				//ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.p001));
				ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.p001));
				ThermalPrinter.walkPaper(100);
				
				ThermalPrinter.checkStatus();
				//ThermalPrinter.printLogo(BitmapFactory.decodeResource(getResources(), R.drawable.op));
				ThermalPrinter.printLogo(getBitmap(PrinterActivitySY581.this, R.drawable.op));
				ThermalPrinter.walkPaper(100);
				handler.sendEmptyMessageDelayed(OVERTIME, 20000);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printLongText extends Thread {
		
		public void run() {
			super.run();
			try {
				//ThermalPrinter.start(PrinterActivity.this);
				ThermalPrinter.checkStatus();
				ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);
				ThermalPrinter.setLeftIndent(leftDistance);
				ThermalPrinter.setLineSpace(5);
				if (wordFont == 0) {
					ThermalPrinter.setFontSize(0);
				} else if (wordFont == 1) {
					ThermalPrinter.setFontSize(1);
				} else if (wordFont == 2) {
					ThermalPrinter.setFontSize(2);
				} else if (wordFont == 3) {
					ThermalPrinter.setFontSize(3);
				} else if (wordFont == 4) {
					ThermalPrinter.setFontSize(4);
				} else if (wordFont == 5) {
					ThermalPrinter.setFontSize(5);
				} else if (wordFont == 6) {
					ThermalPrinter.setFontSize(6);
				} else if (wordFont == 7) {
					ThermalPrinter.setFontSize(7);
				}
				ThermalPrinter.setGray(printGray);
				printingLongText = 1;
				ThermalPrinter.addString("1\n" + getString(R.string.printContent1));
				ThermalPrinter.addString("2\n" + getString(R.string.printContent1));
				ThermalPrinter.addString("3\n" + getString(R.string.printContent1));
				/*ThermalPrinter.addString("4\n" + printContent1);
				ThermalPrinter.addString("5\n" + printContent1);
				ThermalPrinter.addString("6\n" + printContent1);
				ThermalPrinter.addString("7\n" + printContent1);
				ThermalPrinter.addString("8\n" + printContent1);*/
				ThermalPrinter.printString();
				ThermalPrinter.walkPaper(50);
				if(wordFont == 0) {
					handler.sendEmptyMessageDelayed(OVERTIME, 20000);
				}else {
					handler.sendEmptyMessageDelayed(OVERTIME, 40000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}

	private class printUnsupportedText extends Thread {
		
		public void run() {
			super.run();
			try {
				int printer_type = SystemUtil.getPrinterType();
				int paper_width = 576;
				if(printer_type == SystemUtil.PRINTER_PT486F08401MB) {
					paper_width = 384;
				}
				//ThermalPrinter.start(PrinterActivity.this);
				Paint paint = new Paint();
				int fontSize = wordFont * 12;
				paint.setTextSize(fontSize);
				int gray = 0x7F;
				gray -= 0x80 / 12 * printGray;
				int color = 0xFF;
				color = color << 8 | gray;
				color = color << 8 | gray;
				color = color << 8 | gray;
				paint.setColor(color);
				Rect bounds = new Rect();
				paint.getTextBounds(printContent, 0, 1, bounds);
				int line_height = bounds.height();
				line_height = line_height > fontSize ? line_height : fontSize;
				line_height = line_height > lineDistance ? line_height : lineDistance;
				int length = printContent.length();
				int line_start_index = 0;
				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (i = 0; i < length; i++) {
					if (printContent.charAt(i) == '\n') {
						sb.append(printContent.subSequence(line_start_index, i));
						line_start_index = i;
						continue;
					}
					if (leftDistance + paint.measureText(printContent, line_start_index, i) > paper_width) {
						sb.append(printContent.subSequence(line_start_index, i - 1));
						sb.append('\n');
						line_start_index = i - 1;
						continue;
					}
				}
				sb.append(printContent.subSequence(line_start_index, i - 1));
				String[] lines = sb.toString().split("\n");
				int line = lines.length;
				bitmap = Bitmap.createBitmap(576, line * line_height, Bitmap.Config.ARGB_8888);
				bitmap.eraseColor(Color.WHITE);
				Canvas canvas = new Canvas(bitmap);
				for (i = 0; i < lines.length; i++) {
					canvas.drawText(lines[i], leftDistance, (i + 1) * line_height, paint);
				}

				ThermalPrinter.walkPaper(100);
				ThermalPrinter.printLogo(bitmap);
				ThermalPrinter.walkPaper(100);
				((ImageView) findViewById(R.id.view)).setImageBitmap(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
				bitmap = null;
				//ThermalPrinter.stop(PrinterActivity.this);
			}
		}
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		circle = false;
		cancelProgressDialog();
		unregisterReceiver(printReceive);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
		ThermalPrinter.stop();
//		System.exit(0);
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 生成条码
	 * 
	 * @param str
	 *            条码内容
	 * @param type
	 *            条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
	 *            EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
	 *            RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
	 * @param bmpWidth
	 *            生成位图宽,宽不能大于384，不然大于打印纸宽度
	 * @param bmpHeight
	 *            生成位图高，8的倍数
	 */

	public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight)
			throws WriterException {
		Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
		mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组（一直横着排）
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public void selectIndex(View view) {
		switch (view.getId()) {
		case R.id.index_text:
			text_index.setEnabled(false);
			pic_index.setEnabled(true);
			comm_index.setEnabled(true);
			print_text.setVisibility(View.VISIBLE);
			print_pic.setVisibility(View.GONE);
			print_comm.setVisibility(View.GONE);

			break;

		case R.id.index_pic:

			text_index.setEnabled(true);
			pic_index.setEnabled(false);
			comm_index.setEnabled(true);
			print_text.setVisibility(View.GONE);
			print_pic.setVisibility(View.VISIBLE);
			print_comm.setVisibility(View.GONE);
			break;
		case R.id.index_comm:
			text_index.setEnabled(true);
			pic_index.setEnabled(true);
			comm_index.setEnabled(false);
			print_text.setVisibility(View.GONE);
			print_pic.setVisibility(View.GONE);
			print_comm.setVisibility(View.VISIBLE);

			break;
		}
	}

	private void savepic() {
		File file = new File(picturePath);
		if (!file.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("syhlogo.png");
				fos = new FileOutputStream(file);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file2 = new File(picturePath2);
		if (!file2.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("op.png");
				fos = new FileOutputStream(file2);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file3 = new File(picturePath3);
		if (!file3.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("test.bmp");
				fos = new FileOutputStream(file3);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file4 = new File(picturePath4);
		if (!file4.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("test1.png");
				fos = new FileOutputStream(file4);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void cancelProgressDialog() {
		if (handler.hasMessages(OVERTIME)) {
			handler.removeMessages(OVERTIME);
		}
		isDialogDismiss = true;
		if (progressDialog != null && !PrinterActivitySY581.this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	

	public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
	
	private void sleepThread(int delay){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
