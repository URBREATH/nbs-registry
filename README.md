# urbreath-nbs-registry

## Overview

NBS registry is a Spring Boot application service that manages Natured-Based Solutions (NBS) for the UrBreath project. It offers an API to request and manage NBS according to user roles and exposes a proxy service to request data from third-party services of UrBreath application. Additionally, it connects with MinIO to store video and images for NBS.

## Table of Contents

1. [Installation](#installation)
2. [Usage](#usage)
3. [Deployment](#deployment)
4. [License](#license)
5. [Contributors](#contributors)

### Installation

1. Clone the repository:

    ```sh
    git clone https://github.com/URBREATH/nbs-registry.git
    cd nbs-registry
    ```

2. Install the dependencies:

    ```sh
    mvn install
    ```

3. Instantiate an instance of Keycloak with PostgreSQL, MinIO and MongoDB and configure the following variables:

   ```sh
    server.port=${APP_PORT:8093}
    application.url=${APP_URL:http://localhost:8093}
    spring.data.mongodb.port = ${MONGO_PORT:27017}
    spring.data.mongodb.host = ${MONGO_HOST:localhost}
    spring.data.mongodb.database = ${MONGO_DB:urbreath}
    spring.data.mongodb.username = ${MONGO_USERNAME:root}
    spring.data.mongodb.password = ${MONGO_PASSWORD:password}
    spring.keycloak.url=${KEYCLOAK_URL:###}
    keycloak.realm=${KEYCLOAK_REALM:urbreath-auth}
    keycloak.url=${KEYCLOAK_URL:http://localhost:9080}
    keycloak.client=${KEYCLOAK_CLIENT:urbreath}
    keycloak.client.secret=${KEYCLOAK_CLIENT_SECRET:###}
    spring.security.cors.domains=${CORS_DOMAINS:http://localhost:3000}
    urbreath.kpi.manager.url=${KPI_MANAGER_URL:http://localhost:8090}
    urbreath.idra.url=${IDRA_URL:http://localhost:8090}
    minio.url=${MINIO_URL:http://localhost:9001}
    minio.username=${MINIO_USERNAME:root}
    minio.password=${MINIO_PASSWORD:password}
    minio.bucket=${MINIO_BUCKET:nbs-registry}
   ```

### Usage

1. Run the application after MongoDB, Keycloak and MinIO are initiated:

    ```sh
    mvn spring-boot:run
    ```

2. The application will start on `http://localhost:8093`.

3. Access the OpenAPI documentation at `http://localhost:8093/api/nbs/swagger-ui/index.html`.

### Deployment

For local deployment Docker containers can be utilized to deploy the microservice with the following procedure:

1. Ensure Docker is installed and running.

2. Build the maven project:

    ```sh
    mvn package
    ```

3. Build the Docker container:

    ```sh
    docker build -t urbreath-nbs-registry:latest .
    ```

4. Run the Docker container including the environmental variables:

    ```sh
    docker run -d -p 8093:8093 --name urbreath-nbs-registry urbreath-nbs-registry:latest
    ```

   ``NOTE``: The following environmental variable should be configured:

   ```sh
    APP_PORT=..
    APP_URL=..
    MONGO_PORT=..
    MONGO_HOST=..
    MONGO_DB=..
    MONGO_USERNAME=..
    MONGO_PASSWORD=..
    KEYCLOAK_URL=..
    KEYCLOAK_REALM=..
    KEYCLOAK_CLIENT=..
    KEYCLOAK_CLIENT_SECRET=..
    CORS_DOMAINS=..
    KPI_MANAGER_URL=..
    IDRA_URL=..
    MINIO_URL=..
    MINIO_USERNAME=..
    MINIO_PASSWORD=..
    MINIO_BUCKET=..
   ```

5. To stop container run:

    ```sh
   docker stop urbreath-nbs-registry
    ```

## License

TThis project has received funding from the European Union's Horizon 2022 research and innovation programm, under Grant Agreement 101091996.

For more details about the licence, see the [LICENSE](LICENSE) file.

## Contributors

- Alkis Aznavouridis (<a.aznavouridis@atc.gr>)
