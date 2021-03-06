package com.alaya.protocol.core;

import com.alaya.protocol.core.methods.request.ShhFilter;
import com.alaya.protocol.rx.JsonRpc2_0Rx;
import com.alaya.utils.Numeric;
import com.alaya.protocol.Web3j;
import com.alaya.protocol.Web3jService;
import com.alaya.protocol.core.methods.response.AdminProgramVersion;
import com.alaya.protocol.core.methods.response.AdminSchnorrNIZKProve;
import com.alaya.protocol.core.methods.response.DbGetHex;
import com.alaya.protocol.core.methods.response.DbGetString;
import com.alaya.protocol.core.methods.response.DbPutHex;
import com.alaya.protocol.core.methods.response.DbPutString;
import com.alaya.protocol.core.methods.response.DebugEconomicConfig;
import com.alaya.protocol.core.methods.response.PlatonAccounts;
import com.alaya.protocol.core.methods.response.PlatonEvidences;
import com.alaya.protocol.core.methods.response.PlatonGasPrice;
import com.alaya.protocol.core.methods.response.PlatonProtocolVersion;
import com.alaya.protocol.core.methods.response.PlatonSendTransaction;
import com.alaya.protocol.core.methods.response.PlatonSyncing;
import com.alaya.protocol.core.methods.response.PlatonBlock;
import com.alaya.protocol.core.methods.response.PlatonBlockNumber;
import com.alaya.protocol.core.methods.response.PlatonCall;
import com.alaya.protocol.core.methods.response.PlatonEstimateGas;
import com.alaya.protocol.core.methods.response.PlatonFilter;
import com.alaya.protocol.core.methods.response.PlatonGetBalance;
import com.alaya.protocol.core.methods.response.PlatonGetBlockTransactionCountByHash;
import com.alaya.protocol.core.methods.response.PlatonGetBlockTransactionCountByNumber;
import com.alaya.protocol.core.methods.response.PlatonGetCode;
import com.alaya.protocol.core.methods.response.PlatonGetStorageAt;
import com.alaya.protocol.core.methods.response.PlatonGetTransactionCount;
import com.alaya.protocol.core.methods.response.PlatonGetTransactionReceipt;
import com.alaya.protocol.core.methods.response.PlatonLog;
import com.alaya.protocol.core.methods.response.PlatonPendingTransactions;
import com.alaya.protocol.core.methods.response.PlatonSign;
import com.alaya.protocol.core.methods.response.PlatonTransaction;
import com.alaya.protocol.core.methods.response.PlatonUninstallFilter;
import com.alaya.protocol.core.methods.response.Log;
import com.alaya.protocol.core.methods.response.NetListening;
import com.alaya.protocol.core.methods.response.NetPeerCount;
import com.alaya.protocol.core.methods.response.NetVersion;
import com.alaya.protocol.core.methods.response.ShhAddToGroup;
import com.alaya.protocol.core.methods.response.ShhHasIdentity;
import com.alaya.protocol.core.methods.response.ShhMessages;
import com.alaya.protocol.core.methods.response.ShhNewFilter;
import com.alaya.protocol.core.methods.response.ShhNewGroup;
import com.alaya.protocol.core.methods.response.ShhNewIdentity;
import com.alaya.protocol.core.methods.response.ShhUninstallFilter;
import com.alaya.protocol.core.methods.response.ShhVersion;
import com.alaya.protocol.core.methods.response.Web3ClientVersion;
import com.alaya.protocol.core.methods.response.Web3Sha3;
import com.alaya.utils.Async;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import rx.Observable;

/**
 * JSON-RPC 2.0 factory implementation.
 */
public class JsonRpc2_0Web3j implements Web3j {

    public static final int DEFAULT_BLOCK_TIME = 2 * 1000;

    protected final Web3jService web3jService;
    private final JsonRpc2_0Rx web3jRx;
    private final long blockTime;

