//
//  RawConfig.m
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "RawConfig.h"

@implementation RawConfig
-(id)init {
    if(self = [super init]) {
        _raw = [RawConfigValues new];
    }
    return self;
}
@end

@implementation RawConfigValues
@end
