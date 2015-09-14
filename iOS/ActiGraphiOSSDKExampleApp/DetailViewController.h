//
//  DetailViewController.h
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/12/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <CocoaLumberjack/DDLog.h>
#import "UITextViewLogger.h"
#import "ActiGraphSDK.h"

@class DetailViewController;

@interface DetailViewController : UIViewController<AGDeviceSDKDelegate>

@property (strong, nonatomic) NSString* myDevice;
@property (weak, nonatomic) IBOutlet UISegmentedControl *controlSegment;
/// Text view for logging.
@property (weak, nonatomic) IBOutlet UITextView *textView;

@property (weak, nonatomic) IBOutlet UIButton *btnClearLog;
-(void)setDevice:(NSString*)device withService:(AGDeviceSDK*)service;

@end

