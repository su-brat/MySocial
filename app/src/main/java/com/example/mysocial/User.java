package com.example.mysocial;

public class User {

    String Name, Email, About, Phone, Image, Uid;

    public User() {

    }
    public User(String About, String Email, String Name, String Phone, String Image, String Uid) {
        this.Name = Name;
        this.Email = Email;
        this.About = About;
        this.Phone = Phone;
        this.Image = Image;
        this.Uid = Uid;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public String getAbout() {
        return About;
    }

    public String getImage() {
        return Image;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAbout(String about) {
        About = about;
    }
}
