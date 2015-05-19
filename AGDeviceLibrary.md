## AGDeviceLibrary Documentation

#### `public class AGDeviceLibrary`

This class provides the API methods necessary for interacting with ActiGraph activity monitors.

#### `public static AGDeviceLibrary getInstance()`

Method used to obtain a reference to the AGDeviceLibray instance.

 * **Returns:** AGDeviceLibrary

##### `public void registerLibraryListener(Context context, AGDeviceLibraryListener libraryListener)`

Method used to register your application with this instance of AGDeviceLibrary.

 * **Parameters:**
   * `context` — the Activity context using the library.
   * `libraryListener` — the class that implements the interface AGDeviceLibraryListener

#### `public void EnumerateDevices()`

Starts device enumeration for a default interval of 5 seconds. Results are returned via "device" and "devices" messages in OnDeviceData from AGDeviceLibraryListener.

 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>

#### `public void EnumerateDevices(long duration)`

Starts device enumeration for duration specified. Results are returned via "device" and "devices" messages in OnDeviceData from AGDeviceLibraryListener.

 * **Parameters:** `duration` — duration to enumerate (specified in milliseconds)
 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>

#### `public void CancelEnumeration()`

Cancels in progress EnumerateDevice requests.

#### `public void ConnectToDevice(String deviceId)`

Establishes connection to a device. Connection and disconnection messages are returned via "deviceConnected" and "deviceDisconnected" messages in OnDeviceStatus from AGDeviceLibraryListener.

 * **Parameters:** `deviceId` — serial number of the device you wish to connect to
 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>

#### `public void ConfigureDevice(String config)`

Applies desired configuration to connected device.

 * **Parameters:** `config` — JSON formatted configuration message
 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>

#### `public void DisconnectFromDevice()`

Terminates existing device connection.

#### `public String GetConnectedDevice()`

Query serial number of connected device.

 * **Returns:** serial number of connected device, null if no connection present

#### `public void GetDeviceStatus()`

Query status of connected device. Results are returned via "status" message in OnDeviceStatus from AGDeviceLibraryListener.

 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>
