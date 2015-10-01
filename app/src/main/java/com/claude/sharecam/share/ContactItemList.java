package com.claude.sharecam.share;

import com.claude.sharecam.parse.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claude on 15. 8. 19..
 */
public class ContactItemList {
    public List<Contact> addedItems;

    public List<Contact> contactItems;
    public List<Contact> friendItems;

    public ContactItemList()
    {

    }

    public boolean isAdded(Contact individualItem) {
        for(int i=0; i<addedItems.size(); i++)
        {
            if(addedItems.get(i).getRecordId()==individualItem.getRecordId())
            {
                return true;
            }
        }
        return false;
    }
}
