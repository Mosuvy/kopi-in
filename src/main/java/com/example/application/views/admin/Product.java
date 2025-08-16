package com.example.application.views.admin;

import com.example.application.dao.CategoryDAOC;
import com.example.application.dao.ProductDAO;
import com.example.application.models.Products;
import com.example.application.models.CategoriesC;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@PageTitle("Kelola Produk - Kopi.in")
@Route(value = "admin/products", layout = MainLayout.class)
public class Product extends VerticalLayout {

    private Grid<Products> grid;
    private List<Products> products;
    private List<CategoriesC> categories;
    private Dialog productDialog;
    private Products currentProduct;
    private ProductDAO productDAO;
    private CategoryDAOC categoryDAO;

    public Product() {
        addClassName("product-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Initialize DAOs
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAOC();

        // Background styling
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        initializeData();
        add(createHeader(), createActionBar(), createProductGrid());
    }

    private void initializeData() {
        // Load data from database
        categories = categoryDAO.getListCategories();
        products = productDAO.getListProduct();
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
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.15)");

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);

        Div logoContainer = new Div();
        logoContainer.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449 0%, #FFD700 100%)")
                .set("padding", "12px")
                .set("border-radius", "15px");

        Icon coffeeIcon = new Icon(VaadinIcon.COFFEE);
        coffeeIcon.setSize("36px");
        logoContainer.add(coffeeIcon);

        H1 title = new H1("Kelola Produk");
        title.getStyle()
                .set("margin", "0 0 0 20px")
                .set("color", "white");

        logoSection.add(logoContainer, title);

        // Stats section
        VerticalLayout statsSection = new VerticalLayout();
        statsSection.setPadding(false);
        statsSection.setSpacing(false);

        Span totalProducts = new Span("Total: " + products.size() + " Produk");
        totalProducts.getStyle()
                .set("font-size", "18px")
                .set("color", "#F5DEB3");

