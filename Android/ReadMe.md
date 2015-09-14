# ActiGraph Link SDK - ALPHA

## ALPHA Release
This release of the Link SDK is currently in ALPHA state.  Currently, the SDK requires that the LINK device run a specific internal release of Link firmware.  For the BETA release, Link firmware will be public but will require that initialization of the device be handled by ActiGraph's CentrePoint study admin system (subscription required). 

## Requirements

- **Android OS 4.3**
- **API Level 18**

## Installation

1. Download the following files and include them in your project
 - [agdevicelibrary-release.aar](https://github.com/actigraph/ActigraphLinkSDK/releases/download/v1.0/java_archive.zip)

#### Android Studio Instructions
- Download .aar file
- Add file to libs folder located under your *Project Name*/*Your app name*/libs/
- File > New > New Module > Import .aar/Jar > Browse for libs/agdevicelibrary-release.aar
- Add Import Statement import actigraph.deviceapi.*;


## Documentation
- [AGDeviceLibrary](AGDeviceLibrary.md)
- [AGDeviceLibraryListener](AGDeviceLibraryListener.md)
- Also located at Documents/index.html when downloaded

## Usage

### JSON Configuration Examples
The following are possible JSON configurations that can be sent to the device using the following method 

```java
public void ConfigureDevice(String config);
``` 

#### RAW Configuration Request
```json
{
	"raw" : {
		"stream": true 
	}
}
```
#### RAW Configuration Response
> This response will be received multiple times until a RAW Configuration request to disable streaming is received or the device is disconnected.

```json
{
	"raw" : {
		"device" : "TAS1D12345678",
		"timestamp" : "1427113646",
		"acceleration" : [
			{
				"x" : 0.0003,
				"y" : 1.0002,
				"z" : 0.0001
			},
			{
				"x" : 0.0001,
				"y" : 0.0003,
				"z" : 1.0001
			} 
		]
	}
}
```

#### IMU Configuration Request
```json
{
	"imu" : {
		"accelerometer": false,
		"gyroscope": true,
		"magnetometer": false,
		"temperature": true
	}
}
```
#### IMU Configuration Response
> There is no response from the device upon successful configuration of IMU states.


#### Epoch Configuration Request
```json
{
	"epoch" : {
		"startDateTime": "07-21-2015 04:05:00",
		"stopDateTime": "07-21-2015 04:06:00"
	}
}
```
#### Epoch Configuration Response
> After requesting to download epoch information from the device, there will be progress updates of the download followed by a final response which will have all the epoch information provided.

```json

{ 
	"epochDownload" : {
		"state" : "started",
		"progress" : 0 
	} 
}

{ 
	"epochDownload" : {
		"state" : "downloading",
		"progress" : 55 
	} 
}

{ 
	"epochDownload" : {
		"state" : "completed",
		"progress" : 100 
	} 
}

{
	"epoch" : {
		"startdatetime" : "07-21-2015 04:05:00",
		"stopdatetime" : "07-21-2015 04:06:00",
		"epochData" : [{
			"timestamp": "07-07-2015 04:05:00",
			"xCounts": 20,
			"yCounts": 100,
			"zCounts": 30,
			"steps": 0,
			"heartRate": 0,
			"wearDetect": 1
		},
		{
			"timestamp": "07-07-2015 04:06:00",
			"xCounts": 10,
			"yCounts": 40,
			"zCounts": 200,
			"steps": 0,
			"heartRate": 0,
			"wearDetect": 1
		}]
	}
}

```

### Other JSON Responses
The following are other responses that can be received based off the various states of the SDK or if an error has occured.

#### Device found during scan
```json
{
	"device" : "TAS1A00000001"
}
```
#### Devices found after scan completed
```json
{
	"devices" : [
		"TAS1A14758923",
		"TAS1D03845638",
		"TAS1D83493233"
	]
}
```
#### Device Connected / Disconnected
```json
{
	"deviceConnected" : "TAS1A14758923"
}

{
	"deviceDisconnected" : "TAS1A14758923"
}
```
#### SDK Errors
```json
{
	"error" : {
		"code" : 3,
		"description" : "Unrecognized Configuration",
		"extra" : "inu, mageenetometer" 
	}
}
```
> #### Code Descriptions
> 0 - No Device Connected  
> 1 - Device Not Found  
> 2 - Device Enumeration in Progress  
> 3 - Unrecognized Configuration  
> 4 - SDK Access Disabled  
> 5 - Device Connection Failed  
> 6 - Wireless Tranfer Error  
> 7 - Epoch Download Error  
> 8 - Date Format Error  

## Sample Application

Actigraph has developed an open-source sample application to help developers get started with the SDK.  Screen images are provided in this section to illustrate the sample app.

> Example application and library created using Android Studio 1.1.0

### Sample app screen capture of device enumeration
![Sample app screen capture of device enumeration](https://cloud.githubusercontent.com/assets/1958634/7756698/a174cac6-ffc2-11e4-9e07-8e5c3c0e2bfe.png)

### Sample app screen capture of device status
![Sample app screen capture of device status](https://cloud.githubusercontent.com/assets/1958634/7756618/1175d28a-ffc2-11e4-9778-4287550f5e0b.png)

### Sample app screen capture of raw data streaming
![Sample app screen capture of raw data streaming](https://cloud.githubusercontent.com/assets/1958634/7756724/c65b9856-ffc2-11e4-81d9-4a6752e4f40b.png)
