package com.example.application.views;

import com.example.application.dao.PromoDAO;
import com.example.application.models.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    private final Dialog logoutDialog;
    private final Dialog cartDialog;
    private VerticalLayout cartContent;
    private Promo activePromo;
    private double discountAmount = 0.0;

    public AppLayoutNavbar() {
        this.logoutDialog = createLogoutDialog();
        this.cartDialog = createCartDialog();

        initializeNavbar();
    }

    private void initializeNavbar() {
        Image logo = createLogo();
        HorizontalLayout navigation = createNavigationLayout();
        Avatar avatar = createUserAvatar();
        Button cartButton = createCartButton();

        addToNavbar(logo, navigation, avatar, cartButton);
    }

    private Image createLogo() {
        Image logo = new Image("/images/logo_kopi-in.png", "Kopi.in Logo");
        logo.setWidth("100px");
        logo.getStyle().set("margin-left", "12px");
        return logo;
    }

    private HorizontalLayout createNavigationLayout() {
        HorizontalLayout navigation = new HorizontalLayout();
        navigation.addClassNames(
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.Gap.SMALL,
                LumoUtility.Height.MEDIUM,
                LumoUtility.Width.FULL
        );
        return navigation;
    }

    private Button createCartButton() {
        Button cartButton = new Button(VaadinIcon.CART.create());
        cartButton.addClassName("cart-button");
        cartButton.addClickListener(e -> cartDialog.open());
        return cartButton;
    }

    private Dialog createCartDialog() {
        Dialog dialog = new Dialog();
        dialog.setResizable(false);
        dialog.setModal(false);
        dialog.setCloseOnOutsideClick(true);

        dialog.getElement().getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("right", "0")
                .set("width", "400px")
                .set("height", "100%")
                .set("margin", "0");
        dialog.getElement().getThemeList().add("right-side-dialog");

        cartContent = createCartContent(getCartFromSession());
        dialog.add(cartContent);

        return dialog;
    }

    private List<CartItem> getCartFromSession() {
        List<CartItem> cart = (List<CartItem>) VaadinSession.getCurrent().getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            VaadinSession.getCurrent().setAttribute("cart", cart);
        }
        return cart;
    }

    private VerticalLayout createCartContent(List<CartItem> cartItems) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setWidth("100%");

        layout.add(createCartHeader());

        if (cartItems.isEmpty()) {
            layout.add(createEmptyCartContent());
            return layout;
        }

        layout.add(createCartItemsList(cartItems));
        layout.add(createOrderSummary(cartItems));
        layout.add(createCouponSection());
        layout.add(createCartActionButtons());

        return layout;
    }

    private Component createCartHeader() {
        return new H2("Keranjang Anda");
    }

    private Component createEmptyCartContent() {
        VerticalLayout emptyContent = new VerticalLayout();
        emptyContent.add(new Paragraph("Your cart is empty"));
        emptyContent.add(new Button("Continue Shopping", e -> cartDialog.close()));
        return emptyContent;
    }

    private Component createCartItemsList(List<CartItem> cartItems) {
        VerticalLayout itemsLayout = new VerticalLayout();

        for (CartItem item : cartItems) {
            itemsLayout.add(createCartItemRow(item));
            itemsLayout.add(new Hr());
        }

        return itemsLayout;
    }

    private Component createCartItemRow(CartItem item) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Image image = new Image();
            image.setSrc("images/products/" + item.getImageUrl());
            image.setWidth("60px");
            image.setHeight("60px");
            row.add(image);
        }

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.add(new Span(item.getProductName()));
        infoLayout.add(new Span(formatRupiah(item.getPrice())));

        IntegerField quantityField = createQuantityField(item);
        Button removeButton = createRemoveButton(item);

        HorizontalLayout actionsLayout = new HorizontalLayout(quantityField, removeButton);
        actionsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        row.add(infoLayout);
        row.add(actionsLayout);
        row.setFlexGrow(1, infoLayout);

        return row;
    }

    private IntegerField createQuantityField(CartItem item) {
        IntegerField quantityField = new IntegerField();
        quantityField.setValue(item.getQuantity());
        quantityField.setMin(1);
        quantityField.setMax(99);
        quantityField.setWidth("70px");
        quantityField.addValueChangeListener(e -> {
            item.setQuantity(e.getValue());
            updateCart();
        });
        return quantityField;
    }

    private Button createRemoveButton(CartItem item) {
        Button removeButton = new Button(VaadinIcon.TRASH.create(), e -> {
            getCartFromSession().remove(item);
            updateCart();
        });
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        return removeButton;
    }

    private Component createOrderSummary(List<CartItem> cartItems) {
        double subtotal = calculateSubtotal(cartItems);
        double tax = subtotal * 0.05;
        double total = calculateTotal(subtotal, tax);

        VerticalLayout summary = new VerticalLayout();
        summary.setSpacing(false);
        summary.add(createSummaryRow("Subtotal", formatRupiah(subtotal)));

        if (activePromo != null) {
            summary.add(createSummaryRow(
                    "Diskon (" + (activePromo.getDiscount_value() * 100) + "%)",
                    "-" + formatRupiah(discountAmount)
            ));
        }

        summary.add(createSummaryRow("Pajak (5%)", formatRupiah(tax)));
        summary.add(createSummaryRow("Total", formatRupiah(total), true));

        return summary;
    }

    private double calculateSubtotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    private double calculateTotal(double subtotal, double tax) {
        double total = subtotal + tax;
        if (activePromo != null) {
            discountAmount = subtotal * activePromo.getDiscount_value();
            total -= discountAmount;
        }
        return total;
    }

    private Component createCouponSection() {
        TextField couponField = new TextField("Coupon Code");
        Button applyButton = new Button("Apply");
        applyButton.addClickListener(e -> applyCoupon(couponField));

        HorizontalLayout couponLayout = new HorizontalLayout(couponField, applyButton);
        couponLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        if (activePromo != null) {
            Button removePromoButton = createRemovePromoButton(couponField);
            return new VerticalLayout(couponLayout, removePromoButton);
        }

        return couponLayout;
    }

    private void applyCoupon(TextField couponField) {
        String couponCode = couponField.getValue().trim();

        if (couponCode.isEmpty()) {
            showErrorNotification("Masukkan kode promo terlebih dahulu");
            return;
        }

        Promo promo = new PromoDAO().getPromoByCode(couponCode);

        if (promo == null) {
            showErrorNotification("Kode promo tidak valid atau sudah kadaluarsa");
            return;
        }

        double subtotal = calculateSubtotal(getCartFromSession());
        if (subtotal < promo.getMin_purchase()) {
            showErrorNotification("Minimal pembelian untuk promo ini adalah " +
                    formatRupiah(promo.getMin_purchase()));
            return;
        }

        this.activePromo = promo;
        this.discountAmount = subtotal * promo.getDiscount_value();
        VaadinSession.getCurrent().setAttribute("activePromo", promo);

        showSuccessNotification("Promo berhasil diterapkan! Diskon " +
                (promo.getDiscount_value() * 100) + "%");

        updateCart();
    }

    private Button createRemovePromoButton(TextField couponField) {
        Button button = new Button("Hapus Promo", click -> {
            activePromo = null;
            discountAmount = 0.0;
            VaadinSession.getCurrent().setAttribute("activePromo", null);
            couponField.clear();
            updateCart();
            Notification.show("Promo dihapus", 2000, Notification.Position.MIDDLE);
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private Component createCartActionButtons() {
        Button continueButton = new Button("Continue Shopping", e -> cartDialog.close());
        Button checkoutButton = new Button("Checkout", e -> {
            cartDialog.close();
            UI.getCurrent().navigate("customer/checkout");
        });
        checkoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttons = new HorizontalLayout(continueButton, checkoutButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return buttons;
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

    public void updateCart() {
        UI.getCurrent().access(() -> {
            cartDialog.removeAll();
            cartDialog.add(createCartContent(getCartFromSession()));

            if (!cartDialog.isOpened()) {
                cartDialog.open();
            }
        });
    }

    private Avatar createUserAvatar() {
        Avatar avatar = new Avatar();
        avatar.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER
        );
        avatar.getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("margin-right", "var(--lumo-space-l)")
                .set("cursor", "pointer");

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(avatar);
        contextMenu.setOpenOnClick(true);

        if (isUserLoggedIn()) {
            setupLoggedInUserMenu(avatar, contextMenu);
        } else {
            setupGuestUserMenu(avatar, contextMenu);
        }

        return avatar;
    }
    private void setupLoggedInUserMenu(Avatar avatar, ContextMenu contextMenu) {
        Users user = getCurrentUser();
        avatar.setName(user.getUsername());

        contextMenu.addItem(createUserInfo(user), e -> {}).getElement().setAttribute("disabled", "true");
        contextMenu.add(new Hr());

        contextMenu.addItem(createMenuItem(VaadinIcon.MENU.create(), "Menu"),
                e -> UI.getCurrent().navigate("customer"));

        contextMenu.addItem(createMenuItem(VaadinIcon.USER.create(), "Profile"),
                e -> UI.getCurrent().navigate("customer/profile"));

        contextMenu.addItem(createMenuItem(VaadinIcon.CLIPBOARD_TEXT.create(), "History Order"),
                e -> UI.getCurrent().navigate("customer/transaction-history"));

        contextMenu.add(new Hr());
        contextMenu.addItem(createMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout"),
                e -> logoutDialog.open());
    }

    private void setupGuestUserMenu(Avatar avatar, ContextMenu contextMenu) {
        avatar.setName("Guest");

        contextMenu.addItem(createGuestInfo(), e -> {}).getElement().setAttribute("disabled", "true");
        contextMenu.add(new Hr());

        contextMenu.addItem(createMenuItem(VaadinIcon.SIGN_IN.create(), "Login"),
                e -> UI.getCurrent().navigate("/"));

        contextMenu.addItem(createMenuItem(VaadinIcon.USER.create(), "Register"),
                e -> UI.getCurrent().navigate("register"));
    }

    private HorizontalLayout createUserInfo(Users user) {
        return createPersonInfo(user.getUsername(), user.getEmail());
    }

    private HorizontalLayout createGuestInfo() {
        return createPersonInfo("Guest", "Please log in");
    }

    private HorizontalLayout createPersonInfo(String name, String secondaryText) {
        HorizontalLayout personInfo = new HorizontalLayout();
        personInfo.setSpacing(false);
        personInfo.setPadding(false);
        personInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar smallAvatar = new Avatar(name);
        smallAvatar.setWidth("32px");
        smallAvatar.setHeight("32px");

        VerticalLayout details = new VerticalLayout();
        details.setSpacing(false);
        details.setPadding(false);
        details.add(new Span(name));
        details.add(new Span(secondaryText));
        details.setAlignItems(FlexComponent.Alignment.START);
        details.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        details.getStyle().set("margin-left", "var(--lumo-space-s)");
        details.getStyle().set("margin-top", "var(--lumo-space-xs)");
        details.getStyle().set("margin-bottom", "var(--lumo-space-xs)");
        details.getStyle().set("font-size", "var(--lumo-font-size-s)");
        details.getStyle().set("color", "var(--lumo-body-text-color)");


        personInfo.add(smallAvatar, details);
        return personInfo;
    }

    private HorizontalLayout createMenuItem(Component icon, String text) {
        HorizontalLayout menuItem = new HorizontalLayout(icon, new Span(text));
        menuItem.setSpacing(true);
        menuItem.setAlignItems(FlexComponent.Alignment.CENTER);
        menuItem.setPadding(false);
        return menuItem;
    }

    private Dialog createLogoutDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);
        dialog.setHeaderTitle("Konfirmasi Logout");
        dialog.add(new Span("Apakah Anda yakin ingin keluar dari akun ini?"));

        Button cancelButton = new Button("Batal", e -> dialog.close());
        Button logoutButton = createLogoutButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, logoutButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();
        dialog.getFooter().add(buttons);

        return dialog;
    }

    private Button createLogoutButton(Dialog dialog) {
        Button button = new Button("Keluar", e -> {
            VaadinSession.getCurrent().setAttribute("user", null);
            VaadinSession.getCurrent().setAttribute("cart", null);
            dialog.close();
            UI.getCurrent().getPage().setLocation("/");
            showSuccessNotification("Anda telah logout");
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        button.getStyle().set("font-weight", "bold");
        return button;
    }

    private void showErrorNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccessNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static String formatRupiah(double amount) {
        return String.format("Rp%,.0f", amount).replace(",", ".");
    }

    public static boolean isUserLoggedIn() {
        return VaadinSession.getCurrent().getAttribute("user") != null;
    }

    public static Users getCurrentUser() {
        return (Users) VaadinSession.getCurrent().getAttribute("user");
    }
}