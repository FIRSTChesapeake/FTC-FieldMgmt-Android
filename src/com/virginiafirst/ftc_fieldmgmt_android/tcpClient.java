/**
 * 
 */
package com.virginiafirst.ftc_fieldmgmt_android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.virginiafirst.ftc_fieldmgmt_android.startFrag.OnSettingsGivenListener;

import android.content.Context;
import android.widget.Toast;
import FTCMgrShared.*; 
/**
 * @author Matthew Glennon
 * Email: mglennon@virginiafirst.org
 *
 */
public class tcpClient {
	
	private SocketAddress		serverEndpoint	= null;
	private Socket				cSocket			= null;
	private Thread 				cThread 		= null;
	private ObjectInputStream   ObjInStream   	= null;
	private ObjectOutputStream  ObjOutStream   	= null;

	private int					socketTimeout	= 5000;
	private boolean		stopRequested	= false;

	protected String 			sHost;
	protected String			sPass;
	protected int	 			sPort;

	private boolean 			iConnected		= false;

	private MainActivity myParent = null;
	
	public boolean isConnected(){
		return iConnected;
	}
	private void NewFieldPack(TCPPack pack){
		System.out.println("Back Recved. Giving it to Main.");
		myParent.FieldPackRcvd(pack);
	}
	
	public void abort(){
		try {
			stopRequested = true;
			cThread.interrupt();
			cSocket.close();
		} catch (IOException e) {
			System.out.println("IO Exception Closing Port: "+ e.getMessage());
		}
	}
	
	public tcpClient(MainActivity obj, String Host, int Port, String authPass) {
		myParent = obj;
		sHost = Host;
		sPort = Port;
		sPass = authPass;
		System.out.println("Trying Connection to "+Host+" on port "+Port);
		try {
			serverEndpoint = new InetSocketAddress(sHost,sPort);
			cSocket = new Socket();
			cSocket.connect(serverEndpoint,socketTimeout);
            while (!cSocket.isConnected()){
            	// Do nothing.
            }
            if(cSocket.isConnected()) {
            	System.out.println("Connected!");
            	iConnected = true;
            	cThread = new clientThread();
                cThread.start();
            }
		} catch (IOException e){
			System.out.println("IP Exception Connecting: "+e.getMessage());
		}
        //catch (final Exception e) {
        //	System.out.println("Exception Connecting: "+e.getMessage());
        //}
	}
	private class clientThread extends Thread {

		public void run() {
			
			try {
                // Create input and output streams for this client.
				System.out.println("Building Ouput Stream..");
				ObjOutStream = new ObjectOutputStream(cSocket.getOutputStream());
				System.out.println("Flushing Ouput Stream..");
				ObjOutStream.flush();
				System.out.println("Building Input Stream..");
                ObjInStream = new ObjectInputStream(cSocket.getInputStream());
                System.out.println("Listening for Info..");
                while (!stopRequested) {
                	System.out.println("Starting Listening Loop..");
                	
                    // Do the magic! ---------------------------------------
                    final Object inObj = ObjInStream.readObject();
                    System.out.println("Object Received. Processing..");
                    final TCPPack inPack = (TCPPack) inObj;
                    System.out.print("Server Message Recvd: "+ inPack.PackType.toString());
                    switch (inPack.PackType) {
                        case NONE:
                        		
                            break;
                        case REFRESH_REQUEST:
                        		
                            break;
                        case REFRESH:
                        	NewFieldPack(inPack);
                        	break;
                        case BYE:
                        	stopRequested = true;
                        	break;
                    }
                }
                // Clean up.
                if(cThread!=null) cThread = null;
                // Close up shop.
                ObjOutStream.close();
                ObjInStream.close();
                cSocket.close();

			} catch (final ClassNotFoundException e) {
				System.out.println("Class Exception: "+e.getMessage());
				
            } catch (final SocketException e) {
            	System.out.println("Socket Exception: "+e.getMessage());
            } catch (final IOException e) {
            	System.out.println("IO Exception: "+e.getMessage());
            }
		}
	}
}
