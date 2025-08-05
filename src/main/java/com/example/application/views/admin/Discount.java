package com.example.application.views.admin;

import com.example.application.dao.PromoDAO;
import com.example.application.models.Promo;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@PageTitle("Discount Management - Kopi.in")
@Route(value = "admin/discount", layout = MainLayout.class)
public class Discount extends VerticalLayout {

    // Constants
    private static final String PRIMARY_COLOR = "#8B4513";
    private static final String SECONDARY_COLOR = "#A0522D";
    private static final String ACTIVE_COLOR = "#228B22";
    private static final String INACTIVE_COLOR = "#DC143C";
    private static final int NOTIFICATION_DURATION = 3000;

    // UI Components
    private final Grid<Promo> grid = new Grid<>();
    private List<Promo> promoList;
    private final Dialog discountDialog = new Dialog();
    private final PromoDAO promoDAO = new PromoDAO();

    // Form Fields
    private final TextField nameField = new TextField("Nama Discount");
    private final TextField codeField = new TextField("Kode Discount");
    private final TextArea descriptionField = new TextArea("Deskripsi");
    private final NumberField discountValueField = new NumberField("Nilai Diskon (%)");
    private final NumberField minPurchaseField = new NumberField("Minimal Pembelian (Rp)");
    private final DatePicker startDatePicker = new DatePicker("Tanggal Mulai");
    private final DatePicker endDatePicker = new DatePicker("Tanggal Berakhir");

    private Promo editingPromo;

    public Discount() {
        addClassName("discount-view");
        configureLayout();
        initializeData();
        setupUIComponents();
    }

