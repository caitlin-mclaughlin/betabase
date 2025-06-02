package com.betabase.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CalendarController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private YearMonth currentYearMonth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/betabase/views/sidebar.fxml"));
            VBox sidebar = loader.load();
            
            mainPane.setLeft(sidebar);

            // Bind sidebar width to the smaller of 25% of total width or 300px
            sidebar.prefWidthProperty().bind(
                Bindings.createDoubleBinding(() -> 
                    Math.min(mainPane.getWidth() * 0.25, 300),
                    mainPane.widthProperty()
                )
            );

            // Highlight Calendar sidebar button
            SidebarController sidebarController = loader.getController();
            sidebarController.setActiveSection("cal");

            currentYearMonth = YearMonth.now();
            drawCalendar(currentYearMonth);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlePrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        drawCalendar(currentYearMonth);
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        drawCalendar(currentYearMonth);
    }

    private void drawCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        monthYearLabel.setText(yearMonth.getMonth() + " " + yearMonth.getYear());

        // Add day labels
        String[] days = {"Sunday", "Monday", "Tueday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int col = 0; col < days.length; col++) {
            Label dayLabel = new Label(days[col]);
            StackPane wrapper = new StackPane(dayLabel);
            wrapper.getStyleClass().add("week-header");
            calendarGrid.add(wrapper, col, 0);
            GridPane.setHgrow(wrapper, Priority.ALWAYS);
            GridPane.setVgrow(wrapper, Priority.ALWAYS);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = yearMonth.lengthOfMonth();

        int startCol = dayOfWeek;
        int totalCells = dayOfWeek + daysInMonth;
        int totalRows = (int) Math.ceil(totalCells / 7.0);

        // Add RowConstraints for each row (including header)
        RowConstraints headerRow = new RowConstraints();
        headerRow.setMinHeight(35);
        headerRow.setPrefHeight(35);
        headerRow.setMaxHeight(35);
        headerRow.setVgrow(Priority.NEVER);
        calendarGrid.getRowConstraints().add(headerRow);

        for (int i = 0; i < totalRows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rc);
        }

        int row = 1;
        int col = startCol;
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            VBox cell = new VBox();
            cell.getStyleClass().add("calendar-cell");

            LocalDate thisDate = yearMonth.atDay(day);
            if (thisDate.equals(today)) {
                cell.getStyleClass().add("current-day-cell");
            }

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("day-label");
            cell.getChildren().add(dayLabel);

            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }
}
