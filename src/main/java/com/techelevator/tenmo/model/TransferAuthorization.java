package com.techelevator.tenmo.model;

import java.security.Principal;

public class TransferAuthorization {
	
	private Principal principal;
	private Transfer transfer;

	public TransferAuthorization(Principal principal, Transfer transfer) {
		this.principal = principal;
		this.transfer = transfer;
	}
	
	public boolean isAllowedToView() {
		return principalUsername().equals(senderUsername()) ||
				principalUsername().equals(receiverUsername());
	}
	
	public boolean isAllowedToCreate() {
		boolean isAllowed = false;
		if(transfer.isRequestType()) {
			isAllowed = principalUsername().equals(receiverUsername());
		} else if(transfer.isSendType()) {
			isAllowed = principalUsername().equals(senderUsername());
		}
		return isAllowed;
	}
	
	public boolean isAllowedToApproveOrReject() {
		return principalUsername().equals(senderUsername());
	}

	private String receiverUsername() {
		return transfer.getUserTo().getUsername();
	}

	private String senderUsername() {
		return transfer.getUserFrom().getUsername();
	}

	private String principalUsername() {
		return principal.getName();
	}
}
