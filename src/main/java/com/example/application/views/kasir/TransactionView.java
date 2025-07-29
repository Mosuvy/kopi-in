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
        mainContainer.setMaxWidth("1200px");
        mainContainer.setWidth("100%");
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
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.05)");

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
        grid.addColumn(order -> String.format("Rp %.2f", order.getTotal_price()))
                .setHeader("Total")
                .setFlexGrow(1);

        // Status column with enhanced styling
        grid.addComponentColumn(order -> {
            Button statusBadge = new Button(order.getStatus());
            String color;
            switch (order.getStatus().toUpperCase()) {
                case "COMPLETED":
                    color = "#2E7D32"; // Green
                    break;
                case "PENDING":
                    color = "#ED6C02"; // Orange
                    break;
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
            
            Button viewButton = createActionButton("View", VaadinIcon.EYE, "#8B4513", e -> showOrderDetails(order));
            Button printButton = createActionButton("Print", VaadinIcon.PRINT, "#A0522D", e -> printReceipt(order));
            
            actions.add(viewButton, printButton);
            return actions;
        }).setHeader("Actions").setFlexGrow(1);

        grid.setItems(orderDAO.getOrdersByStatus("COMPLETED"));

        gridCard.add(grid);
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
        receiptLayout.getStyle()
                .set("background", "white")
                .set("border-radius", "15px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.1)")
                .set("padding", "20px");
        
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

        content.add(title, receiptLayout, closeButton);
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
} 