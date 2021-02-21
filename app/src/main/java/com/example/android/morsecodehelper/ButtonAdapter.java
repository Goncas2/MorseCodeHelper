package com.example.android.morsecodehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ButtonAdapter extends BaseAdapter {

    private Context mContext;

    public ButtonAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return 36;
    }

    @Override
    public Object getItem(int position) {

        if (0 <= position && position <= 25) {
            return (char) (position + 65);
        }
        else{
            return (char) (position - 26 + 48);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.fragment_main, parent, false);
        }

        TextView character = listItemView.findViewById(R.id.tv_character);
        TextView code = listItemView.findViewById(R.id.tv_code);

        character.setText(String.valueOf((char)getItem(position)));
        code.setText(MorseCodes.get((char)getItem(position)).getCode());
        return listItemView;
    }

}
