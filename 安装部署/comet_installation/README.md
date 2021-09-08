# README

## Installation

To install comet , simply:

```shell
$ tar zxf comet_installation.tar.gz
$ cd comet_installation
$ sudo ./install.sh
```

#### Notice

1. Before installation, open port 8080 and 27070~27073.
2. **You have to run the install.sh in root**.
3. This script will uninstall mariadb and mysql in rpm, uninstall redis, rocketmq in path /usr/local, and change $JAVA_HOME.  
4. Our module's version:

**JAVA:**

```shell
java version "1.8.0_261"
Java(TM) SE Runtime Environment (build 1.8.0_261-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.261-b12, mixed mode)
```

**MYSQL:**

```shell
Ver 8.0.21 for Linux on x86_64 (MySQL Community Server - GPL)
```

**REDIS:**

```shell
redis-cli 6.0.7
```

