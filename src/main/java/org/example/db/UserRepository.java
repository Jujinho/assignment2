/**
 * @author Group 1
 */
package org.example.db;

import org.example.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for User entities.
 * Handles database operations for users (including students, academic staff, professional staff, and administrators).
 */
public class UserRepository {

    /**
     * Authenticate a user by username and password
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     *
     * @param username The username
     * @param password The password
     * @return An Optional containing the authenticated User if successful, or empty if authentication fails
     */
    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.username = ? AND u.password = ?"; // In a real application, passwords should be hashed

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    switch (userType) {
                        case "Student":
                            return Optional.of(extractStudentFromResultSet(rs));
                        case "AcademicStaff":
                            return Optional.of(extractAcademicStaffFromResultSet(rs));
                        case "ProfessionalStaff":
                            return Optional.of(extractProfessionalStaffFromResultSet(rs));
                        case "Administrator":
                            return Optional.of(extractAdministratorFromResultSet(rs));
                        default:
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get a user by ID
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     *
     * @param id The user ID
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserById(int id) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    switch (userType) {
                        case "Student":
                            return Optional.of(extractStudentFromResultSet(rs));
                        case "AcademicStaff":
                            return Optional.of(extractAcademicStaffFromResultSet(rs));
                        case "ProfessionalStaff":
                            return Optional.of(extractProfessionalStaffFromResultSet(rs));
                        case "Administrator":
                            return Optional.of(extractAdministratorFromResultSet(rs));
                        default:
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get a user by username
     * This optimized version uses a LEFT JOIN to fetch all user data in a single query
     *
     * @param username The username
     * @return An Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT u.*, " +
                "s.student_id, s.major, s.year, " +
                "a_staff.staff_id as academic_staff_id, a_staff.department as academic_department, a_staff.position as academic_position, " +
                "p_staff.staff_id as professional_staff_id, p_staff.department as professional_department, " +
                "p_staff.position as professional_position, p_staff.specialization, " +
                "admin.admin_id, admin.department as admin_department, admin.position as admin_position, admin.access_level " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id AND u.user_type = 'Student' " +
                "LEFT JOIN academic_staff a_staff ON u.id = a_staff.user_id AND u.user_type = 'AcademicStaff' " +
                "LEFT JOIN professional_staff p_staff ON u.id = p_staff.user_id AND u.user_type = 'ProfessionalStaff' " +
                "LEFT JOIN administrators admin ON u.id = admin.user_id AND u.user_type = 'Administrator' " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    switch (userType) {
                        case "Student":
                            return Optional.of(extractStudentFromResultSet(rs));
                        case "AcademicStaff":
                            return Optional.of(extractAcademicStaffFromResultSet(rs));
                        case "ProfessionalStaff":
                            return Optional.of(extractProfessionalStaffFromResultSet(rs));
                        case "Administrator":
                            return Optional.of(extractAdministratorFromResultSet(rs));
                        default:
                            return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Get all users
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                getUserByType(rs).ifPresent(users::add);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Get all students
     *
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.*, s.student_id, s.major, s.year " +
                "FROM users u " +
                "JOIN students s ON u.id = s.user_id " +
                "WHERE u.user_type = 'Student'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
        }

        return students;
    }

    /**
     * Get all academic staff
     *
     * @return A list of all academic staff
     */
    public List<AcademicStaff> getAllAcademicStaff() {
        List<AcademicStaff> academicStaff = new ArrayList<>();
        String sql = "SELECT u.*, a.staff_id as academic_staff_id, a.department as academic_department, a.position as academic_position " +
                "FROM users u " +
                "JOIN academic_staff a ON u.id = a.user_id " +
                "WHERE u.user_type = 'AcademicStaff'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                academicStaff.add(extractAcademicStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all academic staff: " + e.getMessage());
        }

