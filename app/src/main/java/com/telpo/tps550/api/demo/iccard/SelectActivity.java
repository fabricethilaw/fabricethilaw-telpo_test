package com.telpo.tps550.api.demo.iccard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.telpo.tps550.api.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectActivity extends Activity {
	private static final String TAG = "SelectActivity";
	private UsbManager mManager;
	private String readerName = "";
	
	private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private List<String> firstList = new ArrayList<String>();
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_activity);
	}
	
	
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		toRegisterReceiver();
	}
	
	
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	private void toRegisterReceiver(){
		// Register receiver for USB permission
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mReceiver, filter);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Log.d(TAG, "Device Detached");
				onDetach(intent);
			}
		}
	};
	
	private void onDetach(Intent intent){
		UsbDevice   udev = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if (udev != null ) {
			if (isAlcorReader(udev) == true ) {
				
			}
		}
		else {
			Log.d(TAG,"usb device is null");
		}
	}
	
	
	private UsbDevice getSpinnerSelect(){
		String deviceName;
		deviceName= readerName;
		if (deviceName != null) {
			// For each device
			for (UsbDevice device : mManager.getDeviceList().values()) {
				if (deviceName.equals(pid2DevName (device.getProductId())+"-"+device.getDeviceName())) {
					return device;
				}
			}
		}
		return null;
	}
	
	
	//Button select usbReader
	public void selectReader(View view) {
		firstList.clear();
		EnumeDev();
		UsbDevice udev = getSpinnerSelect();
		ShowDialog();
	}
	
	public void ShowDialog() {
        Context context = SelectActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.formcommonlist, null);
        ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
        MyAdapter adapter = new MyAdapter(context, firstList);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                Log.d(TAG, ""+firstList.get(positon));
                readerName = firstList.get(positon);
                openAT88();
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }
	
    class MyAdapter extends BaseAdapter {
        private List<String> mlist;
        private Context mContext;

        public MyAdapter(Context context, List<String> list) {
            this.mContext = context;
            mlist = new ArrayList<String>();
            this.mlist = list;
        }

        
        public int getCount() {
            return mlist.size();
        }

        
        public Object getItem(int position) {
            return mlist.get(position);
        }

        
        public long getItemId(int position) {
            return position;
        }

        
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.rtu_item,null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView)convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.name.setText(mlist.get(position).toString());
            return convertView;
        }
        class ViewHolder{
            TextView name;
        }
    }

	
	private void openAT88() {
		try{
			UsbDevice dev = getSpinnerSelect();
			if (dev == null)
			{
				// error
				//return;
			}
			Intent intent = new Intent();
			Bundle b = new  Bundle();
			
			intent.setClass(SelectActivity.this, AT88SC153Activity.class);

			b.putParcelable(USB_SERVICE, dev);
			intent.putExtras(b);
			this.startActivity(intent);

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private int EnumeDev()
	{ 
		boolean isReaderFound = false;
		UsbDevice device = null;    
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while(deviceIterator.hasNext()){
			device = deviceIterator.next();   
			Log.d(TAG,"vid&pid:"+ Integer.toHexString(device.getVendorId()) +" " +Integer.toHexString(device.getProductId()));

			if(isAlcorReader(device)) 
			{   
				isReaderFound = true;
				readerName = pid2DevName (device.getProductId())+"-"+device.getDeviceName();
				Log.d(TAG, "readerName is :"+readerName);
				firstList.add(readerName);
			}
			else
			{
				Log.d(TAG,"readerName is wrong");
			}
		}
		if (isReaderFound == false)
		{
			Log.d(TAG,"No Supported Reader Been Found");
		}
		return 0;
	}
	
	private String pid2DevName(int pid) {
		String name = null;
		switch (pid)
		{
		case 0x9520:
		case 0x9522:
		case 0x9540:
		case 0x9525:
		case 0x9563:
			name = new String("SAM Card Reader");
			break;
		case 0x9526:
			name = new String("Smart Card Reader/NFC Reader");
			break;
		case 0x9571:
			name = new String("NFC Reader");
			break;
		case 0x9572:
		case 0x9573:
			name = new String("Smart Card Reader/NFC Reader");
			break;
		}
		return name; 
	}
	
	private boolean isAlcorReader(UsbDevice udev){
		if (udev.getVendorId() == 0x058f
				&& ((udev.getProductId() == 0x9540) 
						|| (udev.getProductId() == 0x9520)  || (udev.getProductId() == 0x9522)
						|| (udev.getProductId() == 0x9525) || (udev.getProductId() == 0x9526) 
						)
				) {
			
			return true;
		}
		else if (udev.getVendorId() == 0x2CE3 
				&& ((udev.getProductId() == 0x9571) || (udev.getProductId() == 0x9572) || (udev.getProductId() == 0x9563)) || (udev.getProductId() == 0x9573)) {
			return true;
		}
		return false;
	}
	
}
