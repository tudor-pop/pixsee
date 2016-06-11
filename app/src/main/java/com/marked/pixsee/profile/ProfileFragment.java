package com.marked.pixsee.profile;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marked.pixsee.R;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.entry.EntryActivity;
import com.marked.pixsee.friendsInvite.FriendsInviteActivity;
import com.marked.pixsee.injection.components.ActivityComponent;
import com.marked.pixsee.injection.components.DaggerActivityComponent;
import com.marked.pixsee.injection.modules.ActivityModule;
import com.marked.pixsee.profile.di.DaggerProfileComponent;
import com.marked.pixsee.profile.di.ProfileModule;

import java.io.File;

import javax.inject.Inject;

public class ProfileFragment extends Fragment implements ProfileContract.View{
	private static String USER_EXTRA = "PROFILE_FRAGMENT_USER";
	private User mUser;
	@Inject
	ProfileContract.Presenter mPresenter;
	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment ProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ProfileFragment newInstance(User user) {
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putParcelable(USER_EXTRA, user);
		fragment.setArguments(args);
		return fragment;
	}


	public ProfileFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mProfileInteraction = (ProfileInteraction) context;
		}catch (ClassCastException e){
			e.printStackTrace();
		}
		mUser = getArguments().getParcelable(USER_EXTRA);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityComponent component = DaggerActivityComponent
				.builder()
				.activityModule(new ActivityModule((AppCompatActivity)getActivity()))
				.build();
		DaggerProfileComponent.builder().activityComponent(component)
				.profileModule(new ProfileModule(this))
				.build()
				.inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

		((TextView)rootView.findViewById(R.id.nameTextview)).setText(mUser.getName());
		((TextView)rootView.findViewById(R.id.usernameTextview)).setText(mUser.getUsername());
		if (mUser.getIconUrl()!=null) // // TODO: 12-Jun-16 remove this and make mUser.getIconUrl() to always have an Url
			((SimpleDraweeView)rootView.findViewById(R.id.iconSimpleDraweeView)).setImageURI(Uri.parse(mUser.getIconUrl()));

		ImageButton inviteFriendsImageButton = ((ImageButton)rootView.findViewById(R.id.inviteFriendsImageButton));
		inviteFriendsImageButton.getDrawable()
				.setColorFilter(ContextCompat.getColor(getActivity(),R.color.accent), PorterDuff.Mode.SRC_ATOP);
		inviteFriendsImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FriendsInviteActivity.class);
				getActivity().startActivity(intent);
			}
		});

		((SimpleDraweeView)rootView.findViewById(R.id.iconSimpleDraweeView)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mProfileInteraction.onCameraClick();
			}
		});
		((Button)rootView.findViewById(R.id.logoutButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = preferences.edit();
				editor.clear();
				editor.commit();
				Intent intent = new Intent(getActivity(), EntryActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		});

		// Inflate the layout for this fragment
		return rootView ;
	}

	public void setProfilePicture(File profilePicture) {
		((SimpleDraweeView) getView().findViewById(R.id.iconSimpleDraweeView)).setImageURI(Uri.parse("file://"+profilePicture.getAbsolutePath()));
		mUser.setIconUrl("file://"+profilePicture.getAbsolutePath());
		mPresenter.saveAppUser(mUser);
	}

	private ProfileInteraction mProfileInteraction;

	@Override
	public void setPresenter(ProfileContract.Presenter presenter) {
		mPresenter = presenter;
	}

	public interface ProfileInteraction{
		void onCameraClick();
	}
}
