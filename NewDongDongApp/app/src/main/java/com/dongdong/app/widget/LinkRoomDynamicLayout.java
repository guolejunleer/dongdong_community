package com.dongdong.app.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;

import com.dd121.community.R;
import com.dongdong.app.util.LogUtils;

public class LinkRoomDynamicLayout extends FrameLayout {

    private static final String TAG = "leer";
    private static final boolean DEBUG = false;

    /*
     * 默认4列
     */
    private static final int DEFAULT_RAW_COUNT = 6;
    private static final int DEFAULT_COLUMN_COUNT = 4;

    private static final int INVALID_POINTER = -1;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    private static final long LONG_CLICK_DURATION = 600; // ms
    private static final long ANIMATION_DURATION = 150; // ms

    private static final long EDGE_HOLD_DURATION = 800; // ms

    private static final int EDGE_TOP = 0;
    private static final int EDGE_BOTTOM = 1;

    /*
     * 固定不可移动子View所处的列数
     */
    private static final int FIXED_VIEW_ROW_POSITION_ONE = 1;
    private static final int FIXED_VIEW_ROW_POSITION_TWO = 4;

    /*
     * 固定不可移动子View广告页的高度因子
     */
    private static final float FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR = 0.3f;

    private ArrayList<Integer> newPositions = new ArrayList<>();

    /*
     * 屏幕的宽度
     */
    private int mScreenWidthPixels;
    private int mScreenHeightPixels;

    /*
     * view在屏幕的宽度和正常子View的宽度
     */
    private int mWidth;
    private int mHeight;
    private int mNormalViewWidth;

    /*
     * 上下滑动的判断标识
     */
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;

    /*
     * 手指移动判断的最小距离
     */
    private int mTouchSlop;

    /*
     * 手指触摸界面时的记录参数
     */
    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mActivePointerId = INVALID_POINTER;

    /*
     * 界面状态标识
     */
    private int mScrollState = SCROLL_STATE_IDLE;

    // click & long click
    private int mLastPosition = -1;
    private long mLastDownTime = Long.MAX_VALUE;

    // internal paddings
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingButtom;

    // rearrange
    private int mLastDragged = -1;
    private int mLastTarget = -1;

    // edge holding
    private int mLastEdge = -1;
    private long mLastEdgeTime = Long.MAX_VALUE;

    /*
     * 判断是否滑动到下下页的最小距离
     */
    private int mEdgeSize;

    private Adapter mAdapter;

    public interface OnDynamicViewChangedPositionListener {
        void onItemChangedPosition(int oldPosition, int newPosition);
    }

