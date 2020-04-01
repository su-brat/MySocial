package com.example.mysocial.models;

public class User {

    String Name, Email, About, Phone, Image, Uid, SignInTimestamp, TypingTo;

    public User() {

    }
    public User(String About, String Email, String Name, String Phone, String Image, String Uid, String SignInTimestamp, String TypingTo) {
        this.Name = Name;
        this.Email = Email;
        this.About = About;
        this.Phone = Phone;
        this.Image = Image;
        this.Uid = Uid;
        this.SignInTimestamp = SignInTimestamp;
        this.TypingTo = TypingTo;
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

    public String getSignInTimestamp() {
        return SignInTimestamp;
    }

    public String getTypingTo() {
        return TypingTo;
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

    public void setSignInTimestamp(String signInTimestamp) {
        SignInTimestamp = signInTimestamp;
    }

    public void setTypingTo(String typingTo) {
        TypingTo = typingTo;
    }
}
