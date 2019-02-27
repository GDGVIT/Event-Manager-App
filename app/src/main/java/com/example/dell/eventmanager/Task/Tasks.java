package com.example.dell.eventmanager.Task;

import java.io.Serializable;
import java.util.HashMap;

public class Tasks implements Serializable {

    private String date;
    private String name;
    private String id;
    private String lable;
    private String lableColor;
    private String completed;

    public class Users implements Serializable {

        String user_name;
        String user_id;
        String user_email;
        String user_photoUrl;

        public Users() {
        }

        public Users(String id, String name, String email, String photoUrl) {
            this.user_email = email;
            this.user_name = name;
            this.user_id = id;
            this.user_photoUrl = photoUrl;
        }

//        public String getUserName() {
//            return user_name;
//        }
//
//        public void setUserName(String user_name) {
//            this.user_name = user_name;
//        }
//
//        public String getUserEmail() {
//            return user_email;
//        }
//
//        public void setUserEmail(String user_email) {
//            this.user_email = user_email;
//        }
//
//        public String getUserKey() {
//            return user_id;
//        }
//
//        public void setUserKey(String user_id) {
//            this.user_id = user_id;
//        }
//
//        public String getUserPhotoUrl() {
//            return user_photoUrl;
//        }
//
//        public void setUserPhotoUrl(String user_photoUrl) {
//            this.user_photoUrl = user_photoUrl;
//        }
//
//        public HashMap<String,String> toUsersFirebaseObject() {
//            HashMap<String,String> user =  new HashMap<String,String>();
//            user.put("user_name", user_name);
//            user.put("user_email", user_email);
//            user.put("user_photo", user_photoUrl);
//            user.put("user_id",user_id);
//            return user;
//        }
    }


    public Tasks() {
    }

    public Tasks(String name, String date, String id, String lable, String lableColor, String completed) {
        this.date = date;
        this.name = name;
        this.id = id;
        this.lable = lable;
        this.lableColor = lableColor;
        this.completed = completed;
    }

    public String getTaskName() {
        return name;
    }

    public void setTaskName(String name) {
        this.name = name;
    }

    public String getTaskDate() {
        return date;
    }

    public void setTaskDate(String date) {
        this.date = date;
    }

    public String getTaskKey() {
        return id;
    }

    public void setTaskKey(String id) {
        this.id = id;
    }

    public void setTaskLable(String lable) {
        this.lable = lable;
    }

    public String getTaskLable() {
        return lable;
    }

    public String getTaskLableColor() {
        return lableColor;
    }

    public void setTaskLableColor(String lableColor) {
        this.lableColor = lableColor;
    }

    public String getTaskComplete() {
        return completed;
    }

    public void setTaskComplete(String completed) {
        this.completed = completed;
    }

    public HashMap<String, String> toTasksFirebaseObject() {
        HashMap<String, String> task = new HashMap<String, String>();
        task.put("name", name);
        //todo.put("message", message);
        task.put("date", date);
        task.put("lable", lable);
        task.put("lableColor", lableColor);
        task.put("completed", completed);
        return task;
    }

    Users u = new Users();
    public String getUserName() {
        return u.user_name;
    }

    public void setUserName(String user_name) {
        this.u.user_name = user_name;
    }

    public String getUserEmail() {
        return u.user_email;
    }

    public void setUserEmail(String user_email) {
        this.u.user_email = user_email;
    }

    public String getUserKey() {
        return u.user_id;
    }

    public void setUserKey(String user_id) {
        this.u.user_id = user_id;
    }

    public String getUserPhotoUrl() {
        return u.user_photoUrl;
    }

    public void setUserPhotoUrl(String user_photoUrl) {
        this.u.user_photoUrl = user_photoUrl;
    }

    public HashMap<String, String> toUsersFirebaseObject() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("user_name", u.user_name);
        user.put("user_email", u.user_email);
        user.put("user_photo", u.user_photoUrl);
        user.put("user_id", u.user_id);
        return user;
    }

}
