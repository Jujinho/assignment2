/**
 * @author Group 1
 */
package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.*;
import org.example.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Controller for the administrator view.
 */
public class AdministratorController {

    // Services
    private AuthenticationService authenticationService;
    private UserService userService;
    private EquipmentService equipmentService;
    private LendingService lendingService;
    private CourseService courseService;

    // Current administrator
    private Administrator currentAdmin;

    // Timer for auto-refresh
    private Timer refreshTimer;

    // Personal Information Tab
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField addressField;
    @FXML
    private TextField adminIdField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField accessLevelField;
    @FXML
    private Button updateInfoButton;
    @FXML
    private Button changePasswordButton;

    // Manage Users Tab
    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private TextField searchUserField;
    @FXML
    private Button searchUserButton;
    @FXML
    private Button addUserButton;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TableColumn<User, String> userDepartmentColumn;
    @FXML
    private TableColumn<User, Button> userEditColumn;
    @FXML
    private TableColumn<User, Button> userDeleteColumn;

    // Manage Courses Tab
    @FXML
    private TextField searchCourseField;
    @FXML
    private Button searchCourseButton;
    @FXML
    private Button addCourseButton;
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, Integer> courseIdColumn;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, Integer> courseSemesterColumn;
    @FXML
    private TableColumn<Course, Integer> courseYearColumn;
    @FXML
    private TableColumn<Course, String> courseInstructorColumn;
    @FXML
    private TableColumn<Course, Button> courseEditColumn;
    @FXML
    private TableColumn<Course, Button> courseDeleteColumn;

    // Manage Equipment Tab
    @FXML
    private TextField searchEquipmentField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private Button searchEquipmentButton;
    @FXML
    private Button addEquipmentButton;
    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableColumn<Equipment, Integer> eqIdColumn;
    @FXML
    private TableColumn<Equipment, String> eqNameColumn;
    @FXML
    private TableColumn<Equipment, String> eqCategoryColumn;
    @FXML
    private TableColumn<Equipment, String> eqConditionColumn;
    @FXML
    private TableColumn<Equipment, Boolean> eqAvailableColumn;
    @FXML
    private TableColumn<Equipment, Button> eqImagesColumn;
    @FXML
    private TableColumn<Equipment, Button> eqEditColumn;
    @FXML
    private TableColumn<Equipment, Button> eqDeleteColumn;

    // Manage Lending Tab
    @FXML
    private ComboBox<String> lendingStatusComboBox;
    @FXML
    private TextField borrowerField;
    @FXML
    private Button searchLendingButton;
    @FXML
    private Button createLendingButton;
    @FXML
    private TableView<LendingRecord> lendingTable;
    @FXML
    private TableColumn<LendingRecord, Integer> lendingIdColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingBorrowerColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingEquipmentColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingBorrowDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingDueDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> lendingStatusColumn;
    @FXML
    private TableColumn<LendingRecord, Button> lendingActionColumn;

    // System Statistics Tab
    @FXML
    private DatePicker statsFromDatePicker;
    @FXML
    private DatePicker statsToDatePicker;
    @FXML
    private Button refreshStatsButton;
    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> equipmentBarChart;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label totalEquipmentLabel;
    @FXML
    private Label totalLendingsLabel;
    @FXML
    private Label overdueRateLabel;

    // Bottom
    @FXML
    private Button logoutButton;

    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Get services
        authenticationService = Main.getAuthenticationService();
        userService = Main.getUserService();
        equipmentService = Main.getEquipmentService();
        lendingService = Main.getLendingService();
        courseService = Main.getCourseService();

