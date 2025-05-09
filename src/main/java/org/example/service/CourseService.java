/**
 * @author Group 1
 */
package org.example.service;

import org.example.db.CourseRepository;
import org.example.model.AcademicStaff;
import org.example.model.Course;
import org.example.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * Service class for course-related operations.
 */
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    /**
     * Constructor
     *
     * @param courseRepository The CourseRepository to use for course operations
     */
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    /**
     * Get a course by ID
     *
     * @param id The course ID
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseById(int id) {
        return courseRepository.getCourseById(id);
    }
    
    /**
     * Get a course by course code
     *
     * @param courseCode The course code
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.getCourseByCourseCode(courseCode);
    }
    
    /**
     * Get all courses
     *
     * @return A list of all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.getAllCourses();
    }
    
    /**
     * Get courses by instructor
     *
     * @param instructorId The ID of the instructor (academic staff)
     * @return A list of courses taught by the instructor
     */
    public List<Course> getCoursesByInstructor(int instructorId) {
        return courseRepository.getCoursesByInstructor(instructorId);
    }
    
    /**
     * Get courses by student
     *
     * @param studentId The ID of the student
     * @return A list of courses in which the student is enrolled
     */
    public List<Course> getCoursesByStudent(int studentId) {
        return courseRepository.getCoursesByStudent(studentId);
    }
    
    /**
     * Create a new course
     *
     * @param course The course to create
     * @return true if the course was created successfully, false otherwise
     */
    public boolean createCourse(Course course) {
        return courseRepository.createCourse(course);
    }
    
    /**
     * Update a course
     *
     * @param course The course to update
     * @return true if the course was updated successfully, false otherwise
     */
    public boolean updateCourse(Course course) {
        return courseRepository.updateCourse(course);
    }
    
    /**
     * Delete a course
     *
     * @param id The ID of the course to delete
     * @return true if the course was deleted successfully, false otherwise
     */
    public boolean deleteCourse(int id) {
        return courseRepository.deleteCourse(id);
    }
    
    /**
     * Enroll a student in a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the enrollment was successful, false otherwise
     */
    public boolean enrollStudent(int studentId, int courseId) {
        return courseRepository.enrollStudent(studentId, courseId);
    }
    
    /**
     * Remove a student from a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the removal was successful, false otherwise
     */
    public boolean removeStudent(int studentId, int courseId) {
        return courseRepository.removeStudent(studentId, courseId);
    }
    
    /**
     * Get students enrolled in a course
     *
     * @param courseId The ID of the course
     * @return A list of students enrolled in the course
     */
    public List<Student> getStudentsInCourse(int courseId) {
        return courseRepository.getStudentsInCourse(courseId);
    }
    
    /**
     * Assign an instructor to a course
     *
     * @param courseId     The ID of the course
     * @param instructorId The ID of the instructor (academic staff)
     * @return true if the assignment was successful, false otherwise
     */
    public boolean assignInstructor(int courseId, int instructorId) {
        return courseRepository.assignInstructor(courseId, instructorId);
    }
    
    /**
     * Check if a course code is available
     *
     * @param courseCode The course code to check
     * @return true if the course code is available, false otherwise
     */
    public boolean isCourseCodeAvailable(String courseCode) {
        return !courseRepository.getCourseByCourseCode(courseCode).isPresent();
    }
    
    /**
     * Check if a student is enrolled in a course
     *
     * @param studentId The ID of the student
     * @param courseId  The ID of the course
     * @return true if the student is enrolled in the course, false otherwise
     */
    public boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            return false;
        }
        
        List<Student> students = courseRepository.getStudentsInCourse(courseId);
        return students.stream().anyMatch(student -> student.getId() == studentId);
    }
    
    /**
     * Check if an academic staff is the instructor of a course
     *
     * @param academicStaffId The ID of the academic staff
     * @param courseId        The ID of the course
     * @return true if the academic staff is the instructor of the course, false otherwise
     */
    public boolean isInstructorOfCourse(int academicStaffId, int courseId) {
        Optional<Course> courseOpt = courseRepository.getCourseById(courseId);
        if (!courseOpt.isPresent()) {
            return false;
        }
        
        Course course = courseOpt.get();
        AcademicStaff instructor = course.getInstructor();
        return instructor != null && instructor.getId() == academicStaffId;
    }
}