package com.whiznic.blackbox1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whiznic.blackbox1.helper.AssetsHelper;

import java.util.ArrayList;

/**
 * Created by Uss on 26/01/2016.
 */
public class BinMemberAdapter extends BaseAdapter {
    private ArrayList<AssetsHelper> assetsHelpers;
    private Context context;

    public BinMemberAdapter(Context context){
        this.context = context;
        assetsHelpers = new ArrayList<>();
    }

    public void addItem(AssetsHelper helper) {
        assetsHelpers.add(helper);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return assetsHelpers.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public AssetsHelper getItem(int position) {
        return assetsHelpers.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bin_spinner_item, null);
        }
        AssetsHelper helper = getItem(position);
        Log.d("HB getView", helper.assetName + " -- " + helper.isChecked);
        TextView textView =(TextView) convertView.findViewById(R.id.item);
        textView.setText(helper.assetName);

        textView.setTag(position);
        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                AssetsHelper helper = getItem(position);

                helper.isChecked = !helper.isChecked;
                ((CheckBox) v).setChecked(helper.isChecked);
                assetsHelpers.set(position, helper);
                Log.d("HB", helper.assetName + " -- " + helper.isChecked);
            }
        });
        textView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) buttonView.getTag();
                AssetsHelper helper = getItem(position);
                buttonView.setChecked(helper.isChecked);
            }
        });*/
        return convertView;
    }
}
