/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.sign;

import java.util.Collection;

public class SearchingAnimation {

    private final int animations;

    private final int animationsPerSecond;

    private final Collection<SignLayout> searchingLayouts;

    public SearchingAnimation(final int animations, final int animationsPerSecond, final Collection<SignLayout> searchingLayouts) {
        this.animations = animations;
        this.animationsPerSecond = animationsPerSecond;
        this.searchingLayouts = searchingLayouts;
    }

    public Collection<SignLayout> getSearchingLayouts() {
        return searchingLayouts;
    }

    public int getAnimations() {
        return animations;
    }

    public int getAnimationsPerSecond() {
        return animationsPerSecond;
    }
}
