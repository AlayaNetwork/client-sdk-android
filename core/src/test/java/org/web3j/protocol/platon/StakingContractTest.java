package org.web3j.protocol.platon;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;

import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.SampleKeys;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.bean.Node;
import org.web3j.platon.bean.RestrictingPlan;
import org.web3j.platon.contracts.RestrictingPlanContract;
import org.web3j.platon.contracts.StakingContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultWasmGasProvider;
import org.web3j.utils.Numeric;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StakingContractTest {

    private static final int OFFSET_SHORT_ITEM = 0x80;
    private static final int OFFSET_LONG_ITEM = 0xb7;
    private static final int SIZE_THRESHOLD = 56;

    private Web3j web3j = Web3jFactory.build(new HttpService("http://10.10.8.118:6789"));

    private StakingContract stakingContract;
    private RestrictingPlanContract restrictingPlanContract;

    private String nodeId = "0x1f3a8672348ff6b789e416762ad53e69063138b8eb4d8780101658f24b2369f1a8e09499226b467d8bc0c4e03e1dc903df857eeb3c67733d21b6aaee2840e429";
    private String benifitAddress = "0x89dcade1e353984f4085ef99d3e24b5667a93aeb";
    private String nodeName = "nodeName";
    private String websites = "www.baidu.com";
    private String details = "details";
    private String text = "0xf9073c01b907355b7b224e6f64654964223a223166336138363732333438666636623738396534313637363261643533653639303633313338623865623464383738303130313635386632346232333639663161386530393439393232366234363764386263306334653033653164633930336466383537656562336336373733336432316236616165653238343232333334222c225374616b696e6741646472657373223a22307830303030303037343063653331623366616332306461633337396462323433303231613531653830222c2242656e6966697441646472657373223a22307831303030303030303030303030303030303030303030303030303030303030303030303030303032222c225374616b696e675478496e646578223a302c2250726f6365737356657273696f6e223a302c225374616b696e67426c6f636b4e756d223a322c22536861726573223a3235362c2245787465726e616c4964223a227878636363636464646464646464222c224e6f64654e616d65223a224920416d2030222c2257656273697465223a227777772e62616964752e636f6d222c2244657461696c73223a227468697320697320206261696475207e7e222c2256616c696461746f725465726d223a327d2c7b224e6f64654964223a223266336138363732333438666636623738396534313637363261643533653639303633313338623865623464383738303130313635386632346232333639663161386530393439393232366234363764386263306334653033653164633930336466383537656562336336373733336432316236616165653238343335343636222c225374616b696e6741646472657373223a22307837343063653331623366616332306461633337396462323433303231613531653830343434353535222c2242656e6966697441646472657373223a22307831303030303030303030303030303030303030303030303030303030303030303030303030303032222c225374616b696e675478496e646578223a312c2250726f6365737356657273696f6e223a312c225374616b696e67426c6f636b4e756d223a332c22536861726573223a3235362c2245787465726e616c4964223a227878636363636464646464646464222c224e6f64654e616d65223a224920416d2031222c2257656273697465223a227777772e62616964752e636f6d222c2244657461696c73223a227468697320697320206261696475207e7e222c2256616c696461746f725465726d223a327d2c7b224e6f64654964223a223366336138363732333438666636623738396534313637363261643533653639303633313338623865623464383738303130313635386632346232333639663161386530393439393232366234363764386263306334653033653164633930336466383537656562336336373733336432316236616165653238353434383738222c225374616b696e6741646472657373223a22307830303030303037343063653331623366616332306461633337396462323433303231613531653830222c2242656e6966697441646472657373223a22307831303030303030303030303030303030303030303030303030303030303030303030303030303032222c225374616b696e675478496e646578223a322c2250726f6365737356657273696f6e223a342c225374616b696e67426c6f636b4e756d223a342c22536861726573223a3235362c2245787465726e616c4964223a227878636363636464646464646464222c224e6f64654e616d65223a224920416d2032222c2257656273697465223a227777772e62616964752e636f6d222c2244657461696c73223a227468697320697320206261696475207e7e222c2256616c696461746f725465726d223a327d2c7b224e6f64654964223a223366336138363732333438666636623738396534313637363261643533653639303633313338623865623464383738303130313635386632346232333639663161386530393439393232366234363764386263306334653033653164633930336466383537656562336336373733336432316236616165653238353634363436222c225374616b696e6741646472657373223a22307830303030303037343063653331623366616332306461633337396462323433303231613531653830222c2242656e6966697441646472657373223a22307831303030303030303030303030303030303030303030303030303030303030303030303030303032222c225374616b696e675478496e646578223a332c2250726f6365737356657273696f6e223a392c225374616b696e67426c6f636b4e756d223a352c22536861726573223a3235362c2245787465726e616c4964223a227878636363636464646464646464222c224e6f64654e616d65223a224920416d2033222c2257656273697465223a227777772e62616964752e636f6d222c2244657461696c73223a227468697320697320206261696475207e7e222c2256616c696461746f725465726d223a327d5d826f6b";
    private String text1 = "0x7b22537461747573223a747275652c2244617461223a225b7b5c224e6f646549645c223a5c2231663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383432323333345c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a302c5c2250726f6365737356657273696f6e5c223a302c5c225374616b696e67426c6f636b4e756d5c223a322c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c2278786363636364646464646464645c222c5c224e6f64654e616d655c223a5c224920416d20305c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2232663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383433353436365c222c5c225374616b696e67416464726573735c223a5c223078373430636533316233666163323064616333373964623234333032316135316538303434343535355c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a312c5c2250726f6365737356657273696f6e5c223a312c5c225374616b696e67426c6f636b4e756d5c223a332c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c2278786363636364646464646464645c222c5c224e6f64654e616d655c223a5c224920416d20315c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2233663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383534343837385c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a322c5c2250726f6365737356657273696f6e5c223a342c5c225374616b696e67426c6f636b4e756d5c223a342c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c2278786363636364646464646464645c222c5c224e6f64654e616d655c223a5c224920416d20325c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2233663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383536343634365c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a332c5c2250726f6365737356657273696f6e5c223a392c5c225374616b696e67426c6f636b4e756d5c223a352c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c2278786363636364646464646464645c222c5c224e6f64654e616d655c223a5c224920416d20335c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d5d222c224572724d7367223a226f6b227d";
    private String text2 = "0x7b22537461747573223a747275652c2244617461223a225b7b5c224e6f646549645c223a5c2231663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383432323333345c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a302c5c2250726f6365737356657273696f6e5c223a302c5c225374616b696e67426c6f636b4e756d5c223a322c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c22e4b8ade69687efbc8ce68891e698afe4b8ade69687e4b8ade696875c222c5c224e6f64654e616d655c223a5c22e68891e698af2020e789b9e6ae8ae7aca6e58fb7efbc9a20e298845c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2232663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383433353436365c222c5c225374616b696e67416464726573735c223a5c223078373430636533316233666163323064616333373964623234333032316135316538303434343535355c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a312c5c2250726f6365737356657273696f6e5c223a312c5c225374616b696e67426c6f636b4e756d5c223a332c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c22e4b8ade69687efbc8ce68891e698afe4b8ade69687e4b8ade696875c222c5c224e6f64654e616d655c223a5c22e68891e698af2020e789b9e6ae8ae7aca6e58fb7efbc9a20e298855c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2233663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383534343837385c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a322c5c2250726f6365737356657273696f6e5c223a342c5c225374616b696e67426c6f636b4e756d5c223a342c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c22e4b8ade69687efbc8ce68891e698afe4b8ade69687e4b8ade696875c222c5c224e6f64654e616d655c223a5c22e68891e698af2020e789b9e6ae8ae7aca6e58fb7efbc9a20e2988e5c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d2c7b5c224e6f646549645c223a5c2233663361383637323334386666366237383965343136373632616435336536393036333133386238656234643837383031303136353866323462323336396631613865303934393932323662343637643862633063346530336531646339303364663835376565623363363737333364323162366161656532383536343634365c222c5c225374616b696e67416464726573735c223a5c223078303030303030373430636533316233666163323064616333373964623234333032316135316538305c222c5c2242656e69666974416464726573735c223a5c223078313030303030303030303030303030303030303030303030303030303030303030303030303030325c222c5c225374616b696e675478496e6465785c223a332c5c2250726f6365737356657273696f6e5c223a392c5c225374616b696e67426c6f636b4e756d5c223a352c5c225368617265735c223a3235362c5c2245787465726e616c49645c223a5c22e4b8ade69687efbc8ce68891e698afe4b8ade69687e4b8ade696875c222c5c224e6f64654e616d655c223a5c22e68891e698af2020e789b9e6ae8ae7aca6e58fb7efbc9a20e298bb5c222c5c22576562736974655c223a5c227777772e62616964752e636f6d5c222c5c2244657461696c735c223a5c227468697320697320206261696475207e7e5c222c5c2256616c696461746f725465726d5c223a327d5d222c224572724d7367223a226f6b227d";
    private Credentials credentials;

    @Before
    public void init() {

        credentials = Credentials.create(SampleKeys.KEY_PAIR);

        stakingContract = StakingContract.load(
                web3j,
                credentials,
                new DefaultWasmGasProvider(), "102");

        restrictingPlanContract = RestrictingPlanContract.load(
                web3j,
                credentials,
                new DefaultWasmGasProvider(), "102");
    }

    @Test
    public void createRestrictingPlan() {

        List<RestrictingPlan> restrictingPlans = new ArrayList<>();
        restrictingPlans.add(new RestrictingPlan(BigInteger.valueOf(1), BigInteger.valueOf(1)));
        restrictingPlans.add(new RestrictingPlan(BigInteger.valueOf(2), BigInteger.valueOf(2)));

        try {
            restrictingPlanContract.createRestrictingPlan(benifitAddress, restrictingPlans).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void staking() {

//        try {
//            BaseResponse baseResponse = stakingContract.staking(nodeId, BigInteger.valueOf(10), StakingAmountType.FREE_AMOUNT_TYPE,benifitAddress,nodeId,nodeName,websites,details).send();
//            System.out.println(baseResponse.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        int a = 1000;
//        Uint32 uint32 = new Uint32(a);
//
//        String rlpString = PlatOnTypeEncoder.encode(uint32);
//
//        byte[] bytes = RlpEncoder.encode(RlpString.create(65535));
////        Uint32 uint321 = TypeDecoder.de(rlpString.toString(),Uint32.class);
//
//        System.out.println(Hex.toHexString(bytes));
//
//        byte[] payload = Numeric.hexStringToByteArray(benifitAddress);
//
//       RlpType rlpType =  RlpString.create(RlpEncoder.encode(RlpString.create(payload)));
//
//        RlpList rlpList = RlpDecoder.decode(payload);
//
//        byte[] bytes = encodeElement(benifitAddress.getBytes());
//
//        System.out.println(Hex.toHexString(bytes));


//        RLPList rlpList = PlatOnUtil.invokeDecode(Numeric.hexStringToByteArray(text1));
//
//        RLPList rlp = (RLPList) rlpList.get(0);
//
//        RLPItem rlpItem = (RLPItem) rlp.get(0);
//        String item0 = new String(rlpItem.getRLPData());
//        RLPItem rlpItem1 = (RLPItem) rlp.get(1);
//        String rlpItem1Text = new String(rlpItem1.getRLPData());
//        List<Node> nodes = JSONUtil.parseArray(rlpItem1Text, Node.class);
//        List<Node> nodeList = JSONUtil.parseObject(rlpItem1Text, List.class);
//        Node node = JSONUtil.parseObject((Map<String, String>) nodeList.get(0), Node.class);
//        RLPItem rlpItem2 = (RLPItem) rlp.get(2);

        String json = new String(Numeric.hexStringToByteArray(text2));

//        BaseResponse<List<Node>> baseResponse = JSONObject.parseObject(json,new TypeReference<BaseResponse<List<Node>>>(){});
        BaseResponse<List<Node>> baseResponse = parseListResult(json,Node.class);

//        List<Node> nodeList = JSONUtil.parseArray(data,Node.class);

        System.out.println(json);
    }

    private static <T> BaseResponse<List<T>> parseListResult(String json, Class<T> clazz) {
        ParameterizedTypeImpl inner = new ParameterizedTypeImpl(new Type[]{clazz}, null, List.class);
        ParameterizedTypeImpl outer = new ParameterizedTypeImpl(new Type[]{inner}, null, BaseResponse.class);
        return JSONObject.parseObject(json, outer);
    }

}
