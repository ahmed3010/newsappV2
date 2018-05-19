package com.shohayeb.newsapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String URL_KEY = "param1";
    private View loadingView;
    private TextView errorTextView;
    private int loaderID = 0;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private String url;
    private String page = "1";
    private ArrayList<News> newsList = new ArrayList<>();
    private RecyclerAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView recyclerView;

    public NewsFragment() {
    }

    public static NewsFragment newInstance(String url) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        adapter = new RecyclerAdapter(getContext(), newsList);
        if (getArguments() != null) {
            url = getArguments().getString(URL_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        loadingView = rootView.findViewById(R.id.loading_linear_layout);
        errorTextView = rootView.findViewById(R.id.loading_error);
        mySwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        if (isConnected() && newsList.isEmpty()) {
            getLoaderManager().initLoader(loaderID, null, this);
            showLoading();
        } else if (errorTextView != null) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(R.string.loading_error);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void loadNextDataFromApi() {
        if (isConnected() && !newsList.isEmpty()) {
            int newPage = Integer.parseInt(page);
            page = String.valueOf(++newPage);
            if (!newsList.contains(null)) {
                newsList.add(null);
                adapter.notifyItemInserted(newsList.size() - 1);
            }
            getLoaderManager().restartLoader(loaderID, null, this);
        } else {
            Toast.makeText(getContext(), R.string.loading_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (recyclerView != null && adapter != null) {
                View view = recyclerView.getLayoutManager().findViewByPosition(0);
                if (view != null) {
                    ImageView imageView = view.findViewById(R.id.main_image);
//                    imageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
                }
            }
        } else {
            if (recyclerView != null && adapter != null) {
                View view = recyclerView.getLayoutManager().findViewByPosition(0);
                if (view != null) {
                    ImageView imageView = view.findViewById(R.id.main_image);
                    imageView.setAnimation(null);
                }
            }
        }
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }
                }
        );
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onGlobalLayout() {
                View view = recyclerView.getLayoutManager().findViewByPosition(0);
                if (view != null) {
                    ImageView imageView = view.findViewById(R.id.main_image);
//                    imageView.setAnimation(null);
                    if (isMenuVisible() && isVisible() && imageView.getTag() == null) {
                        if (imageView.getAnimation() == null) {
                            imageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
//                            imageView.setTag("s");
                        } else {
                            imageView.getAnimation().cancel();
                        }
                    }
                }
            }
        });
    }

    private void refresh() {
        if (isConnected()) {
            newsList.clear();
            page = "1";
            scrollListener.resetState();
            getLoaderManager().restartLoader(loaderID, null, this);
        } else {
            errorTextView.setText(R.string.loading_error);
            Toast.makeText(getContext(), "No internet connection found", Toast.LENGTH_SHORT).show();
            hideLoading();
            if (newsList.isEmpty()) {
                errorTextView.setVisibility(View.VISIBLE);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        }
    }

    private void hideLoading() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
        loadingView.setVisibility(View.GONE);
    }

    private boolean isConnected() {
        if (getContext() != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(getContext(), Contract.createUrl(uriParser(url, page)));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        hideLoading();
        if (!data.isEmpty()) {
            if (!newsList.isEmpty() && newsList.contains(null)) {
                newsList.remove(newsList.size() - 1);
                adapter.notifyItemRemoved(newsList.size() - 1);
            }
            newsList.addAll(data);
            adapter.notifyItemRangeInserted(newsList.size(), data.size());
            errorTextView.setVisibility(View.GONE);

        } else {
            if (newsList.isEmpty()) {
                errorTextView.setText(R.string.data_error);
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
        View view = recyclerView.getLayoutManager().findViewByPosition(0);
        if (view != null) {
            ImageView imageView = view.findViewById(R.id.main_image);
            imageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {

    }

    private String uriParser(String url, String page) {
        Uri uri = Uri.parse(url);
        return uri.buildUpon().appendQueryParameter("page", page).toString();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getLoaderManager().destroyLoader(loaderID);
    }

}
