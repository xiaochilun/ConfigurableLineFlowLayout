package com.zym.configurablelineflowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 可配置折叠显示行数的FlowLayout Demo
 */
public class MainActivity extends AppCompatActivity {

    private static final int LIMIT_LINE_COUNT = 2;
    private ConfigurableLineFlowLayout mFlowLayout;
    private List<TagBean> mTagList = new ArrayList<>();
    private boolean isSpread;// 是否展开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mTagList.clear();
        for (int i = 0; i < 20; i++) {
            TagBean tagBean = new TagBean(String.format("标签%d", i));
            mTagList.add(tagBean);
        }

        renderView();
    }

    private void renderView() {
        mFlowLayout.removeAllViews();

        for (TagBean tagBean : mTagList) {
            final TextView tagView = getTagView();
            tagView.setText(tagBean.name);

            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int childCount = mFlowLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        mFlowLayout.getChildAt(i).setSelected(false);
                    }
                    tagView.setSelected(true);
                }
            });

            mFlowLayout.addView(tagView);
        }

        mFlowLayout.getChildAt(0).setSelected(true);
        mFlowLayout.setLimitLine(true);
    }

    private void initView() {
        final TextView tv = findViewById(R.id.tv);
        mFlowLayout = findViewById(R.id.flow_layout);
        mFlowLayout.setLimitLineCount(LIMIT_LINE_COUNT);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpread = !isSpread;
                mFlowLayout.setLimitLine(!isSpread);
                tv.setText(isSpread ? "折叠" : "展开");
            }
        });


        mFlowLayout.setOverFlowListener(new ConfigurableLineFlowLayout.OverFlowListener() {
            @Override
            public void overFlow(boolean isOverFlow) {
                tv.setVisibility(isOverFlow ? View.VISIBLE : View.GONE);
            }
        });
    }

    private TextView getTagView() {
        TextView view = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.layout_tag, null, false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        view.setLayoutParams(layoutParams);
        return view;
    }

}
