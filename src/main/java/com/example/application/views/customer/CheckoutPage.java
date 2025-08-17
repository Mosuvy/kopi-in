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
@Route(value = "checkout", layout = AppLayoutNavbar.class)
public class CheckoutPage extends VerticalLayout {

    private final OrderDAO orderDAO = new OrderDAO();
    private final TransactionsDAO transactionsDAO = new TransactionsDAO();
    private List<CartItem> cartItems;
    private Promo activePromo;
    private double totalAmount;
    private double discountAmount;

    public CheckoutPage() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Get cart items from session
        cartItems = (List<CartItem>) VaadinSession.getCurrent().getAttribute("cart");
        activePromo = (Promo) VaadinSession.getCurrent().getAttribute("activePromo");

        if (cartItems == null || cartItems.isEmpty()) {
            add(new H2("Keranjang Anda kosong"));
            add(new Button("Kembali ke Beranda", e -> UI.getCurrent().navigate("customer")));
            return;
        }

        // Calculate totals
        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        discountAmount = 0.0;
        if (activePromo != null) {
            discountAmount = subtotal * activePromo.getDiscount_value();
        }

        double tax = (subtotal - discountAmount) * 0.05; // 5% tax
        totalAmount = subtotal - discountAmount + tax;

        // Create order summary
        add(createOrderSummary(subtotal, tax));

