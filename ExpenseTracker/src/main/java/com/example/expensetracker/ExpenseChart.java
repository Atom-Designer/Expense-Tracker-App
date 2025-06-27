package com.example.expensetracker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseChart extends Application {
    public static ArrayList<Transaction> sharedTransactions = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Expense Pie Chart");

        HashMap<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : sharedTransactions) {
            if (t.getType().equalsIgnoreCase("Expense")) {
                String category = t.getCategory();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + t.getAmount());
            }
        }

        PieChart pieChart = new PieChart();
        for (String category : categoryTotals.keySet()) {
            pieChart.getData().add(new PieChart.Data(category, categoryTotals.get(category)));
        }

        VBox root = new VBox(pieChart);
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void launchChart(ArrayList<Transaction> transactions) {
        sharedTransactions = transactions;
        Application.launch();
    }
}
