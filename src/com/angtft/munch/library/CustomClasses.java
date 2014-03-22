package com.angtft.munch.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.angtft.munch.R;

/*
public class CustomClasses 
{
	
	public class MunchSpinnerAdapter extends ArrayAdapter<String>
		implements SpinnerAdapter
		{
			private LayoutInflater inflate;
			private int resourceId;
			private String[] options;
			private int selIndex;
			private Context context;
		
		public MunchSpinnerAdapter( Context context, int textViewResourceId, String[] objects)
		{
			super(context, textViewResourceId, objects);
			this.context = context;
			this.inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.resourceId = textViewResourceId;
			this.options = objects;
			
		}
		
		public void setSelectedIndex(int selIndex)
		{
			this.selIndex = selIndex;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = inflate.inflate(resourceId, null);
				Holder holder = new Holder();
				holder.textView = (TextView) convertView.findViewById(R.id.spinner_itemconvertView.setTag(holder);
				convertView.setTag(holder);
			}
			
			Holder holder = (Holder)convertView.getTag();
			holder.textView.setText(options[position]);
			
			return convertView;
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = inflate.inflate(resourceId, null);
				Holder holder = new Holder();
				holder.textView = (TextView) convertView.findViewById(R.id.spinner_item);
				convertView.setTag(holder);
			}
			
			Holder holder = (Holder) convertView.getTag();
			holdter.textView.setText(options[position]);
			if(position == selIndex)
			{
				holder.textView.setBackgroundColor(context.getResources().getColor(R.color.this));
			}
			else
				holder.textView.setBackgroundColor(context.getResources().getColor(R.color.spinner_item_default));
			
			return convertView;
		}
		
		private class Holder
		{
			TextView textView;
		}
		
	}

}
*/