    public JsonRpc2_0Web3j(Web3jService web3jService) {
        this(web3jService, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpc2_0Web3j(
            Web3jService web3jService, long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        this.web3jService = web3jService;
        this.web3jRx = new JsonRpc2_0Rx(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
    }

    @Override
    public Request<?, Web3ClientVersion> web3ClientVersion() {
        return new Request<String, Web3ClientVersion>(
                "web3_clientVersion",
                Collections.<String>emptyList(),
                web3jService,
                Web3ClientVersion.class);
    }

    @Override
    public Request<?, Web3Sha3> web3Sha3(String data) {
        return new Request<String, Web3Sha3>(
                "web3_sha3",
                Arrays.asList(data),
                web3jService,
                Web3Sha3.class);
    }

    @Override
    public Request<?, NetVersion> netVersion() {
        return new Request<String, NetVersion>(
                "net_version",
                Collections.<String>emptyList(),
                web3jService,
                NetVersion.class);
    }

    @Override
    public Request<?, NetListening> netListening() {
        return new Request<String, NetListening>(
                "net_listening",
                Collections.<String>emptyList(),
                web3jService,
                NetListening.class);
    }

    @Override
    public Request<?, NetPeerCount> netPeerCount() {
        return new Request<String, NetPeerCount>(
                "net_peerCount",
                Collections.<String>emptyList(),
                web3jService,
                NetPeerCount.class);
    }

    @Override
    public Request<?, PlatonProtocolVersion> platonProtocolVersion() {
        return new Request<String, PlatonProtocolVersion>(
                "platon_protocolVersion",
                Collections.<String>emptyList(),
                web3jService,
                PlatonProtocolVersion.class);
    }

    @Override
    public Request<?, PlatonSyncing> platonSyncing() {
        return new Request<String, PlatonSyncing>(
                "platon_syncing",
                Collections.<String>emptyList(),
                web3jService,
                PlatonSyncing.class);
    }

    @Override
    public Request<?, PlatonGasPrice> platonGasPrice() {
        return new Request<String, PlatonGasPrice>(
                "platon_gasPrice",
                Collections.<String>emptyList(),
                web3jService,
                PlatonGasPrice.class);
    }

    @Override
    public Request<?, PlatonAccounts> platonAccounts() {
        return new Request<String, PlatonAccounts>(
                "platon_accounts",
                Collections.<String>emptyList(),
                web3jService,
                PlatonAccounts.class);
    }

    @Override
    public Request<?, PlatonBlockNumber> platonBlockNumber() {
        return new Request<String, PlatonBlockNumber>(
                "platon_blockNumber",
                Collections.<String>emptyList(),
                web3jService,
                PlatonBlockNumber.class);
    }

    @Override
    public Request<?, PlatonGetBalance> platonGetBalance(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<String, PlatonGetBalance>(
                "platon_getBalance",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetBalance.class);
    }

    @Override
    public Request<?, PlatonGetStorageAt> platonGetStorageAt(
            String address, BigInteger position, DefaultBlockParameter defaultBlockParameter) {
        return new Request<String, PlatonGetStorageAt>(
                "platon_getStorageAt",
                Arrays.asList(
                        address,
                        Numeric.encodeQuantity(position),
                        defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetStorageAt.class);
    }

    @Override
    public Request<?, PlatonGetTransactionCount> platonGetTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<String, PlatonGetTransactionCount>(
                "platon_getTransactionCount",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetTransactionCount.class);
    }

    @Override
    public Request<?, PlatonGetBlockTransactionCountByHash> platonGetBlockTransactionCountByHash(
            String blockHash) {
        return new Request<String, PlatonGetBlockTransactionCountByHash>(
                "platon_getBlockTransactionCountByHash",
                Arrays.asList(blockHash),
                web3jService,
                PlatonGetBlockTransactionCountByHash.class);
    }

    @Override
    public Request<?, PlatonGetBlockTransactionCountByNumber> platonGetBlockTransactionCountByNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<String, PlatonGetBlockTransactionCountByNumber>(
                "platon_getBlockTransactionCountByNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetBlockTransactionCountByNumber.class);
    }

    @Override
    public Request<?, PlatonGetCode> platonGetCode(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<String, PlatonGetCode>(
                "platon_getCode",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                PlatonGetCode.class);
    }

    @Override
    public Request<?, PlatonSign> platonSign(String address, String sha3HashOfDataToSign) {
        return new Request<String, PlatonSign>(
                "platon_sign",
                Arrays.asList(address, sha3HashOfDataToSign),
                web3jService,
                PlatonSign.class);
    }

    @Override
    public Request<?, PlatonSendTransaction>
    platonSendTransaction(
            com.alaya.protocol.core.methods.request.Transaction transaction) {
        return new Request<com.alaya.protocol.core.methods.request.Transaction, PlatonSendTransaction>(
                "platon_sendTransaction",
                Arrays.asList(transaction),
                web3jService,
                PlatonSendTransaction.class);
    }

    @Override
    public Request<?, PlatonSendTransaction>
    platonSendRawTransaction(
            String signedTransactionData) {
        return new Request<String, PlatonSendTransaction>(
                "platon_sendRawTransaction",
                Arrays.asList(signedTransactionData),
                web3jService,
                PlatonSendTransaction.class);
    }

    @Override
    public Request<?, PlatonCall> platonCall(
            com.alaya.protocol.core.methods.request.Transaction transaction, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "platon_call",
                Arrays.asList(transaction, defaultBlockParameter),
                web3jService,
                PlatonCall.class);
    }

    @Override
    public Request<?, PlatonEstimateGas> platonEstimateGas(com.alaya.protocol.core.methods.request.Transaction transaction) {
        return new Request<com.alaya.protocol.core.methods.request.Transaction, PlatonEstimateGas>(
                "platon_estimateGas",
                Arrays.asList(transaction),
                web3jService,
                PlatonEstimateGas.class);
    }

    @Override
    public Request<?, PlatonBlock> platonGetBlockByHash(
            String blockHash, boolean returnFullTransactionObjects) {
        return new Request<Object, PlatonBlock>(
                "platon_getBlockByHash",
                Arrays.<Object>asList(
                        blockHash,
                        returnFullTransactionObjects),
                web3jService,
                PlatonBlock.class);
    }

    @Override
    public Request<?, PlatonBlock> platonGetBlockByNumber(
            DefaultBlockParameter defaultBlockParameter,
            boolean returnFullTransactionObjects) {
        return new Request<Object, PlatonBlock>(
                "platon_getBlockByNumber",
                Arrays.<Object>asList(
                        defaultBlockParameter.getValue(),
                        returnFullTransactionObjects),
                web3jService,
                PlatonBlock.class);
    }

    @Override
    public Request<?, PlatonTransaction> platonGetTransactionByHash(String transactionHash) {
        return new Request<String, PlatonTransaction>(
                "platon_getTransactionByHash",
                Arrays.asList(transactionHash),
                web3jService,
                PlatonTransaction.class);
    }

    @Override
    public Request<?, PlatonTransaction> platonGetTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex) {
        return new Request<String, PlatonTransaction>(
                "platon_getTransactionByBlockHashAndIndex",
                Arrays.asList(
                        blockHash,
                        Numeric.encodeQuantity(transactionIndex)),
                web3jService,
                PlatonTransaction.class);
    }

    @Override
    public Request<?, PlatonTransaction> platonGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex) {
        return new Request<String, PlatonTransaction>(
                "platon_getTransactionByBlockNumberAndIndex",
                Arrays.asList(
                        defaultBlockParameter.getValue(),
                        Numeric.encodeQuantity(transactionIndex)),
                web3jService,
                PlatonTransaction.class);
    }

