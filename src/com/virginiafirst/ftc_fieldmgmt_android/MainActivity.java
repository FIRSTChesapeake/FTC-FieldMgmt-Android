package com.virginiafirst.ftc_fieldmgmt_android;

import java.util.Timer;
import java.util.TimerTask;

import com.virginiafirst.ftc_fieldmgmt_android.startFrag.OnSettingsGivenListener;

import FTCMgrShared.TCPPack;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends FragmentActivity implements OnSettingsGivenListener {

	Fragment fragment;
	Button btnField1, btnField2, btnDisconnect;
	private TextView LogField, statField;
	
	private startFrag connFrag = null;
	private fieldFrag fieldFrag1 = new fieldFrag(1);
	private fieldFrag fieldFrag2 = new fieldFrag(2);

	static tcpClient	conn 			= null;
	Handler		handler			= null;
	int			tcpPort			= 2213;
	
	private Timer timer = new Timer();

	public SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Get my preferences
		settings = getPreferences(0);

		btnField1 = (Button) findViewById(R.id.btnField1);
		btnField2 = (Button) findViewById(R.id.btnField2);
		btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		
		LogField = (TextView) findViewById(R.id.LogField);
		statField = (TextView) findViewById(R.id.statDisplay);
		
		LogAdd("Checking Wifi..");
		// Check Wifi
		if(!setWifi()){
			LogAdd("Failed to enable Wifi!");
			System.out.println("FAILED TO ENABLE WIFI!");
		}
	
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		String serverIP = settings.getString("serverIP","");
		String authPass = settings.getString("authPass","");

		connFrag = new startFrag(serverIP,authPass);
		ft.add(R.id.mainFrag, connFrag);
		ft.commit();

		btnField1.setOnClickListener(btnOnClickListener);
		btnField2.setOnClickListener(btnOnClickListener);
		btnDisconnect.setOnClickListener(btnOnClickListener);

		btnField1.setEnabled(false);
		btnField2.setEnabled(false);
		btnDisconnect.setEnabled(false);
		LogAdd("Startup Complete.");
				
		statField.setText("Status: Init Complete");
		timer.schedule(heartbeat, 3000, 1000);
	}
	
	private TimerTask heartbeat = new TimerTask(){
		@Override
		public void run() {
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(conn==null){
						statField.setText("Status: Disconnected (null)");
					} else if(!conn.isConnected()){
						statField.setText("Status: Disconnected");
					} else {
						statField.setText("Status: Connected");
					}
				}
			});
		}
	};
	@Override
	protected void onStop(){
		super.onStop();
		SharedPreferences.Editor editor = settings.edit();
		editor.commit();
	}

	Button.OnClickListener btnOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){

			if(v == btnField1){
				ChangeFrag(fieldFrag1);
			} else if(v == btnField2){
				ChangeFrag(fieldFrag2);
			} else if(v == btnDisconnect){
				LogAdd("Disconnecting.");
				conn.abort();
				UpdateForConnection(false);
				
			} else {
				ChangeFrag(new startFrag("",""));
			}
		}
	};

	public void LogAdd(String str){
		final String newLine = str; 
		runOnUiThread(new Runnable(){
			@Override
			public void run(){
				final String cur = LogField.getText().toString();
				final String newline = "\n";
				LogField.setText(newLine + newline + cur);
			}
		});
	}
	
	private void ChangeFrag(Fragment newFragment){
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(R.id.mainFrag, newFragment);
		// Don't exit when we go back
		trans.addToBackStack(null);
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		trans.commit();
	}

	private boolean setWifi(){
		final WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if(wifiMgr.isWifiEnabled()){
			return true;
		}else {
			if(wifiMgr.setWifiEnabled(true)){
				return true;
			} else {
				return false;
			}
		}

	}

	private void GetConnected(final MainActivity obj, final String serverIP, final String authPass){
		LogAdd("Connecting..");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.arg1==1){
					LogAdd("Connected!");
					UpdateForConnection(true);
				} else {
					LogAdd("Connection Failed!");
					UpdateForConnection(false);
				}
			}
		};
		final ProgressDialog dialog		= ProgressDialog.show(this, "Connecting","Wait..");
		Thread a = new Thread(){
			@Override
			public void run(){
				conn = new tcpClient(obj, serverIP, tcpPort, authPass);
				handler.post(new Runnable(){
					@Override
					public void run(){
						dialog.dismiss();
						Message msg = new Message();
						if(conn.isConnected()) msg.arg1 = 1;
						else msg.arg1 = 0;
						handler.dispatchMessage(msg);
					}
				});
			}
		};
		a.start();
	}
	private void UpdateForConnection(boolean isConnected){
		btnField1.setEnabled(isConnected);
		btnField2.setEnabled(isConnected);
		btnDisconnect.setEnabled(isConnected);
		if(isConnected) ChangeFrag(fieldFrag1);
		if(!isConnected) ChangeFrag(connFrag);
	}
	@Override
	public void onSettingsGiven(String serverIP, String authPass, boolean remember) {
		Editor editor = settings.edit();
		if(remember){
			editor.putString("serverIP", serverIP);
			editor.putString("authPass", authPass);
		} else {
			editor.remove("serverIP");
			editor.remove("authPass");
		}
		editor.commit();

		GetConnected(this, serverIP,authPass);
	}
	public void FieldPackRcvd(TCPPack pack) {
		System.out.println("Field Data Rcvd!");
		if(pack.FieldID == 1) fieldFrag1.UpdateField(pack);
		if(pack.FieldID == 2) fieldFrag2.UpdateField(pack);
	}
}
