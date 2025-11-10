package com.example.restapplication.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

public class LastItemBottomOffsetDecoration extends RecyclerView.ItemDecoration{
	private final int bottomOffsetPx;
        
    private int dpToPx(Context context, int dp){
		return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public LastItemBottomOffsetDecoration(Context context, int bottomOffsetDp){
        this.bottomOffsetPx = dpToPx(context, bottomOffsetDp);
    }
 
    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                                @NonNull View view,
                                @NonNull RecyclerView parent,
                                @NonNull RecyclerView.State state){
        RecyclerView.Adapter adapter = parent.getAdapter();
        if(adapter == null  || !(adapter instanceof LastItemOffsetProvider)) return;   
 
        int lastIndex = adapter.getItemCount() - 1;       
   
        int position = parent.getChildAdapterPosition(view);
        if(position == RecyclerView.NO_POSITION) return;
                
		boolean should = ((LastItemOffsetProvider) adapter).shouldApplyBottomOffset(position);

        if(position == lastIndex && should){
          	outRect.bottom = bottomOffsetPx; // last item bottom offset to not hide behind bottom nav bar
        }else{
            outRect.bottom = 0;
        }
    }
}
