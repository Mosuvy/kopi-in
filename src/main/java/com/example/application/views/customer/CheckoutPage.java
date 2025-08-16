package com.example.application.views.customer;

import com.example.application.dao.PromoDAO;
import com.example.application.dao.UserDAO;
import com.example.application.models.CartItem;
import com.example.application.models.Promo;
import com.example.application.models.Users;

import com.example.application.views.AppLayoutNavbar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Checkout - kopi-In")
@Route(value = "checkout", layout = AppLayoutNavbar.class)
public class CheckoutPage extends VerticalLayout {

    public CheckoutPage() {

    }
}