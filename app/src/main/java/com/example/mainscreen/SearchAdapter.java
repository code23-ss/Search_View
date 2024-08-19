package com.example.mainscreen;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<SearchItem> searchItemArrayList;
    private final Activity activity;

    public SearchAdapter(ArrayList<SearchItem> searchItemArrayList, Activity activity) {
        this.searchItemArrayList = searchItemArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // restaurantName으로 변경
        holder.restaurantName.setText(searchItemArrayList.get(position).getRestaurantName());
    }

    @Override
    public int getItemCount() {
        return searchItemArrayList.size();
    }

    public void filterList(ArrayList<SearchItem> filteredList) {
        searchItemArrayList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_search 레이아웃에서 TextView 참조
            this.restaurantName = itemView.findViewById(R.id.restaurantName);
        }
    }
}

