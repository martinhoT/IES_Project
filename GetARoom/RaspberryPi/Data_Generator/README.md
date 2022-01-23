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