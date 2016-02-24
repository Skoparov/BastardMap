package com.example.skoparov.bastardmaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class BastardPathListFragment extends ListFragment
{
    public static class RowItem
    {
        String title;
        String details;
        //int icon;

        RowItem(String pathName, String details)
        {
            title = pathName;
            this.details = details;
        }
    }

    public class CustomAdapter extends BaseAdapter
    {
        Context mContext;
        List<RowItem> mRowItems;

        CustomAdapter(Context context, List<RowItem> rowItem)
        {
            mContext = context;
            mRowItems = rowItem;

        }

        @Override
        public int getCount()
        {
            return mRowItems.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mRowItems.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return mRowItems.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            if (convertView == null)
            {
                LayoutInflater mInflater = (LayoutInflater) mContext
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.path_row, null);
            }

            RowItem item = mRowItems.get(position);

            TextView title = (TextView) convertView.findViewById(R.id.path_title);
            title.setText(item.title);

            TextView details = (TextView) convertView.findViewById(R.id.path_details);
            details.setText(item.details);

            Button deleteButton = (Button)  convertView.findViewById(R.id.delete_btn);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    final int position = listView.getPositionForView(parentRow);
                    final RowItem item = mRowItems.get(position);

                    AlertDialog.Builder adb=new AlertDialog.Builder(mActivity);
                    adb.setTitle("Are you sure, you bastard?");
                    adb.setMessage("Delete track?\n" + item.title);
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mTracker.deletePath(item.title);
                            mRowItems.remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    adb.show();
                }

            });

            return convertView;
        }
    }

    private CustomAdapter mAdapter;
    private List<RowItem> mRowItems;
    private Activity mActivity;
    private BastardTracker mTracker;
    private ListView mListView;

    public void setData( List<RowItem> items )
    {
        mRowItems = items;
    }

    public void setTools( Activity activity, BastardTracker tracker )
    {
        mActivity = activity;
        mTracker = tracker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mListView = getListView();
        mAdapter = new CustomAdapter(getActivity(), mRowItems);
        setListAdapter(mAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                List<String> selection = mTracker.getSwitchedPaths();
                for (String key : selection)
                {
                    int index = -1;//mRowItems.indexOf(key);
                    for(int item = 0; item < mRowItems.size(); ++item)
                    {
                        if(mRowItems.get(item).title == key)
                        {
                            index = item;
                            break;
                        }
                    }

                    if (index != -1)
                    {
                        mListView.setItemChecked(index, true);
                        mListView.getChildAt(index).setBackgroundColor(getSelectionColor());
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id)
    {
        if( mTracker != null )
        {
            String name = mRowItems.get(position).title;
            mTracker.switchPaintPath(name);

            List<String> selection = mTracker.getSwitchedPaths();
            boolean selected = selection.contains(name)?
                    true : false;

            Integer color = selected? getSelectionColor() : Color.TRANSPARENT;
            parent.getChildAt(position).setBackgroundColor(color);

            parent.setItemChecked(position, selected);
        }
    }

    private Integer getSelectionColor()
    {
        return Color.LTGRAY;
    }
}
