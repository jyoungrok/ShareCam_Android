# ShareCam_Android

ShareCam android application

## Developed / Updated
#### 15. 06. 04
- sign up / sign in 
  - /signup/SignUpFragment - facebook sign up / sign in 
  - /signup/PhoeverifyFragment - verify phone number by sms or skip that
  - /signup/InputProfileFragment - update profile and user name and complete sigin up logic 
- camera

#### 15. 06. 05



## Develop Plan
- contact
  - initialize contact    
    - upload contact information to parse with deleting existing contact rows in parse server
      - The form of phone number,which is saved with country,should be like "+821033575960"
      - 821033579560 -> +821033579560
      - 01033579560 -> +821033579560
    - synchronize contact with sharecam friends ( it will be done in parse cloud code )
    - this function will be called once user sign up and user want to synchronize
  - synchronize contact
    - synchronize contact with sharecam friends without upload local contacts to parse server 
    - this function will be called once users start ShareActivity 
  
- share config




