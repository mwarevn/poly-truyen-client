package com.example.poly_truyen_client.models;

public class Cats {
    private String _id, name;

    public Cats() {
    }

    public Cats(String name) {
        this.name = name;
    }

    public Cats(String _id, String name) {
        this._id = _id;
        this.name = name;
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
}
