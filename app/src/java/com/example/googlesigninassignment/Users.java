package com.example.googlesigninassignment;

public class Users {



    String image;
    String email;
    String name;

    String id;
    public Users(String name, String email, String image, String id){
        this.image = image;
        this.email = email;
        this.name = name;
        this.id = id;


    }

    public Users (){

    }

    public String getImage() {

        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String uid) {
        this.id = uid;

    }
    public String getId() {
       return id;
    }
}
