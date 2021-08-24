package com.ujjwalkumar.tictactoe;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    static int STATUS_WAITING = 0;
    static int STATUS_P1_WON = 1;
    static int STATUS_P2_WON = 2;
    static int STATUS_TIE = 3;
    static int STATUS_RUNNING = 4;
    String id, pass, uid1, uid2;
    int turn;                   // can be 1 or 2
    int status;                 // can be 0, 1, 2, 3 or 4
    ArrayList<Integer> arr;     // 0-> empty, 1-> p1(circle), 2-> p2(cross)

    public Game(String id, String pass, String uid1, String uid2, int turn, int status, ArrayList<Integer> arr) {
        this.id = id;
        this.pass = pass;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.turn = turn;
        this.status = status;
        this.arr = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            arr.add(0);
    }

    public Game(String id, String uid1) {
        this.id = id;
        this.pass = generatePassword();
        this.uid1 = uid1;
        this.uid2 = "";
        this.turn = 1;
        this.status = STATUS_WAITING;
        this.arr = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            arr.add(0);
    }

    public Game() {
        this.id = "";
        this.pass = "";
        this.uid1 = "";
        this.uid2 = "";
        this.turn = 0;
        this.status = STATUS_WAITING;
        this.arr = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            arr.add(0);
    }

    static String generatePassword() {
        Random rnd = new Random();
        int tmp = rnd.nextInt(890000) + 100001;
        return String.valueOf(tmp);
    }

    public int getWinner() {
        boolean p1 = false;
        boolean p2 = false;

        if (arr.get(0) == 1 && arr.get(1) == 1 && arr.get(2) == 1)
            p1 = true;
        else if (arr.get(3) == 1 && arr.get(4) == 1 && arr.get(5) == 1)
            p1 = true;
        else if (arr.get(6) == 1 && arr.get(7) == 1 && arr.get(8) == 1)
            p1 = true;
        else if (arr.get(0) == 1 && arr.get(3) == 1 && arr.get(6) == 1)
            p1 = true;
        else if (arr.get(1) == 1 && arr.get(4) == 1 && arr.get(7) == 1)
            p1 = true;
        else if (arr.get(2) == 1 && arr.get(5) == 1 && arr.get(8) == 1)
            p1 = true;
        else if (arr.get(0) == 1 && arr.get(4) == 1 && arr.get(8) == 1)
            p1 = true;
        else if (arr.get(2) == 1 && arr.get(4) == 1 && arr.get(6) == 1)
            p1 = true;

        if (arr.get(0) == 2 && arr.get(1) == 2 && arr.get(2) == 2)
            p2 = true;
        else if (arr.get(3) == 2 && arr.get(4) == 2 && arr.get(5) == 2)
            p2 = true;
        else if (arr.get(6) == 2 && arr.get(7) == 2 && arr.get(8) == 2)
            p2 = true;
        else if (arr.get(0) == 2 && arr.get(3) == 2 && arr.get(6) == 2)
            p2 = true;
        else if (arr.get(1) == 2 && arr.get(4) == 2 && arr.get(7) == 2)
            p2 = true;
        else if (arr.get(2) == 2 && arr.get(5) == 2 && arr.get(8) == 2)
            p2 = true;
        else if (arr.get(0) == 2 && arr.get(4) == 2 && arr.get(8) == 2)
            p2 = true;
        else if (arr.get(2) == 2 && arr.get(4) == 2 && arr.get(6) == 2)
            p2 = true;

        if (p1 && p2)
            return 3;
        else if (p1)
            return 1;
        else if (p2)
            return 2;

        for(int e: arr) {
            if(e==0)
                return 0;
        }

        return 1;
    }

    public void toggleTurn() {
        if (this.turn == 1)
            this.turn = 2;
        else
            this.turn = 1;
    }

    public void restartGame() {
        this.turn = 0;
        this.status = STATUS_WAITING;
        this.arr = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            arr.add(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Integer> getArr() {
        return arr;
    }

    public void setArr(ArrayList<Integer> arr) {
        this.arr = arr;
    }

    public int getArr(int i) {
        if (i < 0 || i >= 9)
            return -1;

        return arr.get(i);
    }

    public void setArr(int pos, int val) {
        if (pos < 0 || pos >= 9)
            return;

        arr.set(pos, val);
    }
}
