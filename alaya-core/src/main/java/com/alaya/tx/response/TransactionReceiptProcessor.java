package com.alaya.tx.response;

import java.io.IOException;

import com.alaya.protocol.Web3j;
import com.alaya.protocol.core.methods.response.PlatonGetTransactionReceipt;
import com.alaya.protocol.core.methods.response.TransactionReceipt;
import com.alaya.protocol.exceptions.TransactionException;

/**
 * Abstraction for managing how we wait for transaction receipts to be generated on the network.
 */
public abstract class TransactionReceiptProcessor {

    private final Web3j web3j;

    public TransactionReceiptProcessor(Web3j web3j) {
        this.web3j = web3j;
    }

    public abstract TransactionReceipt waitForTransactionReceipt(
            String transactionHash)
            throws IOException, TransactionException;

    TransactionReceipt sendTransactionReceiptRequest(
            String transactionHash) throws IOException, TransactionException {
        PlatonGetTransactionReceipt transactionReceipt =
                web3j.platonGetTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            throw new TransactionException("Error processing request: "
                    + transactionReceipt.getError().getMessage());
        }

        return transactionReceipt.getTransactionReceipt();
    }
}
