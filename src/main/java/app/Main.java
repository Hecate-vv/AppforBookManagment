package app;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import app.model.Book;
import app.database.DatabaseController;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main extends Application {
    private TableView<Book> tableView;
    private ObservableList<Book> bookList;
    private DatabaseController dbController;
    private Label errorLabel;

    @Override
    public void start(Stage primaryStage) {
        dbController = new DatabaseController();

        // Initialize the TableView
        tableView = new TableView<>();
        bookList = FXCollections.observableArrayList(dbController.bookList());
        tableView.setItems(bookList);
        // Create columns for the TableView
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        TableColumn<Book, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()).asObject());
        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        tableView.getColumns().addAll(titleColumn, authorColumn, yearColumn, genreColumn);

        // Create the UI components
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        Label authorLabel = new Label("Author:");
        TextField authorField = new TextField();
        Label yearLabel = new Label("Year:");
        TextField yearField = new TextField();
        Label genreLabel = new Label("Genre:");
        TextField genreField = new TextField();
        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");

        // Error label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Set up the layout for input fields with increased padding
        GridPane inputPane = new GridPane();
        inputPane.setHgap(10);
        inputPane.setVgap(10);
        inputPane.setPadding(new Insets(15));
        inputPane.add(titleLabel, 0, 0);
        inputPane.add(titleField, 1, 0);
        inputPane.add(authorLabel, 0, 1);
        inputPane.add(authorField, 1, 1);
        inputPane.add(yearLabel, 0, 2);
        inputPane.add(yearField, 1, 2);
        inputPane.add(genreLabel, 0, 3);
        inputPane.add(genreField, 1, 3);
        inputPane.add(searchLabel, 0, 4);
        inputPane.add(searchField, 1, 4);
        inputPane.add(addButton, 0, 5);
        inputPane.add(deleteButton, 1, 5);
        inputPane.add(errorLabel, 0, 6, 2, 1);

        // Set up the main layout
        VBox mainLayout = new VBox(10, inputPane, tableView);
        mainLayout.setPadding(new Insets(20)); // Increase padding
        Scene scene = new Scene(mainLayout, 1280, 720);
        primaryStage.setTitle("Book Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add functionality to buttons
        addButton.setOnAction(event -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String yearText = yearField.getText();
            String genre = genreField.getText();

            if (title.isEmpty() || author.isEmpty() || yearText.isEmpty() || genre.isEmpty()) {
                showError("All fields must be filled.");
            } else {
                try {
                    int year = Integer.parseInt(yearText);
                    dbController.addBook(title, author, year, genre);
                    refreshBookList();
                    clearFields(titleField, authorField, yearField, genreField);
                    clearError();
                } catch (NumberFormatException e) {
                    showError("Year must be a number.");
                }
            }
        });

        //Delete book from Database
        deleteButton.setOnAction(event -> {
            Book selectedBook = tableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                dbController.deleteBook(selectedBook.getTitle());
                System.out.println(selectedBook.getTitle());
                refreshBookList();
                clearError();
            } else {
                showError("No book selected.");
            }
        });

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBooks(newValue);
        });
    }

    // Clear all input fields
    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    // Display an error message
    private void showError(String message) {
        errorLabel.setText(message);
    }

    // Clear any displayed error message
    private void clearError() {
        errorLabel.setText("");
    }

    // Refresh the book list displayed in the TableView
    private void refreshBookList() {
        bookList.setAll(dbController.bookList());
    }

    // Filter books based on keyword
    private void filterBooks(String keyword) {
        if (keyword.isEmpty()) {
            tableView.setItems(bookList);
        }
        else{
            ObservableList<Book> filteredList = FXCollections.observableArrayList();
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            for (Book book : bookList) {
                if (matchesPattern(book, pattern)) {
                    filteredList.add(book);
                }
            }
            tableView.setItems(filteredList);
        }
    }

    // Check if any book title matches the search patter
    private boolean matchesPattern(Book book, Pattern pattern) {
        Matcher titleMatcher = pattern.matcher(book.getTitle());
        return titleMatcher.find();
    }

        public static void main(String[] args) {
            launch(args);
        }
    }
