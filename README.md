# ShareCam_Android

## Local Database Class

### Album
| field | type | description |
| ------------- | ------------- | ----------- |
| type | int | 0 - 보낸 사진 앨범 / 1 - 받은 사진 앨범범 |
| receiverPhoneList | JSONArray<String> | 보낸 사진 앨범에서 공유 연락처 목록 |
| receiverPhoneListSize | int | receiverPhoneList의 size |
| lastUpdatedAt | Date | 해당 앨범 마지막 사진의 updatedAt | 
| picture | Picture | 대문 사진(마지막 사진) | 
| isNew | boolean | 안 읽은 사진이 있는 경우 - true | 


### Contact 

- 로컬 데이터들(서버에는 저장되지 않고 로컬에만 저장되는 데이터)만 표시 

| field | type | description |
| ------------- | ------------- | ----------- |
| contactName | String | 연락처 이름 |
| contactPhotoUri | String | 연락처 사진 URI / 없을 경우 - NULL |