    @Override
    public Request<?, PlatonGetTransactionReceipt> platonGetTransactionReceipt(String transactionHash) {
        return new Request<String, PlatonGetTransactionReceipt>(
                "platon_getTransactionReceipt",
                Arrays.asList(transactionHash),
                web3jService,
                PlatonGetTransactionReceipt.class);
    }

    @Override
    public Request<?, PlatonFilter> platonNewFilter(
            com.alaya.protocol.core.methods.request.PlatonFilter ethFilter) {
        return new Request<com.alaya.protocol.core.methods.request.PlatonFilter, PlatonFilter>(
                "platon_newFilter",
                Arrays.asList(ethFilter),
                web3jService,
                PlatonFilter.class);
    }

    @Override
    public Request<?, PlatonFilter> platonNewBlockFilter() {
        return new Request<String, PlatonFilter>(
                "platon_newBlockFilter",
                Collections.<String>emptyList(),
                web3jService,
                PlatonFilter.class);
    }

    @Override
    public Request<?, PlatonFilter> platonNewPendingTransactionFilter() {
        return new Request<String, PlatonFilter>(
                "platon_newPendingTransactionFilter",
                Collections.<String>emptyList(),
                web3jService,
                PlatonFilter.class);
    }

    @Override
    public Request<?, PlatonUninstallFilter> platonUninstallFilter(BigInteger filterId) {
        return new Request<String, PlatonUninstallFilter>(
                "platon_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                PlatonUninstallFilter.class);
    }

    @Override
    public Request<?, PlatonLog> platonGetFilterChanges(BigInteger filterId) {
        return new Request<String, PlatonLog>(
                "platon_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                PlatonLog.class);
    }

    @Override
    public Request<?, PlatonLog> platonGetFilterLogs(BigInteger filterId) {
        return new Request<String, PlatonLog>(
                "platon_getFilterLogs",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                PlatonLog.class);
    }

    @Override
    public Request<?, PlatonLog> platonGetLogs(
            com.alaya.protocol.core.methods.request.PlatonFilter platonFilter) {
        return new Request<com.alaya.protocol.core.methods.request.PlatonFilter, PlatonLog>(
                "platon_getLogs",
                Arrays.asList(platonFilter),
                web3jService,
                PlatonLog.class);
    }

    @Override
    public Request<?, PlatonPendingTransactions> platonPendingTx() {
        return new Request<>(
                "eth_pendingTransactions",
                Collections.<String>emptyList(),
                web3jService,
                PlatonPendingTransactions.class);

    }

    @Override
    public Request<?, DbPutString> dbPutString(
            String databaseName, String keyName, String stringToStore) {
        return new Request<String, DbPutString>(
                "db_putString",
                Arrays.asList(databaseName, keyName, stringToStore),
                web3jService,
                DbPutString.class);
    }

    @Override
    public Request<?, DbGetString> dbGetString(String databaseName, String keyName) {
        return new Request<String, DbGetString>(
                "db_getString",
                Arrays.asList(databaseName, keyName),
                web3jService,
                DbGetString.class);
    }

    @Override
    public Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore) {
        return new Request<String, DbPutHex>(
                "db_putHex",
                Arrays.asList(databaseName, keyName, dataToStore),
                web3jService,
                DbPutHex.class);
    }

    @Override
    public Request<?, DbGetHex> dbGetHex(String databaseName, String keyName) {
        return new Request<String, DbGetHex>(
                "db_getHex",
                Arrays.asList(databaseName, keyName),
                web3jService,
                DbGetHex.class);
    }

    @Override
    public Request<?, com.alaya.protocol.core.methods.response.ShhPost> shhPost(com.alaya.protocol.core.methods.request.ShhPost shhPost) {
        return new Request<com.alaya.protocol.core.methods.request.ShhPost, com.alaya.protocol.core.methods.response.ShhPost>(
                "shh_post",
                Arrays.asList(shhPost),
                web3jService,
                com.alaya.protocol.core.methods.response.ShhPost.class);
    }

    @Override
    public Request<?, ShhVersion> shhVersion() {
        return new Request<String, ShhVersion>(
                "shh_version",
                Collections.<String>emptyList(),
                web3jService,
                ShhVersion.class);
    }

    @Override
    public Request<?, ShhNewIdentity> shhNewIdentity() {
        return new Request<String, ShhNewIdentity>(
                "shh_newIdentity",
                Collections.<String>emptyList(),
                web3jService,
                ShhNewIdentity.class);
    }

    @Override
    public Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress) {
        return new Request<String, ShhHasIdentity>(
                "shh_hasIdentity",
                Arrays.asList(identityAddress),
                web3jService,
                ShhHasIdentity.class);
    }

