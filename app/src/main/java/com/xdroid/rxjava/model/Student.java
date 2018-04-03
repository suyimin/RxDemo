package com.xdroid.rxjava.model;

import java.util.ArrayList;

/**
 * 类描述:学生类
 */
public class Student {

    public int id;//学号
    public String name;//姓名

    public ArrayList<Course> courses;//学生选修的课程

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courses=" + courses +
                '}';
    }
}
