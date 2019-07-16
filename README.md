# KRY code assignment

One of our developers built a simple service poller.
The service consists of a backend service written in Vert.x (https://vertx.io/) that keeps a list of services (defined by a URL), and periodically does a HTTP GET to each and saves the response ("OK" or "FAIL").

Unfortunately, the original developer din't finish the job, and it's now up to you to complete the thing.
Some of the issues are critical, and absolutely need to be fixed for this assignment to be considered complete.
There is also a wishlist of features in two separate tracks - if you have time left, please choose *one* of the tracks and complete as many of those issues as you can.

Critical issues (required to complete the assignment):

- Whenever the server is restarted, any added services disappear
- There's no way to delete individual services
- We want to be able to name services and remember when they were added
- The HTTP poller is not implemented

Frontend/Web track:
- We want full create/update/delete functionality for services
- The results from the poller are not automatically shown to the user (you have to reload the page to see results)
- We want to have informative and nice looking animations on add/remove services

Backend track
- Simultaneous writes sometimes causes strange behavior
- Protect the poller from misbehaving services (for example answering really slowly)
- Service URL's are not validated in any way ("sdgf" is probably not a valid service)
- A user (with a different cookie/local storage) should not see the services added by another user

Spend maximum four hours working on this assignment - make sure to finish the issues you start.

Put the code in a git repo on GitHub and send us the link (niklas.holmqvist@kry.se) when you are done.

Good luck!

# Building
We recommend using IntelliJ as it's what we use day to day at the KRY office.
In intelliJ, choose
```
New -> New from existing sources -> Import project from external model -> Gradle -> select "use gradle wrapper configuration"
```

You can also run gradle directly from the command line:
```
./gradlew clean run
```

# Progress
####since the time is limited, I managed to fix the critical issues but here are some ideas on how to go forward with the backend wish-list:

_**- Simultaneous writes sometimes causes strange behavior:**_
    A suggestion is to use Database Transactions or Resource Locking to prevent strange behaviours. 
    
_**- Protect the poller from misbehaving services (for example answering really slowly):**_
    We can set connection timeout and consider polling is failed for that service if timeout occurs.
    
_**-Service URL's are not validated in any way ("sdgf" is probably not a valid service):**_
    Use regex validation or a third party library for url validation.

_**- A user (with a different cookie/local storage) should not see the services added by another user:**_
    We can use the session ID to hash data before sending back to our front-end client and it can
    be decrypted in front end. Or we can save the session ID in our database and filter data with
    that session before serving it to the front end.(only optimal for limited time of usage)
     
**Note:**
    I mainly focused on getting the functionality fixed during the limited time
    available and so I opted in some cases for solutions that can be considered not the most optimal
    ones but yet they are good enough to do the job. And given the fact it is the first time for me to 
    have hands on Vert.x I had some issues that held me back a little bit but it was fun to resolve them. 
    Having said that, if I had more time, these are some things I would do in a better way:
    
        - use better database migrations frameworks like FlywayDB or similar.
        - implement better Exception Handlers.
        - implement a data layer and business models to abstract our data models.
        - abstract the server router into different components (authentication layer, validation layer etc...)
        - write unit tests to mock some services and to simulate handling incoming http requests.
        - clean up and refactoring.
     
