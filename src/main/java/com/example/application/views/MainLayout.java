package com.example.application.views;

import com.example.application.models.Users;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Objects;

@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private static final String COFFEE_PRIMARY = "#6F4E37";    // Coffee brown
    private static final String COFFEE_LIGHT = "#A67B5B";      // Lighter coffee
    private static final String COFFEE_DARK = "#483024";       // Dark coffee
    private static final String CREAM = "#FFF8DC";             // Creamy background
    private static final String COFFEE_ACCENT = "#D4B08C";     // Accent color
    private static final String SUCCESS_COLOR = "#2E7D32";     // Success green
    private static final String WARNING_COLOR = "#ED6C02";     // Warning orange

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        getStyle()
                .set("background", CREAM)
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 12px rgba(111, 78, 55, 0.1)");
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.getStyle()
                .set("border-radius", "8px")
                .set("margin-right", "8px")
                .set("color", COFFEE_PRIMARY)
                .set("background", "transparent")
                .set("transition", "all 0.2s ease");

        toggle.getElement().executeJs(
            "this.addEventListener('mouseenter', () => {" +
            "  this.style.background = '" + CREAM + "';" +
            "});" +
            "this.addEventListener('mouseleave', () => {" +
            "  this.style.background = 'transparent';" +
            "});"
        );

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.getStyle()
                .set("font-weight", "700")
                .set("color", COFFEE_DARK)
                .set("margin", "0")
                .set("font-size", "1.5rem");

        Header header = new Header(toggle, viewTitle);
        header.getStyle()
                .set("background", "#fff")
                .set("border-radius", "8px 8px 0 0")
                .set("box-shadow", "0 1px 6px rgba(111, 78, 55, 0.08)")
                .set("padding", "0.75rem 1.5rem")
                .set("margin-bottom", "0.5rem");

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        // Logo and Brand Section
        HorizontalLayout brandSection = new HorizontalLayout();
        brandSection.setAlignItems(FlexComponent.Alignment.CENTER);
        brandSection.setSpacing(true);

        // Logo container with coffee icon
        Div logoContainer = new Div();
        logoContainer.getStyle()
                .set("background", "linear-gradient(135deg, " + COFFEE_PRIMARY + ", " + COFFEE_DARK + ")")
                .set("padding", "12px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 15px rgba(111, 78, 55, 0.2)");

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("24px");
        coffeeIcon.getStyle()
                .set("color", "#fff")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.2))");
        logoContainer.add(coffeeIcon);

        // Brand name with enhanced styling
        H2 brandName = new H2("Kopi.in");
        brandName.getStyle()
                .set("margin", "0 0 0 8px")
                .set("font-size", "24px")
                .set("font-weight", "700")
                .set("color", COFFEE_PRIMARY)
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.1)");

        brandSection.add(logoContainer, brandName);

        Header header = new Header(brandSection);
        header.getStyle()
                .set("background", "#fff")
                .set("border-radius", "8px 8px 0 0")
                .set("box-shadow", "0 1px 6px rgba(111, 78, 55, 0.08)")
                .set("padding", "1.25rem");

        Scroller scroller = new Scroller(createNavigation());
        scroller.getStyle()
                .set("background", "#fff")
                .set("border-radius", "0 0 8px 8px")
                .set("padding", "0.5rem");

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.getStyle()
                .set("background", "#fff")
                .set("border-radius", "8px")
                .set("padding", "0.5rem");

