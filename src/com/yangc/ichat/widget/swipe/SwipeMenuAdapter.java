package com.yangc.ichat.widget.swipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class SwipeMenuAdapter implements WrapperListAdapter, SwipeMenuView.OnSwipeItemClickListener {

	private Context mContext;
	private ListAdapter mAdapter;
	private SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener;

	public SwipeMenuAdapter(Context mContext, ListAdapter mAdapter) {
		this.mContext = mContext;
		this.mAdapter = mAdapter;
	}

	@Override
	public int getCount() {
		return this.mAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return this.mAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return this.mAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeMenuLayout layout = null;
		if (convertView == null) {
			View contentView = this.mAdapter.getView(position, convertView, parent);
			SwipeMenu menu = new SwipeMenu(this.mContext);
			menu.setViewType(this.mAdapter.getItemViewType(position));
			createMenu(menu);
			SwipeMenuView menuView = new SwipeMenuView(menu);
			menuView.setOnSwipeItemClickListener(this);
			SwipeMenuListView listView = (SwipeMenuListView) parent;
			layout = new SwipeMenuLayout(contentView, menuView, listView.getCloseInterpolator(), listView.getOpenInterpolator());
			layout.setPosition(position);
		} else {
			layout = (SwipeMenuLayout) convertView;
			layout.closeMenu();
			layout.setPosition(position);
		}
		return layout;
	}

	public void createMenu(SwipeMenu menu) {
		// Test Code
		SwipeMenuItem item = new SwipeMenuItem(this.mContext);
		item.setTitle("Item 1");
		item.setBackground(new ColorDrawable(Color.GRAY));
		item.setWidth(300);
		menu.addMenuItem(item);

		item = new SwipeMenuItem(this.mContext);
		item.setTitle("Item 2");
		item.setBackground(new ColorDrawable(Color.RED));
		item.setWidth(300);
		menu.addMenuItem(item);
	}

	@Override
	public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
		if (this.onMenuItemClickListener != null) {
			this.onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
		}
	}

	public void setOnMenuItemClickListener(SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		this.mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		this.mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return this.mAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return this.mAdapter.isEnabled(position);
	}

	@Override
	public boolean hasStableIds() {
		return this.mAdapter.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return this.mAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return this.mAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return this.mAdapter.isEmpty();
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return this.mAdapter;
	}

}
