/*
 * Copyright 2013 ParanoidAndroid Project
 *
 * This file is part of Paranoid OTA.
 *
 * Paranoid OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Paranoid OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Paranoid OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.paranoid.paranoidota.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.paranoid.paranoidota.ListViewAdapter;
import com.paranoid.paranoidota.R;

public class ChangelogFragment extends Fragment {
	String[] aboutOptions;
	
	ListView changelogLV;
	
	ListViewAdapter adapter;
	
	int[] flag;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View view = inflater.inflate(R.layout.fragment_changelog, container, false);
        
        aboutOptions = new String[] { 
        		"Ninja level +1 (HALO now disappears if only persistent or pinned apps are present and of course if its empty)",  
        		"Fixed Linked volume settings bug",
        		"Support us!",
        		"Fixed notification icons not showing when HALO is active (Tablet UI bug)",
        		"Other Tablet UI fixes",
        		"Added incoming call screen transparency",
        		"Reordered settings menu",
        		"Fixed Custom kernel force close (Back to stock kernel i.e. No OTG)" +
        	    "-May release separate flash-able OTG kernel in future",
        	    "Merged 4.3_r3.1 (JLS36G) (Very minor changes STILL JWR KERNEL) "};
        
        changelogLV = (ListView) view.findViewById(R.id.changelogLV);
        
        adapter = new ListViewAdapter(getActivity(), aboutOptions, 
                flag);
        // Binds the Adapter to the ListView
        changelogLV.setAdapter(adapter);                             

        return view;
        
        }
	}