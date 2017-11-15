package cn.edu.nju.entity.sign;

public class NonTerminalSign extends Sign {

    /**
     * 非终结符
     */
    char nonTerminalSign;

    public NonTerminalSign(char nonTerminalSign) {
        this.signType = SignType.NON_TERMINAL;
        this.nonTerminalSign = nonTerminalSign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonTerminalSign that = (NonTerminalSign) o;

        return nonTerminalSign == that.nonTerminalSign;
    }

    @Override
    public int hashCode() {
        return (int) nonTerminalSign;
    }

    public char getNonTerminalSign() {
        return nonTerminalSign;
    }
}
