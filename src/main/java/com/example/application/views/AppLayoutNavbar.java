package com.example.application.views;

import com.example.application.models.CartItem;
import com.example.application.models.ProductsC;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Side;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.crud.CrudEditorPosition;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
public class AppLayoutNavbar extends AppLayout {

    // TODO: Gunakan logo kopiin
    // TODO: pref dan refactor

    private Dialog logoutDialog;
    private Dialog cartDialog;

    public AppLayoutNavbar() {
        this.logoutDialog = createLogoutDialog();

        H1 title = new H1("KopiIn");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

        HorizontalLayout navigation = getNavigation();

        Avatar avatar = getAvatar();

        addToNavbar(title, navigation, avatar);

        // CART DIALOG (ganti dari drawer ke dialog kanan)
        cartDialog = new Dialog();
        cartDialog.setResizable(false);
        cartDialog.setModal(false);
        cartDialog.setCloseOnOutsideClick(true);
        cartDialog.getElement().getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("right", "0")
                .set("width", "400px")
                .set("height", "100%")
                .set("margin", "0");
        cartDialog.getElement().getThemeList().add("right-side-dialog");

        VerticalLayout cartContent = getCartContent(getCartFromSession());
        cartDialog.add(cartContent);

        // CART BUTTON
        Button cartButton = new Button(VaadinIcon.CART.create());
        cartButton.addClassName("cart-button");
        cartButton.addClickListener(e -> cartDialog.open());

        addToNavbar(new HorizontalLayout(cartButton));
    }

