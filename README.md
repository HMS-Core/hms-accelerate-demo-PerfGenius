# HMS Accelerate Kit PerfGenius Sample

## Table of Contents

 * [Introduction](#introduction)
 * [Getting Started](#getting-started)
 * [Supported Environments](#supported-environments)
 * [License](#license)


## Introduction
PerfGenius SDK sample code encapsulates APIs of the PerfGenius SDK. It provides many sample programs for your reference or usage.
The following describes packages of Android sample code.
    
src/main/cpp/include/acckitdemo/PerfGeniusApi:                           Head file of plantform apis.

src/main/cpp/source/acckitjni-lib.cpp:                                   Sample code of demo API usage.

src/main/java/com/huawei/kirin/acckitdemo/jni/PerfGeniusJNI.java：        Sample code of demo JNI usage.

src/main/java/com/huawei/kirin/acckitdemo/ui/PerfGeniusApiActivity.java： Sample code of demo Java usage.

src/main/res/layout/activity_perfgenius_api.xml：                         Layout file of perfgenius API test.
    
## Getting Started

1. Check whether the Android studio development environment is ready. Open the sample code project directory with file "build.gradle" in Android Studio. Run TestApp on your divice or simulator which have installed latest Huawei Mobile Service(HMS).
2. Register a [HUAWEI account](https://developer.huawei.com/consumer/en/).
3. Create an app and configure the app information in AppGallery Connect.
See details: [PerfGenius SDK Development Preparation](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001054817121)
4. To build this demo, please first import the demo in the Android Studio (3.6+).
5. Configure the sample code:
     (1) Change the value of applicationid in the app-level build.gradle file of the sample project to the package name of your app.
6. Run the sample on your Android device or emulator.

## Supported Environments
1. Devices with Android 6.0 or later is recommended.

## License
PerfGenius SDK sample is licensed under the: [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
