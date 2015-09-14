//
//  RawConfig.h
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "JSONModel.h"

@class RawConfig;
@class RawConfigValues;

@interface RawConfig : JSONModel
@property (strong, nonatomic) RawConfigValues* raw;
@end

@interface RawConfigValues : JSONModel
@property (assign, nonatomic) bool stream;
@end
