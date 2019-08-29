package org.web3j.platon.contracts;

import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.TransactionCallback;
import org.web3j.platon.bean.Node;
import org.web3j.platon.bean.ProgramVersion;
import org.web3j.platon.bean.StakingParam;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.PlatOnContract;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.JSONUtil;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.functions.Func1;

public class StakingContract extends PlatOnContract {

    public static StakingContract load(Web3j web3j) {
        return new StakingContract(ContractAddress.STAKING_CONTRACT_ADDRESS, web3j);
    }

    public static StakingContract load(Web3j web3j, Credentials credentials, String chainId) {
        return new StakingContract(ContractAddress.STAKING_CONTRACT_ADDRESS, chainId, web3j, credentials);
    }

    public static StakingContract load(Web3j web3j, Credentials credentials, GasProvider contractGasProvider, String chainId) {
        return new StakingContract(ContractAddress.STAKING_CONTRACT_ADDRESS, chainId, web3j, credentials, contractGasProvider);
    }

    /**
     * 查询操作
     *
     * @param contractAddress
     * @param web3j
     */
    private StakingContract(String contractAddress, Web3j web3j) {
        super(contractAddress, web3j);
    }

    /**
     * sendRawTransaction，使用默认gasProvider
     *
     * @param contractAddress
     * @param chainId
     * @param web3j
     * @param credentials
     */
    private StakingContract(String contractAddress, String chainId, Web3j web3j, Credentials credentials) {
        super(contractAddress, chainId, web3j, credentials);
    }

    /**
     * sendRawTransaction 使用用户自定义的gasProvider
     *
     * @param contractAddress
     * @param chainId
     * @param web3j
     * @param credentials
     * @param gasProvider
     */
    protected StakingContract(String contractAddress, String chainId, Web3j web3j, Credentials credentials, GasProvider gasProvider) {
        super(contractAddress, chainId, web3j, credentials, gasProvider);
    }

    /**
     * 发起质押
     *
     * @param stakingParam
     * @return
     * @see StakingParam
     */
    public RemoteCall<BaseResponse> staking(StakingParam stakingParam) throws Exception {
        StakingParam tempStakingParam = stakingParam.clone();
        tempStakingParam.setProcessVersion(getProgramVersion().send().data);
        final PlatOnFunction function = new PlatOnFunction(
                FunctionType.STAKING_FUNC_TYPE,
                tempStakingParam.getSubmitInputParameters());
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取质押gasProvider
     *
     * @param stakingParam
     * @return
     */
    public Observable<GasProvider> getStakingGasProvider(StakingParam stakingParam) {
        StakingParam tempStakingParam = stakingParam.clone();
        return Observable.fromCallable(new Callable<ProgramVersion>() {
            @Override
            public ProgramVersion call() throws Exception {
                return getProgramVersion().send().data;
            }
        }).map(new Func1<ProgramVersion, GasProvider>() {
            @Override
            public GasProvider call(ProgramVersion programVersion) {
                tempStakingParam.setProcessVersion(programVersion);
                return new PlatOnFunction(
                        FunctionType.STAKING_FUNC_TYPE,
                        tempStakingParam.getSubmitInputParameters()).getGasProvider();
            }
        });
    }


    /**
     * 获取质押gasProvider
     *
     * @param gasPrice
     * @param stakingParam
     * @return
     */
    public Observable<BigInteger> getFeeAmount(BigInteger gasPrice, StakingParam stakingParam) {
        StakingParam tempStakingParam = stakingParam.clone();
        return Observable.fromCallable(new Callable<ProgramVersion>() {
            @Override
            public ProgramVersion call() throws Exception {
                return getProgramVersion().send().data;
            }
        }).map(new Func1<ProgramVersion, BigInteger>() {
            @Override
            public BigInteger call(ProgramVersion programVersion) {
                tempStakingParam.setProcessVersion(programVersion);
                PlatOnFunction platOnFunction = new PlatOnFunction(
                        FunctionType.STAKING_FUNC_TYPE,
                        tempStakingParam.getSubmitInputParameters());
                return platOnFunction.getGasLimit().add(gasPrice == null || gasPrice.compareTo(BigInteger.ZERO) != 1 ? platOnFunction.getGasPrice() : gasPrice);
            }
        });
    }


    /**
     * 发起质押
     *
     * @param stakingParam
     * @return
     * @see StakingParam
     */
    public RemoteCall<PlatonSendTransaction> stakingReturnTransaction(StakingParam stakingParam) throws Exception {
        StakingParam tempStakingParam = stakingParam.clone();
        tempStakingParam.setProcessVersion(getProgramVersion().send().data);
        final PlatOnFunction function = new PlatOnFunction(
                FunctionType.STAKING_FUNC_TYPE,
                stakingParam.getSubmitInputParameters());
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取质押结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getStakingResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.STAKING_FUNC_TYPE);
    }

