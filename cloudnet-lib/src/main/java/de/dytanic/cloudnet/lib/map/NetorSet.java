package de.dytanic.cloudnet.lib.map;

final class NetorSet<VF, VS> {

    private VF valueF;
    private VS valueS;

    public NetorSet(final VF valueF, final VS valueS) {
        this.valueF = valueF;
        this.valueS = valueS;
    }

    public VF getFirstValue() {
        return valueF;
    }

    public VS getSecondValue() {
        return valueS;
    }

    public void updateFirst(final VF value) {
        this.valueF = value;
    }

    public void updateSecond(final VS value) {
        this.valueS = value;
    }

}
