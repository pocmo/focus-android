/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus

import android.content.Context
import android.util.AttributeSet
import mozilla.components.browser.search.SearchEngineManager
import mozilla.components.browser.search.provider.AssetsSearchEngineProvider
import mozilla.components.browser.search.provider.localization.LocaleSearchLocalizationProvider
import mozilla.components.browser.session.SessionManager
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.EngineView
import org.mozilla.focus.search.BingSearchEngineFilter
import org.mozilla.focus.search.CustomSearchEngineProvider
import org.mozilla.focus.search.HiddenSearchEngineFilter

/**
 * Helper object for lazily initializing components.
 */
class Components() {
    val searchEngineManager by lazy {
        val assetsProvider = AssetsSearchEngineProvider(
                LocaleSearchLocalizationProvider(),
                filters = listOf(BingSearchEngineFilter(), HiddenSearchEngineFilter()),
                additionalIdentifiers = listOf("ddg"))

        val customProvider = CustomSearchEngineProvider()

        SearchEngineManager(listOf(assetsProvider, customProvider))
    }

    val sessionManager by lazy {
        SessionManager(DummyEngine())
    }
}

/**
 * We are not using an "Engine" implementation yet. Therefore we create this dummy that we pass to
 * the <code>SessionManager</code> for now.
 */
private class DummyEngine : Engine {
    override fun createSession(): EngineSession {
        throw NotImplementedError()
    }

    override fun createView(context: Context, attrs: AttributeSet?): EngineView {
        throw NotImplementedError()
    }

    override fun name(): String {
        throw NotImplementedError()
    }
}