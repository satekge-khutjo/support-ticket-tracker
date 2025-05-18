package com.delulu;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainApp extends Application {

    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private static final String FILE_PATH = "tickets.txt";
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // Load from file at start
        loadTicketsFromFile();
        // UI 
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        Button addButton = new Button("Add Ticket");
        Button deleteButton = new Button("Delete Ticket");
        Button toggleStatusBtn = new Button("Toggle Status");
        
        ListView<Ticket> ticketListView = new ListView<>(tickets);

        ticketListView.setCellFactory(param -> new ListCell<>() {
    @Override
    protected void updateItem(Ticket ticket, boolean empty) {
        super.updateItem(ticket, empty);
        if (empty || ticket == null) {
            setText(null);
        } else {
            setText("#" + ticket.id + " [" + ticket.status + "] " + ticket.title + ": " + ticket.description);
        }
    }
});
        // Layout
        VBox inputBox = new VBox(10, titleField, descriptionField, addButton, deleteButton, toggleStatusBtn );
        inputBox.setPrefWidth(220);
        inputBox.setStyle("-fx-padding: 10;");

        BorderPane root = new BorderPane();
        root.setLeft(inputBox);
        root.setCenter(ticketListView);

        //add ticket
        addButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            if (!title.isEmpty()) {
                int id = ID_GENERATOR.getAndIncrement();
                Ticket ticket = new Ticket(id, title, description, "Open");
                tickets.add(ticket);
                saveTicketsToFile();
                titleField.clear();
                descriptionField.clear();
            }
        });

        // Delete Button
        deleteButton.setOnAction(e -> {
            Ticket selected = ticketListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                tickets.remove(selected);
                saveTicketsToFile();
            }
        });
        //status button
         toggleStatusBtn.setOnAction(e -> {
            Ticket selected = ticketListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.toggleStatus();
                ticketListView.refresh();
                saveTicketsToFile();
            }
        });

        // display
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Ticket Tracker");
        stage.setScene(scene);
        stage.show();
    }

    // Save tickets to file
    private void saveTicketsToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Ticket ticket : tickets) {
                writer.write(ticket.id + "\t"+ticket.title + "\t" + ticket.description + "\t"+ ticket.status);
                writer.newLine();
                System.out.println("Saved: "+ ticket);
            }
        } catch (IOException e) {
            System.err.println("Failed to save tickets: " + e.getMessage());
        }
    }

  // Load tickets from file
    private void loadTicketsFromFile() {
        Path path = Paths.get(FILE_PATH);
        if (Files.exists(path)) {
            System.out.println("Loading tickets...");
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                int maxId = 0;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t", 4);
                    if (parts.length >= 4) {
                        int id = Integer.parseInt(parts[0]);
                        String title = parts[1];
                        String description = parts[2];
                        String status = parts[3];
                        tickets.add(new Ticket(id, title, description, status));
                       if (id > maxId) maxId = id;
                       System.out.println("Loaded: "+ title);
                        
                    }
                }
                 ID_GENERATOR.set(maxId + 1);
            } catch (IOException e) {
                System.err.println("Failed to load tickets: " + e.getMessage());
            }
        }else{
            System.out.println("No previous tickets found. ");
        }
        
    }

    // class to hold ticket info
    public static class Ticket {
        int id;
        String title;
        String description;
        String status;

        Ticket(int id, String title, String description, String status) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.status = status;
        }

         void toggleStatus() {
            this.status = this.status.equalsIgnoreCase("Open") ? "Closed" : "Open";
        }

        @Override
        public String toString() {
            return "#" + id + " [" + status + "] " + title + ": " + description;
        }
    }
}
