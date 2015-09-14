# ActiGraph iOS SDK

## Requirements

- **ARC only, iOS8+**
- **SystemConfiguration.framework**
- **[JSONModel](https://github.com/icanzilb/JSONModel) Library**

## Installation

1. [Download the latest release](https://github.com/actigraph/ActigraphLinkSDK/releases/download/v1.0/ios_archive.zip) of the SDK and add the following files to your Xcode project
	- AGDeviceSDK.h
	- AGDeviceSDKDelegate.h
	- ActiGraphSDK.h
	- libAGDeviceSDK.a 
2. Link your app to SystemConfiguration.framework
3. Include JSONModel's files or install via CocoaPods.

## Public Endpoints

The following methods/properties are available for use with this SDK

```objc
// Properties
@property (weak) id <AGDeviceSDKDelegate> delegate;

/////////////////
// LifeCycle Methods
/////////////////

/*!
 Default constructor for the SDK. 
 @param delegate A class/object that inherits the protocol to get information about the SDK's actions.
 @code _agSDK = [[AGDeviceSDK alloc] initWithDelegate:self];
 */
- (instancetype) initWithDelegate:(id<AGDeviceSDKDelegate>)delegate;

/////////////////
// Public Methods
/////////////////

/*!
 Request the phone to start scanning for devices (10 seconds)
 @code [_agSDK enumerateDevices];
 */
- (void) enumerateDevices;

/*!
 Request the phone to start scanning for device for a duration
 @param duration A number representing how long the phone should scan for devices
 @code [_agSDK enumerateDevices:60];
 */
- (void) enumerateDevices:(double)duration;

/*!
 Request the phone to stop scanning for devices
 @code [_agSDK cancelEnumeration];
 */
- (void) cancelEnumeration;

/*!
 Connects to the device, if it isn't already connected
 @param device The serial number of the device
 @code [_agSDK connectToDevice:@"TAS1A12345678"];
 */
- (void) connectToDevice:(NSString*)deviceId;

/*!
 Disconnect from the device, if it is connected
 @code [_agSDK disconnectFromDevice];
 */
- (void) disconnectFromDevice;

/*!
 Configure the connected device to perform certain actions.
 @param config A valid JSON string containing the configuration for a device
 @code [_agSDK configurDevice:@"{ "raw" : { "stream" : true } }"];
 */
- (void) configureDevice:(NSString*)config;

/*!
 Get the currently connected device's serial number.
 @code NSString* myConnectedDeviceSN = [_agSDK getConnectedDevice];
 */
- (NSString*) getConnectedDevice;

/*!
 Requests status information from the currently connected device.
 @code [_agSDK getDeviceStatus];
 */
- (void) getDeviceStatus;

 
```


## Usage

### JSON Configuration Examples
The following are possible JSON configurations that can be sent to the device using the following method 

```objc 
-(void) configureDevice:(NSString*)config 
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
> 9 - Invalid ActiGraph Device  
> 10 - Existing Connection Found (Device Already Connected)  
> 11 - The Last Configuration Request Received Failed

#### Upon Successful config sent
> A response matching your configuration request sent will be sent back to you denoting a successful config was received.

```json
{
	"raw" : {
		"stream" : true
	}
}
```

## Code Examples

### Scanning For Devices
The following is an example on how to scan for nearby ActiGraph Link devices:

```objc

///////////////
// Header File
///////////////

#include "ActiGraphLinkSDK.h"
#include "JSONModel.h"

// Model for devices that are found from the SDK
@interface FoundDevice : JSONModel
@property NSString* device;
@end

@interface MasterViewController : UITableViewController <AGDeviceSDKDelegate>
@end

///////////////
// Implementation File
///////////////

@interface MasterViewController()
@property NSMutableArray* deviceList; // -> _deviceList;
@property AGDeviceSDK* agSDK; // -> _agSDK
@end

@implementation FoundDevice
@end

@implementation MasterViewController

#pragma mark - 
#pragma mark - Life Cycle Methods

- (id) init {
	if(self = [super init]) {
		// Create the SDK object, referencing it to our class for notifications
		_agSDK = [[AGDeviceSDK alloc] initWithDelegate:self];
	}
	return self;
}

- (void) viewDidLoad {
	[super viewDidLoad];

	// Check if we have already created the SDK object
	if(!_agSDK)
		_agSDK = [[AGDeviceSDK alloc] initWithDelegate:self];
		
	// Just for this example, not recommended to do this here (use button instead)
	// Start scanning for devices for 15 seconds.
	[_agSDK enumerateDevices:15];
		
}

#pragma mark - 
#pragma mark - AGDeviceSDKDelegate implementation

- (void) onDeviceData:(NSString*)data { 
	NSLog(@"Received %@ from device.", data);
	
	// Parse the inbound JSON from the SDK into something we can use (using JSONModel)
	NSError* error = nil;
	FoundDevice* fdm = [[FoundDevice alloc] initWithString:data error:&error];
	    
	// If we encountered an error during parsing, lets return early
	if(error)
		return;
	    
	// Check if we already have that object
	NSInteger deviceIndex = [_deviceList indexOfObject:fdm.device];
	if(ind == NSNotFound) {
		[_deviceList insertObject:fdm.device atIndex:0]; // Top of the list
		NSIndexPath* indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
		[self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
	}
	
}
- (void) onDeviceStatus:(NSString*)status { 
	NSLog(@"Received %@ status from device.");
}

#pragma mark -
#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _deviceList.count;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    NSDate* object = _deviceList[indexPath.row];
    cell.textLabel.text = [object description];
    return cell;
}

@end

```

### Connect to a Device

The following will demonstrate on how to connect to a device that you have already found.  
**IMPORTANT :** You must scan for Link devices prior to attempting a connection to a Link device. The SDK uses an internal list of found devices and requires you pass a serial number to an already found device. If you attempt to connect to a device prior to searching for it using the SDK, the connection will not be made.

```objc

///////////////
// Header File
///////////////

#include "ActiGraphLinkSDK.h"
#include "JSONModel.h"

@interface MyClass : NSObject <AGDeviceSDKDelegate>
- (id) initWithDevice:(NSString*)device;
@end

// JSON Configuration Models

@interface DeviceConnectedStatus : JSONModel
@property NSString* deviceConnected;
@end

@interface DeviceDisconnectedStatus : JSONModel
@property NSString* deviceDisconnected;
@end

///////////////
// Implementation File
///////////////

@interface MyClass()
@property NSString* myDevice;
@property AGDeviceSDK* agSDK; // -> _agSDK
@end

@implementation DeviceConnectedStatus
@end

@implementation DeviceDisconnectedStatus
@end

#pragma mark - 
#pragma mark - Life Cycle Methods


- (id) initWithDevice:(NSString*)device {
	if(self = [super init]) {
		_agSDK = [[AGDeviceSDK alloc] initWithDelegate:self];
		_myDevice = device;
	}
}

#pragma mark - 
#pragma mark - My Methods

- (void) connect { 
	if(_myDevice) {
		[_agSDK connectToDevice:_myDevice];
	}
}

- (void) disconnect {
	if(_myDevice) {
		[_agSDK disconnectFromDevice];
		_myDevice = nil;
	}
}

#pragma mark - 
#pragma mark - AGDeviceSDKDelegate implementation

- (void) onDeviceData:(NSString*)data { 
	NSLog(@"Received %@ from device.", data);
	
	// Using JSONModel for parsing
	NSError* connectError = nil;
	DeviceConnectedStatus* dcs = [[DeviceConnectedStatus alloc] initWithString:data error:&connectError];
	
	NSError* disconnectError = nil;
	DeviceDisconnectStatus* dds = [[DeviceDisconnectStatus alloc] initWithString:data error:&disconnectError];
	
}
- (void) onDeviceStatus:(NSString*)status { 
	NSLog(@"Received %@ status from device.");
}

@end

```

### Configure a Device

The following will demonstrate on how to configure a connected device to enable/disable streaming of RAW data.

```objc

///////////////
// Header File
///////////////

#include "ActiGraphLinkSDK.h"
#include "JSONModel.h"

@interface DetailViewController : UIViewController <AGDeviceSDKDelegate>
@property (weak, nonatomic) IBOutlet UISegmentedControl *controlSegment;
- (void) setDevice:(NSString*)device withService:(AGDeviceSDK*)service;
@end

// JSON Configuration Models

@class RawConfig;
@class RawConfigVaules;
@class IMUConfig;
@class IMUConfigValues;
@class EpochConfig;
@class EpochConfigValues;

@interface RawConfig : JSONModel
@property RawConfigValues* raw;
@end

@interface RawConfigValues : JSONModel
@property bool stream;
@end

@interface IMUConfig : JSONModel
@property IMUConfigValues* imu;
@end

@interface IMUConfigValues : JSONModel
@property bool accelerometer;
@property bool gyroscope;
@property bool magnetometer;
@property bool temperature;
@end

@interface EpochConfig : JSONModel
@property EpochConfigValues* epoch;
@end

@interface EpochConfigValues : JSONModel
@property NSDate* startDateTime;
@property NSDate* stopDateTime;
@end

///////////////
// Implementation File
///////////////

@interface DetailViewController()
@property NSString* myDevice;
@property AGDeviceSDK* agSDK; // -> _agSDK
@property bool isRequestingEpochs;
@property bool isStreamingRaw;
@property bool isIMUEnabled;
@end

// JSON Configuration Model Implmentation (for sake of Example)

@implementation RawConfig
- (id) init {
    if(self = [super init]) { _raw = [RawConfigValues new]; }
    return self;
}
@end
@implementation RawConfigValues
@end

@implementation IMUConfig
- (id) init {
    if(self = [super init]) { _imu = [IMUConfigValues new]; }
    return self;
}
@end
@implementation IMUConfigValues
@end

@implementation EpochConfig
- (id) init {
    if(self = [super init]) { _epoch = [EpochConfigValues new]; }
    return self;
}
@end
@implementation EpochConfigValues
@end

@implementation DetailViewController

#pragma mark - 
#pragma mark - My Methods

- (void) setDevice:(NSString*)device withService:(AGDeviceSDK*)service {

	_myDevice = device;
	_agSDK = service;
	
	// If you pass around the SDK object, be sure to update the delegate!
	_agSDK.delegate = self;

}

- (IBAction)onControlSegmentTapped:(UISegmentedControl *)sender {

	NSInteger clickedSegment = [sender selectedSegmentIndex];
    
    RawConfig* raw = [RawConfig new];
    IMUConfig* imu = [IMUConfig new];
        
    if(_agSDK) {
        switch (clickedSegment) {
            case 0: // Connect/Disconnect
            {
                bool isConnceted = [_agSDK getConnectedDevice] != nil;
                
                if(isConnceted)
                    [_agSDK disconnectFromDevice];
                else
                    [_agSDK connectToDevice:_myDevice];
                
                break;
            }
            case 1: // Status
            {
                [_agSDK getDeviceStatus];
                
                break;
            }
            case 2: // RAW
            {
                _isStreamingRaw = !_isStreamingRaw;
                raw.raw.stream = _isStreamingRaw;
                
                NSString* rawConfig = [raw toJSONString];
                [_agSDK configureDevice:rawConfig];
                
                break;
            }
            case 3: // Epoch
            {
                NSDateFormatter *df = [[NSDateFormatter alloc] init];
                [df setDateFormat:@"yyyy-MM-dd HH:mm zzz"];
                
                // It is preferred to get the start/stop datetimes from the device
                // prior to downloading Epoch data by calling getDeviceStatus and parsing the results
                
                EpochConfig* epoch = [EpochConfig new];
                epoch.epoch.startDateTime = [df voicedateFromString:@"2015-08-25 22:00 CST"];
                epoch.epoch.stopDateTime = [df dateFromString:@"2015-08-26 00:00 CST"];
                
                NSString* config = [epoch toJSONString];
                [_agSDK configureDevice:config];
                
                break;
            }
            case 4: // IMU
            {
                _isIMUEnabled = !_isIMUEnabled;
                
                imu.imu.accelerometer = _isIMUEnabled;
                imu.imu.gyroscope = _isIMUEnabled;
                imu.imu.magnetometer = _isIMUEnabled;
                imu.imu.temperature = _isIMUEnabled;
                
                NSString* config = [imu toJSONString];
                [_agSDK configureDevice:config];
                
                break;
            }
        }
    }

}

#pragma mark - 
#pragma mark - AGDeviceSDKDelegate implementation

- (void) onDeviceData:(NSString*)data { 
	NSLog(@"Received %@ from device.", data);
}
- (void) onDeviceStatus:(NSString*)status { 
	NSLog(@"Received %@ status from device.");
}

@end

```
