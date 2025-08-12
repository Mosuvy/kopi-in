package com.example.application.views.customer;

import com.example.application.dao.CategoryDAOC;
import com.example.application.dao.ProductDAOC;
import com.example.application.models.CartItem;
import com.example.application.models.CategoriesC;
import com.example.application.models.ProductsC;
import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("kopi-In")
@Route(value = "customer", layout = AppLayoutNavbar.class)
public class HomePage extends VerticalLayout {

    private final ProductDAOC productDAO = new ProductDAOC();
    private final CategoryDAOC categoryDAO = new CategoryDAOC();
    private final List<CartItem> cartItems = new ArrayList<>();


    // TODO: Cek apakah user sudah login
    // - Jika login : tampilkan semua fitur
    // - Jika guest : tampilkan mode guest

    // TODO: Tampilan Home
    // - Menu
    // - Profil Pengguna
    // - History Pemesanan
    // - Logout

    // TODO: Fitur Buat Order
    // - Jika login : hubungkan ke akun
    // - Jika guest : simpan dengan akun guest

    // TODO: Fitur Profil Pengguna
    // - Jika login : tampilkan tombol "Profil"
    // - Jika guest : tampilkan tombol "Login / Register"

    // TODO: Fitur History Pemesanan
    // - Jika login: tampilkan riwayat transaksi sebelumnya
    // - Jika guest: tidak tampil

    // TODO: Fitur Logout

    // field
    TextField searchField = new TextField();

    // Main content area
    private VerticalLayout mainContent;

    public HomePage() {
        // Initialize main content
        mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setPadding(false);
        mainContent.setSpacing(false);

        Div heroSection = createHeroSection();
        Div filterSection = createFilterSection();
        Div menuSection = createMenuSection();

        // Footer
        Footer footer = createFooter();

        mainContent.add(heroSection, filterSection, menuSection);

        // Style the main layout
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setFlexGrow(1, mainContent);

        // Add components
        add(mainContent, footer);
    }

    // ================== Main Layout ==================
    private Div createHeroSection() {
        Div heroContainer = new Div();
        heroContainer.setId("hero-section");
        heroContainer.addClassName("hero-section");

        Image bgImage = new Image(
                "https://images.unsplash.com/photo-1447933601403-0c6688de566e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2061&q=80",
                "Background kopi"
        );
        bgImage.addClassName("hero-bg");

        VerticalLayout heroContent = new VerticalLayout();
        heroContent.addClassName("hero-content");

        H1 mainTitle = new H1("kopiIn");
        mainTitle.addClassName("hero-title");

        Span subtitle = new Span("Pesan kopi dengan mudah dan cepat");
        subtitle.addClassName("hero-subtitle");

        Button orderButton = new Button("Pesan Sekarang");
        orderButton.addClassName("order-button");

        orderButton.getElement().executeJs(
                "this.addEventListener('mouseenter', function() {" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 6px 20px rgba(205, 133, 63, 0.6)';" +
                        "});" +
                        "this.addEventListener('mouseleave', function() {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = '0 4px 15px rgba(205, 133, 63, 0.4)';" +
                        "});"
        );

        orderButton.addClickListener(event -> {
            orderButton.getUI().ifPresent(ui -> {
                ui.getPage().executeJs(
                        "document.getElementById('filter-section').scrollIntoView({" +
                                "  behavior: 'smooth'," +
                                "  block: 'start'" +
                                "});"
                );
            });
        });

        heroContent.add(mainTitle, subtitle, orderButton);
        heroContainer.add(bgImage, heroContent);

        return heroContainer;
    }

    // ================== Menu Section ==================
    private Div createMenuSection() {
        Div menuSection = new Div();
        menuSection.setId("menu-section");
        menuSection.addClassName("menu-section");

        List<ProductsC> allProducts = productDAO.getListProduct().stream()
                .filter(p -> p.getIs_active() != null && p.getIs_active() == 1)
                .collect(Collectors.toList());

        List<CategoriesC> allCategories = categoryDAO.getListCategories();

        Map<String, List<ProductsC>> groupedByCategory = allProducts.stream()
                .collect(Collectors.groupingBy(ProductsC::getCategory_id));

        for (CategoriesC category : allCategories) {
            String categoryId = category.getId();
            String categoryName = category.getName();

            List<ProductsC> productList = groupedByCategory.get(categoryId);
            if (productList == null || productList.isEmpty()) continue;

            H2 categoryTitle = new H2(categoryName);
            categoryTitle.addClassName("kategori-title");

            Div productGrid = new Div();
            productGrid.addClassName("product-grid");

            for (ProductsC p : productList) {
                Div card = createProductCard(
                        p.getId(), p.getName(), p.getImage_url(), p.getPrice(), p.getCategory_id()
                );
                productGrid.add(card);
            }

            menuSection.add(categoryTitle, productGrid);
        }

        return menuSection;
    }

