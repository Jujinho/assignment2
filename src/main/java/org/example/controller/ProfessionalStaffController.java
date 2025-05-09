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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.Main;
import org.example.model.*;
import org.example.service.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Controller for the professional staff view.
 */
public class ProfessionalStaffController {

    // Services
    private AuthenticationService authenticationService;
    private UserService userService;
    private EquipmentService equipmentService;
    private LendingService lendingService;

    // Current professional staff
    private ProfessionalStaff currentStaff;

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
    private TextField staffIdField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField specializationField;
    @FXML
    private Button updateInfoButton;
    @FXML
    private Button changePasswordButton;

    // Borrow Equipment Tab
    @FXML
    private TextField searchEquipmentField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> conditionComboBox;
    @FXML
    private Button searchEquipmentButton;
    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableColumn<Equipment, String> eqNameColumn;
    @FXML
    private TableColumn<Equipment, String> eqCategoryColumn;
    @FXML
    private TableColumn<Equipment, String> eqConditionColumn;
    @FXML
    private TableColumn<Equipment, String> eqManufacturerColumn;
    @FXML
    private TableColumn<Equipment, String> eqModelColumn;
    @FXML
    private TableColumn<Equipment, Boolean> eqAvailableColumn;
    @FXML
    private TableColumn<Equipment, Button> eqActionColumn;
    @FXML
    private TextField purposeField;

    // My Lending History Tab
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField equipmentNameField;
    @FXML
    private ComboBox<String> statusComboBox;

    // Auto-complete suggestions list
    private javafx.collections.ObservableList<String> equipmentNameSuggestions;
    @FXML
    private Button filterHistoryButton;
    @FXML
    private TableView<LendingRecord> lendingHistoryTable;
    @FXML
    private TableColumn<LendingRecord, String> equipmentNameColumn;
    @FXML
    private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> dueDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML
    private TableColumn<LendingRecord, String> statusColumn;
    @FXML
    private TableColumn<LendingRecord, String> purposeColumn;
    @FXML
    private TableColumn<LendingRecord, Button> actionColumn;

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

