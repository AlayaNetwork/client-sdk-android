package com.alaya.platon.contracts;

import com.alaya.abi.datatypes.BytesType;
import com.alaya.abi.datatypes.Type;
import com.alaya.abi.datatypes.Utf8String;
import com.alaya.abi.datatypes.generated.Uint32;
import com.alaya.abi.datatypes.generated.Uint8;
import com.alaya.crypto.Credentials;
import com.alaya.protocol.Web3j;
import com.alaya.protocol.core.RemoteCall;
import com.alaya.protocol.core.methods.response.PlatonSendTransaction;
import com.alaya.utils.Numeric;
import com.alaya.platon.BaseResponse;
import com.alaya.platon.ContractAddress;
import com.alaya.platon.FunctionType;
import com.alaya.platon.PlatOnFunction;
import com.alaya.platon.TransactionCallback;
import com.alaya.platon.bean.GovernParam;
import com.alaya.platon.bean.ProgramVersion;
import com.alaya.platon.bean.Proposal;
import com.alaya.platon.VoteOption;
import com.alaya.platon.bean.TallyResult;
import com.alaya.tx.PlatOnContract;
import com.alaya.tx.gas.GasProvider;
import com.alaya.utils.JSONUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import rx.Observable;

public class ProposalContract extends PlatOnContract {

    /**
     * 查询操作
     *
     * @param web3j
     * @return
     */
    public static ProposalContract load(Web3j web3j) {
        return new ProposalContract(ContractAddress.PROPOSAL_CONTRACT_ADDRESS, web3j);
    }

    /**
     * sendRawTransaction 使用用户自定义的gasProvider，必须传chainId
     *
     * @param web3j
     * @param credentials
     * @param chainId
     * @return
     */
    public static ProposalContract load(Web3j web3j, Credentials credentials, long chainId) {
        return new ProposalContract(ContractAddress.PROPOSAL_CONTRACT_ADDRESS, chainId, web3j, credentials);
    }

    private ProposalContract(String contractAddress, Web3j web3j) {
        super(contractAddress, web3j);
    }

    private ProposalContract(String contractAddress, long chainId, Web3j web3j, Credentials credentials) {
        super(contractAddress, chainId, web3j, credentials);
    }

