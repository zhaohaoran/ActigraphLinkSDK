//
//  IMUConfig.h
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "JSONModel.h"

@class IMUConfig;
@class IMUConfigValues;

@interface IMUConfig : JSONModel
@property (strong, nonatomic) IMUConfigValues* imu;
@end

@interface IMUConfigValues : JSONModel
@property (assign, nonatomic) bool accelerometer;
@property (assign, nonatomic) bool gyroscope;
@property (assign, nonatomic) bool magnetometer;
@property (assign, nonatomic) bool temperature;
@end