        // Get current administrator
        if (authenticationService.isAdministrator()) {
            currentAdmin = authenticationService.getCurrentAdministrator();
            welcomeLabel.setText("Welcome, " + currentAdmin.getFullName());

            // Initialize personal information
            initializePersonalInfo();

            // Initialize other tabs
            initializeUserManagement();
            initializeCourseManagement();
            initializeEquipmentManagement();
            initializeLendingManagement();
            initializeStatistics();

            // Set up auto-refresh timer (refresh every 10 seconds)
            refreshTimer = new Timer(true); // true makes it a daemon timer
            refreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Run on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        loadLendingRecords();
                    });
                }
            }, 10000, 10000); // 10 seconds delay, 10 seconds period
        } else {
            showAlert("Error", "Not logged in as administrator", Alert.AlertType.ERROR);
        }
    }

    /**
     * Initialize personal information tab
     */
    private void initializePersonalInfo() {
        // Set personal information fields
        firstNameField.setText(currentAdmin.getFirstName());
        lastNameField.setText(currentAdmin.getLastName());
        emailField.setText(currentAdmin.getEmail());
        phoneField.setText(currentAdmin.getPhoneNumber());
        if (currentAdmin.getDateOfBirth() != null) {
            dobPicker.setValue(currentAdmin.getDateOfBirth());
        }
        addressField.setText(currentAdmin.getAddress());
        adminIdField.setText(currentAdmin.getAdminId());
        departmentField.setText(currentAdmin.getDepartment());
        positionField.setText(currentAdmin.getPosition());
        accessLevelField.setText(currentAdmin.getAccessLevel());
    }

    /**
     * Initialize user management tab
     */
    private void initializeUserManagement() {
        // Set up user type combo box
        userTypeComboBox.setItems(FXCollections.observableArrayList(
                "All", "Student", "Academic Staff", "Professional Staff", "Administrator"));
        userTypeComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        userNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getFullName()));

        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userDepartmentColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String department = "";

            if (user instanceof AcademicStaff) {
                department = ((AcademicStaff) user).getDepartment();
            } else if (user instanceof ProfessionalStaff) {
                department = ((ProfessionalStaff) user).getDepartment();
            } else if (user instanceof Administrator) {
                department = ((Administrator) user).getDepartment();
            }

            return new SimpleStringProperty(department);
        });

        // Set up edit column
        userEditColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                User user = getTableView().getItems().get(getIndex());
                editButton.setOnAction(event -> handleEditUser(user));
                setGraphic(editButton);
            }
        });

        // Set up delete column
        userDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                User user = getTableView().getItems().get(getIndex());
                deleteButton.setOnAction(event -> handleDeleteUser(user));
                setGraphic(deleteButton);
            }
        });

        // Load users
        loadUsers();
    }

    /**
     * Initialize course management tab
     */
    private void initializeCourseManagement() {
        // Set up table columns
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        courseInstructorColumn.setCellValueFactory(cellData -> {
            AcademicStaff instructor = cellData.getValue().getInstructor();
            return new SimpleStringProperty(instructor != null ? instructor.getFullName() : "None");
        });

        // Set up edit column
        courseEditColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Course course = getTableView().getItems().get(getIndex());
                editButton.setOnAction(event -> handleEditCourse(course));
                setGraphic(editButton);
            }
        });

        // Set up delete column
        courseDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Course course = getTableView().getItems().get(getIndex());
                deleteButton.setOnAction(event -> handleDeleteCourse(course));
                setGraphic(deleteButton);
            }
        });

        // Load courses
        loadCourses();
    }

    /**
     * Initialize equipment management tab
     */
    private void initializeEquipmentManagement() {
        // Set up category combo box
        List<String> categories = equipmentService.getAllCategories();
        categories.add(0, "All");
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        categoryComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        eqIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        eqNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eqCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        eqConditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        eqAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Set up images column
        eqImagesColumn.setCellFactory(param -> new TableCell<>() {
            private final Button imagesButton = new Button("Images");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                imagesButton.setOnAction(event -> handleManageEquipmentImages(equipment));
                setGraphic(imagesButton);
            }
        });

        // Set up edit column
        eqEditColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                editButton.setOnAction(event -> handleEditEquipment(equipment));
                setGraphic(editButton);
            }
        });

        // Set up delete column
        eqDeleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                deleteButton.setOnAction(event -> handleDeleteEquipment(equipment));
                setGraphic(deleteButton);
            }
        });

        // Load equipment
        loadEquipment();
    }

    /**
     * Initialize lending management tab
     */
    private void initializeLendingManagement() {
        // Set up status combo box
        lendingStatusComboBox.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        lendingStatusComboBox.getSelectionModel().selectFirst();

        // Set up table columns
        lendingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        lendingBorrowerColumn.setCellValueFactory(cellData -> {
            User borrower = cellData.getValue().getBorrower();
            return new SimpleStringProperty(borrower != null ? borrower.getFullName() : "");
        });

        lendingEquipmentColumn.setCellValueFactory(cellData -> {
            Equipment equipment = cellData.getValue().getEquipment();
            return new SimpleStringProperty(equipment != null ? equipment.getName() : "");
        });

        lendingBorrowDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getBorrowDate().format(dateFormatter)));

        lendingDueDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate().format(dateFormatter)));

        lendingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up action column
        lendingActionColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                actionBox.getChildren().addAll(viewButton, editButton, deleteButton);
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LendingRecord record = getTableView().getItems().get(getIndex());
                viewButton.setOnAction(event -> handleViewLendingRecord(record));
                editButton.setOnAction(event -> handleEditLendingRecord(record));
                deleteButton.setOnAction(event -> handleDeleteLendingRecord(record));
                setGraphic(actionBox);
            }
        });

        // Load lending records
        loadLendingRecords();
    }

    /**
     * Initialize statistics tab
     */
    private void initializeStatistics() {
        // Set up date pickers
        statsFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        statsToDatePicker.setValue(LocalDate.now());

        // Load statistics
        loadStatistics();
    }

    /**
     * Handle update information button click
     *
     * @param event The action event
     */
    @FXML
    public void handleUpdateInfo(ActionEvent event) {
        // Update admin information
        currentAdmin.setFirstName(firstNameField.getText());
        currentAdmin.setLastName(lastNameField.getText());
        currentAdmin.setEmail(emailField.getText());
        currentAdmin.setPhoneNumber(phoneField.getText());
        currentAdmin.setDateOfBirth(dobPicker.getValue());
        currentAdmin.setAddress(addressField.getText());

        // Save changes
        boolean success = userService.updatePersonalInfo(currentAdmin);
        if (success) {
            showAlert("Success", "Personal information updated successfully", Alert.AlertType.INFORMATION);
            welcomeLabel.setText("Welcome, " + currentAdmin.getFullName());
        } else {
            showAlert("Error", "Failed to update personal information", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle change password button click
     *
     * @param event The action event
     */
    @FXML
    public void handleChangePassword(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Change password functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle search user button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchUser(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Search user functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle add user button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddUser(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Add user functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle search course button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchCourse(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Search course functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle add course button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddCourse(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Add course functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle search equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchEquipment(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Search equipment functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle add equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleAddEquipment(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Add equipment functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle search lending button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchLending(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Search lending functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle create lending button click
     *
     * @param event The action event
     */
    @FXML
    public void handleCreateLending(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Create lending functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle refresh statistics button click
     *
     * @param event The action event
     */
    @FXML
    public void handleRefreshStats(ActionEvent event) {
        // Implementation would go here
        showAlert("Info", "Refresh statistics functionality not implemented yet", Alert.AlertType.INFORMATION);
    }

    /**
     * Handle logout button click
     *
     * @param event The action event
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        // Log out the user
        authenticationService.logout();

        try {
            // Navigate back to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Set up the scene
            Scene scene = new Scene(root, 800, 600);

            // Set up the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Error loading login view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Load users
     */
    private void loadUsers() {
        List<User> users = userService.getAllUsers();

        // Filter by user type if needed
        String userType = userTypeComboBox.getValue();
        if (userType != null && !userType.equals("All")) {
            users = users.stream()
                    .filter(user -> user.getRole().equals(userType))
                    .collect(Collectors.toList());
        }

        // Filter by search term if provided
        String searchTerm = searchUserField.getText();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchTermLower = searchTerm.toLowerCase();
            users = users.stream()
                    .filter(user -> 
                            user.getFullName().toLowerCase().contains(searchTermLower) ||
                            String.valueOf(user.getId()).contains(searchTermLower))
                    .collect(Collectors.toList());
        }

        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Handle edit user
     *
     * @param user The user to edit
     */
    private void handleEditUser(User user) {
        // Create a dialog for editing user
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user information for " + user.getFullName());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField emailField = new TextField(user.getEmail());
        TextField phoneField = new TextField(user.getPhoneNumber());
        DatePicker dobPicker = new DatePicker(user.getDateOfBirth());
        TextField addressField = new TextField(user.getAddress());

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Date of Birth:"), 0, 4);
        grid.add(dobPicker, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a user when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setPhoneNumber(phoneField.getText());
                user.setDateOfBirth(dobPicker.getValue());
                user.setAddress(addressField.getText());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            boolean success = userService.updateUser(updatedUser);
            if (success) {
                showAlert("Success", "User updated successfully", Alert.AlertType.INFORMATION);
                loadUsers(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update user", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete user
     *
     * @param user The user to delete
     */
    private void handleDeleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user " + user.getFullName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userService.deleteUser(user.getId());
            if (success) {
                showAlert("Success", "User deleted successfully", Alert.AlertType.INFORMATION);
                loadUsers(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete user", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load courses
     */
    private void loadCourses() {
        List<Course> courses = courseService.getAllCourses();

        // Filter by search term if provided
        String searchTerm = searchCourseField.getText();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchTermLower = searchTerm.toLowerCase();
            courses = courses.stream()
                    .filter(course -> 
                            course.getCourseCode().toLowerCase().contains(searchTermLower) ||
                            course.getCourseName().toLowerCase().contains(searchTermLower))
                    .collect(Collectors.toList());
        }

        coursesTable.setItems(FXCollections.observableArrayList(courses));
    }

    /**
     * Handle edit course
     *
     * @param course The course to edit
     */
    private void handleEditCourse(Course course) {
        // Create a dialog for editing course
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Edit course information for " + course.getCourseCode());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField courseCodeField = new TextField(course.getCourseCode());
        TextField courseNameField = new TextField(course.getCourseName());
        TextField descriptionField = new TextField(course.getDescription());
        TextField semesterField = new TextField(String.valueOf(course.getSemester()));
        TextField yearField = new TextField(String.valueOf(course.getYear()));

        // Get all academic staff for instructor selection
        List<AcademicStaff> academicStaffList = userService.getAllAcademicStaff();
        ComboBox<AcademicStaff> instructorComboBox = new ComboBox<>();
        instructorComboBox.setItems(FXCollections.observableArrayList(academicStaffList));
        instructorComboBox.setCellFactory(param -> new ListCell<AcademicStaff>() {
            @Override
            protected void updateItem(AcademicStaff item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
        instructorComboBox.setButtonCell(new ListCell<AcademicStaff>() {
            @Override
            protected void updateItem(AcademicStaff item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });

        // Set the current instructor if available
        if (course.getInstructor() != null) {
            for (AcademicStaff staff : academicStaffList) {
                if (staff.getId() == course.getInstructor().getId()) {
                    instructorComboBox.getSelectionModel().select(staff);
                    break;
                }
            }
        }

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(courseCodeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(courseNameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Semester:"), 0, 3);
        grid.add(semesterField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Instructor:"), 0, 5);
        grid.add(instructorComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a course when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    course.setCourseCode(courseCodeField.getText());
                    course.setCourseName(courseNameField.getText());
                    course.setDescription(descriptionField.getText());
                    course.setSemester(Integer.parseInt(semesterField.getText()));
                    course.setYear(Integer.parseInt(yearField.getText()));
                    course.setInstructor(instructorComboBox.getValue());
                    return course;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Semester and year must be numbers", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Course> result = dialog.showAndWait();

        result.ifPresent(updatedCourse -> {
            boolean success = courseService.updateCourse(updatedCourse);
            if (success) {
                showAlert("Success", "Course updated successfully", Alert.AlertType.INFORMATION);
                loadCourses(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update course", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete course
     *
     * @param course The course to delete
     */
    private void handleDeleteCourse(Course course) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Course");
        confirmAlert.setContentText("Are you sure you want to delete course " + course.getCourseCode() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = courseService.deleteCourse(course.getId());
            if (success) {
                showAlert("Success", "Course deleted successfully", Alert.AlertType.INFORMATION);
                loadCourses(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete course", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load equipment
     */
    private void loadEquipment() {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();

        // Filter by search term if provided
        String searchTerm = searchEquipmentField.getText();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchTermLower = searchTerm.toLowerCase();
            equipmentList = equipmentList.stream()
                    .filter(equipment -> 
                            equipment.getName().toLowerCase().contains(searchTermLower))
                    .collect(Collectors.toList());
        }

        // Filter by category if needed
        String category = categoryComboBox.getValue();
        if (category != null && !category.equals("All")) {
            equipmentList = equipmentList.stream()
                    .filter(equipment -> equipment.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        equipmentTable.setItems(FXCollections.observableArrayList(equipmentList));
    }

    /**
     * Handle manage equipment images
     *
     * @param equipment The equipment to manage images for
     */
    private void handleManageEquipmentImages(Equipment equipment) {
        // Create a dialog for managing equipment images
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Images");
        dialog.setHeaderText("Manage images for " + equipment.getName());

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add Image", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CLOSE);

        // Create a list view for images
        ListView<HBox> imagesListView = new ListView<>();
        imagesListView.setPrefWidth(400);
        imagesListView.setPrefHeight(300);

        // Load images
        List<byte[]> images = equipmentService.getEquipmentImages(equipment.getId());

        // Add images to list view
        for (int i = 0; i < images.size(); i++) {
            byte[] imageData = images.get(i);

            // Create image view
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

            // Convert byte array to image
            javafx.scene.image.Image image = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(imageData));
            imageView.setImage(image);

            // Create delete button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

            final int imageIndex = i;
            deleteButton.setOnAction(event -> {
                boolean success = equipmentService.deleteEquipmentImage(imageIndex);
                if (success) {
                    showAlert("Success", "Image deleted successfully", Alert.AlertType.INFORMATION);
                    handleManageEquipmentImages(equipment); // Refresh the dialog
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to delete image", Alert.AlertType.ERROR);
                }
            });

            // Create HBox for image and button
            HBox imageBox = new HBox(10);
            imageBox.getChildren().addAll(imageView, deleteButton);

            imagesListView.getItems().add(imageBox);
        }

        // Set the content
        dialog.getDialogPane().setContent(imagesListView);

        // Handle add image button
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume(); // Prevent dialog from closing

            // Create file chooser
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            // Show file chooser
            java.io.File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                boolean success = equipmentService.addEquipmentImage(equipment.getId(), file);
                if (success) {
                    showAlert("Success", "Image added successfully", Alert.AlertType.INFORMATION);
                    handleManageEquipmentImages(equipment); // Refresh the dialog
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to add image", Alert.AlertType.ERROR);
                }
            }
        });

        dialog.showAndWait();
    }

    /**
     * Handle edit equipment
     *
     * @param equipment The equipment to edit
     */
    private void handleEditEquipment(Equipment equipment) {
        // Create a dialog for editing equipment
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Edit Equipment");
        dialog.setHeaderText("Edit equipment information for " + equipment.getName());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        TextField nameField = new TextField(equipment.getName());
        TextField descriptionField = new TextField(equipment.getDescription());
        TextField categoryField = new TextField(equipment.getCategory());
        TextField conditionField = new TextField(equipment.getCondition());
        DatePicker purchaseDatePicker = new DatePicker(equipment.getPurchaseDate());
        TextField purchasePriceField = new TextField(String.valueOf(equipment.getPurchasePrice()));
        TextField manufacturerField = new TextField(equipment.getManufacturer());
        TextField modelField = new TextField(equipment.getModel());
        TextField serialNumberField = new TextField(equipment.getSerialNumber());
        TextField locationField = new TextField(equipment.getLocation());
        CheckBox availableCheckBox = new CheckBox();
        availableCheckBox.setSelected(equipment.isAvailable());

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionField, 1, 3);
        grid.add(new Label("Purchase Date:"), 0, 4);
        grid.add(purchaseDatePicker, 1, 4);
        grid.add(new Label("Purchase Price:"), 0, 5);
        grid.add(purchasePriceField, 1, 5);
        grid.add(new Label("Manufacturer:"), 0, 6);
        grid.add(manufacturerField, 1, 6);
        grid.add(new Label("Model:"), 0, 7);
        grid.add(modelField, 1, 7);
        grid.add(new Label("Serial Number:"), 0, 8);
        grid.add(serialNumberField, 1, 8);
        grid.add(new Label("Location:"), 0, 9);
        grid.add(locationField, 1, 9);
        grid.add(new Label("Available:"), 0, 10);
        grid.add(availableCheckBox, 1, 10);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an equipment when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    equipment.setName(nameField.getText());
                    equipment.setDescription(descriptionField.getText());
                    equipment.setCategory(categoryField.getText());
                    equipment.setCondition(conditionField.getText());
                    equipment.setPurchaseDate(purchaseDatePicker.getValue());
                    equipment.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
                    equipment.setManufacturer(manufacturerField.getText());
                    equipment.setModel(modelField.getText());
                    equipment.setSerialNumber(serialNumberField.getText());
                    equipment.setLocation(locationField.getText());
                    equipment.setAvailable(availableCheckBox.isSelected());
                    return equipment;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Purchase price must be a number", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Equipment> result = dialog.showAndWait();

        result.ifPresent(updatedEquipment -> {
            boolean success = equipmentService.updateEquipment(updatedEquipment);
            if (success) {
                showAlert("Success", "Equipment updated successfully", Alert.AlertType.INFORMATION);
                loadEquipment(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update equipment", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete equipment
     *
     * @param equipment The equipment to delete
     */
    private void handleDeleteEquipment(Equipment equipment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Equipment");
        confirmAlert.setContentText("Are you sure you want to delete equipment " + equipment.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = equipmentService.deleteEquipment(equipment.getId());
            if (success) {
                showAlert("Success", "Equipment deleted successfully", Alert.AlertType.INFORMATION);
                loadEquipment(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete equipment", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load lending records
     */
    private void loadLendingRecords() {
        List<LendingRecord> lendingRecords = lendingService.getAllLendingRecords();

        // Filter by status if needed
        String status = lendingStatusComboBox.getValue();
        if (status != null && !status.equals("All")) {
            lendingRecords = lendingRecords.stream()
                    .filter(record -> record.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // Filter by borrower if provided
        String borrowerName = borrowerField.getText();
        if (borrowerName != null && !borrowerName.isEmpty()) {
            String borrowerNameLower = borrowerName.toLowerCase();
            lendingRecords = lendingRecords.stream()
                    .filter(record -> record.getBorrower() != null && 
                            record.getBorrower().getFullName().toLowerCase().contains(borrowerNameLower))
                    .collect(Collectors.toList());
        }

        lendingTable.setItems(FXCollections.observableArrayList(lendingRecords));
    }

    /**
     * Handle view lending record
     *
     * @param record The lending record to view
     */
    private void handleViewLendingRecord(LendingRecord record) {
        // Create a dialog for viewing lending record
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("View Lending Record");
        dialog.setHeaderText("Lending Record Details");

        // Set the button types
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Create the form fields (read-only)
        TextField idField = new TextField(String.valueOf(record.getId()));
        idField.setEditable(false);

        TextField borrowerField = new TextField(record.getBorrower() != null ? record.getBorrower().getFullName() : "");
        borrowerField.setEditable(false);

        TextField equipmentField = new TextField(record.getEquipment() != null ? record.getEquipment().getName() : "");
        equipmentField.setEditable(false);

        TextField courseField = new TextField(record.getCourse() != null ? record.getCourse().getCourseCode() : "");
        courseField.setEditable(false);

        TextField borrowDateField = new TextField(record.getBorrowDate().format(dateFormatter));
        borrowDateField.setEditable(false);

        TextField dueDateField = new TextField(record.getDueDate().format(dateFormatter));
        dueDateField.setEditable(false);

        TextField returnDateField = new TextField(record.getReturnDate() != null ? record.getReturnDate().format(dateFormatter) : "");
        returnDateField.setEditable(false);

        TextField statusField = new TextField(record.getStatus());
        statusField.setEditable(false);

        TextField purposeField = new TextField(record.getPurpose());
        purposeField.setEditable(false);

        TextField conditionField = new TextField(record.getCondition());
        conditionField.setEditable(false);

        TextArea notesArea = new TextArea(record.getNotes());
        notesArea.setEditable(false);
        notesArea.setPrefRowCount(3);

        TextField approverField = new TextField(record.getApprover() != null ? record.getApprover().getFullName() : "");
        approverField.setEditable(false);

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Borrower:"), 0, 1);
        grid.add(borrowerField, 1, 1);
        grid.add(new Label("Equipment:"), 0, 2);
        grid.add(equipmentField, 1, 2);
        grid.add(new Label("Course:"), 0, 3);
        grid.add(courseField, 1, 3);
        grid.add(new Label("Borrow Date:"), 0, 4);
        grid.add(borrowDateField, 1, 4);
        grid.add(new Label("Due Date:"), 0, 5);
        grid.add(dueDateField, 1, 5);
        grid.add(new Label("Return Date:"), 0, 6);
        grid.add(returnDateField, 1, 6);
        grid.add(new Label("Status:"), 0, 7);
        grid.add(statusField, 1, 7);
        grid.add(new Label("Purpose:"), 0, 8);
        grid.add(purposeField, 1, 8);
        grid.add(new Label("Condition:"), 0, 9);
        grid.add(conditionField, 1, 9);
        grid.add(new Label("Notes:"), 0, 10);
        grid.add(notesArea, 1, 10);
        grid.add(new Label("Approver:"), 0, 11);
        grid.add(approverField, 1, 11);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait();
    }

    /**
     * Handle edit lending record
     *
     * @param record The lending record to edit
     */
    private void handleEditLendingRecord(LendingRecord record) {
        // Create a dialog for editing lending record
        Dialog<LendingRecord> dialog = new Dialog<>();
        dialog.setTitle("Edit Lending Record");
        dialog.setHeaderText("Edit Lending Record");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form fields
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        statusComboBox.setValue(record.getStatus());

        DatePicker dueDatePicker = new DatePicker();
        if (record.getDueDate() != null) {
            dueDatePicker.setValue(record.getDueDate().toLocalDate());
        }

        TextField purposeField = new TextField(record.getPurpose());
        TextField conditionField = new TextField(record.getCondition());
        TextArea notesArea = new TextArea(record.getNotes());
        notesArea.setPrefRowCount(3);

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Status:"), 0, 0);
        grid.add(statusComboBox, 1, 0);
        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);
        grid.add(new Label("Purpose:"), 0, 2);
        grid.add(purposeField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionField, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a lending record when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                record.setStatus(statusComboBox.getValue());

                // Update due date if provided
                if (dueDatePicker.getValue() != null) {
                    LocalDateTime dueDateTime = dueDatePicker.getValue().atTime(23, 59);
                    record.setDueDate(dueDateTime);
                }

                record.setPurpose(purposeField.getText());
                record.setCondition(conditionField.getText());
                record.setNotes(notesArea.getText());

                return record;
            }
            return null;
        });

        Optional<LendingRecord> result = dialog.showAndWait();

        result.ifPresent(updatedRecord -> {
            boolean success = lendingService.updateLendingRecord(updatedRecord);
            if (success) {
                showAlert("Success", "Lending record updated successfully", Alert.AlertType.INFORMATION);
                loadLendingRecords(); // Refresh the table
            } else {
                showAlert("Error", "Failed to update lending record", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle delete lending record
     *
     * @param record The lending record to delete
     */
    private void handleDeleteLendingRecord(LendingRecord record) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Lending Record");
        confirmAlert.setContentText("Are you sure you want to delete this lending record?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = lendingService.deleteLendingRecord(record.getId());
            if (success) {
                showAlert("Success", "Lending record deleted successfully", Alert.AlertType.INFORMATION);
                loadLendingRecords(); // Refresh the table
            } else {
                showAlert("Error", "Failed to delete lending record", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Load statistics
     */
    private void loadStatistics() {
        // Get date range
        LocalDate fromDate = statsFromDatePicker.getValue();
        LocalDate toDate = statsToDatePicker.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("Error", "Please select both from and to dates", Alert.AlertType.ERROR);
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("Error", "From date cannot be after to date", Alert.AlertType.ERROR);
            return;
        }

        // Get system-wide lending statistics
        LendingService.LendingStatistics stats = lendingService.getSystemLendingStatistics();

        // Update summary labels
        totalUsersLabel.setText(String.valueOf(userService.getAllUsers().size()));
        totalEquipmentLabel.setText(String.valueOf(equipmentService.getAllEquipment().size()));
        totalLendingsLabel.setText(String.valueOf(stats.totalLendings));

        // Calculate overdue rate
        double overdueRate = stats.totalLendings > 0 ? 
                (double) stats.overdueLendings / stats.totalLendings * 100 : 0;
        overdueRateLabel.setText(String.format("%.1f%%", overdueRate));

        // Update pie chart
        statusPieChart.getData().clear();
        for (Map.Entry<String, Integer> entry : stats.lendingsPerBorrowerType.entrySet()) {
            statusPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Update bar chart
        equipmentBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrowing Frequency");

        // Sort equipment by borrowing frequency (descending)
        List<Map.Entry<String, Integer>> sortedEquipment = new ArrayList<>(stats.lendingsPerEquipment.entrySet());
        sortedEquipment.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Add top 10 equipment to chart
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedEquipment) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            count++;
            if (count >= 10) break; // Limit to top 10
        }

        equipmentBarChart.getData().add(series);
    }

    /**
     * Show an alert dialog
     *
     * @param title   The title of the alert
     * @param message The message to display
     * @param type    The type of alert
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
