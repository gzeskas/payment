**Money transfer service**

To run integration test run gradle task in `./gradlew integrationTest`  
integration test will assure that application is working correctly 

To build the project run `./gradlew clean shadowJar`
Fat jar will be located in: `build/libs/` directory to run simply type: `java -jar build/libs/payment-0.0.1-SNAPSHOT-all.jar`

Web server will start on 8080 port and API will be accessible by URL: `/api/v1/transfers/` 
Request body should be in JSON format as example: 
```
{
    "accountFrom": 1, 
    "accountTo": 2, 
    "amount": 10
}
```

Build status
[![CircleCI](https://circleci.com/gh/gzeskas/payment.svg?style=svg)](https://circleci.com/gh/gzeskas/payment)