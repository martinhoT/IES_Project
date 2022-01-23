# Use the Python3.7.2 image
FROM ubuntu

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

# Install the dependencies
RUN pip3 install -r requirements.txt

# run the command to start uWSGI
CMD ["./runGen.sh", "--broker", "34.140.235.127", "--name", "melga-gen"]
