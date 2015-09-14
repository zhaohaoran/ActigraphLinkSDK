//
//  AGDeviceSDKDelegate.h
//  AGDeviceSDK
//
//  Created by Richard Shaw on 9/8/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#ifndef AGDeviceSDK_AGLinkSDKDelegate_h
#define AGDeviceSDK_AGLinkSDKDelegate_h

@protocol AGDeviceSDKDelegate <NSObject>
-(void)OnDeviceData:(NSString*)data;
-(void)OnDeviceStatus:(NSString*)status;
@end

#endif
