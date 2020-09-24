package com.alaya.platon.contracts;

import com.alaya.crypto.Credentials;
import com.alaya.protocol.Web3j;
import com.alaya.protocol.core.RemoteCall;
import com.alaya.platon.BaseResponse;
import com.alaya.platon.ContractAddress;
import com.alaya.platon.FunctionType;
import com.alaya.platon.PlatOnFunction;
import com.alaya.platon.bean.Node;
import com.alaya.tx.PlatOnContract;
import com.alaya.utils.JSONUtil;

import java.util.List;
import java.util.concurrent.Callable;

public class NodeContract extends PlatOnContract {

    public static NodeContract load(Web3j web3j) {
        return new NodeContract(ContractAddress.NODE_CONTRACT_ADDRESS, web3j);
    }

    public static NodeContract load(Web3j web3j, Credentials credentials, long chainId) {
        return new NodeContract(ContractAddress.NODE_CONTRACT_ADDRESS, chainId, web3j, credentials);
    }

    private NodeContract(String contractAddress, Web3j web3j) {
        super(contractAddress, web3j);
    }

    private NodeContract(String contractAddress, long chainId, Web3j web3j, Credentials credentials) {
        super(contractAddress, chainId, web3j, credentials);
    }


    /**
     * 查询当前结算周期的验证人队列
     *
     * @return
     */
    public RemoteCall<BaseResponse<List<Node>>> getVerifierList() {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_VERIFIERLIST_FUNC_TYPE);
        return new RemoteCall<BaseResponse<List<Node>>>(new Callable<BaseResponse<List<Node>>>() {
            @Override
            public BaseResponse<List<Node>> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), Node.class);
                return response;
            }
        });
    }

    /**
     * 查询当前共识周期的验证人列表
     *
     * @return
     */
    public RemoteCall<BaseResponse<List<Node>>> getValidatorList() {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_VALIDATORLIST_FUNC_TYPE);
        return new RemoteCall<BaseResponse<List<Node>>>(new Callable<BaseResponse<List<Node>>>() {
            @Override
            public BaseResponse<List<Node>> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), Node.class);
                return response;
            }
        });
    }

    /**
     * 查询所有实时的候选人列表
     *
     * @return
     */
    public RemoteCall<BaseResponse<List<Node>>> getCandidateList() {
        final PlatOnFunction function = new PlatOnFunction(FunctionType.GET_CANDIDATELIST_FUNC_TYPE);
        return new RemoteCall<BaseResponse<List<Node>>>(new Callable<BaseResponse<List<Node>>>() {
            @Override
            public BaseResponse<List<Node>> call() throws Exception {
                BaseResponse response = executePatonCall(function);
                response.data = JSONUtil.parseArray(JSONUtil.toJSONString(response.data), Node.class);
                return response;
            }
        });
    }
}