    private OnDynamicViewChangedPositionListener mDynamicViewChangedListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void OnDynamicViewChangedPositionListener(
            OnDynamicViewChangedPositionListener listener) {
        mDynamicViewChangedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    private static void DEBUG_LOG(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public LinkRoomDynamicLayout(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LinkRoomDynamicLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkRoomDynamicLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        setChildrenDrawingOrderEnabled(true);

        // internal paddings
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingButtom = getPaddingBottom();
        super.setPadding(0, 0, 0, 0);

        final Context context = getContext();
        final ViewConfiguration configuration = ViewConfiguration.get(context);

        float density = context.getResources().getDisplayMetrics().density;
        mScreenWidthPixels = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;

        mNormalViewWidth = mScreenWidthPixels / DEFAULT_COLUMN_COUNT;
        mTouchSlop = ViewConfigurationCompat
                .getScaledPagingTouchSlop(configuration);
        DEBUG_LOG("init-->>>  widthPixels:" + mScreenWidthPixels
                + "; heightPixels:" + mScreenHeightPixels);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec((int) (mNormalViewWidth * (DEFAULT_RAW_COUNT +
                FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR)), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        mWidth = mScreenWidthPixels;
        mHeight = getHeight();

        mEdgeSize = mNormalViewWidth / 4;

        newPositions.clear();
        int childCount = getChildCount();
        // DEBUG_LOG("onMeasure-->>> childCount:" + childCount + "; mWidth:"
        // + mWidth + "; mHeight:" + mHeight);
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final Rect rect = getRectByPosition(i);

                child.measure(MeasureSpec.makeMeasureSpec(rect.width(),
                        MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                        rect.height(), MeasureSpec.EXACTLY));
                child.layout(rect.left, rect.top, rect.right, rect.bottom);
                newPositions.add(-1);
            }
        }
    }

    private Rect getRectByPosition(int i) {
        Rect rect = new Rect();
        if (i == 0) {
            rect.left = 0;
            rect.top = 0;
            rect.right = mWidth;
            rect.bottom = 2 * mNormalViewWidth;
        } else if (i > 0 && i < 9) {
            rect.left = ((i - 1) % DEFAULT_COLUMN_COUNT) * mNormalViewWidth;
            rect.top = (2 + ((i - 1) / DEFAULT_COLUMN_COUNT))
                    * mNormalViewWidth;
            rect.right = rect.left + mNormalViewWidth;
            rect.bottom = rect.top + mNormalViewWidth;
        } else if (i == 9) {
            rect.left = 0;
            rect.top = 4 * mNormalViewWidth;
            rect.right = mWidth;
            rect.bottom = (int) (rect.top + mNormalViewWidth
                    * (1 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR));
        } else if (i > 9) {
            rect.left = ((i - 2) % DEFAULT_COLUMN_COUNT) * mNormalViewWidth;
            rect.top = (int) (((3 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR) +
                    ((i - 2) / DEFAULT_COLUMN_COUNT)) * mNormalViewWidth);
            rect.right = rect.left + mNormalViewWidth;
            rect.bottom = rect.top + mNormalViewWidth;
        }
        return rect;
    }

    private Rect getRectByViewPosition(int i) {
        // 有可能拖拽会有问题，注释！！
        // Rect rect = new Rect();
        // // DEBUG_LOG("getRectByPosi1tion------->>>mUsedWidth:" + mUsedWidth);
        // boolean isOverFixPosition = position > 9;
        // // 当位置大于固定位置view时，要重新它所占用的空间Rect
        // if (position == 0) {
        // rect.left = 0;
        // rect.top = 0;
        // rect.right = rect.left + mWidth;
        // rect.bottom = rect.top + 2 * mNormalViewWidth;
        // } else if (position == 9) {
        // rect.left = 0;
        // rect.top = 4 * mNormalViewWidth;
        // rect.right = rect.left + mWidth;
        // rect.bottom = (int) (rect.top + mNormalViewWidth
        // * (1 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR));
        // } else {
        // int left = mNormalViewWidth
        // * ((isOverFixPosition ? (position - 2) : (position - 1)) %
        // DEFAULT_COLUMN_COUNT);
        // int top = (int) (mNormalViewWidth * (isOverFixPosition ? (((position
        // - 2) / DEFAULT_COLUMN_COUNT) + (3 +
        // FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR))
        // : ((position - 1) / DEFAULT_COLUMN_COUNT) + 2));
        // rect.left = left;
        // rect.top = top;
        // rect.right = rect.left + mNormalViewWidth;
        // rect.bottom = rect.top + mNormalViewWidth;
        // }
        //
        // return rect;
        Rect rect = new Rect();
        if (i == 0) {
            rect.left = 0;
            rect.top = 0;
            rect.right = mWidth;
            rect.bottom = 2 * mNormalViewWidth;
        } else if (i > 0 && i < 9) {
            rect.left = ((i - 1) % DEFAULT_COLUMN_COUNT) * mNormalViewWidth;
            rect.top = (2 + ((i - 1) / DEFAULT_COLUMN_COUNT))
                    * mNormalViewWidth;
            rect.right = rect.left + mNormalViewWidth;
            rect.bottom = rect.top + mNormalViewWidth;
        } else if (i == 9) {
            rect.left = 0;
            rect.top = 4 * mNormalViewWidth;
            rect.right = mWidth;
            rect.bottom = (int) (rect.top + mNormalViewWidth
                    * (1 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR));
        } else if (i > 9) {
            rect.left = ((i - 2) % DEFAULT_COLUMN_COUNT) * mNormalViewWidth;
            rect.top = (int) (((3 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR) + ((i - 2) / DEFAULT_COLUMN_COUNT)) * mNormalViewWidth);
            rect.right = rect.left + mNormalViewWidth;
            rect.bottom = rect.top + mNormalViewWidth;
        }
        return rect;
    }

    private View tempView;

    /*
     * 判断广告是否自动滚动
     */
    private void setIsrefresh(boolean b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View test = getChildAt(i);
            if (test != null && (test instanceof DynamicItemContainView)) {
                DynamicItemContainView a = (DynamicItemContainView) test;
                CommonViewPager pager = (CommonViewPager) a
                        .findViewById(R.id.adview_pager);
                if (pager != null) {
                    pager.setIsRefresh(b);
                }
            }
        }
    }

