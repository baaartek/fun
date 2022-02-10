package org.example;

import java.util.*;

public class Main {

    static void go(int[] t, int idx) {

        if(idx == t.length) {
            System.out.println(Arrays.toString(t));
        } else {
            for(int i=idx; i<t.length; i++) {
                int tmp = t[i];
                t[i] = t[idx];
                t[idx] = tmp;
                go(t, idx+1);
                tmp = t[i];
                t[i] = t[idx];
                t[idx] = tmp;
            }
        }


    }

    public static void main(String[] args) throws Exception {

        int[] t = new int[]{1, 2, 3, 4, 5};

        go(t, 0);


    }

}
