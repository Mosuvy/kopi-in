package com.example.application.views.kasir;

import com.example.application.dao.OrderDAO;
import com.example.application.dao.ProductDAO;
import com.example.application.dao.PromoDAO;
import com.example.application.models.Orders;
import com.example.application.models.OrderItems;
import com.example.application.models.Promo;
import com.example.application.views.MainLayout;
import com.example.application.views.kasir.utils.ReceiptGenerator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ClickEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Transactions | Kopi.in")
@Route(value = "kasir/transactions", layout = MainLayout.class)
public class TransactionView extends VerticalLayout {
        private Grid<Orders> grid;
        private OrderDAO orderDAO;
        private ProductDAO productDAO;
        private PromoDAO promoDAO;
        private TextField filterField;

    public TransactionView() {
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        promoDAO = new PromoDAO();

        setSizeFull();
        setSpacing(false);
        setPadding(false);
        getStyle().set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)");

        add(createHeader(), createMainContent());
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #795548 50%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "25px 30px")
                .set("margin-bottom", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Background pattern overlay
        Div pattern = new Div();
        pattern.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-image", "radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 1px, transparent 1px)")
                .set("background-size", "30px 30px")
                .set("opacity", "0.4");
        header.getElement().appendChild(pattern.getElement());

        H2 title = new H2("Transaction History");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        Span timeText = new Span(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeText.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8");

        header.add(title, timeText);
        return header;
    }

    private Component createMainContent() {
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setWidthFull();
        mainContainer.setHeightFull();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);

        // Search Card
        VerticalLayout searchCard = new VerticalLayout();
        searchCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px 25px 15px");

        filterField = new TextField();
        filterField.setPlaceholder("Search transactions...");
        filterField.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(e -> updateList());
        filterField.setWidth("300px");
        filterField.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        searchCard.add(filterField);

                // Transactions Grid Card
                VerticalLayout gridCard = new VerticalLayout();
                gridCard.getStyle()
                                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                                .set("border-radius", "20px")
                                .set("padding", "25px")
                                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                                .set("margin", "0 15px");

