package com.example.application.views.admin;

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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("User Management - Kopi.in")
@Route(value = "admin/users", layout = MainLayout.class)
public class User extends VerticalLayout {

    private Grid<com.example.application.models.Users> grid;
    private List<com.example.application.models.Users> usersList;

    public User() {
        addClassName("users-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Background gradient untuk seluruh halaman
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        // Initialize data
        initializeData();

        add(createHeader(), createStatsCards(), createUserGrid());
    }

    private void initializeData() {
        usersList = new ArrayList<>();

        // Sample data
        com.example.application.models.Users admin1 = new com.example.application.models.Users();
        admin1.setId("USR001");
        admin1.setUsername("admin.kopi");
        admin1.setEmail("admin@kopi.in");
        admin1.setRole("Admin");
        admin1.setIs_active(1);
        admin1.setCreated_at(Timestamp.valueOf("2024-01-15 10:30:00"));

        com.example.application.models.Users kasir1 = new com.example.application.models.Users();
        kasir1.setId("USR002");
        kasir1.setUsername("kasir.satu");
        kasir1.setEmail("kasir1@kopi.in");
        kasir1.setRole("Kasir");
        kasir1.setIs_active(1);
        kasir1.setCreated_at(Timestamp.valueOf("2024-01-20 14:15:00"));

        com.example.application.models.Users kasir2 = new com.example.application.models.Users();
        kasir2.setId("USR003");
        kasir2.setUsername("kasir.dua");
        kasir2.setEmail("kasir2@kopi.in");
        kasir2.setRole("Kasir");
        kasir2.setIs_active(0);
        kasir2.setCreated_at(Timestamp.valueOf("2024-02-01 09:20:00"));

        com.example.application.models.Users customer1 = new com.example.application.models.Users();
        customer1.setId("USR004");
        customer1.setUsername("budi.santoso");
        customer1.setEmail("budi@gmail.com");
        customer1.setRole("Customer");
        customer1.setIs_active(1);
        customer1.setCreated_at(Timestamp.valueOf("2024-02-15 16:45:00"));

        com.example.application.models.Users customer2 = new com.example.application.models.Users();
        customer2.setId("USR005");
        customer2.setUsername("sari.dewi");
        customer2.setEmail("sari.dewi@yahoo.com");
        customer2.setRole("Customer");
        customer2.setIs_active(1);
        customer2.setCreated_at(Timestamp.valueOf("2024-03-01 11:30:00"));

        usersList.add(admin1);
        usersList.add(kasir1);
        usersList.add(kasir2);
        usersList.add(customer1);
        usersList.add(customer2);
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
                .set("border-radius", "0 0 20px 20px")
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

        // Title section
        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);
        titleSection.getStyle().set("position", "relative").set("z-index", "2");

        // Icon container
        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "12px")
                .set("border-radius", "15px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("margin-right", "15px");

        Icon usersIcon = new Icon(VaadinIcon.USERS);
        usersIcon.setSize("36px");
        usersIcon.getStyle()
                .set("color", "#4E342E")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.3))");
        iconContainer.add(usersIcon);

        H1 title = new H1("User Management");
        title.getStyle()
                .set("margin", "0 0 0 10px")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        titleSection.add(iconContainer, title);

        // Add user button
        Button addUserBtn = new Button("Tambah User", new Icon(VaadinIcon.PLUS));
        addUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserBtn.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("color", "#4E342E")
                .set("border", "none")
                .set("border-radius", "12px")
                .set("padding", "12px 20px")
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
                .set("padding", "0 15px");

        long totalUsers = usersList.size();
        long activeUsers = usersList.stream().filter(u -> u.getIs_active() == 1).count();
        long adminCount = usersList.stream().filter(u -> "Admin".equals(u.getRole())).count();
        long kasirCount = usersList.stream().filter(u -> "Kasir".equals(u.getRole())).count();

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

        // Decorative background element
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

        // Header with icon and emoji
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

        // Value with enhanced styling
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
        gridContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("margin", "0 15px");

        // Grid header
        HorizontalLayout gridHeader = new HorizontalLayout();
        gridHeader.setWidthFull();
        gridHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        gridHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        gridHeader.getStyle().set("margin-bottom", "20px");

        H3 gridTitle = new H3("Daftar Users");
        gridTitle.getStyle()
                .set("margin", "0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        // Search and filter section
        HorizontalLayout searchSection = new HorizontalLayout();
        searchSection.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari user...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.getStyle()
                .set("border-radius", "12px")
                .set("margin-right", "10px");

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.setPlaceholder("Filter Role");
        roleFilter.setItems("Semua", "Admin", "Kasir", "Customer");
        roleFilter.setValue("Semua");
        roleFilter.getStyle().set("border-radius", "12px");

        searchSection.add(searchField, roleFilter);
        gridHeader.add(gridTitle, searchSection);

        // Initialize grid
        grid = new Grid<>(com.example.application.models.Users.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.getStyle()
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.1)");

        // Configure columns
        configureGrid();

        grid.setItems(usersList);

        gridContainer.add(gridHeader, grid);
        gridContainer.setPadding(false);

        return gridContainer;
    }

    private void configureGrid() {
        // User ID column with enhanced styling
        grid.addColumn(com.example.application.models.Users::getId)
                .setHeader("User ID")
                .setWidth("100px")
                .setFlexGrow(0);

        // Username column with icon
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

        // Email column
        grid.addColumn(com.example.application.models.Users::getEmail)
                .setHeader("Email")
                .setWidth("250px");

        // Role column with badges
        grid.addColumn(new ComponentRenderer<>(user -> {
            Span roleBadge = new Span(user.getRole());
            String roleColor = getRoleColor(user.getRole());
            roleBadge.getStyle()
                    .set("background", "linear-gradient(135deg, " + roleColor + ", " + roleColor + "dd)")
                    .set("color", "white")
                    .set("padding", "6px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)");
            return roleBadge;
        })).setHeader("Role").setWidth("120px");

        // Status column with indicators
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

        // Created date column
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

        // Actions column
        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editBtn.getStyle()
                    .set("color", "#8B4513")
                    .set("border-radius", "8px")
                    .set("margin-right", "5px");
            editBtn.addClickListener(e -> openUserDialog(user));

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            deleteBtn.getStyle()
                    .set("color", "#DC143C")
                    .set("border-radius", "8px");
            deleteBtn.addClickListener(e -> confirmDelete(user));

            actions.add(editBtn, deleteBtn);
            return actions;
        })).setHeader("Aksi").setWidth("120px").setFlexGrow(0);
    }

    private String getRoleColor(String role) {
        switch (role) {
            case "Admin": return "#8B4513";
            case "Kasir": return "#CD853F";
            case "Customer": return "#D2691E";
            default: return "#666";
        }
    }

    private void openUserDialog(com.example.application.models.Users user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        // Dialog header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "20px")
                .set("margin", "-20px -20px 20px -20px")
                .set("border-radius", "12px 12px 0 0");

        Icon dialogIcon = new Icon(user == null ? VaadinIcon.PLUS : VaadinIcon.EDIT);
        dialogIcon.setSize("20px");

        H3 dialogTitle = new H3(user == null ? "Tambah User Baru" : "Edit User");
        dialogTitle.getStyle().set("margin", "0").set("margin-left", "10px");

        HorizontalLayout titleLayout = new HorizontalLayout(dialogIcon, dialogTitle);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Button closeBtn = new Button(new Icon(VaadinIcon.CLOSE));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeBtn.getStyle().set("color", "white");
        closeBtn.addClickListener(e -> dialog.close());

        header.add(titleLayout, closeBtn);

        // Form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField idField = new TextField("User ID");
        TextField usernameField = new TextField("Username");
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");
        ComboBox<String> roleField = new ComboBox<>("Role");
        ComboBox<String> statusField = new ComboBox<>("Status");

        // Configure fields
        idField.setPlaceholder("Otomatis dibuat");
        idField.setReadOnly(true);

        usernameField.setPlaceholder("Masukkan username");
        emailField.setPlaceholder("Masukkan email");
        passwordField.setPlaceholder("Masukkan password");

        roleField.setItems("Admin", "Kasir", "Customer");
        roleField.setPlaceholder("Pilih role");

        statusField.setItems("Active", "Inactive");
        statusField.setPlaceholder("Pilih status");

        // Fill form if editing
        if (user != null) {
            idField.setValue(user.getId());
            usernameField.setValue(user.getUsername());
            emailField.setValue(user.getEmail());
            roleField.setValue(user.getRole());
            statusField.setValue(user.getIs_active() == 1 ? "Active" : "Inactive");
        } else {
            statusField.setValue("Active");
        }

        formLayout.add(idField, usernameField, emailField, passwordField, roleField, statusField);

        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "20px");

        Button cancelBtn = new Button("Batal");
        cancelBtn.addClickListener(e -> dialog.close());

        Button saveBtn = new Button(user == null ? "Tambah" : "Simpan");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #CD853F)")
                .set("border", "none");

        saveBtn.addClickListener(e -> {
            // TODO: Implement save logic
            dialog.close();
            // Refresh grid would go here
        });

        buttonLayout.add(cancelBtn, saveBtn);

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.add(header, formLayout, buttonLayout);
        dialogContent.setPadding(false);
        dialogContent.setSpacing(false);

        dialog.add(dialogContent);
        dialog.open();
    }

    private void confirmDelete(com.example.application.models.Users user) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Konfirmasi Hapus");
        confirmDialog.setText("Apakah Anda yakin ingin menghapus user '" + user.getUsername() + "'?");

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelText("Batal");

        confirmDialog.setConfirmText("Hapus");
        confirmDialog.setConfirmButtonTheme("error primary");

        confirmDialog.addConfirmListener(e -> {
            // TODO: Implement delete logic
            usersList.remove(user);
            grid.getDataProvider().refreshAll();
        });

        confirmDialog.open();
    }
}