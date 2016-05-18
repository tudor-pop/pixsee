package com.marked.pixsee.friends;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.marked.pixsee.R;
import com.marked.pixsee.commons.SpaceItemDecorator;
import com.marked.pixsee.data.User;
import com.marked.pixsee.frienddetail.FriendDetailActivity;
import com.marked.pixsee.friends.data.FriendRepository;
import com.marked.pixsee.friends.di.DaggerFriendsComponent;
import com.marked.pixsee.friends.di.FriendModule;
import com.marked.pixsee.friends.di.FriendsComponent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;


/**
 * A list fragment representing a list of Contacts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a [ChatDetailFragment].
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the [Callbacks]
 * interface.
 */
public class FriendFragment extends Fragment implements FriendsContract.View {
	public static int REQUEST_INVITE = 11;

	@Inject
	FriendRepository mRepository;

	@Inject
	FriendPresenter mPresenter;

	private RecyclerView mFriendsRecyclerview;

	private FriendsAdapter mFriendsAdapter;
	private LinearLayoutManager mLayoutManager;

	//	lateinit private var mFabMenu: FloatingActionMenu

	public FriendItemListener friendItemInteraction = new FriendItemListener() {
		@Override
		public void onFriendClick(User friend) {
			Intent intent = new Intent(getActivity(), FriendDetailActivity.class);
			intent.putExtra(FriendDetailActivity.EXTRA_CONTACT, friend);
			getActivity().startActivity(intent);
		}
	};

	@Override
	public void setRecyclerViewVisibility(int viewVisibility) {
		if (mFriendsRecyclerview != null)
			mFriendsRecyclerview.setVisibility(viewVisibility);
	}

	@Override
	public void onFriendsReplace(List<User> friends) {
//		int count = mFriendsAdapter.getItemCount();
		mFriendsAdapter.setDataSet(friends);
		mFriendsAdapter.notifyDataSetChanged();
//		mFriendsAdapter.notifyItemRangeRemoved(0,count);
//		mFriendsAdapter.notifyItemRangeInserted(0,friends.size());
	}

	@UiThread
	@Override
	public void onFriendsInsert(@NonNull List<User> friends, int from, int to) {
		mFriendsAdapter.setDataSet(friends);
		mFriendsAdapter.notifyItemRangeInserted(from, to);
	}

	@Override
	public void showFriendDetailUI(@NotNull User friend) {

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		FriendsComponent component = DaggerFriendsComponent.builder()
				                             .activityComponent(((FriendsActivity) getActivity()).getComponent())
				                             .friendModule(new FriendModule(this))
				                             .build();
		component.inject(this);
		component.inject(mPresenter);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mFriendsAdapter = new FriendsAdapter(friendItemInteraction);
		mPresenter.setView(this);
		mPresenter.loadFriends(true, 50);
	}

