// Generated by Dagger (https://dagger.dev).
package com.example.shoppingapp.viewmodel;

import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public LoginViewModel_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new LoginViewModel_Factory(firebaseAuthProvider);
  }

  public static LoginViewModel newInstance(FirebaseAuth firebaseAuth) {
    return new LoginViewModel(firebaseAuth);
  }
}
