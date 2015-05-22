## AGDeviceLibraryListener Documentation

#### `public interface AGDeviceLibraryListener`

This interface provides the callbacks necessary for interacting with ActiGraph activity monitors.

#### `void OnDeviceData(String data)`

This method is called when new data has been received from the device library

 * **Parameters:** `data` — JSON formatted device data output
 * **See also:** [JSON Message Definitions](Documents/actigraph/deviceapi/JSON_Message_Definitions.pdf)

#### `void OnDeviceStatus(String status)`

This method is called when a status update has been received from the device library

 * **Parameters:** `status` — JSON formatted device status output
 * **See also:** [JSON Message Definitions](Documents/actigraph/deviceapi/JSON_Message_Definitions.pdf)
