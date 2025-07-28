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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

@PageTitle("New Order | Kopi.in")
@Route(value = "kasir/order", layout = MainLayout.class)
public class OrderView extends VerticalLayout {
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final PromoDAO promoDAO;
    private final Grid<OrderItems> orderGrid;
    private final List<OrderItems> currentOrderItems;
    private double totalAmount = 0.0;
    private final H2 totalDisplay;
    private final ComboBox<Products> productComboBox;
    private final ComboBox<Promo> promoComboBox;

    public OrderView() {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        promoDAO = new PromoDAO();
        currentOrderItems = new ArrayList<>();
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        setPadding(true);

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("1200px");
        mainContainer.setWidth("100%");
        mainContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "20px")
                .set("box-shadow", "0 0 20px rgba(0,0,0,0.1)")
                .set("padding", "2rem");

        H2 header = new H2("New Order");
        header.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XXXLARGE);
        header.getStyle().set("color", "#6B4423");

        HorizontalLayout productSelectionLayout = new HorizontalLayout();
        productSelectionLayout.setWidthFull();
        productSelectionLayout.setAlignItems(Alignment.BASELINE);

        productComboBox = new ComboBox<>("Select Product");
        productComboBox.setItems(productDAO.getAllProducts());
        productComboBox.setItemLabelGenerator(Products::getName);
        productComboBox.setWidth("300px");

        IntegerField quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setStepButtonsVisible(true);
        quantityField.setWidth("150px");

        Button addButton = new Button("Add to Order", VaadinIcon.PLUS.create());
        addButton.getStyle()
                .set("background", "#6B4423")
                .set("color", "white")
                .set("cursor", "pointer");

        productSelectionLayout.add(productComboBox, quantityField, addButton);
        productSelectionLayout.setFlexGrow(1, productComboBox);

        // Order Grid
        orderGrid = new Grid<>();
        orderGrid.addColumn(item -> {
            Products product = productDAO.getProductById(item.getProduct_id());
            return product != null ? product.getName() : "";
        }).setHeader("Product");
        orderGrid.addColumn(OrderItems::getQuantity).setHeader("Quantity");
        orderGrid.addColumn(OrderItems::getPrice).setHeader("Price");
        orderGrid.addColumn(item -> item.getPrice() * item.getQuantity()).setHeader("Subtotal");

        // Remove button column
        orderGrid.addComponentColumn(item -> {
            Button removeButton = new Button("Remove", VaadinIcon.TRASH.create());
            removeButton.getStyle().set("color", "red");
            removeButton.addClickListener(e -> removeItem(item));
            return removeButton;
        });

        // Total and Actions area
        HorizontalLayout totalLayout = new HorizontalLayout();
        totalLayout.setWidthFull();
        totalLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        totalLayout.setAlignItems(Alignment.CENTER);

        totalDisplay = new H2("Total: Rp 0");
        totalDisplay.getStyle().set("color", "#6B4423");

        promoComboBox = new ComboBox<>("Apply Promo");
        promoComboBox.setItems(promoDAO.getAllPromos());
        promoComboBox.setItemLabelGenerator(Promo::getName);
        promoComboBox.setWidth("250px");

        Button checkoutButton = new Button("Proceed to Payment", VaadinIcon.MONEY.create());
        checkoutButton.getStyle()
                .set("background", "#6B4423")
                .set("color", "white")
                .set("cursor", "pointer");

        totalLayout.add(totalDisplay, promoComboBox, checkoutButton);

        // Add components to main container
        mainContainer.add(
                header,
                productSelectionLayout,
                orderGrid,
                totalLayout
        );

        add(mainContainer);

        // Event Handlers
        addButton.addClickListener(e -> addItemToOrder(
                productComboBox.getValue(),
                quantityField.getValue()
        ));

        checkoutButton.addClickListener(e -> proceedToCheckout());
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
} 