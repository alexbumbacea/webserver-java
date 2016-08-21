## Features
* supported method GET and HEAD
* static file delivery
* access log / error log
* streams support for large files
* dynamic pages trough extending "AbstractController" class

## Usage

### Configuration
You can configure port and path for checking for files

    Config serverConfig = new Config();
    serverConfig.setPort(8080);
    serverConfig.setPath("/tmp");
    Webserver server = new Webserver(serverConfig);

#### Configure number of workers

    Config.POOL_SIZE (default is 20)

#### Configure timeout on socket

    Config.HTTP_SOCKET_TIMEOUT (default is 5000 in MS)

#### Load dynamic pages

    serverConfig.addMapping("GET", "/test.dynamic", new SampleController());

#### Configure logger

Edit the file "logger.properties" according to the documentation from here https://docs.oracle.com/cd/E19717-01/819-7753/gcblo/

### Build
    mvn clean package
### Test
For the test component apache http client is used
in order to simulate the protocol from an external client

    mvn clean test
### Run
    java -jar target/web-1.0-SNAPSHOT.jar "<folder>" <port>
