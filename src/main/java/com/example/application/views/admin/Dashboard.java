package com.example.application.views.admin;

import com.example.application.models.Feedback;
import com.example.application.models.Users;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Dashboard Admin - Kopi.in")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class Dashboard extends VerticalLayout {

    // Mock data untuk feedback
    private List<Feedback> feedbackList;

    public Dashboard() {
        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Background gradient untuk seluruh halaman
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        initializeFeedbackData();

        add(createHeader(), createStatsCards(), createChartsSection(), createFeedbackSection());
    }

    private void initializeFeedbackData() {
        feedbackList = new ArrayList<>();

        // Create mock feedback data using your actual Feedback model
        Feedback feedback1 = new Feedback();
        feedback1.setId("1");
        feedback1.setUser_id("1");
        feedback1.setMessage("Kopi enak, pelayanan cepat!");
        feedback1.setCreated_at(new Timestamp(System.currentTimeMillis()));
        feedback1.setStatus("unread");

        Feedback feedback2 = new Feedback();
        feedback2.setId("2");
        feedback2.setUser_id("2");
        feedback2.setMessage("Tempat nyaman untuk bekerja, WiFi kencang");
        feedback2.setCreated_at(new Timestamp(System.currentTimeMillis()));
        feedback2.setStatus("read");

        Feedback feedback3 = new Feedback();
        feedback3.setId("3");
        feedback3.setUser_id("3");
        feedback3.setMessage("Harga agak mahal tapi worth it");
        feedback3.setCreated_at(new Timestamp(System.currentTimeMillis()));
        feedback3.setStatus("unread");

        Feedback feedback4 = new Feedback();
        feedback4.setId("4");
        feedback4.setUser_id("4");
        feedback4.setMessage("Menu variatif, sangat recommended!");
        feedback4.setCreated_at(new Timestamp(System.currentTimeMillis()));
        feedback4.setStatus("read");

        Feedback feedback5 = new Feedback();
        feedback5.setId("5");
        feedback5.setUser_id("5");
        feedback5.setMessage("Antrian agak lama saat rush hour");
        feedback5.setCreated_at(new Timestamp(System.currentTimeMillis()));
        feedback5.setStatus("unread");

        feedbackList.add(feedback1);
        feedbackList.add(feedback2);
        feedbackList.add(feedback3);
        feedbackList.add(feedback4);
        feedbackList.add(feedback5);
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

        // Logo section dengan desain yang lebih menarik
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

        logoSection.add(logoContainer, logoText);

        // Welcome section dengan waktu real-time yang diperjelas
        VerticalLayout welcomeSection = new VerticalLayout();
        welcomeSection.setPadding(false);
        welcomeSection.setSpacing(false);
        welcomeSection.setAlignItems(FlexComponent.Alignment.END);
        welcomeSection.getStyle().set("position", "relative").set("z-index", "2");

        Span welcomeText = new Span("Selamat Datang, Admin!");
        welcomeText.getStyle()
                .set("font-size", "20px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.4)")
                .set("margin-bottom", "2px");

        Span timeText = new Span("Hari ini, " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeText.getStyle()
                .set("font-size", "14px")
                .set("color", "#F5DEB3")
                .set("font-weight", "400")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.3)");

        welcomeSection.add(welcomeText, timeText);
        header.add(logoSection, welcomeSection);

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

        statsLayout.add(
                createEnhancedStatCard("Total Penjualan Hari Ini", "Rp 2.450.000", VaadinIcon.DOLLAR, "#8B4513", "📈"),
                createEnhancedStatCard("Pesanan Aktif", "24", VaadinIcon.CLIPBOARD_CHECK, "#A0522D", "🔥"),
                createEnhancedStatCard("Menu Tersedia", "18", VaadinIcon.MENU, "#CD853F", "☕"),
                createEnhancedStatCard("Customer Online", "156", VaadinIcon.USERS, "#D2691E", "👥")
        );

        return statsLayout;
    }

    private Component createEnhancedStatCard(String title, String value, VaadinIcon icon, String color, String emoji) {
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

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                    .set("transform", "translateY(-5px)")
                    .set("box-shadow", "0 15px 35px rgba(0,0,0,0.15)");
            bgDecor.getStyle().set("opacity", "0.8");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                    .set("transform", "translateY(0px)")
                    .set("box-shadow", "0 8px 25px rgba(0,0,0,0.1)");
            bgDecor.getStyle().set("opacity", "0.6");
        });

        return card;
    }

    private Component createChartsSection() {
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        chartsLayout.setSpacing(true);
        chartsLayout.getStyle()
                .set("padding", "0 15px")
                .set("margin-bottom", "25px");

        // Sales Chart dengan ukuran yang diperbesar
        VerticalLayout salesChartContainer = new VerticalLayout();
        salesChartContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("flex", "2")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-height", "450px"); // Diperbesar tingginya

        // Header section
        HorizontalLayout chartHeader = new HorizontalLayout();
        chartHeader.setWidthFull();
        chartHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        chartHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 salesTitle = new H3("Penjualan 7 Hari Terakhir");
        salesTitle.getStyle()
                .set("margin", "0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        Span trendIndicator = new Span("📈 +15%");
        trendIndicator.getStyle()
                .set("background", "linear-gradient(135deg, #E8F5E8, #F0FFF0)")
                .set("color", "#228B22")
                .set("padding", "6px 12px")
                .set("border-radius", "20px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("border", "1px solid #90EE90");

        chartHeader.add(salesTitle, trendIndicator);

        Component salesChart = createAnimatedSalesChart();
        salesChartContainer.add(chartHeader, salesChart);
        salesChartContainer.setPadding(false);

        // Menu populer dengan style yang ditingkatkan dan diperbesar
        VerticalLayout menuChartContainer = new VerticalLayout();
        menuChartContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("flex", "1")
                .set("min-height", "450px"); // Diperbesar tingginya

        H3 menuTitle = new H3("Menu Populer Hari Ini");
        menuTitle.getStyle()
                .set("margin", "0 0 25px 0")
                .set("color", "#4E342E")
                .set("font-size", "18px")
                .set("font-weight", "600");

        Component menuChart = createEnhancedMenuChart();
        menuChartContainer.add(menuTitle, menuChart);
        menuChartContainer.setPadding(false);

        chartsLayout.add(salesChartContainer, menuChartContainer);
        return chartsLayout;
    }

    private Component createFeedbackSection() {
        VerticalLayout feedbackSection = new VerticalLayout();
        feedbackSection.setWidthFull();
        feedbackSection.getStyle()
                .set("padding", "0 15px 25px 15px");

        // Feedback card container
        Div feedbackCard = new Div();
        feedbackCard.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Background decoration
        Div bgDecor = new Div();
        bgDecor.getStyle()
                .set("position", "absolute")
                .set("top", "-50px")
                .set("right", "-50px")
                .set("width", "150px")
                .set("height", "150px")
                .set("background", "linear-gradient(135deg, #8B451320, #8B451310)")
                .set("border-radius", "50%")
                .set("opacity", "0.6");
        feedbackCard.add(bgDecor);

        HorizontalLayout feedbackHeader = new HorizontalLayout();
        feedbackHeader.setWidthFull();
        feedbackHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        feedbackHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        feedbackHeader.getStyle()
                .set("position", "relative")
                .set("z-index", "2")
                .set("margin-bottom", "20px");

        // Title section with icon
        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #CD853F)")
                .set("padding", "12px")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 15px rgba(139, 69, 19, 0.4)")
                .set("margin-right", "10px");

        Icon feedbackIcon = new Icon(VaadinIcon.COMMENT);
        feedbackIcon.setSize("24px");
        feedbackIcon.getStyle().set("color", "white");
        iconContainer.add(feedbackIcon);

        H3 feedbackTitle = new H3("Customer Feedback");
        feedbackTitle.getStyle()
                .set("margin", "0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        titleSection.add(iconContainer, feedbackTitle);

        // Feedback stats
        HorizontalLayout statsSection = new HorizontalLayout();
        statsSection.setAlignItems(FlexComponent.Alignment.CENTER);
        statsSection.setSpacing(true);

        long unreadCount = feedbackList.stream().filter(f -> "unread".equals(f.getStatus())).count();

        Span totalFeedback = new Span("Total: " + feedbackList.size());
        totalFeedback.getStyle()
                .set("background", "linear-gradient(135deg, #E3F2FD, #F3E5F5)")
                .set("color", "#1976D2")
                .set("padding", "6px 12px")
                .set("border-radius", "15px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-right", "8px");

        Span unreadFeedback = new Span("Unread: " + unreadCount);
        unreadFeedback.getStyle()
                .set("background", "linear-gradient(135deg, #FFF3E0, #FFE0B2)")
                .set("color", "#F57C00")
                .set("padding", "6px 12px")
                .set("border-radius", "15px")
                .set("font-size", "12px")
                .set("font-weight", "600");

        statsSection.add(totalFeedback, unreadFeedback);

        feedbackHeader.add(titleSection, statsSection);

        // Description
        Span description = new Span("Lihat dan kelola semua feedback dari customer untuk meningkatkan kualitas layanan.");
        description.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("margin-bottom", "25px")
                .set("line-height", "1.5")
                .set("position", "relative")
                .set("z-index", "2");

        // View feedback button
        Button viewFeedbackBtn = new Button("Lihat Semua Feedback", new Icon(VaadinIcon.EYE));
        viewFeedbackBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewFeedbackBtn.getStyle()
                .set("background", "linear-gradient(135deg, #8B4513, #CD853F)")
                .set("border", "none")
                .set("border-radius", "12px")
                .set("padding", "12px 24px")
                .set("font-weight", "600")
                .set("font-size", "14px")
                .set("box-shadow", "0 6px 20px rgba(139, 69, 19, 0.3)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("position", "relative")
                .set("z-index", "2");

        viewFeedbackBtn.addClickListener(e -> openFeedbackModal());

        // Hover effect
        viewFeedbackBtn.getElement().addEventListener("mouseenter", e -> {
            viewFeedbackBtn.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 8px 25px rgba(139, 69, 19, 0.4)");
        });

        viewFeedbackBtn.getElement().addEventListener("mouseleave", e -> {
            viewFeedbackBtn.getStyle()
                    .set("transform", "translateY(0px)")
                    .set("box-shadow", "0 6px 20px rgba(139, 69, 19, 0.3)");
        });

        VerticalLayout content = new VerticalLayout();
        content.add(feedbackHeader, description, viewFeedbackBtn);
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
                .set("position", "relative")
                .set("z-index", "2");

        feedbackCard.add(content);
        feedbackSection.add(feedbackCard);
        feedbackSection.setPadding(false);

        return feedbackSection;
    }

    private void openFeedbackModal() {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");
        dialog.setHeight("700px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setSizeFull();
        dialogContent.setPadding(false);
        dialogContent.setSpacing(false);
        dialogContent.getStyle()
                .set("border-radius", "15px")
                .set("overflow", "hidden");

        // Header
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

        Icon dialogIcon = new Icon(VaadinIcon.COMMENT);
        dialogIcon.setSize("24px");
        dialogIcon.getStyle().set("color", "#FFD700");

        H3 dialogTitle = new H3("Customer Feedback");
        dialogTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "20px")
                .set("font-weight", "600")
                .set("color", "white");

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

        // Content area
        VerticalLayout contentArea = new VerticalLayout();
        contentArea.setSizeFull();
        contentArea.getStyle()
                .set("background", "white")
                .set("padding", "20px");

        // Search field
        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari feedback...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.getStyle()
                .set("margin-bottom", "20px")
                .set("border-radius", "10px");

        // Grid
        Grid<Feedback> grid = new Grid<>(Feedback.class, false);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.getStyle()
                .set("border-radius", "10px")
                .set("border", "1px solid #e0e0e0");

        // Configure grid columns
        grid.addColumn(new ComponentRenderer<>(feedback -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            Icon userIcon = new Icon(VaadinIcon.USER);
            userIcon.setSize("16px");
            userIcon.getStyle().set("color", "#8B4513").set("margin-right", "8px");

            Span username = new Span("User " + feedback.getUser_id());
            username.getStyle().set("font-weight", "500");

            layout.add(userIcon, username);
            return layout;
        })).setHeader("User").setWidth("150px");

        grid.addColumn(new ComponentRenderer<>(feedback -> {
            Span message = new Span(feedback.getMessage());
            message.getStyle()
                    .set("max-width", "300px")
                    .set("white-space", "normal")
                    .set("word-wrap", "break-word")
                    .set("line-height", "1.4");
            return message;
        })).setHeader("Message").setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(feedback -> {
            Span timestamp = new Span(feedback.getCreated_at().toString());
            timestamp.getStyle().set("font-size", "12px");
            return timestamp;
        })).setHeader("Submitted At").setWidth("150px");

        grid.addColumn(new ComponentRenderer<>(feedback -> {
            Span statusBadge = new Span(feedback.getStatus());
            String statusColor = feedback.getStatus().equals("read") ? "#228B22" : "#FF6B35";
            statusBadge.getStyle()
                    .set("background", "linear-gradient(135deg, " + statusColor + ", " + statusColor + "dd)")
                    .set("color", "white")
                    .set("padding", "4px 12px")
                    .set("border-radius", "15px")
                    .set("font-size", "11px")
                    .set("font-weight", "600")
                    .set("text-transform", "uppercase");
            return statusBadge;
        })).setHeader("Status").setWidth("100px");

        grid.addColumn(new ComponentRenderer<>(feedback -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            if ("unread".equals(feedback.getStatus())) {
                Button markReadBtn = new Button(new Icon(VaadinIcon.CHECK));
                markReadBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
                markReadBtn.getStyle()
                        .set("color", "#228B22")
                        .set("border-radius", "6px");
                markReadBtn.addClickListener(e -> {
                    feedback.setStatus("read");
                    grid.getDataProvider().refreshItem(feedback);
                });
                actions.add(markReadBtn);
            }

            return actions;
        })).setHeader("Action").setWidth("80px").setFlexGrow(0);

        // Search functionality
        List<Feedback> filteredFeedback = new ArrayList<>(feedbackList);
        grid.setItems(filteredFeedback);

        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> {
            String searchTerm = e.getValue().toLowerCase();
            List<Feedback> filtered = feedbackList.stream()
                    .filter(feedback ->
                            feedback.getUser_id().toLowerCase().contains(searchTerm) ||
                                    feedback.getMessage().toLowerCase().contains(searchTerm))
                    .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
            grid.setItems(filtered);
        });

        contentArea.add(searchField, grid);
        contentArea.setPadding(false);

        dialogContent.add(header, contentArea);
        dialog.add(dialogContent);
        dialog.open();
    }

    private Component createAnimatedSalesChart() {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        // Stats summary
        HorizontalLayout statsRow = createChartStats();

        // Custom bar chart dengan animasi yang lebih bagus
        Component customChart = createCustomSalesChart();

        chartContainer.add(statsRow, customChart);
        return chartContainer;
    }

    private Component createCustomSalesChart() {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        // Data penjualan (dalam jutaan rupiah)
        String[] days = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        double[] sales = {1.2, 1.45, 1.8, 2.1, 2.45, 2.8, 1.9}; // dalam jutaan
        double maxSales = 2.8;

        // Chart area dengan gradient background - diperbesar
        Div chartArea = new Div();
        chartArea.getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("border-radius", "15px")
                .set("padding", "25px")
                .set("position", "relative")
                .set("min-width", "680px")
                .set("min-height", "320px") // Diperbesar dari 280px
                .set("box-shadow", "inset 0 2px 8px rgba(0,0,0,0.05)")
                .set("flex", "1");

        // Grid lines background
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("position", "absolute")
                .set("top", "25px")
                .set("left", "25px")
                .set("right", "25px")
                .set("bottom", "25px")
                .set("background-image", "linear-gradient(to top, rgba(139,69,19,0.1) 1px, transparent 1px)")
                .set("background-size", "100% 40px")
                .set("border-radius", "8px");
        chartArea.add(gridContainer);

        // Bars container
        HorizontalLayout barsContainer = new HorizontalLayout();
        barsContainer.setWidthFull();
        barsContainer.setAlignItems(FlexComponent.Alignment.END);
        barsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        barsContainer.getStyle()
                .set("height", "260px") // Diperbesar dari 220px
                .set("padding", "20px 10px")
                .set("position", "relative")
                .set("z-index", "2");

        for (int i = 0; i < days.length; i++) {
            VerticalLayout barColumn = new VerticalLayout();
            barColumn.setAlignItems(FlexComponent.Alignment.CENTER);
            barColumn.setPadding(false);
            barColumn.setSpacing(false);
            barColumn.getStyle()
                    .set("flex", "1")
                    .set("height", "100%")
                    .set("position", "relative")
                    .set("cursor", "pointer")
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("justify-content", "flex-end");

            // Value label di atas bar
            Span valueLabel = new Span("Rp " + sales[i] + "M");
            valueLabel.getStyle()
                    .set("font-size", "11px")
                    .set("font-weight", "600")
                    .set("color", "#8B4513")
                    .set("background", "white")
                    .set("padding", "4px 8px")
                    .set("border-radius", "8px")
                    .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)")
                    .set("margin-bottom", "8px")
                    .set("opacity", "0")
                    .set("transform", "translateY(-10px)")
                    .set("transition", "all 0.3s ease")
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("left", "50%")
                    .set("transform", "translateX(-50%) translateY(-10px)")
                    .set("z-index", "10");

            // Bar area container - untuk memastikan animasi dari bawah
            Div barAreaContainer = new Div();
            double barHeight = (sales[i] / maxSales) * 180; // max height 180px (diperbesar)
            barAreaContainer.getStyle()
                    .set("width", "40px") // Diperbesar dari 35px
                    .set("height", barHeight + "px")
                    .set("position", "relative")
                    .set("display", "flex")
                    .set("align-items", "flex-end")
                    .set("margin-bottom", "10px");

            // Bar yang sebenarnya - yang akan di-animate
            Div barContainer = new Div();
            barContainer.getStyle()
                    .set("width", "100%")
                    .set("height", "0%")
                    .set("background", "linear-gradient(to top, #8B4513 0%, #CD853F 50%, #DEB887 100%)")
                    .set("border-radius", "8px 8px 4px 4px")
                    .set("position", "relative")
                    .set("transition", "height 1.2s cubic-bezier(0.4, 0, 0.2, 1)")
                    .set("box-shadow", "0 4px 15px rgba(139, 69, 19, 0.3)")
                    .set("overflow", "hidden")
                    .set("transform-origin", "bottom");

            // Shine effect di dalam bar
            Div shine = new Div();
            shine.getStyle()
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("left", "-100%")
                    .set("width", "100%")
                    .set("height", "100%")
                    .set("background", "linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent)")
                    .set("animation", "shine 3s infinite");
            barContainer.add(shine);

            barAreaContainer.add(barContainer);

            // Day label - tetap di bawah
            Span dayLabel = new Span(days[i]);
            dayLabel.getStyle()
                    .set("font-size", "12px")
                    .set("font-weight", "600")
                    .set("color", "#8B4513")
                    .set("background", "white")
                    .set("padding", "6px 10px")
                    .set("border-radius", "10px")
                    .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                    .set("border", "1px solid #f0f0f0")
                    .set("margin-top", "0");

            barColumn.add(valueLabel, barAreaContainer, dayLabel);

            // Hover effects
            final int currentIndex = i;
            barColumn.getElement().addEventListener("mouseenter", e -> {
                barAreaContainer.getStyle()
                        .set("transform", "translateY(-3px)")
                        .set("transition", "transform 0.3s ease");
                barContainer.getStyle()
                        .set("box-shadow", "0 8px 25px rgba(139, 69, 19, 0.4)");
                valueLabel.getStyle()
                        .set("opacity", "1")
                        .set("transform", "translateX(-50%) translateY(0px)");
            });

            barColumn.getElement().addEventListener("mouseleave", e -> {
                barAreaContainer.getStyle()
                        .set("transform", "translateY(0px)");
                barContainer.getStyle()
                        .set("box-shadow", "0 4px 15px rgba(139, 69, 19, 0.3)");
                valueLabel.getStyle()
                        .set("opacity", "0")
                        .set("transform", "translateX(-50%) translateY(-10px)");
            });

            barsContainer.add(barColumn);

            // Animate bars dari bawah ke atas dengan delay
            barContainer.getElement().executeJs(
                    "setTimeout(() => { " +
                            "this.style.height = '100%'; " +
                            "}, " + (i * 200 + 500) + ");"
            );
        }

        chartArea.add(barsContainer);

        // Add CSS keyframes untuk animasi shine
        chartContainer.getElement().executeJs(
                "if (!document.querySelector('#shine-animation')) {" +
                        "const style = document.createElement('style');" +
                        "style.id = 'shine-animation';" +
                        "style.textContent = `" +
                        "@keyframes shine {" +
                        "  0% { left: -100%; }" +
                        "  100% { left: 100%; }" +
                        "}" +
                        "`;" +
                        "document.head.appendChild(style);" +
                        "}"
        );

        chartContainer.add(chartArea);
        return chartContainer;
    }

    private HorizontalLayout createChartStats() {
        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.setWidthFull();
        statsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        statsRow.getStyle()
                .set("margin-bottom", "20px")
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("border-radius", "12px")
                .set("padding", "15px");

        statsRow.add(
                createMiniStat("Rata-rata", "Rp 1.971k", "#8B4513", "📊"),
                createMiniStat("Tertinggi", "Rp 2.800k", "#CD853F", "🚀"),
                createMiniStat("Trend", "+15%", "#228B22", "📈")
        );

        return statsRow;
    }

    private Div createMiniStat(String label, String value, String color, String emoji) {
        Div container = new Div();
        container.getStyle()
                .set("background-color", "white")
                .set("border-radius", "10px")
                .set("padding", "15px")
                .set("text-align", "center")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)")
                .set("border-top", "3px solid " + color)
                .set("min-width", "100px")
                .set("flex", "1")
                .set("margin", "0 5px")
                .set("position", "relative");

        Span emojiSpan = new Span(emoji);
        emojiSpan.getStyle()
                .set("font-size", "16px")
                .set("display", "block")
                .set("margin-bottom", "5px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "12px")
                .set("color", "#666")
                .set("display", "block")
                .set("font-weight", "500");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "bold")
                .set("color", color)
                .set("display", "block")
                .set("margin-top", "5px");

        container.add(emojiSpan, labelSpan, valueSpan);
        return container;
    }

    private Component createEnhancedMenuChart() {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        // Data menu populer
        String[] menus = {"Kopi Hitam", "Cappuccino", "Latte", "Americano", "Mocha"};
        int[] percentages = {35, 25, 20, 12, 8};
        String[] colors = {"#8B4513", "#A0522D", "#CD853F", "#D2691E", "#DEB887"};
        String[] icons = {"☕", "🥛", "🍯", "💧", "🍫"};

        for (int i = 0; i < menus.length; i++) {
            Div menuRow = new Div();
            menuRow.getStyle()
                    .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                    .set("border-radius", "12px")
                    .set("padding", "18px") // Diperbesar dari 15px
                    .set("margin-bottom", "15px") // Diperbesar dari 12px
                    .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                    .set("border-left", "4px solid " + colors[i])
                    .set("cursor", "pointer")
                    .set("transition", "all 0.3s ease")
                    .set("position", "relative")
                    .set("overflow", "hidden");

            HorizontalLayout menuContent = new HorizontalLayout();
            menuContent.setWidthFull();
            menuContent.setAlignItems(FlexComponent.Alignment.CENTER);

            // Enhanced icon container - diperbesar
            Div iconContainer = new Div();
            iconContainer.getStyle()
                    .set("width", "50px") // Diperbesar dari 45px
                    .set("height", "50px")
                    .set("background", "linear-gradient(135deg, " + colors[i] + ", " + colors[i] + "cc)")
                    .set("border-radius", "12px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("font-size", "22px") // Diperbesar dari 20px
                    .set("margin-right", "15px")
                    .set("box-shadow", "0 4px 12px " + colors[i] + "40");

            Span iconSpan = new Span(icons[i]);
            iconContainer.add(iconSpan);

            // Menu info dengan progress bar yang lebih bagus
            VerticalLayout menuInfo = new VerticalLayout();
            menuInfo.setPadding(false);
            menuInfo.setSpacing(false);
            menuInfo.getStyle().set("flex", "1");

            Span menuName = new Span(menus[i]);
            menuName.getStyle()
                    .set("font-weight", "600")
                    .set("color", "#333")
                    .set("font-size", "16px") // Diperbesar dari 15px
                    .set("margin-bottom", "10px"); // Diperbesar dari 8px

            // Enhanced progress bar - diperbesar
            Div progressContainer = new Div();
            progressContainer.getStyle()
                    .set("background-color", "#f5f5f5")
                    .set("border-radius", "10px")
                    .set("height", "10px") // Diperbesar dari 8px
                    .set("position", "relative")
                    .set("overflow", "hidden")
                    .set("box-shadow", "inset 0 2px 4px rgba(0,0,0,0.1)");

            Div progressBar = new Div();
            progressBar.getStyle()
                    .set("background", "linear-gradient(90deg, " + colors[i] + ", " + colors[i] + "dd)")
                    .set("height", "100%")
                    .set("width", "0%")
                    .set("border-radius", "10px")
                    .set("transition", "width 1.5s cubic-bezier(0.4, 0, 0.2, 1)")
                    .set("position", "relative")
                    .set("box-shadow", "0 0 10px " + colors[i] + "50");

            progressContainer.add(progressBar);
            menuInfo.add(menuName, progressContainer);

            // Enhanced percentage badge - diperbesar
            Div percentageBadge = new Div();
            percentageBadge.getStyle()
                    .set("background", "linear-gradient(135deg, " + colors[i] + ", " + colors[i] + "dd)")
                    .set("color", "white")
                    .set("padding", "10px 18px") // Diperbesar dari 8px 15px
                    .set("border-radius", "25px")
                    .set("font-weight", "bold")
                    .set("font-size", "14px") // Diperbesar dari 13px
                    .set("box-shadow", "0 4px 12px " + colors[i] + "40")
                    .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)");
            percentageBadge.setText(percentages[i] + "%");

            menuContent.add(iconContainer, menuInfo, percentageBadge);
            menuRow.add(menuContent);

            // Enhanced hover effects
            final int currentIndex = i;
            menuRow.getElement().addEventListener("mouseenter", e -> {
                menuRow.getStyle()
                        .set("transform", "translateY(-3px) scale(1.02)")
                        .set("box-shadow", "0 8px 25px rgba(0,0,0,0.15)");
            });

            menuRow.getElement().addEventListener("mouseleave", e -> {
                menuRow.getStyle()
                        .set("transform", "translateY(0px) scale(1)")
                        .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)");
            });

            chartContainer.add(menuRow);

            // Animate progress bars dengan delay
            final int delay = i * 300;
            progressBar.getElement().executeJs(
                    "setTimeout(() => { this.style.width = '" + percentages[i] + "%'; }, " + delay + ");"
            );
        }

        return chartContainer;
    }
}