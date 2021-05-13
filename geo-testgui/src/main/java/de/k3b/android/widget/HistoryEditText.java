/*
 * Copyright (c) 2015-2016 by k3b.
 *
 * This file is part of k3b-geoHelper library.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.widget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.k3b.android.geo.testgui.R;

/**
 * Add history-popup to EditText.
 * invoke via Clipboard ContextActionBar: star (history) to open a popupmenu with previous values.
 * Long-Press if no selection => popup with previous values.
 *
 * Created by k3b on 26.08.2015.
 */
public class HistoryEditText {
    private static final int NO_ID = -1;
    private final Activity mContext;
    private final String mDelimiter;
    private final int mMaxHisotrySize;
    private final EditorHandler[] mEditorHandlers;

    /** ContextActionBar for one EditText */
    protected class EditorHandler implements ActionMode.Callback, View.OnLongClickListener, View.OnClickListener  {
        private final EditText mEditor;
        private final ImageButton mCmd;
        private final String mId;

        public EditorHandler(String id, EditText editor, int cmdId) {
            mId = id;
            mEditor = editor;

            mCmd = (cmdId != NO_ID) ? (ImageButton) mContext.findViewById(cmdId) : null;
            mEditor.setCustomSelectionActionModeCallback(this);

            if (mCmd == null) {
                mEditor.setOnLongClickListener(this);
            } else {
                mCmd.setOnClickListener(this);
            }
        }

        public String toString(SharedPreferences pref) {
            return mId + " : '" + getHistory(pref) + "'";
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_history_edit_text, menu);
            // menu.removeItem(android.R.id.selectAll);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_history) {
                showHistory();
                return true;
            }
            return false;
        }

        /**
         * ActionMode.Callback
         * Called when an action mode is about to be exited and destroyed.
         *
         * @param mode The current ActionMode being destroyed
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // not used
        }

        protected void showHistory() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            List<String> items = getHistory(sharedPref);

            PopupMenu popup = new PopupMenu(mContext, mEditor);
            Menu root = popup.getMenu();
            int len = items.size();
            if (len > 10) len = 10;
            for (int i = 0; i < len; i++) {
                String text = items.get(i).trim();

                if ((text != null) && (text.length() > 0)) {
                    root.add(text);
                }
            }

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mEditor.setText(item.getTitle());
                    mEditor.setSelection(0, mEditor.length());
                    return true;
                }
            });
            popup.show();
        }

        private List<String> getHistory(SharedPreferences sharedPref) {
            String history = sharedPref.getString(mId, "");
            return asList(history);
        }

        private void saveHistory(SharedPreferences sharedPref, SharedPreferences.Editor edit) {
            List<String> history = getHistory(sharedPref);
            history = include(history, mEditor.getText().toString().trim());
            String result = toString(history);
            edit.putString(mId, result);
        }

        private List<String> asList(String serialistedListElements) {
            String[] items = serialistedListElements.split(mDelimiter);
            return Arrays.asList(items);
        }

        private String toString(List<String> list) {
            StringBuffer result = new StringBuffer();
            String nextDelim = "";
            for (String instance : list) {
                if (instance != null) {
                    instance = instance.trim();
                    if (instance.length() > 0) {
                        result.append(nextDelim).append(instance);
                        nextDelim = mDelimiter;
                    }
                }
            }
            return result.toString();
        }

        private List<String>  include(List<String>  oldhistory, String newValue) {
            ArrayList<String> newHistory = new ArrayList<>(oldhistory);
            if ((newValue != null) && (newValue.length() > 0)) {
                newHistory.remove(newValue);
                newHistory.add(0, newValue);
            }

            int len = newHistory.size();

            // forget oldest entries if maxHisotrySize is reached
            while (len > mMaxHisotrySize) {
                len--;
                newHistory.remove(len);
            }
            return newHistory;
        }

        @Override
        public void onClick(View v) {
            showHistory();
        }

        @Override
        public boolean onLongClick(View v) {

            if (mEditor.getSelectionEnd() - mEditor.getSelectionStart() > 0) {
                return false;
            }
            showHistory();
            return true;
        }
    }

    /** define history function for these editors */
    public HistoryEditText(Activity context, int[] cmdIds, EditText... editors) {
        this(context, context.getClass().getSimpleName() + "_history_","';'",  8, cmdIds, editors);
    }

    /** define history function for these editors */
    public HistoryEditText(Activity context, String settingsPrefix, String delimiter, int maxHisotrySize, int[] cmdIds, EditText... editors) {

        this.mContext = context;
        this.mDelimiter = delimiter;
        this.mMaxHisotrySize = maxHisotrySize;
        mEditorHandlers = new EditorHandler[editors.length];

        for (int i = 0; i < editors.length; i++) {
            mEditorHandlers[i] = createHandler(settingsPrefix+i, editors[i], getId(cmdIds, i));
        }
    }

    protected EditorHandler createHandler(String id, EditText editor, int cmdId) {
        return new EditorHandler(id,editor, cmdId);
    }

    private int getId(int[] ids, int offset) {
        if ((ids != null) && (offset >= 0) && (offset < ids.length) ) return ids[offset];
        return NO_ID;
    }

    /** include current editor-content to history and save to settings */
    public void saveHistory() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = sharedPref.edit();

        for (EditorHandler instance: mEditorHandlers) {
            instance.saveHistory(sharedPref, edit);
        }
        edit.apply();
    }

    @Override
    public String toString() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        StringBuffer result = new StringBuffer();
        for (EditorHandler instance: mEditorHandlers) {
            result.append(instance.toString(sharedPref)).append("\n");
        }
        return result.toString();
    }
}
