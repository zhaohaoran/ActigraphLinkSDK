//
//  MasterViewController.h
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/12/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <CocoaLumberjack/DDLog.h>
#import "ActiGraphSDK.h"

@class DetailViewController;

@interface MasterViewController : UITableViewController<AGDeviceSDKDelegate>

@property (strong, nonatomic) AGDeviceSDK* actigraphService;
@property (strong, nonatomic) DetailViewController *detailViewController;


@end

