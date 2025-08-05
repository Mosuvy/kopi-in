package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
public class AppLayoutNavbar extends AppLayout {

    // TODO: Gunakan logo kopiin
    // TODO: pref dan refactor

    private Dialog logoutDialog;

    public AppLayoutNavbar() {
        this.logoutDialog = createLogoutDialog();

        H1 title = new H1("KopiIn");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute");

        HorizontalLayout navigation = getNavigation();
        navigation.getElement();

        Avatar avatar = getAvatar();
        avatar.getElement();

        addToNavbar(title, navigation, avatar);
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