package com.claude.sharecam.parse;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseFile;

import java.io.Serializable;

/**
 * Created by Claude on 15. 8. 13..
 */
public class SerializableParseFile implements Serializable{
    ParseFile parseFile;
    public SerializableParseFile()
    {

    }
    public SerializableParseFile(ParseFile parseFile)
    {
        this.parseFile=parseFile;
    }

    public ParseFile getParseFile()
    {
        return parseFile;
    }


}
