// ===========================================================================
// NOT USED AT THE MOMENT. CAN BE REPLACED WITH OTHER PICTURES IF IMPLEMENTED.
// ===========================================================================

package com.example.restapplication.ui.home;

import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restapplication.R;

public class CatSlideshowAdapter extends RecyclerView.Adapter<CatSlideshowAdapter.CatViewHolder> {
    private final int[] catImages = {
        // R.drawable.judge_1,
        // R.drawable.judge_2,
        // R.drawable.judge_3
        // and more...
    };

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new CatViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        ((ImageView) holder.itemView).setImageResource(catImages[position]);
    }

    @Override
    public int getItemCount() {
        return catImages.length;
    }

    static class CatViewHolder extends RecyclerView.ViewHolder {
        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