    /*
     * 设置viewpager跟着手指抬起，进行刷新操作
     */
    private void setNextview() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View test = getChildAt(i);
            if (test != null && (test instanceof DynamicItemContainView)) {
                DynamicItemContainView a = (DynamicItemContainView) test;
                CommonViewPager pager = (CommonViewPager) a.findViewById(R.id.adview_pager);
                if (pager != null) {
                    int mcurrentId = pager.getCurrentItem();
                    pager.setCurrentItem(mcurrentId + 1);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && ev.getEdgeFlags() != 0) {
                // Don't handle edge touches immediately -- they may actually
                // belong
                // to one of our
                // descendants.
                return false;
            }

            final int action = ev.getAction();
            boolean needsInvalidate = false;

            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {

                    // 设定广告不自动播放
                    setIsrefresh(false);

                    // Remember where the motion event started
                    mLastMotionX = mInitialMotionX = ev.getX();
                    mLastMotionY = mInitialMotionY = ev.getY();
                    // mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                    DEBUG_LOG("Down at " + mLastMotionX + "," + mLastMotionY
                            + " mIsBeingDragged=" + mIsBeingDragged
                            + " mIsUnableToDrag=" + mIsUnableToDrag
                            + "; mLastPosition" + mLastPosition);

                    if (!mIsBeingDragged && mScrollState == SCROLL_STATE_IDLE) {
                        mLastPosition = getPositionByXY((int) mLastMotionX,
                                (int) mLastMotionY);
                        DynamicItemContainView childAt = (DynamicItemContainView) getChildAt(mLastPosition);
                        if (mLastPosition >= getChildCount()
                                || (childAt != null && !childAt.getDragState())) {// Don't
                            // handle
                            // out
                            // of
                            // range views
                            return false;
                        }
                        tempView = getChildAt(mLastPosition);
                        tempView.setBackgroundColor(Color.parseColor("#f0f0f0"));
                        // tempView.setBackgroundResource(R.drawable.abc_btn_borderless_material);
                    } else {
                        mLastPosition = -1;
                    }
                    if (mLastPosition >= 0) {
                        mLastDownTime = System.currentTimeMillis();
                    } else {
                        mLastDownTime = Long.MAX_VALUE;
                    }
                    DEBUG_LOG("Down at mLastPosition=" + mLastPosition);

                    // if(){
                    //
                    // }
                    mLastDragged = -1;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    // final int pointerIndex =
                    // MotionEventCompat.findPointerIndex(ev,
                    // /* mActivePointerId */0);
                    // final float x = MotionEventCompat.getX(ev, pointerIndex);
                    // final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float x = ev.getX();
                    final float y = ev.getY();
                    if (mLastDragged >= 0) {
                        // change draw location of dragged visual
                        final View v = getChildAt(mLastDragged);
                        final int l = getScrollX() + (int) x - v.getWidth() / 2;
                        final int t = getScrollY() + (int) y - v.getHeight() / 2;
                        v.layout(l, t, l + v.getWidth(), t + v.getHeight());

                        // check for new target hover
                        if (mScrollState == SCROLL_STATE_IDLE) {
                            final int target = getTargetByXY((int) x, (int) y);

                            if (target != -1 && mLastTarget != target) {
                                animateGap(target);
                                mLastTarget = target;
                                DEBUG_LOG("Moved to mLastTarget=" + mLastTarget);
                            }
                            // edge holding
                            HomePagerHorizontalScrollView parent = (HomePagerHorizontalScrollView) getParent();

                            int relY = (int) (y - parent.getScrollY());
                            final int edge = getEdgeByXY((int) x, relY);
                            if (mLastEdge == -1) {
                                if (edge != mLastEdge) {
                                    mLastEdge = edge;
                                    mLastEdgeTime = System.currentTimeMillis();
                                }
                            } else {
                                if (edge != mLastEdge) {
                                    mLastEdge = -1;
                                } else {
                                    if ((System.currentTimeMillis() - mLastEdgeTime) >= EDGE_HOLD_DURATION) {
                                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                        triggerSwipe(edge);
                                        mLastEdge = -1;
                                    }
                                }
                            }
                        }
                    } else if (!mIsBeingDragged) {
                        final float xDiff = Math.abs(x - mLastMotionX);
                        final float yDiff = Math.abs(y - mLastMotionY);
                        DEBUG_LOG("Moved to " + x + "," + y + " diff=" + xDiff
                                + "," + yDiff);

                        if (yDiff > mTouchSlop && yDiff > xDiff) {// 手指上下滑动时的处理事件
                            DEBUG_LOG("Starting drag=================================Starting drag========!");
                            mIsBeingDragged = true;
                            requestParentDisallowInterceptTouchEvent(true);
                            mLastMotionX = x;
                            mLastMotionY = y - mInitialMotionY > 0 ? mInitialMotionY
                                    + mTouchSlop
                                    : mInitialMotionY - mTouchSlop;
                            setScrollState(SCROLL_STATE_DRAGGING);
                            // setScrollingCacheEnabled(true);
                        }
                    }
                    // Not else! Note that mIsBeingDragged can be set above.
                    if (mIsBeingDragged) {
                        // Scroll to follow the motion event
                        needsInvalidate |= performDrag(y);
                    } else if (mLastPosition >= 0) {
                        final int currentPosition = getPositionByXY((int) x,
                                (int) y);
                        if (currentPosition >= getChildCount()) {
                            return false;
                        }
                        DEBUG_LOG("Moved to currentPosition=" + currentPosition);
                        if (currentPosition == mLastPosition
                                && currentPosition != 0 && currentPosition != 9) {// >=0
                            // changed
                            if ((System.currentTimeMillis() - mLastDownTime) >= LONG_CLICK_DURATION) {
                                if (onItemLongClick(currentPosition)) {
                                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                    mLastDragged = mLastPosition;
                                    requestParentDisallowInterceptTouchEvent(true);
                                    mLastTarget = -1;
                                    animateDragged();
                                    mLastPosition = -1;
                                }
                                mLastDownTime = Long.MAX_VALUE;
                            }
                        } else {
                            mLastPosition = -1;
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {

                    // 设定广告自动播放
                    setIsrefresh(true);

                    tempView.setBackgroundColor(Color.WHITE);
                    // tempView.setBackgroundResource(Color.WHITE);
                    DEBUG_LOG("Touch up!!!mLastDragged:" + mLastDragged
                            + "; mIsBeingDragged:" + mIsBeingDragged
                            + "; mLastTarget:" + mLastTarget);

                    // final int pointerIndex =
                    // MotionEventCompat.findPointerIndex(ev,
                    // /* mActivePointerId */0);
                    // final float x = MotionEventCompat.getX(ev, pointerIndex);
                    // final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float x = ev.getX();
                    final float y = ev.getY();
                    if (mLastDragged > 0) {// >=0 changed
                        rearrange();
                        DEBUG_LOG("Touch up rearrange!!!");
                    } else if (mIsBeingDragged) {
                        // mActivePointerId = INVALID_POINTER;
                        endDrag();
                    } else if (mLastPosition >= 0) {
                        final int currentPosition = getPositionByXY((int) x,
                                (int) y);
                        DEBUG_LOG("Touch up!!! currentPosition=" + currentPosition);
                        if (currentPosition == mLastPosition
                                && currentPosition < getChildCount()) {
                            onItemClick(currentPosition);
                        }
                    } else {
                        requestLayout();
                        invalidate();
                        DEBUG_LOG("ACTION_UP++++++++++++mLastDragged="
                                + mLastDragged + "; mLastPosition:" + mLastPosition);
                    }
                    setScrollState(SCROLL_STATE_IDLE);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:

                    setIsrefresh(true);

                    // tempView.setBackgroundResource(0);
                    tempView.setBackgroundColor(Color.WHITE);
                    DEBUG_LOG("Touch cancel!!!");
                    if (mLastDragged >= 0) {
                        rearrange();
                    } else if (mIsBeingDragged) {
                        // mActivePointerId = INVALID_POINTER;
                        endDrag();
                    }
                    break;
                case MotionEventCompat.ACTION_POINTER_DOWN: {
                    // final int index = MotionEventCompat.getActionIndex(ev);
                    // final float x = MotionEventCompat.getX(ev, index);
                    // mLastMotionX = x;
                    // mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                    break;
                }
                case MotionEventCompat.ACTION_POINTER_UP:
                    // onSecondaryPointerUp(ev);
                    // mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat
                    // .findPointerIndex(ev, mActivePointerId));
                    break;
            }
            if (needsInvalidate) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            return true;
        } catch (Exception e) {
            DEBUG_LOG("onTouchEevent had fatal!!!!" + e.toString());
            return true;
        }
    }

    private void triggerSwipe(int edge) {
        HomePagerHorizontalScrollView parent = (HomePagerHorizontalScrollView) getParent();
        DEBUG_LOG("triggerSwipe parent.getScrollY():" + parent.getScrollY());
        // parent.smoothScrollTo(0, edge == 1 ? 200 : -200);
        parent.smoothScrollBy(0, edge == 1 ? 400 : -400);
    }

    private int getPositionByXY(int x, int y) {
        boolean flag = y > (5 + FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR)
                * mNormalViewWidth;
        y = (int) (flag ? y - FIXED_VIEW_ROW_POSITION_TWO_HEIGHT_FACTOR
                * mNormalViewWidth : y);
        final int col = (x - mPaddingLeft) / mNormalViewWidth;
        final int row = (y - mPaddingTop) / mNormalViewWidth;

        int position = DEFAULT_COLUMN_COUNT * row + col;
        // DEBUG_LOG("getPositionByXY----->>>>col:" + col + "; row:" + row
        // + "; position:" + position);
        if (row <= FIXED_VIEW_ROW_POSITION_ONE) {
            position = 0;
        } else if (row < FIXED_VIEW_ROW_POSITION_TWO
                && row > FIXED_VIEW_ROW_POSITION_ONE) {
            position -= 7;
        } else if (row == FIXED_VIEW_ROW_POSITION_TWO) {
            position = 9;
        } else if (row > FIXED_VIEW_ROW_POSITION_TWO) {
            position -= 10;
        }
        return position;
    }

    private int getTargetByXY(int x, int y) {
        final int position = getPositionByXY(x, y);

        if (position < 0 || position >= getChildCount()) {
            return -1;
        }
        // final Rect r = getRectByPosition(position);
        final Rect r = getRectByViewPosition(position);
        if (!r.contains(x, y)) {
            return -1;
        }
        return position;
    }

    private int getEdgeByXY(int x, int y) {
        if (y < mEdgeSize) {
            return EDGE_TOP;
        } else if (y >= (((View) getParent()).getHeight() - mEdgeSize)) {
            return EDGE_BOTTOM;
        }
        return -1;
    }

    private boolean performDrag(float y) {
        boolean needsInvalidate = false;

        final float deltaY = mLastMotionY - y;
        mLastMotionY = y;
        // scrollBy(0, (int) deltaY);
        DEBUG_LOG("performDrag -->>> scrollY:" + 0 + "; deltaY:" + deltaY);
        return needsInvalidate;
    }

    private boolean onItemLongClick(int position) {
        DEBUG_LOG("onItemLongClick position=" + position);
        return mOnItemLongClickListener == null || mOnItemLongClickListener.onItemLongClick(null,
                getChildAt(position), position, position);
    }

    private void onItemClick(int position) {
        DynamicItemContainView childAt = (DynamicItemContainView) getChildAt(position);
        DEBUG_LOG("onItemClick position=" + position + "; childAt:"
                + childAt.getDragState());
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, getChildAt(position),
                    position, position);
        }
    }

    private void rearrange() {
        if (mLastDragged >= 0 && mLastDragged <= getChildCount()) {
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).clearAnimation();
            }
            DynamicItemContainView childAt = (DynamicItemContainView) getChildAt(mLastTarget);
            if (mLastTarget >= 0 && mLastDragged != mLastTarget
                    && mLastTarget <= getChildCount() && mLastTarget != 0
                    && childAt.getDragState()) {
                DEBUG_LOG("rearrange---- mLastDragged=" + mLastDragged
                        + "; mLastTarget:" + mLastTarget);
                resortViews();
                DEBUG_LOG("rearrange---- mLastDragged=" + mLastDragged
                        + "; mLastTarget:" + mLastTarget);
                if (mDynamicViewChangedListener != null) {
                    mDynamicViewChangedListener.onItemChangedPosition(
                            mLastDragged, mLastTarget);
                }
                DEBUG_LOG("rearrange---- getChildCount()=" + getChildCount());
            }
            mLastDragged = -1;
            mLastTarget = -1;
            requestLayout();
            invalidate();
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
    }

    private void resortViews() {
        final View child = getChildAt(mLastDragged);
        if (mLastDragged < 9 && mLastTarget < 9) {
            removeViewAt(mLastDragged);
            addView(child, mLastTarget);
        } else if (mLastDragged > 9 && mLastTarget > 9) {
            removeViewAt(mLastDragged);
            addView(child, mLastTarget);
        } else if (mLastDragged < 9 && mLastTarget > 9) {
            // 以viewpager为界线，从上布局拖到下布局
            DEBUG_LOG("mLastDragged:" + mLastDragged + "; mLastTarget:"
                    + mLastTarget);
            removeViewAt(mLastDragged);
            addView(child, mLastTarget);
            final View childNever = getChildAt(8);
            removeView(childNever);
            addView(childNever, 9);
            setNextview();
        } else if (mLastDragged > 9 && mLastTarget < 9) {
            // //以viewpager为界线，从下布局拖到上布局
            removeViewAt(mLastDragged);
            addView(child, mLastTarget);
            final View childNever = getChildAt(10);
            removeView(childNever);
            addView(childNever, 9);
            setNextview();
        }

    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }
        mScrollState = newState;
    }

    private void requestParentDisallowInterceptTouchEvent(
            boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mLastDragged == -1) {
            return i;
        } else if (i == childCount - 1) {
            return mLastDragged;
        } else if (i >= mLastDragged) {
            return i + 1;
        }
        return i;
    }

    private void animateDragged() {
        if (mLastDragged >= 0) {
            final View v = getChildAt(mLastDragged);
            DEBUG_LOG("animateDragged-->>> mLastDragged: " + mLastDragged);

            final Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight(),
                    v.getBottom());
            r.inset(-r.width() / 20, -r.height() / 20);
            v.measure(MeasureSpec.makeMeasureSpec(r.width(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    r.height(), MeasureSpec.EXACTLY));
            v.layout(r.left, r.top, r.right, r.bottom);

            AnimationSet animSet = new AnimationSet(true);
            ScaleAnimation scale = new ScaleAnimation(0.9091f, 1.1f, 0.9091f,
                    1.1f, v.getWidth() / 2, v.getHeight() / 2);
            scale.setDuration(ANIMATION_DURATION);
            AlphaAnimation alpha = new AlphaAnimation(1, 1.5f);
            alpha.setDuration(ANIMATION_DURATION);

            animSet.addAnimation(scale);
            animSet.addAnimation(alpha);
            animSet.setFillEnabled(true);
            animSet.setFillAfter(true);

            v.clearAnimation();
            v.startAnimation(animSet);
        }
    }

    private void animateGap(int target) {
        for (int i = 0; i < getChildCount(); i++) {
            DynamicItemContainView v = (DynamicItemContainView) getChildAt(i);

            int tempPosition = i > 9 ? i - 2 : i - 1;
            if (/* i == mLastDragged || */!v.getDragState()) {// if can not drag
                continue;
            }

            int newPos = i;
            if (mLastDragged < target && i > mLastDragged && i <= target) {
                newPos--;
            } else if (target < mLastDragged && i >= target && i < mLastDragged) {
                newPos++;
            }

            int oldPos = i;
            if (newPositions.get(i) != -1) {
                oldPos = newPositions.get(i);
            }
            DEBUG_LOG("animateGap target:" + target + " i=" + i
                    + ", mLastDragged=" + mLastDragged + ";newPos==" + newPos
                    + ";oldPos==" + oldPos + "; newPositions.get(" + i + ")="
                    + newPositions.get(i));
            if (oldPos == newPos || target == 0 || target == 9) {
                continue;
            }

            // animate
            DEBUG_LOG("animateGap :::::::::::from=" + oldPos + ", to=" + newPos);
            // 这一步是处理移动子view拖动到不可移动子view时的情况
            boolean flag = false;
            if (oldPos < 9 && newPos == 9) {
                newPos++;
                flag = true;
            } else if (oldPos > 9 && newPos == 9) {
                newPos--;
                flag = true;
            }
            final Rect oldRect = getRectByViewPosition(oldPos);
            final Rect newRect = getRectByViewPosition(newPos);
            oldRect.offset(-v.getLeft(), -v.getTop());
            newRect.offset(-v.getLeft(), -v.getTop());

            TranslateAnimation translate = new TranslateAnimation(oldRect.left,
                    newRect.left, oldRect.top, newRect.top);
            translate.setDuration(ANIMATION_DURATION);
            translate.setFillEnabled(true);
            translate.setFillAfter(true);
            v.clearAnimation();
            v.startAnimation(translate);
            if (oldPos < 9 && flag) {
                newPos--;
            } else if (oldPos > 9 && flag) {
                newPos++;
            }
            newPositions.set(i, newPos);
        }
    }

    public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            removeAllViews();
            scrollTo(0, 0);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            for (int i = 0; i < mAdapter.getCount(); i++) {
                final View child = mAdapter.getView(i, null, this);
                addView(child);
            }
        }
    }

    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    };

    private void dataSetChanged() {
        DEBUG_LOG("dataSetChanged");
        for (int i = 0; i < getChildCount() && i < mAdapter.getCount(); i++) {
            final View child = getChildAt(i);
            final View newChild = mAdapter.getView(i, child, this);
            if (newChild != child) {
                removeViewAt(i);
                addView(newChild, i);
            }
        }
        for (int i = getChildCount(); i < mAdapter.getCount(); i++) {
            final View child = mAdapter.getView(i, null, this);
            addView(child);
        }
        while (getChildCount() > mAdapter.getCount()) {
            removeViewAt(getChildCount() - 1);
        }
    }

    /**
     * 获取物业公告ViewPager
     *
     * @return viewpager
     */
    public Map getBulletinViewPager() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View test = getChildAt(i);
            if (test != null && (test instanceof DynamicItemContainView) && i == 9) {
                LogUtils.i("LinkRoomDynamicLayout.clazz-->getBulletinViewPager()");
                DynamicItemContainView a = (DynamicItemContainView) test;
                CommonViewPager pager = (CommonViewPager) a.findViewById(R.id.adview_pager);
                ViewGroup points = (ViewGroup) a.findViewById(R.id.ll_point);
                LogUtils.i("LinkRoomDynamicLayout.clazz-->getBulletinViewPager()-->ViewPager:" + pager);

                Map<String,Object> map = new HashMap<>();
                map.put("viewPager", pager);
                map.put("viewGroup", points);
                return map;
            }
        }
        return null;
    }

    /**
     * 获取广告ViewPager
     *
     * @return viewpager
     */
    public Map getADViewPager() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View test = getChildAt(i);
            if (test != null && (test instanceof DynamicItemContainView) && i == 0) {
                LogUtils.i("LinkRoomDynamicLayout.clazz-->getADViewPager()");
                DynamicItemContainView a = (DynamicItemContainView) test;
                CommonViewPager pager = (CommonViewPager) a.findViewById(R.id.adview_pager);
                ViewGroup points = (ViewGroup) a.findViewById(R.id.ll_point);
                LogUtils.i("LinkRoomDynamicLayout.clazz-->getADViewPager()-->ViewPager:" + pager);
                Map<String,Object> map = new HashMap<>();
                map.put("viewPager", pager);
                map.put("viewGroup", points);
                return map;
            }
        }
        return null;
    }
}