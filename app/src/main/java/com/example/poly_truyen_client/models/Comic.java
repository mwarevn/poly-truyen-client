package com.example.poly_truyen_client.models;

import java.util.ArrayList;

public class Comic {
    private String _id, name, desc, author, poster, createdAt;
    private ArrayList<String> contents;

    private Cats cats;
    public Comic() {
    }

    public Comic(String _id, String name, String desc, String author, String poster, String createdAt, ArrayList<String> contents, Cats cats) {
        this._id = _id;
        this.name = name;
        this.desc = desc;
        this.author = author;
        this.poster = poster;
        this.createdAt = createdAt;
        this.contents = contents;
        this.cats = cats;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public Cats getCats() {
        return cats;
    }

    public void setCats(Cats cats) {
        this.cats = cats;
    }
}