	public void setupListeners(View rootView) {
//		mBinding.vuforiaCamera.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mPresenter.getOpenCamera().execute();
//			}
//		});
		rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.getFabCommand().execute();
			}
		});
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
		mFriendsRecyclerview = (RecyclerView) rootView.findViewById(R.id.friendsRecyclerview);
		setUpRecyclerView();
		//		setUpFab();
		setupListeners(rootView);

		((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

		return rootView;
	}

	void setUpRecyclerView() {
		mFriendsRecyclerview.addItemDecoration(new SpaceItemDecorator(getActivity(), R.dimen.item_spacing_chat));
		mFriendsRecyclerview.setAdapter(mFriendsAdapter);
		mFriendsRecyclerview.setLayoutManager(mLayoutManager);
		mFriendsRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (dy > 0 && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
					int s = mLayoutManager.getChildCount();
					int x = mLayoutManager.findFirstVisibleItemPosition();
					if (x + s >= recyclerView.getLayoutManager().getItemCount()) {
						mPresenter.loadFriends(50);
					}
				}
			}
		});
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_friends, menu);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(onQueryTextListener);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
		super.onCreateOptionsMenu(menu, inflater);
	}

	private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String query) {

			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			mPresenter.loadFriends(newText, 10);
			return false;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	//	void setUpFab() {
	//		mFabMenu = mBinding.fabMenu;
	//		createCustomAnimation();
	//        mFabMenu.setClosedOnTouchOutside(true);
	//        mFabMenu.setOnMenuButtonClickListener {
	//            if (mFabMenu.isOpened) {
	//
	//            }
	//            mFabMenu.toggle(true);
	//        }
	//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("TAG", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

		if (requestCode == REQUEST_INVITE) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getActivity(), "Hurray \ud83d\ude04", Toast.LENGTH_SHORT).show();
				// Check how many invitations were sent and log a message
				// The ids array contains the unique invitation ids for each invitation sent
				// (one for each contact select by the user). You can use these for analytics
				// as the ID will be consistent on the sending and receiving devices.
				if (data != null) {
					String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
					Toast.makeText(getActivity(), "got invitations " + ids.length, Toast.LENGTH_SHORT).show();
				}
			} else {
				// Sending failed or it was canceled, show failure message to the user
				Toast.makeText(getActivity(), "Ooh \ud83d\ude14", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/* // uncomment this when we are going to add floating action menu
	    private fun createCustomAnimation() {
            val set = AnimatorSet();

            val scaleOutX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
            val scaleOutY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

            val scaleInX = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
            val scaleInY = ObjectAnimator.ofFloat(mFabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

            scaleOutX.setDuration(50);
            scaleOutY.setDuration(50);

            scaleInX.setDuration(150);
            scaleInY.setDuration(150);


            scaleInX.addListener(object : AnimatorListenerAdapter() {
                override
                fun onAnimationStart(animation: Animator) {
                    if (mFabMenu.isOpened)
                        mFabMenu.getMenuIconView().setImageResource(R.drawable.ic_group_add_24dp);
                    else
                        mFabMenu.getMenuIconView().setImageResource(R.drawable.ic_email_24dp);
                }
            });

            set.play(scaleOutX).with(scaleOutY);
            set.play(scaleInX).with(scaleInY).after(scaleOutX);
            set.setInterpolator(OvershootInterpolator(2.0f));

            mFabMenu.setIconToggleAnimatorSet(set);
        }

        */

	/**
	 * Closes the Floating Action Button
	 *
	 * @return true if it was closed successfully, false otherwise
	 */
	/*
	    fun closeFAM(): Boolean {
            if (mFabMenu.isOpened) {
                mFabMenu.close(true)
                return true
            } else
                return false
        }

        override fun onStop() {
            super.onStop()
            mFabMenu.close(false)
        }*/
	public void onDetach() {
		super.onDetach();
		// Reset the active callbacks interface to the dummy implementation.
		//		dettachListeners();
	}

	//	public void attachListeners(){
	//		for (Contact contact:mContacts.getContacts())
	//			mSocket.on(contact.getId(), onNewRoom);
	//	}
	//	public void dettachListeners(){
	//		for (Contact contact:mContacts.getContacts())
	//			mSocket.off(contact.getId(), onNewRoom);
	//	}


	public static FriendFragment newInstance() {
		return new FriendFragment();
	}

	@Override
	public void setPresenter(FriendsContract.Presenter presenter) {

	}


	public interface FriendItemListener {
		void onFriendClick(User friend);
	}
}

//
//	private Socket mSocket;
//	{
//		try {
//			mSocket = IO.socket(GCMConstants.SERVER);
//		} catch (URISyntaxException e) {
//		}
//	}
//
//	public static String room;
//
//	private Emitter.Listener onNewRoom = new Emitter.Listener() {
//		@Override
//		public void call(final Object... args) {
//			new Handler(Looper.getMainLooper()).post(new Runnable() {
//				@Override
//				public void run() {
////					room = (String) args[0];
//
//
//					// add the message to view
//					//					addMessage(username, message);
//					Toast.makeText(getActivity(), "newRoom TOAAST ", Toast.LENGTH_SHORT).show();
//					//					room = (String) args[0];
//				}
//
//			});
//		}
//	};
/**
 * A dummy implementation of the [Callbacks] interface that does
 * nothing. Used only when this fragment is not attached to an activity.
 */
