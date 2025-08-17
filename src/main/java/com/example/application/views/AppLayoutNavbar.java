package com.example.application.views;

import com.example.application.models.CartItem;
import com.example.application.models.ProductsC;
import com.example.application.models.Promo;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import com.example.application.dao.PromoDAO;
import com.example.application.models.Users;

import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
public class AppLayoutNavbar extends AppLayout {

    // TODO: pref dan refactor

    private VerticalLayout cartContent;
    private Dialog logoutDialog;
    private Dialog cartDialog;

    private Promo activePromo = null;
    private double discountAmount = 0.0;

    public AppLayoutNavbar() {
        this.logoutDialog = createLogoutDialog();

        Image title = new Image("/images/logo_kopi-in.png", "Kopi.in Logo");
        title.setWidth("100px");
        title.getStyle().set("margin-left", "12px");

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

        cartContent = getCartContent(getCartFromSession());
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
        H2 header = new H2("Keranjang Anda");
        cartLayout.add(header);

        if (cartItems == null || cartItems.isEmpty()) {
            cartLayout.add(new Paragraph("Your cart is empty"));

            Button continueButton = new Button("Continue Shopping", e -> {
                cartDialog.close();
            });

            cartLayout.add(continueButton);
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
            infoLayout.add(new Span(formatRupiah(item.getPrice())));

            // Kontrol jumlah
            IntegerField quantityField = new IntegerField();
            quantityField.setValue(item.getQuantity());
            quantityField.setMin(1);
            quantityField.setMax(99);
            quantityField.setWidth("70px");
// Di dalam loop item cart, modifikasi quantity field:
            quantityField.addValueChangeListener(e -> {
                item.setQuantity(e.getValue());
                updateCart(); // Panggil updateCart() untuk refresh tampilan
            });

// Modifikasi tombol remove:
            Button removeButton = new Button(VaadinIcon.TRASH.create(), e -> {
                cartItems.remove(item);
                updateCart(); // Panggil updateCart() untuk refresh tampilan
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
        final double[] subtotal = {cartItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum()};
        double tax = subtotal[0] * 0.05; // Pajak 5%
        double total = subtotal[0] + tax;

// Apply discount if promo exists
        if (activePromo != null) {
            discountAmount = subtotal[0] * activePromo.getDiscount_value();
            total -= discountAmount;
        }

        VerticalLayout summaryLayout = new VerticalLayout();
        summaryLayout.setSpacing(false);
        summaryLayout.add(createSummaryRow("Subtotal", formatRupiah(subtotal[0])));
        if (activePromo != null) {
            summaryLayout.add(createSummaryRow(
                    "Diskon (" + (activePromo.getDiscount_value() * 100) + "%)",
                    "-" + formatRupiah(discountAmount)
            ));
        }
        summaryLayout.add(createSummaryRow("Pajak (5%)", formatRupiah(tax)));
        summaryLayout.add(createSummaryRow("Total", formatRupiah(total), true));

        cartLayout.add(summaryLayout);

        // Bagian kupon
        TextField couponField = new TextField("Coupon Code");
        Button applyButton = new Button("Apply");

        applyButton.addClickListener(e -> {
            String couponCode = couponField.getValue();

            if (couponCode == null || couponCode.trim().isEmpty()) {
                Notification.show("Masukkan kode promo terlebih dahulu", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Panggil DAO untuk cek promo
            PromoDAO promoDAO = new PromoDAO();
            Promo promo = promoDAO.getPromoByCode(couponCode);

            if (promo == null) {
                Notification.show("Kode promo tidak valid atau sudah kadaluarsa", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            this.activePromo = promo;
            this.discountAmount = subtotal[0] * promo.getDiscount_value();
            VaadinSession.getCurrent().setAttribute("activePromo", promo);

            // Hitung subtotal untuk cek minimal pembelian
            subtotal[0] = cartItems.stream()
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();

            if (subtotal[0] < promo.getMin_purchase()) {
                Notification.show("Minimal pembelian untuk promo ini adalah " +
                                        formatRupiah(promo.getMin_purchase()), 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Set promo aktif
            this.activePromo = promo;
            this.discountAmount = subtotal[0] * promo.getDiscount_value();

            Notification.show("Promo berhasil diterapkan! Diskon " +
                                    (promo.getDiscount_value() * 100) + "%", 3000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Update tampilan cart
            updateCart();
        });

        // Di bagian kupon, setelah applyButton
        if (activePromo != null) {
            Button removePromoButton = new Button("Hapus Promo", click -> {
                activePromo = null;
                discountAmount = 0.0;
                VaadinSession.getCurrent().setAttribute("activePromo", null);
                couponField.clear();
                updateCart();
                Notification.show("Promo dihapus", 2000,
                        Notification.Position.MIDDLE);
            });
            removePromoButton.addThemeVariants(ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            cartLayout.add(removePromoButton);
        }

        HorizontalLayout couponLayout = new HorizontalLayout(couponField, applyButton);
        couponLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        cartLayout.add(couponLayout);

        // Tombol aksi
        Button continueButton = new Button("Continue Shopping", e -> {
            cartDialog.close();
        });

        Button checkoutButton = new Button("Checkout", e -> {
            cartDialog.close();
            UI.getCurrent().navigate("checkout");
        });
        checkoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(continueButton, checkoutButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        cartLayout.add(buttonsLayout);

        return cartLayout;
    }

    public void updateCart() {
        UI.getCurrent().access(() -> {
            cartDialog.removeAll();
            VerticalLayout newCartContent = getCartContent(getCartFromSession());
            cartDialog.add(newCartContent);

            // Jika dialog belum terbuka, kita bisa membukanya otomatis
            if (!cartDialog.isOpened()) {
                cartDialog.open();
            }
        });
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

    public static String formatRupiah(double amount) {
        return String.format("Rp%,.0f", amount).replace(",", ".");
    }

    private Avatar getAvatar() {
        Avatar avatar = new Avatar();
        avatar.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER);
        avatar.getStyle().set("background-color", "var(--lumo-contrast-5pct)")
                .set("margin-right", "var(--lumo-space-l)")
                .set("cursor", "pointer");

        // Cek apakah user sudah login (contoh: dari session)
        boolean isLoggedIn = VaadinSession.getCurrent().getAttribute("user") != null;

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(avatar);
        contextMenu.setOpenOnClick(true);

        if (isLoggedIn) {
            // Jika sudah login
            Users user = (Users) VaadinSession.getCurrent().getAttribute("user");
            avatar.setName(user.getUsername());

//             User info section
            contextMenu.addItem(createUserInfo(user), e -> {
            }).getElement().setAttribute("disabled", "true");

            contextMenu.add(new Hr());

            // Menu Menu
            contextMenu.addItem(createMenuItem(VaadinIcon.MENU.create(), "Menu"), e -> {
                UI.getCurrent().navigate("customer");
            });


            // Menu Profile
            contextMenu.addItem(createMenuItem(VaadinIcon.USER.create(), "Profile"), e -> {
                UI.getCurrent().navigate("profile");
            });

            // Menu History Order
            contextMenu.addItem(createMenuItem(VaadinIcon.CLIPBOARD_TEXT.create(), "History Order"), e -> {
                UI.getCurrent().navigate("transaction-history");
            });

            contextMenu.add(new Hr());

            // Menu Logout
            contextMenu.addItem(createMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout"), e -> {
                logoutDialog.open();
            });
        } else {
            // Jika belum login
            avatar.setName("Guest");

            // Guest info section
            contextMenu.addItem(createGuestInfo(), e -> {
            }).getElement().setAttribute("disabled", "true");

            contextMenu.add(new Hr());

            // Menu Login
            contextMenu.addItem(createMenuItem(VaadinIcon.SIGN_IN.create(), "Login"), e -> {
                UI.getCurrent().navigate("/");
            });

            // Menu Register
            contextMenu.addItem(createMenuItem(VaadinIcon.USER.create(), "Register"), e -> {
                UI.getCurrent().navigate("register");
            });
        }

        return avatar;
    }

    private HorizontalLayout createUserInfo(Users user) {
        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setSpacing(false);
        userInfo.setPadding(false);
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar smallAvatar = new Avatar();
        smallAvatar.setName(user.getUsername());
        smallAvatar.setWidth("32px");
        smallAvatar.setHeight("32px");

        Span userName = new Span(user.getUsername());
        userName.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        Span userEmail = new Span(user.getEmail());
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

    private HorizontalLayout createGuestInfo() {
        HorizontalLayout guestInfo = new HorizontalLayout();
        guestInfo.setSpacing(false);
        guestInfo.setPadding(false);
        guestInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar smallAvatar = new Avatar();
        smallAvatar.setName("Guest");
        smallAvatar.setWidth("32px");
        smallAvatar.setHeight("32px");

        Span guestText = new Span("Guest User");
        guestText.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        Span loginPrompt = new Span("Login to access features");
        loginPrompt.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        guestInfo.add(smallAvatar);

        // Container untuk text
        var guestDetails = new HorizontalLayout();
        guestDetails.setSpacing(false);
        guestDetails.setPadding(false);
        guestDetails.getStyle().set("flex-direction", "column")
                .set("align-items", "flex-start")
                .set("margin-left", "var(--lumo-space-s)");

        guestDetails.add(guestText, loginPrompt);
        guestInfo.add(guestDetails);

        return guestInfo;
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
            VaadinSession.getCurrent().setAttribute("user", null);
            VaadinSession.getCurrent().setAttribute("cart", null);

            logoutDialog.close();
            UI.getCurrent().getPage().setLocation("/");

            Notification.show("Anda telah logout", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        logoutButton.getStyle().set("font-weight", "bold");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, logoutButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();
        logoutDialog.getFooter().add(buttons);

        return logoutDialog;
    }

    public static boolean isUserLoggedIn() {
        return VaadinSession.getCurrent().getAttribute("user") != null;
    }

    public static Users getCurrentUser() {
        return (Users) VaadinSession.getCurrent().getAttribute("user");
    }
}