        // Payment section
        add(createPaymentSection());
    }

    private VerticalLayout createOrderSummary(double subtotal, double tax) {
        VerticalLayout summary = new VerticalLayout();
        summary.setSpacing(false);
        summary.setPadding(false);

        summary.add(new H2("Ringkasan Pesanan"));

        // Order items list
        VerticalLayout itemsList = new VerticalLayout();
        itemsList.setSpacing(false);
        itemsList.setPadding(false);

        for (CartItem item : cartItems) {
            HorizontalLayout itemRow = new HorizontalLayout();
            itemRow.setWidthFull();
            itemRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

            Span name = new Span(item.getProductName() + " x" + item.getQuantity());
            Span price = new Span(AppLayoutNavbar.formatRupiah(item.getPrice() * item.getQuantity()));

            itemRow.add(name, price);
            itemsList.add(itemRow);
        }

        summary.add(itemsList);
        summary.add(new Hr());

        // Summary rows
        summary.add(createSummaryRow("Subtotal", AppLayoutNavbar.formatRupiah(subtotal)));

        if (activePromo != null) {
            summary.add(createSummaryRow(
                    "Diskon (" + (activePromo.getDiscount_value() * 100) + "%)",
                    "-" + AppLayoutNavbar.formatRupiah(discountAmount)
            ));
        }

        summary.add(createSummaryRow("Pajak (5%)", AppLayoutNavbar.formatRupiah(tax)));
        summary.add(createSummaryRow("Total", AppLayoutNavbar.formatRupiah(totalAmount), true));

        return summary;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, false);
    }

    private HorizontalLayout createSummaryRow(String label, String value, boolean bold) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        Span valueSpan = new Span(value);

        if (bold) {
            labelSpan.getStyle().set("font-weight", "bold");
            valueSpan.getStyle().set("font-weight", "bold");
        }

        row.add(labelSpan, valueSpan);
        return row;
    }

    private VerticalLayout createPaymentSection() {
        VerticalLayout paymentSection = new VerticalLayout();
        paymentSection.setSpacing(true);
        paymentSection.setPadding(false);

        paymentSection.add(new H2("Metode Pembayaran"));

        // Payment method selection
        ComboBox<String> paymentMethod = new ComboBox<>("Pilih Metode Pembayaran");
        paymentMethod.setItems("cash", "emoney");
        paymentMethod.setRequired(true);

        // Cash payment fields (only shown when "Tunai" is selected)
        TextField paidAmountField = new TextField("Jumlah Uang Dibayarkan");
        paidAmountField.setVisible(false);
        paidAmountField.setPattern("[0-9]*");

        paymentMethod.addValueChangeListener(e -> {
            paidAmountField.setVisible("Tunai".equals(e.getValue()));
        });

        Button payButton = new Button("Bayar Sekarang", VaadinIcon.CREDIT_CARD.create());
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        payButton.addClickListener(e -> {
            if (paymentMethod.isEmpty()) {
                Notification.show("Pilih metode pembayaran terlebih dahulu", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if ("Tunai".equals(paymentMethod.getValue()) &&
                    (paidAmountField.isEmpty() || Double.parseDouble(paidAmountField.getValue()) < totalAmount)) {
                Notification.show("Jumlah pembayaran kurang dari total", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            processPayment(paymentMethod.getValue(),
                    "Tunai".equals(paymentMethod.getValue()) ? Double.parseDouble(paidAmountField.getValue()) : totalAmount);
        });

        paymentSection.add(paymentMethod, paidAmountField, payButton);
        return paymentSection;
    }

    private void processPayment(String paymentMethod, double paidAmount) {
        try {
            // 1. Create Order
            String orderId = createOrder(paymentMethod);
            if (orderId == null) {
                throw new Exception("Gagal membuat pesanan");
            }

            // 2. Create Transaction
            Transactions transaction = new Transactions();
            transaction.setOrder_id(orderId);
            transaction.setPayment_method(paymentMethod);
            transaction.setPaid_amount(paidAmount);
            transaction.setChange_returned(paidAmount - totalAmount);

            boolean transactionSuccess = transactionsDAO.createTransaction(transaction);
            if (!transactionSuccess) {
                throw new Exception("Gagal memproses pembayaran");
            }

            // 3. Clear cart and promo
            VaadinSession.getCurrent().setAttribute("cart", null);
            VaadinSession.getCurrent().setAttribute("activePromo", null);

            // 4. Show success dialog
            showSuccessDialog(orderId, paymentMethod, paidAmount);

        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 3000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private String createOrder(String paymentMethod) throws SQLException {
        Users currentUser = (Users) VaadinSession.getCurrent().getAttribute("user");
        String userId = currentUser != null ? currentUser.getId() : "1";

        Orders order = new Orders();
        order.setUser_id(Integer.valueOf(userId));
        order.setCreated_by(Integer.valueOf(userId));
        order.setStatus("processing"); // Initial status
        order.setOrder_type("online");
        order.setTotal_price(totalAmount + (totalAmount * 0.05) - discountAmount);

        if (activePromo != null) {
            order.setPromo_id(activePromo.getId());
        }

        order.setFinal_price(totalAmount);

        // Convert cart items to order items
        List<OrderItems> orderItems = cartItems.stream()
                .map(item -> {
                    OrderItems orderItem = new OrderItems();
                    orderItem.setProduct_id(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setSubtotal(item.getPrice() * item.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Save to database
        return orderDAO.createOrderWithItems(order, orderItems);
    }

    private void showSuccessDialog(String orderId, String paymentMethod, double paidAmount) {
        Dialog successDialog = new Dialog();
        successDialog.setCloseOnOutsideClick(false);
        successDialog.setCloseOnEsc(false);

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

        H2 title = new H2("Pembayaran Berhasil!");
        title.getStyle().set("color", "var(--lumo-success-text-color)");

        Icon successIcon = VaadinIcon.CHECK_CIRCLE.create();
        successIcon.setSize("50px");
        successIcon.setColor("var(--lumo-success-text-color)");

        VerticalLayout details = new VerticalLayout();
        details.setSpacing(false);
        details.setPadding(false);

        details.add(createDetailRow("Nomor Pesanan", orderId));
        details.add(createDetailRow("Metode Pembayaran", paymentMethod));
        details.add(createDetailRow("Total Pembayaran", AppLayoutNavbar.formatRupiah(totalAmount)));

        if ("Tunai".equals(paymentMethod)) {
            details.add(createDetailRow("Dibayarkan", AppLayoutNavbar.formatRupiah(paidAmount)));
            details.add(createDetailRow("Kembalian", AppLayoutNavbar.formatRupiah(paidAmount - totalAmount)));
        }

        Button closeButton = new Button("Selesai", e -> {
            successDialog.close();
            UI.getCurrent().navigate("customer");
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        content.add(successIcon, title, details, closeButton);
        successDialog.add(content);
        successDialog.open();
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row.add(new Span(label), new Span(value));
        return row;
    }
}