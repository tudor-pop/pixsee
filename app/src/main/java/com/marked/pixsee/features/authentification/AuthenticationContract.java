package com.marked.pixsee.features.authentification;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.features.authentification.signup.SignUpEmailFragment;
import com.marked.pixsee.features.authentification.signup.SignUpNameFragment;
import com.marked.pixsee.features.authentification.signup.SignUpPassFragment;

/**
 * Created by Tudor on 13-Jun-16.
 */
public interface AuthenticationContract {
	interface Presenter extends BasePresenter, SignUpNameFragment.SignUpNameFragmentInteraction,SignUpEmailFragment
			.SignUpEmailFragmentInteraction,SignUpPassFragment.SignUpPassFragmentInteraction{

		void checkEmail(String email);

		void handleLogin(String email, String password);
	}

	interface View extends BaseView<Presenter> {

		void hideDialog();

		void showToast(String s);

		void showSignupStepPassword();

		void showSignupStepName();

		void showSignupStepEmail(String name);

		void showDialog(String title,String message);

		void signupComplete(String name, String email, String username, String password);

		void showMainScreen();

		void showLoginStep();
	}
}
