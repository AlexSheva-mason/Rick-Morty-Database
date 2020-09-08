package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterImageBinding;
import com.shevaalex.android.rickmortydatabase.ui.BaseFragment;
import com.shevaalex.android.rickmortydatabase.utils.ImageParsingUtil;
import com.shevaalex.android.rickmortydatabase.utils.StringParsing;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CharacterImageFragment extends BaseFragment implements View.OnClickListener {
    private FragmentCharacterImageBinding binding;
    private String imageUrl;
    private String characterName;
    private Activity a;
    private final String SHARE_TYPE = "image/*";
    private FirebaseAnalytics mFirebaseAnalytics;

    public CharacterImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            a = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(a);
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
        if (binding.shareButton != null) {
            binding.shareButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.charactersListFragment, R.id.locationsListFragment, R.id.episodesListFragment).build();
        Toolbar toolbar = binding.toolbarFragmentCharacterImage;
        if (toolbar != null) {
            createOptionsMenu(toolbar);
            NavigationUI.setupWithNavController(
                    toolbar, navController, appBarConfiguration);
        }
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

    private void createOptionsMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.toolbar_fragment_character_image);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.share_button) {
                shareImage();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        shareImage();
    }

    private void shareImage() {
        String parsedName = StringParsing.parseCharacterName(characterName);
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType(SHARE_TYPE);
                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, ImageParsingUtil.parseBitmapToUri(bitmap, parsedName, requireContext()));
                        startActivity(Intent.createChooser(shareIntent, a.getApplicationContext().getResources().getString(R.string.share_title)));
                        //log share intent with firebase
                        Bundle shareBundle = new Bundle();
                        shareBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, SHARE_TYPE);
                        shareBundle.putString(FirebaseAnalytics.Param.ITEM_ID, parsedName);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, shareBundle);
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(requireContext(), requireContext().getResources().getString(R.string.error_share_no_network), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {                    }
                });
    }
}