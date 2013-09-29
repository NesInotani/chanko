package com.chanko.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 * 
 */
public class BluetoothChatService {
	// Debugging
	private static final String DTAG = BluetoothChatService.class
			.getSimpleName();

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";
	private static final String NAME_INSECURE = "BluetoothChatInsecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID_INSECURE = UUID
			.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

	// Member field
	private final BluetoothAdapter bluetoothAdapter;
	private final Handler handler;
	private AcceptThread secureAcceptThread;
	private AcceptThread insecureAcceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private int state;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	/**
	 * Constructor. Prepare a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send message back to the UI Activity
	 */
	public BluetoothChatService(Context context, Handler handler) {
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.state = STATE_NONE;
		this.handler = handler;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		Log.d(DTAG, "setState() " + this.state + " -> " + state);
		this.state = state;

		// Give the new state to the Handler so the UI Activity can update
		handler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1)
				.sendToTarget();
	}

	/**
	 * Return the current connection state.
	 * 
	 * @return current connection state
	 */
	public synchronized int getState() {
		return state;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		Log.d(DTAG, "start");

		// Cancel any thread attempting to make a connection
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		// Cancel any thread currently running a connection
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (secureAcceptThread == null) {
			secureAcceptThread = new AcceptThread(true);
			secureAcceptThread.start();
		}
		if (insecureAcceptThread == null) {
			insecureAcceptThread = new AcceptThread(false);
			insecureAcceptThread.start();
		}
	}

	/**
	 * Start the ConnecThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BlutoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true), Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		Log.d(DTAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (state == STATE_CONNECTING) {
			if (connectThread != null) {
				connectThread.cancel();
				connectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		// Start the thread connect with the given device
		connectThread = new ConnectThread(device, secure);
		connectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device, final String socketType) {
		Log.d(DTAG, "connected, Socket Type: " + socketType);

		// Cancel the thread that completed the connection
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		// Cancel any thread currently running a connection
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (secureAcceptThread != null) {
			secureAcceptThread.cancel();
			secureAcceptThread = null;
		}
		if (insecureAcceptThread != null) {
			insecureAcceptThread.cancel();
			insecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		connectedThread = new ConnectedThread(socket, socketType);
		connectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = handler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		handler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		Log.d(DTAG, "stop");

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		if (secureAcceptThread != null) {
			secureAcceptThread.cancel();
			secureAcceptThread = null;
		}

		if (insecureAcceptThread != null) {
			insecureAcceptThread.cancel();
			insecureAcceptThread = null;
		}

		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronized a copy of the ConnectedThread
		synchronized (this) {
			if (state != STATE_CONNECTED) {
				return;
			}
			r = connectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = handler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
		msg.setData(bundle);
		handler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = handler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
		msg.setData(bundle);
		handler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled)
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket serverSocket;
		private String socketType;

		public AcceptThread(boolean secure) {
			BluetoothServerSocket tmp = null;
			socketType = secure ? "Secure" : "Insecure";

			// Create a new listening server socket
			try {
				if (secure) {
					tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
							NAME_SECURE, MY_UUID_SECURE);
				} else {
					tmp = bluetoothAdapter
							.listenUsingInsecureRfcommWithServiceRecord(
									NAME_INSECURE, MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(DTAG, "Socket Type: " + socketType + " listen() failed",
						e);
			}
			serverSocket = tmp;
		}

		@Override
		public void run() {
			Log.d(DTAG, "Socket Type: " + socketType + " BEGIN acceptThread"
					+ this);
			setName("AcceptThread" + socketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (state != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = serverSocket.accept();
				} catch (IOException e) {
					Log.e(DTAG, "Socket Type: " + socketType
							+ "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (state) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(),
									socketType);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate
							// new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(DTAG, "Could not close unwanted socket",
										e);
							}
							break;
						}

					}
				}
			}
			Log.i(DTAG, "END acceptThread, socket Type: " + socketType);
		}

		public void cancel() {
			Log.d(DTAG, "Socket Type " + socketType + " cancel " + this);
			try {
				serverSocket.close();
			} catch (IOException e) {
				Log.e(DTAG, "Socket Type " + socketType
						+ " close() of server failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket socket;
		private final BluetoothDevice device;
		private String socketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			this.device = device;
			BluetoothSocket tmp = null;
			socketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the given
			// BluetoothDevice
			try {
				if (secure) {
					tmp = device
							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(DTAG, "Socket Type: " + socketType + " create() failed",
						e);
			}
			socket = tmp;
		}

		@Override
		public void run() {
			Log.i(DTAG, "BEGIN connectThread Socket Type: " + socketType);
			setName("ConnectThread" + socketType);

			// Always cancel discovery because it will slow down a connection
			bluetoothAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a successful
				// connection or an exception
				socket.connect();
			} catch (IOException e) {
				// Close the socket
				try {
					socket.close();
				} catch (IOException e2) {
					Log.e(DTAG, "unable to close() " + socketType
							+ " socket during connection failure", e2);
				}
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				connectThread = null;
			}

			// Start the connected thread
			connected(socket, device, socketType);
		}

		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e(DTAG, "close() of connect " + socketType
						+ " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket socket;
		private final InputStream in;
		private final OutputStream out;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.d(DTAG, "create ConnectedThread: " + socketType);

			this.socket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(DTAG, "temp sockets not created", e);
			}

			this.in = tmpIn;
			this.out = tmpOut;
		}

		@Override
		public void run() {
			Log.i(DTAG, "BEGIN connectedThread");

			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected.
			while (true) {
				try {
					// Read from the InputStream
					bytes = in.read(buffer);

					// Send the obtained bytes to the UI Activity
					handler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes,
							-1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(DTAG, "disconnected", e);
					connectionLost();
					// Start the service over to restart listening mode
					BluetoothChatService.this.start();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				out.write(buffer);

				// Share the sent message back to the UI Activity
				handler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
						buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(DTAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e(DTAG, "close() of connect socket failed", e);
			}
		}
	}
}