    @Override
    public Request<?, ShhNewGroup> shhNewGroup() {
        return new Request<String, ShhNewGroup>(
                "shh_newGroup",
                Collections.<String>emptyList(),
                web3jService,
                ShhNewGroup.class);
    }

    @Override
    public Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress) {
        return new Request<String, ShhAddToGroup>(
                "shh_addToGroup",
                Arrays.asList(identityAddress),
                web3jService,
                ShhAddToGroup.class);
    }

    @Override
    public Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter) {
        return new Request<ShhFilter, ShhNewFilter>(
                "shh_newFilter",
                Arrays.asList(shhFilter),
                web3jService,
                ShhNewFilter.class);
    }

    @Override
    public Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId) {
        return new Request<String, ShhUninstallFilter>(
                "shh_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhUninstallFilter.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId) {
        return new Request<String, ShhMessages>(
                "shh_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhMessages.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetMessages(BigInteger filterId) {
        return new Request<String, ShhMessages>(
                "shh_getMessages",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhMessages.class);
    }

    @Override
    public Observable<String> ethBlockHashObservable() {
        return web3jRx.ethBlockHashObservable(blockTime);
    }

    @Override
    public Observable<String> ethPendingTransactionHashObservable() {
        return web3jRx.ethPendingTransactionHashObservable(blockTime);
    }

    @Override
    public Observable<Log> ethLogObservable(
            com.alaya.protocol.core.methods.request.PlatonFilter ethFilter) {
        return web3jRx.ethLogObservable(ethFilter, blockTime);
    }

    @Override
    public Observable<com.alaya.protocol.core.methods.response.Transaction>
    transactionObservable() {
        return web3jRx.transactionObservable(blockTime);
    }

    @Override
    public Observable<com.alaya.protocol.core.methods.response.Transaction>
    pendingTransactionObservable() {
        return web3jRx.pendingTransactionObservable(blockTime);
    }

    @Override
    public Observable<PlatonBlock> blockObservable(boolean fullTransactionObjects) {
        return web3jRx.blockObservable(fullTransactionObjects, blockTime);
    }

    @Override
    public Observable<PlatonBlock> replayBlocksObservable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock,
            boolean fullTransactionObjects) {
        return web3jRx.replayBlocksObservable(startBlock, endBlock, fullTransactionObjects);
    }

    @Override
    public Observable<PlatonBlock> replayBlocksObservable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock,
            boolean fullTransactionObjects, boolean ascending) {
        return web3jRx.replayBlocksObservable(startBlock, endBlock,
                fullTransactionObjects, ascending);
    }

    @Override
    public Observable<com.alaya.protocol.core.methods.response.Transaction>
    replayTransactionsObservable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        return web3jRx.replayTransactionsObservable(startBlock, endBlock);
    }

    @Override
    public Observable<PlatonBlock> catchUpToLatestBlockObservable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects,
            Observable<PlatonBlock> onCompleteObservable) {
        return web3jRx.catchUpToLatestBlockObservable(
                startBlock, fullTransactionObjects, onCompleteObservable);
    }

    @Override
    public Observable<PlatonBlock> catchUpToLatestBlockObservable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return web3jRx.catchUpToLatestBlockObservable(startBlock, fullTransactionObjects);
    }

    @Override
    public Observable<com.alaya.protocol.core.methods.response.Transaction>
    catchUpToLatestTransactionObservable(DefaultBlockParameter startBlock) {
        return web3jRx.catchUpToLatestTransactionObservable(startBlock);
    }

    @Override
    public Observable<PlatonBlock> catchUpToLatestAndSubscribeToNewBlocksObservable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return web3jRx.catchUpToLatestAndSubscribeToNewBlocksObservable(
                startBlock, fullTransactionObjects, blockTime);
    }

    @Override
    public Observable<com.alaya.protocol.core.methods.response.Transaction>
    catchUpToLatestAndSubscribeToNewTransactionsObservable(
            DefaultBlockParameter startBlock) {
        return web3jRx.catchUpToLatestAndSubscribeToNewTransactionsObservable(
                startBlock, blockTime);
    }

    @Override
    public Request<?, PlatonEvidences> platonEvidences() {
        return new Request<>(
                "platon_evidences",
                Collections.<String>emptyList(),
                web3jService,
                PlatonEvidences.class);
    }

    @Override
    public Request<?, AdminProgramVersion> getProgramVersion() {
        return new Request<>(
                "admin_getProgramVersion",
                Collections.<String>emptyList(),
                web3jService,
                AdminProgramVersion.class);
    }

    @Override
    public Request<?, AdminSchnorrNIZKProve> getSchnorrNIZKProve() {
        return new Request<>(
                "admin_getSchnorrNIZKProve",
                Collections.<String>emptyList(),
                web3jService,
                AdminSchnorrNIZKProve.class);
    }

    @Override
    public Request<?, DebugEconomicConfig> getEconomicConfig() {
        return new Request<>(
                "debug_economicConfig",
                Collections.<String>emptyList(),
                web3jService,
                DebugEconomicConfig.class);
    }
}
