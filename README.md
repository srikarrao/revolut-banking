# Revolut Banking (Money Transfer API)

Revolut Coding Exercise


#### Steps To Execute ####

**Prerequisites**

* Java JDK 11

* Run `./gradlew clean build` or `gradlew.bat clean build`
* Go to `build/distributions` and unpack `revolut-banking-0.0.1-SNAPSHOT.zip`
* Go to `revolut-banking-0.0.1-SNAPSHOT/bin` and run there `./revolut-banking` or `revolut-banking.bat`. 
* Open `http://localhost:8080/ping` in your browser to get the `pong` message
* Use below endpoints to execute/test the API using Postman/SoapUI tool

#### REST API#### 

**Get Account Endpointurl**

```
http://localhost:8080/v1/accounts/802909
Status: 200 OK
Method: GET
```

**Get Account Response**

```
{
    "status": 200,
    "data": {
        "accNumber": 802909,
        "balance": 130
    }
}
```

**Transfer Amount Endpointurl**

```
http://localhost:8080/v1/transfers
Status: 200 OK
Method: POST
```

**Transfer Amount Request**

```
{
    "amount": 10.00,
    "fromAccount": 137985,
    "toAccount": 802909
}
```

**Transfer Amount Response**

```
{
    "status": 200,
    "data": {
        "accNumber": 137985,
        "balance": 1010
    }
}

```
**Create Account Endpointurl**

```
http://localhost:8080/v1/accounts
Status: 200 OK
Method: POST
```

**Create Account Request**

```
{
	"balance":120.12
}
```

**Create Account Response**

```
{
    "status": 200,
    "data": {
        "accNumber": 802909,
        "balance": 120.12
    }
}
```

#### Technologies Used ####
* **Spark Java Rest Framework:**
For exposing REST API endpoints. Has a bug in framework for trailing '/' in the endpoint url. Tried intercepting the request and changing the resourcePath. Didn't worked. For this application standard is no trailing '/' in the resource URI. Used default Jetty http server. 
* **Wix-Embedded-MySql:** Used this to store the account details. The schema is pre-built during application bootstrap using DB_INIT_SCRIPT.sql.. Used `ENGINE=MEMORY` which provides in-memory SQL database.
 
**NOTE:**  `ENGINE=INNODB` provides ACID transaction support, with `ENGINE=MEMORY` is is not transaction strong. Being in-memory, the data will be lost once the MySql process is terminated. 
* Sql2o Database Client: Used this framework as an alerantive to JDBC, Hibernate, Spring JPA. Performance metrics are favorable with speed.
* Used Mockito & Junit for Unit Testing.
* Gradle as build management tool.
* Java Version 11. Though didn't get any use case to use its latest features.
