package org.techtown.planner01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.channguyen.rsv.RangeSliderView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class Fragment2 extends Fragment {
    private static final String TAG = "Fragment2";

    // 문맥, 리스너, 요청 리스너
    Context context;
    OnTabItemSelectedListener listener;
    OnRequestListener requestListener;

    // 날짜 텍스트
    TextView dateTextView;

    // 컨텐츠 인풋
    EditText contentsInput;

    // 파일? 결과 포토 비트맵
    File file;
    Bitmap resultPhotoBitmap;

    // 삽입 모드
    int mMode = AppConstants.MODE_INSERT;


    // 난이도 슬라이드
    RangeSliderView diffSlider;
    int diffIndex = 2;

    // 중요도 슬라이드
    RangeSliderView impoSlider;
    int impoIndex = 2;

    // 아이템
    Note item;

    // 불러오기
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }

        if (context instanceof OnRequestListener) {
            requestListener = (OnRequestListener) context;
        }
    }

    // 떼내기
    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }

    // 뷰 생성
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(context, "계획 관리 탭", Toast.LENGTH_LONG).show();

        // 인플레이터 활용 루트뷰 : 프래그먼트2 데리고 오기
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        // UI 설정
        initUI(rootView);

        // check current location
        if (requestListener != null) {
            requestListener.onRequest("getCurrentLocation");
        }

        applyItem();

        return rootView;
    }

    // UI 설정
    private void initUI(ViewGroup rootView) {

        // 날짜 컨텐츠 난이도
        dateTextView = rootView.findViewById(R.id.dateTextView);
        contentsInput = rootView.findViewById(R.id.contentsInput);
        diffSlider = rootView.findViewById(R.id.sliderView);
        impoSlider = rootView.findViewById(R.id.sliderView2);

        // 저장버튼
        Button saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 저장 또는 변화
                if(mMode == AppConstants.MODE_INSERT) {
                    saveNote();
                } else if(mMode == AppConstants.MODE_MODIFY) {
                    modifyNote();
                }

                // 리스너 변환
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });

        // 삭제 버튼
        Button deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });

        // 난이도 슬라이드 조작
        RangeSliderView sliderView = rootView.findViewById(R.id.sliderView);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int dindex) {
                AppConstants.println("diffIndex changed to " + dindex);
                diffIndex = dindex;
            }

        };

        // 값 조작 하기
        sliderView.setOnSlideListener(listener);
        sliderView.setInitialIndex(4);

        // 중요도 슬라이드 조작
        RangeSliderView sliderView2 = rootView.findViewById(R.id.sliderView2);
        final RangeSliderView.OnSlideListener listener2 = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int iindex) {
                AppConstants.println("impoIndex changed to " + iindex);
                impoIndex = iindex;
            }
        };

        sliderView2.setOnSlideListener(listener2);
        sliderView2.setInitialIndex(2);
    }


    public void setDateString(String dateString) {
        dateTextView.setText(dateString);
    }

    public void setContents(String data) {
        contentsInput.setText(data);
    }


    public void setDiffIndex(int diff) {
        try {
            diffIndex = diff;
            diffSlider.setInitialIndex(diffIndex);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setImpoIndex(int impo) {
        try {
            impoIndex = impo;
            impoSlider.setInitialIndex(impoIndex);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setItem(Note item) {
        this.item = item;
    }

    public void applyItem() {
        AppConstants.println("applyItem called.");

        if (item != null) {
            mMode = AppConstants.MODE_MODIFY;

            setDateString(item.getCreateDateStr());
            setContents(item.getContents());

            setDiffIndex(item.getDiff());
            setImpoIndex(item.getImpo());
        } else {
            mMode = AppConstants.MODE_INSERT;



            Date currentDate = new Date();
            String currentDateString = AppConstants.dateFormat3.format(currentDate);
            setDateString(currentDateString);

            contentsInput.setText("");
            //pictureImageView.setImageResource(R.drawable.noimagefound);
            setDiffIndex(2);
            setImpoIndex(2);
        }

    }


    // 노트 저장
    private void saveNote() {
        String contents = contentsInput.getText().toString();


        // 이거 찾아가서 바꿔야함
        String sql = "insert into " + NoteDatabase.TABLE_NOTE +
                "(CONTENTS, diff, impo) values(" +
                "'"+ contents + "', " +
                "'"+ diffIndex + "', " +
                "'"+ impoIndex + "')";

        Log.d(TAG, "sql : " + sql);
        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sql);

    }

    //노트 변경
    private void modifyNote() {
        if (item != null) {
            //String address = locationTextView.getText().toString();
            String contents = contentsInput.getText().toString();


            // update note
            String sql = "update " + NoteDatabase.TABLE_NOTE +
                    " set " +
                    "   ,CONTENTS = '" + contents + "'" +
                    "   ,diff = '" + diffIndex + "'" +
                    "   ,impo = '" + impoIndex + "'" +
                    " where " +
                    "   _id = " + item._id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }


    // 삭제 함수
    private void deleteNote() {
        AppConstants.println("deleteNote called.");

        if (item != null) {
            // delete note
            String sql = "delete from " + NoteDatabase.TABLE_NOTE +
                    " where " +
                    "   _id = " + item._id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }


}