    /**
     * 查询提案
     *
     * @param proposalId
     * @return
     */
    public RemoteCall<BaseResponse<Proposal>> getProposal(String proposalId) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.GET_PROPOSAL_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(proposalId))));
        return new RemoteCall<BaseResponse<Proposal>>(new Callable<BaseResponse<Proposal>>() {
            @Override
            public BaseResponse<Proposal> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseObject(JSONUtil.toJSONString(response.data), Proposal.class);
                return response;
            }
        });
    }

    /**
     * 查询提案结果
     *
     * @param proposalId
     * @return
     */
    public RemoteCall<BaseResponse<TallyResult>> getTallyResult(String proposalId) {
        PlatOnFunction function = new PlatOnFunction(FunctionType.GET_TALLY_RESULT_FUNC_TYPE,
                Arrays.asList(new BytesType(Numeric.hexStringToByteArray(proposalId))));
        return new RemoteCall<BaseResponse<TallyResult>>(new Callable<BaseResponse<TallyResult>>() {
            @Override
            public BaseResponse<TallyResult> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseObject(JSONUtil.toJSONString(response.data), TallyResult.class);
                return response;
            }
        });
    }

    /**
     * 获取提案列表
     *
     * @return
     */
    public RemoteCall<BaseResponse<List<Proposal>>> getProposalList() {
        PlatOnFunction function = new PlatOnFunction(FunctionType.GET_PROPOSAL_LIST_FUNC_TYPE,
                Arrays.<Type>asList());
        return new RemoteCall<BaseResponse<List<Proposal>>>(new Callable<BaseResponse<List<Proposal>>>() {
            @Override
            public BaseResponse<List<Proposal>> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), Proposal.class);
                return response;
            }
        });
    }

    /**
     * 给提案投票
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param proposalID     提案ID
     * @param verifier       投票验证人
     * @return
     */
    public RemoteCall<BaseResponse> vote(ProgramVersion programVersion, VoteOption voteOption, String proposalID, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 给提案投票
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param gasProvider
     * @param proposalID     提案ID
     * @param verifier       投票验证人
     * @return
     */
    public RemoteCall<BaseResponse> vote(ProgramVersion programVersion, VoteOption voteOption, GasProvider gasProvider, String proposalID, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))), gasProvider);
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 给提案投票
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param proposalID     提案ID
     * @param verifier       投票验证人
     * @return
     */
    public GasProvider getVoteProposalGasProvider(ProgramVersion programVersion, VoteOption voteOption, String proposalID, String verifier) {
        return new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign())))).getGasProvider();
    }


    /**
     * 获取提交投票提案的手续费
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param gasPrice
     * @param proposalID
     * @param verifier
     * @return
     */
    public BigInteger getVoteProposalFeeAmount(ProgramVersion programVersion, VoteOption voteOption, BigInteger gasPrice, String proposalID, String verifier) {

        PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return platOnFunction.getGasLimit().multiply(gasPrice == null || gasPrice.compareTo(BigInteger.ZERO) != 1 ? platOnFunction.getGasPrice() : gasPrice);
    }

    /**
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param proposalID     提案ID
     * @param verifier       投票验证人
     * @return
     */
    public RemoteCall<PlatonSendTransaction> voteReturnTransaction(ProgramVersion programVersion, VoteOption voteOption, String proposalID, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()), new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption     投票选项
     * @param proposalID     提案ID
     * @param verifier       投票验证人
     * @return
     */
    public RemoteCall<PlatonSendTransaction> voteReturnTransaction(ProgramVersion programVersion, VoteOption voteOption, GasProvider gasProvider, String proposalID, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.VOTE_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new BytesType(Numeric.hexStringToByteArray(proposalID)), new Uint8(voteOption.getValue()),
                        new Uint32(programVersion.getProgramVersion()), new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))), gasProvider);
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取投票结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getVoteResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.VOTE_FUNC_TYPE);
    }


    /**
     * @param programVersion      程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption          投票选项
     * @param proposalID          提案ID
     * @param verifier            投票验证人
     * @param transactionCallback
     */
    public void asyncVote(ProgramVersion programVersion, VoteOption voteOption, String proposalID, String verifier, TransactionCallback transactionCallback) throws Exception {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = voteReturnTransaction(programVersion, voteOption, proposalID, verifier);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getVoteResult(ethSendTransaction).sendAsync().get();
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
     * @param programVersion      程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param voteOption          投票选项
     * @param gasProvider
     * @param proposalID          提案ID
     * @param verifier            投票验证人
     * @param transactionCallback
     */
    public void asyncVote(ProgramVersion programVersion, VoteOption voteOption, String proposalID, String verifier, GasProvider gasProvider, TransactionCallback transactionCallback) throws Exception {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = voteReturnTransaction(programVersion, voteOption, gasProvider, proposalID, verifier);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getVoteResult(ethSendTransaction).sendAsync().get();
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
     * 版本声明
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param verifier       声明的节点，只能是验证人/候选人
     * @return
     */
    public RemoteCall<BaseResponse> declareVersion(ProgramVersion programVersion, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 版本声明
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param verifier       声明的节点，只能是验证人/候选人
     * @param gasProvider
     * @return
     */
    public RemoteCall<BaseResponse> declareVersion(ProgramVersion programVersion, GasProvider gasProvider, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))), gasProvider);
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取版本声明的gasProvider
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param verifier
     * @return
     */
    public GasProvider getDeclareVersionGasProvider(ProgramVersion programVersion, String verifier) {
        return new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign())))).getGasProvider();
    }


    /**
     * 获取版本声明的手续费
     *
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param gasPrice
     * @param verifier
     * @return
     */
    public BigInteger getDeclareVersionFeeAmount(ProgramVersion programVersion, BigInteger gasPrice, String verifier) {

        PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return platOnFunction.getGasLimit().multiply(gasPrice == null || gasPrice.compareTo(BigInteger.ZERO) != 1 ? platOnFunction.getGasPrice() : gasPrice);
    }


    /**
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param verifier       声明的节点，只能是验证人/候选人
     * @return
     */
    public RemoteCall<PlatonSendTransaction> declareVersionReturnTransaction(ProgramVersion programVersion, String verifier) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))));
        return executeRemoteCallPlatonTransaction(function);
    }


    /**
     * @param programVersion 程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param verifier       声明的节点，只能是验证人/候选人
     * @param gasProvider
     * @return
     */
    public RemoteCall<PlatonSendTransaction> declareVersionReturnTransaction(ProgramVersion programVersion, String verifier, GasProvider gasProvider) throws Exception {
        PlatOnFunction function = new PlatOnFunction(FunctionType.DECLARE_VERSION_FUNC_TYPE,
                Arrays.<Type>asList(new BytesType(Numeric.hexStringToByteArray(verifier)),
                        new Uint32(programVersion.getProgramVersion()),
                        new BytesType(Numeric.hexStringToByteArray(programVersion.getProgramVersionSign()))), gasProvider);
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取版本声明的结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getDeclareVersionResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.DELEGATE_FUNC_TYPE);
    }

    /**
     * 异步声明版本
     *
     * @param programVersion      程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param activeNode
     * @param version
     * @param transactionCallback
     */
    public void asyncDeclareVersion(ProgramVersion programVersion, String activeNode, BigInteger version, TransactionCallback transactionCallback) throws Exception {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = declareVersionReturnTransaction(programVersion, activeNode);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getVoteResult(ethSendTransaction).sendAsync().get();
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
     * 异步声明版本
     *
     * @param programVersion      程序的真实版本，治理rpc接口admin_getProgramVersion获取
     * @param activeNode
     * @param version
     * @param gasProvider
     * @param transactionCallback
     */
    public void asyncDeclareVersion(ProgramVersion programVersion, String activeNode, BigInteger version, GasProvider gasProvider, TransactionCallback transactionCallback) throws Exception {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = declareVersionReturnTransaction(programVersion, activeNode, gasProvider);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getVoteResult(ethSendTransaction).sendAsync().get();
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
     * 提交提案
     *
     * @param proposal 包括文本提案和版本提案
     * @return
     */
    public RemoteCall<BaseResponse> submitProposal(Proposal proposal) {
        if (proposal == null) {
            throw new NullPointerException("proposal must not be null");
        }
        PlatOnFunction function = new PlatOnFunction(proposal.getSubmitFunctionType(),
                proposal.getSubmitInputParameters());
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 提交提案
     *
     * @param proposal    包括文本提案、升级提案、取消提案
     * @param gasProvider
     * @return
     */
    public RemoteCall<BaseResponse> submitProposal(Proposal proposal, GasProvider gasProvider) {
        if (proposal == null) {
            throw new NullPointerException("proposal must not be null");
        }
        PlatOnFunction function = new PlatOnFunction(proposal.getSubmitFunctionType(),
                proposal.getSubmitInputParameters(), gasProvider);
        return executeRemoteCallTransactionWithFunctionType(function);
    }

    /**
     * 获取提交提案gasProvider
     *
     * @param proposal
     * @return
     */
    public Observable<GasProvider> getSubmitProposalGasProvider(Proposal proposal) {
        if (proposal == null) {
            return Observable.error(new Throwable("proposal must not be null"));
        }
        return Observable.fromCallable(new Callable<GasProvider>() {
            @Override
            public GasProvider call() throws Exception {
                return new PlatOnFunction(proposal.getSubmitFunctionType(),
                        proposal.getSubmitInputParameters()).getGasProvider();
            }
        });
    }

    /**
     * 获取提交提案的手续费
     *
     * @param gasPrice
     * @param proposal
     * @return
     */
    public Observable<BigInteger> getSubmitProposalFeeAmount(BigInteger gasPrice, Proposal proposal) {
        if (proposal == null) {
            return Observable.error(new Throwable("proposal must not be null"));
        }

        return Observable.fromCallable(new Callable<BigInteger>() {
            @Override
            public BigInteger call() throws Exception {
                PlatOnFunction platOnFunction = new PlatOnFunction(proposal.getSubmitFunctionType(),
                        proposal.getSubmitInputParameters());
                return platOnFunction.getGasLimit().multiply(gasPrice == null || gasPrice.compareTo(BigInteger.ZERO) != 1 ? platOnFunction.getGasPrice() : gasPrice);
            }
        });
    }

    /**
     * 提交提案
     *
     * @param proposal
     * @return
     */
    public RemoteCall<PlatonSendTransaction> submitProposalReturnTransaction(Proposal proposal) {
        if (proposal == null) {
            throw new NullPointerException("proposal must not be null");
        }
        PlatOnFunction function = new PlatOnFunction(proposal.getSubmitFunctionType(),
                proposal.getSubmitInputParameters());
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 提交提案
     *
     * @param proposal
     * @param gasProvider
     * @return
     */
    public RemoteCall<PlatonSendTransaction> submitProposalReturnTransaction(Proposal proposal, GasProvider gasProvider) {
        if (proposal == null) {
            throw new NullPointerException("proposal must not be null");
        }
        PlatOnFunction function = new PlatOnFunction(proposal.getSubmitFunctionType(),
                proposal.getSubmitInputParameters(), gasProvider);
        return executeRemoteCallPlatonTransaction(function);
    }

    /**
     * 获取提交提案的结果
     *
     * @param ethSendTransaction
     * @return
     */
    public RemoteCall<BaseResponse> getSubmitProposalResult(PlatonSendTransaction ethSendTransaction) {
        return executeRemoteCallTransactionWithFunctionType(ethSendTransaction, FunctionType.SUBMIT_TEXT_FUNC_TYPE);
    }

    /**
     * 异步获取提案结果
     *
     * @param proposal
     * @param transactionCallback
     */
    public void asyncSubmitProposal(Proposal proposal, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = submitProposalReturnTransaction(proposal);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getSubmitProposalResult(ethSendTransaction).sendAsync().get();
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
     * 异步获取提案结果
     *
     * @param proposal
     * @param transactionCallback
     */
    public void asyncSubmitProposal(Proposal proposal, GasProvider gasProvider, TransactionCallback transactionCallback) {

        if (transactionCallback != null) {
            transactionCallback.onTransactionStart();
        }

        RemoteCall<PlatonSendTransaction> ethSendTransactionRemoteCall = submitProposalReturnTransaction(proposal, gasProvider);

        try {
            PlatonSendTransaction ethSendTransaction = ethSendTransactionRemoteCall.sendAsync().get();
            if (transactionCallback != null) {
                transactionCallback.onTransaction(ethSendTransaction);
            }
            BaseResponse baseResponse = getSubmitProposalResult(ethSendTransaction).sendAsync().get();
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
     * 查询已生效的版本
     *
     * @return
     */
    public RemoteCall<BaseResponse> getActiveVersion() {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_ACTIVE_VERSION);
        return new RemoteCall<BaseResponse>(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                return executePatonCall(function);
            }
        });
    }

    /**
     * 查询当前块高的治理参数值
     *
     * @param module 参数模块
     * @param name   参数名称
     * @return
     */
    public RemoteCall<BaseResponse<String>> getGovernParamValue(String module, String name) {
        PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.GET_GOVERN_PARAM_VALUE, Arrays.asList(new Utf8String(module), new Utf8String(name)));
        return new RemoteCall<BaseResponse<String>>(new Callable<BaseResponse<String>>() {
            @Override
            public BaseResponse<String> call() throws Exception {
                return executePatonCall(platOnFunction);
            }
        });
    }

    /**
     * 查询提案的累积可投票人数
     *
     * @param proposalId 提案ID
     * @param blockHash  块hash
     * @return
     */
    public RemoteCall<BaseResponse<BigInteger[]>> getAccuVerifiersCount(String proposalId, String blockHash) {
        PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.GET_ACCUVERIFIERS_COUNT, Arrays.asList(new BytesType(Numeric.hexStringToByteArray(proposalId)), new BytesType(Numeric.hexStringToByteArray(blockHash))));
        return new RemoteCall<BaseResponse<BigInteger[]>>(new Callable<BaseResponse<BigInteger[]>>() {
            @Override
            public BaseResponse<BigInteger[]> call() throws Exception {
                BaseResponse response = executePatonCall(platOnFunction);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), BigInteger.class);
                return response;
            }
        });
    }

    /**
     * 查询可治理参数列表
     *
     * @return
     */
    public RemoteCall<BaseResponse<List<GovernParam>>> getParamList(String module) {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_PARAM_LIST, Arrays.asList(new Utf8String(module)));
        return new RemoteCall<BaseResponse<List<GovernParam>>>(new Callable<BaseResponse<List<GovernParam>>>() {
            @Override
            public BaseResponse<List<GovernParam>> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), GovernParam.class);
                return response;
            }
        });
    }
}
