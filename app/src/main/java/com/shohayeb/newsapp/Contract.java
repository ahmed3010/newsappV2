package com.shohayeb.newsapp;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

class Contract {
    private static final String ENDPOINT = "http://content.guardianapis.com/search?";
    private static final String SECTION_PARAM = "section";
    private static final String API_PARAM = "api-key";
    private static final String SHOW_TAG_PARAM = "show-tags";
    private static final String CONTRIBUTOR_TAG = "contributor";
    private static final String API_KEY = "e70180ca-8692-4f87-b2b2-e11e4fb1eebf";
    private static final String SORT_PARAM = "order-by";
    private static final String PAGE_PARAM = "page-size";
    private static final String SHOW_FIELDS_PARAM = "show-fields";

    static String getUrl(String section, String page, String sortOrder) {
        if (section != null) {
            return Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter(SECTION_PARAM, section)
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .appendQueryParameter(SHOW_TAG_PARAM, CONTRIBUTOR_TAG)
                    .appendQueryParameter(SHOW_FIELDS_PARAM, "thumbnail")
                    .appendQueryParameter(SORT_PARAM, sortOrder)
                    .appendQueryParameter(PAGE_PARAM, page).build().toString();
        } else {
            return Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .appendQueryParameter(SHOW_TAG_PARAM, CONTRIBUTOR_TAG)
                    .appendQueryParameter(SORT_PARAM, sortOrder)
                    .appendQueryParameter(SHOW_FIELDS_PARAM, "thumbnail")
                    .appendQueryParameter(PAGE_PARAM, page).build().toString();
        }
    }

    public static URL createUrl(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
