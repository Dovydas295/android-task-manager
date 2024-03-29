/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.jpsan.es;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TaskManager extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	
	private static final int INSERT_ID = Menu.FIRST;
	private static final int SORT_DATE_ID = Menu.FIRST+4;
	private static final int SORT_TITLE_ID = Menu.FIRST+5;
	
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int DONE_ID = Menu.FIRST + 2;
	private static final int UNDONE_ID = Menu.FIRST + 3;
	

	private TasksDbAdapter mDbHelper;
	
	private int actualSortKey;
	private int actualSortOrder;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_list);
		mDbHelper = new TasksDbAdapter(this);
		mDbHelper.open();
		actualSortKey = TasksDbAdapter.BY_TITLE;
		actualSortOrder = TasksDbAdapter.ASC_ORDER;
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		Cursor notesCursor = mDbHelper.fetchAllNotes(actualSortKey,
				actualSortOrder);
		// Get all of the rows from the database and create the item list
		notesCursor = mDbHelper.fetchAllNotes(actualSortKey,
				actualSortOrder);
		startManagingCursor(notesCursor);

		// Create an array to specify the fields we want to display in the list
		String[] from = new String[] {TasksDbAdapter.KEY_TITLE,
				TasksDbAdapter.KEY_DATE };

		// and an array of the fields we want to bind those fields to
		int[] to = new int[] {R.id.text1, R.id.text2 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.notes_row, notesCursor, from, to);
		setListAdapter(notes);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, SORT_TITLE_ID,0, R.string.menu_sort_title);
		menu.add(0, SORT_DATE_ID,0, R.string.menu_sort_date);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		case SORT_TITLE_ID:
			this.actualSortKey = TasksDbAdapter.BY_TITLE;
			this.actualSortOrder = TasksDbAdapter.ASC_ORDER;
			fillData();
			return true;
		case SORT_DATE_ID:
			this.actualSortKey = TasksDbAdapter.BY_DATE;
			this.actualSortOrder = TasksDbAdapter.DESC_ORDER;
			fillData();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DONE_ID, 0, R.string.menu_done);
		menu.add(0, UNDONE_ID, 0, R.string.menu_undone);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteNote(info.id);
			fillData();
			return true;
		case DONE_ID:
			// Code to set the "V" icon
			fillData();
			return true;
		case UNDONE_ID:
			// Code to set the "X" icon
			fillData();
			return true;

		}
		return super.onContextItemSelected(item);
	}

	private void createNote() {
		Intent i = new Intent(this, TaskEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TaskEdit.class);
		i.putExtra(TasksDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
}
