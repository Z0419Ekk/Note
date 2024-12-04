package com.example.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

public class AddActivity extends AppCompatActivity {

    private EditText etTitle,etContent;

    private NoteDbOpenHelper mNoteDbOpenHelper;
    RadioButton sunny_radio_button,rainy_radio_button,overcast_radio_button,cloudy_radio_button;
    ImageButton ivPhoto;
    Button btn_Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btn_Time = findViewById(R.id.btn_Time);
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
        sunny_radio_button = findViewById(R.id.sunny_radio_button);
        rainy_radio_button = findViewById(R.id.rainy_radio_button);
        overcast_radio_button = findViewById(R.id.overcast_radio_button);
        cloudy_radio_button = findViewById(R.id.cloudy_radio_button);
        ivPhoto = findViewById(R.id.iv_photo);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this,
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

    }

    public void add(View view) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.toastShort(this, "标题不能为空！");
            return;
        }

        Note note = new Note();

        note.setTitle(title);
        note.setContent(content);
        // 获取ImageView中的图片，并转换为Bitmap格式
        BitmapDrawable drawable = (BitmapDrawable) ivPhoto.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

// 创建一个字节数组输出流，用于存储压缩后的图像数据
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();

// 压缩Bitmap，参数40表示压缩质量（0-100），越低压缩率越高
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byStream);

// 将输出流转换为字节数组
        byte[] byteArray = byStream.toByteArray();

// 将字节数组存储到你的对象中
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
        long row = mNoteDbOpenHelper.insertData(note);
        if (row != -1) {
            ToastUtil.toastShort(this,"添加成功！");
            this.finish();
        }else {
            ToastUtil.toastShort(this,"添加失败！");
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //从相册返回的数据
            if (data != null) {
                //得到图片的全路径
                Uri uri = data.getData();
                ivPhoto.setImageURI(uri);
            }
        }
    }

    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}