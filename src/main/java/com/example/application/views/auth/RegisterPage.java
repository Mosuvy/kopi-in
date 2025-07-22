package com.example.application.views.auth;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterPage extends VerticalLayout {
    public RegisterPage() {
        add(new H1("Halaman Register"));
    }
}
