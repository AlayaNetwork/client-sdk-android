package org.web3j.tx;

import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.PlatOnEventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.PlatonCall;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.GasProvider;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.JSONUtil;
import org.web3j.utils.Numeric;
import org.web3j.utils.PlatOnUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;


/**
 * Solidity contract type abstraction for interacting with smart contracts via native Java types.
 */
@SuppressWarnings("WeakerAccess")
public abstract class PlatOnContract extends ManagedTransaction {

    protected String contractAddress;
    protected GasProvider gasProvider;
    protected TransactionReceipt transactionReceipt;

    private PlatOnContract(String contractAddress,
                           Web3j web3j, TransactionManager transactionManager,
                           GasProvider gasProvider) {
        super(web3j, transactionManager);

        this.contractAddress = ensResolver.resolve(contractAddress);

        this.gasProvider = gasProvider;
    }

    /**
     * sendRawTransaction 使用用户自定义的gasProvider，必须传chainId
     *
     * @param contractAddress
     * @param chainId
     * @param web3j
     * @param credentials
     * @param gasProvider
     */
    protected PlatOnContract(String contractAddress, String chainId,
                             Web3j web3j, Credentials credentials,
                             GasProvider gasProvider) {
        this(contractAddress, web3j,
                new RawTransactionManager(web3j, credentials, new Byte(chainId)),
                gasProvider);
    }

    /**
     * sendRawTransaction 使用默认的gasProvider，必须传chainId
     *
     * @param contractAddress
     * @param chainId
     * @param web3j
     * @param credentials
     */
    protected PlatOnContract(String contractAddress, String chainId,
                             Web3j web3j, Credentials credentials) {

        this(contractAddress, web3j, new RawTransactionManager(web3j, credentials, new Byte(chainId)), null);
    }

