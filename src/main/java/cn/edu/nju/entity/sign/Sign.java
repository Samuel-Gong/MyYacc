package cn.edu.nju.entity.sign;

/**
 * 终结符和非终结符的父类
 */
public abstract class Sign {

    /**
     * 符号类型
     */
    protected SignType signType;

    public SignType getSignType() {
        return signType;
    }

    public void setSignType(SignType signType) {
        this.signType = signType;
    }
}
