package com.shevaalex.android.rickmortydatabase.ui.character;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterImageBinding;
import com.shevaalex.android.rickmortydatabase.utils.ImageParsingUtil;
import com.shevaalex.android.rickmortydatabase.utils.StringParsing;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CharacterImageFragment extends Fragment implements View.OnClickListener {
    private FragmentCharacterImageBinding binding;
    private String imageUrl;
    private String characterName;

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
        characterName = CharacterImageFragmentArgs.fromBundle(requireArguments()).getCharacterName();
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
        String parsedName = StringParsing.parseCharacterName(characterName);
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, ImageParsingUtil.parseBitmapToUri(bitmap, parsedName, requireContext()));
                        startActivity(Intent.createChooser(shareIntent, "Share image with..."));
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {                    }
                });
    }
}