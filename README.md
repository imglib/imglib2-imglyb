[![](https://github.com/imglib/imglib2-imglyb/actions/workflows/build-main.yml/badge.svg)](https://github.com/imglib/imglib2-imglyb/actions/workflows/build-main.yml)
[![developer chat](https://img.shields.io/badge/zulip-join_chat-brightgreen.svg)](https://imagesc.zulipchat.com/#narrow/stream/327240-ImgLib2)

**IMPORTANT NOTE** The python code has been extracted into a [imglib/imglyb](https://github.com/imglib/imglyb). This repository contains Java code only now. To install the python package from conda, run
```bash
conda install -c conda-forge imglyb
```

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

### Build
```bash
cd /path/to/imglib2-imglyb
mvn clean package
```


