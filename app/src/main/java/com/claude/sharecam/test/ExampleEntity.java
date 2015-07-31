//package com.claude.sharecam.test;
//
//import com.cardiomood.android.sync.annotations.ParseClass;
//import com.cardiomood.android.sync.annotations.ParseField;
//import com.cardiomood.android.sync.ormlite.SyncEntity;
//
//import com.j256.ormlite.field.DataType;
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
//
//import java.io.Serializable;
//import java.util.Date;
//
//// This class will be mapped to a Parse class named "Example"
//@ParseClass(name = "Example")
//@DatabaseTable(tableName = "examples", daoClass = ExampleDAO.class)
//public class ExampleEntity extends SyncEntity implements Serializable {
//
//    /** Local ID field of this entity */
//    @DatabaseField(columnName = "_id", generatedId = true)
//    private Long id;
//
//    // mapped to the "name" field of ParseObject
//    @ParseField(name = "name")
//    @DatabaseField(columnName = "name")
//    private String exampleName;
//
//
//
//    // mapped to the "lastViewDate" field of ParseObject
//    @DatabaseField(columnName = "last_view_date", dataType = DataType.DATE_LONG)
//    @ParseField(name = "lastViewDate")
//    private Date lastViewed;
//
//    public ExampleEntity() {
//        // public constructor with no arguments required
//    }
//
//    // getters and setters here
//
//}