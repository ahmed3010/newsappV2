package com.shohayeb.newsapp;


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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
        if (getArguments() != null) {
            url = getArguments().getString(URL_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        loadingView = rootView.findViewById(R.id.loading_linear_layout);
        errorTextView = rootView.findViewById(R.id.loading_error);
        mySwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        if (isConnected() && newsList.isEmpty()) {
            getLoaderManager().initLoader(loaderID, null, this);
            showLoading();
        } else {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(R.string.loading_error);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new RecyclerAdapter(getContext(), newsList);
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
    }

    private void refresh() {
        if (isConnected()) {
            newsList.clear();
            page = "1";
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
        ConnectivityManager cm =
                (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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
            adapter.notifyDataSetChanged();
            errorTextView.setVisibility(View.GONE);
        } else {
            if (newsList.isEmpty()) {
                errorTextView.setText(R.string.data_error);
                errorTextView.setVisibility(View.VISIBLE);
            }
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
