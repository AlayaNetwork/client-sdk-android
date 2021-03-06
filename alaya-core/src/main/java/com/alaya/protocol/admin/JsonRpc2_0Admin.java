package com.alaya.protocol.admin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alaya.protocol.admin.methods.response.NewAccountIdentifier;
import com.alaya.protocol.admin.methods.response.PersonalListAccounts;
import com.alaya.protocol.admin.methods.response.PersonalUnlockAccount;
import com.alaya.protocol.Web3jService;
import com.alaya.protocol.core.JsonRpc2_0Web3j;
import com.alaya.protocol.core.Request;
import com.alaya.protocol.core.methods.request.Transaction;
import com.alaya.protocol.core.methods.response.PlatonSendTransaction;

/**
 * JSON-RPC 2.0 factory implementation for common Parity and Geth.
 */
public class JsonRpc2_0Admin extends JsonRpc2_0Web3j implements Admin {

    public JsonRpc2_0Admin(Web3jService web3jService) {
        super(web3jService);
    }
    
    @Override
    public Request<?, PersonalListAccounts> personalListAccounts() {
        return new Request<String, PersonalListAccounts>(
                "personal_listAccounts",
                Collections.<String>emptyList(),
                web3jService,
                PersonalListAccounts.class);
    }

    @Override
    public Request<?, NewAccountIdentifier> personalNewAccount(String password) {
        return new Request<String, NewAccountIdentifier>(
                "personal_newAccount",
                Arrays.asList(password),
                web3jService,
                NewAccountIdentifier.class);
    }   

    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String accountId, String password,
            BigInteger duration) {
        List<Object> attributes = new ArrayList<Object>(3);
        attributes.add(accountId);
        attributes.add(password);
        
        if (duration != null) {
            // Parity has a bug where it won't support a duration
            // See https://github.com/ethcore/parity/issues/1215
            attributes.add(duration.longValue());
        } else {
            // we still need to include the null value, otherwise Parity rejects request
            attributes.add(null);
        }
        
        return new Request<Object, PersonalUnlockAccount>(
                "personal_unlockAccount",
                attributes,
                web3jService,
                PersonalUnlockAccount.class);
    }
    
    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String accountId, String password) {
        
        return personalUnlockAccount(accountId, password, null);
    }
    
    @Override
    public Request<?, PlatonSendTransaction> personalSendTransaction(
            Transaction transaction, String passphrase) {
        return new Request<Object, PlatonSendTransaction>(
                "personal_sendTransaction",
                Arrays.asList(transaction, passphrase),
                web3jService,
                PlatonSendTransaction.class);
    }
    
}
