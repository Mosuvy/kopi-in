package com.example.application.views.admin;

import com.example.application.dao.AdminDAO;
import com.example.application.models.Feedback;
import com.example.application.models.Users;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@PageTitle("Dashboard Admin - Kopi.in")
@Route(value = "admin/dashboard", layout = MainLayout.class)
public class Dashboard extends VerticalLayout {

    private final AdminDAO adminDAO;
    private final NumberFormat currencyFormat;
    private List<Feedback> feedbackList = new ArrayList<>();

    // Stats variables for real-time updates
    private Span todaySalesValue;
    private Span activeOrdersValue;
    private Span availableMenuValue;
    private Span onlineCustomersValue;

    public Dashboard(@Autowired AdminDAO adminDAO) {
        this.adminDAO = adminDAO;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);

        // Initialize currency formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Background gradient
        getElement().getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("min-height", "100vh")
                .set("padding", "0");

        // Load data and build UI
        loadDashboardData();
        add(createHeader(), createStatsCards(), createChartsSection(), createFeedbackSection());
    }

    private void loadDashboardData() {
        if (adminDAO == null) {
            Notification.show("AdminDAO is not available", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // Load feedback data
            feedbackList = adminDAO.getAllFeedback();
        } catch (Exception e) {
            Notification.show("Error loading dashboard data: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            feedbackList = new ArrayList<>();
        }
    }

    // ... (rest of your methods remain the same)

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

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.getStyle().set("position", "relative").set("z-index", "2");

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

        Div logoText = new Div();
        logoText.getStyle().set("display", "flex").set("align-items", "center");

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

        // Welcome section
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

        // Create stats cards with real data
        Component todaySalesCard = createEnhancedStatCard("Total Penjualan Hari Ini",
                getTodaySalesFormatted(), VaadinIcon.DOLLAR, "#8B4513", "📈");
        Component activeOrdersCard = createEnhancedStatCard("Pesanan Aktif",
                String.valueOf(getActiveOrdersCount()), VaadinIcon.CLIPBOARD_CHECK, "#A0522D", "🔥");
        Component availableMenuCard = createEnhancedStatCard("Menu Tersedia",
                String.valueOf(getAvailableMenuCount()), VaadinIcon.MENU, "#CD853F", "☕");
        Component onlineCustomersCard = createEnhancedStatCard("Customer Online",
                String.valueOf(getOnlineCustomersCount()), VaadinIcon.USERS, "#D2691E", "👥");

        statsLayout.add(todaySalesCard, activeOrdersCard, availableMenuCard, onlineCustomersCard);

        // Auto refresh stats every 30 seconds
        UI.getCurrent().setPollInterval(30000);
        UI.getCurrent().addPollListener(e -> refreshStats());

        return statsLayout;
    }

    private String getTodaySalesFormatted() {
        if (adminDAO == null) return "Rp 0";
        try {
            double todaySales = adminDAO.getTodayTotalSales();
            return currencyFormat.format(todaySales);
        } catch (Exception e) {
            return "Error";
        }
    }

    private int getActiveOrdersCount() {
        if (adminDAO == null) return 0;
        try {
            return adminDAO.getActiveOrdersCount();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getAvailableMenuCount() {
        if (adminDAO == null) return 0;
        try {
            return adminDAO.getAvailableMenuCount();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getOnlineCustomersCount() {
        if (adminDAO == null) return 0;
        try {
            return adminDAO.getOnlineCustomersCount();
        } catch (Exception e) {
            return 0;
        }
    }

    private void refreshStats() {
        if (todaySalesValue != null) {
            todaySalesValue.setText(getTodaySalesFormatted());
        }
        if (activeOrdersValue != null) {
            activeOrdersValue.setText(String.valueOf(getActiveOrdersCount()));
        }
        if (availableMenuValue != null) {
            availableMenuValue.setText(String.valueOf(getAvailableMenuCount()));
        }
        if (onlineCustomersValue != null) {
            onlineCustomersValue.setText(String.valueOf(getOnlineCustomersCount()));
        }
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

        // Store reference for updates
        if (title.contains("Penjualan")) {
            todaySalesValue = new Span(value);
            todaySalesValue.getStyle()
                    .set("font-size", "28px")
                    .set("font-weight", "700")
                    .set("color", color);
            valueH2.removeAll();
            valueH2.add(todaySalesValue);
        } else if (title.contains("Pesanan")) {
            activeOrdersValue = new Span(value);
            activeOrdersValue.getStyle()
                    .set("font-size", "28px")
                    .set("font-weight", "700")
                    .set("color", color);
            valueH2.removeAll();
            valueH2.add(activeOrdersValue);
        } else if (title.contains("Menu")) {
            availableMenuValue = new Span(value);
            availableMenuValue.getStyle()
                    .set("font-size", "28px")
                    .set("font-weight", "700")
                    .set("color", color);
            valueH2.removeAll();
            valueH2.add(availableMenuValue);
        } else if (title.contains("Customer")) {
            onlineCustomersValue = new Span(value);
            onlineCustomersValue.getStyle()
                    .set("font-size", "28px")
                    .set("font-weight", "700")
                    .set("color", color);
            valueH2.removeAll();
            valueH2.add(onlineCustomersValue);
        }

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

        // Sales Chart with real data
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
                .set("min-height", "400px")
                .set("max-width", "100%");

        // Header section - removed trend indicator
        H3 salesTitle = new H3("Penjualan 7 Hari Terakhir");
        salesTitle.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        Component salesChart = createRealSalesChart();
        salesChartContainer.add(salesTitle, salesChart);
        salesChartContainer.setPadding(false);

        // Popular menu with real data
        VerticalLayout menuChartContainer = new VerticalLayout();
        menuChartContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("flex", "1")
                .set("min-height", "400px")
                .set("min-width", "300px");

        H3 menuTitle = new H3("Menu Populer Hari Ini");
        menuTitle.getStyle()
                .set("margin", "0 0 25px 0")
                .set("color", "#4E342E")
                .set("font-size", "18px")
                .set("font-weight", "600");

        Component menuChart = createRealMenuChart();
        menuChartContainer.add(menuTitle, menuChart);
        menuChartContainer.setPadding(false);

        chartsLayout.add(salesChartContainer, menuChartContainer);
        return chartsLayout;
    }

    private Component createRealSalesChart() {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        // Get real sales data
        List<AdminDAO.DailySales> salesData = getRealSalesData();

        // Stats summary - removed trend percentage
        HorizontalLayout statsRow = createRealChartStats(salesData);

        // Custom bar chart with real data
        Component customChart = createCustomSalesChartWithData(salesData);

        chartContainer.add(statsRow, customChart);
        return chartContainer;
    }

    private List<AdminDAO.DailySales> getRealSalesData() {
        if (adminDAO == null) return new ArrayList<>();
        try {
            return adminDAO.getWeeklySalesData();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private HorizontalLayout createRealChartStats(List<AdminDAO.DailySales> salesData) {
        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.setWidthFull();
        statsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        statsRow.getStyle()
                .set("margin-bottom", "20px")
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("border-radius", "12px")
                .set("padding", "15px")
                .set("flex-wrap", "wrap")
                .set("gap", "10px");

        // Calculate stats from real data - removed trend percentage
        double average = salesData.stream().mapToDouble(AdminDAO.DailySales::getTotal).average().orElse(0.0);
        double highest = salesData.stream().mapToDouble(AdminDAO.DailySales::getTotal).max().orElse(0.0);

        statsRow.add(
                createMiniStat("Rata-rata", currencyFormat.format(average), "#8B4513", "📊"),
                createMiniStat("Tertinggi", currencyFormat.format(highest), "#CD853F", "🚀")
        );

        return statsRow;
    }

    private Component createCustomSalesChartWithData(List<AdminDAO.DailySales> salesData) {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        if (salesData.isEmpty()) {
            Div noDataDiv = new Div();
            noDataDiv.add(new Span("Tidak ada data penjualan"));
            noDataDiv.getStyle()
                    .set("text-align", "center")
                    .set("padding", "50px")
                    .set("color", "#666");
            return noDataDiv;
        }

        // Find max sales for scaling
        double maxSales = salesData.stream().mapToDouble(AdminDAO.DailySales::getTotal).max().orElse(1.0);

        // Chart area
        Div chartArea = new Div();
        chartArea.getStyle()
                .set("background", "linear-gradient(135deg, #f8f6f0 0%, #f0ede3 100%)")
                .set("border-radius", "15px")
                .set("padding", "20px")
                .set("position", "relative")
                .set("width", "95%")
                .set("height", "300px")
                .set("box-shadow", "inset 0 2px 8px rgba(0,0,0,0.05)")
                .set("overflow", "hidden");

        // Grid lines background
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("position", "absolute")
                .set("top", "20px")
                .set("left", "20px")
                .set("right", "20px")
                .set("bottom", "20px")
                .set("background-image", "linear-gradient(to top, rgba(139,69,19,0.1) 1px, transparent 1px)")
                .set("background-size", "100% 35px")
                .set("border-radius", "8px");
        chartArea.add(gridContainer);

        // Bars container
        HorizontalLayout barsContainer = new HorizontalLayout();
        barsContainer.setWidthFull();
        barsContainer.setAlignItems(FlexComponent.Alignment.END);
        barsContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        barsContainer.getStyle()
                .set("height", "240px")
                .set("padding", "15px")
                .set("position", "relative")
                .set("z-index", "2")
                .set("gap", "8px");

        // Days of week in Indonesian
        String[] dayNames = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

        for (int i = 0; i < Math.min(salesData.size(), 7); i++) {
            AdminDAO.DailySales dailySale = salesData.get(i);
            String dayName = i < dayNames.length ? dayNames[i] : "Day " + (i + 1);

            VerticalLayout barColumn = createSalesBarColumn(dailySale, dayName, maxSales, i);
            barsContainer.add(barColumn);
        }

        chartArea.add(barsContainer);
        chartContainer.add(chartArea);
        return chartContainer;
    }

    private VerticalLayout createSalesBarColumn(AdminDAO.DailySales dailySale, String dayName, double maxSales, int index) {
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
                .set("justify-content", "flex-end")
                .set("min-width", "0")
                .set("max-width", "none");

        // Format value for display
        String formattedValue = currencyFormat.format(dailySale.getTotal());

        // Value label
        Span valueLabel = new Span(formattedValue);
        valueLabel.getStyle()
                .set("font-size", "10px")
                .set("font-weight", "600")
                .set("color", "#8B4513")
                .set("background", "white")
                .set("padding", "3px 6px")
                .set("border-radius", "6px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)")
                .set("margin-bottom", "6px")
                .set("opacity", "0")
                .set("transform", "translateY(-10px)")
                .set("transition", "all 0.3s ease")
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "50%")
                .set("transform", "translateX(-50%) translateY(-10px)")
                .set("z-index", "10")
                .set("white-space", "nowrap");

        // Bar area container
        Div barAreaContainer = new Div();
        double barHeight = maxSales > 0 ? (dailySale.getTotal() / maxSales) * 160 : 0;
        barAreaContainer.getStyle()
                .set("width", "calc(100% - 8px)")
                .set("min-width", "25px")
                .set("max-width", "40px")
                .set("height", barHeight + "px")
                .set("position", "relative")
                .set("display", "flex")
                .set("align-items", "flex-end")
                .set("margin", "0 auto 8px auto");

        // Actual bar
        Div barContainer = new Div();
        barContainer.getStyle()
                .set("width", "100%")
                .set("height", "0%")
                .set("background", "linear-gradient(to top, #8B4513 0%, #CD853F 50%, #DEB887 100%)")
                .set("border-radius", "6px 6px 3px 3px")
                .set("position", "relative")
                .set("transition", "height 1.2s cubic-bezier(0.4, 0, 0.2, 1)")
                .set("box-shadow", "0 3px 12px rgba(139, 69, 19, 0.3)")
                .set("overflow", "hidden")
                .set("transform-origin", "bottom");

        // Shine effect
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

        // Day label
        Span dayLabel = new Span(dayName);
        dayLabel.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("color", "#8B4513")
                .set("background", "white")
                .set("padding", "4px 6px")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("text-align", "center")
                .set("white-space", "nowrap");

        barColumn.add(valueLabel, barAreaContainer, dayLabel);

        // Hover effects
        barColumn.getElement().addEventListener("mouseenter", e -> {
            barAreaContainer.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("transition", "transform 0.3s ease");
            barContainer.getStyle()
                    .set("box-shadow", "0 6px 20px rgba(139, 69, 19, 0.4)");
            valueLabel.getStyle()
                    .set("opacity", "1")
                    .set("transform", "translateX(-50%) translateY(0px)");
        });

        barColumn.getElement().addEventListener("mouseleave", e -> {
            barAreaContainer.getStyle()
                    .set("transform", "translateY(0px)");
            barContainer.getStyle()
                    .set("box-shadow", "0 3px 12px rgba(139, 69, 19, 0.3)");
            valueLabel.getStyle()
                    .set("opacity", "0")
                    .set("transform", "translateX(-50%) translateY(-10px)");
        });

        // Animate bar
        barContainer.getElement().executeJs(
                "setTimeout(() => { " +
                        "this.style.height = '100%'; " +
                        "}, " + (index * 200 + 500) + ");"
        );

        return barColumn;
    }

    private Component createRealMenuChart() {
        VerticalLayout chartContainer = new VerticalLayout();
        chartContainer.setPadding(false);
        chartContainer.setSpacing(false);
        chartContainer.setSizeFull();

        // Get real popular menu data
        List<AdminDAO.PopularMenuItem> popularMenus = getRealPopularMenuData();

        if (popularMenus.isEmpty()) {
            Div noDataDiv = new Div();
            noDataDiv.add(new Span("Tidak ada data menu populer"));
            noDataDiv.getStyle()
                    .set("text-align", "center")
                    .set("padding", "50px")
                    .set("color", "#666");
            return noDataDiv;
        }

        // Colors and icons for menu items
        String[] colors = {"#8B4513", "#A0522D", "#CD853F", "#D2691E", "#DEB887"};
        String[] icons = {"☕", "🥛", "🍯", "💧", "🍫"};

        for (int i = 0; i < Math.min(popularMenus.size(), 5); i++) {
            AdminDAO.PopularMenuItem menuItem = popularMenus.get(i);
            String color = i < colors.length ? colors[i] : colors[0];
            String icon = i < icons.length ? icons[i] : "☕";

            Div menuRow = createMenuRow(menuItem, color, icon, i);
            chartContainer.add(menuRow);
        }

        return chartContainer;
    }

    private List<AdminDAO.PopularMenuItem> getRealPopularMenuData() {
        if (adminDAO == null) return new ArrayList<>();
        try {
            return adminDAO.getTodayPopularMenu();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Div createMenuRow(AdminDAO.PopularMenuItem menuItem, String color, String icon, int index) {
        Div menuRow = new Div();
        menuRow.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "12px")
                .set("padding", "15px")
                .set("margin-bottom", "12px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                .set("border-left", "4px solid " + color)
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("position", "relative")
                .set("overflow", "hidden");

        HorizontalLayout menuContent = new HorizontalLayout();
        menuContent.setWidthFull();
        menuContent.setAlignItems(FlexComponent.Alignment.CENTER);
        menuContent.getStyle().set("gap", "12px");

        // Icon container
        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("background", "linear-gradient(135deg, " + color + ", " + color + "cc)")
                .set("border-radius", "10px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "18px")
                .set("flex-shrink", "0")
                .set("box-shadow", "0 3px 10px " + color + "40");

        Span iconSpan = new Span(icon);
        iconContainer.add(iconSpan);

        // Menu info
        VerticalLayout menuInfo = new VerticalLayout();
        menuInfo.setPadding(false);
        menuInfo.setSpacing(false);
        menuInfo.getStyle()
                .set("flex", "1")
                .set("min-width", "0");

        Span menuName = new Span(menuItem.getName());
        menuName.getStyle()
                .set("font-weight", "600")
                .set("color", "#333")
                .set("font-size", "14px")
                .set("margin-bottom", "8px")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis");

        // Progress bar
        Div progressContainer = new Div();
        progressContainer.getStyle()
                .set("background-color", "#f5f5f5")
                .set("border-radius", "8px")
                .set("height", "8px")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("box-shadow", "inset 0 2px 4px rgba(0,0,0,0.1)")
                .set("width", "100%");

        Div progressBar = new Div();
        progressBar.getStyle()
                .set("background", "linear-gradient(90deg, " + color + ", " + color + "dd)")
                .set("height", "100%")
                .set("width", "0%")
                .set("border-radius", "8px")
                .set("transition", "width 1.5s cubic-bezier(0.4, 0, 0.2, 1)")
                .set("position", "relative")
                .set("box-shadow", "0 0 8px " + color + "50");

        progressContainer.add(progressBar);
        menuInfo.add(menuName, progressContainer);

        // Percentage badge
        Div percentageBadge = new Div();
        percentageBadge.getStyle()
                .set("background", "linear-gradient(135deg, " + color + ", " + color + "dd)")
                .set("color", "white")
                .set("padding", "8px 12px")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("font-size", "12px")
                .set("box-shadow", "0 3px 10px " + color + "40")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)")
                .set("flex-shrink", "0")
                .set("white-space", "nowrap");
        percentageBadge.setText(menuItem.getPercentage() + "%");

        menuContent.add(iconContainer, menuInfo, percentageBadge);
        menuRow.add(menuContent);

        // Hover effects
        menuRow.getElement().addEventListener("mouseenter", e -> {
            menuRow.getStyle()
                    .set("transform", "translateY(-2px) scale(1.01)")
                    .set("box-shadow", "0 6px 20px rgba(0,0,0,0.12)");
        });

        menuRow.getElement().addEventListener("mouseleave", e -> {
            menuRow.getStyle()
                    .set("transform", "translateY(0px) scale(1)")
                    .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)");
        });

        // Animate progress bar
        final int delay = index * 300;
        progressBar.getElement().executeJs(
                "setTimeout(() => { this.style.width = '" + menuItem.getPercentage() + "%'; }, " + delay + ");"
        );

        return menuRow;
    }

    private Div createMiniStat(String label, String value, String color, String emoji) {
        Div container = new Div();
        container.getStyle()
                .set("background-color", "white")
                .set("border-radius", "10px")
                .set("padding", "12px")
                .set("text-align", "center")
                .set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)")
                .set("border-top", "3px solid " + color)
                .set("min-width", "80px")
                .set("flex", "1")
                .set("margin", "0")
                .set("position", "relative");

        Span emojiSpan = new Span(emoji);
        emojiSpan.getStyle()
                .set("font-size", "14px")
                .set("display", "block")
                .set("margin-bottom", "4px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "11px")
                .set("color", "#666")
                .set("display", "block")
                .set("font-weight", "500");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "bold")
                .set("color", color)
                .set("display", "block")
                .set("margin-top", "4px");

        container.add(emojiSpan, labelSpan, valueSpan);
        return container;
    }

    private Component createFeedbackSection() {
        VerticalLayout feedbackSection = new VerticalLayout();
        feedbackSection.setWidthFull();
        feedbackSection.getStyle()
                .set("padding", "0 15px 25px 15px");

        Div feedbackCard = new Div();
        feedbackCard.getStyle()
                .set("background", "linear-gradient(135deg, #4E342E 0%, #795548 50%, #8B4513 100%)")
                .set("color", "white")
                .set("border-radius", "15px")
                .set("padding", "30px")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.15)")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Background pattern
        Div pattern = new Div();
        pattern.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-image", "radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 1px, transparent 1px)")
                .set("background-size", "30px 30px")
                .set("opacity", "0.3");
        feedbackCard.add(pattern);

        // Main content layout
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.getStyle()
                .set("position", "relative")
                .set("z-index", "2")
                .set("flex-wrap", "wrap")
                .set("gap", "20px");

        // Left section
        VerticalLayout leftSection = new VerticalLayout();
        leftSection.setPadding(false);
        leftSection.setSpacing(false);
        leftSection.getStyle().set("flex", "1").set("min-width", "300px");

        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);
        titleSection.getStyle().set("margin-bottom", "10px");

        Icon feedbackIcon = new Icon(VaadinIcon.COMMENT);
        feedbackIcon.setSize("24px");
        feedbackIcon.getStyle().set("color", "#FFD700");

        H3 feedbackTitle = new H3("Customer Feedback");
        feedbackTitle.getStyle()
                .set("margin", "0")
                .set("color", "white")
                .set("font-size", "22px")
                .set("font-weight", "600");

        titleSection.add(feedbackIcon, feedbackTitle);

        Span description = new Span("Lihat dan kelola semua feedback dari customer untuk meningkatkan kualitas layanan kopi.in");
        description.getStyle()
                .set("color", "#F5DEB3")
                .set("font-size", "14px")
                .set("line-height", "1.5");

        leftSection.add(titleSection, description);

        // Right section
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.getStyle()
                .set("flex-wrap", "wrap")
                .set("gap", "20px");

        // Stats container with real feedback data
        long unreadCount = feedbackList.stream().filter(f -> "unread".equals(f.getStatus())).count();

        VerticalLayout statsContainer = new VerticalLayout();
        statsContainer.setPadding(false);
        statsContainer.setSpacing(false);
        statsContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        statsContainer.getStyle()
                .set("background", "rgba(255, 255, 255, 0.1)")
                .set("border-radius", "12px")
                .set("padding", "15px")
                .set("min-width", "120px");

        Span totalText = new Span(String.valueOf(feedbackList.size()));
        totalText.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "bold")
                .set("color", "#FFD700")
                .set("line-height", "1");

        Span totalLabel = new Span("Total Feedback");
        totalLabel.getStyle()
                .set("font-size", "12px")
                .set("color", "#F5DEB3")
                .set("text-align", "center")
                .set("margin-top", "4px");

        if (unreadCount > 0) {
            HorizontalLayout unreadLayout = new HorizontalLayout();
            unreadLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            unreadLayout.setSpacing(false);
            unreadLayout.getStyle()
                    .set("margin-top", "8px")
                    .set("justify-content", "center");

            Div unreadBadge = new Div();
            unreadBadge.getStyle()
                    .set("background", "linear-gradient(135deg, #FF6B35, #FF8C42)")
                    .set("color", "white")
                    .set("padding", "4px 8px")
                    .set("border-radius", "12px")
                    .set("font-size", "11px")
                    .set("font-weight", "600")
                    .set("box-shadow", "0 2px 8px rgba(255, 107, 53, 0.3)")
                    .set("text-align", "center")
                    .set("min-width", "60px");

            unreadBadge.setText(unreadCount + " belum dibaca");
            unreadLayout.add(unreadBadge);

            statsContainer.add(totalText, totalLabel, unreadLayout);
        } else {
            statsContainer.add(totalText, totalLabel);
        }

        // View feedback button
        Button viewFeedbackBtn = new Button("Lihat Feedback", new Icon(VaadinIcon.EYE));
        viewFeedbackBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewFeedbackBtn.getStyle()
                .set("background", "linear-gradient(135deg, #D7A449, #FFD700)")
                .set("color", "#4E342E")
                .set("border", "none")
                .set("border-radius", "12px")
                .set("padding", "12px 20px")
                .set("font-weight", "600")
                .set("font-size", "14px")
                .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("min-width", "150px");

        viewFeedbackBtn.addClickListener(e -> openFeedbackModal());

        // Hover effects
        viewFeedbackBtn.getElement().addEventListener("mouseenter", e -> {
            viewFeedbackBtn.getStyle()
                    .set("transform", "translateY(-2px)")
                    .set("box-shadow", "0 6px 20px rgba(215, 164, 73, 0.5)");
        });

        viewFeedbackBtn.getElement().addEventListener("mouseleave", e -> {
            viewFeedbackBtn.getStyle()
                    .set("transform", "translateY(0px)")
                    .set("box-shadow", "0 4px 15px rgba(215, 164, 73, 0.4)");
        });

        rightSection.add(statsContainer, viewFeedbackBtn);
        mainContent.add(leftSection, rightSection);
        feedbackCard.add(mainContent);
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
                    if (adminDAO != null && adminDAO.updateFeedbackStatus(feedback.getId(), "read")) {
                        feedback.setStatus("read");
                        grid.getDataProvider().refreshItem(feedback);
                        Notification.show("Feedback marked as read", 2000, Notification.Position.BOTTOM_START)
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    } else {
                        Notification.show("Failed to update feedback status", 3000, Notification.Position.BOTTOM_START)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
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
            List<Feedback> filtered;
            if (adminDAO != null) {
                try {
                    if (searchTerm.trim().isEmpty()) {
                        filtered = adminDAO.getAllFeedback();
                    } else {
                        filtered = adminDAO.searchFeedback(searchTerm);
                    }
                    grid.setItems(filtered);
                } catch (Exception ex) {
                    Notification.show("Error searching feedback: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

        contentArea.add(searchField, grid);
        contentArea.setPadding(false);

        dialogContent.add(header, contentArea);
        dialog.add(dialogContent);
        dialog.open();
    }
}