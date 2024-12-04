package com.example.note;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.adapter.MyAdapter;
import com.example.note.bean.Note;
import com.example.note.util.SpfUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mBtnAdd;
    private List<Note> mNotes;
    private MyAdapter mMyAdapter;

    private NoteDbOpenHelper mNoteDbOpenHelper;

    public static final int MODE_LINEAR = 0;
    public static final int MODE_GRID = 1;

    public static final String KEY_LAYOUT_MODE = "key_layout_mode";

    private int currentListLayoutMode = MODE_LINEAR;

//    EditText search_box;
    Button time_picker_button;

    private TextView jokeTextView;
    private JokeFetcher jokeFetcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
        jokeTextView = findViewById(R.id.jokeTextView);
        jokeFetcher = new JokeFetcher();

        fetchAndDisplayJoke();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDb();
        setListLayout();
    }

    private void setListLayout() {
        currentListLayoutMode = SpfUtil.getIntWithDefault(this, KEY_LAYOUT_MODE, MODE_LINEAR);
        if (currentListLayoutMode == MODE_LINEAR) {
            setToLinearList();
        }else{
            setToGridList();
        }
    }

    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mMyAdapter.refreshData(mNotes);
    }

    private void initEvent() {
        mMyAdapter = new MyAdapter(this, mNotes);
        mRecyclerView.setAdapter(mMyAdapter);
        time_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前日期
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // 创建日期选择弹窗
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                // 将选定的日期设置为按钮文本
                                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
                                time_picker_button.setText("选择的日期:"+selectedDate);
                                mNotes=mNoteDbOpenHelper.queryFromDbByDate(time_picker_button.getText().toString());
                                mMyAdapter.setmBeanList(mNotes);
                                mMyAdapter.notifyDataSetChanged();
                            }
                        }, year, month, dayOfMonth);


                // 显示日期选择弹窗
                datePickerDialog.show();
            }
        });
        findViewById(R.id.clean_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDataFromDb();
                time_picker_button.setText("Pick Time");
            }
        });

    }

    private void initData() {
        mNotes = new ArrayList<>();
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);

    }
    private void fetchAndDisplayJoke() {
        jokeFetcher.fetchJoke(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> jokeTextView.setText("Failed to fetch joke"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JokeFetcher.JokeResponse jokeResponse = jokeFetcher.gson.fromJson(jsonResponse, JokeFetcher.JokeResponse.class);

                    if (jokeResponse != null && jokeResponse.result != null && jokeResponse.result.data.length > 0) {
                        String joke = jokeResponse.result.data[0].content;
                        runOnUiThread(() -> jokeTextView.setText(joke));
                    } else {
                        runOnUiThread(() -> jokeTextView.setText("No joke found"));
                    }
                } else {
                    runOnUiThread(() -> jokeTextView.setText("Failed to fetch joke"));
                }
            }
        });
    }
    private List<Note> getDataFromDB() {
       return mNoteDbOpenHelper.queryAllFromDb();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rlv);
        time_picker_button = findViewById(R.id.time_picker_button);

    }


    public void add(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNotes = mNoteDbOpenHelper.queryFromDbByTitle(newText);
                mMyAdapter.refreshData(mNotes);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setToLinearList() {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMyAdapter.setViewType(MyAdapter.TYPE_LINEAR_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }


    private void setToGridList() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMyAdapter.setViewType(MyAdapter.TYPE_GRID_LAYOUT);
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }
}