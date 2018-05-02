//
//  KMDownloadKBWindowController.m
//  Keyman4MacIM
//
//  Created by Serkan Kurt on 18/05/2015.
//  Copyright (c) 2017 SIL International. All rights reserved.
//

#import "KMDownloadKBWindowController.h"
#import "KMInputMethodAppDelegate.h"

@interface KMDownloadKBWindowController ()
@property (nonatomic, weak) IBOutlet WebView *webView;
@property (nonatomic, weak) IBOutlet NSTextFieldCell *urlLabel;
@end

@implementation KMDownloadKBWindowController

- (KMInputMethodAppDelegate *)AppDelegate {
    return (KMInputMethodAppDelegate *)[NSApp delegate];
}

- (void)windowDidLoad {
    [super windowDidLoad];
    
    [self.webView setFrameLoadDelegate:(id<WebFrameLoadDelegate>)self];
    [self.webView setUIDelegate:(id<WebUIDelegate>)self];
    
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    NSString *url = [NSString stringWithFormat:@"https://keyman.com/go/macos/10.0/download-keyboards/?version=%@", version];
    NSLog(@"KMDownloadKBWindowController opening url = %@, version = '%@'", url, version);
    [self.webView.mainFrame loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
}

BOOL hand = false;
NSPoint prevMouseLocOverUrl;

- (void)webView:(WebView *)sender mouseDidMoveOverElement:(NSDictionary *)elementInformation modifierFlags:(NSUInteger)modifierFlags {
    NSArray* keys = [elementInformation objectForKey:WebElementLinkURLKey];
    if (keys && !hand) {
        [self.urlLabel setStringValue:[NSString stringWithFormat:@"%@",keys]];
        hand = YES;
        // The following DOES NOT work, probably because Keyman is never the active app!
        [[NSCursor pointingHandCursor] set];
        prevMouseLocOverUrl = [NSEvent mouseLocation]; // current mouse position
    }
    else if (hand) {
        // This keeps getting called incessantly even if the mouse doesn't move, and if
        // the mouse is over a link, the keys only come back with the link info the very
        // first time.
        NSPoint currentMouseLoc = [NSEvent mouseLocation]; // current mouse position
        if (currentMouseLoc.x != prevMouseLocOverUrl.x || currentMouseLoc.y != prevMouseLocOverUrl.y) {
            [self.urlLabel setStringValue:@""];
            // The following DOES NOT work, probably because Keyman is never the active app!
            NSLog(@"CURSOR: Arrow");
            hand = NO;
            [[NSCursor arrowCursor] set];
        }
    }
}

@end
