package com.claude.sharecam.api;

import android.content.Context;
import android.util.Log;

import com.claude.sharecam.R;
import com.claude.sharecam.response.RestError;
import com.google.gson.JsonObject;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Claude on 15. 5. 7..
 */
public class ErrorCode {

    public static final String TAG="ErrorCode";

    public static final int INVALID_PARAMETER=10;
    public static final int PERMISSION_DENIED=11;
    public static final int PERMISSION_DENIED_NOT_MY_INF=12;

    //toast error
    public static final int INVALID_PHONE_NUMBER=200;
    public static final int VERIFICATION_NUMBER_NOT_MATCHED=201;
    public static final int PHONE_ALREADY_EXIST=202;
    public static final int CONTINUOS_ATTEMPT_NOT_ALLOWED=203;
//    public static final int FACEBOOK_INVALID_ACCESS_TOKEN=202;

//    public static int getToastMessageId(Context context,RestError restError)
//    {
//        if(restError!=null) {
//
//            Log.e("server", restError.strMessage!=null ?restError.strMessage:" str message empty" );
//            switch (restError.code) {
//                case INVALID_PHONE_NUMBER:
//                    return R.string.invalid_phone;
////                return context.getResources().getString(R.string.invalid_phone);
//                case VERIFICATION_NUMBER_NOT_MATCHED:
//                    return R.string.verification_number_not_matched;
////                return context.getResources().getString(R.string.verification_number_not_matched);
////                case FACEBOOK_INVALID_ACCESS_TOKEN:
////                    return R.string.facebook_invalid_access_token;
//                case CONTINUOS_ATTEMPT_NOT_ALLOWED:
//                    return R.string.continuous_attempts_not_allowed;
//                default:
//                    return R.string.unknown_error_code;
////                return context.getResources().getString(R.string.unknown_error_code);
//            }
//        }
//        else
//            return R.string.unknown_error_code;
//    }

    public static int getToastMessageId(ParseException e)
    {
        if(e.getCode()==209)
        {
            return R.string.invalid_session_token;
        }
        Log.d(TAG,e.getMessage());
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(e.getMessage());

            switch(jsonObject.getInt("code"))
            {
                case INVALID_PHONE_NUMBER:
                    return R.string.invalid_phone;
//                return context.getResources().getString(R.string.invalid_phone);
                case VERIFICATION_NUMBER_NOT_MATCHED:
                    return R.string.verification_number_not_matched;
//                return context.getResources().getString(R.string.verification_number_not_matched);
                case PHONE_ALREADY_EXIST:
                    return R.string.phone_already_exist;
                case CONTINUOS_ATTEMPT_NOT_ALLOWED:
                    return R.string.continuous_attempts_not_allowed;
                default:
                    return R.string.unknown_error_code;
            }
        }
        catch (JSONException error) {
            error.printStackTrace();
            return R.string.unknown_error_code;
        }
        catch( NullPointerException error){
            error.printStackTrace();
            return R.string.unknown_error_code;
        }
    }
}
