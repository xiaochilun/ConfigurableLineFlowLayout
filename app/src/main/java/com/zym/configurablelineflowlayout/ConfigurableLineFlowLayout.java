package com.zym.configurablelineflowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义流式布局
 */
public class ConfigurableLineFlowLayout extends ViewGroup {

    private int limitLineCount = 1;// 默认折叠时最多显示1行

    private boolean isLimitLine;// 是否需要折叠

    private boolean isOverFlow;// 是否溢出

    private OverFlowListener overFlowListener;

    public void setLimitLineCount(int limitLineCount) {
        this.limitLineCount = limitLineCount;
    }

    /**
     * 设置折叠或是展开
     * @param limitLine true:折叠 false:展开
     */
    public void setLimitLine(boolean limitLine) {
        isLimitLine = limitLine;
        requestLayout();
        invalidate();
    }

    public void setOverFlowListener(OverFlowListener overFlowListener) {
        this.overFlowListener = overFlowListener;
    }

    public ConfigurableLineFlowLayout(Context context) {
        super(context);
    }

    public ConfigurableLineFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfigurableLineFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 溢出监听
     */
    public interface OverFlowListener {
        void onOverFlow(boolean isOverFlow);
        void onCompleteExpand();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isLimitLine && overFlowListener != null) {
            overFlowListener.onOverFlow(isOverFlow);
        } else if (!isLimitLine && overFlowListener != null) {
            overFlowListener.onCompleteExpand();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得父容器给她设置的测量大小和模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 记录ViewGroup的宽和高
        int width = 0, height = 0;

        int lineWidth = 0;// 每一行的宽度
        int lineHeight = 0;// 第一行的高度

        int lineCount = 1;// 所有行数

        isOverFlow = false;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 测量child的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 获取child的LayoutParams
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 获取当前child实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 获取当前child实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                // 如果当前child加入超出当前行最大宽度则开启新行
                width = Math.max(lineWidth, childWidth);// 更新ViewGroup的宽度
                height += lineHeight;// 累加ViewGroup的高度

                lineWidth = childWidth;// 开启新行的行宽度
                lineHeight = childHeight;// 开启新行的行高度

                lineCount++;
                if (isLimitLine && lineCount == limitLineCount + 1) {
                    isOverFlow = true;
                    break;
                }
            } else {
                // 如果当前child加入没有超过当前行最大宽度
                lineWidth += childWidth;// 累加行宽度
                lineHeight = Math.max(lineHeight, childHeight);// 更新行高度
            }

            // 如果是最后一个child
            if (i == childCount - 1) {
                width = Math.max(lineWidth, childWidth);
                height += lineHeight;
            }
        }

        int measureWidth = (modeWidth == MeasureSpec.EXACTLY ? sizeWidth
                : width + getPaddingLeft() + getPaddingRight());

        int measureHeight = (modeHeight == MeasureSpec.EXACTLY ? sizeHeight
                : height + getPaddingTop() + getPaddingBottom());

        setMeasuredDimension(measureWidth, measureHeight);
    }

    // 存储所有Child的集合
    private List<List<View>> mAllViews = new ArrayList<>();
    // 存储每一行child高度的集合
    private List<Integer> mLineHeights = new ArrayList<>();

    public List<Integer> getLineHeights() {
        return mLineHeights;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();

        int width = getWidth();// ViewGroup宽度

        int lineWidth = 0, lineHeight = 0;// 行宽度 高度

        List<View> lineViews = new ArrayList<>();// 每行child所在的集合
        int lineCount = 1;// 行数

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > width - getPaddingLeft() - getPaddingRight()) {
                // 如果需要换行

                mAllViews.add(lineViews);// 记录当前行所有View
                mLineHeights.add(lineHeight);// 记录当前行的高度

                // 重置
                lineWidth = 0;
                lineViews = new ArrayList<>();

                lineCount++;

                if (isLimitLine && lineCount == limitLineCount + 1) {
                    break;
                }
            }

            lineWidth += childWidth;// 行宽度累加
            lineHeight = Math.max(lineHeight, childHeight);// 行高度更新
            lineViews.add(child);
        }

        // 记录最后一行
        mLineHeights.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0, top = 0;
        int lineNums = mAllViews.size();// 得到总行数

        for (int i = 0; i < lineNums; i++) {
            lineViews = mAllViews.get(i);// 当前行所有的child
            lineHeight = mLineHeights.get(i);// 当前行的高度

            for (int j = 0; j < lineViews.size(); j++) {// 遍历当前行所有child
                View child = lineViews.get(j);
                if (child.getVisibility() == GONE) continue;

                // 计算child的左上右下坐标值
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int leftChild = left + lp.leftMargin;
                int topChild = top + lp.topMargin;
                int rightChild = leftChild + child.getMeasuredWidth();
                int bottomChild = topChild + child.getMeasuredHeight();

                if (rightChild + lp.rightMargin > width) {// 修正right margin
                    rightChild = width - lp.rightMargin;
                }

                child.layout(leftChild, topChild, rightChild, bottomChild);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            // 当前行layout完毕，重置left、top 准备下一行layout
            left = 0;
            top += lineHeight;
        }
    }
}
