## AGDeviceLibraryListener Documentation

#### `public interface AGDeviceLibraryListener`

This interface provides the callbacks necessary for interacting with ActiGraph activity monitors.

#### `void OnDeviceData(String data)`

This method is called when new data has been received from the device library

 * **Parameters:** `data` — JSON formatted device data output
 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>

#### `void OnDeviceStatus(String status)`

This method is called when a status update has been received from the device library

 * **Parameters:** `status` — JSON formatted device status output
 * **See also:** <a href="file:JSON_Message_Definitions.pdf">JSON Message Definitions</a>
