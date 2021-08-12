package com.hanium.greenduks;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QnaListActivity extends AppCompatActivity {

    RecyclerView qRecyclerView;
    QnaAdapter qRecyclerAdapter;
    ArrayList<Qna> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qnalist);

        qRecyclerView = (RecyclerView)findViewById(R.id.rvQna);

        /* initiate adapter */
        qRecyclerAdapter = new QnaAdapter();

        /* initiate recyclerview */
        qRecyclerView.setAdapter(qRecyclerAdapter);
        qRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* adapt data */
        list = new ArrayList<Qna>();
        for(int i = 1; i <= 10; i++){
            if(i % 2 == 0)
                list.add(new Qna("답변완료", "문의 제목입니다.(test)", "2021-08-12"));
            else
                list.add(new Qna("미답변", "문의 제목입니다.(test)", "2021-08-12"));
        }

        qRecyclerAdapter.setQnaList(list);
    }

}