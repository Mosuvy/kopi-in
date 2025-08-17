package com.example.application.views.customer;

import com.example.application.dao.OrderDAO;
import com.example.application.dao.TransactionsDAO;
import com.example.application.models.OrderItems;
import com.example.application.models.TransactionHistory;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import java.text.SimpleDateFormat;
import java.util.List;

@PageTitle("Detail Pesanan - kopi-In")
@Route(value = "order-details/:orderId", layout = AppLayoutNavbar.class)
public class OrderDetailsPage extends VerticalLayout implements BeforeEnterObserver {

    private final OrderDAO orderDAO = new OrderDAO();
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private String orderId;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        orderId = event.getRouteParameters().get("orderId").orElse("");
        if (orderId.isEmpty()) {
            event.forwardTo(HistoryPage.class);
            return;
        }

        createView();
    }

    private void createView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Back button
        Button backButton = new Button("Kembali ke Riwayat", VaadinIcon.ARROW_LEFT.create());
        backButton.addClickListener(e -> UI.getCurrent().navigate("transaction-history"));

        // Order header
        H2 header = new H2("Detail Pesanan #" + orderId);
        HorizontalLayout headerLayout = new HorizontalLayout(backButton, header);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setSpacing(true);

        // Get order items
        List<OrderItems> orderItems = orderDAO.getOrderItems(orderId);

        // Get transaction details
        TransactionHistory transaction = transactionsDAO.getTransactionHistoryByOrderId(orderId);

        // Order items grid
        Grid<OrderItems> itemsGrid = new Grid<>();
        itemsGrid.setItems(orderItems);
        itemsGrid.addColumn(OrderItems::getProduct_id).setHeader("Produk");
        itemsGrid.addColumn(OrderItems::getQuantity).setHeader("Jumlah");
        itemsGrid.addColumn(item -> String.format("Rp%,.2f", item.getPrice())).setHeader("Harga Satuan");
        itemsGrid.addColumn(item -> String.format("Rp%,.2f", item.getPrice() * item.getQuantity()))
                .setHeader("Subtotal");

        // Transaction summary
        VerticalLayout summaryLayout = new VerticalLayout();
        summaryLayout.setPadding(false);
        summaryLayout.setSpacing(false);

        H3 summaryHeader = new H3("Ringkasan Pembayaran");
        summaryLayout.add(summaryHeader);

        if (transaction != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");

            summaryLayout.add(createSummaryRow("Tanggal Transaksi", sdf.format(transaction.getPaidAt())));
            summaryLayout.add(createSummaryRow("Metode Pembayaran",
                    "cash".equalsIgnoreCase(transaction.getPaymentMethod()) ? "Tunai" : "E-Money"));

            summaryLayout.add(new Div()); // Spacer

            summaryLayout.add(createSummaryRow("Total Pesanan", String.format("Rp%,.2f", transaction.getTotalPrice())));

            if ("cash".equalsIgnoreCase(transaction.getPaymentMethod())) {
                summaryLayout.add(createSummaryRow("Dibayarkan", String.format("Rp%,.2f", transaction.getPaidAmount())));
                summaryLayout.add(createSummaryRow("Kembalian", String.format("Rp%,.2f", transaction.getChangeReturned())));
            }

            summaryLayout.add(new Div()); // Spacer

            Span status = new Span(transaction.getStatus());
            switch (transaction.getStatus().toLowerCase()) {
                case "completed":
                    status.getStyle().set("color", "var(--lumo-success-text-color)");
                    break;
                case "processing":
                    status.getStyle().set("color", "var(--lumo-primary-text-color)");
                    break;
                case "cancelled":
                    status.getStyle().set("color", "var(--lumo-error-text-color)");
                    break;
            }
            summaryLayout.add(createSummaryRow("Status Pesanan", status));
        }

        add(headerLayout, new H3("Daftar Item"), itemsGrid, summaryLayout);
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.add(new Span(label), new Span(value));
        return row;
    }

    private HorizontalLayout createSummaryRow(String label, com.vaadin.flow.component.Component component) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.add(new Span(label), component);
        return row;
    }
}