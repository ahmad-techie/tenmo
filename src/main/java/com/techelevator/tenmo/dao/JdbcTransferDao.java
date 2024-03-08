package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private Logger log = LoggerFactory.getLogger(getClass());
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private UserDao userDao;

    private static final String SQL_SELECT_TRANSFER = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.amount, " +
            "aFrom.account_id as fromAcct, aFrom.user_id as fromUser, aFrom.balance as fromBal, " +
            "aTo.account_id as toAcct, aTo.user_id as toUser, aTo.balance as toBal " +
            "FROM transfer t " +
            "INNER JOIN transfer_type tt ON t.transfer_type_id = tt.transfer_type_id "+
            "INNER JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id "+
            "INNER JOIN account aFrom on account_from = aFrom.account_id " +
            "INNER JOIN account aTo on account_to = aTo.account_id ";

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @Override
    public List<Transfer> findAll() {
        List<Transfer> transfers = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(SQL_SELECT_TRANSFER);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public Transfer getTransferById(Long transferId) {
        Transfer transfer = null;
        String sql = SQL_SELECT_TRANSFER + "where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,transferId);
        if(results.next() ) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer addTransfer(Transfer newTransfer) {
        String sql = "INSERT INTO transfer (transfer_id, transfer_type_id,transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?, ?)";

        Long newTransferId = getNextTransferId();
        Long transferTypeId = getTransferTypeId(newTransfer.getTransferType());
        Long transferStatusId = getTransferStatusId(newTransfer.getTransferStatus());
        Account fromAccount = accountDao.getAccountByUserId(newTransfer.getUserFrom().getId());
        Account toAccount = accountDao.getAccountByUserId(newTransfer.getUserTo().getId());
        
        jdbcTemplate.update(sql, newTransferId, transferTypeId, transferStatusId, fromAccount.getAccountId(), toAccount.getAccountId(), newTransfer.getAmount());
        log.debug("created new Transfer with ID: "+newTransferId);
        
        return getTransferById(newTransferId);
    }

    @Override
    public List<Transfer> getTransfersForUser(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = SQL_SELECT_TRANSFER + "" +
                "where (account_from IN (SELECT account_id FROM account WHERE user_id = ?) " +
                "OR account_to IN (SELECT account_id FROM account WHERE user_id = ?))";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfersForUser(int userId) {
        String sql = SQL_SELECT_TRANSFER + "WHERE transfer_status_id = 1 AND account_from IN (SELECT account_id FROM account WHERE user_id = ?)";
        List<Transfer> transfers = new ArrayList<>();

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }
        return transfers;
    }
    
	@Override
	public void updateStatus(Transfer transfer) {
		String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
		Long transferStatusId = getTransferStatusId(transfer.getTransferStatus());
		jdbcTemplate.update(sql, transferStatusId, transfer.getTransferId());
	}

    private long getNextTransferId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id')");
		if(nextIdResult.next()) {
			return nextIdResult.getLong(1);
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new transfer");
		}
	}
    
    private Long getTransferTypeId(String transferType) {
    	String sql = "SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = ?";
    	SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferType);
    	if(results.next()) {
    		return results.getLong(1);
    	} else {
    		throw new RuntimeException("Unable to lookup transferType "+transferType);
    	}
    }
    
    private Long getTransferStatusId(String transferStatus) {
    	String sql = "SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?";
    	SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatus);
    	if(results.next()) {
    		return results.getLong(1);
    	} else {
    		throw new RuntimeException("Unable to lookup transferStatus "+transferStatus);
    	}
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        return new Transfer(rs.getLong("transfer_id"),
						    rs.getString("transfer_type_desc"),
						    rs.getString("transfer_status_desc"),
						    userDao.getUserById(rs.getInt("fromUser")),
						    userDao.getUserById(rs.getInt("toUser")),
						    rs.getBigDecimal("amount"));
    }
}
