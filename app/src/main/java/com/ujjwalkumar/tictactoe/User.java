package com.ujjwalkumar.tictactoe;

public class User {
    String uid, pass, email, name;
    int won, tie, total;

    public User(String uid, String pass, String email, String name, int won, int tie, int total) {
        this.uid = uid;
        this.pass = pass;
        this.email = email;
        this.name = name;
        this.won = won;
        this.tie = tie;
        this.total = total;
    }

    public User(String uid, String pass, String email, String name) {
        this.uid = uid;
        this.pass = pass;
        this.email = email;
        this.name = name;
        this.won = 0;
        this.tie = 0;
        this.total = 0;
    }

    public User() {
        this.uid = "";
        this.pass = "";
        this.email = "";
        this.name = "";
        this.won = 0;
        this.tie = 0;
        this.total = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getTie() {
        return tie;
    }

    public void setTie(int tie) {
        this.tie = tie;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
