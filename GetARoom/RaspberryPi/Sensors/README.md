## How to run

### Build docker
```bash
docker build -t datagen . 
```

### Run docker background
```bash
docker run -d --network=host -p 1885:1883 -p 5000:5000 --device=/dev/video0:/dev/video0 sensors
```

### Run docker iteractive
```bash
docker run -it --network=host -p 1885:1883 -p 5000:5000 --device=/dev/video0:/dev/video0 sensors
```