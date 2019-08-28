package org.web3j.protocol.platon;

import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.DoubleSignType;
import org.web3j.platon.DuplicateSignType;
import org.web3j.platon.contracts.SlashContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonBlock;
import org.web3j.protocol.http.HttpService;

public class SlashContractTest {
    private Web3j web3j = Web3jFactory.build(new HttpService("http://192.168.120.76:6794"));

    private String address = "0x493301712671Ada506ba6Ca7891F436D29185821";
    private String benifitAddress = "0x12c171900f010b17e969702efa044d077e868082";
    private String data = "{\n" +
            "\t\"duplicate_prepare\": [{\n" +
            "\t\t\"VoteA\": {\n" +
            "\t\t\t\"timestamp\": 0,\n" +
            "\t\t\t\"block_hash\": \"0x0a0409021f020b080a16070609071c141f19011d090b091303121e1802130407\",\n" +
            "\t\t\t\"block_number\": 2,\n" +
            "\t\t\t\"validator_index\": 1,\n" +
            "\t\t\t\"validator_address\": \"0x120b77ab712589ebd42d69003893ef962cc52832\",\n" +
            "\t\t\t\"signature\": \"0xa65e16b3bc4862fdd893eaaaaecf1e415cdc2c8a08e4bbb1f6b2a1f4bf4e2d0c0ec27857da86a5f3150b32bee75322073cec320e51fe0a123cc4238ee4155bf001\"\n" +
            "\t\t},\n" +
            "\t\t\"VoteB\": {\n" +
            "\t\t\t\"timestamp\": 0,\n" +
            "\t\t\t\"block_hash\": \"0x18030d1e01071b1d071a12151e100a091f060801031917161e0a0d0f02161d0e\",\n" +
            "\t\t\t\"block_number\": 2,\n" +
            "\t\t\t\"validator_index\": 1,\n" +
            "\t\t\t\"validator_address\": \"0x120b77ab712589ebd42d69003893ef962cc52832\",\n" +
            "\t\t\t\"signature\": \"0x9126f9a339c8c4a873efc397062d67e9e9109895cd9da0d09a010d5f5ebbc6e76d285f7d87f801850c8552234101b651c8b7601b4ea077328c27e4f86d66a1bf00\"\n" +
            "\t\t}\n" +
            "\t}],\n" +
            "\t\"duplicate_viewchange\": [],\n" +
            "\t\"timestamp_viewchange\": []\n" +
            "}";

    private SlashContract slashContract;

    private Credentials credentials;

    @Before
    public void init() {
        credentials = Credentials.create("0xa56f68ca7aa51c24916b9fff027708f856650f9ff36cc3c8da308040ebcc7867");

        slashContract = SlashContract.load(web3j,
                credentials, "100");
    }

    @Test
    public void reportDuplicateSign() {
        try {
            System.out.println(web3j.platonGetBlockByNumber(DefaultBlockParameterName.LATEST,false).send().getBlock().getNumberRaw());
            BaseResponse baseResponse = slashContract.reportDoubleSign(data).send();
            System.out.println(baseResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkDuplicateSign() {

        try {
            PlatonBlock platonBlock = web3j.platonGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            BaseResponse baseResponse = slashContract.checkDoubleSign(DuplicateSignType.PREPARE_BLOCK, "0x4F8eb0B21eb8F16C80A9B7D728EA473b8676Cbb3", platonBlock.getBlock().getNumber()).send();
            System.out.println(baseResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
