package com.example.application.views.kasir;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.RouterLink;

@PageTitle("Kasir Dashboard | Kopi.in")
@Route(value = "kasir", layout = MainLayout.class)
public class Dashboard extends VerticalLayout {

    public Dashboard() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%)");
        getStyle().set("min-height", "100vh");

        // Main container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setWidth("900px");
        mainContainer.setHeight("600px");
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setJustifyContentMode(JustifyContentMode.START);
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("border-radius", "20px");
        mainContainer.getStyle().set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);

        // Header section
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);           
        header.getStyle().set("background", "linear-gradient(135deg, #6B4423 0%, #8B593E 100%)");
        header.getStyle().set("padding", "1.5rem 2rem");
        header.getStyle().set("color", "white");
        header.getStyle().set("border-radius", "18px 18px 0 0");

        H2 title = new H2("Kasir Dashboard");
        title.getStyle().set("color", "#fff");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("margin", "0");
        title.getStyle().set("font-size", "2.2rem");
        title.addClassName(Margin.NONE);

        Image logo = new Image("images/logo_kopi-in.png", "Kopi.in Logo");
        logo.setHeight("48px");
        logo.getStyle().set("margin-left", "16px");

        header.add(title, logo);

        // Welcome message
        Paragraph welcome = new Paragraph("Selamat datang di sistem kasir Kopi.in! Pilih aksi di bawah untuk mulai.");
        welcome.getStyle().set("color", "#6B4423");
        welcome.getStyle().set("font-size", "1.1rem");
        welcome.getStyle().set("margin", "24px 0 8px 0");
        welcome.getStyle().set("text-align", "center");
        welcome.getStyle().set("font-weight", "500");

        // Quick actions section
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(JustifyContentMode.CENTER);
        actions.setAlignItems(Alignment.CENTER);
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "32px");

        RouterLink orderLink = new RouterLink("", OrderView.class);
        orderLink.add(createActionButton("New Order", VaadinIcon.PLUS));

        RouterLink productsLink = new RouterLink("", ProductView.class);
        productsLink.add(createActionButton("Products", VaadinIcon.COFFEE));

        RouterLink transactionsLink = new RouterLink("", TransactionView.class);
        transactionsLink.add(createActionButton("Transactions", VaadinIcon.MONEY));

        actions.add(orderLink, productsLink, transactionsLink);

        mainContainer.add(header, welcome, actions);
        add(mainContainer);
    }

    private Button createActionButton(String text, VaadinIcon icon) {
        Button button = new Button(text);
        button.setIcon(icon.create());
        button.setWidth("180px");
        button.setHeight("60px");
        button.getStyle()
              .set("background", "linear-gradient(135deg, #8B593E 0%, #D2691E 100%)")
              .set("color", "white")
              .set("border-radius", "12px")
              .set("font-size", "1.1rem")
              .set("font-weight", "600")
              .set("box-shadow", "0 4px 16px rgba(139, 69, 19, 0.08)")
              .set("cursor", "pointer")
              .set("transition", "all 0.3s ease");
        button.addClassName("large-button");
        
        // Hover effect
        button.getElement().executeJs(
                "this.addEventListener('mouseenter', () => {" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 10px 25px rgba(139, 69, 19, 0.18)';" +
                        "});" +
                        "this.addEventListener('mouseleave', () => {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = '0 4px 16px rgba(139, 69, 19, 0.08)';" +
                        "});"
        );
        return button;
    }
}
