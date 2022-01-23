## How to run

### Build docker
```bash
docker build -t datagen . 
```

### Run docker background
```bash
docker run -d --network=host -p 1885:1883 datagen
```

### Run docker iteractive
```bash
docker run -i -t --network=host -p 1885:1883 datagen
```

### Change the broker host's address and client name
In order to change the broker's address, change the '--broker' argument to the desired address in the last line of the Dockerfile.
In order to change the MQTT client's name, change the '--name' argument in the last line of the Dockerfile too.
```
CMD ["./runGen.sh", "--broker", "<broker address>", "--name", "<client name>"]
```
