package com.example.covidnewsapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.covidnewsapp.R;
import com.example.covidnewsapp.Utils;
import com.example.covidnewsapp.model.ArticleModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private List<ArticleModel> articleModelList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(List<ArticleModel> articleModelList, Context context) {
        this.articleModelList = articleModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holders, int position) {
        final NewsViewHolder holder = holders;
        ArticleModel articleModel = articleModelList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(articleModel.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.newsThumbnail);

        holder.title.setText(articleModel.getTitle());
        holder.desc.setText(articleModel.getDescription());
        holder.source.setText(articleModel.getSource().getName());
        holder.time.setText(" \u2022 " + Utils.DateToTimeFormat(articleModel.getPublishedAt()));
    }

    @Override
    public int getItemCount() {
        return articleModelList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView newsThumbnail;
    TextView title, desc, source, time;
    ProgressBar progressBar;
    NewsAdapter.OnItemClickListener onItemClickListener;

    public NewsViewHolder(@NonNull View itemView, NewsAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);

        itemView.setOnClickListener(this);
        newsThumbnail = itemView.findViewById(R.id.newsThumbnail);
        title = itemView.findViewById(R.id.newsTitle);
        desc = itemView.findViewById(R.id.newsPreview);
        source = itemView.findViewById(R.id.newsSource);
        time = itemView.findViewById(R.id.updatedTime);
        progressBar = itemView.findViewById(R.id.progress_load_photo);

        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v, getAdapterPosition());
    }
}
