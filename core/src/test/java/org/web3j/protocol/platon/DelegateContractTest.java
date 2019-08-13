package org.web3j.protocol.platon;

import org.junit.Before;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.encoders.HexEncoder;
import org.web3j.abi.PlatOnTypeDecoder;
import org.web3j.abi.PlatOnTypeEncoder;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.bean.Delegation;
import org.web3j.platon.bean.DelegationIdInfo;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.tx.gas.DefaultWasmGasProvider;
import org.web3j.utils.Numeric;
import org.web3j.utils.PlatOnUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DelegateContractTest {

    private String nodeId = "1f3a8672348ff6b789e416762ad53e69063138b8eb4d8780101658f24b2369f1a8e09499226b467d8bc0c4e03e1dc903df857eeb3c67733d21b6aaee2840e429";
    private String delegateAddress = "0x493301712671Ada506ba6Ca7891F436D29185821";

    private Web3j web3j = Web3jFactory.build(new HttpService("http://10.10.8.200:6789"));

    private Credentials credentials;

    private DelegateContract delegateContract;

    @Before
    public void init() {
        credentials = Credentials.create("0xa11859ce23effc663a9460e332ca09bd812acc390497f8dc7542b6938e13f8d7");

        delegateContract = DelegateContract.load(web3j,
                credentials,
                new DefaultWasmGasProvider(), "102");
    }

    @Test
    public void decode() throws UnsupportedEncodingException {
        String text = "f856838203ec8180b842b8401f3a8672348ff6b789e416762ad53e69063138b8eb4d8780101658f24b2369f1a8e09499226b467d8bc0c4e03e1dc903df857eeb3c67733d21b6aaee2840e4298b8ad3c21bcecceda1000000";

        RlpList rlpList = RlpDecoder.decode(Hex.decode(text));

        RlpList rl = (RlpList) rlpList.getValues().get(0);

        RlpString rlpType = (RlpString) rl.getValues().get(0);

        RlpString rlpTyp1 = (RlpString) rl.getValues().get(1);
        RlpString rlpTyp2 = (RlpString) rl.getValues().get(2);
        RlpString rlpTyp3 = (RlpString) rl.getValues().get(3);

        RlpList rlps = RlpDecoder.decode(rlpType.getBytes());
        BigInteger bigInteger = new BigInteger(((RlpString)rlps.getValues().get(0)).getBytes());

        RlpList rlps1 = RlpDecoder.decode(rlpTyp1.getBytes());
        BigInteger bigInteger1 = new BigInteger(1,((RlpString)rlps1.getValues().get(0)).getBytes());

        RlpList rlps2 = RlpDecoder.decode(rlpTyp2.getBytes());
        String nodeId = Numeric.toHexString(((RlpString)rlps2.getValues().get(0)).getBytes());

        System.out.println(nodeId);

    }

    @Test
    public void delegate() {

        try {
            BaseResponse baseResponse = delegateContract.delegate(nodeId, StakingAmountType.FREE_AMOUNT_TYPE, new BigInteger("1000000000000000000000000")).send();
            System.out.println(baseResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void unDelegate() {

        try {
            BaseResponse baseResponse = delegateContract.unDelegate(nodeId, BigInteger.valueOf(2360), new BigInteger("1000000000000000000000000")).send();
            System.out.println(baseResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDelegateInfo() {

        try {
            BaseResponse<Delegation> baseResponse = delegateContract.getDelegateInfo(nodeId, delegateAddress, BigInteger.valueOf(2360)).send();
            System.out.println(baseResponse.data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRelatedListByDelAddr() {
        try {
            BaseResponse<List<DelegationIdInfo>> baseResponse = delegateContract.getRelatedListByDelAddr(delegateAddress).send();
            DelegationIdInfo delegationIdInfo = baseResponse.data.get(0);
            System.out.println(delegationIdInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendTransaction(String privateKey, String from, String toAddress, BigDecimal amount, long gasPrice, long gasLimit) {

        BigInteger GAS_PRICE = BigInteger.valueOf(gasPrice);
        BigInteger GAS_LIMIT = BigInteger.valueOf(gasLimit);

        Credentials credentials = Credentials.create(privateKey);

        try {

            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(Numeric.hexStringToByteArray(PlatOnTypeEncoder.encode(new Int64(0)))));
            String txType = Hex.toHexString(RlpEncoder.encode(new RlpList(result)));

            BigInteger nonce = web3j.platonGetTransactionCount(from, DefaultBlockParameterName.LATEST).send().getTransactionCount();

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, GAS_PRICE, GAS_LIMIT, toAddress, amount.toBigInteger(),
                    txType);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, new Byte("100"), credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            PlatonSendTransaction transaction = web3j.platonSendRawTransaction(hexValue).send();

            return transaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
