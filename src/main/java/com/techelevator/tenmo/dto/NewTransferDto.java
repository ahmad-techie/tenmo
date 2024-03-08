package com.techelevator.tenmo.dto;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

public class NewTransferDto {

    private int userTo;
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal amount;
    @NotEmpty
    private String transferType;

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @AssertTrue
    public boolean isValidTransferType() {
        return Transfer.TRANSFER_TYPE_REQUEST.equals(this.transferType) || Transfer.TRANSFER_TYPE_SEND.equals(this.transferType);
    }

}
