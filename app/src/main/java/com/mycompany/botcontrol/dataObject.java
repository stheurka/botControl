package com.mycompany.botcontrol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhishi on 30/3/15.
 */

public class dataObject {
    private String request;
    private boolean state;
    private Integer id;
    private Integer seek;

    public dataObject()
    {

    }

    public dataObject(String commandRequest, Integer switchId ,boolean switchState, Integer switchSeek)
    {
        this.state = switchState;
        this.request = commandRequest;
        this.id = switchId;
        this.seek = switchSeek;
    }

        //getter and setter methods


    public void setSeek(Integer seek) {
        this.seek = seek;
    }

    public Integer getSeek() {
        return seek;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getRequest() {
        return request;
    }

    public boolean getState() {
        return state;
    }

    public Integer getId() {
        return id;
    }

    @Override
        public String toString() {
            return "DataObject [request=" + request + "state=" +state + "id" + id + "seek" + seek + "]";
        }
 }
