package com.example.application.views.kasir;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@PageTitle("Kasir Dashboard - Kopi.in")
@Route(value = "kasir", layout = MainLayout.class)
public class Dashboard extends VerticalLayout {

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

        add(createHeader(), createStatsCards(), createChartsSection());
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

        H1 title = new H1("Kasir Dashboard");
        title.getStyle()
                .set("margin", "0 0 0 20px")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "white")
                .set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");

        logoSection.add(logoContainer, logoText, title);

        // Welcome section dengan waktu real-time
        VerticalLayout welcomeSection = new VerticalLayout();
        welcomeSection.setPadding(false);
        welcomeSection.setSpacing(false);
        welcomeSection.setAlignItems(FlexComponent.Alignment.END);
        welcomeSection.getStyle().set("position", "relative").set("z-index", "2");

        Span welcomeText = new Span("Selamat datang, Kasir!");
        welcomeText.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "500")
                .set("color", "#F5DEB3")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.3)");

        Span timeText = new Span(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeText.getStyle()
                .set("font-size", "14px")
                .set("color", "#D7CCC8")
                .set("margin-top", "2px");

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
                .set("padding", "0 15px");

        statsLayout.add(
                createEnhancedStatCard("Pesanan Hari Ini", "24", VaadinIcon.CLIPBOARD_CHECK, "#8B4513", "📋"),
                createEnhancedStatCard("Total Penjualan", "Rp 2.450.000", VaadinIcon.DOLLAR, "#A0522D", "💰"),
                createEnhancedStatCard("Menu Tersedia", "18", VaadinIcon.COFFEE, "#CD853F", "☕"),
                createEnhancedStatCard("Promo Aktif", "3", VaadinIcon.TICKET, "#D2691E", "🏷️")
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
        chartsLayout.getStyle().set("padding", "0 15px");

        // Quick Actions Section
        VerticalLayout quickActionsContainer = new VerticalLayout();
        quickActionsContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("flex", "2");

        H3 actionsTitle = new H3("Quick Actions");
        actionsTitle.getStyle()
                .set("margin", "0 0 25px 0")
                .set("color", "#4E342E")
                .set("font-size", "20px")
                .set("font-weight", "600");

        // Create action buttons with enhanced styling
        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);
        actionsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        actionsLayout.add(
            createActionButton("New Order", VaadinIcon.PLUS, OrderView.class, "#8B4513"),
            createActionButton("Products", VaadinIcon.COFFEE, ProductView.class, "#A0522D"),
            createActionButton("Transactions", VaadinIcon.MONEY, TransactionView.class, "#CD853F")
        );

        quickActionsContainer.add(actionsTitle, actionsLayout);

        // Popular Menu Section
        VerticalLayout menuChartContainer = new VerticalLayout();
        menuChartContainer.getStyle()
                .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                .set("border-radius", "20px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("border", "1px solid #f0f0f0")
                .set("flex", "1");

        H3 menuTitle = new H3("Menu Populer Hari Ini");
        menuTitle.getStyle()
                .set("margin", "0 0 25px 0")
                .set("color", "#4E342E")
                .set("font-size", "18px")
                .set("font-weight", "600");

        Component menuChart = createPopularMenuList();
        menuChartContainer.add(menuTitle, menuChart);

        chartsLayout.add(quickActionsContainer, menuChartContainer);
        return chartsLayout;
    }

    private RouterLink createActionButton(String text, VaadinIcon icon, Class<? extends Component> navigationTarget, String color) {
        RouterLink link = new RouterLink("", navigationTarget);
        
        // Button container
        Div buttonContainer = new Div();
        buttonContainer.getStyle()
                .set("background", "linear-gradient(135deg, " + color + ", " + color + "dd)")
                .set("border-radius", "15px")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("color", "white")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease")
                .set("box-shadow", "0 4px 15px " + color + "40");

        // Icon
        Icon buttonIcon = new Icon(icon);
        buttonIcon.setSize("32px");
        buttonIcon.getStyle()
                .set("margin-bottom", "10px")
                .set("filter", "drop-shadow(0 2px 4px rgba(0,0,0,0.2))");

        // Text
        Span buttonText = new Span(text);
        buttonText.getStyle()
                .set("display", "block")
                .set("font-size", "16px")
                .set("font-weight", "600")
                .set("text-shadow", "0 1px 2px rgba(0,0,0,0.2)");

        buttonContainer.add(buttonIcon, buttonText);

        // Hover effects
        buttonContainer.getElement().addEventListener("mouseenter", e -> {
            buttonContainer.getStyle()
                    .set("transform", "translateY(-5px)")
                    .set("box-shadow", "0 8px 25px " + color + "60");
        });

        buttonContainer.getElement().addEventListener("mouseleave", e -> {
            buttonContainer.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 4px 15px " + color + "40");
        });

        link.add(buttonContainer);
        return link;
    }

    private Component createPopularMenuList() {
        VerticalLayout menuList = new VerticalLayout();
        menuList.setPadding(false);
        menuList.setSpacing(false);

        String[] menus = {"Kopi Hitam", "Cappuccino", "Latte", "Americano", "Mocha"};
        int[] orders = {45, 38, 32, 28, 25};
        String[] colors = {"#8B4513", "#A0522D", "#CD853F", "#D2691E", "#DEB887"};
        String[] icons = {"☕", "🥛", "🍯", "💧", "🍫"};

        for (int i = 0; i < menus.length; i++) {
            Div menuItem = new Div();
            menuItem.getStyle()
                    .set("background", "linear-gradient(135deg, white 0%, #fefefe 100%)")
                    .set("border-radius", "12px")
                    .set("padding", "15px")
                    .set("margin-bottom", "10px")
                    .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                    .set("border-left", "4px solid " + colors[i])
                    .set("transition", "all 0.3s ease");

            HorizontalLayout content = new HorizontalLayout();
            content.setWidthFull();
            content.setAlignItems(FlexComponent.Alignment.CENTER);

            // Icon container
            Div iconContainer = new Div();
            iconContainer.getStyle()
                    .set("background", "linear-gradient(135deg, " + colors[i] + "20, " + colors[i] + "10)")
                    .set("padding", "8px")
                    .set("border-radius", "10px")
                    .set("margin-right", "12px");

            Span icon = new Span(icons[i]);
            icon.getStyle().set("font-size", "20px");
            iconContainer.add(icon);

            // Menu info
            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);

            Span name = new Span(menus[i]);
            name.getStyle()
                    .set("font-weight", "600")
                    .set("color", colors[i]);

            Span orderCount = new Span(orders[i] + " orders");
            orderCount.getStyle()
                    .set("font-size", "12px")
                    .set("color", "#666");

            info.add(name, orderCount);
            content.add(iconContainer, info);
            menuItem.add(content);

            // Hover effect
            menuItem.getElement().addEventListener("mouseenter", e -> {
                menuItem.getStyle()
                        .set("transform", "translateX(5px)")
                        .set("box-shadow", "0 6px 15px rgba(0,0,0,0.12)");
            });

            menuItem.getElement().addEventListener("mouseleave", e -> {
                menuItem.getStyle()
                        .set("transform", "translateX(0)")
                        .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)");
            });

            menuList.add(menuItem);
        }

        return menuList;
    }
}
