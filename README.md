## Features
* supported method GET and HEAD
* static file delivery
* access log / error log
* streams support for large files

## Usage

### Configuration
You can configure port and path for checking for files

    new Webserver(path, port);

You can also configure the number of workers

    Webserver.POOL_SIZE (default is 20)

and

    Webserver.HTTP_SOCKET_TIMEOUT (default is 5000 in MS)
### Build
    mvn clean package
### Test
For the test component apache http client is used
in order to simulate the protocol from an external client

    mvn clean test
### Run
    java -jar target/web-1.0-SNAPSHOT.jar "<folder>" <port>
