package com.example.application.views.customer;

import com.example.application.dao.OrderDAO;
import com.example.application.dao.TransactionsDAO;
import com.example.application.models.*;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Checkout - kopi-In")
@Route(value = "customer/checkout", layout = AppLayoutNavbar.class)
public class CheckoutPage extends VerticalLayout {

    private final OrderDAO orderDAO = new OrderDAO();
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private List<CartItem> cartItems;
    private Promo activePromo;
    private double totalAmount;
    private double discountAmount;
    private double subtotal;
    private double tax;

    public CheckoutPage() {
        setupLayout();
        loadSessionData();

        if (isCartEmpty()) {
            showEmptyCartState();
            return;
        }

        calculateOrderTotals();
        createCheckoutLayout();
    }

    private void setupLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--lumo-contrast-5pct)");
    }

    private void loadSessionData() {
        cartItems = (List<CartItem>) VaadinSession.getCurrent().getAttribute("cart");
        activePromo = (Promo) VaadinSession.getCurrent().getAttribute("activePromo");
    }

    private boolean isCartEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    private void showEmptyCartState() {
        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setAlignItems(Alignment.CENTER);
        emptyState.setJustifyContentMode(JustifyContentMode.CENTER);
        emptyState.setHeightFull();

        Icon cartIcon = VaadinIcon.CART.create();
        cartIcon.setSize("48px");
        cartIcon.setColor("var(--lumo-contrast-60pct)");

        H2 emptyTitle = new H2("Keranjang Anda kosong");
        emptyTitle.getStyle().set("margin-top", "0");

        Button homeButton = new Button("Kembali ke Beranda",
                VaadinIcon.HOME.create(), e -> UI.getCurrent().navigate("customer"));
        homeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        emptyState.add(cartIcon, emptyTitle, homeButton);
        add(emptyState);
    }

    private void calculateOrderTotals() {
        subtotal = calculateSubtotal();
        discountAmount = calculateDiscount();
        tax = calculateTax();
        totalAmount = calculateTotalAmount();
    }

    private double calculateSubtotal() {
        return cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private double calculateDiscount() {
        return activePromo != null ? subtotal * activePromo.getDiscount_value() : 0.0;
    }

    private double calculateTax() {
        return (subtotal - discountAmount) * 0.05;
    }

    private double calculateTotalAmount() {
        return subtotal - discountAmount + tax;
    }

    private void createCheckoutLayout() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(true);
        mainLayout.setSpacing(true);

        VerticalLayout summaryCard = createOrderSummary();
        styleCard(summaryCard);

        VerticalLayout paymentCard = createPaymentSection();
        styleCard(paymentCard);

        mainLayout.add(summaryCard, paymentCard);
        mainLayout.setFlexGrow(1, summaryCard);
        mainLayout.setFlexGrow(1, paymentCard);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(mainLayout);
    }

    private void styleCard(VerticalLayout card) {
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("padding", "var(--lumo-space-l)");
    }

    private VerticalLayout createOrderSummary() {
        VerticalLayout summary = new VerticalLayout();
        summary.setSpacing(true);
        summary.setPadding(false);

        summary.add(createSummaryTitle("Ringkasan Pesanan"));
        summary.add(createOrderItemsList());
        summary.add(new Hr());
        summary.add(createSummaryDetails());

        return summary;
    }

    private H2 createSummaryTitle(String title) {
        H2 header = new H2(title);
        header.getStyle()
                .set("margin-top", "0")
                .set("color", "var(--lumo-primary-text-color)");
        return header;
    }

    private VerticalLayout createOrderItemsList() {
        VerticalLayout itemsList = new VerticalLayout();
        itemsList.setSpacing(true);
        itemsList.setPadding(false);

        cartItems.forEach(item -> itemsList.add(createOrderItemRow(item)));
        return itemsList;
    }

    private HorizontalLayout createOrderItemRow(CartItem item) {
        HorizontalLayout itemRow = new HorizontalLayout();
        itemRow.setWidthFull();
        itemRow.setJustifyContentMode(JustifyContentMode.BETWEEN);
        itemRow.setAlignItems(Alignment.CENTER);

        Span name = new Span(item.getProductName() + " × " + item.getQuantity());
        Span price = new Span(AppLayoutNavbar.formatRupiah(item.getPrice() * item.getQuantity()));
        price.getStyle().set("font-weight", "500");

        itemRow.add(name, price);
        return itemRow;
    }

    private VerticalLayout createSummaryDetails() {
        VerticalLayout summaryRows = new VerticalLayout();
        summaryRows.setSpacing(true);
        summaryRows.setPadding(false);

        summaryRows.add(createSummaryRow("Subtotal", AppLayoutNavbar.formatRupiah(subtotal)));

        if (activePromo != null) {
            HorizontalLayout promoRow = createSummaryRow(
                    "Diskon (" + (activePromo.getDiscount_value() * 100) + "%)",
                    "-" + AppLayoutNavbar.formatRupiah(discountAmount)
            );
            promoRow.getStyle().set("color", "var(--lumo-success-text-color)");
            summaryRows.add(promoRow);
        }

        summaryRows.add(createSummaryRow("Pajak (5%)", AppLayoutNavbar.formatRupiah(tax)));
        summaryRows.add(new Hr());
        summaryRows.add(createSummaryRow("Total", AppLayoutNavbar.formatRupiah(totalAmount), true));

        return summaryRows;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, false);
    }

    private HorizontalLayout createSummaryRow(String label, String value, boolean bold) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.setAlignItems(Alignment.CENTER);

        Span labelSpan = new Span(label);
        Span valueSpan = new Span(value);

        if (bold) {
            labelSpan.getStyle().set("font-weight", "600");
            valueSpan.getStyle().set("font-weight", "600")
                    .set("color", "var(--lumo-primary-text-color)")
                    .set("font-size", "var(--lumo-font-size-l)");
        }

        row.add(labelSpan, valueSpan);
        return row;
    }

    private VerticalLayout createPaymentSection() {
        VerticalLayout paymentSection = new VerticalLayout();
        paymentSection.setSpacing(true);
        paymentSection.setPadding(false);

        paymentSection.add(createSummaryTitle("Pembayaran"));
        paymentSection.add(createPaymentForm());

        return paymentSection;
    }

    private VerticalLayout createPaymentForm() {
        ComboBox<String> paymentMethod = createPaymentMethodComboBox();
        TextField paidAmountField = createPaidAmountField();

        paymentMethod.addValueChangeListener(e ->
                paidAmountField.setVisible("Cash".equals(e.getValue()))
        );

        Button payButton = createPayButton(paymentMethod, paidAmountField);

        VerticalLayout paymentForm = new VerticalLayout(
                paymentMethod,
                paidAmountField,
                createPaymentInfoText(),
                payButton
        );
        paymentForm.setSpacing(true);
        paymentForm.setPadding(false);

        return paymentForm;
    }

    private ComboBox<String> createPaymentMethodComboBox() {
        ComboBox<String> paymentMethod = new ComboBox<>("Pilih Metode Pembayaran");
        paymentMethod.setItems("Cash", "EMoney");
        paymentMethod.setPlaceholder("Pilih metode pembayaran");
        paymentMethod.setRequiredIndicatorVisible(true);
        paymentMethod.setWidthFull();
        return paymentMethod;
    }

    private TextField createPaidAmountField() {
        TextField paidAmountField = new TextField("Jumlah Uang Dibayarkan");
        paidAmountField.setVisible(false);
        paidAmountField.setPattern("[0-9]*");
        paidAmountField.setPrefixComponent(new Span("Rp"));
        paidAmountField.setWidthFull();
        return paidAmountField;
    }

    private Div createPaymentInfoText() {
        Div paymentInfo = new Div();
        paymentInfo.setText("Pesanan Anda akan segera diproses setelah pembayaran berhasil.");
        paymentInfo.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");
        return paymentInfo;
    }

    private Button createPayButton(ComboBox<String> paymentMethod, TextField paidAmountField) {
        Button payButton = new Button("Bayar Sekarang", VaadinIcon.CREDIT_CARD.create());
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        payButton.setWidthFull();
        payButton.addClickListener(e -> handlePayment(paymentMethod, paidAmountField));
        return payButton;
    }

    private void handlePayment(ComboBox<String> paymentMethod, TextField paidAmountField) {
        if (paymentMethod.isEmpty()) {
            showPaymentError("Pilih metode pembayaran terlebih dahulu");
            return;
        }

        if (isCashPaymentInsufficient(paymentMethod, paidAmountField)) {
            showPaymentError("Jumlah pembayaran kurang dari total");
            return;
        }

        double paidAmount = getPaidAmount(paymentMethod, paidAmountField);
        processPayment(paymentMethod.getValue().toLowerCase(), paidAmount);
        cartItems.clear();
    }

    private boolean isCashPaymentInsufficient(ComboBox<String> paymentMethod, TextField paidAmountField) {
        return "Cash".equals(paymentMethod.getValue()) &&
                (paidAmountField.isEmpty() || Double.parseDouble(paidAmountField.getValue()) < totalAmount);
    }

    private double getPaidAmount(ComboBox<String> paymentMethod, TextField paidAmountField) {
        return "Cash".equals(paymentMethod.getValue()) ?
                Double.parseDouble(paidAmountField.getValue()) :
                totalAmount;
    }

    private void showPaymentError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void processPayment(String paymentMethod, double paidAmount) {
        try {
            String orderId = createOrder(paymentMethod);
            if (orderId == null) throw new Exception("Gagal membuat pesanan");

            boolean transactionSuccess = createTransaction(orderId, paymentMethod, paidAmount);
            if (!transactionSuccess) throw new Exception("Gagal memproses pembayaran");

            clearSessionData();
            showSuccessDialog(orderId, paymentMethod, paidAmount);
        } catch (Exception e) {
            showPaymentError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createOrder(String paymentMethod) throws SQLException {
        Users currentUser = (Users) VaadinSession.getCurrent().getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "1";

        Orders order = new Orders();
        order.setUser_id(Integer.valueOf(userId));
        order.setCreated_by(Integer.valueOf(userId));
        order.setStatus("accepted");
        order.setOrder_type("online");
        order.setTotal_price(totalAmount + (totalAmount * 0.05) - discountAmount);

        if (activePromo != null) {
            order.setPromo_id(activePromo.getId());
        }

        order.setFinal_price(totalAmount);

        return orderDAO.createOrderWithItems(order, createOrderItems());
    }

    private List<OrderItems> createOrderItems() {
        return cartItems.stream()
                .map(this::convertToOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItems convertToOrderItem(CartItem item) {
        OrderItems orderItem = new OrderItems();
        orderItem.setProduct_id(item.getProductId());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setPrice(item.getPrice());
        orderItem.setSubtotal(item.getPrice() * item.getQuantity());
        return orderItem;
    }

    private boolean createTransaction(String orderId, String paymentMethod, double paidAmount) throws SQLException {
        Transactions transaction = new Transactions();
        transaction.setOrder_id(orderId);
        transaction.setPayment_method(paymentMethod);
        transaction.setPaid_amount(paidAmount);
        transaction.setChange_returned(paidAmount - totalAmount);
        return transactionsDAO.createTransaction(transaction);
    }

    private void clearSessionData() {
        VaadinSession.getCurrent().setAttribute("cart", null);
        VaadinSession.getCurrent().setAttribute("activePromo", null);
    }

    private void showSuccessDialog(String orderId, String paymentMethod, double paidAmount) {
        Dialog successDialog = new Dialog();
        successDialog.setCloseOnOutsideClick(false);
        successDialog.setCloseOnEsc(false);
        successDialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(true);

        content.add(createSuccessIcon());
        content.add(createSuccessTitle());
        content.add(createTransactionDetails(orderId, paymentMethod, paidAmount));
        content.add(createThankYouMessage());
        content.add(createCloseButton(successDialog));

        successDialog.add(content);
        successDialog.open();
    }

    private Icon createSuccessIcon() {
        Icon successIcon = VaadinIcon.CHECK_CIRCLE.create();
        successIcon.setSize("64px");
        successIcon.setColor("var(--lumo-success-text-color)");
        return successIcon;
    }

    private H2 createSuccessTitle() {
        H2 title = new H2("Pembayaran Berhasil!");
        title.getStyle()
                .set("color", "var(--lumo-success-text-color)")
                .set("margin-top", "0");
        return title;
    }

    private VerticalLayout createTransactionDetails(String orderId, String paymentMethod, double paidAmount) {
        VerticalLayout details = new VerticalLayout();
        details.setSpacing(true);
        details.setPadding(false);
        details.setWidthFull();

        details.add(createDetailRow("Nomor Pesanan", orderId));
        details.add(createDetailRow("Metode Pembayaran", paymentMethod.equals("cash") ? "Cash" : "E-Money"));
        details.add(createDetailRow("Total Pembayaran", AppLayoutNavbar.formatRupiah(totalAmount)));

        if ("cash".equals(paymentMethod)) {
            details.add(createDetailRow("Dibayarkan", AppLayoutNavbar.formatRupiah(paidAmount)));
            details.add(createDetailRow("Kembalian", AppLayoutNavbar.formatRupiah(paidAmount - totalAmount)));
        }

        return details;
    }

    private Div createThankYouMessage() {
        Div thankYou = new Div();
        thankYou.setText("Terima kasih telah berbelanja di kopi-In!");
        thankYou.getStyle()
                .set("font-size", "var(--lumo-font-size-m)")
                .set("text-align", "center");
        return thankYou;
    }

    private Button createCloseButton(Dialog dialog) {
        Button closeButton = new Button("Selesai", VaadinIcon.CHECK.create(), e -> {
            dialog.close();
            UI.getCurrent().navigate("customer");
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.setWidthFull();
        return closeButton;
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.setAlignItems(Alignment.CENTER);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "var(--lumo-secondary-text-color)");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-weight", "500");

        row.add(labelSpan, valueSpan);
        return row;
    }
}