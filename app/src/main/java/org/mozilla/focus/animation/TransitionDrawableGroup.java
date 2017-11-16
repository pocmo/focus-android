/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.animation;

import android.graphics.drawable.TransitionDrawable;

/**
 * A class to allow {@link TransitionDrawable}'s animations to play together: similar to {@link android.animation.AnimatorSet}.
 */
public class TransitionDrawableGroup {
    private final TransitionDrawable[] transitionDrawables;

    private boolean transitioned;

    public TransitionDrawableGroup(TransitionDrawable... transitionDrawables) {
        this.transitionDrawables = transitionDrawables;
    }

    public synchronized void startTransition(final int durationMillis) {
        if (transitioned) {
            // We already played the start transition. Let's not play it again until it is reset.
            return;
        }

        // In theory, there are no guarantees these will play together.
        // In practice, I haven't noticed any problems.
        for (final TransitionDrawable transitionDrawable : transitionDrawables) {
            transitionDrawable.startTransition(durationMillis);
        }

        transitioned = true;
    }

    public synchronized void resetTransition() {
        for (final TransitionDrawable transitionDrawable : transitionDrawables) {
            transitionDrawable.resetTransition();
        }

        transitioned = false;
    }
}
