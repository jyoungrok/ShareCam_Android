package com.claude.sharecam.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.claude.sharecam.Util;
import com.claude.sharecam.parse.*;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claude on 15. 7. 12..
 */
public class DBHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME = "sharecam.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * The data access object used to interact with the Sqlite database to do C.R.U.D operations.
     */
    private Dao<UploadingPicture, Long> uploadingPictureDao;
    private Dao<IndividualItem, Long> individualItemDao;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {

            /**
             * creates the Table
             */
            TableUtils.createTable(connectionSource, UploadingPicture.class);
            TableUtils.createTable(connectionSource,IndividualItem.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, UploadingPicture.class, false);
            onCreate(sqLiteDatabase, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns an instance of the data access object
     * @return
     * @throws SQLException
     */
    public Dao<UploadingPicture, Long> getUploadingPictureDao() throws SQLException {
        if(uploadingPictureDao == null) {
            uploadingPictureDao = getDao(UploadingPicture.class);
        }
        return uploadingPictureDao;
    }

    public Dao<IndividualItem, Long> getIndividualItemDao() throws SQLException {
        if(individualItemDao == null) {
            individualItemDao = getDao(IndividualItem.class);
        }
        return individualItemDao;
    }


    //공유 설정 대상 불러오기
    public static List<IndividualItem> getSharePerson(Context context) throws SQLException {

        return ((Util)context.getApplicationContext()).dbHelper.getIndividualItemDao().queryBuilder().where().eq("isShortCut",false).query();
    }

//

//    //state가 finished인 uploading picture응 제외하고 모두 불러옴
//    public List<UploadingPicture> getUP_without_finished(Context context)
//    {
//        QueryBuilder<UploadingPicture,Long> queryBuilder=null;
//        try {
//            queryBuilder=((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().queryBuilder();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if(queryBuilder!=null)
//        {
//            queryBuilder.where().ne("state",)
//        }
//
//        return null;
//
//    }
}