    private void configureLayout() {
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);
        setSizeFull(); // Menggunakan ukuran penuh layar

        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("height", "100vh") // Tinggi penuh viewport
                .set("overflow", "hidden"); // Mencegah scroll pada container utama
    }

    private void initializeData() {
        PromoDAO service = new PromoDAO();
        promoList = service.getListPromo();

        // Ensure all promos have valid dates
        promoList.forEach(promo -> {
            if (promo.getStart_date() == null) {
                promo.setStart_date(Date.valueOf(LocalDate.now()));
            }
            if (promo.getEnd_date() == null) {
                promo.setEnd_date(Date.valueOf(LocalDate.now().plusMonths(1)));
            }
        });
    }

    private void refreshPromoList() {
        promoList = promoDAO.getListPromo();
        grid.setItems(promoList);
        grid.getDataProvider().refreshAll();
    }

    private void setupUIComponents() {
        add(createHeader(), createStatsCards(), createDiscountGrid());
        configureDiscountDialog();
    }

    // Header Section
    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setHeight("80px"); // Fixed height untuk header
        header.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #795548 50%, #8B4513 100%)")
                .set("color", "white")
                .set("padding", "0 30px")
                .set("border-radius", "0 0 15px 15px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("flex-shrink", "0"); // Mencegah header menyusut

        // Add background pattern
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

        // Logo container with icon
        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "10px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("margin-right", "12px");

        Icon discountIcon = new Icon(VaadinIcon.TAG);
        discountIcon.setSize("28px");
        discountIcon.getStyle()
                .set("color", "#4E342E")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.3))");
        iconContainer.add(discountIcon);

        // Title
        H1 title = new H1("Discount Management");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "24px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        titleSection.add(iconContainer, title);

        Button addButton = new Button("Tambah Discount", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("color", "#4E342E")
                .set("border", "none")
                .set("border-radius", "10px")
                .set("padding", "10px 18px")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
        addButton.addClickListener(e -> openDiscountDialog(null));

        header.add(titleSection, addButton);
        return header;
    }

    // Stats Cards Section
    private Component createStatsCards() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        statsLayout.getStyle()
                .set("margin-bottom", "25px")
                .set("margin-top", "25px")
                .set("padding", "0 15px");

        long totalDiscounts = promoList.size();
        long activeDiscounts = promoList.stream().filter(this::isPromoActive).count();
        long inactiveDiscounts = totalDiscounts - activeDiscounts;

        statsLayout.add(
                createStatCard("Total Discounts", String.valueOf(totalDiscounts), VaadinIcon.TAG, "#8B4513", "🏷️"),
                createStatCard("Active", String.valueOf(activeDiscounts), VaadinIcon.CHECK_CIRCLE, "#228B22", "✅"),
                createStatCard("Inactive", String.valueOf(inactiveDiscounts), VaadinIcon.CLOSE_CIRCLE, "#DC143C", "❌")
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

    // Grid Section
    private Component createDiscountGrid() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setSizeFull(); // Menggunakan sisa ruang yang tersedia
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
        gridHeader.setHeight("50px"); // Fixed height untuk grid header
        gridHeader.getStyle()
                .set("margin-bottom", "15px")
                .set("flex-shrink", "0");

        H3 gridTitle = new H3("Daftar Discount");
        gridTitle.getStyle()
                .set("margin", "0")
                .set("color", "#4E342E")
                .set("font-size", "18px")
                .set("font-weight", "600");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari discount...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("250px");
        searchField.getStyle()
                .set("border-radius", "10px");

        gridHeader.add(gridTitle, searchField);

        configureGridColumns();
        grid.setItems(promoList);
        grid.setSizeFull(); // Grid menggunakan sisa ruang yang tersedia
        grid.getStyle()
                .set("border-radius", "10px")
                .set("overflow", "auto") // Scroll hanya pada grid jika diperlukan
                .set("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
                .set("flex", "1");

        gridContainer.add(gridHeader, grid);
        gridContainer.setPadding(false);

        return gridContainer;
    }

    private void configureGridColumns() {
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(Promo::getName)
                .setHeader(createColumnHeader("Nama Discount", "🏷️"))
                .setFlexGrow(2)
                .setSortable(true);

        grid.addColumn(Promo::getCode)
                .setHeader(createColumnHeader("Kode", "🎫"))
                .setFlexGrow(1);

        grid.addColumn(promo -> promo.getDiscount_value() + "%")
                .setHeader(createColumnHeader("Diskon", "💰"))
                .setFlexGrow(1);

        grid.addColumn(promo -> "Rp " + String.format("%,.0f", promo.getMin_purchase()))
                .setHeader(createColumnHeader("Min. Pembelian", "🛒"))
                .setFlexGrow(1);

        grid.addColumn(promo -> promo.getStart_date().toString())
                .setHeader(createColumnHeader("Mulai", "📅"))
                .setFlexGrow(1);

        grid.addColumn(promo -> promo.getEnd_date().toString())
                .setHeader(createColumnHeader("Berakhir", "📅"))
                .setFlexGrow(1);

        grid.addComponentColumn(this::createStatusBadge)
                .setHeader(createColumnHeader("Status", "🔄"))
                .setFlexGrow(1);

        grid.addComponentColumn(this::createActionButtons)
                .setHeader(createColumnHeader("Aksi", "⚙️"))
                .setFlexGrow(1);
    }

    private Component createColumnHeader(String text, String emoji) {
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);

        Span emojiSpan = new Span(emoji);
        emojiSpan.getStyle().set("margin-right", "8px");

        Span textSpan = new Span(text);
        textSpan.getStyle()
                .set("font-weight", "600")
                .set("color", "#4E342E");

        header.add(emojiSpan, textSpan);
        return header;
    }

    // Promo Status Methods
    private boolean isPromoActive(Promo promo) {
        if (promo.getStart_date() == null || promo.getEnd_date() == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate = promo.getStart_date().toLocalDate();
        LocalDate endDate = promo.getEnd_date().toLocalDate();

        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    private Component createStatusBadge(Promo promo) {
        boolean isActive = isPromoActive(promo);
        String status = isActive ? "Aktif" : "Tidak Aktif";
        Span badge = new Span(status);

        String bgColor = isActive ?
                "linear-gradient(135deg, #E8F5E8, #F0FFF0)" :
                "linear-gradient(135deg, #FFE8E8, #FFF0F0)";
        String textColor = isActive ? ACTIVE_COLOR : INACTIVE_COLOR;
        String borderColor = isActive ? "#90EE90" : "#FFB6C1";

        badge.getStyle()
                .set("background", bgColor)
                .set("color", textColor)
                .set("border", "1px solid " + borderColor)
                .set("padding", "6px 12px")
                .set("border-radius", "20px")
                .set("font-size", "12px")
                .set("font-weight", "600");

        return badge;
    }

    // Action Buttons
    private Component createActionButtons(Promo promo) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(
                createEditButton(promo),
                createDeleteButton(promo),
                createToggleButton(promo)
        );
        return actions;
    }

    private Button createEditButton(Promo promo) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.getStyle()
                .set("color", "#8B4513")
                .set("border-radius", "6px")
                .set("margin-right", "5px");
        editButton.addClickListener(e -> openDiscountDialog(promo));
        return editButton;
    }

    private Button createDeleteButton(Promo promo) {
        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        deleteButton.getStyle()
                .set("color", "#DC143C")
                .set("border-radius", "6px")
                .set("margin-right", "5px");
        deleteButton.addClickListener(e -> deletePromo(promo));
        return deleteButton;
    }

    private Button createToggleButton(Promo promo) {
        Button toggleButton = new Button(new Icon(VaadinIcon.POWER_OFF));
        toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        boolean isActive = isPromoActive(promo);
        String bgColor = isActive ? "#FF8C00" : "#32CD32";

        toggleButton.getStyle()
                .set("color", bgColor)
                .set("border-radius", "6px");
        toggleButton.addClickListener(e -> togglePromoStatus(promo));
        return toggleButton;
    }

    // Discount Dialog Methods
    private void configureDiscountDialog() {
        discountDialog.setWidth("600px");
        discountDialog.setCloseOnEsc(true);
        discountDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(false);
        dialogContent.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden");

        dialogContent.add(
                createDialogHeader(),
                createFormLayout(),
                createDialogButtons()
        );

        discountDialog.add(dialogContent);
    }

    private Component createDialogHeader() {
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

        Icon dialogIcon = new Icon(VaadinIcon.TAG);
        dialogIcon.setSize("20px");
        dialogIcon.getStyle().set("color", "#FFD700");

        H3 dialogTitle = new H3("Tambah Discount");
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("white-space", "nowrap");

        titleLayout.add(dialogIcon, dialogTitle);

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.getStyle()
                .set("color", "white")
                .set("background", "transparent")
                .set("border", "none")
                .set("padding", "8px");
        closeButton.addClickListener(e -> discountDialog.close());

        header.add(titleLayout, closeButton);
        return header;
    }

    private Component createFormLayout() {
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.getStyle()
                .set("padding", "25px")
                .set("background", "white");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        configureFormFields();

        formLayout.add(nameField, codeField);
        formLayout.add(descriptionField);
        formLayout.setColspan(descriptionField, 2);
        formLayout.add(discountValueField, minPurchaseField);
        formLayout.add(startDatePicker, endDatePicker);

        formContainer.add(formLayout);
        formContainer.setPadding(false);
        return formContainer;
    }

    private void configureFormFields() {
        nameField.setPlaceholder("Masukkan nama discount");
        codeField.setPlaceholder("Masukkan kode discount");
        descriptionField.setPlaceholder("Masukkan deskripsi discount");
        descriptionField.setHeight("100px");

        discountValueField.setPlaceholder("0");
        discountValueField.setMin(0);
        discountValueField.setMax(100);

        minPurchaseField.setPlaceholder("0");
        minPurchaseField.setMin(0);
    }

    private Component createDialogButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle()
                .set("margin-top", "20px")
                .set("padding", "0 25px 25px 25px");

        Button cancelButton = new Button("Batal");
        cancelButton.getStyle()
                .set("margin-right", "10px")
                .set("border-radius", "8px");
        cancelButton.addClickListener(e -> discountDialog.close());

        Button saveButton = new Button("Simpan");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #CD853F)")
                .set("border", "none")
                .set("border-radius", "8px");
        saveButton.addClickListener(e -> saveDiscount());

        buttonLayout.add(cancelButton, saveButton);
        return buttonLayout;
    }

    // Form Handling Methods
    private void openDiscountDialog(Promo promo) {
        editingPromo = promo;
        updateDialogTitle(promo == null ? "Tambah Discount" : "Edit Discount");

        if (promo == null) {
            clearForm();
        } else {
            populateForm(promo);
        }

        discountDialog.open();
    }

    private void updateDialogTitle(String title) {
        // Update icon based on action
        Icon newIcon = new Icon(editingPromo == null ? VaadinIcon.PLUS : VaadinIcon.EDIT);
        newIcon.setSize("20px");
        newIcon.getStyle().set("color", "#FFD700");

        // Find and update the title in the header
        HorizontalLayout header = (HorizontalLayout) ((VerticalLayout) discountDialog.getChildren().findFirst().get()).getChildren().findFirst().get();
        HorizontalLayout titleLayout = (HorizontalLayout) header.getChildren().findFirst().get();

        titleLayout.removeAll();

        H3 dialogTitle = new H3(title);
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("white-space", "nowrap");

        titleLayout.add(newIcon, dialogTitle);
    }

    private void clearForm() {
        nameField.clear();
        codeField.clear();
        descriptionField.clear();
        discountValueField.clear();
        minPurchaseField.clear();
        startDatePicker.clear();
        endDatePicker.clear();
    }

    private void populateForm(Promo promo) {
        nameField.setValue(promo.getName());
        codeField.setValue(promo.getCode());
        descriptionField.setValue(promo.getDescription());
        discountValueField.setValue(promo.getDiscount_value());
        minPurchaseField.setValue(promo.getMin_purchase());

        if (promo.getStart_date() != null) {
            startDatePicker.setValue(promo.getStart_date().toLocalDate());
        }
        if (promo.getEnd_date() != null) {
            endDatePicker.setValue(promo.getEnd_date().toLocalDate());
        }
    }

    // CRUD Operations
    private void saveDiscount() {
        if (!validateForm()) {
            return;
        }

        try {
            if (editingPromo == null) {
                addNewPromo();
            } else {
                updateExistingPromo();
            }

            grid.getDataProvider().refreshAll();
            discountDialog.close();
        } catch (Exception e) {
            Notification.show("Gagal menyimpan discount: " + e.getMessage(),
                    NOTIFICATION_DURATION, Notification.Position.MIDDLE);
        }
    }

    private boolean validateForm() {
        if (nameField.isEmpty() || codeField.isEmpty()) {
            showValidationError("Nama dan kode discount harus diisi!");
            return false;
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showValidationError("Tanggal mulai dan berakhir harus diisi!");
            return false;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showValidationError("Tanggal mulai tidak boleh setelah tanggal berakhir!");
            return false;
        }

        if (discountValueField.isEmpty() || discountValueField.getValue() <= 0) {
            showValidationError("Nilai diskon harus lebih dari 0%!");
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        Notification.show(message, NOTIFICATION_DURATION, Notification.Position.MIDDLE);
    }

    private void addNewPromo() {
        Promo newPromo = new Promo();
        newPromo.setId("PR-" + String.format("%05d", new Random().nextInt(99999)));
        newPromo.setName(nameField.getValue());
        newPromo.setCode(codeField.getValue());
        newPromo.setDescription(descriptionField.getValue());
        newPromo.setDiscount_value(discountValueField.getValue());
        newPromo.setMin_purchase(minPurchaseField.getValue());
        newPromo.setStart_date(Date.valueOf(startDatePicker.getValue()));
        newPromo.setEnd_date(Date.valueOf(endDatePicker.getValue()));

        if (promoDAO.createPromo(newPromo)) {
            Notification.show("Discount berhasil ditambahkan!", NOTIFICATION_DURATION, Notification.Position.MIDDLE);
            refreshPromoList();
        }
    }

    private void updateExistingPromo() {
        editingPromo.setName(nameField.getValue());
        editingPromo.setCode(codeField.getValue());
        editingPromo.setDescription(descriptionField.getValue());
        editingPromo.setDiscount_value(discountValueField.getValue());
        editingPromo.setMin_purchase(minPurchaseField.getValue());
        editingPromo.setStart_date(Date.valueOf(startDatePicker.getValue()));
        editingPromo.setEnd_date(Date.valueOf(endDatePicker.getValue()));

        if (promoDAO.updatePromo(editingPromo)) {
            Notification.show("Discount berhasil diperbarui!", NOTIFICATION_DURATION, Notification.Position.MIDDLE);
            refreshPromoList();
        }
    }

    private void deletePromo(Promo promo) {
        if (promoDAO.deletePromo(promo.getId())) {
            Notification.show("Discount berhasil dihapus!", NOTIFICATION_DURATION, Notification.Position.MIDDLE);
            refreshPromoList();
        }
    }

    private void togglePromoStatus(Promo promo) {
        LocalDate now = LocalDate.now();
        if (isPromoActive(promo)) {
            promo.setEnd_date(Date.valueOf(now.minusDays(1)));
        } else {
            promo.setEnd_date(Date.valueOf(now.plusMonths(6)));
        }

        promoDAO.updatePromo(promo);

        grid.getDataProvider().refreshAll();
        String newStatus = isPromoActive(promo) ? "Aktif" : "Tidak Aktif";
        Notification.show("Status discount berhasil diubah menjadi " + newStatus,
                NOTIFICATION_DURATION, Notification.Position.MIDDLE);
    }
}