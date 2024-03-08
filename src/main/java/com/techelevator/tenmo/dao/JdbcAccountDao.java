package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        Account account = null;
        String sql = "select account_id, user_id, balance from account where user_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public void updateBalance(Account account) {
    	String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
    	jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        return new Account(rs.getLong("account_id"), rs.getInt("user_id"), rs.getBigDecimal("balance"));
    }
}
