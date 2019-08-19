package org.web3j.platon.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigInteger;
import java.util.List;

public class RestrictingItem {
    /**
     * 锁仓余额
     */
    private BigInteger balance;
    /**
     * 惩罚金额
     */
    private BigInteger slash;
    /**
     * 质押/抵押金额
     */
    private BigInteger pledge;
    /**
     * 欠释放金额
     */
    private BigInteger debt;
    /**
     * 锁仓分录信息
     */
    @JSONField(name = "Entry")
    private List<RestrictingInfo> info;

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public BigInteger getSlash() {
        return slash;
    }

    public void setSlash(BigInteger slash) {
        this.slash = slash;
    }

    public BigInteger getPledge() {
        return pledge;
    }

    public void setPledge(BigInteger pledge) {
        this.pledge = pledge;
    }

    public BigInteger getDebt() {
        return debt;
    }

    public void setDebt(BigInteger debt) {
        this.debt = debt;
    }

    public List<RestrictingInfo> getInfo() {
        return info;
    }

    public void setInfo(List<RestrictingInfo> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "RestrictingItem{" +
                "balance=" + balance +
                ", slash=" + slash +
                ", pledge=" + pledge +
                ", debt=" + debt +
                ", info=" + info +
                '}';
    }
}
