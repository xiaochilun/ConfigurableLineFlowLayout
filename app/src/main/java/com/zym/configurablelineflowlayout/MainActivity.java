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
    private static final int MAX_SHOW_LINE_COUNT = 6;// 最多显示行数
    private ConfigurableLineFlowLayout mFlowLayout;
    private List<TagBean> mTagList = new ArrayList<>();
    private boolean isSpread;// 是否展开
    private MaxHeightScrollView mMaxHeightScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mTagList.clear();
        for (int i = 0; i < 50; i++) {
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
        mMaxHeightScrollView = findViewById(R.id.scroll_view);
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
            public void onOverFlow(boolean isOverFlow) {
                tv.setVisibility(isOverFlow ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCompleteExpand() {
                List<Integer> lineHeights = mFlowLayout.getLineHeights();
                if (!isSpread || lineHeights.isEmpty()) {
                    return;
                }

                int maxHeight = 0;
                if (lineHeights.size() >= MAX_SHOW_LINE_COUNT) {// 超过N行 高度固定 可滑动
                    for (int i = 0; i < MAX_SHOW_LINE_COUNT; i++) {
                        maxHeight += lineHeights.get(i);
                    }
                    mMaxHeightScrollView.setMaxHeight(maxHeight);
                }
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
