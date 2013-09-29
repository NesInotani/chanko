package com.chanko.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothChat extends Activity {
	// Debugging
	private static final String DTAG = BluetoothChat.class.getSimpleName();

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private ListView conversationView;
	private EditText outEditText;
	private Button sendButton;

	// Name of the connected device
	private String connectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> conversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer outStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter bluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService chatService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DTAG, "+++ ON CREATE +++");

		// Set up the window layout
		setContentView(R.layout.main);

		// Get local Bluetooth adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(DTAG, "++ ON START ++");

		// If BT is not on, request that is be enabled.
		// setupChat() will then be called during onActivityResult
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		// Otherwise, setup the chat session
		else {
			if (chatService == null) {
				setupChat();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(DTAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (chatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (chatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				chatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(DTAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		conversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		conversationView = (ListView) findViewById(R.id.in);
		conversationView.setAdapter(conversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		outEditText = (EditText) findViewById(R.id.edit_text_out);
		outEditText.setOnEditorActionListener(writeListener);

		// Initialize the send button with a listener that for click events
		sendButton = (Button) findViewById(R.id.button_send);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});

		// Initialize the BluetoothChatService to perform bluetooth connections
		chatService = new BluetoothChatService(this, handler);

		// Initialize the buffer for outgoing messages
		outStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		Log.d(DTAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(DTAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth caht services
		if (chatService != null) {
			chatService.stop();
		}
		Log.d(DTAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		Log.d(DTAG, "ensure discoverable");
		if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			chatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			outStringBuffer.setLength(0);
			outEditText.setText(outStringBuffer);
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener writeListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = v.getText().toString();
				sendMessage(message);
			}
			Log.d(DTAG, "END onEditorAction");

			return true;
		}
	};

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				Log.d(DTAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							connectedDeviceName));
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the beffer
				String writeMessage = new String(writeBuf);
				conversationArrayAdapter.add("Me:   " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				conversationArrayAdapter.add(connectedDeviceName + ":   "
						+ readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				connectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + connectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(DTAG, "onActivityResult " + resultCode);

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User dit not enable Bluetooth or an error occurred
				Log.d(DTAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		chatService.connect(device, secure);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}
}
