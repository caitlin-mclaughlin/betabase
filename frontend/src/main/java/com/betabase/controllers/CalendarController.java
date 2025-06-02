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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
        monthYearLabel.setText(yearMonth.getMonth() + " " + yearMonth.getYear());

        // Add day labels
        String[] days = {"Sunday", "Monday", "Tueday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int col = 0; col < days.length; col++) {
            Label dayLabel = new Label(days[col]);
            StackPane wrapper = new StackPane(dayLabel);
            wrapper.getStyleClass().add("week-header");
            calendarGrid.add(wrapper, col, 0);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            // Create container for cell
            VBox cell = new VBox();
            cell.getStyleClass().add("calendar-cell");

            // Add day number
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("day-label");
            cell.getChildren().add(dayLabel);

            // Make cell grow to fill available space
            GridPane.setHgrow(cell, Priority.ALWAYS);
            GridPane.setVgrow(cell, Priority.ALWAYS);

            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }
}
