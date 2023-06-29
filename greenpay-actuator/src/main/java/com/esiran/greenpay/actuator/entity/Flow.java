package com.esiran.greenpay.actuator.entity;

import com.sun.net.httpserver.HttpServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Flow<T> {
    private List<Task<T>> tasks;
    private final T data;
    private Map<String,Object> results;
    private String successfulString;
    private String failedString;
    private boolean checked;
    private Object request;
    public Flow(T data) {
        this.data = data;
        this.tasks = new ArrayList<>();
    }
    public void execDependent(String taskName) throws Exception {
        for (Task<T> task : tasks){
            if (task.dependent().equals(taskName)){
                task.action(this);
                execDependent(task.taskName());
            }
        }
    }

    public List<Task<T>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task<T>> tasks) {
        this.tasks = tasks;
    }

    public void add(Task<T> task){
        tasks.add(task);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public T getData(){
        return data;
    }
    public void returns(Map<String,Object> results){
        this.results = results;
    }
    public Map<String,Object> getResults(){
        return results;
    }

    public String getSuccessfulString() {
        return successfulString;
    }

    public void setSuccessfulString(String successfulString) {
        this.successfulString = successfulString;
    }

    public String getFailedString() {
        return failedString;
    }

    public void setFailedString(String failedString) {
        this.failedString = failedString;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }
}
