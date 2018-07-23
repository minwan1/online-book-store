package com.book.member;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemberControllerTest {


    @Test
    public void name() {

        final int convert = convert("123");
        System.out.println(convert);

    }


    public static int convert(String s) {

        char[] ca = s.toCharArray();
        /*
        123
        1 * 10 + 2 = 12
        12 * 10 + 3= 123;
         */
        int num = 0;

        for (char c : ca) {
            num = num * 10;
            num = num + (c - '0');
        }

        return num;

    }
}