package com.economy;

/*
 * Copyright (c) 2021.
 *
 * Author:   Finogenov Vasily
 * Nickname: Solveus, solveus_666
 *
 * Github:   https://github.com/Solveus
 *
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import static javax.swing.JOptionPane.showMessageDialog;

public class App extends JFrame {
    private JPanel mainPane;
    private JTable table;
    private JTextField amount;
    private JTextField percent;
    private JButton btnSolve;
    private JComboBox<Integer> term;
    private JLabel resultSum;
    private JLabel resultPercent;
    private JLabel resultPayment;
    private DefaultTableModel model;

    private Integer[] month = { 3, 6, 12, 24, 36, 48, 86 };
    private String[] colName = { "№", "Платеж", "Проценты", "Долг", "Остаток на конец периода"};

    private int inputtedPercent;
    private int inputtedAmount;
    private double monthPayment;

    public App(String title) {
        super(title);

        initGui();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initGui() {

        term.setModel(new DefaultComboBoxModel<Integer>(month));
        term.setSelectedIndex(2);
        table.setFillsViewportHeight(true);
        btnSolve.addActionListener(e -> solveCredit());

        amount.setText("300000");
        percent.setText("9");
    }

    private void solveCredit() {

        if (!checkInput())
            return;

        int N = month[term.getSelectedIndex()];
        double p = (((double) inputtedPercent/100) / 12);

        monthPayment = (inputtedAmount * (p + p/(Math.pow((1+p), N) - 1)));
        monthPayment = round(monthPayment, 2);

        // table
        ArrayList<Double[]> tableRow = new ArrayList<>();

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.setColumnIdentifiers(colName);

        double tempBalanceOwed = inputtedAmount;

        for (int i = 0; i < N; i++) {
            Double[] arr = new Double[5];

            arr[0] = round(i+1, 1);
            arr[1] = round(monthPayment,                2);  // Платеж
            arr[2] = round((tempBalanceOwed * p),       2);  // Проценты
            arr[3] = round((monthPayment    - arr[2]),  2);  // Долг
            arr[4] = round((tempBalanceOwed - arr[3]),  2);  // Остаток на конец периода

            tempBalanceOwed = arr[4];

            // add last penny to previous month
            if(i == N-1) {
                arr[3] += arr[4];
                arr[3] = round(arr[3], 2);
                arr[4] = 0.0;
            }
            tableRow.add(arr);
        }

        // percent summ
        double sum = 0;
        for ( Double[] arr : tableRow)
            sum += arr[2];

        sum = round(sum, 2);

        // update labels
        resultPercent.setText(String.valueOf(sum));
        resultSum.setText(String.valueOf(inputtedAmount + sum));
        resultPayment.setText(String.valueOf(monthPayment));

        // add rows in table
        for (Double[] arr : tableRow)
            model.addRow(arr);

        table.setModel(model);
    }

    private boolean checkInput() {

        String amountText = amount.getText();
        String percentText = percent.getText();

        if (amountText == null || percentText == null) {
            showMessageDialog(null, "Пожалуйста, заполните все поля.");
            return false;
        }

        if (!amountText.matches("[0-9]+") || !percentText.matches("[0-9]+")) {
            showMessageDialog(null, "Сумма кредита и процент должны быть числом!");
            return false;
        }

        try {
            inputtedAmount  = Integer.parseInt(amountText);
            inputtedPercent = Integer.parseInt(percentText);
        } catch (NumberFormatException e) {
            showMessageDialog(null, "Невозможно преобразовать введенные данные в число!");
            return false;
        }

        if (inputtedPercent <= 0 || inputtedAmount <= 0) {
            showMessageDialog(null, "Сумма и процент не должны быть отрицательны или равны нулю!");
            return false;
        }

        return true;

    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
