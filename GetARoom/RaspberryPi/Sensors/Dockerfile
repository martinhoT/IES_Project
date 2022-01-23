# Use the Python3.7.2 image
FROM ubuntu

# Set variable for snowfalke tzdata install
ENV TZ=Europe/Lisbon
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set the working directory to /app
WORKDIR /Data_Generator

# Copy the current directory contents into the container at /app 
ADD . /Data_Generator

# Update and enable https sources
RUN apt-get update && apt-get install -y apt-transport-https

# Get python
RUN apt-get -y install python3

# Get pip
RUN apt-get -y install python3-pip

# Install the dependencies
RUN apt-get -y install libmosquitto-dev

# Install cmake for dlib error
RUN apt-get -y install cmake

# Install the dependencies
RUN pip3 install -r requirements.txt

# Install the cv2 dependencies that are normally present on the local machine, but are missing in our Docker
RUN apt-get install ffmpeg libsm6 libxext6  -y

# run the command to start uWSGI
CMD ["./runSen.sh", "--broker", "34.140.235.127"]
