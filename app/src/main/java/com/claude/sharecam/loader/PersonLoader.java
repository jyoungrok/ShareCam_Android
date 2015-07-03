package com.claude.sharecam.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.claude.sharecam.Util;
import com.claude.sharecam.parse.Friend;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SharePerson;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.share.IndividualItem;
import com.claude.sharecam.share.IndividualItemList;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claude on 15. 6. 10..
 */
public class PersonLoader extends AsyncTaskLoader<IndividualItemList> {

    Context context;
    public PersonLoader(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public IndividualItemList loadInBackground() {

        Log.d("jyr","person loader load in background");

        IndividualItemList individualItemList =new IndividualItemList();

        //연락처 데이터 불러옴
        individualItemList.contactItems = Util.getContactList(context);

        //쉐어캠 친구 데이터 불러옴
        List<Friend> friendList= ParseAPI.getFriends_Local(context);

        //추가된 연락처 목록 불러옴
        List<SharePerson> sharePersonList =ParseAPI.getSharePerson_Local(context);


        individualItemList.addedItems=new ArrayList<IndividualItem>();
        individualItemList.friendItems=new ArrayList<IndividualItem>();


        //추가된 item setting
//        for(int i=0; i<shareContactList.size(); i++)
//        {
//            ShareContact temp=shareContactList.get(i);
//            personItemList.addedItems.add(new PersonItem(temp.getName(),temp.getNationalPhone(),temp.getProfileUri(),PersonItem.CONTACT));
//        }



        for(int i=0; i<friendList.size(); i++)
        {
            try {
                Log.d("jyr","profile size="+friendList.get(i).getFriendUser().getParseFile("profile").getData().length);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            User friendUser= (User) friendList.get(i).getFriendUser();
            individualItemList.friendItems.add(new IndividualItem(friendUser.getObjectId(),friendUser.getUsername(),friendUser.getProfileFile(),Util.convertToNationalNumber(context, friendUser.getPhone())));
        }



        for(int i=0; i< individualItemList.contactItems.size(); i++)
        {
            //연락처 데이터 들 중 쉐어캠 친구인 것이 있는지 확인
            for(int j=0; j< individualItemList.friendItems.size(); j++) {
                //연락처 중 쉐어캠 친구인 경우
                if(individualItemList.friendItems.get(j).phoneNumber.equals(individualItemList.contactItems.get(i).phoneNumber)) {

                    //제거
                    individualItemList.contactItems.remove(i);
                    i--;
                    break;
//                    individualItemList.contactItems.get(i).isFriend = true;
//                    individualItemList.contactItems.get(i).friendIndex=j;
////                    personItems.get(i).contactProfile=friendItems.get(j).contactProfile;
//                    individualItemList.contactItems.get(i).personName= individualItemList.contactItems.get(i).personName+"("+ individualItemList.friendItems.get(j).personName+")";
//                    break;
                }
            }
            //이미 추가된 연락처가 있는지 확인
            for(int j=0; j< sharePersonList.size(); j++)
            {
                //연락처
                if(!sharePersonList.get(j).getIsFriend() && individualItemList.contactItems.get(i).MODE== IndividualItem.CONTACT && individualItemList.contactItems.get(i).phoneNumber.equals(sharePersonList.get(j).getNationalPhone()))
                {
                    individualItemList.contactItems.get(i).added=true;
                    individualItemList.addedItems.add(individualItemList.contactItems.get(i));
                    break;
                }
            }
        }



        for(int i=0; i< individualItemList.friendItems.size(); i++)
        {
            //이미 추가된 쉐어캠 친구가 있는지 있는지 확인
            for(int j=0; j< sharePersonList.size(); j++) {
                //쉐어캠 친구
                if (sharePersonList.get(j).getIsFriend() && individualItemList.friendItems.get(i).MODE == IndividualItem.FRIEND && individualItemList.friendItems.get(i).objectId.equals(sharePersonList.get(j).getFriendObjectId())) {
                    individualItemList.friendItems.get(i).added=true;
                    individualItemList.addedItems.add(individualItemList.friendItems.get(i));
                }
            }

        }



        return individualItemList;
    }
    @Override
    protected void onStartLoading() {
        Log.d("jyr","start loading");
        forceLoad();
    }
}
