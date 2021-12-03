package com.project.timerpocket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView = null;
    private Button btnNewTimer = null;

    private ArrayList<ListData> listData = new ArrayList<ListData>();
    static ListAdapter adapter = null;


    static myDBHelper myHelper;
    static SQLiteDatabase sqlDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);
        btnNewTimer = findViewById(R.id.btnNewTimer);
        myHelper = new myDBHelper(this);
        listData = getTimerList();


        adapter = new ListAdapter(this, listData);
        listView.setAdapter(adapter);

        final ImageView imgBookmark_no = (ImageView) findViewById(R.id.imgBookmark_no);
        final ImageView imgBookmark_only = (ImageView) findViewById(R.id.imgBookmark_only);

        // 즐겨찾기 버튼 터치시 동작(빈 별 -> 색칠된 별) -> 이후에 즐겨찾기한 타이머들만 나오도록 기능 추가
        imgBookmark_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBookmark_no.setVisibility(View.GONE);
                imgBookmark_only.setVisibility(View.VISIBLE);
                Intent intent2 = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent2);
            }

        });

        // 즐겨찾기 버튼 터치시 동작(색칠된 별 -> 빈 별) -> 다시 본래 폴더 목록들이 나오도록
//        imgBookmark_only.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgBookmark_only.setVisibility(View.GONE);
//                imgBookmark_no.setVisibility(View.VISIBLE);
//            }
//
//        });



        btnNewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTimerActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                    intent.putExtra("title", listData.get(position).getTitle());
                    intent.putExtra("hour", listData.get(position).getHour());
                    intent.putExtra("minute", listData.get(position).getMinute());
                    intent.putExtra("second", listData.get(position).getSecond());
                    startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new ListViewItemLongClickListener());




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String timerTitle = data.getStringExtra("title");
                int hour = data.getIntExtra("hour",0);
                int minute = data.getIntExtra("minute",0);
                int second = data.getIntExtra("second",0);
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO timerTBL VALUES ('"+ data.getStringExtra("title") +"', '"+ hour +"', '"+ minute +"', '"+ second +"', 0);");

                listData.add(new ListData(timerTitle, hour, minute, second, 0));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<ListData> getTimerList() {
        ArrayList<ListData> list = new ArrayList<>();
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM timerTBL;", null);
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
        sqlDB.close();
        return list;
    }

//    class UpdateBookmarkClickListener implements AdapterView.OnClickListener {
//        @Override
//        public void onClick(View v) {
//
//        }
//    }

    class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setTitle("삭제하시겠습니까?");
            alert.setNegativeButton("아니오", null);
            alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String keyName = listData.get(position).getTitle();
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("DELETE FROM timerTBL WHERE tTitle = '"+ keyName +"';");
                    listData.remove(position);
                    adapter.notifyDataSetChanged();
                    sqlDB.close();
                    Toast.makeText(getApplicationContext(), "삭제했습니다", Toast.LENGTH_SHORT).show();
                }
            });
            alert.show();
            return true;

        }
    }


    static class ListAdapter extends BaseAdapter {
        LayoutInflater inflater = null;
        ArrayList<ListData> items = new ArrayList<ListData>();
        Context context = null;


        public ListAdapter(Context context, ArrayList<ListData> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.items = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void dataChange(){
            adapter.notifyDataSetChanged();
        }

        public void cancelBookmark(int i) {
            ListData data = items.get(i);
            sqlDB = myHelper.getWritableDatabase();
            sqlDB.execSQL("UPDATE timerTBL SET bookmark = 0 WHERE tTitle = '"+ data.getTitle() +"';");
            dataChange();
            sqlDB.close();
        }

        public void selectBookmark(int i) {
            ListData data = items.get(i);
            sqlDB = myHelper.getWritableDatabase();
            sqlDB.execSQL("UPDATE timerTBL SET bookmark = 1 WHERE tTitle = '"+ data.getTitle() +"';");
            dataChange();
            sqlDB.close();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = null;

            if (view == null) {
                itemView = inflater.inflate(R.layout.layout_list, null);
            } else {
                itemView = view;
            }

            ListData data = items.get(i);

            TextView tvTimerName = itemView.findViewById(R.id.tvTimerName);
            ImageView imgBookmark_cancel = itemView.findViewById(R.id.imgBookmark_cancel);
            ImageView imgBookmark_select = itemView.findViewById(R.id.imgBookmark_select);

            tvTimerName.setText(data.getTitle());



            imgBookmark_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgBookmark_cancel.setVisibility(View.GONE);
                    imgBookmark_select.setVisibility(View.VISIBLE);
                    selectBookmark(i);
                }
            });

            imgBookmark_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgBookmark_select.setVisibility(View.GONE);
                    imgBookmark_cancel.setVisibility(View.VISIBLE);
                    cancelBookmark(i);
                }
            });

            if(data.getBookmark() == 0) {
                imgBookmark_select.setVisibility(View.GONE);
                imgBookmark_cancel.setVisibility(View.VISIBLE);
            } else {
                imgBookmark_cancel.setVisibility(View.GONE);
                imgBookmark_select.setVisibility(View.VISIBLE);
            }


            return itemView;
        }


    }



    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "timerDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // bookmark = 0이면 false, 1이면 true
            db.execSQL("CREATE TABLE timerTBL (tTitle CHAR(20) PRIMARY KEY, tHour INTEGER, tMinute INTEGER, tSecond INTEGER, bookmark INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // oldVersion, newVersion에는 그냥 아무 숫자나 넣으면 됨
            db.execSQL("DROP TABLE IF EXISTS timerTBL");
            onCreate(db);
        }
    }



}