package actigraph.deviceapi.exampleapp;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/* AGDeviceLibrary:
        Import ActiGraph device library.
*/
import actigraph.deviceapi.*;

public class MainActivity extends ActionBarActivity implements AGDeviceLibraryListener {

    private AGDeviceLibrary agDeviceLibrary;
    private Button enumerateButton;
    private Button startStreamButton;
    private Button stopStreamButton;
    private Button startIMUButton;
    private Button stopIMUButton;
    private Button deviceStatusButton;
    private Button epochDownloadButton;
    private ListView serialListView;
    private Runnable uiRunnable;
    private Boolean  buttonState = false;
    private DeviceListAdapter mDeviceListAdapter;
    private Boolean shouldConnect = true;
    private String  connectedDevice;
    private TextView streamTextView;
    private ScrollView streamScrollView;
    private int        numReceivedStreams;
    private Context self;

    private boolean requestingEpochDownload = false;
    private final int NUM_EPOCH_MINUTES_TO_DOWNLOAD = 210;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;

        enumerateButton   = (Button)findViewById(R.id.enumerateButton);
        startStreamButton = (Button)findViewById(R.id.startStreamButton);
        serialListView    = (ListView)findViewById(R.id.serialListView);
        streamTextView    = (TextView)findViewById(R.id.textView1);
        stopStreamButton  = (Button)findViewById(R.id.stopStreamButton);
        streamScrollView  = (ScrollView)findViewById(R.id.textAreaScroller);
        startIMUButton    = (Button)findViewById(R.id.startIMUButton);
        stopIMUButton     = (Button)findViewById(R.id.stopIMUButton);
        deviceStatusButton = (Button)findViewById(R.id.deviceStatus);
        epochDownloadButton = (Button)findViewById(R.id.epochDownloadButton);

        epochDownloadButton.setText("Download Last " + String.format("%.2f", (double)NUM_EPOCH_MINUTES_TO_DOWNLOAD / 60) +
                                        " hour(s) of Epoch Data");


        deviceStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* AGDeviceLibrary:
                    Get the device status JSON of a connected device.
                 */
                agDeviceLibrary.GetDeviceStatus();
            }
        });

        epochDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestingEpochDownload = true;

                 /* AGDeviceLibrary:
                    Get the device status JSON of a connected device for use in calculation
                    epoch download window
                 */
                agDeviceLibrary.GetDeviceStatus();
            }
        });

        startIMUButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject imuData = new JSONObject();
                JSONObject imuMessage = new JSONObject();

                try {
                    imuData.put("temperature", "enable");
                    imuData.put("magnetometer", "enable");
                    imuData.put("gyroscope", "enable");
                    imuData.put("accelerometer", "enable");
                    imuMessage.put("imu", imuData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* AGDeviceLibrary:
                    Here we configure the connected device's IMU via the above JSON
                 */
                agDeviceLibrary.ConfigureDevice(imuMessage.toString());
            }
        });

        stopIMUButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject imuData = new JSONObject();
                JSONObject imuMessage = new JSONObject();

                try {
                    imuData.put("temperature", "disable");
                    imuData.put("magnetometer", "disable");
                    imuData.put("gyroscope", "disable");
                    imuData.put("accelerometer", "disable");
                    imuMessage.put("imu", imuData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* AGDeviceLibrary:
                    Here we disable the connected device's IMU via the above JSON
                 */
                agDeviceLibrary.ConfigureDevice(imuMessage.toString());
            }
        });

        serialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (shouldConnect) {
                    DeviceInfo dev = (DeviceInfo) mDeviceListAdapter.getItem(position);
                    /* AGDeviceLibrary:
                        Establish a connection to the selected device from the device list.
                    */
                    agDeviceLibrary.ConnectToDevice(dev.mDeviceId);
                } else {
                    /* AGDeviceLibrary:
                        If we were already connected to a device just disconnect.
                    */
                    agDeviceLibrary.DisconnectFromDevice();
                }
            }
        });

        startStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject streamMessage = new JSONObject();
                JSONObject rawMessage = new JSONObject();

                try {
                    streamMessage.put("stream", "enable");
                    rawMessage.put("raw", streamMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* AGDeviceLibrary:
                    Here we configure the connected device to stream raw data via the above JSON
                 */

                agDeviceLibrary.ConfigureDevice(rawMessage.toString());
            }
        });

        stopStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject streamMessage = new JSONObject();
                JSONObject rawMessage = new JSONObject();

                try {
                    streamMessage.put("stream", "disable");
                    rawMessage.put("raw", streamMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* AGDeviceLibrary:
                    Here we disable raw streaming from the connected device via the above JSON
                 */

                agDeviceLibrary.ConfigureDevice(rawMessage.toString());
            }
        });

        enumerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonState) {
                    /* AGDeviceLibrary:
                        If we are currently enumerating, let's cancel it
                    */
                    agDeviceLibrary.CancelEnumeration();
                    enumerateButton.setText("Enumerate Devices");
                    buttonState = false;
                } else {
                    /* AGDeviceLibrary:
                        Start a 6 second enumeration
                    */
                    agDeviceLibrary.EnumerateDevices(6000);
                    enumerateButton.setText("Searching");
                    mDeviceListAdapter.clear();

                    /* AGDeviceLibrary:
                        If we were connected to a device before we performed the enumerate, query
                        the library for that device and add it to our results list.
                    */
                    if (agDeviceLibrary.GetConnectedDevice() != null) {
                        shouldConnect = false;
                        mDeviceListAdapter.addDevice(agDeviceLibrary.GetConnectedDevice(), true);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });

                    buttonState = true;
                }
            }
        });

        uiRunnable = new Runnable() {
            public void run() {
                enumerateButton.setText("Enumerate Devices");
            }
        };

        /* AGDeviceLibrary:
              This call instantiates the library (if not already instantiated), stores the reference in
               agDeviceLibrary, and configures the context and library listener to 'this' via
               registerLibraryListener
         */
        (agDeviceLibrary = AGDeviceLibrary.getInstance()).registerLibraryListener(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDeviceListAdapter = new DeviceListAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        serialListView.setAdapter(mDeviceListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void OnDeviceData(final String data) {
        try {
            JSONObject jData = new JSONObject(data);
            Iterator<?> i = jData.keys();

            while (i.hasNext()){
                String key = (String)i.next();

                ////////////////////////////////
                // Known Keys
                //
                // found : Identifies a found device during enumeration. Only appears during enumeration
                // devices: Raised once enumeration has completed, and contains an array of found devices
                //
                //
                ////////////////////////////////

                switch (key) {
                    case "device":
                        String foundDevice = jData.getString(key);

                        // Add our found device to the list
                        mDeviceListAdapter.addDevice(foundDevice, false);

                        // Always update the UI on the UI Thread, we could be doing this from anywhere
                        // Let the table know we just got some new data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceListAdapter.notifyDataSetChanged();
                            }
                        });

                        break;
                    case "devices":

                        // Since we're waiting on the the search to end (after 5 seconds, forced)
                        // We'll enable the button to start searching again here
                        buttonState = false;
                        runOnUiThread(uiRunnable);

                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (++numReceivedStreams == 4) {
                                    numReceivedStreams = 0;
                                    streamTextView.setText("");
                                }
                                streamTextView.append(data);
                                streamTextView.append("\n");
                            }
                        });

                        streamScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                streamScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestEpoch(int numMinutesToDownload, String startDate, String stopDate) {
        final SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("MM-dd-yyyy kk:mm:ss");

        long targetstartdatetime;
        long startdatetime;
        long stopdatetime;

        Date date;

        try {
            date = sdf.parse(startDate);
            startdatetime = date.getTime() / 1000;

            date = sdf.parse(stopDate);
            stopdatetime = date.getTime() / 1000;

            if (stopdatetime - ((numMinutesToDownload - 1) * 60) >= startdatetime) {
                targetstartdatetime = stopdatetime - ((numMinutesToDownload - 1) * 60);
            } else {
                targetstartdatetime = startdatetime;
            }

            date = new Date(targetstartdatetime * 1000);

            JSONObject epochMessage = new JSONObject();
            JSONObject epochData = new JSONObject();

            try {
                epochData.put("startdatetime", sdf.format(date));
                epochData.put("stopdatetime", stopDate);
                epochMessage.put("epoch", epochData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            agDeviceLibrary.ConfigureDevice(epochMessage.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnDeviceStatus(final String status) {
        try {
            JSONObject jData = new JSONObject(status);
            Iterator<?> i = jData.keys();

            while (i.hasNext()){
                String key = (String)i.next();

                ////////////////////////////////
                // Known Keys
                //
                // deviceConnected : Identifies when a device has connected to the phone
                // deviceDisconnected: Identifies when a device has become disconnected to the phone
                //
                ////////////////////////////////

                if (key.equals("deviceConnected")) {
                    shouldConnect = false;
                    connectedDevice = jData.getString(key);
                    ((DeviceInfo) mDeviceListAdapter.getItem(connectedDevice)).mIsConnected = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                } else if (key.equals("deviceDisconnected")) {
                    shouldConnect = true;
                    DeviceInfo deviceInf = (DeviceInfo)mDeviceListAdapter.getItem(connectedDevice);
                    if (deviceInf != null) {
                        deviceInf.mIsConnected = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceListAdapter.notifyDataSetChanged();
                            streamTextView.setText(" ");
                        }
                    });
                } else if (key.equals("error")) {
                    final JSONObject errorInfo = jData.getJSONObject(key);
                    Iterator<?> j = errorInfo.keys();
                    while (j.hasNext()) {
                        key = (String)j.next();
                        if (key.equals("description")) {
                            try {
                                final String errorDescription = errorInfo.getString(key);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(self, errorDescription, Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    //Clear this request in the event of an error
                    requestingEpochDownload = false;
                } else if (requestingEpochDownload && key.equals("status")) {
                    String startDate = null;
                    String stopDate = null;
                    final JSONObject statusInfo = jData.getJSONObject((key));
                    Iterator<?> j = statusInfo.keys();
                    while (j.hasNext()) {
                        key = (String)j.next();
                        if (key.equals("epoch")) {
                            final JSONObject epochInfo = statusInfo.getJSONObject(key);
                            Iterator<?> k = epochInfo.keys();
                            while (k.hasNext()) {
                                key = (String)k.next();
                                if (key.equals("startdatetime")) {
                                    startDate = epochInfo.getString(key);
                                } else if (key.equals("stopdatetime")) {
                                    stopDate = epochInfo.getString(key);
                                }
                            }
                        }
                    }

                    if (startDate != null && stopDate != null) {
                        requestEpoch(NUM_EPOCH_MINUTES_TO_DOWNLOAD, startDate, stopDate);
                    }

                    requestingEpochDownload = false;
                } else {

                    // Other status outputs are just added to the text view

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (++numReceivedStreams == 4) {
                                numReceivedStreams = 0;
                                streamTextView.setText("");
                            }
                            streamTextView.append(status);
                            streamTextView.append("\n");
                        }
                    });

                    streamScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            streamScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class DeviceInfo {
        public DeviceInfo(String devId, Boolean isConnected) {
            mDeviceId = devId;
            mIsConnected = isConnected;
        }
        public String mDeviceId;
        public Boolean mIsConnected;
    }

    private class DeviceListAdapter extends BaseAdapter {
        private ArrayList<DeviceInfo> devices;
        private LayoutInflater mInflater;

        public DeviceListAdapter() {
            super();
            devices = new ArrayList<DeviceInfo>();
            mInflater = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(String serial, Boolean isConnected) {
            if (getItem(serial) == null) {
                devices.add(new DeviceInfo(serial, isConnected));
            }
        }

        public void clear () { devices.clear(); }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        public Object getItem(String devId) {
            Object retVal = null;
            for (int i = 0; i < devices.size(); i++) {
                if (((DeviceInfo)getItem(i)).mDeviceId.equals(devId)) {
                    retVal = getItem(i);
                    break;
                }
            }

            return retVal;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_device, null);

                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                viewHolder.deviceConnected = (RadioButton)convertView.findViewById(R.id.radioButton);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String deviceName = devices.get(position).mDeviceId;
            viewHolder.deviceName.setText(deviceName);
            viewHolder.deviceConnected.setChecked(devices.get(position).mIsConnected);
            return convertView;
        }

    }

    static class ViewHolder {
        TextView deviceName;
        RadioButton deviceConnected;
    }
}
