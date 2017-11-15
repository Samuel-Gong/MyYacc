package cn.edu.nju.entity.sign;

public class TerminalSign extends Sign {

    /**
     * 终结符
     */
    private String terminalSign;

    public TerminalSign(String terminalSign) {
        this.signType = SignType.TERMINAL;
        this.terminalSign = terminalSign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerminalSign that = (TerminalSign) o;

        return terminalSign.equals(that.terminalSign);
    }

    @Override
    public int hashCode() {
        return terminalSign.hashCode();
    }

    public String getTerminalSign() {
        return terminalSign;
    }
}
