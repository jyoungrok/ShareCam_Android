//package com.claude.sharecam;
//
//import android.annotation.TargetApi;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.support.v7.app.ActionBarActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//
//import com.claude.sharecam.parse.Test;
//import com.parse.FindCallback;
//import com.parse.GetDataCallback;
//import com.parse.ParseException;
//import com.parse.ParseFile;
//import com.parse.ParseQuery;
//import com.parse.SaveCallback;
//
//import java.io.ByteArrayOutputStream;
//import java.util.List;
//
//public class TestActivity extends ActionBarActivity {
//
//    Test testObject;
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test2);
//
//
//
////        Drawable d=getDrawable(R.mipmap.test); // the drawable (Captain Obvious, to the rescue!!!)
////        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
////        ByteArrayOutputStream stream = new ByteArrayOutputStream();
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
////        byte[] bitmapdata = stream.toByteArray();
////        final ParseFile file = new ParseFile("file.jpeg",bitmapdata);
////        final Test test=new Test();
////        test.put("file",file);
////        test.saveInBackground(new SaveCallback() {
////            @Override
////            public void done(com.parse.ParseException e) {
////                if(e==null)
////                {
////                    Log.d("jyr", "test data save success");
////                    ((EditText)findViewById(R.id.objectId)).setText(test.getObjectId());
//////            p
////                }
////                else
////                    Log.d("jyr","test data save fail");
////            }
////        });
//
//
//        ((Button)findViewById(R.id.findTest)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseQuery<Test> query=ParseQuery.getQuery(Test.class);
//                query.whereEqualTo("objectId",((EditText)findViewById(R.id.objectId)).getText().toString());
//                query.findInBackground(new FindCallback<Test>() {
//                    @Override
//                    public void done(List<Test> list, com.parse.ParseException e) {
//                        if(list.size()!=0 && e==null)
//                        {
//                            Log.d("jyr","find object success");
//                            testObject=list.get(0);
//                        }
//
//                        else
//                            Log.d("jyr","find object fail");
//                    }
//                });
//            }
//        });
//
//        (findViewById(R.id.getDataTest)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ParseFile file1=testObject.getParseFile("file");
//                file1.getDataInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] bytes, ParseException e) {
//
//                       setImageViewWithByteArray(((ImageView) findViewById(R.id.imageTest)), bytes);
//                    }
//                });
//            }
//        });
//
//    }
//
//    public  void setImageViewWithByteArray(ImageView view, byte[] data) {
//        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//        view.setImageBitmap(bitmap);
//    }
//
//
//}