    /**
     * 查询操作，查询不需要chainId和gasProvier
     *
     * @param contractAddress
     * @param web3j
     */
    protected PlatOnContract(String contractAddress,
                             Web3j web3j) {

        this(contractAddress, web3j,
                new ReadonlyTransactionManager(web3j, contractAddress), null);
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    /**
     * Allow {@code gasPrice} to be set.
     *
     * @param newPrice gas price to use for subsequent transactions
     * @deprecated use ContractGasProvider
     */
    public void setGasPrice(BigInteger newPrice) {
        this.gasProvider = new ContractGasProvider(newPrice, gasProvider.getGasLimit());
    }

    /**
     * Get the current {@code gasPrice} value this contract uses when executing transactions.
     *
     * @return the gas price set on this contract
     * @deprecated use ContractGasProvider
     */
    public BigInteger getGasPrice() {
        return gasProvider.getGasPrice();
    }

    /**
     * If this Contract instance was created at deployment, the TransactionReceipt associated
     * with the initial creation will be provided, e.g. via a <em>deploy</em> method. This will
     * not persist for Contracts instances constructed via a <em>load</em> method.
     *
     * @return the TransactionReceipt generated at contract deployment
     */
    public Optional<TransactionReceipt> getTransactionReceipt() {
        return Optional.ofNullable(transactionReceipt);
    }

    /**
     * Execute constant function call - i.e. a call that does not change state of the contract
     *
     * @param function to call
     * @return {@link List} of values returned by function call
     */
    private List<Type> executeCall(Function function) throws IOException {
        String encodedFunction = PlatOnUtil.invokeEncode(function);
        PlatonCall ethCall = web3j.platonCall(
                Transaction.createEthCallTransaction(
                        transactionManager.getFromAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .send();
        String value = ethCall.getValue();
        return FunctionReturnDecoder.decode(value, function.getOutputParameters());
    }

    protected BaseResponse executePatonCall(PlatOnFunction function) throws IOException {
        PlatonCall ethCall = web3j.platonCall(
                Transaction.createEthCallTransaction(
                        transactionManager.getFromAddress(), contractAddress, function.getEncodeData()),
                DefaultBlockParameterName.LATEST)
                .send();
        String value = ethCall.getValue();
        BaseResponse response = JSONUtil.parseObject(new String(Numeric.hexStringToByteArray(value)), BaseResponse.class);
        if (response == null) {
            response = new BaseResponse();
        }
        return response;
    }

    protected BaseResponse executePatonCall(PlatOnFunction function, String contractAddress) throws IOException {
        PlatonCall ethCall = web3j.platonCall(
                Transaction.createEthCallTransaction(
                        transactionManager.getFromAddress(), contractAddress, function.getEncodeData()),
                DefaultBlockParameterName.LATEST)
                .send();
        String value = ethCall.getValue();
        BaseResponse response = JSONUtil.parseObject(new String(Numeric.hexStringToByteArray(value)), BaseResponse.class);
        if (response == null) {
            response = new BaseResponse();
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Type> T executeCallSingleValueReturn(
            Function function) throws IOException {
        List<Type> values = executeCall(function);
        if (!values.isEmpty()) {
            return (T) values.get(0);
        } else {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    protected <T extends Type, R> R executeCallSingleValueReturn(
            Function function, Class<R> returnType) throws IOException {
        T result = executeCallSingleValueReturn(function);
        if (result == null) {
            throw new ContractCallException("Empty value (0x) returned from contract");
        }

        Object value = result.getValue();
        if (returnType.isAssignableFrom(value.getClass())) {
            return (R) value;
        } else if (result.getClass().equals(Address.class) && returnType.equals(String.class)) {
            return (R) result.toString();  // cast isn't necessary
        } else {
            throw new ContractCallException(
                    "Unable to convert response: " + value
                            + " to expected type: " + returnType.getSimpleName());
        }
    }

    protected List<Type> executeCallMultipleValueReturn(
            Function function) throws IOException {
        return executeCall(function);
    }

    /**
     * Given the duration required to execute a transaction.
     *
     * @param function to send in transaction
     * @param weiValue in Wei to send in transaction
     * @return {@link Optional} containing our transaction receipt
     * @throws IOException          if the call to the node fails
     * @throws TransactionException if the transaction was not mined while waiting
     */
    BaseResponse executeTransaction(
            PlatOnFunction function, BigInteger weiValue)
            throws TransactionException, IOException {

        if (gasProvider == null) {
            gasProvider = function.getGasProvider();
        }

        TransactionReceipt receipt = send(contractAddress, function.getEncodeData(), weiValue,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit());

        return getResponseFromTransactionReceipt(receipt, function.getType());
    }

    BaseResponse getTransactionResult(PlatonSendTransaction ethSendTransaction, int functionType) throws IOException, TransactionException {

        TransactionReceipt receipt = getTransactionReceipt(ethSendTransaction);

        if (!receipt.isStatusOK()) {
            throw new TransactionException(
                    String.format(
                            "Transaction has failed with status: %s. "
                                    + "Gas used: %d. (not-enough gas?)",
                            receipt.getStatus(),
                            receipt.getGasUsed()));
        }
        return getResponseFromTransactionReceipt(transactionReceipt, functionType);
    }

    PlatonSendTransaction executePlatonTransaction(
            PlatOnFunction function, BigInteger weiValue)
            throws TransactionException, IOException {

        if (gasProvider == null) {
            gasProvider = function.getGasProvider();
        }
        return sendPlatonRawTransaction(contractAddress, function.getEncodeData(), weiValue,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit());
    }

    private BaseResponse getResponseFromTransactionReceipt(TransactionReceipt transactionReceipt, int functionType) throws TransactionException {

        Event event = new Event(functionType,
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }));

        List<EventValuesWithLog> eventValuesWithLogList = extractEventParametersWithLog(event, transactionReceipt);

        return JSONUtil.parseObject(getResponseFromLog(transactionReceipt, eventValuesWithLogList), BaseResponse.class);
    }

    private String getResponseFromLog(TransactionReceipt transactionReceipt, List<EventValuesWithLog> eventValuesWithLogList) throws TransactionException {

        boolean isEventValuesWithLogEmpty = eventValuesWithLogList == null || eventValuesWithLogList.isEmpty();

        List<Type> nonIndexedValues;

        if (isEventValuesWithLogEmpty || (nonIndexedValues = eventValuesWithLogList.get(0).getNonIndexedValues()) == null || nonIndexedValues.isEmpty()) {
            throw new TransactionException(
                    String.format(
                            "Transaction has failed with status: %s. "
                                    + "Gas used: %d. (not-enough gas?)",
                            transactionReceipt.getStatus(),
                            transactionReceipt.getGasUsed()));
        }

        return (String) nonIndexedValues.get(0).getValue();
    }

    protected <T> RemoteCall<BaseResponse<T>> executePlatonRemoteCallSingleValueReturn(PlatOnFunction function) {
        return new RemoteCall<BaseResponse<T>>(new Callable<BaseResponse<T>>() {
            @Override
            public BaseResponse<T> call() throws Exception {
                return executePatonCall(function);
            }
        });
    }

    protected <T extends Type> RemoteCall<T> executeRemoteCallSingleValueReturn(Function function) {
        return new RemoteCall<>(() -> executeCallSingleValueReturn(function));
    }

    protected <T> RemoteCall<T> executeRemoteCallSingleValueReturn(
            Function function, Class<T> returnType) {
        return new RemoteCall<>(() -> executeCallSingleValueReturn(function, returnType));
    }

    protected RemoteCall<List<Type>> executeRemoteCallMultipleValueReturn(Function function) {
        return new RemoteCall<>(() -> executeCallMultipleValueReturn(function));
    }

    protected RemoteCall<PlatonSendTransaction> executeRemoteCallPlatonTransaction(PlatOnFunction function, BigInteger weiValue) {
        return new RemoteCall<>(() -> executePlatonTransaction(function, weiValue));
    }

    protected RemoteCall<PlatonSendTransaction> executeRemoteCallPlatonTransaction(PlatOnFunction function) {
        return new RemoteCall<>(() -> executePlatonTransaction(function, BigInteger.ZERO));
    }

    protected RemoteCall<BaseResponse> executeRemoteCallTransactionWithFunctionType(PlatOnFunction function, BigInteger weiValue) {
        return new RemoteCall<>(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                return executeTransaction(function, weiValue);
            }
        });
    }

    protected RemoteCall<BaseResponse> executeRemoteCallTransactionWithFunctionType(PlatOnFunction function) {
        return new RemoteCall<>(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                return executeTransaction(function, BigInteger.ZERO);
            }
        });
    }

    protected RemoteCall<BaseResponse> executeRemoteCallTransactionWithFunctionType(PlatonSendTransaction ethSendTransaction, int functionType) {
        return new RemoteCall<>(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                return getTransactionResult(ethSendTransaction, functionType);
            }
        });
    }

    /**
     * 查询节点代码版本
     *
     * @return
     */
    public RemoteCall<BaseResponse<String>> getProgramVersion() {

        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_PROGRAM_VERSION);
        return new RemoteCall<BaseResponse<String>>(new Callable<BaseResponse<String>>() {
            @Override
            public BaseResponse<String> call() throws Exception {
                return executePatonCall(function, ensResolver.resolve(ContractAddress.PROPOSAL_CONTRACT_ADDRESS));
            }
        });
    }

    public static EventValues staticExtractEventParameters(
            Event event, Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = PlatOnEventEncoder.encodeWithFunctionType(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = PlatOnUtil.eventDecode(log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    protected EventValues extractEventParameters(Event event, Log log) {
        return staticExtractEventParameters(event, log);
    }

    protected List<EventValues> extractEventParameters(
            Event event, TransactionReceipt transactionReceipt) {
        List<EventValues> list = new ArrayList<>();
        for (Log log : transactionReceipt.getLogs()) {
            EventValues eventValues = extractEventParameters(event, log);
            if (eventValues != null) {
                list.add(eventValues);
            }
        }
        return list;
    }

    protected EventValuesWithLog extractEventParametersWithLog(Event event, Log log) {
        final EventValues eventValues = staticExtractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    protected List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> list = new ArrayList<>();
        for (Log log : transactionReceipt.getLogs()) {
            EventValuesWithLog eventValuesWithLog = extractEventParametersWithLog(event, log);
            if (eventValuesWithLog != null) {
                list.add(eventValuesWithLog);
            }
        }
        return list;
    }

    /**
     * Adds a log field to {@link EventValues}.
     */
    public static class EventValuesWithLog {
        private final EventValues eventValues;
        private final Log log;

        private EventValuesWithLog(EventValues eventValues, Log log) {
            this.eventValues = eventValues;
            this.log = log;
        }

        public List<Type> getIndexedValues() {
            return eventValues.getIndexedValues();
        }

        public List<Type> getNonIndexedValues() {
            return eventValues.getNonIndexedValues();
        }

        public Log getLog() {
            return log;
        }
    }
}
