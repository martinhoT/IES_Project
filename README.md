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

## Sensor

In order to run the Sensor application (the one that is run on the Raspberry Pi instances) do:
```bash
cd GetARoom/RaspberryPi
./run.sh
```

*Note: this process will be constantly appending data to the same file as long as it's being run, and the file is only cleared when the script is run again."
