//
//  EpochConfig.h
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/20/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "JSONModel.h"

@class EpochConfig;
@class EpochConfigValues;

@interface EpochConfig : JSONModel
@property (strong, nonatomic) EpochConfigValues* epoch;
@end

@interface EpochConfigValues : JSONModel
@property (strong, nonatomic) NSDate* startDateTime;
@property (strong, nonatomic) NSDate* stopDateTime; // Intentionally Wrong TODO: Fix this
@end
