package com.virginiafirst.ftc_fieldmgmt_android;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import FTCMgrShared.*;
public class fieldFrag extends Fragment{
	
	private int myFieldID = 0;
	
	private TextView FieldID, textR1, textR2, textB1, textB2;
	
	public fieldFrag(int FieldID){
		myFieldID = FieldID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fieldfrag,container, false);
		
		FieldID = (TextView) rootView.findViewById(R.id.FieldID);
		
		textR1 = (TextView) rootView.findViewById(R.id.RobotR1);
		textR2 = (TextView) rootView.findViewById(R.id.RobotR2);
		textB1 = (TextView) rootView.findViewById(R.id.RobotB1);
		textB2 = (TextView) rootView.findViewById(R.id.RobotB2);
		
		FieldID.setText(String.valueOf(myFieldID));
		
		UpdateField(new TCPPack());
		
		return rootView;
	}
	public void UpdateField(TCPPack data){
		//FIXME Add Field Status
		UpdateOneRobot(textR1, data.R1);
		UpdateOneRobot(textR2, data.R2);
		UpdateOneRobot(textB1, data.B1);
		UpdateOneRobot(textB2, data.B2);
	}
	private TextView UpdateOneRobot(TextView tv, Robot robot){
		switch (robot.Status){
			case RED:
				tv.setBackgroundColor(Color.RED);
				break;
			case YELLOW:
				tv.setBackgroundColor(Color.YELLOW);
				break;
			case GREEN:
				tv.setBackgroundColor(Color.GREEN);
				break;
			default:
				tv.setBackgroundColor(Color.BLUE);
				break;
		}
		return tv;
	}
}
