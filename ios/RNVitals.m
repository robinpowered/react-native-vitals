
#import "RNVitals.h"
#import <mach/mach.h>
#import <mach/mach_host.h>

// Used to send events to JS
#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#else
#import "React/RCTBridge.h"
#import "React/RCTEventDispatcher.h"
#endif

@implementation RNVitals

@synthesize bridge = _bridge;

static NSString * const LOW_MEMORY = @"LOW_MEMORY";

- (instancetype)init
{
    if ((self = [super init])) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationDidReceiveMemoryWarning:) name:UIApplicationDidReceiveMemoryWarningNotification object:nil];
    }

    return self;

}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (NSDictionary *)getMemoryInfo
{
    struct mach_task_basic_info info;
    mach_msg_type_number_t size = sizeof(info);
    kern_return_t kerr = task_info(mach_task_self(),
                                   MACH_TASK_BASIC_INFO,
                                   (task_info_t)&info,
                                   &size);

    if (kerr != KERN_SUCCESS) {
        return @{@"failed": @"failure"};
    }

    return @{
             @"total": [NSNumber numberWithUnsignedLongLong:[NSProcessInfo processInfo].physicalMemory],
             @"used": [NSNumber numberWithUnsignedLongLong:info.resident_size],
             @"free": [NSNumber numberWithUnsignedLongLong:info.virtual_size]
             };
}

- (void)applicationDidReceiveMemoryWarning:(NSNotification *)notification
{
    NSDictionary *memoryInfo = [RNVitals getMemoryInfo];
    if ([memoryInfo objectForKey:@"failed"] != nil)
    {
        [_bridge.eventDispatcher sendDeviceEventWithName:LOW_MEMORY
                                                    body:@"An error happened while getting memory logs"];
    }
    else
    {
        [_bridge.eventDispatcher sendDeviceEventWithName:LOW_MEMORY
                                                    body:memoryInfo];
    }
}

RCT_EXPORT_MODULE()

- (NSDictionary *)constantsToExport
{
    return @{ @"LOW_MEMORY": LOW_MEMORY };
}

RCT_EXPORT_METHOD(getStorage:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    unsigned long long totalSpace = 0;
    unsigned long long totalFreeSpace = 0;

    __autoreleasing NSError *error = nil;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSDictionary *dictionary = [[NSFileManager defaultManager] attributesOfFileSystemForPath:[paths lastObject] error:&error];

    if (dictionary) {
        NSNumber *fileSystemSizeInBytes = [dictionary objectForKey: NSFileSystemSize];
        NSNumber *freeFileSystemSizeInBytes = [dictionary objectForKey:NSFileSystemFreeSize];
        totalSpace = [fileSystemSizeInBytes unsignedLongLongValue];
        totalFreeSpace = [freeFileSystemSizeInBytes unsignedLongLongValue];
        unsigned long long used = totalFreeSpace - totalSpace;
        resolve(@{
                  @"total": [NSNumber numberWithUnsignedLongLong:totalSpace],
                  @"free": [NSNumber numberWithUnsignedLongLong:totalFreeSpace],
                  @"used": [NSNumber numberWithUnsignedLongLong:used]
                  });
    } else {
        reject(@"not-support", @"An error happened", nil);
    }
}

RCT_EXPORT_METHOD(getMemory:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDictionary *memoryInfo = [RNVitals getMemoryInfo];
    if ([memoryInfo objectForKey:@"failed"] != nil)
    {
        reject(@"not-support", @"An error happened", nil);
    }
    else
    {
        resolve(memoryInfo);
    }

}

@end