    // ================== Filter Section ==================
    private Div createFilterSection() {
        Div filterSection = new Div();
        filterSection.setId("filter-section");
        filterSection.setClassName("filter-section");

        searchField.setPlaceholder("Cari produk...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.addClassName("search-input");

        ComboBox<String> kategoriFilter = new ComboBox<>();
        kategoriFilter.setPlaceholder("Pilih kategori");
        List<CategoriesC> categories = categoryDAO.getListCategories();

        Map<String, String> categoryNameToId = categories.stream()
                .collect(Collectors.toMap(CategoriesC::getName, CategoriesC::getId));

        List<String> categoryNames = categories.stream()
                .map(CategoriesC::getName)
                .collect(Collectors.toList());
        categoryNames.add(0, "Semua");

        kategoriFilter.setItems(categoryNames);
        kategoriFilter.setWidth("200px");
        kategoriFilter.addClassName("kategori-dropdown");

        Button removeCategory = new Button(VaadinIcon.CLOSE_SMALL.create());
        removeCategory.getElement().setProperty("title", "Hapus filter kategori");
        removeCategory.getStyle().set("margin-left", "5px");
        removeCategory.getStyle().set("color", "white");
        removeCategory.getStyle().set("background-color", "red");
        removeCategory.getStyle().set("border", "none");
        removeCategory.getStyle().set("border-radius", "5px");
        removeCategory.getStyle().set("padding", "0 5px");
        removeCategory.getStyle().set("cursor", "pointer");

        searchField.addValueChangeListener(e -> applyFilter(searchField.getValue(), kategoriFilter.getValue()));
        kategoriFilter.addValueChangeListener(e -> applyFilter(searchField.getValue(), kategoriFilter.getValue()));
        removeCategory.addClickListener(e -> {
            kategoriFilter.setValue("Semua");
            applyFilter(searchField.getValue(), "Semua");
        });

        HorizontalLayout filterLayout = new HorizontalLayout(searchField, kategoriFilter, removeCategory);
        filterLayout.setSpacing(true);
        filterLayout.setAlignItems(FlexComponent.Alignment.END);

        filterSection.add(filterLayout);
        return filterSection;
    }

    private void applyFilter(String keyword, String kategoriNama) {
        mainContent.removeAll();

        List<ProductsC> allProducts = productDAO.getListProduct().stream()
                .filter(p -> p.getIs_active() == 1)
                .collect(Collectors.toList());

        if (keyword != null && !keyword.trim().isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (kategoriNama != null && !kategoriNama.equals("Semua")) {
            String categoryId = categoryDAO.getListCategories().stream()
                    .filter(c -> c.getName().equals(kategoriNama))
                    .map(CategoriesC::getId)
                    .findFirst().orElse(null);
            allProducts = allProducts.stream()
                    .filter(p -> p.getCategory_id().equals(categoryId))
                    .collect(Collectors.toList());
        }

        Map<String, List<ProductsC>> groupedByCategory = allProducts.stream()
                .collect(Collectors.groupingBy(ProductsC::getCategory_id));

        Div filteredMenu = new Div();
        filteredMenu.addClassName("menu-section");

        if (allProducts.isEmpty()) {
            filteredMenu.add(new Paragraph("Produk tidak ditemukan."));
        } else {
            List<CategoriesC> allCategories = categoryDAO.getListCategories();

            for (CategoriesC category : allCategories) {
                String categoryId = category.getId();
                String categoryName = category.getName();
                List<ProductsC> productList = groupedByCategory.get(categoryId);

                if (productList == null || productList.isEmpty()) continue;

                H2 categoryTitle = new H2(categoryName);
                categoryTitle.addClassName("kategori-title");

                Div productGrid = new Div();
                productGrid.addClassName("product-grid");

                for (ProductsC p : productList) {
                    productGrid.add(createProductCard(p.getId(), p.getName(), p.getImage_url(), p.getPrice(), p.getCategory_id()));
                }

                filteredMenu.add(categoryTitle, productGrid);
            }
        }

        mainContent.add(createHeroSection(), createFilterSection(), filteredMenu);
    }

    // ================== Card Products ==================
    private Div createProductCard(String id, String nama, String imageUrl, double harga, String kategori) {
        Div card = new Div();
        card.addClassName("product-card");

        // Class kategori untuk filtering (contoh: kategori-kopi-hitam)
        String kategoriClass = "kategori-" + kategori.toLowerCase().replaceAll("\\s+", "-");
        card.addClassName(kategoriClass);

        Image img = new Image();
        img.addClassName("product-image");
        img.setAlt(nama);

        if (imageUrl == null || imageUrl.isEmpty()) {
            img.setSrc("https://placehold.co/400");
        } else {
            img.setSrc(imageUrl);
        }

        H3 title = new H3(nama);
        title.addClassName("product-title");

        Span price = new Span("Rp " + String.format("%,.0f", harga));
        price.addClassName("product-price");

        Button addToCart = new Button("Masukkan Keranjang");
        addToCart.addClassName("add-to-cart-button");

        addToCart.addClickListener(event -> {
            addToCart.getUI().ifPresent(ui -> {
                ui.getPage().executeJs("alert('Produk " + id +" berhasil ditambahkan ke keranjang!')");
                Optional<CartItem> existingItem = cartItems.stream()
                        .filter(item -> item.getProductId().equals(id))
                        .findFirst();

                if (existingItem.isPresent()) {
                    // Tambah quantity
                    CartItem item = existingItem.get();
                    item.setQuantity(item.getQuantity() + 1);
                } else {
                    // Tambah item baru
                    CartItem newItem = new CartItem(id, nama, harga, 1, imageUrl);
                    cartItems.add(newItem);
                }

                VaadinSession.getCurrent().setAttribute("cart", cartItems);
            });
        });

        card.add(img, title, price, addToCart);
        return card;
    }

    // ================== Footer ==================
    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassName("footer");

        VerticalLayout footerContent = new VerticalLayout();
        footerContent.addClassName("kopi-footer-content");
        footerContent.setSpacing(true);
        footerContent.setPadding(true);
        footerContent.setWidthFull();
        footerContent.setAlignItems(Alignment.CENTER);

        HorizontalLayout footerBody = new HorizontalLayout();
        footerBody.addClassName("footer-body");

        VerticalLayout infoSection = createInfoSection();
        VerticalLayout feedbackSection = createFeedbackSection();
        footerBody.add(infoSection, feedbackSection);

        Div copyright = new Div();
        Span copyrightText = new Span("© 2025 KopiIn. All rights reserved.");
        copyright.addClassName("footer-copyright");
        copyright.add(copyrightText);

        footerContent.add(footerBody, copyright);
        footer.add(footerContent);

        return footer;
    }

    private VerticalLayout createInfoSection() {
        VerticalLayout infoSection = new VerticalLayout();
        infoSection.setPadding(false);
        infoSection.setSpacing(false);
        infoSection.setWidth("50%");

        H1 appTitle = new H1("kopi-In");
        appTitle.addClassName("footer-info-title");

        Span tagline = new Span("Pesan kopi dengan cepat tanpa harus ke kasir.");
        tagline.addClassName("footer-info-sub");

        Span contact = new Span("Kontak: kopi@kopiin.com | 0821-1338-7838");
        contact.addClassName("footer-info-contact");

        infoSection.add(appTitle, tagline, contact);
        return infoSection;
    }

    private VerticalLayout createFeedbackSection() {
        VerticalLayout feedbackSection = new VerticalLayout();
        feedbackSection.setPadding(false);
        feedbackSection.setSpacing(false);
        feedbackSection.setWidth("45%");

        TextArea feedbackInput = new TextArea();
        feedbackInput.setPlaceholder("Pesan kamu...");
        feedbackInput.setWidthFull();
        feedbackInput.setHeight("100px");
        feedbackInput.addClassName("footer-feedback-textarea");

        Button submitButton = new Button("Kirim Feedback");
        submitButton.addClassName("footer-feedback-button");

        submitButton.addClickListener(event -> {
            String feedbackMessage = feedbackInput.getValue();
            if (!feedbackMessage.trim().isEmpty()) {
                feedbackInput.clear();
                submitButton.getUI().ifPresent(ui -> {
                    ui.getPage().executeJs("alert('Terima kasih atas feedback-nya!')");
                    // TODO: Kirim feedback ke server
                });
            }
        });

        feedbackSection.add(feedbackInput, submitButton);
        return feedbackSection;
    }
}