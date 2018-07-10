[![Join the chat at https://gitter.im/imglib2-imglyb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/imglib2-imglyb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**IMPORTANT NOTE** The python code has been extracted into a [hanslovsky/imglyb](https://github.com/hanslovsky/imglyb). This repository contains Java code only now.

# imglib2-imglyb

`imglib2-imglyb` gives access pointers into native memory and expose them to `ImgLib2` data structures.
This means **shared memory** between native code, e.g. `numpy` in Python, and `ImgLib2`, i.e. any `ImgLib2` algorithm can run on native arrays without creating copies of the data!
For example, Python users can now make use of the [BigDataViewer](https://github.com/bigdataviewer/bigdataviewer-core) to visualize dense volumetric data.


## Installation

`imglib2-imlgyb` is available on the [saalfeldlab maven repository](https://github.com/saalfeldlab/maven). Add this to your `pom.xml`:
```xml
<repositories>
    <repository>
        <id>saalfeld-lab-maven-repo</id>
        <url>https://saalfeldlab.github.io/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.imglib</groupId>
        <artifactId>imglib2-imglyb</artifactId>
        <version>${imglib2-imglyb.version}</version>
    </dependency>
</dependencies>
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
cd /path/to/imglib2-imglyb
mvn clean package
```


