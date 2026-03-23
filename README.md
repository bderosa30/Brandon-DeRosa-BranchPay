# Github User Details Aggregator

### Frameworks

* Spring Boot/MVC - Used for quickly standing up a REST service, leveraging DI
* Spring Cache - Used to cache server responses for reduced latency and reduced risk of 429 errors. Cache has a total of 1000 keys for 
  an estimated footprint of 100MB. This is reasonable for a standalone app. Consider moving to a dedicated cache (Redis) as traffic grows.
* Lombok - For java POJO boilerplate code and builder patterns. Protobuf works great here as well,
  but IMO is not needed unless we plan on using gRPC as well. 
* Retrofit/Okhttp/Gson - Retrofit has a clean interface for declaring http(s) actions. Okhttp is the http client used
  with Retrofit, which is efficient and has many fault-tolerance tools built-in (not used in this project). 
  Gson is the supported JSON serializer/deseralizer with easy customization.
* Gradle - For build speed and flexibility. IMO a little more readable than maven. 
* Docker - Build and deploy via Docker for easy app standup. Lending itself well for future container orchestration
  like Kubernetes. 


### Thought Process
* REST controller(s) - minimal code where most of the work is delegated to action handlers.
* Simple Caching - To help alleviate 429 error from GitHub rate limits. 
    * However it doesn't prevent rate limit from occurring, just reduces the likelihood.
      We can utilize a simple interceptor to track if our app is in a state of being rate limited.
      Returning canned rejections in favor of hitting GitHub APIs and ferrying back a 429 error.
* Retrofit/Okhttp - Easy to standup and highly maintainable, while being performant and fault tolerant.
* Gson over Jackson - For its simplicity, though for increased performance or higher degree of customization
  we would prefer Jackson.

### Testing
* Integration tests are run and validated against supplied figures from the interview email.
* It is not a comprehensive test suite but the rate limiting interceptor and core data processing
  functions are tested against.
  * GSON serialization/deserialization is covered by integration tests, but if 
    time permitted I would implement tests for those.

### Deploy/Run
Download and install Docker, 
* build dockerfile using ```docker build -t brandonderosa .```
* run docker image using ```docker run -p 8090:8090 brandonderosa```