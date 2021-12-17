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
cd GetARoom
docker-compose up --build
```

## Sensor

In order to run the Sensor application (the one that is run on the Raspberry Pi instances) do:
```bash
# Terminal 1
cd RaspberryPi
> Data/Output/logs.txt
./run.sh

# Terminal 2
cd RaspberryPi/sensor
# Use the '-h'/'--help' option for usage
./sensor [options]
```
