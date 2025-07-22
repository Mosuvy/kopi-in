package com.example.application.models;

import java.sql.Timestamp;

public class Transactions {
    private String id;
    private String order_id;
    private String payment_method;
    private Double paid_amount;
    private Timestamp paid_at;
    private Double change_returned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Double getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(Double paid_amount) {
        this.paid_amount = paid_amount;
    }

    public Timestamp getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(Timestamp paid_at) {
        this.paid_at = paid_at;
    }

    public Double getChange_returned() {
        return change_returned;
    }

    public void setChange_returned(Double change_returned) {
        this.change_returned = change_returned;
    }
}
