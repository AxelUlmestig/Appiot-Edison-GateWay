# 0: Introduction  
This guide will cover how to use two Intel Edisons as gateway and sensor 
respectively. The sensor Edison will measure temperature and send it to the 
gateway Edison which will then send it to Appiot.

In order to do that you need to install some software on the edison, register 
the devices and build the code.

# 1: Prerequisites
1.1: Edison setup
Follow the instructions on this page: (there are links to other OS'
s if you're not on Windows)
https://software.intel.com/en-us/get-started-edison-windows

### 1.2: Maven
```sh
$ wget http://apache.mirrors.spacedump.net/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
$ tar xf apache-maven-3.3.9-bin.tar.gz
$ export PATH=${PWD}/apache-maven-3.3.9/bin/:$PATH
```

### 1.3: BlueZ
The version of BlueZ that comes with the Edison might be too old for the TinyB 
package that is used for the BLE communcation.

A newer version can be downloaded here:
http://www.bluez.org/release-of-bluez-5-42/

//TODO verify that this works
```sh
$ wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.42.tar.xz
$ tar xf bluez-5.42.tar.xz
$ cd bluez-5.42/
$  ./configure --prefix=/usr --mandir=/usr/share/man \
$               --sysconfdir=/etc --localstatedir=/var
$ make && make install
```

### 1.3: Java Cacerts
if the file /usr/lib/jvm/java-8-openjdk/jre/lib/security/cacerts is empty (or 
non-existing) you can copy the cacerts from the computer you're working on.

In my case they were located under
/c/program\ Files/Java/jdk1.8.0_101/jre/lib/security/cacerts

# 2: Build TinyB
### 2.1: Clone project
```sh
$ git clone https://github.com/intel-iot-devkit/tinyb.git
```

### 2.2: Remove version check: 
(This is because the specificationVersion information is lost in Maven, would 
be nice to fix)
```sh
$ nano java/BluetoothManager.java
```
Add a block comment starting on line 219 and ending on line 239 (after the 
if case). The getBluetoothManager method should look like this when you're 
done:

```java
public static synchronized BluetoothManager getBluetoothManager() throws RuntimeException, BluetoothException
{
    if (inst == null)
    {
        inst = new BluetoothManager();
        inst.init();
        /*
        String nativeAPIVersion = getNativeAPIVersion();
        String APIVersion = BluetoothManager.class.getPackage().getSpecificationVersion();
        if (APIVersion.equals(nativeAPIVersion) == false) {
            String[] nativeAPIVersionCode = nativeAPIVersion.split("\\D");
            String[] APIVersionCode = APIVersion.split("\\D");
            if (APIVersionCode[0].equals(nativeAPIVersionCode[0]) == false) {
                if (Integer.valueOf(APIVersionCode[0]) < Integer.valueOf(nativeAPIVersionCode[0]))
                    throw new RuntimeException("Java library is out of date. Please update the Java library.");
                else throw new RuntimeException("Native library is out of date. Please update the native library.");
            }
            else if (APIVersionCode[0].equals("0") == true) {
                if (Integer.valueOf(APIVersionCode[1]) < Integer.valueOf(nativeAPIVersionCode[1]))
                    throw new RuntimeException("Java library is out of date. Please update the Java library.");
                else throw new RuntimeException("Native library is out of date. Please update the native library.");
            }
            else if (Integer.valueOf(APIVersionCode[1]) < Integer.valueOf(nativeAPIVersionCode[1]))
                System.err.println("Java library is out of date. Please update the Java library.");
            else System.err.println("Native library is out of date. Please update the native library.");
        }
        */
    }
    return inst;
}
```

### 2.4 Set Java variables:
```sh
$ export JAVA_HOME=/usr/lib/jvm/java-8-openjdk/
$ export PATH=$JAVA_HOME/bin:$PATH
```

### 2.3 Build:
Move to the top of the tinyb folder and execute the following shell commands:
```sh
$ mkdir build
$ cd build
$ cmake -DBUILDJAVA=ON -DCMAKE_INSTALL_PREFIX=/usr ..
$ make
$ sudo make install
```

There should now be a file called tinyb.jar under
/usr/lib/java/

# 3: Build the SampleGateway
### 3.1 Set up $SENSATION_HOME
Create sensation home folder and assign the variable $SENSATION_HOME to it's 
path.
```sh
$ mkdir ~/sensation_home
$ export SENSATION_HOME=~/sensation_home
```

```sh
$ git clone <gateway project> //TODO insert proper link
```

Install dependencies and package tinyb.jar for maven

```sh
$ mvn install:install-file \
        -Dfile=/usr/lib/java/tinyb.jar \
        -DgroupId=tinyb \
        -DartifactId=tinyb \
        -Dversion=1.0 \
        -Dpackaging=jar \
        -DgeneratePom=true
```

//TODO add some details
Set the macAdress, serialNumber and sensorHardwareTypeId in
SampleGateway.java

Compile a runnable jar file
```sh
$ mvn compile:assembly:single
```

# 4: Run
There should now be a runnable jar file under
SampeGateway/target/SampleGateway-0.0.1-jar-with-dependencies.jar
Run it with
```sh
$ java -jar SampeGateway/target/SampleGateway-0.0.1-jar-with-dependencies.jar
```

You should now be able to see the readings from the other Edison
written in the terminal now.

# 5: Set up Sensors in Appiot
### 5.1 Create device type
Go to 'Settings' -> 'Hardware Types'
Then press Device Types and then Create.
Name the "Edison Temperature" and give it a random id between
10000 and 30000.
Press 'Save'.

### 5.2 Register Device
Go back to the front page and press 'Register Device'.
Set the Serial Number to your Edison's mac address (or some other unique id).
Choose your newly created Device Type "Edison Temperature".
Press 'Continue' and then 'Register'.

# APPENDIX
If you're getting problems with opkg you might need to specify the repos for 
opkg:

```sh
$ nano /etc/opkg/base-feeds.conf
```
Add the following lines and save:
```
src all     http://iotdk.intel.com/repos/1.1/iotdk/all
src x86 http://iotdk.intel.com/repos/1.1/iotdk/x86
src i586    http://iotdk.intel.com/repos/1.1/iotdk/i586
```
