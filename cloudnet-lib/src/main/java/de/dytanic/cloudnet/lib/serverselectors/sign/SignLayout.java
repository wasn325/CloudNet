package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 26.05.2017.
 */
public class SignLayout implements Nameable {

    private final String name;
    private final String[] signLayout;
    private final String blockName;
    private final int subId;
    /**
     * blockIds are not supported in all versions, use {@link SignLayout#blockName} instead
     */
    @Deprecated
    int blockId;

    public SignLayout(final String name, final String[] signLayout, final int blockId, final String blockName, final int subId) {
        this.name = name;
        this.signLayout = signLayout;
        this.blockId = blockId;
        this.blockName = blockName;
        this.subId = subId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getSubId() {
        return subId;
    }

    public String getBlockName() {
        return blockName;
    }

    public String[] getSignLayout() {
        return signLayout;
    }
}
