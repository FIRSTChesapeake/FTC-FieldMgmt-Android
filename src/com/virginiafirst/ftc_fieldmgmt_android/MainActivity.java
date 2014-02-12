package com.virginiafirst.ftc_fieldmgmt_android;

import com.virginiafirst.ftc_fieldmgmt_android.startFrag.OnSettingsGivenListener;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends FragmentActivity implements OnSettingsGivenListener{

	Fragment fragment;
	Button btnField1, btnField2;
	
	tcpClient	conn 			= null;
	Handler		handler			= null;
	int			tcpPort			= 2213;
	
	public SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get my preferences
		settings = getPreferences(0);
		
		btnField1 = (Button) findViewById(R.id.btnField1);
		btnField2 = (Button) findViewById(R.id.btnField2);

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		String serverIP = settings.getString("serverIP","");
		String authPass = settings.getString("authPass","");
						
		startFrag welcome = new startFrag(serverIP,authPass);
		ft.add(R.id.mainFrag, welcome);
		ft.commit();

		btnField1.setOnClickListener(btnOnClickListener);
		btnField2.setOnClickListener(btnOnClickListener);
		
		btnField1.setEnabled(false);
		btnField2.setEnabled(false);
	}
	@Override
	protected void onStop(){
		super.onStop();
		SharedPreferences.Editor editor = settings.edit();
		editor.commit();
	}
	
	Button.OnClickListener btnOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			Fragment newFragment;
			if(v == btnField1){
				newFragment = new fieldFrag(1);
			} else if(v == btnField2){
				newFragment = new fieldFrag(2);
			} else {
				newFragment = new startFrag("","");
			}
			FragmentTransaction trans = getFragmentManager().beginTransaction();
			trans.replace(R.id.mainFrag, newFragment);
			// Don't exit when we go back
			trans.addToBackStack(null);
			trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			trans.commit();
		}
	};

	private void GetConnected(final String serverIP, final String authPass){
		handler = new Handler();
		final ProgressDialog dialog		= ProgressDialog.show(this, "Connecting","Wait..");
		conn = new tcpClient(serverIP, tcpPort, authPass);
		new Thread(){
			@Override
			public void run(){
				handler.post(new Runnable(){
					@Override
					public void run(){
						dialog.dismiss();
					}
				});
				if (conn.isConnected()){
					btnField1.setEnabled(true);
					btnField2.setEnabled(true);
				}
			}
		}.start();
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
		
		GetConnected(serverIP,authPass);
	}
}
