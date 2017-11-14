
#import "RNVitals.h"
#import <mach/mach.h>
#import <mach/mach_host.h>

// Used to send events to JS
#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#else
#import "React/RCTBridge.h"
#endif

#if __has_include(<React/RCTEventDispatcher.h>)
#import <React/RCTEventDispatcher.h>
#elif __has_include("RCTEventDispatcher.h")
#import "RCTEventDispatcher.h"
#else
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

- (void)applicationDidReceiveMemoryWarning:(NSNotification *)notification
{
    [_bridge.eventDispatcher sendDeviceEventWithName:LOW_MEMORY
                                                body:@YES];
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

    resolve(@{
      @"totalSpace": [NSNumber numberWithUnsignedLongLong:totalSpace],
      @"freeSpace": [NSNumber numberWithUnsignedLongLong:totalFreeSpace]
    });
  } else {
    reject(@"not-support", @"An error happened", nil);
  }
}

RCT_EXPORT_METHOD(getMemory:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  mach_port_t host_port;
  mach_msg_type_number_t host_size;
  vm_size_t pagesize;

  host_port = mach_host_self();
  host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);
  host_page_size(host_port, &pagesize);

  vm_statistics_data_t vm_stat;

  if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS) {
      NSLog(@"Failed to fetch vm statistics");
  }

  /* Stats in bytes */
  natural_t mem_free = vm_stat.free_count * pagesize;
  resolve(@{
            @"totalMemory": [NSNumber numberWithUnsignedLongLong:[NSProcessInfo processInfo].physicalMemory],
            @"freeMemory": [NSNumber numberWithUnsignedLongLong:mem_free]
            });
}

@end
