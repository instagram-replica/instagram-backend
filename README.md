<p align="center">
  <img src="https://user-images.githubusercontent.com/11808903/36056926-52b61114-0e11-11e8-8d4e-b5b1cd5a84cf.png" width="100"/>
</p>

<h1 align="center">instagram-backend</h1>


## Architecture
### HTTP Server (Netty):
A Netty server that accepts HTTP requests on port `8080`. This server receives the HTTP `POST` request, converts it into JSON, makes sure the user is authenticated, uploads media file(s) (if any) to a media server and injects the returned URL(s) into the JSON request, stamps the request with a `UUID` and forwards it to a RMQ (RabbitMQ) queue using `requests_mapping.properties` file, then it waits for a response with the same UUID and returns to the user when it gets a response with a matching `UUID`.

### Services
Each service consists of an RMQ consumer, RMQ producer and a `ExecutorService`. Each service also has its own unique request and response queue.

- **RMQ Consumer**: Receives messages from the service's request queue
- **RMQ Producer**: Sends messages to the service's response queue
- **ExecutorService**: Initializes a thread pool and makes sure every incoming message gets allocated a thread from the thread pool

## Prequsites 
- RabbitMQ
- ArangoDB
- PostgreSQL
- [Postman](https://www.getpostman.com/apps) (for testing)

## Getting started
- Drop SQL and NoSQL (todo)
- Make sure RabbitMQ is running
- Clone the repo & open in IntelliJ
- Open Maven Projects tab and click compile
- Open Maven Projects tab, expand Plugins, expand `activejdbc-instrumentation` and click `activejdbc-instrumentation:instrument`
- Navigate to `persistence` package, expand `nosql`, click and run `ArangoInterfaceMethods.java` this file creates `NoSQL` tables and graphs

## Running the app
- Run `src/main/java/HTTPServer/Server.java`, this makes sure that the server is running on port 8080 and ready to accept HTTP requests
- Run `src/main/java/services/users/Server.java`, this file initializes an RMQ consumer for the users service that will be later used to authenticate requests
- Run `src/main/java/services/activities/Server.java`, this file initializes an RMQ consumer for activities that will be later used by other services
- Run `src/main/java/services/<your-service>/Server.java`, this also initializes an RMQ consumer for the specified service

## Sending requests
- Open Postman
- In the URL field write `http://localhost:8080/`
- Choose the type of the request to be `POST` request
- Underneath the URL field, click on `Body`, choose `raw` from the presented options
- Send a Sign up or Sign in request to obtain a token that will be used for authenticating other requests

```JSON
{
    "method" : "signUp",
    "params": {
        "username" : "username",
        "fullname" : "FullName",
        "passwordHash" : "!@#!@$C!",
        "email" : "user@user.com",
        "gender" : "male/female",
        "dateOfBirth" : "Sun Mar 04 23:05:25 EET 2018",
        "phone" : "+201009775576"
    }
}
```
- Using the returned token, go ahead and create a new tab in Postman, choose the same options mentioned eariler. Before sending the request, go to `Headers` tab and add `x-access-token` with the obtained token (obviously, we don't add any headers in the case of sending Signup or Signin requests)


