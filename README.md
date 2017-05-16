[![Join the chat at https://gitter.im/imglib2-imglyb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/imglib2-imglyb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# imglib2-imglyb

`imglib2-imglyb` aims at connecting two worlds that have been seperated for too long:
 * Python with [numpy](https://github.com/numpy/numpy)
 * Java with [imglib2](https://github.com/imglib/ImgLib2)

`imglib2-imglyb` uses [PyJNIus](https://github.com/kivy/pyjnius) to access `numpy` arrays and expose them to `ImgLib2`.
This means **shared memory** between `numpy` and `ImgLib2`, i.e. any `ImgLib2` algorithm can run on `numpy` arrays without creating copies of the data!
For example, Python users can now make use of the [BigDataViewer](https://github.com/bigdataviewer/bigdataviewer-core) to visualize dense volumetric data.
If you are interested in using `imglib2-imglyb`, have a look at the [imglyb-examples](https://github.com/hanslovsky/imglyb-examples) repository and extend the examples as needed!



## Installation

If you are running Linux or, with limitations, OSX, you can get `imglib2-imlgyb` from conda:
```bash
conda install -c hanslovsky imglib2-imglyb
```
Re-activate the environment after installation to correctly set the environment variables.
If this does not work for you, please follow the build instructions below.

### Requirements
 * Python 2 or 3
 * Java 8
 * [Apache Maven](https://maven.apache.org/)
 * [Apache Ant](http://ant.apache.org/)
 * [imglib2-unsafe](https://github.com/imglib/imglib2-unsafe)
 * Currently: `imglib2-unsafe-0.0.1-SNAPSHOT.jar` in local maven repository (see instructions below)
 * pyjnius.jar (see instructions below)
 * Cython

### Build
```bash
# get PyJNIus
https://github.com/kivy/pyjnius
cd pyjnius
make # creates build/pyjnius.jar
make tests
python setup.py install
```

```bash
# get imglib2-unsafe-0.0.1-SNAPSHOT
git clone https://github.com/imglib/imglib2-unsafe
cd imglib2-unsafe
mvn clean install
```

```bash
cd /path/to/imglib2-imglyb
mvn clean package
python setup.py install
```

## Run

### Requirements
 * PyJNIus
 * Java 8
 * numpy

### Run
If you do not use conda you need to set your environment before using `imglib2-imglyb`:
```bash
export JAVA_HOME=/path/to/JAVA_HOME # not necessary if using conda
export PYJNIUS_JAR=/path/to/pyjnius/build/pyjnius.jar # not necessary if using conda
export IMGLYB_JAR=/path/to/imglib2-imglyb/target/imglib2-imglyb-<VERSION>.jar # not necessary if using conda
```
Note that, in your python files, the line
```python
import imglyb
```
needs to come before any of
```python
from imglyb import util
import jnius
from jnius import *
```
It is best to follow and extend the [imglyb-examples](https://github.com/hanslovsky/imglyb-examples) according to your needs.

## Known Issues
### AWT through PyJNIus on OSX

Currently, AWT, PyJNIus and cocoa do not get along very well
In general, the cocoa event loop needs to be started before the jvm or awt is loaded (see example below). 
This requires `PyObjC` which is not available through conda currently. (Thanks to @tpietzsch for figuring out!)
AWT frames (`JFrame`) are not visible or `BigDataViewer` crashes on user input.
`BigDataViewer` functionality is thus not available or very limited on OSX.
This is the working example for debugging and trying to figure out a way to fix the OSX issues:
```python
from __future__ import print_function

def main():
    import imglyb
    from imglyb import util
    from jnius import autoclass, cast
    import multiprocessing
    import numpy as np
    from skimage import io
    import time
    import vigra
    import argparse
    default_url = 'https://github.com/hanslovsky/imglyb-examples/raw/master/resources/butterfly_small.jpg'

    parser = argparse.ArgumentParser()
    parser.add_argument( '--url', '-u', default=default_url )

    args = parser.parse_args()

    RealARGBConverter = autoclass( 'net.imglib2.converter.RealARGBConverter')
    Converters = autoclass( 'net.imglib2.converter.Converters' )
    ARGBType = autoclass ( 'net.imglib2.type.numeric.ARGBType' )
    RealType = autoclass ( 'net.imglib2.type.numeric.real.DoubleType' )
    DistanceTransform = autoclass( 'net.imglib2.algorithm.morphology.distance.DistanceTransform' )
    DISTANCE_TYPE = autoclass( 'net.imglib2.algorithm.morphology.distance.DistanceTransform$DISTANCE_TYPE' )
    Views = autoclass( 'net.imglib2.view.Views' )
    Executors = autoclass( 'java.util.concurrent.Executors' )
    t = ARGBType()

    img = io.imread( args.url )
    argb = (
        np.left_shift(img[...,0], np.zeros(img.shape[:-1],dtype=np.uint32) + 16) + \
        np.left_shift(img[...,1], np.zeros(img.shape[:-1],dtype=np.uint32) + 8)  + \
        np.left_shift(img[...,2], np.zeros(img.shape[:-1],dtype=np.uint32) + 0) ) \
        .astype( np.int32 )

    print( "Creating color class..." )
    Color = autoclass( 'java.awt.Color' )
    print( Color.BLACK.toString() )
    JFrame = autoclass( 'javax.swing.JFrame' )
    print( "Before constructor" )
    jFrame = JFrame( "Test frame" )
    print( "After constructor" )
    jFrame.setPreferredSize( autoclass( 'java.awt.Dimension' )( 400, 300 ) )
    jFrame.pack()
    jFrame.setVisible( True )
    jFrame.show()
    print( "JFrame should be showing now" )

    print( "Showing first bdv" )
    bdv = util.BdvFunctions.show( util.to_imglib_argb( argb ), "argb", util.options2D().frameTitle( "b-fly" ) )
    print( "Showing second bdv" )

if __name__ == "__main__":
    import objc
    from Foundation import *
    from AppKit import *
    from PyObjCTools import AppHelper
    import sys

    class AppDelegate( NSObject ):

        def init( self ):
            self = objc.super( AppDelegate, self ).init()
            if self is None:
                return None
            return self

        def runjava_( self, arg ):
                        main()

        def applicationDidFinishLaunching_( self, aNotification ):
            self.performSelectorInBackground_withObject_( "runjava:", 0 )

    app = NSApplication.sharedApplication()
    delegate = AppDelegate.alloc().init()
    app.setDelegate_( delegate )
    AppHelper.runEventLoop()
    ```