        // Get current professional staff
        if (authenticationService.isProfessionalStaff()) {
            currentStaff = authenticationService.getCurrentProfessionalStaff();
            welcomeLabel.setText("Welcome, " + currentStaff.getFullName());

            // Initialize personal information
            initializePersonalInfo();

            // Initialize equipment borrowing
            initializeEquipmentBorrowing();

            // Initialize lending history
            initializeLendingHistory();

            // Set up auto-refresh timer (refresh every 10 seconds)
            refreshTimer = new Timer(true); // true makes it a daemon timer
            refreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Run on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        loadLendingHistory();
                    });
                }
            }, 10000, 10000); // 10 seconds delay, 10 seconds period
        } else {
            showAlert("Error", "Not logged in as professional staff", Alert.AlertType.ERROR);
        }
    }

    /**
     * Initialize personal information tab
     */
    private void initializePersonalInfo() {
        // Set personal information fields
        firstNameField.setText(currentStaff.getFirstName());
        lastNameField.setText(currentStaff.getLastName());
        emailField.setText(currentStaff.getEmail());
        phoneField.setText(currentStaff.getPhoneNumber());
        if (currentStaff.getDateOfBirth() != null) {
            dobPicker.setValue(currentStaff.getDateOfBirth());
        }
        addressField.setText(currentStaff.getAddress());
        staffIdField.setText(currentStaff.getStaffId());
        departmentField.setText(currentStaff.getDepartment());
        positionField.setText(currentStaff.getPosition());
        specializationField.setText(currentStaff.getSpecialization());
    }

    /**
     * Initialize equipment borrowing tab
     */
    private void initializeEquipmentBorrowing() {
        // Set up category and condition combo boxes
        loadCategories();
        loadConditions();

        // Set up table columns
        eqNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eqCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        eqConditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        eqManufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        eqModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        eqAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // Set up action column
        eqActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button borrowButton = new Button("Borrow");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Equipment equipment = getTableView().getItems().get(getIndex());
                if (equipment.isAvailable()) {
                    borrowButton.setOnAction(event -> handleBorrowEquipment(equipment));
                    setGraphic(borrowButton);
                } else {
                    setGraphic(null);
                }
            }
        });

        // Load equipment
        loadAllEquipment();
    }

    /**
     * Initialize lending history tab
     */
    private void initializeLendingHistory() {
        // Set up status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "All", "Pending", "Approved", "Borrowed", "Returned", "Overdue", "Rejected"));
        statusComboBox.getSelectionModel().selectFirst();

        // Set up date pickers
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());

        // Initialize auto-complete for equipment name field
        initializeAutoComplete();

        // Set up table columns
        equipmentNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getEquipment().getName()));

        borrowDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getBorrowDate().format(dateFormatter)));

        dueDateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDueDate().format(dateFormatter)));

        returnDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime returnDate = cellData.getValue().getReturnDate();
            return new SimpleStringProperty(returnDate != null ? returnDate.format(dateFormatter) : "");
        });

        statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatus()));

        purposeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPurpose()));

        // Set up action column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnButton = new Button("Return");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                LendingRecord record = getTableView().getItems().get(getIndex());
                if ("Borrowed".equals(record.getStatus())) {
                    returnButton.setOnAction(event -> handleReturnEquipment(record));
                    setGraphic(returnButton);
                } else {
                    setGraphic(null);
                }
            }
        });

        // Load lending history
        loadLendingHistory();
    }

    /**
     * Load all equipment
     */
    private void loadAllEquipment() {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        equipmentTable.setItems(FXCollections.observableArrayList(equipmentList));
    }

    /**
     * Load categories for combo box
     */
    private void loadCategories() {
        List<String> categories = equipmentService.getAllCategories();
        categories.add(0, "All");
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        categoryComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Load conditions for combo box
     */
    private void loadConditions() {
        List<String> conditions = equipmentService.getAllConditions();
        conditions.add(0, "All");
        conditionComboBox.setItems(FXCollections.observableArrayList(conditions));
        conditionComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Load lending history
     */
    private void loadLendingHistory() {
        List<LendingRecord> lendingRecords = lendingService.getLendingRecordsByBorrower(currentStaff.getId());
        lendingHistoryTable.setItems(FXCollections.observableArrayList(lendingRecords));
    }

    /**
     * Initialize auto-complete functionality for equipment name field
     */
    private void initializeAutoComplete() {
        // Get all equipment names for auto-complete
        List<Equipment> allEquipment = equipmentService.getAllEquipment();
        equipmentNameSuggestions = FXCollections.observableArrayList(
                allEquipment.stream()
                        .map(Equipment::getName)
                        .distinct()
                        .collect(Collectors.toList())
        );

        // Create auto-complete popup
        javafx.scene.control.ListView<String> suggestionList = new javafx.scene.control.ListView<>(equipmentNameSuggestions);
        javafx.stage.Popup popup = new javafx.stage.Popup();
        popup.getContent().add(suggestionList);

        // Set up listener for text changes
        equipmentNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                popup.hide();
                return;
            }

            // Filter suggestions based on input
            String lowerCaseInput = newValue.toLowerCase();
            ObservableList<String> filteredList = FXCollections.observableArrayList(
                    equipmentNameSuggestions.stream()
                            .filter(name -> name.toLowerCase().contains(lowerCaseInput))
                            .collect(Collectors.toList())
            );

            suggestionList.setItems(filteredList);

            // Show popup if there are suggestions
            if (!filteredList.isEmpty() && equipmentNameField.isFocused()) {
                javafx.geometry.Bounds bounds = equipmentNameField.localToScreen(equipmentNameField.getBoundsInLocal());
                popup.show(equipmentNameField, bounds.getMinX(), bounds.getMaxY());
                suggestionList.setPrefWidth(equipmentNameField.getWidth());
                suggestionList.setPrefHeight(Math.min(filteredList.size() * 24, 200));
            } else {
                popup.hide();
            }
        });

        // Handle selection from suggestion list
        suggestionList.setOnMouseClicked(event -> {
            String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                equipmentNameField.setText(selectedItem);
                popup.hide();
            }
        });

        // Hide popup when focus is lost
        equipmentNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popup.hide();
            }
        });
    }

    /**
     * Handle update information button click
     *
     * @param event The action event
     */
    @FXML
    public void handleUpdateInfo(ActionEvent event) {
        // Update staff information
        currentStaff.setFirstName(firstNameField.getText());
        currentStaff.setLastName(lastNameField.getText());
        currentStaff.setEmail(emailField.getText());
        currentStaff.setPhoneNumber(phoneField.getText());
        currentStaff.setDateOfBirth(dobPicker.getValue());
        currentStaff.setAddress(addressField.getText());

        // Save changes
        boolean success = userService.updatePersonalInfo(currentStaff);
        if (success) {
            showAlert("Success", "Personal information updated successfully", Alert.AlertType.INFORMATION);
            welcomeLabel.setText("Welcome, " + currentStaff.getFullName());
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
        // Create a dialog for changing password
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current password and new password");

        // Set the button types
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // Create the password fields
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm New Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a password-pair when the change button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new String[]{oldPasswordField.getText(), newPasswordField.getText(), confirmPasswordField.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(passwords -> {
            String oldPassword = passwords[0];
            String newPassword = passwords[1];
            String confirmPassword = passwords[2];

            // Validate passwords
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "Passwords cannot be empty", Alert.AlertType.ERROR);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert("Error", "New passwords do not match", Alert.AlertType.ERROR);
                return;
            }

            // Change password
            boolean success = userService.changePassword(currentStaff.getId(), oldPassword, newPassword);
            if (success) {
                showAlert("Success", "Password changed successfully", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to change password. Check your current password.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle search equipment button click
     *
     * @param event The action event
     */
    @FXML
    public void handleSearchEquipment(ActionEvent event) {
        String name = searchEquipmentField.getText();
        String category = categoryComboBox.getValue();
        String condition = conditionComboBox.getValue();

        // If "All" is selected, set to null for the search
        if ("All".equals(category)) {
            category = null;
        }
        if ("All".equals(condition)) {
            condition = null;
        }

        // Search equipment
        List<Equipment> searchResults = equipmentService.searchEquipment(name, category, condition);
        equipmentTable.setItems(FXCollections.observableArrayList(searchResults));
    }

    /**
     * Handle borrow equipment button click
     *
     * @param equipment The equipment to borrow
     */
    private void handleBorrowEquipment(Equipment equipment) {
        String purpose = purposeField.getText();

        if (purpose.isEmpty()) {
            showAlert("Error", "Please enter a purpose", Alert.AlertType.ERROR);
            return;
        }

        // Create lending request
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusWeeks(2); // Maximum borrowing period is 2 weeks

        boolean success = lendingService.createStaffLendingRequest(
                currentStaff, equipment, borrowDate, dueDate, purpose, "");

        if (success) {
            showAlert("Success", "Borrowing request submitted successfully", Alert.AlertType.INFORMATION);
            loadAllEquipment(); // Refresh equipment list
            loadLendingHistory(); // Refresh lending history
        } else {
            showAlert("Error", "Failed to submit borrowing request", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handle filter history button click
     *
     * @param event The action event
     */
    @FXML
    public void handleFilterHistory(ActionEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String equipmentName = equipmentNameField.getText();
        String status = statusComboBox.getValue();

        if (fromDate == null || toDate == null) {
            showAlert("Error", "Please select both from and to dates", Alert.AlertType.ERROR);
            return;
        }

        if (fromDate.isAfter(toDate)) {
            showAlert("Error", "From date cannot be after to date", Alert.AlertType.ERROR);
            return;
        }

        // Get lending records
        List<LendingRecord> allRecords = lendingService.getLendingRecordsByBorrower(currentStaff.getId());

        // Filter by date, equipment name, and status
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        ObservableList<LendingRecord> filteredRecords = allRecords.stream()
                .filter(record -> {
                    boolean dateMatch = record.getBorrowDate().isAfter(fromDateTime) && 
                                       record.getBorrowDate().isBefore(toDateTime);
                    boolean equipmentMatch = equipmentName.isEmpty() || 
                                           record.getEquipment().getName().toLowerCase().contains(equipmentName.toLowerCase());
                    boolean statusMatch = "All".equals(status) || record.getStatus().equals(status);
                    return dateMatch && equipmentMatch && statusMatch;
                })
                .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);

        lendingHistoryTable.setItems(filteredRecords);
    }

    /**
     * Handle return equipment button click
     *
     * @param lendingRecord The lending record to return
     */
    private void handleReturnEquipment(LendingRecord lendingRecord) {
        // Create a dialog for returning equipment
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Return Equipment");
        dialog.setHeaderText("Enter condition and notes for returning " + lendingRecord.getEquipment().getName());

        // Set the button types
        ButtonType returnButtonType = new ButtonType("Return", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(returnButtonType, ButtonType.CANCEL);

        // Create the fields
        ComboBox<String> conditionCombo = new ComboBox<>();
        conditionCombo.setItems(FXCollections.observableArrayList("Excellent", "Good", "Fair", "Poor", "Damaged"));
        conditionCombo.getSelectionModel().selectFirst();

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter any notes about the equipment condition");

        // Layout the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        grid.add(new Label("Condition:"), 0, 0);
        grid.add(conditionCombo, 1, 0);
        grid.add(new Label("Notes:"), 0, 1);
        grid.add(notesArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the return button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == returnButtonType) {
                return conditionCombo.getValue() + "|" + notesArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(returnInfo -> {
            String[] parts = returnInfo.split("\\|");
            String condition = parts[0];
            String notes = parts.length > 1 ? parts[1] : "";

            // Return equipment
            boolean success = lendingService.returnEquipment(
                    lendingRecord.getId(), LocalDateTime.now(), condition, notes);

            if (success) {
                showAlert("Success", "Equipment returned successfully", Alert.AlertType.INFORMATION);
                loadLendingHistory(); // Refresh lending history
                loadAllEquipment(); // Refresh equipment list
            } else {
                showAlert("Error", "Failed to return equipment", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Handle logout button click
     *
     * @param event The action event
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        // Cancel the refresh timer
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }

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