        Span lastUpdate = new Span("Update: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")));
        lastUpdate.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8");

        statsSection.add(totalProducts, lastUpdate);
        header.add(logoSection, statsSection);

        return header;
    }

    private Component createActionBar() {
        HorizontalLayout actionBar = new HorizontalLayout();
        actionBar.setWidthFull();
        actionBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        actionBar.setAlignItems(FlexComponent.Alignment.CENTER);
        actionBar.getStyle()
                .set("padding", "20px 25px")
                .set("margin", "0 15px 20px 15px");

        // Search field dengan fitur lebih lengkap
        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari produk...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.addValueChangeListener(e -> searchProducts(e.getValue()));

        // Add product button
        Button addButton = new Button("Tambah Produk", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openProductDialog(null));

        actionBar.add(searchField, addButton);
        return actionBar;
    }

    private void searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(products);
            return;
        }

        String[] keywords = searchTerm.toLowerCase().split("\\s+");

        List<Products> filteredProducts = products.stream()
                .filter(product ->
                        Arrays.stream(keywords).allMatch(keyword ->
                                String.valueOf(product.getId()).contains(keyword) ||
                                        product.getName().toLowerCase().contains(keyword)
                        )
                )
                .toList();

        grid.setItems(filteredProducts);
    }

    private void filterProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(products);
        } else {
            String lowerCaseFilter = searchTerm.toLowerCase();
            grid.setItems(products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(lowerCaseFilter) ||
                            product.getDescription().toLowerCase().contains(lowerCaseFilter))
                    .toList());
        }
    }

    private Component createProductGrid() {
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.getStyle()
                .set("padding", "25px")
                .set("margin", "0 15px");

        grid = new Grid<>(Products.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeight("70vh");

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
                    .set("font-size", "24px");

            if (product.getImage_url() != null && !product.getImage_url().isEmpty()) {
                imageContainer.add(new Image("images/products/" + product.getImage_url(),
                        getProductEmoji(getCategoryName(product.getCategory_id()))));
            } else {
                imageContainer.add(new Span(getProductEmoji(getCategoryName(product.getCategory_id()))));
            }

            return imageContainer;
        }).setHeader("Gambar").setWidth("100px");

        // Product info column
        grid.addComponentColumn(product -> {
            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);

            Span name = new Span(product.getName());
            name.getStyle().set("font-weight", "600");

            Span id = new Span("ID: " + product.getId());
            id.getStyle().set("font-size", "12px");

            info.add(name, id);
            return info;
        }).setHeader("Informasi Produk").setFlexGrow(1);

        // Price column
        grid.addComponentColumn(product -> {
            Span price = new Span("Rp " + String.format("%,.0f", product.getPrice()));
            price.getStyle().set("font-weight", "bold");
            return price;
        }).setHeader("Harga").setWidth("120px");

        // Status column
        grid.addComponentColumn(product -> {
            Span statusBadge = new Span(product.getIs_active() == 1 ? "Aktif" : "Tidak Aktif");
            statusBadge.getStyle()
                    .set("padding", "6px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("background", product.getIs_active() == 1 ? "#22C55E" : "#EF4444")
                    .set("color", "white");
            return statusBadge;
        }).setHeader("Status").setWidth("100px");

        // Action column
        grid.addComponentColumn(product -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> openProductDialog(product));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> confirmDeleteProduct(product));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Aksi").setWidth("120px");

        grid.setItems(products);
        gridContainer.add(grid);

        return gridContainer;
    }

    private void openProductDialog(Products product) {
        currentProduct = product;
        boolean isEdit = product != null;

        productDialog = new Dialog();
        productDialog.setWidth("700px");

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
                .set("padding", "15px")
                .set("margin-bottom", "15px")
                .set("border-radius", "8px 8px 0 0");

        H3 dialogTitle = new H3(isEdit ? "Edit Produk" : "Tambah Produk Baru");
        dialogTitle.getStyle().set("margin", "0").set("color", "white");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.getStyle().set("color", "white");
        closeButton.addClickListener(e -> productDialog.close());

        header.add(dialogTitle, closeButton);

        // Form fields
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Product name field
        TextField nameField = new TextField("Nama Produk");
        nameField.setPlaceholder("Contoh: Kopi Hitam Premium");
        nameField.setRequiredIndicatorVisible(true);
        if (isEdit) nameField.setValue(product.getName());

        // Price field
        NumberField priceField = new NumberField("Harga (Rp)");
        priceField.setPrefixComponent(new Span("Rp"));
        priceField.setRequiredIndicatorVisible(true);
        if (isEdit) priceField.setValue(product.getPrice());

        // Description field
        TextArea descriptionField = new TextArea("Deskripsi");
        descriptionField.setPlaceholder("Deskripsi produk...");
        descriptionField.setWidthFull();
        descriptionField.setHeight("150px");
        if (isEdit) descriptionField.setValue(product.getDescription());
        formLayout.setColspan(descriptionField, 2);

        // Category select
        Select<CategoriesC> categorySelect = new Select<>();
        categorySelect.setLabel("Kategori");
        categorySelect.setItems(categories);
        categorySelect.setItemLabelGenerator(CategoriesC::getName);
        categorySelect.setRequiredIndicatorVisible(true);
        if (isEdit) {
            categorySelect.setValue(categories.stream()
                    .filter(cat -> cat.getId().equals(product.getCategory_id()))
                    .findFirst().orElse(null));
        }

        // Image upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Seret file gambar ke sini atau klik untuk upload"));

        Image imagePreview = new Image();
        imagePreview.setMaxWidth("200px");
        imagePreview.setVisible(false);

        if (isEdit && product.getImage_url() != null && !product.getImage_url().isEmpty()) {
            imagePreview.setSrc("images/products/" + product.getImage_url());
            imagePreview.setVisible(true);
        }

        upload.addSucceededListener(event -> {
            // Show preview
            imagePreview.setSrc("data:" + event.getMIMEType() + ";base64," +
                    buffer.getFileData().toString());
            imagePreview.setVisible(true);
        });

        VerticalLayout imageUploadLayout = new VerticalLayout();
        imageUploadLayout.add(upload, imagePreview);
        formLayout.setColspan(imageUploadLayout, 2);

        // Active status checkbox
        Checkbox activeCheckbox = new Checkbox("Produk Aktif");
        if (isEdit) {
            activeCheckbox.setValue(product.getIs_active() == 1);
        } else {
            activeCheckbox.setValue(true); // Default active for new products
        }

        formLayout.add(
                nameField,
                priceField,
                descriptionField,
                categorySelect,
                imageUploadLayout,
                activeCheckbox
        );

        // Action buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);

        Button cancelButton = new Button("Batal");
        cancelButton.addClickListener(e -> productDialog.close());

        Button saveButton = new Button(isEdit ? "Update" : "Simpan");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            // Validation
            if (nameField.isEmpty() || priceField.isEmpty() || categorySelect.isEmpty()) {
                Notification.show("Harap isi semua field wajib!", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Handle image upload
            String imageUrl = null;
            if (buffer.getFileName() != null && !buffer.getFileName().isEmpty()) {
                // New image uploaded
                imageUrl = saveUploadedImage(buffer, buffer.getFileName(), nameField.getValue());
                if (imageUrl == null) {
                    return;
                }
            } else if (isEdit && product.getImage_url() != null && !product.getImage_url().isEmpty()) {
                // Keep existing image
                imageUrl = product.getImage_url();
            }

            saveProduct(
                    isEdit,
                    nameField.getValue(),
                    descriptionField.getValue(),
                    priceField.getValue(),
                    categorySelect.getValue().getId(),
                    imageUrl,
                    activeCheckbox.getValue() ? 1 : 0
            );

            productDialog.close();
        });

        buttonLayout.add(cancelButton, saveButton);
        dialogLayout.add(header, formLayout, buttonLayout);
        productDialog.add(dialogLayout);
        productDialog.open();
    }

    private void saveProduct(boolean isEdit, String name, String description, Double price,
                             String categoryId, String imageUrl, int isActive) {
        try {
            if (isEdit) {
                // Update existing product
                currentProduct.setName(name);
                currentProduct.setDescription(description);
                currentProduct.setPrice(price);
                currentProduct.setCategory_id(categoryId);
                currentProduct.setImage_url(imageUrl);
                currentProduct.setIs_active(isActive);
                currentProduct.setUpdated_at(new Timestamp(System.currentTimeMillis()));

                boolean success = productDAO.updateProduct(currentProduct);
                if (success) {
                    Notification.show("Produk berhasil diupdate!")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    refreshProductData();
                } else {
                    throw new Exception("Gagal update produk di database");
                }
            } else {
                // Create new product - TIDAK MENGGUNAKAN UUID LAGI
                Products newProduct = new Products();
                newProduct.setName(name);
                newProduct.setDescription(description);
                newProduct.setPrice(price);
                newProduct.setCategory_id(categoryId);
                newProduct.setImage_url(imageUrl);
                newProduct.setIs_active(isActive);
                // Timestamp akan dihandle oleh database atau DAO

                boolean success = productDAO.createProduct(newProduct);
                if (success) {
                    Notification.show("Produk berhasil ditambahkan!")
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    refreshProductData();
                } else {
                    throw new Exception("Gagal tambah produk ke database");
                }
            }
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private String saveUploadedImage(MemoryBuffer buffer, String fileName, String productName) {
        try {
            // Create products directory if it doesn't exist
            String uploadDir = "src/main/resources/META-INF/resources/images/products/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate filename based on product name
            String sanitizedProductName = productName.toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")  // Replace special chars with hyphen
                    .replaceAll("-+", "-")         // Replace multiple hyphens with single
                    .replaceAll("^-|-$", "");     // Remove leading/trailing hyphens

            // Get file extension
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

            // Combine with timestamp to ensure uniqueness
            String uniqueFileName = sanitizedProductName + "-" +
                    System.currentTimeMillis() + fileExtension;

            String filePath = uploadDir + uniqueFileName;

            // Save file
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer.getInputStream().readAllBytes());
            fos.close();

            // Return the filename
            return uniqueFileName;
        } catch (Exception e) {
            Notification.show("Gagal menyimpan gambar: " + e.getMessage(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    private void refreshProductData() {
        products = productDAO.getListProduct();
        grid.setItems(products);
    }

    private void confirmDeleteProduct(Products product) {
        if (product == null) {
            Notification.show("Produk tidak valid", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Hapus Produk");
        dialog.setText("Apakah Anda yakin ingin menghapus produk \"" + product.getName() + "\"?");

        dialog.setCancelable(true);
        dialog.setCancelText("Batal");

        dialog.setConfirmText("Hapus");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteProduct(product));
        dialog.open();
    }

    private void deleteProduct(Products product) {
        try {
            boolean success = productDAO.deleteProduct(product.getId());
            if (success) {
                products = productDAO.getListProduct(); // Refresh from DB
                grid.setItems(products);
                Notification.show("Produk berhasil dihapus!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                throw new Exception("Gagal menghapus produk");
            }
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private String getCategoryName(String categoryId) {
        return categories.stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .map(CategoriesC::getName)
                .findFirst()
                .orElse("Unknown");
    }

    private String getProductEmoji(String categoryName) {
        switch (categoryName) {
            case "Minuman": return "☕";
            case "Makanan": return "🥪";
            case "Dessert": return "🍰";
            default: return "🍽";
        }
    }
}