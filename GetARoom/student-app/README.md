## IES Project Prototype
### Run application:

```bash
docker-compose up --build
```

### Get mysql shell from container
```bash
docker exec -it getaroom-mysqldb-1 mysql -h localhost -P 3306 -u root -p123456
```