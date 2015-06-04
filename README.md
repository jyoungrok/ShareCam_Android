# ShareCam_Android

ShareCam android application

## Developed / Updated
#### 15. 06. 04
- sign up / sign in 
  - /signup/SignUpFragment - facebook sign up / sign in 
  - /signup/PhoeverifyFragment - verify phone number by sms or skip that
  - /signup/InputProfileFragment - update profile and user name and complete sigin up logic 
- camera




## Develop Plan
- contact
  - initialize contact    
   - upload contact information to parse with deleting existing contact rows in parse server
   - synchronize contact with sharecam friends ( it will be done in parse cloud code )
    - this function will be called once user sign up and user want to synchronize
  - synchronize contact
    - synchronize contact with sharecam friends without upload local contacts to parse server 
    - this function will be called once users start ShareActivity 
  
- share config



## Error

##### Take pictures consecutively


##### When camera activity show, push the home button 
```
Process: com.claude.sharecam, PID: 17249
    java.lang.InterruptedException
            at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.reportInterruptAfterWait(AbstractQueuedSynchronizer.java:1991)
            at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2025)
            at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:410)
            at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1035)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1097)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:588)
            at java.lang.Thread.run(Thread.java:820)
```




