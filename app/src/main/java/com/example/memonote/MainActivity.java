package com.example.memonote;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText mMemoEdit = null;
    TextFileManager mTextFileManager = new TextFileManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 액션바 색상 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFF7E8E));

        mMemoEdit = (EditText) findViewById(R.id.memo_edit);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            // 1. 파일에 저장된 메모 텍스트 파일 불러오기
            case R.id.load_btn: {
                String memoData = mTextFileManager.load();
                mMemoEdit.setText(memoData);

                Toast.makeText(this, "불러오기 완료", Toast.LENGTH_LONG).show();
                break;
            }

            // 2. EditText에 입력된 메모 -> text 파일로 저장
            case R.id.save_btn: {
                String memoData = mMemoEdit.getText().toString();
                mTextFileManager.save(memoData);
                mMemoEdit.setText("");

                Toast.makeText(this, "저장 완료", Toast.LENGTH_LONG).show();
                break;
            }

            // 3. text 파일로 저장된 메모 삭제
            case R.id.delete_btn: {
                mTextFileManager.delete();
                mMemoEdit.setText("");

                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show();
            }

            // 4. 그림 메모 띄우기
            case R.id.paint: {
                Intent myIntent = new Intent(getApplicationContext(), FingerPaint.class);
            startActivity(myIntent);
            Toast.makeText(this, "그려보세요~", Toast.LENGTH_LONG).show();
        }
        }
    }
}
