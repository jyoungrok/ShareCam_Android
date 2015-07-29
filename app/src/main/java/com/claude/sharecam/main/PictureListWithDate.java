package com.claude.sharecam.main;

import com.claude.sharecam.parse.Picture;

import java.util.ArrayList;
import java.util.List;

/**
 *  앨범 사진의 item으로 사용 ( 사진 정렬 / 일반 정렬)
 */
public class PictureListWithDate{


    public static final int SORT_BY_PICTURE=0;
    public static final int SORT_BY_DATE=1;

    List<Picture> pictureList;//sort by picture 에서 사용
    ArrayList<PictureItem> pictureWithDateList;//sort by date에서 사용

    int dateNum;
    int type;
    public PictureListWithDate(){
        dateNum=0;
    }
    public PictureListWithDate(List<Picture> pictureList,int type)
    {
        dateNum=0;
        setArItem(pictureList);
        this.type=type;
    }

    //최신 날짜 부터 dscending으로 pictureList가 정렬되어 있다고 가정
    public void setArItem(List<Picture> pictureList)
    {
        this.pictureList=pictureList;
        pictureWithDateList =new ArrayList<PictureItem>();

        String lastDate=null;
        for(int i=0; i<pictureList.size(); i++)
        {
            //처음인 경우
            if(i==0)
            {
//                dateNum++;
                lastDate=pictureList.get(i).getCreatedAt_yyyyMMdd();
                pictureWithDateList.add(new PictureItem(PictureItem.DATE_TYPE, lastDate));
                pictureWithDateList.add(new PictureItem(PictureItem.PICTURE_TYPE, pictureList.get(i)));
            }
            else {
                //같은 날짜가 추가된 경우
               if(pictureList.get(i).getCreatedAt_yyyyMMdd().equals(lastDate))
               {
                   pictureWithDateList.add(new PictureItem(PictureItem.PICTURE_TYPE, pictureList.get(i)));
               }
               //새로운 날짜가 추가된 경우
                else {
//                   dateNum++;
                   lastDate=pictureList.get(i).getCreatedAt_yyyyMMdd();
                   pictureWithDateList.add(new PictureItem(PictureItem.DATE_TYPE, lastDate));
                   pictureWithDateList.add(new PictureItem(PictureItem.PICTURE_TYPE, pictureList.get(i)));
               }
            }
        }
    }

    public void addLastItems(List<Picture> newPictureList)
    {
        String lastDate=null;
        //마지막 날짜 찾기
        for(int i= pictureWithDateList.size()-1; i>=0; i--)
        {
            if(pictureWithDateList.get(i).getType()==PictureItem.DATE_TYPE)
            {
                lastDate= pictureWithDateList.get(i).getDate();
                break;
            }
        }

        //데이터가 없는 상태에서 데이터를 추가한 경우
        if(lastDate==null) {
            setArItem(newPictureList);
            return;
        }
        //아이템들 추가
        pictureList.addAll(newPictureList);

        for(int i=0; i<newPictureList.size(); i++)
        {
            //같은 날짜가 추가된 경우
            if(newPictureList.get(i).getCreatedAt_yyyyMMdd().equals(lastDate))
            {
                pictureWithDateList.add(new PictureItem(PictureItem.PICTURE_TYPE, newPictureList.get(i)));
            }
            //새로운 날짜가 추가된 경우
            else {
//                dateNum++;
                lastDate=newPictureList.get(i).getCreatedAt_yyyyMMdd();
                pictureWithDateList.add(new PictureItem(PictureItem.DATE_TYPE, lastDate));
                pictureWithDateList.add(new PictureItem(PictureItem.PICTURE_TYPE, newPictureList.get(i)));
            }
        }
    }


    public int getPictureIndexFromPictureWithDateItem(int position)
    {
        int pictureIndex=position;
        for(int i=0; i<pictureWithDateList.size(); i++)
        {
            if(i==position)
                break;
            if(pictureWithDateList.get(i).getType()==PictureItem.DATE_TYPE)
                pictureIndex--;
        }

        return pictureIndex;
    }

    //sortByDate를 위한 Item으로 사용
    public class PictureItem{
        public static final int PICTURE_TYPE=0;
        public static final int DATE_TYPE=1;
        int type; //사진 / 날짜
        String date;// type이 date인 경우
        Picture picture;

        public int getType()
        {
            return type;
        }
        public String getDate(){return date;}
        public Picture getPicture(){return picture;}

        public PictureItem()
        {

        }
        public PictureItem(int type, String date)
        {
            this.type=type;
            this.date=date;
        }
        public PictureItem(int type,Picture picture)
        {
            this.type=type;
            this.picture=picture;
        }
    }
}
