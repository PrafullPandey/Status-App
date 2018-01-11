package com.example.p2_vaio.status;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by p2_vaio on 7/8/2017.
 */

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface onRecyclerClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private final onRecyclerClickListener listener ;
    private final GestureDetectorCompat gestureDetectorCompat;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, final onRecyclerClickListener listener) {
        this.listener = listener;
        gestureDetectorCompat = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: in");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView!=null&&listener!=null){
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    listener.onItemClick(childView,recyclerView.getChildAdapterPosition(childView));
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: in");
                super.onLongPress(e);
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView!=null&&listener!=null){
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    listener.onItemLongClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if(gestureDetectorCompat != null){
            boolean result = gestureDetectorCompat.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned "+result);
            return result;
        }else{
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }
}