        return academicStaff;
    }

    /**
     * Get all professional staff
     *
     * @return A list of all professional staff
     */
    public List<ProfessionalStaff> getAllProfessionalStaff() {
        List<ProfessionalStaff> professionalStaff = new ArrayList<>();
        String sql = "SELECT u.*, p.staff_id as professional_staff_id, p.department as professional_department, p.position as professional_position, p.specialization " +
                "FROM users u " +
                "JOIN professional_staff p ON u.id = p.user_id " +
                "WHERE u.user_type = 'ProfessionalStaff'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                professionalStaff.add(extractProfessionalStaffFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all professional staff: " + e.getMessage());
        }

        return professionalStaff;
    }

    /**
     * Get all administrators
     *
     * @return A list of all administrators
     */
    public List<Administrator> getAllAdministrators() {
        List<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT u.*, a.admin_id, a.department as admin_department, a.position as admin_position, a.access_level " +
                "FROM users u " +
                "JOIN administrators a ON u.id = a.user_id " +
                "WHERE u.user_type = 'Administrator'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                administrators.add(extractAdministratorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all administrators: " + e.getMessage());
        }

        return administrators;
    }

    /**
     * Create a new user
     *
     * @param user The user to create
     * @return true if the user was created successfully, false otherwise
     */
    public boolean createUser(User user) {
        if (user instanceof Student) {
            return createStudent((Student) user);
        } else if (user instanceof AcademicStaff) {
            return createAcademicStaff((AcademicStaff) user);
        } else if (user instanceof ProfessionalStaff) {
            return createProfessionalStaff((ProfessionalStaff) user);
        } else if (user instanceof Administrator) {
            return createAdministrator((Administrator) user);
        }
        return false;
    }

    /**
     * Update a user
     *
     * @param user The user to update
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        if (user instanceof Student) {
            return updateStudent((Student) user);
        } else if (user instanceof AcademicStaff) {
            return updateAcademicStaff((AcademicStaff) user);
        } else if (user instanceof ProfessionalStaff) {
            return updateProfessionalStaff((ProfessionalStaff) user);
        } else if (user instanceof Administrator) {
            return updateAdministrator((Administrator) user);
        }
        return false;
    }

    /**
     * Delete a user
     *
     * @param id The ID of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Helper methods

    /**
     * Get a user by type from a ResultSet
     * This optimized version avoids making additional database queries by using JOIN in the original query
     *
     * @param rs The ResultSet containing user data
     * @return An Optional containing the User if found, or empty if not found
     * @throws SQLException If a database access error occurs
     */
    private Optional<User> getUserByType(ResultSet rs) throws SQLException {
        String userType = rs.getString("user_type");

        try {
            switch (userType) {
                case "Student":
                    // Check if student-specific columns exist in the result set
                    try {
                        rs.findColumn("student_id");
                        return Optional.of(extractStudentFromResultSet(rs));
                    } catch (SQLException e) {
                        // If columns don't exist, fall back to separate query
                        return Optional.ofNullable(getStudentById(rs.getInt("id")));
                    }
                case "AcademicStaff":
                    try {
                        rs.findColumn("academic_staff_id");
                        return Optional.of(extractAcademicStaffFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getAcademicStaffById(rs.getInt("id")));
                    }
                case "ProfessionalStaff":
                    try {
                        rs.findColumn("professional_staff_id");
                        rs.findColumn("specialization");
                        return Optional.of(extractProfessionalStaffFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getProfessionalStaffById(rs.getInt("id")));
                    }
                case "Administrator":
                    try {
                        rs.findColumn("admin_id");
                        return Optional.of(extractAdministratorFromResultSet(rs));
                    } catch (SQLException e) {
                        return Optional.ofNullable(getAdministratorById(rs.getInt("id")));
                    }
                default:
                    return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error extracting user from ResultSet: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get a student by ID
     *
     * @param id The student ID
     * @return The Student if found, or null if not found
     */
    private Student getStudentById(int id) {
        String sql = "SELECT u.*, s.student_id, s.major, s.year " +
                "FROM users u " +
                "JOIN students s ON u.id = s.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get an academic staff by ID
     *
     * @param id The academic staff ID
     * @return The AcademicStaff if found, or null if not found
     */
    private AcademicStaff getAcademicStaffById(int id) {
        String sql = "SELECT u.*, a.staff_id as academic_staff_id, a.department as academic_department, a.position as academic_position " +
                "FROM users u " +
                "JOIN academic_staff a ON u.id = a.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAcademicStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting academic staff by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get a professional staff by ID
     *
     * @param id The professional staff ID
     * @return The ProfessionalStaff if found, or null if not found
     */
    private ProfessionalStaff getProfessionalStaffById(int id) {
        String sql = "SELECT u.*, p.staff_id as professional_staff_id, p.department as professional_department, p.position as professional_position, p.specialization " +
                "FROM users u " +
                "JOIN professional_staff p ON u.id = p.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProfessionalStaffFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting professional staff by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get an administrator by ID
     *
     * @param id The administrator ID
     * @return The Administrator if found, or null if not found
     */
    private Administrator getAdministratorById(int id) {
        String sql = "SELECT u.*, a.admin_id, a.department as admin_department, a.position as admin_position, a.access_level " +
                "FROM users u " +
                "JOIN administrators a ON u.id = a.user_id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdministratorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting administrator by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Extract a student from a ResultSet
     *
     * @param rs The ResultSet containing student data
     * @return The extracted Student
     * @throws SQLException If a database access error occurs
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String studentId = rs.getString("student_id");
        String major = rs.getString("major");
        int year = rs.getInt("year");

        Student student = new Student(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                studentId, major, year
        );

        return student;
    }

    /**
     * Extract an academic staff from a ResultSet
     *
     * @param rs The ResultSet containing academic staff data
     * @return The extracted AcademicStaff
     * @throws SQLException If a database access error occurs
     */
    private AcademicStaff extractAcademicStaffFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String staffId = rs.getString("academic_staff_id");
        String department = rs.getString("academic_department");
        String position = rs.getString("academic_position");

        AcademicStaff academicStaff = new AcademicStaff(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                staffId, department, position
        );

        return academicStaff;
    }

    /**
     * Extract a professional staff from a ResultSet
     *
     * @param rs The ResultSet containing professional staff data
     * @return The extracted ProfessionalStaff
     * @throws SQLException If a database access error occurs
     */
    private ProfessionalStaff extractProfessionalStaffFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String staffId = rs.getString("professional_staff_id");
        String department = rs.getString("professional_department");
        String position = rs.getString("professional_position");
        String specialization = rs.getString("specialization");

        ProfessionalStaff professionalStaff = new ProfessionalStaff(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                staffId, department, position, specialization
        );

        return professionalStaff;
    }

    /**
     * Extract an administrator from a ResultSet
     *
     * @param rs The ResultSet containing administrator data
     * @return The extracted Administrator
     * @throws SQLException If a database access error occurs
     */
    private Administrator extractAdministratorFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        Date dateOfBirth = rs.getDate("date_of_birth");
        String address = rs.getString("address");
        String adminId = rs.getString("admin_id");
        String department = rs.getString("admin_department");
        String position = rs.getString("admin_position");
        String accessLevel = rs.getString("access_level");

        Administrator administrator = new Administrator(
                id, username, password, firstName, lastName, email, phoneNumber,
                dateOfBirth != null ? dateOfBirth.toLocalDate() : null, address,
                adminId, department, position, accessLevel
        );

        return administrator;
    }

    // Placeholder methods for CRUD operations
    // These would be implemented with actual database operations in a real application

    private boolean createStudent(Student student) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean createAcademicStaff(AcademicStaff academicStaff) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean createProfessionalStaff(ProfessionalStaff professionalStaff) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean createAdministrator(Administrator administrator) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean updateStudent(Student student) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean updateAcademicStaff(AcademicStaff academicStaff) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean updateProfessionalStaff(ProfessionalStaff professionalStaff) {
        // Implementation omitted for brevity
        return true;
    }

    private boolean updateAdministrator(Administrator administrator) {
        // Implementation omitted for brevity
        return true;
    }
}
