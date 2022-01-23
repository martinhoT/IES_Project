# IES_Project

Application for gathering entry/exit data of people in a building.
Detailed information about the project (such as the architecture) is present in the `reports` folder.

## Authors
| NMec | Name | Role |  
|:-:|:--|:--|  
| 98262 | Martinho Tavares | Team Leader |  
| 98157 | Miguel Monteiro | Product Owner |  
| 89123 | Tomás Candeias | Architect |  
| 98475 | Rodrigo Lima | DevOps master |  

## Links

The website link is http://34.140.235.127, hosted on Google Cloud. The different applications are hosted on the following ports:
- [**Student**](http://34.140.235.127:81): 81
- [**Security guard**](http://34.140.235.127:82): 82
- [**Analyst**](http://34.140.235.127:83): 83

## Data interaction

There are 3 different interfaces:
- **Student**
  - username: Student
  - password: Password
- **Security Guard**
  - username: Security
  - password: Password
- **Analyst**
  - username: Analyst
  - password: Password

## Front-end pages

Below is the complete list of accessible front-end pages for each application.

### Student
- [Login](http://34.140.235.127:81/login)
- [Register](http://34.140.235.127:81/register)
- [Search for a study room](http://34.140.235.127:81/studyRooms)

### Security guard
- [Login](http://34.140.235.127:82/login)
- [Register](http://34.140.235.127:82/register)
- [Heatmaps](http://34.140.235.127:82/heatmaps)
- [Logs](http://34.140.235.127:82/logs)
- [Blacklist](http://34.140.235.127:82/blacklist)
- [Notifications](http://34.140.235.127:82/notifications)

### Analyst
- [Login](http://34.140.235.127:83/login)
- [Register](http://34.140.235.127:83/register)
- [API](http://34.140.235.127:83/api)
- [Graphs](http://34.140.235.127:83/graphs)

## API Documentation
- [API](http://34.140.235.127:83/swagger-ui.html)
- [Internal API](http://34.140.235.127:84/swagger-ui.html)
