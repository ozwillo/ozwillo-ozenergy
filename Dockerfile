# Use an official openjdk base image
FROM ubuntu:xenial

# Maintainer info
MAINTAINER Marc Dutoo, marc.dutoo@smile.fr

# Set the working directory to /app
WORKDIR /app

# Copy and execute the deployment script
COPY faster/20160819_ozenergy_datacore /root/20160819_ozenergy_datacore
ADD deploy.sh /tmp/deploy.sh
RUN /bin/bash -C /tmp/deploy.sh

# Make port 8080 (http) available to the world outside this container
EXPOSE 8080

# Copy and set the start script for when the container launches
COPY start.sh /app/start.sh
CMD /bin/bash start.sh
