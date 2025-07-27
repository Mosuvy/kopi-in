package com.example.application.views.admin;

import com.example.application.models.Products;
import com.example.application.models.Categories;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PageTitle("Kelola Produk - Kopi.in")
@Route(value = "admin/products", layout = MainLayout.class)
public class Product extends VerticalLayout {

    private Grid<Products> grid;
    private List<Products> products;
    private List<Categories> categories;
    private Dialog productDialog;
    private Products currentProduct;

    public Product() {
        addClassName("product-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Background gradient untuk seluruh halaman
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        initializeData();
        add(createHeader(), createActionBar(), createProductGrid());
    }

    private void initializeData() {
        // Initialize categories
        categories = new ArrayList<>();
        categories.add(createCategory("CAT001", "Minuman", "DRINK"));
        categories.add(createCategory("CAT002", "Makanan", "FOOD"));
        categories.add(createCategory("CAT003", "Dessert", "DESSERT"));

        // Initialize products
        products = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        products.add(createProduct("PRD001", "Kopi Hitam", "Kopi hitam premium dengan biji pilihan",
                15000.0, 50, "CAT001", "kopi-hitam.jpg", 1, now));
        products.add(createProduct("PRD002", "Cappuccino", "Kopi dengan susu foam yang creamy",
                25000.0, 30, "CAT001", "cappuccino.jpg", 1, now));
        products.add(createProduct("PRD003", "Latte", "Kombinasi espresso dengan steamed milk",
                28000.0, 25, "CAT001", "latte.jpg", 1, now));
        products.add(createProduct("PRD004", "Americano", "Espresso dengan air panas, rasa bold",
                20000.0, 40, "CAT001", "americano.jpg", 1, now));
        products.add(createProduct("PRD005", "Mocha", "Perpaduan kopi dan coklat yang nikmat",
                30000.0, 0, "CAT001", "mocha.jpg", 0, now));
        products.add(createProduct("PRD006", "Croissant", "Roti pastry Prancis yang renyah",
                18000.0, 20, "CAT002", "croissant.jpg", 1, now));
        products.add(createProduct("PRD007", "Sandwich", "Sandwich dengan isian daging dan sayur",
                22000.0, 15, "CAT002", "sandwich.jpg", 1, now));
        products.add(createProduct("PRD008", "Cheesecake", "Kue keju dengan topping berry",
                25000.0, 10, "CAT003", "cheesecake.jpg", 1, now));
    }

    private Categories createCategory(String id, String name, String code) {
        Categories category = new Categories();
        category.setId(id);
        category.setName(name);
        category.setCode(code);
        return category;
    }

    private Products createProduct(String id, String name, String description, Double price,
                                   Integer stock, String categoryId, String imageUrl,
                                   Integer isActive, Timestamp timestamp) {
        Products product = new Products();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory_id(categoryId);
        product.setImage_url(imageUrl);
        product.setIs_active(isActive);
        product.setCreated_at(timestamp);
        product.setUpdated_at(timestamp);
        return product;
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

        // Logo section dengan desain yang sama seperti dashboard
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.getStyle().set("position", "relative").set("z-index", "2");

        // Logo container dengan efek glow
        Div logoContainer = new Div();
        logoContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "12px")
                .set("border-radius", "15px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("margin-right", "15px");

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("36px");
        coffeeIcon.getStyle()
                .set("color", "#4E342E")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.3))");
        logoContainer.add(coffeeIcon);

        // Enhanced logo text
        Div logoText = new Div();
        logoText.getStyle()
                .set("display", "flex")
                .set("align-items", "center");

        Span k = new Span("K");
        k.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        Span o = new Span("o");
        o.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("color", "#D7A449")
                .set("position", "relative")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        // Coffee bean dalam huruf 'o'
        Div coffeeBean = new Div();
        coffeeBean.getStyle()
                .set("position", "absolute")
                .set("width", "8px")
                .set("height", "12px")
                .set("background", "radial-gradient(ellipse, #4E342E 30%, #2E1A0E 70%)")
                .set("border-radius", "50%")
                .set("transform", "rotate(25deg)")
                .set("top", "6px")
                .set("left", "7px")
                .set("box-shadow", "inset 0 1px 2px rgba(255,255,255,0.3)");
        o.add(coffeeBean);

        Span piIn = new Span("pi.in");
        piIn.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        logoText.add(k, o, piIn);

