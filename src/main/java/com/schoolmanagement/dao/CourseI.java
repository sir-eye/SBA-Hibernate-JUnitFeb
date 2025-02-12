package com.schoolmanagement.dao;

import com.schoolmanagement.models.Course;
import java.util.List;

public interface CourseI {
    void createCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(int courseId);
}