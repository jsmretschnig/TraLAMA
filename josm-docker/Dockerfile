# dockerfile

# Use ubuntu as a parent image - 18.04 has LTS
FROM ubuntu:18.04

# MAINTAINER Jakob Smretschnig <jakob.smretschnig@tum.de>, based on docker-image by Young Hahn <https://www.mapbox.com>
LABEL Description="Dockerised JOSM (Java OpenStreetMap) Editor with TraLAMA Plugin"

WORKDIR /josmDocker

# Install system dependencies
RUN apt-get -qq update && apt-get -qq install \
	wget \
	apt-transport-https \
	openjdk-8-jdk \
	xorg \
	locales locales-all

# use the built .jar file
RUN wget https://josm.openstreetmap.de/josm-tested.jar

# copy the plugin and default preferences to the container
COPY ./TraLAMA.jar /root/.local/share/JOSM/plugins/
COPY ./defaultPreferences.xml /root/.config/JOSM/preferences.xml

# change the timezone
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# add firefox to the image
#RUN apt-get update && apt-get install -y firefox
#ENV BROWSER /usr/bin/firefox

# change the default language
RUN sed -i -e 's/# de_DE.UTF-8 UTF-8/de_DE.UTF-8 UTF-8/' /etc/locale.gen && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=de_DE.UTF-8
ENV LANG de_DE.UTF-8

# for shared volume
WORKDIR /ACPS/data

EXPOSE 3030

CMD java -jar /josmDocker/josm-tested.jar
