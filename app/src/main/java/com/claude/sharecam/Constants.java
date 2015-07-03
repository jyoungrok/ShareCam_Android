package com.claude.sharecam;

import android.hardware.Camera;

/**
 * Created by Claude on 15. 4. 9..
 */
public class Constants {

    public static final String PARSE_APPLICATION_ID="xBdXEe1dhrVpAtF0tbiM229Yua60qRNwlTqmiAz0";
    public static final String PARSE_CLIENT_KEY="Siw7sRqCzx1KHnqje3TX0pT2XkeJ1OFbZ5VuOKxJ";

    public static final String AWS_PROFILE_URL="https://s3-ap-northeast-1.amazonaws.com/sharecampublic/profile/";
    public static final String GOOGLE_SERVER_CLIENT_ID="573582998268-1ohonoi4k353u2415qoa2ffd7d3vhgg2.apps.googleusercontent.com";
    //request code
    public static final int ACTIVTY_IMAGE_CHOOSE_REQUEST=300;
    public static final int ACTIVITY_CAMERA_REQUEST=301;


    public static final int PROFILE_WIDTH=96;
    public static final int PROFILE_HEIGHT=96;

    public static final int BEFORE_REQUEST=0;
    public static final int DOING_REQUEST=1;
    public static final int AFTER_REQUEST=2;
    //server
//    public static final String SERVER_URL="https://52.68.142.41:1337";
    public static final String SERVER_URL="http://sharecam.parseapp.com/";

    //save folder name
    public static final String SD_CARD_FOLDER_NAME="sharecam";

    //preferance name
    public static final String PREF_NAME="sharecam";


    //preferance identifier

    //basci pref
    public static final String PREF_COOKIE="pref_cookie";
    //user pref
    public static final String PREF_USER_ID="pref_userId";
    public static final String PREF_USER_PROFILE_URL="pref_userProfileURL";
    public static final String PREF_USER_PHONE="pref_userPhone";
    public static final String PREF_USER_TYPE="pref_userType";
    public static final String PREF_USER_TYPE_ID="prefUserTypeId";
    public static final String PREF_USER_NAME="pref_userName";
    public static final String PREF_ACCESS_TOKEN="pref_accessToken";


    //federation pref
    public static final String PREF_FEDERATION_ACCESS_KEY="pref_federationAccessKey";
    public static final String PREF_FEDERATION_SECRET_ACCESS_KEY="pref_secretAccessKey";
    public static final String PREF_FEDERATION_SESSION_TOKEN="pref_sessionToken";
    public static final String PREF_FEDERATION_EXPIRATION="pref_expiration";

//    public static final String PREF_USER="pref_user";
    public static final String PREF_LOGIN="pref_login";
    //cameara pref
    public static final String PREF_FLASH="pref_flash";
    public static final String PREF_GRID="pref_grid";
    public static final String PREF_TIMER="pref_timer";
    public static final String PREF_ONE_TOUCH="pref_oneTouch";
    public static final String PREF_CAMERA_FRONT="pref_cameraType";
    public static final String PREF_CAMERA_RATIO="pref_cameraRatio";
    public static final String PREF_CAMERA_CONT="pref_cameraCont";
    public static final String PREF_CAMERA_MODE="pref_cameraMode";

    //share pref
    public static final String PREF_SHARE_PERSON="pref_sharePerson";
//    public static final String PREF_SHARE_CONTACT="pref_shareContact";
//    public static final String PREF_SHARE_FRIEND="pref_shareFriend";



    //preferance value

    //pref camera timer
    public static final int PREF_TIMER_UNSET=-1;
    public static final int PREF_TIMER_3=3;
    public static final int PREF_TIMER_5=5;
    public static final int PREF_TIMER_10=10;
    //pref camera cont
    public static final int PREF_RATIO_34=1;
    public static final int PREF_RATIO_11=2;
    public static final int PREF_CONT_1 =1;//3:4비율 1번 촬영
    public static final int PREF_CONT_2_VERTICAL =2;//3:4비율 2번 촬영
    public static final int PREF_CONT_3_VERTICAL =3;
    public static final int PREF_CONT_4 =4;
    //pref camera mode
    public static final int PREF_CAMERA_DIRECT_MODE=1;
    public static final int PREF_CAMERA_ONE_PICTURE_MODE=2;
    public static final int PREF_CAMERA_MULTIPLE_PICTURE_MODE=3;

//    public static final int PREF_CONT_11_1=5;//1:1비율 한번 촬영
//    public static final int PREF_CONT_11_2_vertical=6;//1:1비율 한번 촬영
//    public static final int PREF_CONT_11_2_horizontal=7;//1:1비율 한번 촬영
//    public static final int PREF_CONT_11_4=8;//1:1비율 한번 촬영

    //preferance default value
    public static final String PREF_SHARE_PERSON_DEFAULT="[]";
    public static final boolean PREF_LOGIN_DEFAULT=false;
    public static final int PREF_RATIO_DEFAULT=PREF_RATIO_11;
    public static final int PREF_CONT_DEFAULT= PREF_CONT_1;
    public static final String PREF_FLASH_DEFAULT= Camera.Parameters.FLASH_MODE_OFF;
    public static final Boolean PREF_GRID_DEFAULT=false;
    public static final int PREF_TIMER_DEFAULT=0;
    public static final Boolean PREF_ONE_TOUCH_DEFAULT=false;
    public static final boolean PREF_CAMERA_FRONT_DEFAULT=false;
    public static final int PREF_CAMERA_MODE_DEFAULT=PREF_CAMERA_ONE_PICTURE_MODE;

    //taking picutre
    public static final int CAPTURE_SECONDS=200;
    //blink seconds
    public static final int CAPTURE_BLINK_SECONDS=200;
    public static final int CONTINUOUS_CAPTRUE_SECONDS=2000;

    public static final int PROGRESS_VISIBLE=1;
    public static final int PROGRESS_INVISIBLE=0;


    //db constant

    //user type
    public static final int FACEBOOK_LOGIN=1;
    public static final int GOOGLE_LOGIN=2;

    //network state
    public static int NETWORK_WIFI            = 1;
    public static int NETWORK_MOBILE          = 2;
    public static int NETWORK_NOT_AVAILABLE   = 0;

    //request code
    public static final int REQUEST_FACEBOOK_LOGIN=0;
    public static final int REQUEST_CHOOSE_IMAGE=1;
    public static final int REQUEST_AVAIRY =2;



    //Loader
    public static final int PERSON_LOADER=1;



}
