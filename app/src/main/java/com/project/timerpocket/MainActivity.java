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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView = null;
    private Button btnNewTimer = null;
    private View i_view;

    private ArrayList<ListData> listData = new ArrayList<ListData>();
    private ListAdapter adapter = null;

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);
        btnNewTimer = findViewById(R.id.btnNewTimer);
        myHelper = new myDBHelper(this);
        listData = getTimerList();
//        i_view = getLayoutInflater().inflate(R.layout.layout_list, null, false);

        adapter = new ListAdapter(this, listData);
        listView.setAdapter(adapter);



        btnNewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTimerActivity.class);
                startActivityForResult(intent, 100);
            }
        });


//        Button btnTimer = i_view.findViewById(R.id.btnTimer);
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
//                dlg.setTitle("삭제하시겠습니까?");
//                dlg.setNegativeButton("아니오", null);
//                dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String keyName = listData.get(which).getTitle();
//                        sqlDB = myHelper.getWritableDatabase();
//                        sqlDB.execSQL("DELETE FROM timerTBL WHERE tTitle = '"+ keyName +"';");
//                        sqlDB.close();
//                        listData.remove(which);
//                        Toast.makeText(getApplicationContext(), "삭제했습니다", Toast.LENGTH_SHORT).show();
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                return true;
//            }
//        });
//        listData.(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
//                dlg.setTitle("삭제하시겠습니까?");
//                dlg.setNegativeButton("아니오", null);
//                dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String keyName = listData.get(which).getTitle();
//                        sqlDB = myHelper.getWritableDatabase();
//                        sqlDB.execSQL("DELETE FROM timerTBL WHERE tTitle = '"+ keyName +"';");
//                        sqlDB.close();
//                        listData.remove(which);
//                        Toast.makeText(getApplicationContext(), "삭제했습니다", Toast.LENGTH_SHORT).show();
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                return true;
//            }
//        });




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
                sqlDB.execSQL("INSERT INTO timerTBL VALUES ('"+ data.getStringExtra("title") +"', '"+ hour +"', '"+ minute +"', '"+ second +"');");

                listData.add(new ListData(timerTitle, hour, minute, second));
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
        int i = 0;
        while(cursor.moveToNext()){
            timerName[i] = cursor.getString(0);
            timerHour[i] = cursor.getInt(1);
            timerMinute[i] = cursor.getInt(2);
            timerSecond[i] = cursor.getInt(3);
            list.add(new ListData(timerName[i], timerHour[i], timerMinute[i], timerSecond[i]));
            i++;
        }
        cursor.close();
        sqlDB.close();
        return list;
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = null;
            if (view == null) {
                itemView = inflater.inflate(R.layout.layout_list, null);
            } else {
                itemView = view;
            }

            ListData data = items.get(i);

            Button btnTimer = itemView.findViewById(R.id.btnTimer);
            btnTimer.setText(data.getTitle());
            btnTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TimerActivity.class);
                    intent.putExtra("title", data.getTitle());
                    intent.putExtra("hour", data.getHour());
                    intent.putExtra("minute", data.getMinute());
                    intent.putExtra("second", data.getSecond());
                    context.startActivity(intent);
                }
            });




            btnTimer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(context.getApplicationContext(), data.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


            return itemView;
        }


    }



    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "timerDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE timerTBL (tTitle CHAR(20) PRIMARY KEY, tHour INTEGER, tMinute INTEGER, tSecond INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // oldVersion, newVersion에는 그냥 아무 숫자나 넣으면 됨
            db.execSQL("DROP TABLE IF EXISTS timerTBL");
            onCreate(db);
        }
    }



}