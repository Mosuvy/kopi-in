package com.example.application.views.admin;

import com.example.application.dao.UserDAO;
import com.example.application.models.Users;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("User Management - Kopi.in")
@Route(value = "admin/users", layout = MainLayout.class)
public class User extends VerticalLayout {

    private Grid<Users> grid;
    private List<Users> usersList;
    private List<Users> filteredUsersList;
    private TextField searchField;
    private ComboBox<String> roleFilter;
    private final UserDAO userDAO = new UserDAO();

    public User() {
        addClassName("users-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("height", "100vh")
                .set("overflow", "hidden");

        initializeData();
        filteredUsersList = new ArrayList<>(usersList);

        add(createHeader(), createStatsCards(), createUserGrid());
    }

    private void initializeData() {
        usersList = userDAO.getListUsers();
        filteredUsersList = new ArrayList<>(usersList);
    }

    private void filterUsers() {
        String searchTerm = searchField.getValue().toLowerCase();
        String selectedRole = roleFilter.getValue();

        filteredUsersList = usersList.stream()
                .filter(user -> {
                    boolean matchesSearch = user.getUsername().toLowerCase().contains(searchTerm) ||
                            user.getEmail().toLowerCase().contains(searchTerm);

                    boolean matchesRole = selectedRole.equals("Semua") ||
                            (user.getRole() != null && user.getRole().equalsIgnoreCase(selectedRole));

                    return matchesSearch && matchesRole;
                })
                .collect(Collectors.toList());

        grid.setItems(filteredUsersList);
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setHeight("80px");
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #795548 50%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "0 30px")
                .set("border-radius", "0 0 15px 15px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("flex-shrink", "0");

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

        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);
        titleSection.getStyle().set("position", "relative").set("z-index", "2");

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "10px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("margin-right", "12px");

        Icon usersIcon = new Icon(VaadinIcon.USERS);
        usersIcon.setSize("28px");
        usersIcon.getStyle()
                .set("color", "#4E342E")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.3))");
        iconContainer.add(usersIcon);

        H1 title = new H1("User Management");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "24px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        titleSection.add(iconContainer, title);

        Button addUserBtn = new Button("Tambah User", new Icon(VaadinIcon.PLUS));
        addUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserBtn.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("color", "#4E342E")
                .set("border", "none")
                .set("border-radius", "10px")
                .set("padding", "10px 18px")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        addUserBtn.addClickListener(e -> openUserDialog(null));

