package com.example.application.views.kasir.utils;

import com.example.application.dao.ProductDAO;
import com.example.application.models.OrderItems;
import com.example.application.models.Orders;
import com.example.application.models.Products;
import com.example.application.models.Promo;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptGenerator {
    private static final ProductDAO productDAO = new ProductDAO();

    public static VerticalLayout generateReceipt(Orders order, List<OrderItems> items, Promo appliedPromo) {
        VerticalLayout receipt = new VerticalLayout();
        receipt.setSpacing(false);
        receipt.setPadding(true);
        receipt.getStyle()
                .set("background", "white")
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("max-width", "400px")
                .set("font-family", "monospace");

        // Header
        H3 header = new H3("Kopi.in");
        header.getStyle()
                .set("text-align", "center")
                .set("margin", "0")
                .set("color", "#6B4423");
        receipt.add(header);

        Paragraph subHeader = new Paragraph("Your Coffee, Your Moment");
        subHeader.getStyle()
                .set("text-align", "center")
                .set("margin", "0")
                .set("font-size", "0.9em")
                .set("color", "#8B593E");
        receipt.add(subHeader);

        receipt.add(new Hr());

        // Order details
        Paragraph orderDetails = new Paragraph(String.format(
                "Order ID: %s\nDate: %s\nType: %s",
                order.getId(),
                order.getCreated_at().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                formatOrderType(order.getOrder_type())
        ));
        orderDetails.getStyle().set("white-space", "pre-line");
        receipt.add(orderDetails);

        receipt.add(new Hr());

        // Items
        Div itemsContainer = new Div();
        itemsContainer.getStyle().set("width", "100%");

        for (OrderItems item : items) {
            Products product = productDAO.getProductById(item.getProduct_id());
            if (product != null) {
                Paragraph itemLine = new Paragraph(String.format(
                        "%-20s %3dx %10.2f\n%33.2f",
                        truncateString(product.getName(), 20),
                    item.getQuantity(),
                        item.getPrice(),
                        item.getQuantity() * item.getPrice()
                ));
                itemLine.getStyle()
                        .set("white-space", "pre")
                        .set("font-family", "monospace")
                        .set("margin", "4px 0");
                itemsContainer.add(itemLine);
        }
        }
        receipt.add(itemsContainer);

        receipt.add(new Hr());

        // Totals
        Div totalsContainer = new Div();
        totalsContainer.getStyle().set("width", "100%");

        Paragraph subtotal = new Paragraph(String.format(
                "Subtotal:%31.2f",
                order.getTotal_price()
        ));
        subtotal.getStyle()
                .set("white-space", "pre")
                .set("font-family", "monospace")
                .set("margin", "4px 0");
        totalsContainer.add(subtotal);

        if (appliedPromo != null) {
            Double discountValue = appliedPromo.getDiscount_value();
            int percent = (discountValue != null) ? (int) Math.round(discountValue * 100) : 0;
            double discountAmount = order.getTotal_price() - (order.getFinal_price() != null ? order.getFinal_price() : order.getTotal_price());
            Paragraph discount = new Paragraph(String.format(
                    "Discount (%d%%):%26.2f",
                    percent,
                    discountAmount
            ));
            discount.getStyle()
                    .set("white-space", "pre")
                    .set("font-family", "monospace")
                    .set("margin", "4px 0")
                    .set("color", "#4CAF50");
            totalsContainer.add(discount);
        }

        Paragraph total = new Paragraph(String.format(
                "TOTAL:%34.2f",
                order.getFinal_price()
        ));
        total.getStyle()
                .set("white-space", "pre")
                .set("font-family", "monospace")
                .set("margin", "4px 0")
                .set("font-weight", "bold");
        totalsContainer.add(total);

        receipt.add(totalsContainer);

        receipt.add(new Hr());

        // Footer
        Paragraph footer = new Paragraph("Thank you for choosing Kopi.in!\nSee you again soon!");
        footer.getStyle()
                .set("text-align", "center")
                .set("margin-top", "16px")
                .set("font-style", "italic")
                .set("white-space", "pre-line");
        receipt.add(footer);

        return receipt;
    }

    private static String truncateString(String str, int length) {
        if (str.length() <= length) {
            return String.format("%-" + length + "s", str);
        }
        return str.substring(0, length - 3) + "...";
    }

    private static String formatOrderType(String orderType) {
        if (orderType == null) return "N/A";
        return orderType.replace("_", " ").toLowerCase();
    }
} 