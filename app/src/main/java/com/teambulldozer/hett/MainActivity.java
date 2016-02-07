package com.teambulldozer.hett;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HETT";

    private DatabaseHelper myDb;
    private ListView list_view1;
    private EditText memoInput;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(getApplicationContext()); // going to call the constructor
        list_view1 = (ListView) findViewById(R.id.list_view1);

        memoInput = (EditText) findViewById(R.id.memoInput);
        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            //등록버튼 클릭 시.
            @Override
            public void onClick(View v) {
                boolean isInserted = myDb.insertData(memoInput.getText().toString()); // meno내용을 전달.
                if (isInserted) { // insertData의 return값이 ture이면.
                    Cursor res = myDb.getAllData(); // DB의 ALL data를 받아와서.
                    if (res.getCount() == 0) { // there is no data available for us.
                        Toast.makeText(MainActivity.this, "No data available", Toast.LENGTH_LONG).show();
                        return;
                    }
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) { //모든 data를 input.
                        buffer.append(res.getString(1) + "\n");
                    }
                    //showMessage(buffer.toString());
                    populateListView();
                } else
                    Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
            }
        });

        populateListView();
        list_view1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                cursor.moveToPosition(position);
                int rowId = (cursor.getPosition() + 1); // sqlite와 sync를 맞춰줘야함.
                Toast.makeText(MainActivity.this, "Item Clicked " + rowId, Toast.LENGTH_LONG).show();
                if (deleteRow(Integer.toString(rowId)))
                    Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "Data Not Deleted", Toast.LENGTH_LONG).show();
            }
        });
        ((BaseAdapter) list_view1.getAdapter()).notifyDataSetChanged();

    }

    public boolean deleteRow(String rowId){
        //Toast.makeText(MainActivity.this, "deleteRow Entered", Toast.LENGTH_LONG).show();
        // 위의 토스트는 디버그용..
        Integer deletedRows = myDb.deleteData(rowId);
        myDb.rearrangeData(rowId);
        populateListView(); // 데이터에 변화가 생겼기 때문에, 새롭게 어댑터를 다시 설정해준다.
        //Toast.makeText(MainActivity.this, "delete Succeeded", Toast.LENGTH_LONG).show();
        return deletedRows != 0;
    }


    private void populateListView(){
        Cursor cursor = myDb.getAllData();
        String[] fromFieldNames = new String[] { myDb.COL_1, myDb.COL_2 };
        int[] toViewIDS = new int[] {R.id.memoIndex, R.id.memoContent};
        /*SimpleCursorAdapter 사용법
        * http://hyojjeong.tistory.com/entry/ListViewCursorAdapter-%EC%82%AC%EC%9A%A9%EB%B2%95*/
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.list_item_memo, cursor, fromFieldNames, toViewIDS, 0);
        list_view1.setAdapter(myCursorAdapter);
    }

    /*
    * 밑의 lifecycle methods는 디버그를 위해 존재한다.
    * */
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart(Bundle) called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause(Bundle) called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume(Bundle) called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

}