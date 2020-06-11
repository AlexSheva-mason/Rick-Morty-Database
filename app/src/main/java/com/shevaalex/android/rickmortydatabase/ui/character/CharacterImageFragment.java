package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterImageBinding;
import com.squareup.picasso.Picasso;

public class CharacterImageFragment extends Fragment implements View.OnClickListener {
    private FragmentCharacterImageBinding binding;
    private String imageUrl;

    public CharacterImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCharacterImageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        imageUrl = CharacterImageFragmentArgs.fromBundle(requireArguments()).getCharacterImageUrl();
        if (!imageUrl.equals("none")) {
            setCharacterImage(imageUrl);
        }
        binding.imageCharacter.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setCharacterImage(String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.picasso_placeholder_error)
                .into(binding.imageCharacter);
    }

    @Override
    public void onClick(View v) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri umageUri = Uri.parse(imageUrl);
        shareIntent.putExtra(Intent.EXTRA_STREAM, umageUri);
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Character name");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share image with..."));
    }
}