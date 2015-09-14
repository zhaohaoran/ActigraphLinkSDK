//
//  EpochConfig.m
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "EpochConfig.h"

@implementation EpochConfig
-(id)init {
    if(self = [super init]) {
        _epoch = [EpochConfigValues new];
    }
    return self;
}
@end

@implementation EpochConfigValues
@end
