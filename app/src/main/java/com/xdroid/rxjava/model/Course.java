package com.xdroid.rxjava.model;

/**
 * 类描述:课程类
 */
public class Course {

    public int id;//课程编号
    public String name;//课程名称

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
