package com.example.covidnewsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.covidnewsapp.R;
import com.example.covidnewsapp.model.StatisticModel;

import java.util.List;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticViewHolder>{
    private List<StatisticModel> statisticModels;
    private ViewPager2 viewPager2;
    private Context context;

    public StatisticAdapter(List<StatisticModel> statisticModels, ViewPager2 viewPager2, Context context) {
        this.statisticModels = statisticModels;
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatisticViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.viewpager_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticViewHolder holder, int position) {
        holder.setStatistic(statisticModels.get(position));
        if (position == statisticModels.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return statisticModels.size();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            statisticModels.addAll(statisticModels);
            notifyDataSetChanged();
        }
    };
}

class StatisticViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView title;
    TextView data;

    public StatisticViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.carouselIcon);
        title = itemView.findViewById(R.id.carouselTitle);
        data = itemView.findViewById(R.id.carouselData);
    }

    void setStatistic(StatisticModel statisticModel) {
        imageView.setImageResource(statisticModel.getCarouselIcon());
        title.setText(statisticModel.getCarouselTitle());
        data.setText(statisticModel.getCarouselData());
    }
}
