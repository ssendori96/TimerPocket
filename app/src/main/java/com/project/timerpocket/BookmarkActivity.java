package com.project.timerpocket;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    private ArrayList<ListData> bookmark_data = new ArrayList<ListData>();
    private static BookmarkAdapter b_adapter = null;

//    static myBookmarkDBHelper myBookmarkHelper;
//    static SQLiteDatabase b_sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_layout);

        final ListView listView2;
        final ImageView imgBookmark_no2 = (ImageView) findViewById(R.id.imgBookmark_no2);
        final ImageView imgBookmark_only2 = (ImageView) findViewById(R.id.imgBookmark_only2);
        listView2 = findViewById(R.id.listView2);
        bookmark_data = getBookmarkList();

        b_adapter = new BookmarkAdapter(this, bookmark_data);
        listView2.setAdapter(b_adapter);



        // 즐겨찾기 버튼 터치시 동작(색칠된 별 -> 빈 별) -> 다시 본래 폴더 목록들이 나오도록
        imgBookmark_only2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBookmark_only2.setVisibility(View.GONE);
                imgBookmark_no2.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

        });


    }

    private ArrayList<ListData> getBookmarkList() {
        ArrayList<ListData> list = new ArrayList<>();
        MainActivity.sqlDB = MainActivity.myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = MainActivity.sqlDB.rawQuery("SELECT * FROM timerTBL WHERE bookmark = 1;", null);
        String[] timerName = new String[cursor.getCount()];
        int[] timerHour = new int[cursor.getCount()];
        int[] timerMinute = new int[cursor.getCount()];
        int[] timerSecond = new int[cursor.getCount()];
        int[] timerBookmarked = new int[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            timerName[i] = cursor.getString(0);
            timerHour[i] = cursor.getInt(1);
            timerMinute[i] = cursor.getInt(2);
            timerSecond[i] = cursor.getInt(3);
            timerBookmarked[i] = cursor.getInt(4);
            list.add(new ListData(timerName[i], timerHour[i], timerMinute[i], timerSecond[i], timerBookmarked[i]));
            i++;
        }
        cursor.close();
        MainActivity.sqlDB.close();
        return list;
    }



    static class BookmarkAdapter extends BaseAdapter {
        LayoutInflater inflater = null;
        ArrayList<ListData> b_items = new ArrayList<ListData>();
        Context context = null;

        public BookmarkAdapter(Context context, ArrayList<ListData> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.b_items = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return b_items.size();
        }

        @Override
        public Object getItem(int position) {
            return b_items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = null;

            if (convertView == null) {
                itemView = inflater.inflate(R.layout.layout_list, null);
            } else {
                itemView = convertView;
            }

            ListData data = b_items.get(position);

            TextView tvTimerName = itemView.findViewById(R.id.tvTimerName);
            ImageView imgBookmark_cancel = itemView.findViewById(R.id.imgBookmark_cancel);
            ImageView imgBookmark_select = itemView.findViewById(R.id.imgBookmark_select);

            tvTimerName.setText(data.getTitle());

            imgBookmark_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgBookmark_select.setVisibility(View.GONE);
                    imgBookmark_cancel.setVisibility(View.VISIBLE);
                    MainActivity.sqlDB = MainActivity.myHelper.getWritableDatabase();
                    MainActivity.sqlDB.execSQL("UPDATE timerTBL SET bookmark = 0 WHERE tTitle = '"+ data.getTitle() +"';");
                    b_adapter.notifyDataSetChanged();
                    MainActivity.sqlDB.close();
                }
            });


            return itemView;
        }
    }


//    public static class myBookmarkDBHelper extends SQLiteOpenHelper {
//        public myBookmarkDBHelper(Context context) {
//            super(context, "bookmarkDB", null, 1);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            db.execSQL("CREATE TABLE bookmarkTBL (tTitle CHAR(20) PRIMARY KEY, tHour INTEGER, tMinute INTEGER, tSecond INTEGER);");
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // oldVersion, newVersion에는 그냥 아무 숫자나 넣으면 됨
//            db.execSQL("DROP TABLE IF EXISTS bookmarkTBL");
//            onCreate(db);
//        }
//    }
}