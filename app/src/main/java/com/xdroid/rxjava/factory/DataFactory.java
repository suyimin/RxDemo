package com.xdroid.rxjava.factory;

import com.xdroid.rxjava.model.Course;
import com.xdroid.rxjava.model.Student;

import java.util.ArrayList;

/**
 * 类描述:获取学生集合
 */
public class DataFactory {

    private DataFactory() {
    }

    public static ArrayList<Student> getData() {

        ArrayList<Student> students = new ArrayList<>();

        ArrayList<Course> courses;
        Student student;
        Course course;
        for (int i = 0; i < 3; i++) {

            courses = new ArrayList<>();

            student = new Student();
            student.id = i;
            student.name = new StringBuffer("学生").append((i + 1)).toString();

            for (int j = 0; j < 2; j++) {
                course = new Course();
                course.id = j;
                course.name = new StringBuffer(student.name).append("的课程").append((j + 1)).toString();
                courses.add(course);
            }

            student.courses = courses;
            students.add(student);

        }
        return students;
    }
}
