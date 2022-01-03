# IES_Project

Application for gathering entry/exit data of people in a building.

## Data interaction

There are 3 different interfaces:
- **Student**
- **Security Guard**
- **Analyst**

# Running the app

## GetARoom application

The GetARoom application includes the following parts:

- **student-app**: the Student application;
- **security-app**: the Security Guard application;
- **analyst-app**: the Analyst application;
- **fetcher**: the application that fetches the data from the MQTT message broker (mosquitto) and saves it in the MongoDB database.

In order to run the application, do:
```bash
cd GetARoom/App
docker-compose up --build
```

## MQTT sender

First Install:
```bash
sudo apt install libmosquittopp-dev
pip install numpy
```

And compile the C++ MQTT sender program, if needed:
```bash
cd GetARoom/RaspberryPi/sender
make
```

In order to run the sender application (the one that is run on the Raspberry Pi instances) do:
```bash
cd GetARoom/RaspberryPi
./run.sh
```

*Note: this script will be constantly appending data to the same file as long as it's being run, and the file is only cleared when the script is run again."*

## MongoDB

In order to access MongoDB, run application and do:
```bash
docker exec -it app_mongodb_1 mongo
```

To gain access to getaroom\_db first run sensor to have data:
```sql
use admin
db.auth("root","123456")
use getaroom_db
```

## MySQL

In order to access MySQL, run application and do:
```bash
docker exec -it app_mysqldb_1 mysql -h localhost -P 3306 -u root -p123456
```

To use the right database
```
use getaroom_db
```

## Data generation
### Generator
We use a generator built with Markov chains to simulate entrances and exits of people between rooms 

```bash
cd GetARoom/RaspberryPi/
./run.sh
```

### Sensors
#### Optical Sensor
We use a camera to verify entrances and exits for status reports

Code based on: https://github.com/saimj7/People-Counting-in-Real-Time
##### Run
```bash
cd GetARoom/RaspberryPi/Data_Generator/Camera_counter/
python -m venv ./venv
source ./venv/bin/activate
pip install -r requirements.txt
python run.py --prototxt mobilenet_ssd/MobileNetSSD_deploy.prototxt --model mobilenet_ssd/MobileNetSSD_deploy.caffemodel
```
##### Run demo
```
cd GetARoom/RaspberryPi/Data_Generator/Camera_counter/
python -m venv ./venv
source ./venv/bin/activate
pip install -r requirements.txt
python run.py --prototxt mobilenet_ssd/MobileNetSSD_deploy.prototxt --model mobilenet_ssd/MobileNetSSD_deploy.caffemodel --input videos/example_01.mp4
```

##### Configs
Change the following file:
```bash
Camera_counter/mylib/config.py
```

Change to integrated camera:
```python
url = 0
```

Change to remote camera:
```python
url = '<address>'
```

#### NFC Sensor
We use a NFC sensor to get identification of people who leave and enter a room

##### Run server
```bash
cd GetARoom/RaspberryPi/Nfc_sensor
python -m venv ./venv
source ./venv/bin/activate
pip install -r requirements.txt
cd ..
python Nfc_sensor/server.py
```

##### Simulate entrances and exits of people
```bash
curl -X POST -F 'name=<full name>' -F 'email=<email>' <flask server url>
```