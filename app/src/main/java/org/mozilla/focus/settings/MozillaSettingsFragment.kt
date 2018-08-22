/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import mozilla.components.browser.session.Session
import org.mozilla.focus.R
import org.mozilla.focus.browser.LocalizedContent
import org.mozilla.focus.ext.components
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.AppConstants
import org.mozilla.focus.utils.SupportUtils

class MozillaSettingsFragment : BaseSettingsFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.mozilla_settings)
    }

    override fun onResume() {
        super.onResume()

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Update title and icons when returning to fragments.
        val updater = activity as BaseSettingsFragment.ActionBarUpdater
        updater.updateTitle(R.string.preference_category_mozilla)
        updater.updateIcon(R.drawable.ic_back)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        // AppCompatActivity has a Toolbar that is used as the ActionBar, and it conflicts with the ActionBar
        // used by PreferenceScreen to create the headers (with title, back navigation), so we wrap all these
        // "preference screens" into separate activities.
        when (preference.key) {
            resources.getString(R.string.pref_key_about) -> run {
                activity?.apply {
                    val session = Session(LocalizedContent.URL_ABOUT, Session.Source.MENU)
                    components.sessionManager.add(session)
                    finish()
                }
            }
            resources.getString(R.string.pref_key_help) -> run {
                activity?.apply {
                    val session = Session(SupportUtils.HELP_URL, Session.Source.MENU)
                    components.sessionManager.add(session)
                    finish()
                }
            }
            resources.getString(R.string.pref_key_rights) -> run {
                activity?.apply {
                    val session = Session(LocalizedContent.URL_RIGHTS, Session.Source.MENU)
                    components.sessionManager.add(session)
                    finish()
                }
            }
            resources.getString(R.string.pref_key_privacy_notice) -> {
                activity?.apply {
                    val url = if (AppConstants.isKlarBuild)
                        SupportUtils.PRIVACY_NOTICE_KLAR_URL
                    else
                        SupportUtils.PRIVACY_NOTICE_URL

                    val session = Session(url, Session.Source.MENU)
                    components.sessionManager.add(session)
                    finish()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        TelemetryWrapper.settingsEvent(key, sharedPreferences.all[key].toString())
    }

    companion object {
        fun newInstance(): MozillaSettingsFragment = MozillaSettingsFragment()
    }
}
