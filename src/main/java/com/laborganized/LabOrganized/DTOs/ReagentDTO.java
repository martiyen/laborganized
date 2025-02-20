package com.laborganized.LabOrganized.DTOs;

import com.laborganized.LabOrganized.models.Reagent;

import java.time.LocalDate;

public class ReagentDTO extends StoreableDTO {
    private String supplier;
    private String reference;
    private Double quantity;
    private String unit;
    private String concentration;
    private LocalDate expirationDate;
    private String comments;

    public ReagentDTO(Reagent reagent) {
        super(reagent);
        this.supplier = reagent.getSupplier();
        this.reference = reagent.getReference();
        this.quantity = reagent.getQuantity();
        this.unit = reagent.getUnit();
        this.concentration = reagent.getConcentration();
        this.expirationDate = reagent.getExpirationDate();
        this.comments = reagent.getComments();
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
