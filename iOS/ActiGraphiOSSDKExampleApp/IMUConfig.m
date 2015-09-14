//
//  IMUConfig.m
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "IMUConfig.h"

@implementation IMUConfig
-(id)init {
    if(self = [super init]) {
        _imu = [IMUConfigValues new];
    }
    return self;
}
@end

@implementation IMUConfigValues
@end
