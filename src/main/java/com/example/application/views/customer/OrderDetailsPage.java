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

import java.text.SimpleDateFormat;
import java.util.List;

import static com.example.application.views.AppLayoutNavbar.getCurrentUser;

@PageTitle("Detail Pesanan - kopi-In")
@Route(value = "customer/order-details/:orderId", layout = AppLayoutNavbar.class)
public class OrderDetailsPage extends VerticalLayout implements BeforeEnterObserver {

    private static final String STYLE_MARGIN_BOTTOM = "var(--lumo-space-xs)";
    private static final String STYLE_FONT_WEIGHT_500 = "500";
    private static final String STYLE_SECONDARY_TEXT = "var(--lumo-secondary-text-color)";

    private final OrderDAO orderDAO = new OrderDAO();
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private String orderId;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        checkUserAuthentication();
        orderId = event.getRouteParameters().get("orderId").orElse("");

        if (orderId.isEmpty()) {
            event.forwardTo(HistoryPage.class);
            return;
        }

        initializePage();
    }

    private void initializePage() {
        configureLayout();
        add(createContent());
    }

    private void configureLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("padding", "var(--lumo-space-m)");
    }

    private VerticalLayout createContent() {
        List<OrderItems> orderItems = orderDAO.getOrderItems(orderId);
        TransactionHistory transaction = transactionsDAO.getTransactionHistoryByOrderId(orderId);

        VerticalLayout content = new VerticalLayout(
                createHeaderLayout(),
                new H3("Daftar Item"),
                createItemsGrid(orderItems),
                createTransactionSummary(transaction)
        );

        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();
        return content;
    }

    private HorizontalLayout createHeaderLayout() {
        Button backButton = createBackButton();
        H2 header = createPageHeader();

        HorizontalLayout headerLayout = new HorizontalLayout(backButton, header);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setSpacing(false);
        headerLayout.setWidthFull();
        headerLayout.setFlexGrow(1, header);

        return headerLayout;
    }

    private Button createBackButton() {
        Button button = new Button("Kembali ke Riwayat", VaadinIcon.ARROW_LEFT.create());
        button.getStyle()
                .set("margin-right", "var(--lumo-space-m)")
                .set("margin-bottom", "var(--lumo-space-m)");
        button.addClickListener(e -> UI.getCurrent().navigate("customer/transaction-history"));
        return button;
    }

    private H2 createPageHeader() {
        H2 header = new H2("Detail Pesanan #" + orderId);
        header.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-xxl)")
                .set("font-weight", "600")
                .set("color", "var(--lumo-primary-text-color)");
        return header;
    }

    private Grid<OrderItems> createItemsGrid(List<OrderItems> orderItems) {
        Grid<OrderItems> grid = new Grid<>();
        grid.setItems(orderItems);
        grid.getStyle()
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("margin-bottom", "var(--lumo-space-l)");

        grid.addColumn(OrderItems::getProduct_id)
                .setHeader("Produk")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(OrderItems::getQuantity)
                .setHeader("Jumlah")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(item -> String.format("Rp%,.2f", item.getPrice()))
                .setHeader("Harga Satuan")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(item -> String.format("Rp%,.2f", item.getPrice() * item.getQuantity()))
                .setHeader("Subtotal")
                .setAutoWidth(true)
                .setFlexGrow(0);

        return grid;
    }

    private VerticalLayout createTransactionSummary(TransactionHistory transaction) {
        VerticalLayout summaryLayout = new VerticalLayout();
        summaryLayout.setPadding(true);
        summaryLayout.setSpacing(false);
        summaryLayout.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");

        summaryLayout.add(createSummaryHeader());

        if (transaction != null) {
            addTransactionDetails(summaryLayout, transaction);
        }

        return summaryLayout;
    }

    private H3 createSummaryHeader() {
        H3 header = new H3("Ringkasan Pembayaran");
        header.getStyle()
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("font-size", "var(--lumo-font-size-l)")
                .set("color", "var(--lumo-primary-text-color)");
        return header;
    }

    private void addTransactionDetails(VerticalLayout layout, TransactionHistory transaction) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");

        layout.add(createSummaryRow("Tanggal Transaksi", sdf.format(transaction.getPaidAt())));
        layout.add(createSummaryRow("Metode Pembayaran",
                formatPaymentMethod(transaction.getPaymentMethod())));

        layout.add(createDivider());

        layout.add(createSummaryRow("Total Pesanan",
                formatCurrency(transaction.getTotalPrice())));

        if ("cash".equalsIgnoreCase(transaction.getPaymentMethod())) {
            layout.add(createSummaryRow("Dibayarkan",
                    formatCurrency(transaction.getPaidAmount())));
            layout.add(createSummaryRow("Kembalian",
                    formatCurrency(transaction.getChangeReturned())));
        }

        layout.add(createDivider());
        layout.add(createSummaryRow("Status Pesanan", createStatusBadge(transaction.getStatus())));
    }

    private String formatPaymentMethod(String method) {
        return "cash".equalsIgnoreCase(method) ? "Tunai" : "E-Money";
    }

    private String formatCurrency(double amount) {
        return String.format("Rp%,.2f", amount);
    }

    private Div createDivider() {
        Div divider = new Div();
        divider.getStyle()
                .set("height", "1px")
                .set("background", "var(--lumo-contrast-20pct)")
                .set("margin", "var(--lumo-space-s) 0");
        return divider;
    }

    private Span createStatusBadge(String status) {
        Span badge = new Span(status);
        badge.getStyle()
                .set("font-weight", STYLE_FONT_WEIGHT_500)
                .set("padding", "var(--lumo-space-xs) var(--lumo-space-s)")
                .set("border-radius", "var(--lumo-border-radius-pill)");

        switch (status.toLowerCase()) {
            case "completed":
                badge.getStyle()
                        .set("color", "var(--lumo-success-contrast-color)")
                        .set("background-color", "var(--lumo-success-color-10pct)");
                break;
            case "processing":
                badge.getStyle()
                        .set("color", "var(--lumo-primary-contrast-color)")
                        .set("background-color", "var(--lumo-primary-color-10pct)");
                break;
            case "cancelled":
                badge.getStyle()
                        .set("color", "var(--lumo-error-contrast-color)")
                        .set("background-color", "var(--lumo-error-color-10pct)");
                break;
        }

        return badge;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-weight", STYLE_FONT_WEIGHT_500)
                .set("color", STYLE_SECONDARY_TEXT);

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-weight", "600");

        return createSummaryRowLayout(labelSpan, valueSpan);
    }

    private HorizontalLayout createSummaryRow(String label, com.vaadin.flow.component.Component component) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-weight", STYLE_FONT_WEIGHT_500)
                .set("color", STYLE_SECONDARY_TEXT);

        return createSummaryRowLayout(labelSpan, component);
    }

    private HorizontalLayout createSummaryRowLayout(com.vaadin.flow.component.Component left,
                                                    com.vaadin.flow.component.Component right) {
        HorizontalLayout row = new HorizontalLayout(left, right);
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.getStyle().set("margin-bottom", STYLE_MARGIN_BOTTOM);
        return row;
    }

    private void checkUserAuthentication() {
        if (getCurrentUser() == null) {
            UI.getCurrent().navigate("/");
        }
    }
}