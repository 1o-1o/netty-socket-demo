package com.me.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransportObject implements Serializable {

    private String name;
    private int id;
    private List<String> list = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", list=" + list +
                '}';
    }

}