    /**
     * 异步获取质押结果
     *
     * @param stakingParam
     * @param transactionCallback
     */
    public void asyncStaking(StakingParam stakingParam, TransactionCallback<BaseResponse> transactionCallback) throws Exception {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        StakingParam tempStakingParam = stakingParam.clone();
        tempStakingParam.setProcessVersion(getProgramVersion().send().data);

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = stakingReturnTransaction(tempStakingParam);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getStakingResult(ethSendTransaction).sendAsync().get();
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
     * 撤销质押
     *
     * @param nodeId 64bytes 被质押的节点Id(也叫候选人的节点Id)
     * @return
     */
    public RemoteCall<BaseResponse> unStaking(String nodeId) {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.WITHDREW_STAKING_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(nodeId))));
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取撤销质押的gasProvider
     *
     * @param nodeId
     * @return
     */
    public Observable<GasProvider> getUnStakingGasProvider(String nodeId) {
        return Observable.fromCallable(new Callable<GasProvider>() {
            @Override
            public GasProvider call() throws Exception {
                return new PlatOnFunction(FunctionType.WITHDREW_STAKING_FUNC_TYPE,
                        Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(nodeId)))).getGasProvider();
            }
        });
    }

    /**
     * 撤销质押
     *
     * @param nodeId 64bytes 被质押的节点Id(也叫候选人的节点Id)
     * @return
     */
    public RemoteCall<PlatonSendTransaction> unStakingReturnTransaction(String nodeId) {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.WITHDREW_STAKING_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(nodeId))));
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取质押结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getUnStakingResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.WITHDREW_STAKING_FUNC_TYPE);
    }

    /**
     * 异步撤销质押
     *
     * @param nodeId 64bytes 被质押的节点Id(也叫候选人的节点Id)
     */
    public void asyncUnStaking(String nodeId, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = unStakingReturnTransaction(nodeId);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getUnStakingResult(ethSendTransaction).sendAsync().get();
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
     * 更新质押信息
     *
     * @param nodeId
     * @param benifitAddress
     * @param externalId
     * @param nodeName
     * @param webSite
     * @param details
     * @return
     */
    public RemoteCall<BaseResponse> updateStakingInfo(String nodeId, String benifitAddress, String externalId, String nodeName, String webSite, String details) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.UPDATE_STAKING_INFO_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(benifitAddress)),
                        new BytesType(Numeric.hexStringToByteArray(nodeId)),
                        new Utf8String(externalId),
                        new Utf8String(nodeName),
                        new Utf8String(webSite),
                        new Utf8String(details)));
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取
     *
     * @param nodeId
     * @param benifitAddress
     * @param externalId
     * @param nodeName
     * @param webSite
     * @param details
     * @return
     */
    public Observable<GasProvider> getUpdateStakingInfo(String nodeId, String benifitAddress, String externalId, String nodeName, String webSite, String details) {
        return Observable.fromCallable(new Callable<GasProvider>() {
            @Override
            public GasProvider call() throws Exception {
                return new PlatOnFunction(FunctionType.UPDATE_STAKING_INFO_FUNC_TYPE,
                        Arrays.asList(new BytesType(Numeric.hexStringToByteArray(benifitAddress)),
                                new BytesType(Numeric.hexStringToByteArray(nodeId)),
                                new Utf8String(externalId),
                                new Utf8String(nodeName),
                                new Utf8String(webSite),
                                new Utf8String(details))).getGasProvider();
            }
        });
    }

    /**
     * 更新质押信息
     *
     * @param nodeId         被质押的节点Id(也叫候选人的节点Id)
     * @param benifitAddress 用于接受出块奖励和质押奖励的收益账户
     * @param externalId     外部Id(有长度限制，给第三方拉取节点描述的Id)
     * @param nodeName       被质押节点的名称(有长度限制，表示该节点的名称)
     * @param webSite        节点的第三方主页(有长度限制，表示该节点的主页)
     * @param details        节点的第三方主页(有长度限制，表示该节点的主页)
     * @return
     */
    public RemoteCall<PlatonSendTransaction> updateStakingInfoReturnTransaction(String nodeId, String benifitAddress, String externalId, String nodeName, String webSite, String details) {

        PlatOnFunction function = new PlatOnFunction(FunctionType.UPDATE_STAKING_INFO_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(benifitAddress)),
                        new BytesType(Numeric.hexStringToByteArray(nodeId)),
                        new Utf8String(externalId),
                        new Utf8String(nodeName),
                        new Utf8String(webSite),
                        new Utf8String(details)));
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取更新质押结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getUpdateStakingInfoResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.UPDATE_STAKING_INFO_FUNC_TYPE);
    }

    /**
     * 异步更新质押信息
     *
     * @param nodeId              被质押的节点Id(也叫候选人的节点Id)
     * @param benifitAddress      用于接受出块奖励和质押奖励的收益账户
     * @param externalId          外部Id(有长度限制，给第三方拉取节点描述的Id)
     * @param nodeName            被质押节点的名称(有长度限制，表示该节点的名称)
     * @param webSite             节点的第三方主页(有长度限制，表示该节点的主页)
     * @param details             节点的第三方主页(有长度限制，表示该节点的主页)
     * @param transactionCallback
     */
    public void asyncUpdateStakingInfo(String nodeId, String benifitAddress, String externalId, String nodeName, String webSite, String details, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = updateStakingInfoReturnTransaction(nodeId, benifitAddress, externalId, nodeName, webSite, details);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getUpdateStakingInfoResult(ethSendTransaction).sendAsync().get();
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
     * 增持质押
     *
     * @param nodeId            被质押的节点Id(也叫候选人的节点Id)
     * @param stakingAmountType 表示使用账户自由金额还是账户的锁仓金额做质押，0: 自由金额； 1: 锁仓金额
     * @param amount            增持的von
     * @return
     */
    public RemoteCall<BaseResponse> addStaking(String nodeId, StakingAmountType stakingAmountType, BigInteger amount) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.ADD_STAKING_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(nodeId)),
                        new Uint16(stakingAmountType.getValue()),
                        new Uint256(amount)));
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取增持质押gasProvider
     *
     * @param nodeId
     * @param stakingAmountType
     * @param amount
     * @return
     */
    public Observable<GasProvider> getAddStakingGasProvider(String nodeId, StakingAmountType stakingAmountType, BigInteger amount) {
        return Observable.fromCallable(new Callable<GasProvider>() {
            @Override
            public GasProvider call() throws Exception {
                return new PlatOnFunction(FunctionType.ADD_STAKING_FUNC_TYPE,
                        Arrays.asList(new BytesType(Numeric.hexStringToByteArray(nodeId)),
                                new Uint16(stakingAmountType.getValue()),
                                new Uint256(amount))).getGasProvider();
            }
        });
    }

    /**
     * 增持质押
     *
     * @param nodeId            被质押的节点Id(也叫候选人的节点Id)
     * @param stakingAmountType 表示使用账户自由金额还是账户的锁仓金额做质押，0: 自由金额； 1: 锁仓金额
     * @param amount            增持的von
     * @return
     */
    public RemoteCall<PlatonSendTransaction> addStakingReturnTransaction(String nodeId, StakingAmountType stakingAmountType, BigInteger amount) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.ADD_STAKING_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(nodeId)),
                        new Uint16(stakingAmountType.getValue()),
                        new Uint256(amount)));
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取增持质押的结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getAddStakingResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.ADD_STAKING_FUNC_TYPE);
    }

    /**
     * @param nodeId              被质押的节点Id(也叫候选人的节点Id)
     * @param stakingAmountType   表示使用账户自由金额还是账户的锁仓金额做质押，0: 自由金额； 1: 锁仓金额
     * @param amount              增持的von
     * @param transactionCallback
     */
    public void asyncAddStaking(String nodeId, StakingAmountType stakingAmountType, BigInteger amount, TransactionCallback transactionCallback) {
        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = addStakingReturnTransaction(nodeId, stakingAmountType, amount);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getAddStakingResult(ethSendTransaction).sendAsync().get();
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
     * 获取质押信息
     *
     * @param nodeId
     * @return
     */
    public RemoteCall<BaseResponse<Node>> getStakingInfo(String nodeId) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.GET_STAKINGINFO_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(nodeId))));
        return new RemoteCall<BaseResponse<Node>>(new Callable<BaseResponse<Node>>() {
            @Override
            public BaseResponse<Node> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseObject((String) response.data, Node.class);
                return response;
            }
        });
    }

}
