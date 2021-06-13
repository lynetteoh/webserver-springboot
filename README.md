# webserver-springboot
Simple webserver created with spring boot - Java

## Prerequisite
1. Java 8 
2. IntelliJ
3. Maven 3.8.1 
4. MongoDB (optional)

### Introduction 
This webserver is build with Java, vert.x framework and MongoDB. Currently, this webserver is connected to MongoDB Atlas. To connect to local mongodb, 
please refer to [MongoDB Configuration](#MongoDBConfiguration). 

#### Run 
1. Command Line 
   
- Go to ../spring.webserver
```
mvn clean package
java -jar target/spring.webserver-0.0.1-SNAPSHOT.jar
```

2. IntelliJ 
   
   Import project and run from Application class

### MongoDB Configuration 
1. To connect to your MongoDB, edit the config file: src/main/resources/application.properties 

### API Supported 
   1.  GET /feature?email=XXX&featureName=XXX
   
This endpoint receives (user’s email) and featureName as request parameters and returns the following response in JSON format. If featureNmae or user do not exists, a response with Http Status Not Found(404) is returned. If parameters received are not in the correct format, a response with Http Status Bad Request(400) is returned. 

```
{  
	"canAccess": true|false (will be true if the user has access to the featureName
}
```

2.  POST /feature

This endpoint receives the following request in JSON format and returns an empty response with HTTP Status OK (200) when the database is updated successfully, otherwise returns Http Status Not Modified (304). Validation will be perform to request body to make sure it adheres to the json schema. During validation fails, a response with Http Status Bad Request is returned. 

> Request Body: 
```
{
	"featureName": "xxx", (string)
	"email": "xxx", (string) 
	"enable": true|false (boolean) (uses true to enable a user's access, otherwise, false
}
```



