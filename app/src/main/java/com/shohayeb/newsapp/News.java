package com.shohayeb.newsapp;

import java.io.Serializable;

/**
 * Created by Ahmad on 19/04/2018.
 */

public class News implements Serializable {
    public static final long serialVersionUID = 21042018L;
    private String title, section, date, author, webUrl, imageUrl;

    News(String title, String section, String date, String webUrl, String author, String imageUrl) {
        this.title = title;
        this.section = section;
        this.date = date;
        this.webUrl = webUrl;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }
}

