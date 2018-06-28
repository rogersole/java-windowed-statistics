# Last minute statistics

The project tries to solve the problem about retrieving the statistics of api usage in the fewer possible time.

## Endpoints

- **`POST /transactions`**

 Called every time that a transaction occurs. Body:

 ```json
{
  "amount": 12.34,           # double
  "timestamp": 1234567890123	# long
}
```
 - `amount`: transaction amount
 - `timestamp`: transaction time in epoch in millis in UTC time zone

 **Response:** Empty body with status code  
  - `201`: when successfully admitted   
  - `204`: if transaction is older than 60 seconds
    

- **`GET  /statistics`**

 Returns the statistics based on the transactiosn which happened in the last 60 seconds.
 
 **Response:**
 
 ```json
 {
 	"sum": 1000,              # double
 	"avg": 100,               # double
 	"max": 200,               # double
 	"min": 50,                # double
 	"count": 10               # long
 }
 ```
  - `sum`: total sum of transactions values in the last 60 secs
  - `avg`: average amount of transactions values in the last 60 secs
  - `max`: highest transaction value in the last 60 secs
  - `min`: lowest transaction value in the last 60 secs
  - `count`: total number of transactions happened in the last 60 secs. 
  

## Configuration file 

In the `application.yml` file there are defined the values for the Spring solution.

Application runs on port `8080` and endpoints are under the `/` path.

```yml
server:
  servlet:
    contextPath: /
  port: 8080
```

The `window.seconds` property defines how big we want the window to keep track of data.
That defines the maximum space for the data structures used.

```yml
window:
  seconds: 60
```


## Build and execution

- Execute:

 ```bash
 $> ./run_project.sh
 ```

The script compiles the project, passes the tests, generate the build and create a distribution.
After all the previous steps are done successfully, the server is started.

Alternatively, `./gradlew clean build run` can be executed from the command line on the project root.


## Decisions taken

### **`StatisticsService.java`**

- Used a queue to store all the `POST` endpoint calls. That allow us to send the response to the client as soon as possible.  
- Created a queue consumer (`TransactionConsumer`) thread to read queue elements and update the statistics data structure.  
- Created a timed (`IntervalStatsCleaner`) thread that's been executed every second to recalculate the current statistics and purge obsolete elements (done while recalculating).  
- There are two approaches for the `getStatistics` results:
 - strongly consistent and almost O(1): used solution. statistics result is calculated when asked (also being updated on every interval and consumer call)
 - eventually consistend and strict O(1): commented solution. since statistics are calculated on every queue element consumed or every second when cleaner process passes, returing the already calculated value would let us have a O(1) but with not strong consistent.


### **`Statistics.java`**

- Contains the data structures that are being constantly updated:
 -  Stores the maximum value data structure (`WindowMax`)
 -  Stores the minimum value data structure (`WindowMax`)
 -  Stores the sum value data structure (`WindowSum`)


### **`WindowSum.java`**

- Handles the transactions in a priority queue, ordered by timestamp. That allows us to purge the first elements easilly when time window is exhausted. 
- The `sum` value is updated on each queue addition and substracion (purge).

### **`WindowMax.java`**

- Handles the transactions in a TreeSet ordered by timestamp.
- Only values that can be maximum value are inserted (values that have another value more in the future than they are and with a higer value, are not even inserted in the TreeSet).
- The same class is used to handle minimum and maximum data. When handling minimum, the comparator method uses the amount in descending order, when handling maximum using it in ascending order. 


