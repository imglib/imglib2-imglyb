"""

This script is a wrapper to allow imglib2-imglyb code that uses Java's AWT
to run properly on OS X.  It starts the Cocoa event loop before Java and
keeps Cocoa happy  See https://github.com/kivy/pyjnius/issues/151 for more.

In particular, this wrapper allows one to run the code from imglyb-examples.

Python 3 only!

usage: python OSXAWTwrapper.py [module name | script path] [module or script parameters]

"""

import os
import sys


import objc
from Foundation import *
from AppKit import *
from PyObjCTools import AppHelper

def runAwtStuff():

    import runpy

    # user can input either a module or a path to a script;
    #   either way, need to remove it from sys.argv,
    #   because the module or script might parse it for its own parameters:
    if len(sys.argv) > 1:
        name = sys.argv[1]
        sys.argv.remove(name)

        # whether module or script, need to set the run_name for things to work as expected!
        if os.path.exists(name):
            runpy.run_path(name, run_name="__main__")
        else:            
            runpy.run_module(name, run_name="__main__")
    else:
        print("no module or script specified")


class AppDelegate (NSObject):
    def init(self):
        self = objc.super(AppDelegate, self).init()
        if self is None:
            return None
        return self

    def runjava_(self, arg):
        runAwtStuff()
        # need to terminate explicitly, or it'll hang when
        #   the wrapped code exits
        NSApp().terminate_(self)

    def applicationDidFinishLaunching_(self, aNotification):
        self.performSelectorInBackground_withObject_("runjava:", 0)


def main():
    app = NSApplication.sharedApplication()
    delegate = AppDelegate.alloc().init()
    NSApp().setDelegate_(delegate)
    AppHelper.runEventLoop()

if __name__ == '__main__' : 
    main()