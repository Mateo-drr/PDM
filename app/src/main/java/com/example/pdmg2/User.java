package com.example.pdmg2;

import java.io.Serializable;

public class User implements Serializable, Comparable<User> {
    private String email;
    private String password;
    private String name;


    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return  "Nome: " + name + "\nEmail: '" + email + "\nPassword: " + password;
    }

    @Override
    public int compareTo(User o) {
        return email.compareToIgnoreCase(o.email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
