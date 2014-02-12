package com.virginiafirst.ftc_fieldmgmt_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class startFrag extends Fragment {
	
	EditText serverIP, authPass;
	CheckBox chkRemember;
	Button btnConnect;
	
	String tempServer = "";
	String tempPass = "";
	
	OnSettingsGivenListener mListener;
	
	public startFrag(String INserverIP, String INauthPass){
		
		tempServer = INserverIP;
		tempPass = INauthPass;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.startfrag,container, false);
		
		// Get my controls
		serverIP = (EditText) rootView.findViewById(R.id.serverIP);
		authPass = (EditText) rootView.findViewById(R.id.authPass);
		chkRemember = (CheckBox) rootView.findViewById(R.id.chkRemember);
		btnConnect = (Button) rootView.findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(btnOnClickListener);
		serverIP.setText(tempServer);
		authPass.setText(tempPass);
		return rootView;
	}
	public interface OnSettingsGivenListener {
		public void onSettingsGiven(String serverIP, String authPass, boolean remember);
	}
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (OnSettingsGivenListener) activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement OnSdettingsGivenListener");
		}
	}
	Button.OnClickListener btnOnClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			mListener.onSettingsGiven(serverIP.getText().toString(), authPass.getText().toString(), chkRemember.isChecked());
		}
	};
}
