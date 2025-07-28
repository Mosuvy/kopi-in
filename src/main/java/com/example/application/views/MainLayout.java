package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private static final String COFFEE_PRIMARY = "#6F4E37"; // Coffee brown
    private static final String COFFEE_LIGHT = "#A67B5B";  // Lighter coffee
    private static final String COFFEE_DARK = "#483024";   // Dark coffee
    private static final String CREAM = "#FFF8DC";         // Creamy background

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        getStyle().set("background", CREAM);
        getStyle().set("border-radius", "8px");
        getStyle().set("box-shadow", "0 2px 12px rgba(111, 78, 55, 0.1)");
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.getStyle().set("border-radius", "8px");
        toggle.getStyle().set("margin-right", "8px");
        toggle.getStyle().set("color", COFFEE_PRIMARY);

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.getStyle().set("font-weight", "700");
        viewTitle.getStyle().set("color", COFFEE_DARK);
        viewTitle.getStyle().set("margin", "0");
        viewTitle.getStyle().set("font-size", "1.5rem");

        Header header = new Header(toggle, viewTitle);
        header.getStyle().set("background", "#fff");
        header.getStyle().set("border-radius", "8px 8px 0 0");
        header.getStyle().set("box-shadow", "0 1px 6px rgba(111, 78, 55, 0.08)");
        header.getStyle().set("padding", "0.75rem 1.5rem");
        header.getStyle().set("margin-bottom", "0.5rem");

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        Span appName = new Span("Kopi.in");
        appName.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
        appName.getStyle().set("color", COFFEE_PRIMARY);
        appName.getStyle().set("margin", "0.5rem 0 1rem 0");
        Header header = new Header(appName);
        header.getStyle().set("background", "#fff");
        header.getStyle().set("border-radius", "8px 8px 0 0");
        header.getStyle().set("box-shadow", "0 1px 6px rgba(111, 78, 55, 0.08)");
        header.getStyle().set("padding", "1.25rem 1.5rem 0.5rem 1.5rem");

        Scroller scroller = new Scroller(createNavigation());
        scroller.getStyle().set("background", "#fff");
        scroller.getStyle().set("border-radius", "0 0 8px 8px");
        scroller.getStyle().set("padding", "0.5rem 0.5rem 1rem 0.5rem");

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.getStyle().set("background", "#fff");
        nav.getStyle().set("border-radius", "8px");
        nav.getStyle().set("box-shadow", "0 1px 6px rgba(111, 78, 55, 0.05)");
        nav.getStyle().set("padding", "0.5rem 0.5rem 1rem 0.5rem");

        // Authentication Routes
        nav.addItem(new SideNavItem("Login", ""));
        nav.addItem(new SideNavItem("Register", "register"));

        // Customer Routes
        nav.addItem(createSectionTitle("Customer"));
        nav.addItem(new SideNavItem("Home", "customer/home"));
        nav.addItem(new SideNavItem("About", "about"));

        // Cashier Routes
        nav.addItem(createSectionTitle("Cashier"));
        nav.addItem(new SideNavItem("Dashboard", "kasir/dashboard"));
        nav.addItem(new SideNavItem("Orders", "kasir/order"));
        nav.addItem(new SideNavItem("Products", "kasir/product"));
        nav.addItem(new SideNavItem("Transactions", "kasir/transaction"));

        // Admin Routes
        nav.addItem(createSectionTitle("Admin"));
        nav.addItem(new SideNavItem("Dashboard", "admin/dashboard"));
        nav.addItem(new SideNavItem("Products", "admin/product"));
        nav.addItem(new SideNavItem("Users", "admin/user"));
        nav.addItem(new SideNavItem("Discounts", "admin/discount"));

        // Style all nav items
        nav.getItems().forEach(item -> {
            item.getStyle().set("border-radius", "8px");
            item.getStyle().set("padding", "0.5rem 1rem");
            item.getStyle().set("margin-bottom", "0.25rem");
            item.getStyle().set("font-weight", "500");
            item.getStyle().set("color", COFFEE_DARK);
            item.getElement().executeJs(
                "this.addEventListener('mouseenter', () => {" +
                "  this.style.background = '" + CREAM + "';" +
                "});" +
                "this.addEventListener('mouseleave', () => {" +
                "  this.style.background = 'transparent';" +
                "});"
            );
        });

        return nav;
    }

    private SideNavItem createSectionTitle(String title) {
        SideNavItem section = new SideNavItem(title);
        section.getStyle().set("font-weight", "600");
        section.getStyle().set("color", COFFEE_LIGHT);
        section.getStyle().set("padding", "1rem 1rem 0.5rem 1rem");
        section.getStyle().set("pointer-events", "none");
        return section;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.getStyle().set("background", "#fff");
        layout.getStyle().set("border-radius", "0 0 8px 8px");
        layout.getStyle().set("box-shadow", "0 -1px 6px rgba(111, 78, 55, 0.05)");
        layout.getStyle().set("padding", "0.75rem 1.5rem");
        layout.getStyle().set("margin-top", "0.5rem");
        
        Span copyright = new Span("© 2024 Kopi.in");
        copyright.getStyle().set("color", COFFEE_LIGHT);
        copyright.getStyle().set("font-size", "0.875rem");
        layout.add(copyright);
        
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
