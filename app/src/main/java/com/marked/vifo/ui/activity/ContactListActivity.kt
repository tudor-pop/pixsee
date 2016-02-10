package com.marked.vifo.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.marked.vifo.R
import com.marked.vifo.helper.add
import com.marked.vifo.ui.fragment.ContactListFragment
import kotlinx.android.synthetic.main.activity_contact_app_bar.*


/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ContactDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 *
 * The activity makes heavy use of fragments. The list of items is a
 * [ContactListFragment] and the item details
 * (if present) is a [ContactDetailFragment].
 *
 *
 * This activity also implements the required
 * [ContactListFragment.Callbacks] interface
 * to listen for item selections.
 */
class ContactListActivity : AppCompatActivity() {
	private val mFragmentManager by lazy { supportFragmentManager }

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private val mTwoPane: Boolean = false

	override
	fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_contact_app_bar)

		setSupportActionBar(toolbar)
		toolbar.title = title
		mFragmentManager.add(R.id.fragmentContainer, ContactListFragment.newInstance())


		fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }
		// TODO: If exposing deep links into your app, handle intents here.
	}
}