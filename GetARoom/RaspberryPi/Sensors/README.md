## How to run

### Build docker
```bash
docker build -t sensors . 
```

### Run docker background
```bash
docker run -d --network=host -p 1885:1883 -p 5000:5000 --device=/dev/video0:/dev/video0 sensors
```

### Run docker iteractive
```bash
docker run -it --network=host -p 1885:1883 -p 5000:5000 --device=/dev/video0:/dev/video0 sensors
```

### Change the broker host's address
In order to change the broker's address, change the '--broker' argument to the desired address in the last line of the 'runSen.sh' script.
```bash
# Sender
./sender/sender "$@" --file "Data/Output/logs.txt" --broker "<broker address>"
```