        header.add(titleSection, addUserBtn);
        return header;
    }

    private Component createStatsCards() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.getStyle()
                .set("margin-bottom", "25px")
                .set("margin-top", "25px")
                .set("padding", "0 15px");

        long totalUsers = usersList.size();
        long activeUsers = usersList.stream().filter(u -> u.getIs_active() == 1).count();
        long adminCount = usersList.stream().filter(u -> "Admin".equalsIgnoreCase(u.getRole())).count();
        long kasirCount = usersList.stream().filter(u -> "Kasir".equalsIgnoreCase(u.getRole())).count();

        statsLayout.add(
                createStatCard("Total Users", String.valueOf(totalUsers), VaadinIcon.USERS, "#8B4513", "👥"),
                createStatCard("Active Users", String.valueOf(activeUsers), VaadinIcon.CHECK_CIRCLE, "#228B22", "✅"),
                createStatCard("Admin", String.valueOf(adminCount), VaadinIcon.SPECIALIST, "#A0522D", "👑"),
                createStatCard("Kasir", String.valueOf(kasirCount), VaadinIcon.USER, "#CD853F", "💼")
        );

        return statsLayout;
    }

    private Component createStatCard(String title, String value, VaadinIcon icon, String color, String emoji) {
        VerticalLayout card = new VerticalLayout();
        card.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "15px")
                .set("padding", "25px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("transition", "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)")
                .set("cursor", "pointer")
                .set("flex", "1");

        Div bgDecor = new Div();
        bgDecor.getStyle()
                .set("position", "absolute")
                .set("top", "-20px")
                .set("right", "-20px")
                .set("width", "80px")
                .set("height", "80px")
                .set("background", "linear-gradient(135deg, " + color + "20, " + color + "10)")
                .set("border-radius", "50%")
                .set("opacity", "0.6");
        card.add(bgDecor);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("position", "relative").set("z-index", "2");

        Div titleSection = new Div();
        titleSection.getStyle().set("display", "flex").set("flex-direction", "column");

        Span titleSpan = new Span(title);
        titleSpan.getStyle()
                .set("font-size", "14px")
                .set("color", "#666")
                .set("font-weight", "500")
                .set("line-height", "1.2")
                .set("margin-bottom", "2px");

        Span emojiSpan = new Span(emoji);
        emojiSpan.getStyle().set("font-size", "12px").set("opacity", "0.7");

        titleSection.add(titleSpan, emojiSpan);

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, " + color + ", " + color + "dd)")
                .set("padding", "10px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 12px " + color + "30");

        Icon cardIcon = new Icon(icon);
        cardIcon.setSize("24px");
        cardIcon.getStyle().set("color", "white");
        iconContainer.add(cardIcon);

        header.add(titleSection, iconContainer);

        H2 valueH2 = new H2(value);
        valueH2.getStyle()
                .set("margin", "15px 0 0 0")
                .set("font-size", "28px")
                .set("font-weight", "700")
                .set("color", color)
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.1)")
                .set("position", "relative")
                .set("z-index", "2");

        card.add(header, valueH2);
        card.setPadding(false);
        card.setSpacing(false);

        return card;
    }

    private Component createUserGrid() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setSizeFull();
        gridContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "15px")
                .set("padding", "20px")
                .set("box-shadow", "0 6px 20px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("margin", "0 15px 15px 15px")
                .set("flex", "1")
                .set("overflow", "hidden");

        HorizontalLayout gridHeader = new HorizontalLayout();
        gridHeader.setWidthFull();
        gridHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        gridHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        gridHeader.setHeight("50px");
        gridHeader.getStyle()
                .set("margin-bottom", "15px")
                .set("flex-shrink", "0");

        H3 gridTitle = new H3("Daftar Users");
        gridTitle.getStyle()
                .set("margin", "0")
                .set("color", "#4E342E")
                .set("font-size", "18px")
                .set("font-weight", "600");

        HorizontalLayout searchSection = new HorizontalLayout();
        searchSection.setAlignItems(FlexComponent.Alignment.CENTER);

        searchField = new TextField();
        searchField.setPlaceholder("Cari user...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("200px");
        searchField.getStyle()
                .set("border-radius", "10px")
                .set("margin-right", "10px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterUsers());

        roleFilter = new ComboBox<>();
        roleFilter.setPlaceholder("Filter Role");
        roleFilter.setItems("Semua", "Admin", "Kasir", "Customer");
        roleFilter.setValue("Semua");
        roleFilter.setWidth("150px");
        roleFilter.getStyle().set("border-radius", "10px");
        roleFilter.addValueChangeListener(e -> filterUsers());

        searchSection.add(searchField, roleFilter);
        gridHeader.add(gridTitle, searchSection);

        grid = new Grid<>(Users.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setSizeFull();
        grid.getStyle()
                .set("border-radius", "10px")
                .set("overflow", "auto")
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("flex", "1");

        configureGrid();
        grid.setItems(filteredUsersList);

        gridContainer.add(gridHeader, grid);
        gridContainer.setPadding(false);

        return gridContainer;
    }

    private void configureGrid() {
        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            Icon userIcon = new Icon(VaadinIcon.USER);
            userIcon.setSize("16px");
            userIcon.getStyle().set("color", "#8B4513").set("margin-right", "8px");

            Span username = new Span(user.getUsername());
            username.getStyle().set("font-weight", "500");

            layout.add(userIcon, username);
            return layout;
        })).setHeader("Username").setWidth("200px");

        grid.addColumn(Users::getEmail)
                .setHeader("Email")
                .setWidth("280px");

        grid.addColumn(new ComponentRenderer<>(user -> {
            String role = user.getRole() != null ? user.getRole() : "Unknown";
            Span roleBadge = new Span(role);
            String roleColor = getRoleColor(role);
            roleBadge.getStyle()
                    .set("background", "linear-gradient(135deg, " + roleColor + ", " + roleColor + "dd)")
                    .set("color", "white")
                    .set("padding", "6px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)")
                    .set("display", "inline-block")
                    .set("min-width", "60px")
                    .set("text-align", "center");
            return roleBadge;
        })).setHeader("Role").setWidth("120px");

        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout statusLayout = new HorizontalLayout();
            statusLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            Icon statusIcon = new Icon(user.getIs_active() == 1 ? VaadinIcon.CHECK_CIRCLE : VaadinIcon.CLOSE_CIRCLE);
            statusIcon.setSize("16px");
            statusIcon.getStyle().set("color", user.getIs_active() == 1 ? "#228B22" : "#DC143C");

            Span statusText = new Span(user.getIs_active() == 1 ? "Active" : "Inactive");
            statusText.getStyle()
                    .set("color", user.getIs_active() == 1 ? "#228B22" : "#DC143C")
                    .set("font-weight", "500")
                    .set("margin-left", "5px");

            statusLayout.add(statusIcon, statusText);
            return statusLayout;
        })).setHeader("Status").setWidth("120px");

        grid.addColumn(new ComponentRenderer<>(user -> {
            if (user.getCreated_at() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String formattedDate = user.getCreated_at().toLocalDateTime().format(formatter);
                Span dateSpan = new Span(formattedDate);
                dateSpan.getStyle().set("color", "#666").set("font-size", "13px");
                return dateSpan;
            }
            return new Span("-");
        })).setHeader("Dibuat").setWidth("150px");

        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editBtn.getStyle()
                    .set("color", "#8B4513")
                    .set("border-radius", "6px")
                    .set("margin-right", "5px");
            editBtn.addClickListener(e -> openUserDialog(user));

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            deleteBtn.getStyle()
                    .set("color", "#DC143C")
                    .set("border-radius", "6px");
            deleteBtn.addClickListener(e -> confirmDelete(user));

            actions.add(editBtn, deleteBtn);
            return actions;
        })).setHeader("Aksi").setWidth("100px").setFlexGrow(0);
    }

    private String getRoleColor(String role) {
        if (role == null) {
            return "#666";
        }

        switch (role.toLowerCase()) {
            case "admin": return "#8B4513";
            case "kasir": return "#CD853F";
            case "customer": return "#D2691E";
            default: return "#666";
        }
    }

    private void openUserDialog(Users user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("480px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(false);
        dialogContent.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "20px 25px")
                .set("margin", "0")
                .set("min-height", "60px");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        titleLayout.setSpacing(true);

        Icon dialogIcon = new Icon(user == null ? VaadinIcon.PLUS : VaadinIcon.EDIT);
        dialogIcon.setSize("20px");
        dialogIcon.getStyle().set("color", "#FFD700");

        H3 dialogTitle = new H3(user == null ? "Tambah User Baru" : "Edit User");
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("white-space", "nowrap");

        titleLayout.add(dialogIcon, dialogTitle);

        Button closeBtn = new Button(new Icon(VaadinIcon.CLOSE));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeBtn.getStyle()
                .set("color", "white")
                .set("background", "transparent")
                .set("border", "none")
                .set("padding", "8px");
        closeBtn.addClickListener(e -> dialog.close());

        header.add(titleLayout, closeBtn);

        VerticalLayout formContainer = new VerticalLayout();
        formContainer.getStyle()
                .set("padding", "25px")
                .set("background", "white");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField usernameField = new TextField("Username");
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPlaceholder("Masukkan password");
        passwordField.setHelperText("Password harus minimal 8 karakter, mengandung huruf besar, huruf kecil, angka, dan karakter khusus");
        passwordField.setPattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");
        passwordField.setErrorMessage("Password tidak memenuhi kriteria");

        ComboBox<String> roleField = new ComboBox<>("Role");
        ComboBox<String> statusField = new ComboBox<>("Status");

        usernameField.setPlaceholder("Masukkan username");
        emailField.setPlaceholder("Masukkan email");

        roleField.setItems("Admin", "Kasir", "Customer");
        roleField.setPlaceholder("Pilih role");

        statusField.setItems("Active", "Inactive");
        statusField.setPlaceholder("Pilih status");

        if (user != null) {
            usernameField.setValue(user.getUsername());
            emailField.setValue(user.getEmail());
            roleField.setValue(user.getRole());
            statusField.setValue(user.getIs_active() == 1 ? "Active" : "Inactive");
        } else {
            statusField.setValue("Active");
        }

        formLayout.add(usernameField, emailField, passwordField, roleField, statusField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "20px");

        Button cancelBtn = new Button("Batal");
        cancelBtn.getStyle()
                .set("margin-right", "10px")
                .set("border-radius", "8px");
        cancelBtn.addClickListener(e -> dialog.close());

        Button saveBtn = new Button(user == null ? "Tambah" : "Simpan");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #CD853F)")
                .set("border", "none")
                .set("border-radius", "8px");

        saveBtn.addClickListener(e -> {
            try {
                if (user == null) {
                    if (passwordField.getValue().isEmpty() || !userDAO.validatePassword(passwordField.getValue())) {
                        Notification.show("Password harus minimal 8 karakter dan mengandung huruf besar, huruf kecil, angka, dan karakter khusus",
                                        5000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }

                    Users newUser = new Users();
                    newUser.setUsername(usernameField.getValue());
                    newUser.setEmail(emailField.getValue());
                    newUser.setPassword(passwordField.getValue());
                    newUser.setRole(roleField.getValue());
                    newUser.setIs_active(statusField.getValue().equals("Active") ? 1 : 0);
                    newUser.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));

                    if (userDAO.createUsers(newUser)) {
                        usersList = userDAO.getListUsers();
                        filterUsers();
                        Notification.show("User berhasil ditambahkan", 3000,
                                        Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                } else {
                    user.setUsername(usernameField.getValue());
                    user.setEmail(emailField.getValue());

                    if (!passwordField.getValue().isEmpty()) {
                        if (!userDAO.validatePassword(passwordField.getValue())) {
                            Notification.show("Password harus minimal 8 karakter dan mengandung huruf besar, huruf kecil, angka, dan karakter khusus",
                                            5000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            return;
                        }
                        user.setPassword(passwordField.getValue());
                    }

                    user.setRole(roleField.getValue());
                    user.setIs_active(statusField.getValue().equals("Active") ? 1 : 0);

                    if (userDAO.updateUsers(user)) {
                        usersList = userDAO.getListUsers();
                        filterUsers();
                        Notification.show("User berhasil diperbarui", 3000,
                                        Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                }
            } catch (Exception ex) {
                Notification.show("Gagal menyimpan user: " + ex.getMessage(), 5000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            dialog.close();
        });

        buttonLayout.add(cancelBtn, saveBtn);
        formContainer.add(formLayout, buttonLayout);

        dialogContent.add(header, formContainer);
        dialog.add(dialogContent);
        dialog.open();
    }

    private void confirmDelete(Users user) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setWidth("400px");

        confirmDialog.setHeader("Konfirmasi Hapus User");
        confirmDialog.setText("Apakah Anda yakin ingin menghapus user '" + user.getUsername() + "'? Tindakan ini tidak dapat dibatalkan.");

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Batal");

        confirmDialog.setConfirmText("Hapus");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            try {
                if (userDAO.deleteUsers(user.getId())) {
                    usersList = userDAO.getListUsers();
                    filterUsers();
                    Notification.show("User berhasil dihapus", 3000,
                                    Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
            } catch (Exception ex) {
                Notification.show("Gagal menghapus user: " + ex.getMessage(), 5000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        confirmDialog.open();
    }
}