package com.example.application.views.kasir;

import com.example.application.dao.OrderDAO;
import com.example.application.dao.ProductDAO;
import com.example.application.dao.PromoDAO;
import com.example.application.models.Orders;
import com.example.application.models.OrderItems;
import com.example.application.models.Products;
import com.example.application.models.Promo;
import com.example.application.views.MainLayout;
import com.example.application.views.kasir.components.PaymentDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("New Order | Kopi.in")
@Route(value = "kasir/order", layout = MainLayout.class)
public class OrderView extends VerticalLayout {
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private PromoDAO promoDAO;
    private Grid<OrderItems> orderGrid;
    private List<OrderItems> currentOrderItems;
    private double totalAmount = 0.0;
    private H2 totalDisplay;
    private ComboBox<Products> productComboBox;
    private ComboBox<Promo> promoComboBox;

    public OrderView() {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        promoDAO = new PromoDAO();
        currentOrderItems = new ArrayList<>();
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)");
        setPadding(false);
        setSpacing(false);

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

        H2 title = new H2("New Order");
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

        // Product Selection Card
        VerticalLayout productCard = new VerticalLayout();
        productCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px 25px 15px");

        HorizontalLayout productSelectionLayout = new HorizontalLayout();
        productSelectionLayout.setWidthFull();
        productSelectionLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        productComboBox = new ComboBox<>("Select Product");
        productComboBox.setItems(productDAO.getAllProducts());
        productComboBox.setItemLabelGenerator(Products::getName);
        productComboBox.setWidth("300px");
        productComboBox.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        IntegerField quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setStepButtonsVisible(true);
        quantityField.setWidth("150px");
        quantityField.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        Button addButton = new Button("Add to Order", VaadinIcon.PLUS.create());
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #A0522D)")
                .set("color", "white")
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
                .set("transition", "all 0.3s ease");

        addButton.getElement().addEventListener("mouseenter", e -> 
            addButton.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 15px rgba(139, 69, 19, 0.3)")
        );

        addButton.getElement().addEventListener("mouseleave", e -> 
            addButton.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
        );

        productSelectionLayout.add(productComboBox, quantityField, addButton);
        productSelectionLayout.setFlexGrow(1, productComboBox);

        productCard.add(productSelectionLayout);

        // Order Grid Card
        VerticalLayout gridCard = new VerticalLayout();
        gridCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px 25px 15px");

