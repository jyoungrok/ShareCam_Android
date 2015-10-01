package com.claude.sharecam.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claude on 15. 8. 22..
 */
public class ShareItem implements Serializable{
//    public List<String> shareUserList;
    public List<String> sharePhoneList;
//    public List<String> shareGroupList;
    public ShareItem(List<String> sharePhoneList)
    {
        this.sharePhoneList=sharePhoneList;
    }
//    public ShareItem( List<String> shareUserList,List<String> sharePhoneList)
//    {
//        this.shareUserList=shareUserList;
//        this.sharePhoneList=sharePhoneList;
//        shareGroupList=new ArrayList<String>();
//    }

    //공유 설정 대상이 있는지 없는지 여부
    public boolean isEmpty()
    {
//        if(sharePhoneList.size()==0 && shareUserList.size()==0 && shareGroupList.size()==0)
//        {
//            return true;
//        }
        if(sharePhoneList.size()==0)
        {
            return  true;
        }
        return false;
    }
}
