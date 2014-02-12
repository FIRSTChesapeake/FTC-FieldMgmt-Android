/**
 * 
 */
package com.virginiafirst.ftc_fieldmgmt_android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Matthew Glennon
 * Email: mglennon@virginiafirst.org
 *
 */
public class tcpClient {

	private SocketAddress		serverEndpoint	= null;
	private Socket				cSocket			= null;
	private Thread 				cThread 		= null;
	private ObjectInputStream   ObjStream   	= null;

	private int					socketTimeout	= 5000;
	private boolean 			stopRequested	= false;

	protected String 			sHost;
	protected String			sPass;
	protected int	 			sPort;

	private boolean 			iConnected		= false;
	
	public boolean isConnected(){
		return iConnected;
	}
	
	public tcpClient(String Host, int Port, String authPass) {
		sHost = Host;
		sPort = Port;
		sPass = authPass;
		System.out.println("Trying Connection to "+Host+" on port "+Port);
		try {
			serverEndpoint = new InetSocketAddress(sHost,sPort);
			cSocket = new Socket();
			cSocket.connect(serverEndpoint,socketTimeout);
            if(cSocket != null) {
            	System.out.println("Connected!");
            	iConnected = true;
            }
			cThread = new clientThread();
            cThread.start();
        } catch (final Exception e) {
        	System.out.println("Exception Connecting: "+e.getMessage());
        }
	}
	private class clientThread extends Thread {

		public void run() {
			try {
                // Create input and output streams for this client.
                ObjStream = new ObjectInputStream(cSocket.getInputStream());
                while (!stopRequested) {
                    // Do the magic! ---------------------------------------
                    final Object inObj = ObjStream.readObject();
                    final TCPPack inPack = (TCPPack) inObj;
                    switch (inPack.PackType) {
                        case NONE:
                        		
                            break;
                        case REFRESH_REQUEST:
                        		
                            break;
                        case BYE:
                        	stopRequested = true;
                        	break;
                    }
                }
                // Clean up.
                if(cThread!=null) cThread = null;
                // Close up shop.
                ObjStream.close();
                cSocket.close();

			} catch (final ClassNotFoundException e) {

            } catch (final SocketException e) {
              System.out.println("Socket Failed: "+e.getMessage());
            } catch (final IOException e) {
              System.out.println("IO Failed: "+e.getMessage());
            }
		}
	}
}
