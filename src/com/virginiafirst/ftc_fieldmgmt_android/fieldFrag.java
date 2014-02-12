package com.virginiafirst.ftc_fieldmgmt_android;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class fieldFrag extends Fragment{
	
	private int myFieldID = 0;
	
	private TextView FieldID, R1, R2, B1, B2;
	
	public fieldFrag(int FieldID){
		myFieldID = FieldID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fieldfrag,container, false);
		
		FieldID = (TextView) rootView.findViewById(R.id.FieldID);
		
		R1 = (TextView) rootView.findViewById(R.id.RobotR1);
		R2 = (TextView) rootView.findViewById(R.id.RobotR2);
		B1 = (TextView) rootView.findViewById(R.id.RobotB1);
		B2 = (TextView) rootView.findViewById(R.id.RobotB2);
		
		FieldID.setText(String.valueOf(myFieldID));
		
		UpdateRobots(-1, -1, -1, -1);
		
		return rootView;
	}
	public void UpdateRobots(int iR1, int iR2, int iB1, int iB2){
		UpdateOneRobot(R1, iR1);
		UpdateOneRobot(R2, iR2);
		UpdateOneRobot(B1, iB1);
		UpdateOneRobot(B2, iB2);
	}
	private TextView UpdateOneRobot(TextView tv, int state){
		switch (state){
			case 0:
				tv.setBackgroundColor(Color.RED);
				break;
			case 1:
				tv.setBackgroundColor(Color.YELLOW);
				break;
			case 2:
				tv.setBackgroundColor(Color.GREEN);
				break;
			default:
				tv.setBackgroundColor(Color.BLUE);
				break;
		}
		return tv;
	}
}
