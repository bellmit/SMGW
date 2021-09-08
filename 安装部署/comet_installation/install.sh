#!/bin/bash

INSTALL_PATH=`find / -name comet_installation 2> /dev/null | head -1`
PRO_PATH='/home/sms'
echo "[INFO]:install path is: $INSTALL_PATH"

###############check user################
user=`whoami`
if [[ $user != "root" ]];then
	echo "[ERROR]:Please switch to the \"root\" user for execution, installation will exit."
	exit
fi

########################################
mkdir -p $PRO_PATH/upload
mkdir -p $PRO_PATH/log
cd $INSTALL_PATH
cp -a java/jdk $PRO_PATH
cp -a rocketmq $PRO_PATH
cp -a java/jarstart $PRO_PATH
cp -a command $PRO_PATH

###############install mysql##############
ismariadb=`rpm -qa | grep -i maria`
ismysql=`rpm -qa | grep -i mysql`

echo "[INFO]:Checking mariadb and mysql installation......"
if [[ -n $ismariadb ]];then
	for p in $ismariadb
	do
		echo "[INFO]:find $p installed in this server, removing it....."
		yum -y remove $p > /dev/null 2>&1
	done
else
	echo "[INFO]:No mariadb server installed in this server...."
fi

if [[ -n $ismysql ]];then
        for p in $ismysql
        do
                echo "[INFO]:find $p installed in this server, removing it....."
		yum -y remove $p > /dev/null 2>&1
        done
else
        echo "[INFO]:No mysql server installed in this server...."
fi

echo "[INFO]:Installing required package....."
rpm -ivh $INSTALL_PATH/package/libaio-0.3.109-13.el7.x86_64.rpm

echo "[INFO] Adjusting parameter in /etc/sysctl.conf"
if [[ -z `grep "swappiness" /etc/sysctl.conf` ]];then
	sed -i "s/\(swappiness = \).*/\110/g" /etc/sysctl.conf
else
	echo "vm.swappiness = 10" >> /etc/sysctl.conf 
fi

echo "[INFO] Turnng off firewall"
systemctl stop firewalld
systemctl disable firewalld
sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config


echo "[INFO]:Starting install mysql....."

mv /etc/my.cnf /etc/my_default.cnf > /dev/null 2>&1
rm -rf /usr/local/mysql
cp -a $INSTALL_PATH/mysql/mysql /usr/local/mysql
mkdir /usr/local/mysql/log
touch /usr/local/mysql/log/error.log
cp -a $INSTALL_PATH/mysql/conf/my.cnf /etc
mkdir /etc/my.cnf.d/
cp -a $INSTALL_PATH/mysql/conf/mysql-clients.cnf /etc/my.cnf.d/

echo "[INFO]:adding user \"mysql\"....."
groupadd mysql
useradd -r -g mysql -s /bin/false mysql
chown -R mysql:mysql /usr/local/mysql
chmod -R 755 /usr/local/mysql

echo "[INFO]:adding mysql environment variable....."
echo "export MYSQL_HOME=/usr/local/mysql" >> /etc/profile
echo 'export PATH=$PATH:$MYSQL_HOME/bin' >> /etc/profile
source /etc/profile

echo "[INFO]:Initializing data......."
/usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql/ --lc_messages_dir=/usr/local/mysql/share --lc_messages=zh_CN

cp -a /usr/local/mysql/support-files/mysql.server /etc/init.d/mysql
chmod +x /etc/init.d/mysql
 
echo "[INFO] Starting mysql"
service mysql start
if [ $? -ne 0 ]; then
    echo "[ERROR]:mysql start failed, please check the error log!"
fi

cnt7=`ps -ef | grep mysql | grep -v grep | wc -l`
while [ $cnt7 -lt 2 ]
    do
        echo "     wait mysql startup..."
        sleep 2
        cnt7=`ps -ef | grep mysqld | grep -v grep | wc -l`
    done

echo "[INFO]:Changing mysql root password"
pass=`grep "temporary password" /usr/local/mysql/log/error.log |awk -F " " '{print $13}'`
/usr/local/mysql/bin/mysqladmin -uroot -p$pass password '3edc#EDC' 

