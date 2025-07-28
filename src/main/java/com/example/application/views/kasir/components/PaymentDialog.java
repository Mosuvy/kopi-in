package com.example.application.views.kasir.components;

import com.example.application.models.OrderItems;
import com.example.application.models.Promo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;
import java.util.function.Consumer;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

public class PaymentDialog extends Dialog {
    private final double totalAmount;
    private final List<OrderItems> orderItems;
    private final Promo appliedPromo;
    private final Consumer<String> onPaymentComplete;

    public PaymentDialog(List<OrderItems> orderItems, double totalAmount, Promo appliedPromo, Consumer<String> onPaymentComplete) {
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.appliedPromo = appliedPromo;
        this.onPaymentComplete = onPaymentComplete;

        setWidth("500px");
        getElement().getStyle().set("border-radius", "12px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("Payment Details");
        title.getStyle().set("margin", "0").set("color", "#6B4423");

        // Order summary
        Paragraph summary = new Paragraph(String.format("Total Amount: Rp %.2f", totalAmount));
        if (appliedPromo != null) {
            summary.setText(summary.getText() + 
                String.format("\nPromo Applied: %s (%d%% discount)", 
                    appliedPromo.getName(), 
                    appliedPromo.getDiscount_percentage()));
        }

        // Payment method selection
        ComboBox<String> paymentMethodCombo = new ComboBox<>("Payment Method");
        paymentMethodCombo.setItems("Cash", "Debit Card", "Credit Card", "E-Wallet");
        paymentMethodCombo.setWidth("100%");

        // Amount received (for cash payments)
        NumberField amountReceived = new NumberField("Amount Received");
        amountReceived.setWidth("100%");
        amountReceived.setMin(totalAmount);
        amountReceived.setValue(totalAmount);

        // Change calculation
        Paragraph changeText = new Paragraph("Change: Rp 0.00");
        amountReceived.addValueChangeListener(e -> {
            double change = e.getValue() - totalAmount;
            changeText.setText(String.format("Change: Rp %.2f", Math.max(0, change)));
        });

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.END);

        Button cancelButton = new Button("Cancel", e -> close());
        cancelButton.getStyle()
                .set("margin-right", "12px")
                .set("color", "#6B4423");

        Button processButton = new Button("Process Payment", e -> {
            if (paymentMethodCombo.isEmpty()) {
                Notification.show("Please select a payment method", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (paymentMethodCombo.getValue().equals("Cash") && 
                (amountReceived.isEmpty() || amountReceived.getValue() < totalAmount)) {
                Notification.show("Please enter a valid amount", 3000, Notification.Position.MIDDLE);
                return;
            }

            onPaymentComplete.accept(paymentMethodCombo.getValue());
            close();
        });
        processButton.getStyle()
                .set("background", "#6B4423")
                .set("color", "white")
                .set("cursor", "pointer");

        actions.add(cancelButton, processButton);

        // Add components to dialog
        content.add(
            title,
            summary,
            paymentMethodCombo,
            amountReceived,
            changeText,
            actions
        );

        add(content);

        // Show/hide amount received field based on payment method
        paymentMethodCombo.addValueChangeListener(e -> {
            boolean isCash = "Cash".equals(e.getValue());
            amountReceived.setVisible(isCash);
            changeText.setVisible(isCash);
        });
    }
} 