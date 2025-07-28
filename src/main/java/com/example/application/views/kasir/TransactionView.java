package com.example.application.views.kasir;

import com.example.application.dao.OrderDAO;
import com.example.application.dao.ProductDAO;
import com.example.application.dao.PromoDAO;
import com.example.application.models.Orders;
import com.example.application.models.OrderItems;
import com.example.application.models.Promo;
import com.example.application.views.MainLayout;
import com.example.application.views.kasir.utils.ReceiptGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;

import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Transactions | Kopi.in")
@Route(value = "kasir/transactions", layout = MainLayout.class)
public class TransactionView extends VerticalLayout {
    private final Grid<Orders> grid;
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final PromoDAO promoDAO;
    private final TextField filterField;

    public TransactionView() {
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        promoDAO = new PromoDAO();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        setPadding(true);

        // Main container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("1200px");
        mainContainer.setWidth("100%");
        mainContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "20px")
                .set("box-shadow", "0 0 20px rgba(0,0,0,0.1)")
                .set("padding", "2rem");

        // Header
        H2 header = new H2("Transaction History");
        header.getStyle().set("color", "#6B4423").set("margin", "0");

        // Filter
        filterField = new TextField();
        filterField.setPlaceholder("Search transactions...");
        filterField.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(e -> updateList());
        filterField.setWidth("300px");

        // Grid
        grid = new Grid<>();
        grid.addColumn(order -> order.getId().substring(0, 8))
                .setHeader("Order ID")
                .setFlexGrow(1);
        grid.addColumn(order -> order.getCreated_at().toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .setHeader("Date")
                .setFlexGrow(1);
        grid.addColumn(Orders::getOrder_type)
                .setHeader("Type")
                .setFlexGrow(1);
        grid.addColumn(Orders::getTotal_price)
                .setHeader("Total")
                .setFlexGrow(1);
        grid.addColumn(Orders::getStatus)
                .setHeader("Status")
                .setFlexGrow(1);
        
        // Actions column
        grid.addComponentColumn(order -> {
            HorizontalLayout actions = new HorizontalLayout();
            
            Button viewButton = new Button("View", VaadinIcon.EYE.create());
            viewButton.getStyle()
                    .set("background", "#6B4423")
                   .set("color", "white");
            viewButton.addClickListener(e -> showOrderDetails(order));

            Button printButton = new Button("Print", VaadinIcon.PRINT.create());
            printButton.getStyle()
                    .set("background", "#8B593E")
                    .set("color", "white");
            printButton.addClickListener(e -> printReceipt(order));
            
            actions.add(viewButton, printButton);
            return actions;
        }).setHeader("Actions").setFlexGrow(1);

        grid.setItems(orderDAO.getOrdersByStatus("COMPLETED"));

        // Layout
        HorizontalLayout toolBar = new HorizontalLayout(filterField);
        toolBar.setWidthFull();
        toolBar.setJustifyContentMode(JustifyContentMode.START);

        mainContainer.add(header, toolBar, grid);
        add(mainContainer);

        updateList();
    }

    private void updateList() {
        List<Orders> orders = orderDAO.getOrdersByStatus("COMPLETED");
        if (!filterField.isEmpty()) {
            String filter = filterField.getValue().toLowerCase();
            orders.removeIf(order -> 
                !order.getId().toLowerCase().contains(filter) &&
                !order.getOrder_type().toLowerCase().contains(filter) &&
                !order.getStatus().toLowerCase().contains(filter)
            );
        }
        grid.setItems(orders);
    }

    private void showOrderDetails(Orders order) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H2 title = new H2("Order Details");
        title.getStyle().set("color", "#6B4423").set("margin", "0");

        List<OrderItems> items = orderDAO.getOrderItems(order.getId());
        Promo promo = order.getPromo_id() != null ? promoDAO.getPromoById(order.getPromo_id()) : null;

        // Generate receipt-like view
        VerticalLayout receiptLayout = ReceiptGenerator.generateReceipt(order, items, promo);
        
        Button closeButton = new Button("Close", VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeButton.getStyle()
                .set("margin-top", "16px")
                .set("background", "#6B4423")
                .set("color", "white");

        content.add(title, receiptLayout, closeButton);
        dialog.add(content);
        dialog.open();
    }

    private void printReceipt(Orders order) {
        List<OrderItems> items = orderDAO.getOrderItems(order.getId());
        Promo promo = order.getPromo_id() != null ? promoDAO.getPromoById(order.getPromo_id()) : null;

        // Generate receipt
        VerticalLayout receipt = ReceiptGenerator.generateReceipt(order, items, promo);

        // In a real application, you would implement actual printing functionality here
        // For now, we'll just show a notification
        Notification.show("Receipt printed successfully!", 3000, Notification.Position.MIDDLE);
    }
} 