        H1 title = new H1("Kelola Produk");
        title.getStyle()
                .set("margin", "0 0 0 20px")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        logoSection.add(logoContainer, logoText, title);

        // Stats section
        VerticalLayout statsSection = new VerticalLayout();
        statsSection.setPadding(false);
        statsSection.setSpacing(false);
        statsSection.setAlignItems(FlexComponent.Alignment.END);
        statsSection.getStyle().set("position", "relative").set("z-index", "2");

        long activeProducts = products.stream().filter(p -> p.getIs_active() == 1).count();
        long totalStock = products.stream().mapToInt(p -> p.getStock() != null ? p.getStock() : 0).sum();

        Span totalProducts = new Span("Total: " + products.size() + " Produk (" + activeProducts + " Aktif)");
        totalProducts.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "500")
                .set("color", "#F5DEB3")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.3)");

        Span stockInfo = new Span("Total Stok: " + totalStock + " item");
        stockInfo.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8")
                .set("margin-top", "2px");

        Span lastUpdate = new Span("Update: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")));
        lastUpdate.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8")
                .set("margin-top", "2px");

        statsSection.add(totalProducts, stockInfo, lastUpdate);
        header.add(logoSection, statsSection);

        return header;
    }

    private Component createActionBar() {
        HorizontalLayout actionBar = new HorizontalLayout();
        actionBar.setWidthFull();
        actionBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        actionBar.setAlignItems(FlexComponent.Alignment.CENTER);
        actionBar.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("padding", "20px 25px")
                .set("border-radius", "15px")
                .set("margin", "0 15px 20px 15px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0");

        // Search section
        HorizontalLayout searchSection = new HorizontalLayout();
        searchSection.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari produk...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.getStyle()
                .set("width", "300px")
                .set("margin-right", "15px");

        Select<String> categoryFilter = new Select<>();
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Semua Kategori");
        categoryNames.addAll(categories.stream().map(Categories::getName).toList());
        categoryFilter.setItems(categoryNames);
        categoryFilter.setValue("Semua Kategori");
        categoryFilter.getStyle().set("width", "150px");

        Select<String> statusFilter = new Select<>();
        statusFilter.setItems("Semua Status", "Aktif", "Tidak Aktif");
        statusFilter.setValue("Semua Status");
        statusFilter.getStyle().set("width", "130px").set("margin-left", "10px");

        searchSection.add(searchField, categoryFilter, statusFilter);

        // Action buttons
        HorizontalLayout buttonSection = new HorizontalLayout();
        buttonSection.setAlignItems(FlexComponent.Alignment.CENTER);

        Button addButton = new Button("Tambah Produk", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("border", "none")
                .set("padding", "12px 20px")
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 15px rgba(139, 69, 19, 0.3)")
                .set("margin-right", "10px");

        addButton.addClickListener(e -> openProductDialog(null));

        Button exportButton = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        exportButton.getStyle()
                .set("border", "2px solid #8B4513")
                .set("color", "#8B4513")
                .set("padding", "10px 18px")
                .set("border-radius", "10px")
                .set("font-weight", "600");

        buttonSection.add(addButton, exportButton);
        actionBar.add(searchSection, buttonSection);

        return actionBar;
    }

    private Component createProductGrid() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "25px")
                .set("margin", "0 15px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0");

        H3 gridTitle = new H3("Daftar Produk");
        gridTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        grid = new Grid<>(Products.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.getStyle()
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.08)");

        // Image column
        grid.addComponentColumn(product -> {
            Div imageContainer = new Div();
            imageContainer.getStyle()
                    .set("width", "60px")
                    .set("height", "60px")
                    .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                    .set("border-radius", "12px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("color", "white")
                    .set("font-size", "24px")
                    .set("box-shadow", "0 4px 12px rgba(139, 69, 19, 0.3)");

            String emoji = getProductEmoji(getCategoryName(product.getCategory_id()));
            Span emojiSpan = new Span(emoji);
            imageContainer.add(emojiSpan);

            return imageContainer;
        }).setHeader("Gambar").setWidth("100px").setFlexGrow(0);

        // Product info column
        grid.addComponentColumn(product -> {
            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);

            Span name = new Span(product.getName());
            name.getStyle()
                    .set("font-weight", "600")
                    .set("font-size", "16px")
                    .set("color", "#333");

            Span id = new Span("ID: " + product.getId());
            id.getStyle()
                    .set("font-size", "12px")
                    .set("color", "#666")
                    .set("margin-top", "2px");

            Span description = new Span(product.getDescription());
            description.getStyle()
                    .set("font-size", "13px")
                    .set("color", "#888")
                    .set("margin-top", "4px")
                    .set("max-width", "200px")
                    .set("text-overflow", "ellipsis")
                    .set("white-space", "nowrap")
                    .set("overflow", "hidden");

            info.add(name, id, description);
            return info;
        }).setHeader("Informasi Produk").setFlexGrow(1);

        // Category column
        grid.addComponentColumn(product -> {
            String categoryName = getCategoryName(product.getCategory_id());
            Span categoryBadge = new Span(categoryName);
            String badgeColor = getCategoryColor(categoryName);
            categoryBadge.getStyle()
                    .set("background", "linear-gradient(135deg, " + badgeColor + ", " + badgeColor + "dd)")
                    .set("color", "white")
                    .set("padding", "6px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)");
            return categoryBadge;
        }).setHeader("Kategori").setWidth("120px").setFlexGrow(0);

        // Price column
        grid.addComponentColumn(product -> {
            Span price = new Span("Rp " + String.format("%,.0f", product.getPrice()));
            price.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "16px")
                    .set("color", "#8B4513");
            return price;
        }).setHeader("Harga").setWidth("120px").setFlexGrow(0);

        // Stock column
        grid.addComponentColumn(product -> {
            VerticalLayout stockInfo = new VerticalLayout();
            stockInfo.setPadding(false);
            stockInfo.setSpacing(false);
            stockInfo.setAlignItems(FlexComponent.Alignment.CENTER);

            Span stockValue = new Span(String.valueOf(product.getStock()));
            stockValue.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "18px")
                    .set("color", product.getStock() > 0 ? "#22C55E" : "#EF4444");

            Span stockLabel = new Span("items");
            stockLabel.getStyle()
                    .set("font-size", "11px")
                    .set("color", "#666");

            stockInfo.add(stockValue, stockLabel);
            return stockInfo;
        }).setHeader("Stok").setWidth("80px").setFlexGrow(0);

        // Status column
        grid.addComponentColumn(product -> {
            Span statusBadge = new Span(product.getIs_active() == 1 ? "Aktif" : "Tidak Aktif");
            if (product.getIs_active() == 1) {
                statusBadge.getStyle()
                        .set("background", "linear-gradient(135deg, #22C55E, #16A34A)")
                        .set("color", "white");
            } else {
                statusBadge.getStyle()
                        .set("background", "linear-gradient(135deg, #EF4444, #DC2626)")
                        .set("color", "white");
            }
            statusBadge.getStyle()
                    .set("padding", "6px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)");
            return statusBadge;
        }).setHeader("Status").setWidth("100px").setFlexGrow(0);

        // Actions column
        grid.addComponentColumn(product -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            editButton.getStyle()
                    .set("color", "#0EA5E9")
                    .set("border-radius", "8px")
                    .set("margin-right", "5px");
            editButton.addClickListener(e -> openProductDialog(product));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            deleteButton.getStyle()
                    .set("color", "#EF4444")
                    .set("border-radius", "8px");
            deleteButton.addClickListener(e -> deleteProduct(product));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Aksi").setWidth("120px").setFlexGrow(0);

        grid.setItems(products);
        grid.setHeight("500px");

        gridContainer.add(gridTitle, grid);
        gridContainer.setPadding(false);

        return gridContainer;
    }

    private String getCategoryName(String categoryId) {
        return categories.stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .map(Categories::getName)
                .findFirst()
                .orElse("Unknown");
    }

    private String getProductEmoji(String categoryName) {
        switch (categoryName) {
            case "Minuman": return "☕";
            case "Makanan": return "🥪";
            case "Dessert": return "🍰";
            default: return "🍽️";
        }
    }

    private String getCategoryColor(String categoryName) {
        switch (categoryName) {
            case "Minuman": return "#8B4513";
            case "Makanan": return "#CD853F";
            case "Dessert": return "#D2691E";
            default: return "#A0522D";
        }
    }

    private void openProductDialog(Products product) {
        currentProduct = product;
        boolean isEdit = product != null;

        productDialog = new Dialog();
        productDialog.setWidth("700px");
        productDialog.setCloseOnEsc(true);
        productDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);

        // Dialog header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("color", "white")
                .set("padding", "20px 25px")
                .set("margin", "-20px -20px 20px -20px")
                .set("border-radius", "12px 12px 0 0");

        H3 dialogTitle = new H3(isEdit ? "Edit Produk" : "Tambah Produk Baru");
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("color", "white")
                .set("font-size", "20px")
                .set("font-weight", "600");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.getStyle()
                .set("color", "white")
                .set("background", "rgba(255,255,255,0.1)")
                .set("border-radius", "8px");
        closeButton.addClickListener(e -> productDialog.close());

        header.add(dialogTitle, closeButton);

        // Form fields
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        TextField nameField = new TextField("Nama Produk");
        nameField.setPlaceholder("Contoh: Kopi Hitam Premium");
        if (isEdit) nameField.setValue(product.getName());

        TextArea descriptionField = new TextArea("Deskripsi");
        descriptionField.setPlaceholder("Deskripsi produk...");
        if (isEdit) descriptionField.setValue(product.getDescription());
        formLayout.setColspan(descriptionField, 2);

        NumberField priceField = new NumberField("Harga (Rp)");
        priceField.setPlaceholder("15000");
        if (isEdit) priceField.setValue(product.getPrice());

        IntegerField stockField = new IntegerField("Stok");
        stockField.setPlaceholder("50");
        stockField.setMin(0);
        if (isEdit) stockField.setValue(product.getStock());

        Select<Categories> categorySelect = new Select<>();
        categorySelect.setLabel("Kategori");
        categorySelect.setItems(categories);
        categorySelect.setItemLabelGenerator(Categories::getName);
        if (isEdit) {
            Categories currentCategory = categories.stream()
                    .filter(cat -> cat.getId().equals(product.getCategory_id()))
                    .findFirst().orElse(null);
            categorySelect.setValue(currentCategory);
        } else {
            categorySelect.setValue(categories.get(0));
        }

        TextField promoField = new TextField("Promo ID (Opsional)");
        promoField.setPlaceholder("PROMO001");
        if (isEdit && product.getPromo_id() != null) promoField.setValue(product.getPromo_id());

        TextField imageUrlField = new TextField("URL Gambar");
        imageUrlField.setPlaceholder("https://example.com/image.jpg");
        if (isEdit) imageUrlField.setValue(product.getImage_url());
        formLayout.setColspan(imageUrlField, 2);

        Checkbox activeCheckbox = new Checkbox("Produk Aktif");
        if (isEdit) activeCheckbox.setValue(product.getIs_active() == 1);
        else activeCheckbox.setValue(true);

        // Image upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drag gambar produk ke sini atau klik untuk upload"));
        formLayout.setColspan(upload, 2);

        formLayout.add(nameField, stockField, descriptionField, priceField, categorySelect,
                promoField, imageUrlField, activeCheckbox, upload);

        // Action buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "20px");

        Button cancelButton = new Button("Batal");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle()
                .set("margin-right", "10px")
                .set("border", "2px solid #6B7280")
                .set("color", "#6B7280")
                .set("padding", "10px 20px")
                .set("border-radius", "8px");
        cancelButton.addClickListener(e -> productDialog.close());

        Button saveButton = new Button(isEdit ? "Update" : "Simpan");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513 0%, #A0522D 100%)")
                .set("border", "none")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "600");

        saveButton.addClickListener(e -> {
            // Validation
            if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
                Notification.show("Nama produk harus diisi!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (priceField.getValue() == null || priceField.getValue() <= 0) {
                Notification.show("Harga harus lebih dari 0!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (stockField.getValue() == null || stockField.getValue() < 0) {
                Notification.show("Stok tidak boleh negatif!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (categorySelect.getValue() == null) {
                Notification.show("Kategori harus dipilih!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Save product
            saveProduct(isEdit, nameField.getValue(), descriptionField.getValue(),
                    priceField.getValue(), stockField.getValue(), categorySelect.getValue().getId(),
                    promoField.getValue(), imageUrlField.getValue(), activeCheckbox.getValue());
            productDialog.close();
        });

        buttonLayout.add(cancelButton, saveButton);

        dialogLayout.add(header, formLayout, buttonLayout);
        productDialog.add(dialogLayout);
        productDialog.open();
    }

    private void saveProduct(boolean isEdit, String name, String description, Double price,
                             Integer stock, String categoryId, String promoId, String imageUrl, Boolean isActive) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (isEdit) {
            // Update existing product
            currentProduct.setName(name);
            currentProduct.setDescription(description);
            currentProduct.setPrice(price);
            currentProduct.setStock(stock);
            currentProduct.setCategory_id(categoryId);
            currentProduct.setPromo_id(promoId != null && !promoId.trim().isEmpty() ? promoId : null);
            currentProduct.setImage_url(imageUrl);
            currentProduct.setIs_active(isActive ? 1 : 0);
            currentProduct.setUpdated_at(now);

            Notification notification = Notification.show("Produk berhasil diupdate! 🎉");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            // Add new product
            String newId = "PRD" + String.format("%03d", products.size() + 1);
            Products newProduct = new Products();
            newProduct.setId(newId);
            newProduct.setName(name);
            newProduct.setDescription(description);
            newProduct.setPrice(price);
            newProduct.setStock(stock);
            newProduct.setCategory_id(categoryId);
            newProduct.setPromo_id(promoId != null && !promoId.trim().isEmpty() ? promoId : null);
            newProduct.setImage_url(imageUrl);
            newProduct.setIs_active(isActive ? 1 : 0);
            newProduct.setCreated_at(now);
            newProduct.setUpdated_at(now);

            products.add(newProduct);

            Notification notification = Notification.show("Produk baru berhasil ditambahkan! ✨");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }

        grid.getDataProvider().refreshAll();
        updateHeaderStats();
    }

    private void updateHeaderStats() {
        // This method would refresh the header statistics
        // In a real application, you might want to refresh the entire header component
        removeAll();
        add(createHeader(), createActionBar(), createProductGrid());
    }

    private void deleteProduct(Products product) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("450px");

        VerticalLayout confirmLayout = new VerticalLayout();
        confirmLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        confirmLayout.setPadding(true);
        confirmLayout.setSpacing(true);

        Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.setSize("64px");
        warningIcon.getStyle()
                .set("color", "#EF4444")
                .set("margin-bottom", "10px");

        H3 confirmTitle = new H3("Hapus Produk?");
        confirmTitle.getStyle()
                .set("margin", "10px 0")
                .set("color", "#333")
                .set("text-align", "center");

        Div productInfo = new Div();
        productInfo.getStyle()
                .set("background", "#f9f9f9")
                .set("padding", "15px")
                .set("border-radius", "8px")
                .set("border-left", "4px solid #EF4444")
                .set("margin", "15px 0")
                .set("width", "100%");

        Span productName = new Span("Produk: " + product.getName());
        productName.getStyle()
                .set("font-weight", "600")
                .set("color", "#333")
                .set("display", "block")
                .set("margin-bottom", "5px");

        Span productId = new Span("ID: " + product.getId());
        productId.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("display", "block")
                .set("margin-bottom", "5px");

        Span stockInfo = new Span("Stok: " + product.getStock() + " item");
        stockInfo.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("display", "block");

        productInfo.add(productName, productId, stockInfo);

        Span confirmText = new Span("Data produk ini akan dihapus secara permanen dan tidak dapat dikembalikan.");
        confirmText.getStyle()
                .set("text-align", "center")
                .set("color", "#666")
                .set("margin-bottom", "20px")
                .set("line-height", "1.5");

        HorizontalLayout confirmButtons = new HorizontalLayout();
        confirmButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        confirmButtons.setSpacing(true);

        Button cancelButton = new Button("Batal");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle()
                .set("padding", "10px 20px")
                .set("border-radius", "8px");
        cancelButton.addClickListener(e -> confirmDialog.close());

        Button deleteButton = new Button("Hapus");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle()
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("font-weight", "600");
        deleteButton.addClickListener(e -> {
            products.remove(product);
            grid.getDataProvider().refreshAll();
            updateHeaderStats();

            Notification notification = Notification.show("Produk \"" + product.getName() + "\" berhasil dihapus! 🗑️");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            confirmDialog.close();
        });

        confirmButtons.add(cancelButton, deleteButton);
        confirmLayout.add(warningIcon, confirmTitle, productInfo, confirmText, confirmButtons);

        confirmDialog.add(confirmLayout);
        confirmDialog.open();
    }
}