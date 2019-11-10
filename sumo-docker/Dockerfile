# dockerfile

# Use ubuntu as a parent image - 18.04 has LTS
FROM ubuntu:18.04

# MAINTAINER Jakob Smretschnig <jakob.smretschnig@tum.de>, based on docker-image by Bo Gao <bogao@dcs.warwick.ac.uk> 
LABEL Description="Dockerised SUMO (Simulation of Urban MObility)"

ENV SUMO_HOME /sumo

# Install system dependencies
RUN apt-get -qq update && apt-get -qq install \
    cmake g++ libxerces-c-dev libfox-1.6-dev libgdal-dev libproj-dev libgl2ps-dev swig git \
    ffmpeg \
    wget \
    sudo \
    xorg \
    python3-dev \
    python3-pip \
    python3-rtree

# Download and build sumo
RUN git clone --recursive https://github.com/eclipse/sumo --verbose --progress && mkdir sumo/build/cmake-build
WORKDIR sumo/build/cmake-build
RUN cmake ../.. && make -j8

# Install python dependencies & Copy the RESTful service
COPY requirements.txt /
RUN mkdir -p /app && pip3 install -r /requirements.txt
COPY SimulationController.py /app

COPY tools /app/tools

# TODO
#RUN apt-get -qq update && apt-get -qq install python3-tk
#RUN cd $SUMO_HOME && ./configure && make install
# remove tmp files

# for shared volume
WORKDIR /ACPS

EXPOSE 4040

CMD ["python3", "/app/SimulationController.py"]
