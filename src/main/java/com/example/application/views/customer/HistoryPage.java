package com.example.application.views.customer;

import com.example.application.dao.TransactionsDAO;
import com.example.application.models.TransactionHistory;
import com.example.application.models.Users;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.text.SimpleDateFormat;
import java.util.List;

@PageTitle("Riwayat Transaksi - kopi-In")
@Route(value = "transaction-history", layout = AppLayoutNavbar.class)
public class HistoryPage extends VerticalLayout {

    private final TransactionsDAO transactionsDAO = new TransactionsDAO();

    public HistoryPage() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Get current user
        Users currentUser = (Users) VaadinSession.getCurrent().getAttribute("user");
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        // Get transaction history
        List<TransactionHistory> histories = transactionsDAO.getTransactionHistoryByUserId(Integer.parseInt(currentUser.getId()));

        // Create header
        H2 header = new H2("Riwayat Transaksi");
        header.getStyle()
                .set("margin-bottom", "0")
                .set("margin-top", "0");

        // Create grid
        Grid<TransactionHistory> grid = new Grid<>();
        grid.setItems(histories);
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Add columns
        grid.addColumn(new ComponentRenderer<>(history -> {
            Div div = new Div();
            div.setText("#" + history.getOrderId());
            div.getStyle().set("font-weight", "bold");
            return div;
        })).setHeader("ID Pesanan").setAutoWidth(true);

        grid.addColumn(history -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
            return sdf.format(history.getCreatedAt());
        }).setHeader("Tanggal").setAutoWidth(true);

        grid.addComponentColumn(history -> {
            String status = history.getStatus();
            Div div = new Div();
            div.setText(status);

            switch (status.toLowerCase()) {
                case "completed": div.getStyle().set("color", "var(--lumo-success-text-color)"); break;
                case "processing": div.getStyle().set("color", "var(--lumo-primary-text-color)"); break;
                case "cancelled": div.getStyle().set("color", "var(--lumo-error-text-color)"); break;
            }

            return div;
        }).setHeader("Status").setAutoWidth(true);

        grid.addColumn(history -> {
            String method = history.getPaymentMethod();
            if ("cash".equalsIgnoreCase(method)) return "Tunai";
            if ("emoney".equalsIgnoreCase(method)) return "E-Money";
            return method;
        }).setHeader("Metode Pembayaran").setAutoWidth(true);

        grid.addColumn(history -> String.format("Rp%,.2f", history.getTotalPrice()))
                .setHeader("Total")
                .setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(history -> {
            Icon detailsIcon = VaadinIcon.EYE.create();
            detailsIcon.setColor("var(--lumo-primary-color)");
            detailsIcon.addClickListener(e -> {
                UI.getCurrent().navigate("order-details/" + history.getOrderId());
            });
            return detailsIcon;
        })).setHeader("Detail").setAutoWidth(true);

        // Add empty state if no history
        if (histories.isEmpty()) {
            Div emptyState = new Div();
            emptyState.setText("Belum ada riwayat transaksi");
            emptyState.getStyle()
                    .set("margin", "auto")
                    .set("font-style", "italic")
                    .set("color", "var(--lumo-secondary-text-color)");
            add(header, emptyState);
        } else {
            add(header, grid);
        }
    }
}