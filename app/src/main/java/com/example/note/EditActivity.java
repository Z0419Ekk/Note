package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.example.note.bean.Note;
import com.example.note.util.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private Note note;
    private EditText etTitle,etContent;

    private NoteDbOpenHelper mNoteDbOpenHelper;
    RadioButton sunny_radio_button,rainy_radio_button,overcast_radio_button,cloudy_radio_button;
    ImageButton ivPhoto;
    Button btn_Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        sunny_radio_button = findViewById(R.id.sunny_radio_button);
        rainy_radio_button = findViewById(R.id.rainy_radio_button);
        overcast_radio_button = findViewById(R.id.overcast_radio_button);
        cloudy_radio_button = findViewById(R.id.cloudy_radio_button);
        ivPhoto = findViewById(R.id.iv_photo);
        btn_Time = findViewById(R.id.btn_Time);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,1);
            }
        });

        btn_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前日期
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // 创建日期选择弹窗
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                // 将选定的日期设置为按钮文本
                                String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
                                btn_Time.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);

                // 显示日期选择弹窗
                datePickerDialog.show();
            }
        });
        initData();

    }

    private void initData() {
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
            switch (note.getWeather()) {
                case 0: sunny_radio_button.setChecked(true);break;
                case 1: rainy_radio_button.setChecked(true);break;
                case 2: overcast_radio_button.setChecked(true);break;
                case 3: cloudy_radio_button.setChecked(true);break;
            }
            btn_Time.setText("选择的日期:"+note.getCreatedTime());

            if(note.getPicture()!=null){
                Bitmap img = BitmapFactory.decodeByteArray(note.getPicture(), 0, note.getPicture().length);
                ivPhoto.setImageBitmap(img);
            }
        }
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    public void save(View view) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.toastShort(this, "标题不能为空！");
            return;
        }

        note.setTitle(title);
        note.setContent(content);
        //把图片先转化成bitmap格式
        BitmapDrawable drawable = (BitmapDrawable) ivPhoto.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        //二进制数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        //将图片压缩成质量为100的PNG格式图片
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byStream);
        //把输出流转换为二进制数组
        byte[] byteArray = byStream.toByteArray();
        note.setPicture(byteArray);
        note.setCreatedTime(btn_Time.getText().toString());
        if (sunny_radio_button.isChecked()){
            note.setWeather(0);
        }else if (rainy_radio_button.isChecked()){
            note.setWeather(1);
        }else if (overcast_radio_button.isChecked()){
            note.setWeather(2);
        }else if (cloudy_radio_button.isChecked()){
            note.setWeather(3);
        }else {
            note.setWeather(0);
        }
        long rowId = mNoteDbOpenHelper.updateData(note);
        if (rowId != -1) {
            ToastUtil.toastShort(this, "修改成功！");
            this.finish();
        }else{
            ToastUtil.toastShort(this, "修改失败！");
        }

    }

    private String getCurrentTimeFormat() {
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        Date date = new Date();
        return sdf.format(date);
    }
}