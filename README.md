# Recommendation service

## How to run the service

You can run from the terminal using Java 17 or from your favourite IDE.

> mvn clean package 

> java -jar target/recommendations-0.0.1-SNAPSHOT.jar

#### Application is available at `localhost:8080/api/cryptos`

> curl --location 'localhost:8080/api/cryptos/stats'

> curl --location 'localhost:8080/api/cryptos/stats/XRP'

> curl --location 'localhost:8080/api/cryptos/highest-range/2022-01-13'

> curl --location 'localhost:8080/api/cryptos/stats?startDate=2022-01-01&endDate=2022-01-10'

> curl --location 'localhost:8080/api/cryptos/stats/XRP?startDate=2022-01-01&endDate=2022-01-10'

#### Documentation is available at `http://localhost:8080/swagger-ui/index.html#/`

---

## How to run in kubernetes/docker

First package the application
> mvn clean package

Build the image
> docker build -t pflorian91/faptic-recommendation-service:latest .

If you want to run it locally using the docker container
> docker run -p 8080:8080 pflorian91/faptic-recommendation-service:latest

Access application e.g. `http://localhost:8080/api/cryptos`

Continue for k8s configuration..

Push the image
> docker push pflorian91/faptic-recommendation-service:latest

Install NGINX Ingress Controller
> helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

> helm repo update

> helm install nginx-ingress ingress-nginx/ingress-nginx

Apply the helm
> kubectl apply -f helm/deployment.yaml

> kubectl apply -f helm/service.yaml

> kubectl apply -f helm/ingress.yaml

Verify pods are running
> kubectl get deployments

> kubectl get pods

Access application e.g. http://localhost/api/cryptos/stats

To test the rate limit

> for i in {1..200}; do curl http://localhost/api/cryptos/stats; done

You should see `503 Service Temporarily Unavailable` in the logs

----

Cleaning up the deployments

> kubectl delete deployment recommendation-service

> kubectl delete service recommendation-service

> kubectl delete ingress recommendation-service-ingress

---

# Requirements and considerations

Documentation for endpoints: Added Swagger/OpenAPI annotations for documenting the endpoints.

Scalability for more cryptos: The service reads crypto data from CSV files in a dynamic way, which should handle additional cryptos easily as long as they follow the same file format and naming convention.

Safeguarding against unsupported cryptos: The service implements isKnownSymbol in CryptoService and effectively checks in controller to address this concern.

Handling different time frames for analysis: The endpoints accept start and end dates, allowing flexible time frame analysis. Defaults to the interval for which data was provided.

Added proper error handling and considered edge cases (like invalid inputs and incorrect data formats).



### Real-world scenario with large datasets

This implementation loads all CSV data into memory at startup. For large datasets, this might not be practical, and we need a more scalable approach.

1. Switch from loading and storing data in-memory to using a database.

We can use a relational database like PostgreSQL to store the data after it was read from the CSV files. We can employ data normalization and indexing strategies, especially on fields like timestamp and symbol, to speed up query performance.

We need to implement a routine to read CSV files and store data in the database. This could be a one-time data migration script or part of the application's startup routine.

2. Lazy Loading and Caching

Instead of loading all data at startup, we can load it on-demand. When a request is made for a particular crypto's data, check if it is already loaded; if not, load it from the corresponding CSV file.

We can cache frequently accessed data (i.e. computed statistics) in a caching solution like Redis. This approach reduces the need to read from the database on every request.

3. Stream Processing

For very large datasets, we can consider stream processing - maybe use a library like Apache Kafka Streams or Apache Flink for processing data streams. These tools can handle large volumes of data efficiently.

Real-time Processing: Process data in real-time as it is ingested, which is beneficial if the CSV data can be streamed as it is generated.

4. Pagination and Query Optimization

If fetching all records at once is not necessary, implement pagination:

Pagination: Fetch only a subset of data at a time. This approach reduces memory usage and improves response times.

Query Parameters: Allow API consumers to specify filters, sorting options, and pagination details.

5. Microservices and Scalability:

If scalability is a prime concern, discuss the potential transition to a microservices architecture, where different aspects of the system (data ingestion, processing, API serving) can be scaled independently.


### Conclusion 

The implementation choice depends on the specific requirements of the dataset size, frequency of data updates and access patterns. 
In many cases, a combination of these strategies provides the best results. 

For example, using a database for storage, along with caching and pagination, can significantly improve performance and scalability for large datasets.