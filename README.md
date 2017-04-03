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
Adjust the python version according to your needs.
If this does not work for you, please follow the build instructions below.

### Requirements
 * Python 2 or 3
 * Java 8
 * [Apache Maven](https://maven.apache.org/)
 * [imglib2-unsafe](https://github.com/imglib/imglib2-unsafe)
 * Currently: `imglib2-unsafe-0.0.1-SNAPSHOT.jar` and `imglib2-algorithm-0.6.4-SNAPSHOT.jar` in local maven repository (see instructions below)

### Build
```bash
# get imglib2-unsafe-0.0.1-SNAPSHOT
git clone https://github.com/imglib/imglib2-unsafe
cd imglib2-unsafe
mvn clean install
```
```bash
# get imglib2-algorithm-0.6.4-SNAPSHOT.jar
git clone https://github.com/imglib/imglib2-algorithm
cd imglib2-algorithm
mvn install
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

### Run
If you do not use conda you need to set your environment before using `imglib2-imglyb`:
```bash
export JAVA_HOME=/path/to/JAVA_HOME # not necessary if using conda
export PYJNIUS_JAR=/path/to/pyjnius/build/pyjnius.jar # not necessary if using conda
export IMGLYB_JAR=/path/to/imglib2-imglyb/target/imglib2-imglyb-<VERSION>.jar # not necessary if using conda
```
Note that the line
```python
import imglyb
```
needs to come before any of
```python
from imglyb import util
import jnius
from fjnius import *
```
It is best to follow and extend the [imglyb-examples](https://github.com/hanslovsky/imglyb-examples) according to your needs.
