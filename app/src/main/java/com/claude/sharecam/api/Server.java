package com.claude.sharecam.api;


import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.response.Empty;


import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by JungHoon on 15. 3. 22..
 */
public interface Server {
/*
    //로그인
    @POST("/user/login")
    void login(@Query("type") int type, @Query("type_id")String type_id,@Query("access_token")String access_token,RestCallBack<User> cb);

    //인증번호 받아오기
    @GET("/user/sms")
    void getPhoneVerifyMessage(@Query("phone") String phone, RestCallBack<Empty> cb);

    //인증번호 확인
    @PUT("/user/sms/confirm")
    void confirmVerificationNumber(@Query("vNumber") String vNumber,RestCallBack<User> cb);

    //회원 이름 및 profile URL등록
    @PUT("/user/{id}")
    void updateUser(@Path("id")int id,@Query("name")String name,@Query("profileURL")String profileURL,@Query("completed")int completed,RestCallBack<User> cb);



    //federation token 얻어오기
    @GET("/user/federation")
    void getFederation(RestCallBack<Federation> cb);
*/


    @GET("/"+ ParseAPI.SM_PHONE_VERIFY)
    void getPhoneVerifyMessage(@Query("phone") String phone, RestCallBack<Empty> cb);

}