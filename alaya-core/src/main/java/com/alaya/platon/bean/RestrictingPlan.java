package com.alaya.platon.bean;

import com.alaya.utils.Numeric;
import com.alibaba.fastjson.annotation.JSONField;

import com.alaya.platon.CustomType;
import com.alaya.rlp.RlpList;
import com.alaya.rlp.RlpString;
import com.alaya.rlp.RlpType;

import java.math.BigInteger;

public class RestrictingPlan extends CustomType {

    /**
     * 表示结算周期的倍数。与每个结算周期出块数的乘积表示在目标区块高度上释放锁定的资金。如果 account 是激励池地址，
     * 那么 period 值是 120（即，30*4） 的倍数。另外，period * 每周期的区块数至少要大于最高不可逆区块高度
     */
    @JSONField(name = "Epoch")
    private BigInteger epoch;

    /**
     * 表示目标区块上待释放的金额
     */
    @JSONField(name = "Amount")
    private BigInteger amount;

    public RestrictingPlan(BigInteger epoch, BigInteger amount) {
        this.epoch = epoch;
        this.amount = amount;
    }

    public BigInteger getEpoch() {
        return epoch;
    }

    public void setEpoch(BigInteger epoch) {
        this.epoch = epoch;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = Numeric.decodeQuantity(amount);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getTypeAsString() {
        return null;
    }

    @Override
    public RlpType getRlpEncodeData() {
        return new RlpList(RlpString.create(epoch), RlpString.create(amount));
    }
}
