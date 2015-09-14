//
//  AGDeviceSDK.h
//  AGDeviceSDK
//
//  Created by Richard Shaw on 8/18/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

@class AGDeviceSDK;
@protocol AGDeviceSDKDelegate;

@interface AGDeviceSDK : NSObject

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


@end
