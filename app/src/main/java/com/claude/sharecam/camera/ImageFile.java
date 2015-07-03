package com.claude.sharecam.camera;

import java.io.Serializable;

/**
 * Created by Claude on 15. 6. 12..
 */
//사진 촬영 후 데이터 저장
class ImageFile implements Serializable{
    public  byte[] file;
    public String uriStr;

    public ImageFile(byte[] file,String uriStr)
    {
        this.file=file;
        this.uriStr=uriStr;
    }
}
