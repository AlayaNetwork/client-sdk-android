package org.web3j.platon.contracts;

import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.DoubleSignType;
import org.web3j.platon.FunctionType;
import org.web3j.platon.TransactionCallback;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.PlatOnContract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class SlashContract extends PlatOnContract {

    public static SlashContract load(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SlashContract("", SLASH_CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);
    }

    public static SlashContract load(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SlashContract("", SLASH_CONTRACT_ADDRESS, web3j, transactionManager, contractGasProvider);
    }

    public static SlashContract load(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String chainId) {
        return new SlashContract("", SLASH_CONTRACT_ADDRESS, chainId, web3j, credentials, contractGasProvider);
    }

    protected SlashContract(String contractBinary, String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, transactionManager, gasProvider);
    }

    public SlashContract(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, credentials, gasProvider);
    }

    public SlashContract(String contractBinary, String contractAddress, String chainId, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, chainId, web3j, credentials, gasProvider);
    }

    /**
     * 举报双签
     *
     * @param data 证据的json值
     * @return
     */
    public RemoteCall<BaseResponse> reportDoubleSign(String data) {
        Function function = new Function(FunctionType.REPORT_DOUBLESIGN_FUNC_TYPE,
                Arrays.asList(new Utf8String(data))
                , Collections.emptyList());
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 举报双签
     *
     * @param data 证据的json值
     * @return
     */
    public RemoteCall<PlatonSendTransaction> reportDoubleSignReturnTransaction(String data) {
        Function function = new Function(FunctionType.REPORT_DOUBLESIGN_FUNC_TYPE,
                Arrays.asList(new Utf8String(data))
                , Collections.emptyList());
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getReportDoubleSignResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.REPORT_DOUBLESIGN_FUNC_TYPE);
    }

    /**
     * 异步举报双签
     *
     * @param data                证据的json值
     * @param transactionCallback
     */
    public void asyncReportDoubleSignResult(String data, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = reportDoubleSignReturnTransaction(data);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getReportDoubleSignResult(ethSendTransaction).sendAsync().get();
            if (transactionCallback != null) {
                if (baseResponse.isStatusOk()) {
                    transactionCallback.onTransactionSucceed(baseResponse);
                } else {
                    transactionCallback.onTransactionFailed(baseResponse);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (transactionCallback != null) {
                transactionCallback.onTransactionFailed(new BaseResponse(e));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            if (transactionCallback != null) {
                transactionCallback.onTransactionFailed(new BaseResponse(e));
            }
        }
    }

    /**
     * 查询节点是否已被举报过多签
     *
     * @param doubleSignType 代表双签类型，1：prepare，2：viewChange
     * @param address        举报的节点地址
     * @param blockNumber    多签的块高
     * @return
     */
    public RemoteCall<BaseResponse> checkDoubleSign(DoubleSignType doubleSignType, String address, BigInteger blockNumber) {
        Function function = new Function(FunctionType.CHECK_DOUBLESIGN_FUNC_TYPE,
                Arrays.asList(new Uint16(doubleSignType.getValue())
                        , new Utf8String(address)
                        , new Uint64(blockNumber))
                , Collections.emptyList());
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 查询节点是否已被举报过多签
     *
     * @param doubleSignType 代表双签类型，1：prepare，2：viewChange
     * @param address        举报的节点地址
     * @param blockNumber    多签的块高
     * @return
     */
    public RemoteCall<PlatonSendTransaction> checkDoubleSignReturnTransaction(DoubleSignType doubleSignType, String address, BigInteger blockNumber) {
        Function function = new Function(FunctionType.CHECK_DOUBLESIGN_FUNC_TYPE,
                Arrays.asList(new Uint16(doubleSignType.getValue())
                        , new Utf8String(address)
                        , new Uint64(blockNumber))
                , Collections.emptyList());
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取查询节点是否已被举报过多签的结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getCheckDoubleSignResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.CHECK_DOUBLESIGN_FUNC_TYPE);
    }

    /**
     * 异步获取
     * @param doubleSignType 代表双签类型，1：prepare，2：viewChange
     * @param address 举报的节点地址
     * @param blockNumber 多签的块高
     * @param transactionCallback
     */
    public void asyncCheckDoubleSign(DoubleSignType doubleSignType, String address, BigInteger blockNumber, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = checkDoubleSignReturnTransaction(doubleSignType, address, blockNumber);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getCheckDoubleSignResult(ethSendTransaction).sendAsync().get();
            if (transactionCallback != null) {
                if (baseResponse.isStatusOk()) {
                    transactionCallback.onTransactionSucceed(baseResponse);
                } else {
                    transactionCallback.onTransactionFailed(baseResponse);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (transactionCallback != null) {
                transactionCallback.onTransactionFailed(new BaseResponse(e));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            if (transactionCallback != null) {
                transactionCallback.onTransactionFailed(new BaseResponse(e));
            }
        }
    }

}