    private List<CartItem> getCartFromSession() {
        List<CartItem> cart = (List<CartItem>) VaadinSession.getCurrent().getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            VaadinSession.getCurrent().setAttribute("cart", cart);
        }
        return cart;
    }

    private VerticalLayout getCartContent(List<CartItem> cartItems) {
        VerticalLayout cartLayout = new VerticalLayout();
        cartLayout.setPadding(true);
        cartLayout.setSpacing(true);
        cartLayout.setWidth("100%");

        // Header
        H2 header = new H2("Your Cart");
        cartLayout.add(header);

        if (cartItems == null || cartItems.isEmpty()) {
            cartLayout.add(new Paragraph("Your cart is empty"));
            return cartLayout;
        }

        // List produk dalam cart
        for (CartItem item : cartItems) {
            HorizontalLayout itemLayout = new HorizontalLayout();
            itemLayout.setWidthFull();
            itemLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            itemLayout.setSpacing(true);

            // Gambar produk (jika ada)
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Image image = new Image(item.getImageUrl(), item.getProductName());
                image.setWidth("60px");
                image.setHeight("60px");
                itemLayout.add(image);
            }

            // Nama dan harga produk
            VerticalLayout infoLayout = new VerticalLayout();
            infoLayout.setSpacing(false);
            infoLayout.add(new Span(item.getProductName()));
            infoLayout.add(new Span(String.format("$%.2f", item.getPrice())));

            // Kontrol jumlah
            IntegerField quantityField = new IntegerField();
            quantityField.setValue(item.getQuantity());
            quantityField.setMin(1);
            quantityField.setMax(99);
            quantityField.setWidth("70px");
            quantityField.addValueChangeListener(e -> {
                item.setQuantity(e.getValue());
                // Di sini Anda bisa menambahkan logic untuk update cart
                // misalnya: cartService.updateQuantity(item.getProductId(), e.getValue());
            });

            // Tombol hapus
            Button removeButton = new Button(VaadinIcon.TRASH.create(), e -> {
                // Di sini Anda bisa menambahkan logic untuk remove item dari cart
                // misalnya: cartService.removeItem(item.getProductId());
                cartItems.remove(item);
                getUI().ifPresent(ui -> ui.getPage().reload());
            });
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            HorizontalLayout actionsLayout = new HorizontalLayout(quantityField, removeButton);
            actionsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            itemLayout.add(infoLayout);
            itemLayout.add(actionsLayout);
            itemLayout.setFlexGrow(1, infoLayout);

            cartLayout.add(itemLayout);
            cartLayout.add(new Hr());
        }

        // Ringkasan belanja
        double subtotal = cartItems.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        double shipping = 3.95; // Contoh biaya pengiriman
        double tax = subtotal * 0.1; // Contoh pajak 10%
        double total = subtotal + shipping + tax;

        VerticalLayout summaryLayout = new VerticalLayout();
        summaryLayout.setSpacing(false);
        summaryLayout.add(createSummaryRow("Subtotal", String.format("$%.2f", subtotal)));
        summaryLayout.add(createSummaryRow("Shipping", String.format("$%.2f", shipping)));
        summaryLayout.add(createSummaryRow("Tax", String.format("$%.2f", tax)));
        summaryLayout.add(createSummaryRow("Total", String.format("$%.2f", total), true));

        cartLayout.add(summaryLayout);

        // Bagian kupon
        TextField couponField = new TextField("Coupon Code");
        Button applyButton = new Button("Apply");
        HorizontalLayout couponLayout = new HorizontalLayout(couponField, applyButton);
        couponLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        cartLayout.add(couponLayout);

        // Tombol aksi
        Button continueButton = new Button("Continue Shopping", e -> {
            cartDialog.close();
        });

        Button checkoutButton = new Button("Checkout", e -> {
            // Navigasi ke halaman checkout
            UI.getCurrent().navigate("checkout");
        });
        checkoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(continueButton, checkoutButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        cartLayout.add(buttonsLayout);

        return cartLayout;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, false);
    }

    private HorizontalLayout createSummaryRow(String label, String value, boolean bold) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        Span valueSpan = new Span(value);

        if (bold) {
            labelSpan.getStyle().set("font-weight", "bold");
            valueSpan.getStyle().set("font-weight", "bold");
        }

        row.add(labelSpan, valueSpan);
        return row;
    }

    private Avatar getAvatar() {
        Avatar avatar = new Avatar();
        avatar.setName("User"); // Set nama default
        avatar.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER);
        avatar.getStyle().set("background-color", "var(--lumo-contrast-5pct)")
                .set("margin-right", "var(--lumo-space-l)")
                .set("cursor", "pointer");

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(avatar);
        contextMenu.setOpenOnClick(true);

        contextMenu.addItem(createUserInfo(), e -> {
        }).getElement().setAttribute("disabled", "true");

        contextMenu.add(new Hr());

        // Menu Profile
        contextMenu.addItem(createMenuItem(VaadinIcon.USER.create(), "Profile"), e -> {
            // TODO: Implement navigation to profile page
            System.out.println("Navigate to Profile");
        });

        // Menu History Order
        contextMenu.addItem(createMenuItem(VaadinIcon.CLIPBOARD_TEXT.create(), "History Order"), e -> {
            // TODO: Implement navigation to history page
            System.out.println("Navigate to History Order");
        });

        contextMenu.add(new Hr());

        // Menu Logout
        contextMenu.addItem(createMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout"), e -> {
            logoutDialog.open();
        });

        return avatar;
    }

    private HorizontalLayout createUserInfo() {
        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setSpacing(false);
        userInfo.setPadding(false);
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar smallAvatar = new Avatar();
        smallAvatar.setName("User");
        smallAvatar.setWidth("32px");
        smallAvatar.setHeight("32px");

        Span userName = new Span("User Name");
        userName.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        Span userEmail = new Span("user@example.com");
        userEmail.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        userInfo.add(smallAvatar);

        // Container untuk nama dan email
        var userDetails = new HorizontalLayout();
        userDetails.setSpacing(false);
        userDetails.setPadding(false);
        userDetails.getStyle().set("flex-direction", "column")
                .set("align-items", "flex-start")
                .set("margin-left", "var(--lumo-space-s)");

        userDetails.add(userName, userEmail);
        userInfo.add(userDetails);

        return userInfo;
    }

    private HorizontalLayout createMenuItem(com.vaadin.flow.component.Component icon, String text) {
        HorizontalLayout menuItem = new HorizontalLayout();
        menuItem.setSpacing(true);
        menuItem.setAlignItems(FlexComponent.Alignment.CENTER);
        menuItem.setPadding(false);

        menuItem.add(icon, new Span(text));
        return menuItem;
    }

    private HorizontalLayout getNavigation() {
        HorizontalLayout navigation = new HorizontalLayout();
        navigation.addClassNames(LumoUtility.JustifyContent.CENTER,
                LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM,
                LumoUtility.Width.FULL);
        navigation.add(
//                createLink("Dashboard")
        );
        return navigation;
    }

    private RouterLink createLink(String viewName) {
        RouterLink link = new RouterLink();
        link.add(viewName);
        // Demo has no routes
        // link.setRoute(viewClass.java);

        link.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
        link.getStyle().set("text-decoration", "none");

        return link;
    }

    private Dialog createLogoutDialog() {
        Dialog logoutDialog = new Dialog();
        logoutDialog.setCloseOnOutsideClick(true);
        logoutDialog.setCloseOnEsc(true);
        logoutDialog.setHeaderTitle("Konfirmasi Logout");

        Span message = new Span("Apakah Anda yakin ingin keluar dari akun ini?");
        logoutDialog.add(message);

        Button cancelButton = new Button("Batal", e -> logoutDialog.close());
        Button logoutButton = new Button("Keluar", e -> {
            logoutDialog.close();
            // TODO: Logout
            getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
        });

        logoutButton.getStyle().set("background-color", "#cd5c5c");
        logoutButton.getStyle().set("color", "white");
        logoutButton.getStyle().set("font-weight", "bold");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, logoutButton);
        logoutDialog.getFooter().add(buttons);

        return logoutDialog;
    }

}