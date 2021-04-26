package easyJava.entity;

import org.web3j.protocol.core.methods.response.Transaction;

public class TransactionMy extends Transaction {
    private String time;
    private String transferType;

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
