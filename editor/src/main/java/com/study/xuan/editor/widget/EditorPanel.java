package com.study.xuan.editor.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.study.xuan.editor.R;
import com.study.xuan.editor.adapter.PanelAdapter;
import com.study.xuan.editor.model.panel.ModelWrapper;
import com.study.xuan.editor.model.panel.PanelFactory;
import com.study.xuan.editor.operate.FontParam;
import com.study.xuan.editor.operate.FontParamBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.study.xuan.editor.common.Const.PANEL_FONT_COLOR;
import static com.study.xuan.editor.common.Const.PANEL_FONT_SIZE;
import static com.study.xuan.editor.common.Const.PANEL_HEADER;

/**
 * Author : xuan.
 * Date : 2017/11/17.
 * Description :操作面板
 */

public class EditorPanel extends LinearLayout {
    private Context mContext;
    private ImageView mIvFont;

    private RecyclerView mPanel;
    private PanelAdapter mPanelAdapter;

    private List<ModelWrapper> mPanelDatas;
    private List<ModelWrapper> mFontDatas;
    private List<ModelWrapper> mHeaderDatas;

    private FontParamBuilder paramBuilder;

    public interface onPanelStateChange {
        void onStateChanged(boolean state);
    }

    private onPanelStateChange mStateChange;

    public void setStateChange(onPanelStateChange mStateChange) {
        this.mStateChange = mStateChange;
    }

    public EditorPanel(@NonNull Context context) {
        this(context, null);
    }

    public EditorPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorPanel(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.editor_panel, this);
        initView(root);
        initEvent();
    }

    private void initEvent() {
        mIvFont.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setSelected(!v.isSelected());
            if (mStateChange != null) {
                mStateChange.onStateChanged(v.isSelected());
            }
            int i = v.getId();
            if (i == R.id.iv_font) {
                if (mFontDatas.size() == 0) {
                    PanelFactory.createFontPanel(mFontDatas);
                    mPanelDatas.addAll(mFontDatas);
                    mPanelAdapter.notifyDataSetChanged();
                }
                mPanel.setVisibility(v.isSelected() ? VISIBLE : GONE);
            }
        }
    };

    private void initView(View root) {
        mIvFont = root.findViewById(R.id.iv_font);
        mPanel = root.findViewById(R.id.rcy_panel);
        GridLayoutManager manager = new GridLayoutManager(mContext, 5){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mPanel.setLayoutManager(manager);
        initDatas();
        mPanelAdapter = new PanelAdapter(mContext, mPanelDatas, paramBuilder);
        setSpanCount(manager, mPanelAdapter);
        mPanel.setAdapter(mPanelAdapter);
    }

    private void initDatas() {
        mPanelDatas = new ArrayList<>();
        mFontDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        paramBuilder = new FontParamBuilder();
    }

    /**
     * 将RecyclerView的网格布局中的某个item设置为独占一行、只占一列，只占两列、等等
     */
    protected void setSpanCount(final GridLayoutManager gridLayoutManager,
                                final RecyclerView.Adapter mAdapter) {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mAdapter.getItemViewType(position);
                if (type == PANEL_FONT_SIZE || type == PANEL_FONT_COLOR || type == PANEL_HEADER) {
                    return gridLayoutManager.getSpanCount();//独占一行
                } else {
                    return 1;//只占一行中的一列
                }
            }
        });
    }

    public FontParam getFontParams() {
        return paramBuilder.build();
    }
}