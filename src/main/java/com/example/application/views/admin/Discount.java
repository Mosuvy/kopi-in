package com.example.application.views.admin;

import com.example.application.dao.PromoDAO;
import com.example.application.models.Promo;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Discount Management - Kopi.in")
@Route(value = "admin/discount", layout = MainLayout.class)
public class Discount extends VerticalLayout {

    private Grid<Promo> grid;
    private List<Promo> promoList;
    private Dialog discountDialog;
    private TextField nameField, codeField;
    private TextArea descriptionField;
    private NumberField discountValueField, minPurchaseField;
    private DatePicker startDatePicker, endDatePicker;
    private Promo editingPromo;

    public Discount() {
        addClassName("discount-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Background gradient untuk seluruh halaman
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        initializeData();
        add(createHeader(), createToolbar(), createDiscountGrid());
        createDiscountDialog();
    }

    private void initializeData() {
        PromoDAO service = new PromoDAO();
        promoList = service.getListPromo();

        // Add null checks or default dates if needed
        for (Promo promo : promoList) {
            if (promo.getStart_date() == null) {
                promo.setStart_date(Date.valueOf(LocalDate.now()));
            }
            if (promo.getEnd_date() == null) {
                promo.setEnd_date(Date.valueOf(LocalDate.now().plusMonths(1)));
            }
        }
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

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.getStyle().set("position", "relative").set("z-index", "2");

        // Logo container
        Div logoContainer = new Div();
        logoContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "12px")
                .set("border-radius", "15px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("margin-right", "15px");

        Icon discountIcon = new Icon(VaadinIcon.TAG);
        discountIcon.setSize("36px");
        discountIcon.getStyle()
                .set("color", "#4E342E")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.3))");
        logoContainer.add(discountIcon);

        H1 title = new H1("Discount Management");
        title.getStyle()
                .set("margin", "0 0 0 20px")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        logoSection.add(logoContainer, title);

        // Stats section
        VerticalLayout statsSection = new VerticalLayout();
        statsSection.setPadding(false);
        statsSection.setSpacing(false);
        statsSection.setAlignItems(FlexComponent.Alignment.END);
        statsSection.getStyle().set("position", "relative").set("z-index", "2");

