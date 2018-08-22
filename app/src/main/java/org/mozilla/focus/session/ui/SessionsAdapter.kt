/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.session.ui

import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import org.mozilla.focus.ext.requireComponents

import java.util.ArrayList
import java.util.Collections

/**
 * Adapter implementation to show a list of active browsing sessions and an "erase" button at the end.
 */
class SessionsAdapter/* package */ internal constructor(
    private val fragment: SessionsSheetFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SessionManager.Observer {
    private var sessions: List<Session>? = null

    init {
        this.sessions = emptyList<Session>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            EraseViewHolder.LAYOUT_ID -> return EraseViewHolder(
                    fragment,
                    inflater.inflate(EraseViewHolder.LAYOUT_ID, parent, false))
            SessionViewHolder.LAYOUT_ID -> return SessionViewHolder(
                    fragment,
                    inflater.inflate(SessionViewHolder.LAYOUT_ID, parent, false) as TextView)
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            EraseViewHolder.LAYOUT_ID -> { /* do nothing */ }
            SessionViewHolder.LAYOUT_ID -> (holder as SessionViewHolder).bind(sessions!![position])
            else -> throw IllegalStateException("Unknown viewType")
        }// Nothing to do here.
    }

    override fun getItemViewType(position: Int): Int {
        return if (isErasePosition(position)) {
            EraseViewHolder.LAYOUT_ID
        } else {
            SessionViewHolder.LAYOUT_ID
        }
    }

    private fun isErasePosition(position: Int): Boolean {
        return position == sessions!!.size
    }

    override fun getItemCount(): Int {
        return sessions!!.size + 1
    }

    override fun onSessionAdded(session: Session) {
        onUpdate(fragment.requireComponents.sessionManager.sessions)
    }

    override fun onSessionRemoved(session: Session) {
        onUpdate(fragment.requireComponents.sessionManager.sessions)
    }

    override fun onSessionSelected(session: Session) {
        onUpdate(fragment.requireComponents.sessionManager.sessions)
    }

    override fun onAllSessionsRemoved() {
        onUpdate(fragment.requireComponents.sessionManager.sessions)
    }

    fun onUpdate(sessions: List<Session>) {
        this.sessions = sessions
        notifyDataSetChanged()
    }
}
