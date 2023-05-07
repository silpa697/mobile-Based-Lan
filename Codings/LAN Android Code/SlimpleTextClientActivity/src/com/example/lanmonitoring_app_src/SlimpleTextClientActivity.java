package com.example.lanmonitoring_app_src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lanmonitoring_app.R;

public class SlimpleTextClientActivity extends Activity {

	private Socket client;
	private Boolean isConnected = false;
	private PrintWriter printwriter;
	private InputStream inputreader;
	private EditText textField;
	private EditText edt_ip;
	private Button btn_send;
	private Button btn_connect;
	Spinner spinner_client_list;
	Spinner spinner_process_list;

	private TextView txt_client_selected, txt_process_selected;
	private TextView txt_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slimple_text_client);

		spinner_client_list = (Spinner) findViewById(R.id.spinner_client_list);

		spinner_process_list = (Spinner) findViewById(R.id.spinner_process_list);

		addDefaultSpinnerClientList();
		edt_ip = (EditText) findViewById(R.id.edt_ip); // reference to the text
														// field
		textField = (EditText) findViewById(R.id.txt_); // reference to the text
														// field
		btn_send = (Button) findViewById(R.id.btn_send); // reference to the
															// send button
		btn_connect = (Button) findViewById(R.id.btn_connect); // reference to
																// the send
																// button
		txt_status = (TextView) findViewById(R.id.txt_status);
		txt_client_selected = (TextView) findViewById(R.id.txt_client);
		txt_process_selected = (TextView) findViewById(R.id.txt_process);
		txt_status.setText("");
		txt_process_selected.setText("N/A");

		printwriter = null;
		inputreader = null;

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Button press event listener
		btn_connect.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					// TODO Connect
					txt_status.append("\nConnecting..");
					edt_ip.setVisibility(View.GONE);
					client = new Socket("" + edt_ip.getText().toString(), 8009);
					txt_status.append("\nConnected...");
					isConnected = true;
					v.setVisibility(View.GONE);
					sendMessage("c_list");

				} catch (UnknownHostException e) {
					txt_status.append("\nHost Not Found!");
					e.printStackTrace();
				} catch (IOException e) {

					txt_status.append("\nError While Reading oR Writing!");
					e.printStackTrace();
				}
			}
		});

		btn_send.setOnClickListener(new View.OnClickListener() {

			// TODO Send Message
			public void onClick(View v) {
				String messsage = textField.getText().toString(); // get the
																	// text

				sendMessage(messsage);
			}
		});

		spinner_client_list
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					protected Adapter initializedAdapter = null;

					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {
						if (initializedAdapter != parentView.getAdapter()) {
							initializedAdapter = parentView.getAdapter();
							return;
						}

						String selected = parentView
								.getItemAtPosition(position).toString();

						if (!selected.equals("Select Client")) {
							txt_client_selected.setText(selected);
							sendMessage("cc_" + selected);
						}

						else {

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
						// your code here
					}
				});

		spinner_process_list
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					protected Adapter initializedAdapter = null;

					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {
						if (initializedAdapter != parentView.getAdapter()) {
							initializedAdapter = parentView.getAdapter();
							return;
						}

						String selected = parentView
								.getItemAtPosition(position).toString();

						if (!selected.equals("Select Process")) {
							txt_process_selected.setText(selected);
						}

						else {

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
						// your code here
					}
				});

	}

	public void sendMessageAndroid(View btn) {
		Button mButton = (Button) btn;
		String btnTag = mButton.getTag().toString();
		String client_selected = txt_client_selected.getText().toString();
		if (client_selected.equals("N/A")) {
			Toast.makeText(getApplicationContext(),
					"Please Select Client first!", Toast.LENGTH_SHORT).show();
		} else {
			sendMessage("" + btnTag + ""); // TODO Send Button Click with Client
											// List Click.
		}
	}

	public void sendMessageAndroidProcess(View btn) {
		Button mButton = (Button) btn;
		String btnTag = mButton.getTag().toString();
		String client_selected = txt_client_selected.getText().toString();
		String process_selected = txt_process_selected.getText().toString();
		if (client_selected.equals("N/A")) {
			Toast.makeText(getApplicationContext(),
					"Please Select Client first!", Toast.LENGTH_SHORT).show();
		} else {

			if (process_selected.equals("N/A")) {
				Toast.makeText(getApplicationContext(),
						"Please Select Process first!", Toast.LENGTH_SHORT).show();
			} else {
				if (btnTag.contains("killp_")) {
					sendMessage("" + btnTag + "" + txt_process_selected); // TODO Send Button Click  
																			
				} else {
					sendMessage("" + btnTag + ""); // TODO Send Button Click  with Client List Click.
				}
			}

		}
	}

	public void sendMessage(String messsage) {
		mLog("in dummy click");
		// message on the
		// text field
		mLog("Message = " + messsage);
		txt_status.append("\nsending Message.. msg = [ " + messsage + " ]");
		textField.setText(""); // Reset the text field to blank
		/*
		 * SendMessage sendMessageTask = new SendMessage();
		 * sendMessageTask.execute();
		 */

		try {

			mLog("Getting Print Writer!");
			printwriter = new PrintWriter(client.getOutputStream(), true);
			mLog("Writing Data!");
			// TODO Actual Send
			printwriter.write(messsage); // write the message to output
											// stream
			mLog("Done with Writing Data!");
			printwriter.flush();

			byte[] b = new byte[10000];

			mLog("Receive Start!");

			mLog("Getting InputStream ");

			// TODO Actual Reeive
			inputreader = client.getInputStream();
			mLog("After  <inputreader=	client.getInputStream();>");

			mLog("Data Received! cnv to byte[]");

			/*
			 * java.util.Scanner s = new
			 * java.util.Scanner(inputreader).useDelimiter("\\A"); String result
			 * = s.hasNext() ? s.next() : "";
			 */
			inputreader.read(b);
			mLog("COnverted to bytee = " + b.toString());

			mLog("now PArsing b[] Data");

			CharSequence seq2 = new String(b, Charset.forName("UTF-8")); // =
																			// convertStreamToString(inputreader);

			mLog("PArsing Data Done");
			String str_received = seq2.toString().trim();

			mLog("Receive result = " + str_received);

			String client_list[] = null;

			// TODO Client List
			if (messsage.equals("c_list")) {

				if (str_received.contains("@#")) {

					mLog("client_list received!");
					client_list = str_received.split("@#");
					addItemsOnSpinnerClientList(client_list);
					for (int i = 0; i < client_list.length; i++) {
						mLog("client_list[" + i + "] = " + client_list[i]);
					}

					mLog("client_list received end!");
				} else if (str_received.equals("0")) {

					mLog("0 client_list received!");
					String[] client_list0 = { "0" };
					addItemsOnSpinnerClientList(client_list0);

					mLog("client_list received end!");

				} else {
					addDefaultSpinnerClientList();
					txt_client_selected.setText("N/A");
				}

			}

			// TODO Process List
			if (messsage.equals("getp")) {

				mLog("in if getp");
				List<String> plist = new ArrayList<String>();
				mLog("in parsing call");

				plist = listAllProcesses(str_received);
				mLog("out parsing call - count = " + plist.size());

				addItemsOnSpinnerProcessList(plist);
				mLog("out if getp");

			}

			/*
			 * txt_status.append("\n Result = "+seq2.toString());
			 * 
			 * printwriter.flush(); mLog("Receive End!");
			 * mLog("Actual String = "+seq2.toString());
			 * mLog("Actual String Len = "+seq2.toString().length());
			 * mLog("Trimmed String  = "+seq2.toString().trim());
			 * mLog("Trimmed String Len = " +seq2.toString().trim().length());
			 */

			// txt_status.append("\nReceived data..len = "+b.length);

		} catch (UnknownHostException e) {

			Log.v("Error", "1 Exception = " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {

			Log.v("Error", "2 Exception = " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {

			Log.v("Error", "3 Exception = " + e.getMessage());
			e.printStackTrace();
		}

	}

	private List<String> listAllProcesses(String result) {
		List<String> mProcessList = new ArrayList<String>();
		mLog("in parsing call methode - count = " + "0");

		String[] process_list = result.split("@#");

		for (String string : process_list) {
			mProcessList.add(string);
			mLog("Adding ---- " + string + "----");
		}

		mLog("out parsing call methode - count = " + mProcessList.size());

		return mProcessList;
	}

	public void addDefaultSpinnerClientList() {

		List<String> list = new ArrayList<String>();
		list.add("Select Client");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_client_list.setAdapter(dataAdapter);
	}

	public void addItemsOnSpinnerClientList(String[] str) {

		List<String> list = new ArrayList<String>();

		list.add("Select Client");
		for (String string : str) {
			list.add(string);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_client_list.setAdapter(dataAdapter);
	}

	public void addItemsOnSpinnerProcessList(List<String> str) {

		mLog("addToSpinnerProcess Start!");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, str);

		mLog("addToSpinnerProcess - binding - list - count = " + str.size()
				+ "!");
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_process_list.setAdapter(dataAdapter);

		mLog("addToSpinnerProcess Start!");
	}



	public void mLog(String str) {
		Log.v("Error", str);
	}

	/*
	 * private class SendMessage extends AsyncTask<Void, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * 
	 * 
	 * 
	 * return null; }
	 * 
	 * 
	 * }
	 * 
	 * }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slimple_text_client, menu);
		return true;
	}

}