echo "[INFO]:Restore sms database"
/usr/local/mysql/bin/mysql -uroot -p3edc#EDC -e "create database IF NOT EXISTS sms"
/usr/local/mysql/bin/mysql -uroot -p3edc#EDC sms < $INSTALL_PATH/mysql/data/sms.sql

echo "[INFO]:Set automatically start up at boot"
chkconfig --add mysql
chkconfig mysql on
chkconfig --list mysql

echo "[INFO]:Mysql installation is complete"

############jdk install###############
echo "[INFO]:Start install JDK"
echo "export JAVA_HOME=$PRO_PATH/jdk" >> /etc/profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> /etc/profile
echo "[INFO]:JDK installation is complete"
source /etc/profile

############redis installation begin#############
echo "[INFO]:Starting redis installation........"
mkdir -p /usr/local/redis/bin
cd $INSTALL_PATH/redis/package
cp -a ./* /usr/local/redis/bin
echo "[INFO]:Starting redis......"
/usr/local/redis/bin/redis-server /usr/local/redis/bin/redis.conf
echo 'export PATH=$PATH:/usr/local/redis/bin' >> /etc/profile
echo "[INFO]:Redis installation is complete"
source /etc/profile

###########rocketmq install#####################
echo "[INFO]:Start install rocketmq"
mv /usr/local/rocketmq /usr/local/rocketmq.bak > /dev/null 2>&1
cp -a $INSTALL_PATH/rocketmq /usr/local
mkdir -p /usr/local/rocketmq/log
touch /usr/local/rocketmq/log/broker.log  /usr/local/rocketmq/log/mqnamesrv.log
echo "[INFO]:Starting rocketmq........."
nohup `sh /usr/local/rocketmq/bin/mqnamesrv -c /usr/local/rocketmq/conf/namesrv.properties > /usr/local/rocketmq/log/mqnamesrv.log 2>&1` >  /dev/null 2>&1 &
nohup `sh /usr/local/rocketmq/bin/mqbroker -n 127.0.0.1:27073 -c /usr/local/rocketmq/conf/broker.conf > /usr/local/rocketmq/log/broker.log 2>&1` > /dev/null 2>&1 &
echo "[INFO]:Rocketmq installation is complete"

mem=`free -h | grep -i mem | awk '{print $2}' | cut -d "." -f1 | grep -Po "[0-9]+"`
base=`expr \( $mem + 2 \) \/ 16`
if [[ $base -le 0 ]];then
	echo "[ERROR]:Mem is too small to run the system,please update your mem over 16G."
	nohup `java -Dfile.encoding=utf-8 -Dspring.profiles.active=admin -jar -Xms1G -Xmx1G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1` > /dev/null 2>&1 &
	nohup `java -Dfile.encoding=utf-8 -Dspring.profiles.active=pro -jar -Xms1G -Xmx1G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1` > /dev/null 2>&1 &
	echo "[INFO]:Install complete, welcome!"
	exit
fi
multi2=`expr $base \* 2`
multi3=`expr $base \* 3`
multi6=`expr $base \* 6`
echo "nohup java -Dfile.encoding=utf-8 -Dspring.profiles.active=admin -jar -Xms${multi2}G -Xmx${multi6}G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1 &" >> ${PRO_PATH}/java/jarstart
echo "nohup java -Dfile.encoding=utf-8 -Dspring.profiles.active=pro -jar -Xms${base}G -Xmx${multi3}G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1 &" >> ${PRO_PATH}/java/jarstart
echo "[INFO]:System starting......."
nohup `java -Dfile.encoding=utf-8 -Dspring.profiles.active=admin -jar -Xms${multi2}G -Xmx${multi6}G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1` > /dev/null 2>&1 &
nohup `java -Dfile.encoding=utf-8 -Dspring.profiles.active=pro -jar -Xms${base}G -Xmx${multi3}G ${PRO_PATH}/java/sms.jar >/dev/null 2>&1` > /dev/null 2>&1 &
echo "[INFO]:Install complete, welcome!"