        // Enhanced grid styling
        orderGrid = new Grid<>();
        orderGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        orderGrid.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.05)");

        orderGrid.addColumn(item -> {
            Products product = productDAO.getProductById(item.getProduct_id());
            return product != null ? product.getName() : "";
        }).setHeader("Product").setFlexGrow(2);
        
        orderGrid.addColumn(OrderItems::getQuantity)
                .setHeader("Quantity")
                .setFlexGrow(1);
        
        orderGrid.addColumn(item -> String.format("Rp %.2f", item.getPrice()))
                .setHeader("Price")
                .setFlexGrow(1);
        
        orderGrid.addColumn(item -> String.format("Rp %.2f", item.getPrice() * item.getQuantity()))
                .setHeader("Subtotal")
                .setFlexGrow(1);

        // Enhanced remove button
        orderGrid.addComponentColumn(item -> {
            Button removeButton = new Button("Remove", VaadinIcon.TRASH.create());
            removeButton.getStyle()
                    .set("color", "#D32F2F")
                    .set("background", "transparent")
                    .set("border", "1px solid #D32F2F")
                    .set("border-radius", "8px")
                    .set("cursor", "pointer")
                    .set("transition", "all 0.3s ease");

            removeButton.getElement().addEventListener("mouseenter", e ->
                removeButton.getStyle()
                        .set("background", "#D32F2F")
                        .set("color", "white")
            );

            removeButton.getElement().addEventListener("mouseleave", e ->
                removeButton.getStyle()
                        .set("background", "transparent")
                        .set("color", "#D32F2F")
            );

            removeButton.addClickListener(e -> removeItem(item));
            return removeButton;
        }).setFlexGrow(1);

        gridCard.add(orderGrid);

        // Total and Actions Card
        HorizontalLayout totalCard = new HorizontalLayout();
        totalCard.setWidthFull();
        totalCard.setJustifyContentMode(JustifyContentMode.BETWEEN);
        totalCard.setAlignItems(FlexComponent.Alignment.CENTER);
        totalCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("margin", "0 15px");

        totalDisplay = new H2("Total: Rp 0");
        totalDisplay.getStyle()
                .set("color", "#8B4513")
                .set("font-weight", "700")
                .set("margin", "0")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.1)");

        promoComboBox = new ComboBox<>("Apply Promo");
        promoComboBox.setItems(promoDAO.getAllPromos());
        promoComboBox.setItemLabelGenerator(Promo::getName);
        promoComboBox.setWidth("250px");
        promoComboBox.getStyle()
                .set("--lumo-border-radius", "10px")
                .set("--lumo-primary-color", "#8B4513");

        Button checkoutButton = new Button("Proceed to Payment", VaadinIcon.MONEY.create());
        checkoutButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #A0522D)")
                .set("color", "white")
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("padding", "10px 20px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
                .set("transition", "all 0.3s ease");

        checkoutButton.getElement().addEventListener("mouseenter", e ->
            checkoutButton.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 15px rgba(139, 69, 19, 0.3)")
        );

        checkoutButton.getElement().addEventListener("mouseleave", e ->
            checkoutButton.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.2)")
        );

        totalCard.add(totalDisplay, promoComboBox, checkoutButton);

        mainContainer.add(productCard, gridCard, totalCard);

        // Event Handlers
        addButton.addClickListener(e -> addItemToOrder(
                productComboBox.getValue(),
                quantityField.getValue()
        ));

        checkoutButton.addClickListener(e -> proceedToCheckout());

        return mainContainer;
    }

    private void addItemToOrder(Products product, int quantity) {
        if (product == null) {
            Notification.show("Please select a product", 3000, Notification.Position.MIDDLE);
            return;
        }

        OrderItems item = new OrderItems();
        item.setProduct_id(product.getId());
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());

        currentOrderItems.add(item);
        updateOrderGrid();
        calculateTotal();
    }

    private void removeItem(OrderItems item) {
        currentOrderItems.remove(item);
        updateOrderGrid();
        calculateTotal();
    }

    private void updateOrderGrid() {
        orderGrid.setItems(currentOrderItems);
    }

    private void calculateTotal() {
        totalAmount = currentOrderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Promo selectedPromo = promoComboBox.getValue();
        if (selectedPromo != null && selectedPromo.getDiscount_value() != null) {
            double discount = selectedPromo.getDiscount_value();
            if (selectedPromo.getMin_purchase() == null || totalAmount >= selectedPromo.getMin_purchase()) {
                totalAmount = totalAmount * (1 - discount);
            }
        }

        totalDisplay.setText(String.format("Total: Rp %.2f", totalAmount));
    }

    private void proceedToCheckout() {
        if (currentOrderItems.isEmpty()) {
            Notification.show("Please add items to the order first", 3000, Notification.Position.MIDDLE);
            return;
        }

        PaymentDialog paymentDialog = new PaymentDialog(
                currentOrderItems,
                totalAmount,
                promoComboBox.getValue(),
                this::processOrder
        );
        paymentDialog.open();
    }

    private void processOrder(String paymentMethod) {
        Orders order = new Orders();
        order.setStatus("PENDING");
        order.setOrder_type("DINE_IN"); // You might want to make this configurable
        order.setTotal_price(totalAmount);
        order.setFinal_price(totalAmount);
        if (promoComboBox.getValue() != null) {
            order.setPromo_id(promoComboBox.getValue().getId());
        }

        String orderId = orderDAO.createOrder(order);
        if (orderId != null) {
            for (OrderItems item : currentOrderItems) {
                item.setOrder_id(orderId);
                orderDAO.addOrderItem(item);
            }
            Notification.show("Order processed successfully!", 3000, Notification.Position.MIDDLE);
            clearOrder();
        } else {
            Notification.show("Error processing order", 3000, Notification.Position.MIDDLE);
        }
    }

    private void clearOrder() {
        currentOrderItems.clear();
        updateOrderGrid();
        calculateTotal();
        productComboBox.clear();
        promoComboBox.clear();
    }

    private void updatePromoComboBox() {
        promoComboBox.clear();
        promoComboBox.setPlaceholder("Pilih Promo");
        promoComboBox.setItems(promoDAO.getAllPromos());
        promoComboBox.setItemLabelGenerator(promo -> {
            if (promo == null) return "";
            String discountText = String.format("%.0f%%", promo.getDiscount_value() * 100);
            String minPurchaseText = promo.getMin_purchase() != null ? 
                String.format(" (Min. Rp %.0f)", promo.getMin_purchase()) : "";
            return promo.getName() + " - " + discountText + minPurchaseText;
        });
    }
} 