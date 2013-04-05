package com.example.keycardapp;

public class IconHandler {

	public static int[] icons = {
		R.drawable.icon_house,
		R.drawable.row_icon_factory,
		R.drawable.row_icon_buss_big,
		R.drawable.row_icon_block,
		R.drawable.icon_plane
	};
	
	public static int getIconCount(){
		return icons.length;
	}
	
	public static Integer[] getIconsID(){
		int numIcons = getIconCount();
		Integer[] ids = new Integer[numIcons];
		for (int i = 0; i < numIcons; ids[i] = i, i++);
		return ids;
	}
	
	public static int getLayoutResourceIDfromIconID(int iconID) {
		if (iconID >= getIconCount()) {
			return icons[0];
		} else {
			return icons[iconID];
		}
	}
}
