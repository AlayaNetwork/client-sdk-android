package com.alaya.tx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.alaya.crypto.Credentials;
import com.alaya.protocol.Web3j;
import com.alaya.protocol.core.RemoteCall;
import com.alaya.protocol.core.methods.response.TransactionReceipt;
import com.alaya.protocol.exceptions.TransactionException;
import com.alaya.utils.Convert;
import com.alaya.utils.Numeric;
import org.spongycastle.util.encoders.Hex;
import com.alaya.abi.PlatOnTypeEncoder;
import com.alaya.abi.datatypes.generated.Int64;
import com.alaya.rlp.RlpEncoder;
import com.alaya.rlp.RlpList;
import com.alaya.rlp.RlpString;
import com.alaya.rlp.RlpType;

/**
 * Class for performing Ether transactions on the Ethereum blockchain.
 */
public class Transfer extends ManagedTransaction {

    // This is the cost to send Ether between parties
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(210000);

    public Transfer(Web3j web3j, TransactionManager transactionManager) {
        super(web3j, transactionManager);
    }

    /**
     * Given the duration required to execute a transaction, asyncronous execution is strongly
     * recommended via {@link Transfer#sendFunds(String, BigDecimal, Convert.Unit)}.
     *
     * @param toAddress destination address
     * @param value     amount to send
     * @param unit      of specified send
     * @return {@link Optional} containing our transaction receipt
     * @throws ExecutionException   if the computation threw an
     *                              exception
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     * @throws TransactionException if the transaction was not mined while waiting
     */
    private TransactionReceipt send(String toAddress, BigDecimal value, Convert.Unit unit)
            throws IOException, InterruptedException,
            TransactionException {

        BigInteger gasPrice = requestCurrentGasPrice();
        return send(toAddress, value, unit, gasPrice, GAS_LIMIT);
    }

    private TransactionReceipt send(String toAddress, BigDecimal value, Convert.Unit unit, BigInteger gasPrice,
                                    BigInteger gasLimit) throws IOException, InterruptedException,
            TransactionException {

        BigDecimal weiValue = Convert.toVon(value, unit);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException(
                    "Non decimal Wei value provided: " + value + " " + unit.toString()
                            + " = " + weiValue + " Wei");
        }

        String resolvedAddress = ensResolver.resolve(toAddress);

        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(Numeric.hexStringToByteArray(PlatOnTypeEncoder.encode(new Int64(0)))));
        String data = Hex.toHexString(RlpEncoder.encode(new RlpList(result)));

        return send(resolvedAddress, data, weiValue.toBigIntegerExact(), gasPrice, gasLimit);
    }

    public static RemoteCall<TransactionReceipt> sendFunds(
            Web3j web3j, Credentials credentials, String chainId,
            String toAddress, BigDecimal value, Convert.Unit unit) throws InterruptedException,
            IOException, TransactionException {

        TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, new Byte(chainId));

        return new RemoteCall<>(() ->
                new Transfer(web3j, transactionManager).send(toAddress, value, unit));
    }

    /**
     * Execute the provided function as a transaction asynchronously. This is intended for one-off
     * fund transfers. For multiple, create an instance.
     *
     * @param toAddress destination address
     * @param value     amount to send
     * @param unit      of specified send
     * @return {@link RemoteCall} containing executing transaction
     */
    public RemoteCall<TransactionReceipt> sendFunds(
            String toAddress, BigDecimal value, Convert.Unit unit) {
        return new RemoteCall<>(() -> send(toAddress, value, unit));
    }

    public RemoteCall<TransactionReceipt> sendFunds(
            String toAddress, BigDecimal value, Convert.Unit unit, BigInteger gasPrice,
            BigInteger gasLimit) {
        return new RemoteCall<>(() -> send(toAddress, value, unit, gasPrice, gasLimit));
    }

}
