package com.schoolmanagement.services;

import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.schoolmanagement.models.Student;
import com.schoolmanagement.models.Course;
import com.schoolmanagement.dao.StudentI;
import com.schoolmanagement.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods, and
 * provides implementation for each method. Lombok @Log is used to
 * generate a logger file.
 */
@Log
public class StudentService implements StudentI {

    @Override
    public void createStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.severe("Error saving student: " + e.getMessage());
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Student", Student.class).list();
        } catch (HibernateException e) {
            log.severe("Error retrieving students: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, email);
        } catch (HibernateException e) {
            log.severe("Error retrieving student: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateStudent(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("FROM Student WHERE email = :email AND password = :password", Student.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.uniqueResult() != null;
        } catch (HibernateException e) {
            log.severe("Error validating student: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Student student = session.get(Student.class, email);
            Course course = session.get(Course.class, courseId);

            if (student != null && course != null) {
                student.getCourses().add(course);
                session.merge(student);
            }

            tx.commit();
            session.clear();
        }
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        List<Course> courses;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Force reload of student entity to get updated courses
            Student student = session.get(Student.class, email);
            session.refresh(student);

            courses = new ArrayList<>(student.getCourses());

            tx.commit();
        }
        return courses;
    }
}