                // Enhanced grid styling
                grid = new Grid<>();
                grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
                grid.getStyle()
                                .set("border-radius", "15px")
                                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.05)");
                // Hapus overflow:hidden agar scroll bisa

                grid.addColumn(order -> order.getId())
                                .setHeader("Order ID")
                                .setFlexGrow(1);
                grid.addColumn(order -> order.getCreated_at().toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                                .setHeader("Date")
                                .setFlexGrow(1);
                grid.addColumn(Orders::getOrder_type)
                                .setHeader("Type")
                                .setFlexGrow(1);
                grid.addColumn(order -> String.format("Rp %.2f", order.getTotal_price()))
                                .setHeader("Total")
                                .setFlexGrow(1);
                // Promo column
                grid.addColumn(order -> {
                        if (order.getPromo_id() != null) {
                                Promo promo = promoDAO.getPromoById(order.getPromo_id());
                                return promo != null ? promo.getName() : "-";
                        } else {
                                return "-";
                        }
                }).setHeader("Promo").setFlexGrow(1);
                // Final Price column
                grid.addColumn(order -> String.format("Rp %.2f", order.getFinal_price() != null ? order.getFinal_price() : order.getTotal_price()))
                                .setHeader("Final Price")
                                .setFlexGrow(1);
                // Status column with DB enums
                grid.addComponentColumn(order -> {
                        String status = order.getStatus();
                        Button statusBadge = new Button(status.substring(0, 1).toUpperCase() + status.substring(1));
                        String color;
                        switch (status) {
                                case "accepted":
                                        color = "#2E7D32"; // Green
                                        break;
                                case "rejected":
                                        color = "#D32F2F"; // Red
                                        break;
                                case "processing":
                                default:
                                        color = "#1976D2"; // Blue
                        }
                        statusBadge.getStyle()
                                        .set("background", color)
                                        .set("color", "white")
                                        .set("border-radius", "20px")
                                        .set("padding", "5px 15px")
                                        .set("font-size", "12px")
                                        .set("font-weight", "600")
                                        .set("box-shadow", "0 2px 8px " + color + "40");
                        return statusBadge;
                }).setHeader("Status").setFlexGrow(1);
                // Actions column with enhanced styling
                grid.addComponentColumn(order -> {
                        HorizontalLayout actions = new HorizontalLayout();
                        actions.setSpacing(true);
                        // Redesigned: modern, spacing, color
                        Button viewButton = createActionButton("View", VaadinIcon.EYE, "#8B4513", e -> showOrderDetails(order));
                        Button printButton = createActionButton("Print", VaadinIcon.PRINT, "#A0522D", e -> printReceipt(order));
                        actions.add(viewButton, printButton);
                        // Tambahkan tombol Accept jika status processing
                        if ("processing".equals(order.getStatus())) {
                                Button acceptButton = new Button("Accept", VaadinIcon.CHECK_CIRCLE.create());
                                acceptButton.getStyle()
                                        .set("background", "linear-gradient(135deg, #388E3C, #4CAF50)")
                                        .set("color", "white")
                                        .set("border-radius", "8px")
                                        .set("font-weight", "600")
                                        .set("box-shadow", "0 4px 12px #388E3C40")
                                        .set("margin-left", "8px")
                                        .set("transition", "all 0.3s ease");
                                acceptButton.getElement().addEventListener("mouseenter", e ->
                                        acceptButton.getStyle()
                                                .set("transform", "translateY(-2px)")
                                                .set("box-shadow", "0 6px 15px #388E3C60")
                                );
                                acceptButton.getElement().addEventListener("mouseleave", e ->
                                        acceptButton.getStyle()
                                                .set("transform", "translateY(0)")
                                                .set("box-shadow", "0 4px 12px #388E3C40")
                                );
                                acceptButton.addClickListener(e -> {
                                        boolean success = orderDAO.updateOrderStatus(order.getId(), "accepted");
                                        if (success) {
                                                Notification.show("Order accepted!", 3000, Notification.Position.TOP_CENTER)
                                                        .addThemeName("success");
                                                updateList();
                                        } else {
                                                Notification.show("Failed to accept order", 3000, Notification.Position.TOP_CENTER)
                                                        .addThemeName("error");
                                        }
                                });
                                actions.add(acceptButton);
                        }
                        return actions;
                }).setHeader("Actions").setFlexGrow(1);
                // Gabungkan semua status dari DB
                List<Orders> allOrders = new java.util.ArrayList<>();
                allOrders.addAll(orderDAO.getOrdersByStatus("processing"));
                allOrders.addAll(orderDAO.getOrdersByStatus("accepted"));
                allOrders.addAll(orderDAO.getOrdersByStatus("rejected"));
                grid.setItems(allOrders);

                // Tambahkan wrapper scrollable dengan dukungan touch/slide
                Div scrollWrapper = new Div(grid);
                scrollWrapper.getStyle()
                        .set("overflow-x", "auto")
                        .set("width", "100%")
                        .set("touch-action", "pan-x")
                        .set("-webkit-overflow-scrolling", "touch")
                        .set("cursor", "grab");
                grid.getStyle()
                        .set("min-width", "900px"); // pastikan grid lebih lebar dari wrapper jika kolom banyak
                gridCard.add(scrollWrapper);
                mainContainer.add(searchCard, gridCard);
                updateList();
                return mainContainer;
    }

    private Button createActionButton(String text, VaadinIcon icon, String color, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(text, icon.create());
        button.getStyle()
                .set("background", "linear-gradient(135deg, " + color + ", " + color + "dd)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px " + color + "40")
                .set("transition", "all 0.3s ease");

        button.getElement().addEventListener("mouseenter", e ->
            button.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 15px " + color + "60")
        );

        button.getElement().addEventListener("mouseleave", e ->
            button.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 12px " + color + "40")
        );

        button.addClickListener(listener);
        return button;
    }

    private void showOrderDetails(Orders order) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        // Enhanced dialog styling
        dialog.getElement().getStyle()
                .set("border-radius", "20px")
                .set("overflow", "hidden");

        content.getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)");

        H2 title = new H2("Order Details");
        title.getStyle()
                .set("color", "#8B4513")
                .set("margin", "0")
                .set("font-weight", "600")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.1)");

        List<OrderItems> items = orderDAO.getOrderItems(order.getId());
        Promo promo = order.getPromo_id() != null ? promoDAO.getPromoById(order.getPromo_id()) : null;

        // Generate receipt-like view with enhanced styling
        VerticalLayout receiptLayout = ReceiptGenerator.generateReceipt(order, items, promo);
        receiptLayout.setId("receipt-layout"); // Tambahkan ID agar mudah di-capture
        receiptLayout.getStyle()
                .set("background", "white")
                .set("border-radius", "15px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.1)")
                .set("padding", "20px");

        Button downloadImageButton = new Button("Download Gambar Struk", VaadinIcon.DOWNLOAD.create());
        downloadImageButton.getStyle()
                .set("background", "linear-gradient(135deg, #388E3C, #4CAF50)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px #388E3C40")
                .set("transition", "all 0.3s ease");
        downloadImageButton.addClickListener(e -> {
            // Jalankan JS untuk html2canvas
            receiptLayout.getElement().executeJs(
                "if (window.html2canvas) { html2canvas(document.getElementById('receipt-layout')).then(function(canvas) { " +
                "var link = document.createElement('a'); link.download = 'struk.png'; link.href = canvas.toDataURL(); link.click(); }); } else { alert('html2canvas belum dimuat!'); }"
            );
        });

        Button closeButton = new Button("Close", VaadinIcon.CLOSE.create());
        closeButton.getStyle()
                .set("margin-top", "16px")
                .set("background", "linear-gradient(135deg, #8B4513, #A0522D)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
                .set("transition", "all 0.3s ease");

        closeButton.getElement().addEventListener("mouseenter", e ->
            closeButton.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 15px rgba(139, 69, 19, 0.3)")
        );

        closeButton.getElement().addEventListener("mouseleave", e ->
            closeButton.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
        );

        closeButton.addClickListener(e -> dialog.close());

        content.add(title, receiptLayout, downloadImageButton, closeButton);
        dialog.add(content);
        dialog.open();
    }

    private void printReceipt(Orders order) {
        List<OrderItems> items = orderDAO.getOrderItems(order.getId());
        Promo promo = order.getPromo_id() != null ? promoDAO.getPromoById(order.getPromo_id()) : null;

        // Generate receipt with enhanced styling
        VerticalLayout receipt = ReceiptGenerator.generateReceipt(order, items, promo);

        // In a real application, you would implement actual printing functionality here
        Notification.show("Receipt printed successfully!", 3000, Notification.Position.MIDDLE);
    }

    private void updateList() {
                List<Orders> orders = new java.util.ArrayList<>();
                orders.addAll(orderDAO.getOrdersByStatus("processing"));
                orders.addAll(orderDAO.getOrdersByStatus("accepted"));
                orders.addAll(orderDAO.getOrdersByStatus("rejected"));
                if (!filterField.isEmpty()) {
                        String filter = filterField.getValue().toLowerCase();
                        orders.removeIf(order ->
                                !(order.getId() + " " + order.getOrder_type() + " " + order.getStatus()).toLowerCase().contains(filter)
                        );
                }
                grid.setItems(orders);
    }
} 