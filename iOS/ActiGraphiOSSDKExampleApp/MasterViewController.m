//
//  MasterViewController.m
//  ActiGraphiOSSDKExampleApp
//
//  Created by Richard Shaw on 8/12/15.
//  Copyright (c) 2015 ActiGraph. All rights reserved.
//

#import "MasterViewController.h"
#import "DetailViewController.h"
#import "FoundDevice.h"

@interface MasterViewController ()
@property NSMutableArray *deviceList;
@property (assign, nonatomic) bool isScanning;
@property (weak, nonatomic) NSTimer* scanTimer;
@end

@implementation MasterViewController

- (void)awakeFromNib {
    [super awakeFromNib];
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        self.clearsSelectionOnViewWillAppear = NO;
        self.preferredContentSize = CGSizeMake(320.0, 600.0);
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    //self.navigationItem.leftBarButtonItem = self.editButtonItem;

    _actigraphService = [[AGDeviceSDK alloc] initWithDelegate:self];
    
    [self toggleScanButton:_isScanning];
    self.detailViewController = (DetailViewController *)[[self.splitViewController.viewControllers lastObject] topViewController];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)toggleScanButton:(bool)isScanning {
    UIBarButtonItem* scanButton = isScanning
    ? [[UIBarButtonItem alloc] initWithTitle:@"Stop" style:UIBarButtonItemStylePlain target:self action:@selector(toggleScanForDevices:)]
    : [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(toggleScanForDevices:)];
    self.navigationItem.rightBarButtonItem = scanButton;
    
    if(_scanTimer)
        [_scanTimer invalidate];
    
    if(_isScanning)
        _scanTimer = [NSTimer scheduledTimerWithTimeInterval:9 target:self selector:@selector(toggleScanForDevices:) userInfo:nil repeats:NO];
    
}

- (void)toggleScanForDevices:(id)sender {
    if (!_deviceList) _deviceList = [[NSMutableArray alloc] init];
    
    if(_isScanning) [_actigraphService cancelEnumeration];
    else [_actigraphService enumerateDevices:15];

    _isScanning = !_isScanning;
    [self toggleScanButton:_isScanning];
}

#pragma mark -
#pragma mark LinkSDKDelegates

-(void)OnDeviceData:(NSString *)data {
    NSLog(@"ODD: %@", data);
    
    NSError* error = nil;
    FoundDevice* fdm = [[FoundDevice alloc] initWithString:data error:&error];
    
    if(error)
        return;
    
    // Check if we have that object
    NSInteger ind = [_deviceList indexOfObject:fdm.device];
    if(ind == NSNotFound) {
        [_deviceList insertObject:fdm.device atIndex:0]; // Top of the list
        NSIndexPath* indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
        [self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
    }
}
-(void)OnDeviceStatus:(NSString *)status {
    NSLog(@"ODS: %@", status);
}

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetail"]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        NSString* device = self.deviceList[indexPath.row];
        DetailViewController *controller = (DetailViewController *)[[segue destinationViewController] topViewController];
        
        // We need to send the service because it maintains a internal list of found devices
        // This is so it can connect to them without needing the consuming library to manage
        // the root device object/type.
        [controller setDevice:device withService:_actigraphService];
        
        controller.navigationItem.leftBarButtonItem = self.splitViewController.displayModeButtonItem;
        controller.navigationItem.leftItemsSupplementBackButton = YES;
    }
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _deviceList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];

    NSDate *object = self.deviceList[indexPath.row];
    cell.textLabel.text = [object description];
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return NO; //YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self.deviceList removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
    }
}

@end