        Span totalDiscounts = new Span("Total: " + promoList.size() + " Discount");
        totalDiscounts.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "500")
                .set("color", "#F5DEB3")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.3)");

        long activeCount = promoList.stream().filter(this::isPromoActive).count();
        Span activeDiscounts = new Span("Aktif: " + activeCount);
        activeDiscounts.getStyle()
                .set("font-size", "14px")
                .set("color", "#90EE90")
                .set("margin-top", "2px");

        statsSection.add(totalDiscounts, activeDiscounts);
        header.add(logoSection, statsSection);

        return header;
    }

    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.getStyle()
                .set("padding", "0 15px 20px 15px");

        // Search field
        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari discount...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.getStyle()
                .set("background", "white")
                .set("border-radius", "25px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
                .set("border", "none")
                .set("width", "300px");

        // Add button
        Button addButton = new Button("Tambah Discount", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("border", "none")
                .set("border-radius", "25px")
                .set("padding", "12px 25px")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(139, 69, 19, 0.3)")
                .set("color", "white");

        addButton.addClickListener(e -> openDiscountDialog(null));

        toolbar.add(searchField, addButton);
        return toolbar;
    }

    private Component createDiscountGrid() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.getStyle()
                .set("background", "white")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("margin", "0 15px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0");

        grid = new Grid<>(Promo.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Configure columns
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

        grid.setItems(promoList);

        // Grid styling
        grid.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden");

        gridContainer.add(grid);
        gridContainer.setPadding(false);
        return gridContainer;
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

    private boolean isPromoActive(Promo promo) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = promo.getStart_date().toLocalDate();
        LocalDate endDate = promo.getEnd_date().toLocalDate();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    private Component createStatusBadge(Promo promo) {
        boolean isActive = isPromoActive(promo);
        String status = isActive ? "Aktif" : "Tidak Aktif";

        Span badge = new Span(status);

        if (isActive) {
            badge.getStyle()
                    .set("background", "linear-gradient(135deg, #E8F5E8, #F0FFF0)")
                    .set("color", "#228B22")
                    .set("border", "1px solid #90EE90");
        } else {
            badge.getStyle()
                    .set("background", "linear-gradient(135deg, #FFE8E8, #FFF0F0)")
                    .set("color", "#DC143C")
                    .set("border", "1px solid #FFB6C1");
        }

        badge.getStyle()
                .set("padding", "6px 12px")
                .set("border-radius", "20px")
                .set("font-size", "12px")
                .set("font-weight", "600");

        return badge;
    }

    private Component createActionButtons(Promo promo) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editButton.getStyle()
                .set("background", "linear-gradient(135deg, #4169E1, #6495ED)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "8px")
                .set("box-shadow", "0 2px 8px rgba(65, 105, 225, 0.3)");
        editButton.addClickListener(e -> openDiscountDialog(promo));

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteButton.getStyle()
                .set("background", "linear-gradient(135deg, #DC143C, #FF6347)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "8px")
                .set("box-shadow", "0 2px 8px rgba(220, 20, 60, 0.3)");
        deleteButton.addClickListener(e -> deletePromo(promo));

        Button toggleButton = new Button(new Icon(VaadinIcon.POWER_OFF));
        toggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        boolean isActive = isPromoActive(promo);
        if (isActive) {
            toggleButton.getStyle()
                    .set("background", "linear-gradient(135deg, #FF8C00, #FFA500)")
                    .set("color", "white");
        } else {
            toggleButton.getStyle()
                    .set("background", "linear-gradient(135deg, #32CD32, #90EE90)")
                    .set("color", "white");
        }
        toggleButton.getStyle()
                .set("border-radius", "8px")
                .set("padding", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0, 0.2)");
        toggleButton.addClickListener(e -> togglePromoStatus(promo));

        actions.add(editButton, deleteButton, toggleButton);
        return actions;
    }

    private void createDiscountDialog() {
        discountDialog = new Dialog();
        discountDialog.setWidth("600px");
        discountDialog.setCloseOnEsc(true);
        discountDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(false);

        // Dialog header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("color", "white")
                .set("padding", "20px")
                .set("margin", "-20px -20px 20px -20px")
                .set("border-radius", "10px 10px 0 0");

        H3 dialogTitle = new H3("Tambah Discount");
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("color", "white")
                .set("font-size", "20px");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.getStyle()
                .set("color", "white")
                .set("background", "transparent");
        closeButton.addClickListener(e -> discountDialog.close());

        header.add(dialogTitle, closeButton);

        // Form fields
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        nameField = new TextField("Nama Discount");
        nameField.setPlaceholder("Masukkan nama discount");

        codeField = new TextField("Kode Discount");
        codeField.setPlaceholder("Masukkan kode discount");

        descriptionField = new TextArea("Deskripsi");
        descriptionField.setPlaceholder("Masukkan deskripsi discount");
        descriptionField.setHeight("100px");

        discountValueField = new NumberField("Nilai Diskon (%)");
        discountValueField.setPlaceholder("0");
        discountValueField.setMin(0);
        discountValueField.setMax(100);

        minPurchaseField = new NumberField("Minimal Pembelian (Rp)");
        minPurchaseField.setPlaceholder("0");
        minPurchaseField.setMin(0);

        startDatePicker = new DatePicker("Tanggal Mulai");
        endDatePicker = new DatePicker("Tanggal Berakhir");

        formLayout.add(nameField, codeField);
        formLayout.add(descriptionField, 2);
        formLayout.add(discountValueField, minPurchaseField);
        formLayout.add(startDatePicker, endDatePicker);

        // Dialog buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "20px");

        Button cancelButton = new Button("Batal");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> discountDialog.close());

        Button saveButton = new Button("Simpan");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("border", "none");
        saveButton.addClickListener(e -> saveDiscount());

        buttonLayout.add(cancelButton, saveButton);

        dialogContent.add(header, formLayout, buttonLayout);
        discountDialog.add(dialogContent);
    }

    private void openDiscountDialog(Promo promo) {
        editingPromo = promo;

        if (promo == null) {
            // Add mode
            discountDialog.getElement().executeJs("this.getElementsByTagName('h3')[0].textContent = 'Tambah Discount'");
            clearForm();
        } else {
            // Edit mode
            discountDialog.getElement().executeJs("this.getElementsByTagName('h3')[0].textContent = 'Edit Discount'");
            populateForm(promo);
        }

        discountDialog.open();
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
        startDatePicker.setValue(promo.getStart_date().toLocalDate());
        endDatePicker.setValue(promo.getEnd_date().toLocalDate());
    }

    private void saveDiscount() {
        // Validasi form (implementasi sederhana)
        if (nameField.isEmpty() || codeField.isEmpty()) {
            Notification.show("Nama dan kode discount harus diisi!", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (editingPromo == null) {
            // Add new promo
            Promo newPromo = new Promo();
            newPromo.setId(String.valueOf(promoList.size() + 1));
            newPromo.setName(nameField.getValue());
            newPromo.setCode(codeField.getValue());
            newPromo.setDescription(descriptionField.getValue());
            newPromo.setDiscount_value(discountValueField.getValue());
            newPromo.setMin_purchase(minPurchaseField.getValue());
            newPromo.setStart_date(Date.valueOf(startDatePicker.getValue()));
            newPromo.setEnd_date(Date.valueOf(endDatePicker.getValue()));

            promoList.add(newPromo);
            Notification.show("Discount berhasil ditambahkan!", 3000, Notification.Position.MIDDLE);
        } else {
            // Edit existing promo
            editingPromo.setName(nameField.getValue());
            editingPromo.setCode(codeField.getValue());
            editingPromo.setDescription(descriptionField.getValue());
            editingPromo.setDiscount_value(discountValueField.getValue());
            editingPromo.setMin_purchase(minPurchaseField.getValue());
            editingPromo.setStart_date(Date.valueOf(startDatePicker.getValue()));
            editingPromo.setEnd_date(Date.valueOf(endDatePicker.getValue()));
            Notification.show("Discount berhasil diperbarui!", 3000, Notification.Position.MIDDLE);
        }

        grid.getDataProvider().refreshAll();
        discountDialog.close();
    }

    private void deletePromo(Promo promo) {
        promoList.remove(promo);
        grid.getDataProvider().refreshAll();
        Notification.show("Discount berhasil dihapus!", 3000, Notification.Position.MIDDLE);
    }

    private void togglePromoStatus(Promo promo) {
        // Toggle dengan mengubah tanggal berakhir
        LocalDate now = LocalDate.now();
        if (isPromoActive(promo)) {
            // Jika aktif, set end date ke kemarin untuk menonaktifkan
            promo.setEnd_date(Date.valueOf(now.minusDays(1)));
        } else {
            // Jika tidak aktif, set end date ke masa depan untuk mengaktifkan
            promo.setEnd_date(Date.valueOf(now.plusMonths(6)));
        }
        grid.getDataProvider().refreshAll();
        String newStatus = isPromoActive(promo) ? "Aktif" : "Tidak Aktif";
        Notification.show("Status discount berhasil diubah menjadi " + newStatus, 3000, Notification.Position.MIDDLE);
    }
}