package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shevaalex.android.rickmortydatabase.MainActivity;
import com.shevaalex.android.rickmortydatabase.R;

import java.util.Objects;

public class CharacterDetailFragment extends Fragment {
    private static final String TAG = "CharacterDetailFragment";
    private Activity a;

    public CharacterDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            a = (Activity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_detail, container, false);
        // find views
        TextView nameCharacter = view.findViewById(R.id.character_name);
        TextView imgUrlCharacter = view.findViewById(R.id.character_img_url);
        // retrieve data from parent fragment and set it to appropriate views
        String charName = CharacterDetailFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getCharacterName();
        String imgUrl = CharacterDetailFragmentArgs.fromBundle(getArguments()).getImageUrl();
        nameCharacter.setText(charName);
        imgUrlCharacter.setText(imgUrl);
        return view;
    }
}