//        nav.addItem(createSectionTitle("Authentication", VaadinIcon.USER));
//        nav.addItem(createNavItem("Login", "", VaadinIcon.SIGN_IN));
//        nav.addItem(createNavItem("Register", "register", VaadinIcon.PLUS_CIRCLE));

        String role = getCurrentUser() != null ? getCurrentUser().getRole() : "";

        switch (role.trim().toLowerCase()) {
            case "admin" -> {
                nav.addItem(createSectionTitle("Admin", VaadinIcon.COGS));
                nav.addItem(createNavItem("Dashboard", "admin/dashboard", VaadinIcon.DASHBOARD));
                nav.addItem(createNavItem("Products", "admin/products", VaadinIcon.PACKAGE));
                nav.addItem(createNavItem("Users", "admin/users", VaadinIcon.USERS));
                nav.addItem(createNavItem("Discounts", "admin/discounts", VaadinIcon.TICKET));
            }
            case "kasir" -> {
                nav.addItem(createSectionTitle("Cashier", VaadinIcon.CASH));
                nav.addItem(createNavItem("Dashboard", "kasir", VaadinIcon.DASHBOARD));
                nav.addItem(createNavItem("Orders", "kasir/order", VaadinIcon.CART, "3", WARNING_COLOR));
                nav.addItem(createNavItem("Products", "kasir/products", VaadinIcon.PACKAGE));
                nav.addItem(createNavItem("Transactions", "kasir/transactions", VaadinIcon.MONEY, "5", SUCCESS_COLOR));
            }
        }

        return nav;
    }

    private SideNavItem createSectionTitle(String title, VaadinIcon icon) {
        Icon sectionIcon = new Icon(icon);
        sectionIcon.getStyle()
                .set("color", COFFEE_LIGHT)
                .set("width", "16px")
                .set("height", "16px")
                .set("margin-right", "8px");

        SideNavItem section = new SideNavItem(title);
        section.getStyle()
                .set("padding", "1rem 1rem 0.5rem 1rem")
                .set("pointer-events", "none")
                .set("margin-top", "0.5rem")
                .set("font-weight", "600")
                .set("color", COFFEE_LIGHT)
                .set("font-size", "14px");

        section.setPrefixComponent(sectionIcon);
        return section;
    }

    private SideNavItem createNavItem(String text, String path, VaadinIcon icon) {
        return createNavItem(text, path, icon, null, null);
    }

    private SideNavItem createNavItem(String text, String path, VaadinIcon icon, String badge, String badgeColor) {
        // Create the nav item with text and path
        SideNavItem navItem = new SideNavItem(text, path);
        
        // Create and style the icon
        Icon navIcon = new Icon(icon);
        navIcon.getStyle()
                .set("color", COFFEE_DARK)
                .set("width", "18px")
                .set("height", "18px");
        navItem.setPrefixComponent(navIcon);

        // Add badge if provided
        if (badge != null) {
            Span badgeSpan = new Span(badge);
            badgeSpan.getStyle()
                    .set("background", badgeColor)
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "12px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("margin-left", "8px");
            navItem.setSuffixComponent(badgeSpan);
        }

        // Style the nav item
        navItem.getStyle()
                .set("border-radius", "8px")
                .set("padding", "0.5rem 1rem")
                .set("margin-bottom", "0.25rem")
                .set("transition", "all 0.2s ease")
                .set("color", COFFEE_DARK)
                .set("font-weight", "500");

        navItem.getElement().executeJs(
            "this.addEventListener('mouseenter', () => {" +
            "  this.style.background = '" + CREAM + "';" +
            "});" +
            "this.addEventListener('mouseleave', () => {" +
            "  this.style.background = 'transparent';" +
            "});"
        );

        return navItem;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.getStyle()
                .set("background", "#fff")
                .set("border-radius", "0 0 8px 8px")
                .set("box-shadow", "0 -1px 6px rgba(111, 78, 55, 0.05)")
                .set("padding", "1rem 1.5rem")
                .set("margin-top", "0.5rem");

        // Version info with icon
        HorizontalLayout versionInfo = new HorizontalLayout();
        versionInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        versionInfo.setSpacing(true);

        Button logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT), e -> {
            Dialog dialog = createLogoutDialog();
            dialog.open();
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        logoutButton.getStyle()
                .set("width", "100%")
                .set("justify-content", "flex-start")
                .set("margin-bottom", "24px")
                .set("font-weight", "600")
                .set("color", "#b71c1c");

        Icon infoIcon = new Icon(VaadinIcon.INFO_CIRCLE);
        infoIcon.getStyle()
                .set("color", COFFEE_LIGHT)
                .set("width", "14px")
                .set("height", "14px");

        Span version = new Span("Version 1.0.0");
        version.getStyle()
                .set("color", COFFEE_LIGHT)
                .set("font-size", "12px");

        versionInfo.add(infoIcon, version);

        // Copyright text
        Span copyright = new Span("© 2024 Kopi.in");
        copyright.getStyle()
                .set("color", COFFEE_LIGHT)
                .set("font-size", "12px")
                .set("margin-top", "4px")
                .set("display", "block");

        VerticalLayout footerContent = new VerticalLayout(logoutButton, versionInfo, copyright);
        footerContent.setSpacing(false);
        footerContent.setPadding(false);
        layout.add(footerContent);

        return layout;
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

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }

    public static Users getCurrentUser() {
        return (Users) VaadinSession.getCurrent().getAttribute("user");
    }
}
