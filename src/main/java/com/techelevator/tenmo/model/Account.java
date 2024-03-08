package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.web.server.ResponseStatusException;

@Data
@AllArgsConstructor
public class Account {

    private Long accountId;
    private int userId;
    private BigDecimal balance;

    public void transfer(Account accountTo, BigDecimal amountToTransfer) {
    	if(this.balance.compareTo(amountToTransfer) >= 0) {
    		this.balance = this.balance.subtract(amountToTransfer);
    		accountTo.balance = accountTo.balance.add(amountToTransfer);
    	} else {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, amountToTransfer+" exceeds the remaining balance of "+this.balance);
    	}
    }

    //<editor-fold desc="Equals, HashCode, and ToString Methods">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                userId == account.userId &&
                balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
    //</editor-fold>
}
