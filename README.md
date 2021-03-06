<p align="center">
  <img src="https://user-images.githubusercontent.com/11808903/36056926-52b61114-0e11-11e8-8d4e-b5b1cd5a84cf.png" width="100"/>
</p>

<h1 align="center">instagram-backend</h1>

## Architecture

### HTTP Server
A Netty server accepts HTTP requests on port `8080`. This server receives an HTTP `POST` request, parses its JSON payload, makes sure the user is authenticated, uploads media file(s) _(if any)_ to a media server & injects the returned URL(s) into the JSON payload, stamps the payload with a UUID & forwards it to a RabbitMQ queue using the `requests_mapping.properties` file, then it waits for a response with the same UUID and returns to the user when it gets a response with a matching UUID.

### Services
Each service consists of an RMQ consumer, RMQ producer & an `ExecutorService`. Each service also has its own unique request & response queue.

- **RMQ Consumer**: Receives messages from the service's request queue
- **RMQ Producer**: Sends messages to the service's response queue
- **ExecutorService**: Initializes a thread pool & makes sure every incoming message gets allocated a thread from the thread pool

## Prerequisites
- Java (8 or higher)
- [Docker](https://docs.docker.com/install/)
- [Postman](https://www.getpostman.com/apps) _(for testing)_

## Getting started
- Clone the repo & open in IntelliJ
- Change directory to repository folder `cd <path/to/repo>` 
- Run `docker-compose up` 
- Open Maven Projects tab, expand `Lifecycle` & click `compile`
- Open Maven Projects tab, expand `Plugins`, expand `activejdbc-instrumentation` & click `activejdbc-instrumentation:instrument`
- Navigate to `persistence` package, expand `sql/users`, click & run `Migrations.java`. This file creates the SQL tables.
- Navigate to `persistence` package, expand `nosql`, click & run `ArangoInterfaceMethods.java`. This file creates the NoSQL tables & graphs.

## Running the app
- Run `src/main/java/http_server/Server.java`, this makes sure that the server is running on port 8080 & ready to accept HTTP requests
- Run `src/main/java/services/users/Server.java`, this file initializes an RMQ consumer for the users service that will be later used to authenticate requests
- Run `src/main/java/services/activities/Server.java`, this file initializes an RMQ consumer for activities that will be later used by other services
- Run `src/main/java/services/<your-service>/Server.java`, this also initializes an RMQ consumer for the specified service

## Sending requests
- Open Postman
- In the URL field, write `http://localhost:8080/`
- Choose the type of the request to be `POST` request
- Underneath the URL field, click on `Body`, choose `raw` from the presented options
- Send a Sign up or Sign in request to obtain a token that will be used for authenticating other requests

```json
{
  "method" : "signup",
  "params": {
      "username" : "john_doe",
      "email" : "john.doe@user.com",
      "password" : "123456",
      "isPrivate": false,
      "fullName" : "John Doe",
      "gender" : "male"
  }
}
```

- Using the returned token, go ahead and create a new tab in Postman, choose the same options mentioned eariler. Before sending the request, go to `Headers` tab and add `x-access-token` with the obtained token _(obviously, we don't add any headers in the case of sending signup or login requests)_
