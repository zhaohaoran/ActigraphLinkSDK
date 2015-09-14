//
//  DetailViewController.m
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/12/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "DetailViewController.h"
#import "ExampleAppModels.h"

@interface DetailViewController ()

@property (strong, nonatomic) AGDeviceSDK* actigraphService;
@property (assign, nonatomic) bool isRequestingEpochs;
@property (assign, nonatomic) bool isStreamingRaw;
@property (assign, nonatomic) bool isIMUEnabled;

@end

@implementation DetailViewController

#pragma mark - Managing the detail item

-(void)setDevice:(NSString*)device withService:(AGDeviceSDK*)service {
    
    // We need to reuse this as the service maintains a internal list of found devices
    // This is so it can connect to them without needing the consuming library to manage
    // the root device object/type.
    
    _actigraphService = service;
    
    _myDevice = device;
    
    // Update the view.
    [self configureView];
}

- (void)configureView {
    // Update the user interface for the detail item.
    if (_myDevice && _textView) {
        self.title = _myDevice;
        
        _textView.text = [NSString stringWithFormat:@"Loaded device: %@", _myDevice];
    }
    
    _actigraphService.delegate = self;
    
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // setup the text view logger
    if(_textView) {
        [DDLog addLogger:[[UITextViewLogger alloc] initWithTextView:_textView] withLogLevel:LOG_FLAG_INFO];
    }
    
    // Do any additional setup after loading the view, typically from a nib.
    [self configureView];    
}

- (void)viewDidDisappear:(BOOL)animated {
    
    [super viewDidDisappear:animated];
    
    if(_actigraphService) {
        _actigraphService.delegate = nil; // surpress messages on the way out
        [_actigraphService disconnectFromDevice];
    }
    
}

- (IBAction)onControlSegmentTapped:(UISegmentedControl *)sender {
    NSInteger clickedSegment = [sender selectedSegmentIndex];
    
    RawConfig* raw = [RawConfig new];
    IMUConfig* imu = [IMUConfig new];
        
    if(_actigraphService) {
        switch (clickedSegment) {
            case 0: // Connect/Disconnect
            {
                bool isConnceted = [_actigraphService getConnectedDevice] != nil;
                
                if(isConnceted)
                    [_actigraphService disconnectFromDevice];
                else
                    [_actigraphService connectToDevice:_myDevice];
                
                break;
            }
            case 1: // Status
            {
                [_actigraphService getDeviceStatus];
                
                break;
            }
            case 2: // RAW
            {
                _isStreamingRaw = !_isStreamingRaw;
                raw.raw.stream = _isStreamingRaw;
                
                NSString* rawConfig = [raw toJSONString];
                [_actigraphService configureDevice:rawConfig];
                
                break;
            }
            case 3: // Epoch
            {
                NSDateFormatter *df = [[NSDateFormatter alloc] init];
                [df setDateFormat:@"yyyy-MM-dd HH:mm zzz"];
                
                //_isRequestingEpochs = true;
                //[_actigraphService GetDeviceStatus];
                EpochConfig* epoch = [EpochConfig new];
                
                //epoch.epoch.startDateTime = [NSDate timeIntervalSinceReferenceDate:@()];
                
                epoch.epoch.startDateTime = [df dateFromString:@"2015-08-25 22:00 CST"];
                epoch.epoch.stopDateTime = [df dateFromString:@"2015-08-26 00:00 CST"]; // 23:00
                
                NSString* config = [epoch toJSONString];
                [_actigraphService configureDevice:config];
                
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
                [_actigraphService configureDevice:config];
                
                break;
            }
        }
    }
    
}

- (IBAction)onBtnClearLog:(id)sender {
    
    // Clear
    _textView.text = @"";
    
}

-(void)prepareForEpochsDownload {
    EpochConfig* epoch = [EpochConfig new];
    NSDate* now = [NSDate date];
    
    epoch.epoch.startDateTime = [now dateByAddingTimeInterval:-3600*3.5]; // 3.5 hours ago
    epoch.epoch.stopDateTime = now;
    
    [_actigraphService configureDevice:[epoch toJSONString]];
}

// Used only for finding devices
-(void)OnDeviceData:(NSString*)data {
    dispatch_async(dispatch_get_main_queue(), ^{
        DDLogInfo(@"%@", data);
    });
}

// Used for communications with devices
-(void)OnDeviceStatus:(NSString*)status {
    dispatch_async(dispatch_get_main_queue(), ^{
        DDLogInfo(@"%@", status);
    });
}


@end
