package com.apptive.joDuo.isthere.search;

/**
 * Created by joseong-yun on 2017. 6. 8..
 */

public class AddressItem {
    public String old_name;
    public String old_bunji;
    public String old_ho;
    public String old_san;
    // 우편번호 생략
    public String new_name;
    public String new_roadname;
    public String new_bunji;
    public String new_ho;

    public void print() {
        System.out.println(old_name);
        System.out.println(old_bunji);
        System.out.println(new_name);
        System.out.println(new_bunji);
    }

}
