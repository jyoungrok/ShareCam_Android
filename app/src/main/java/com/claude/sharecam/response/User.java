package com.claude.sharecam.response;

/**
 * Created by Claude on 15. 5. 12..
 */
public class User {

    public int id;
    // 1 - facebook
    // 2 - google
    public int type;
    public String type_id;
    public int completed;//가입 진행이 마무리 되었는지 여부
    public String phone;
    public String profileURL;
    public String name;

}
