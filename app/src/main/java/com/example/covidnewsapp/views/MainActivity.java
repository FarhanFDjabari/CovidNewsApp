package com.example.covidnewsapp.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.covidnewsapp.R;
import com.example.covidnewsapp.adapter.NewsAdapter;
import com.example.covidnewsapp.adapter.StatisticAdapter;
import com.example.covidnewsapp.api.CovidStatisticApiClient;
import com.example.covidnewsapp.api.CovidStatisticApiInterfaces;
import com.example.covidnewsapp.api.NewsApiClient;
import com.example.covidnewsapp.api.NewsApiInterfaces;
import com.example.covidnewsapp.model.ArticleModel;
import com.example.covidnewsapp.model.NewsModel;
import com.example.covidnewsapp.model.StatisticModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    ViewPager2 viewPager2;
    List<StatisticModel> statisticModelList;
    StatisticAdapter statisticAdapter;
    private Handler statisticHandler;
    public static final String API_KEY = "615a4274fd3a42ab8e597d61d653bde5";
    private RecyclerView newsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<ArticleModel> articles = new ArrayList<>();
    private NewsAdapter newsAdapter;
    private NestedScrollView nestedScrollView;
    private int page = 1, limit = 10;
    private TextView updatedDate, greetMessage;
    private SwipeRefreshLayout refreshLayout;
    private ImageButton menuBtn;
    private FirebaseAuth auth;
    private LinearLayout errorLayout;
    private ProgressBar statisticLoading;
    StatisticModel totalPositif;
    StatisticModel totalMeninggal;
    StatisticModel totalSembuh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        auth = FirebaseAuth.getInstance();

        String accountUsername = auth.getCurrentUser().getDisplayName();
        greetMessage = findViewById(R.id.greetMsg);
        greetMessage.setText("Hi, " + accountUsername + "!");

        statisticHandler = new Handler();

        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.yellow);

        errorLayout = findViewById(R.id.errorLayout);

        statisticLoading = findViewById(R.id.statisticLoading);
        statisticLoading.setVisibility(View.GONE);

        totalPositif = new StatisticModel();
        totalMeninggal = new StatisticModel();
        totalSembuh = new StatisticModel();

        totalSembuh.setCarouselIcon(R.drawable.total_sembuh_emoji);
        totalPositif.setCarouselIcon(R.drawable.total_positif_emoji);
        totalMeninggal.setCarouselIcon(R.drawable.total_meninggal_emoji);

        getStatisticData();

        statisticModelList = new ArrayList<>();
        statisticModelList.add(totalSembuh);
        statisticModelList.add(totalPositif);
        statisticModelList.add(totalMeninggal);

        newsRecyclerView = findViewById(R.id.recyclerView);
        viewPager2 = findViewById(R.id.viewPager);
        menuBtn = findViewById(R.id.collapse_menu_btn);

        menuBtn.bringToFront();

        nestedScrollView = findViewById(R.id.nestedScrollView);
        updatedDate = findViewById(R.id.updatedDate);

        layoutManager = new LinearLayoutManager(MainActivity.this);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        statisticAdapter = new StatisticAdapter(statisticModelList, viewPager2, this);

        viewPager2.setAdapter(statisticAdapter);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                statisticHandler.removeCallbacks(statisticRunnable);
                statisticHandler.postDelayed(statisticRunnable, 3000);
            }
        });

        onLoadingSwipeRefresh();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    onLoadingSwipeRefresh();
                }
            }
        });

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this, menuBtn);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                showAlert();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ProgressDialogStyle);
        builder.setTitle("Are you sure ?");
        builder.setMessage("You will be redirected to login page.");
        builder.setPositiveButton("Ok", (dialog, id) -> logoutProcess() );
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void logoutProcess() {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void initListener() {
        newsAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);

            ArticleModel articleModel = articles.get(position);
            intent.putExtra("url", articleModel.getUrl());
            intent.putExtra("source", articleModel.getSource().getName());
            intent.putExtra("title", articleModel.getTitle());
            intent.putExtra("desc", articleModel.getDescription());

            startActivity(intent);
        });
    }

    private void getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        updatedDate.setText("Last Updated: " + currentDate);
    }

    private void getStatisticData() {
        statisticLoading.setVisibility(View.VISIBLE);
        CovidStatisticApiInterfaces covidStatisticApiInterfaces = CovidStatisticApiClient
                .getApiClient().create(CovidStatisticApiInterfaces.class);

        Call<StatisticModel> callTotalSembuh;
        callTotalSembuh = covidStatisticApiInterfaces.getTotalSembuh();
        callTotalSembuh.enqueue(new Callback<StatisticModel>() {
            @Override
            public void onResponse(Call<StatisticModel> call, Response<StatisticModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (totalSembuh.getName() != null && totalSembuh.getValue() != null) {
                        totalSembuh.clear();
                    }
                    totalSembuh.setName(response.body().getName());
                    totalSembuh.setValue(response.body().getValue());
                    totalSembuh.setCarouselTitle();
                    totalSembuh.setCarouselData();
                    statisticLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<StatisticModel> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Failed fetching \"total sembuh\" data", Toast.LENGTH_SHORT).show();
                totalSembuh.setName("Total Sembuh");
                totalSembuh.setValue("0");
                totalSembuh.setCarouselTitle();
                totalSembuh.setCarouselData();
            }
        });

        Call<StatisticModel> callTotalPositif;
        callTotalPositif = covidStatisticApiInterfaces.getTotalPositif();
        callTotalPositif.enqueue(new Callback<StatisticModel>() {
            @Override
            public void onResponse(Call<StatisticModel> call, Response<StatisticModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (totalPositif.getName() != null && totalPositif.getValue() != null) {
                        totalPositif.clear();
                    }
                    totalPositif.setName(response.body().getName());
                    totalPositif.setValue(response.body().getValue());
                    totalPositif.setCarouselTitle();
                    totalPositif.setCarouselData();
                    statisticLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<StatisticModel> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Failed fetching \"total positif\" data", Toast.LENGTH_SHORT).show();
                totalPositif.setName("Total Positif");
                totalPositif.setValue("0");
                totalPositif.setCarouselTitle();
                totalPositif.setCarouselData();
            }
        });

        Call<StatisticModel> callTotalMeninggal;
        callTotalMeninggal = covidStatisticApiInterfaces.getTotalMeninggal();
        callTotalMeninggal.enqueue(new Callback<StatisticModel>() {
            @Override
            public void onResponse(Call<StatisticModel> call, Response<StatisticModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (totalMeninggal.getName() != null && totalMeninggal.getValue() != null) {
                        totalMeninggal.clear();
                    }
                    totalMeninggal.setName(response.body().getName());
                    totalMeninggal.setValue(response.body().getValue());
                    totalMeninggal.setCarouselTitle();
                    totalMeninggal.setCarouselData();
                    statisticLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<StatisticModel> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Failed fetching \"total meninggal\" data", Toast.LENGTH_SHORT).show();
                totalMeninggal.setName("Total Meninggal");
                totalMeninggal.setValue("0");
                totalMeninggal.setCarouselTitle();
                totalMeninggal.setCarouselData();
            }
        });

    }

    public void LoadJson() {
        errorLayout.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.VISIBLE);
        NewsApiInterfaces newsApiInterfaces = NewsApiClient.getApiClient().create(NewsApiInterfaces.class);
        refreshLayout.setRefreshing(true);

        Call<NewsModel> call;
        call = newsApiInterfaces.getNews("corona virus", "popularity", "en",
                "20", Integer.toString(page), API_KEY);

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful() && response.body().getArticleList() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticleList();
                    newsAdapter = new NewsAdapter(articles, MainActivity.this);
                    newsRecyclerView.setAdapter(newsAdapter);
                    newsAdapter.notifyDataSetChanged();

                    initListener();
                    getTodayDate();
                    refreshLayout.setRefreshing(false);

                } else {
                    refreshLayout.setRefreshing(false);
                    showErrorView();
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                showErrorView();
            }
        });

    }

    private Runnable statisticRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        statisticHandler.removeCallbacks(statisticRunnable);

    }

    @Override
    protected void onResume() {
        super.onResume();
        statisticHandler.postDelayed(statisticRunnable, 3000);
    }

    @Override
    public void onRefresh() {
        LoadJson();
    }

    private void onLoadingSwipeRefresh() {
        if (page == 100/20) {
            page = 1;
            Toast.makeText(this,
                    "Because of API limitation, we are only allowed to display 100 articles",
                    Toast.LENGTH_LONG).show();
        }

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                LoadJson();
            }
        });
    }

    private void showErrorView() {
        errorLayout.setVisibility(
                errorLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
        newsRecyclerView.setVisibility(
                errorLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
    }
}
