#!/bin/sh

# Workaround for Docker Studio since it doesn't support the --add-host parameter yet :
# Add other containers IP to /etc/hosts file
echo 172.17.0.5 ozwillo-datacore-1 >> /etc/hosts
echo 172.17.0.2 ozwillo-mongo-1 >> /etc/hosts
echo 172.17.0.3 ozwillo-mongo-2 >> /etc/hosts
echo 172.17.0.4 ozwillo-mongo-3 >> /etc/hosts

# Setup env variables
export SPARK_HOME=/root/spark-1.6.1-bin-hadoop2.6
export SPARK_MASTER_IP=localhost
export SPARK_MASTER_PORT=7077
export SPARK_MASTER_WEBUI_PORT=16160

# Start mongod and OzEnergy
mongod --fork --logpath /app/mongod.log --smallfiles
cd /app/ozwillo-ozenergy/oz-energy
mvn spring-boot:run -DrunAggregation -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000" > /app/ozenergy.log
