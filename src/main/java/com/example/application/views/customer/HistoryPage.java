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
@Route(value = "customer/transaction-history", layout = AppLayoutNavbar.class)
public class HistoryPage extends VerticalLayout {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm");
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();

    public HistoryPage() {
        initializeLayout();
        checkUserAuthentication();

        Users currentUser = getCurrentUser();
        List<TransactionHistory> histories = getTransactionHistories(currentUser);

        setupPageHeader();
        if (histories.isEmpty()) {
            showEmptyState();
        } else {
            showTransactionGrid(histories);
        }
    }

    private void initializeLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    private void checkUserAuthentication() {
        if (getCurrentUser() == null) {
            UI.getCurrent().navigate("/");
        }
    }

    private Users getCurrentUser() {
        return (Users) VaadinSession.getCurrent().getAttribute("user");
    }

    private List<TransactionHistory> getTransactionHistories(Users user) {
        return transactionsDAO.getTransactionHistoryByUserId(Integer.parseInt(user.getId()));
    }

    private void setupPageHeader() {
        H2 header = new H2("Riwayat Transaksi");
        header.getStyle()
                .set("margin-bottom", "0")
                .set("margin-top", "0");
        add(header);
    }

    private void showEmptyState() {
        Div emptyState = createEmptyStateDiv();
        add(emptyState);
    }

    private Div createEmptyStateDiv() {
        Div emptyState = new Div();
        emptyState.setText("Belum ada riwayat transaksi");
        emptyState.getStyle()
                .set("margin", "auto")
                .set("font-style", "italic")
                .set("color", "var(--lumo-secondary-text-color)");
        return emptyState;
    }

    private void showTransactionGrid(List<TransactionHistory> histories) {
        Grid<TransactionHistory> grid = createTransactionGrid();
        configureGridColumns(grid);
        grid.setItems(histories);
        add(grid);
    }

    private Grid<TransactionHistory> createTransactionGrid() {
        Grid<TransactionHistory> grid = new Grid<>();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        return grid;
    }

    private void configureGridColumns(Grid<TransactionHistory> grid) {
        grid.addColumn(new ComponentRenderer<>(this::createOrderIdColumn))
                .setHeader("ID Pesanan")
                .setAutoWidth(true);
        grid.addColumn(this::formatTransactionDate).setHeader("Tanggal").setAutoWidth(true);
        grid.addComponentColumn(this::createStatusColumn).setHeader("Status").setAutoWidth(true);
        grid.addColumn(this::translatePaymentMethod).setHeader("Metode Pembayaran").setAutoWidth(true);
        grid.addColumn(this::formatTotalPrice).setHeader("Total").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>( history -> {
            Icon detailsIcon = VaadinIcon.EYE.create();
            detailsIcon.getStyle().set("cursor", "pointer");
            detailsIcon.addClickListener(e ->
                    UI.getCurrent().navigate("customer/order-details/" + history.getOrderId())
            );
            return detailsIcon;
        })).setHeader("Detail").setAutoWidth(true);
    }

    private Div createOrderIdColumn(TransactionHistory history) {
        Div div = new Div();
        div.setText("#" + history.getOrderId());
        div.getStyle().set("font-weight", "bold");
        return div;
    }

    private String formatTransactionDate(TransactionHistory history) {
        return DATE_FORMAT.format(history.getCreatedAt());
    }

    private Div createStatusColumn(TransactionHistory history) {
        String status = history.getStatus();
        Div div = new Div();
        div.setText(status);

        switch (status.toLowerCase()) {
            case "completed":
                div.getStyle().set("color", "var(--lumo-success-text-color)");
                break;
            case "processing":
                div.getStyle().set("color", "var(--lumo-primary-text-color)");
                break;
            case "cancelled":
                div.getStyle().set("color", "var(--lumo-error-text-color)");
                break;
        }

        return div;
    }

    private String translatePaymentMethod(TransactionHistory history) {
        String method = history.getPaymentMethod();
        if ("cash".equalsIgnoreCase(method)) return "Tunai";
        if ("emoney".equalsIgnoreCase(method)) return "E-Money";
        return method;
    }

    private String formatTotalPrice(TransactionHistory history) {
        return String.format("Rp%,.2f", history.getTotalPrice());
    }
}