// Generated by Dagger (https://dagger.dev).
package com.example.shoppingapp.viewmodel;

import com.example.shoppingapp.firebase.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class CartViewModel_Factory implements Factory<CartViewModel> {
  private final Provider<FirebaseFirestore> fireStoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<FirebaseUtils> firebaseUtilsProvider;

  public CartViewModel_Factory(Provider<FirebaseFirestore> fireStoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<FirebaseUtils> firebaseUtilsProvider) {
    this.fireStoreProvider = fireStoreProvider;
    this.authProvider = authProvider;
    this.firebaseUtilsProvider = firebaseUtilsProvider;
  }

  @Override
  public CartViewModel get() {
    return newInstance(fireStoreProvider.get(), authProvider.get(), firebaseUtilsProvider.get());
  }

  public static CartViewModel_Factory create(Provider<FirebaseFirestore> fireStoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<FirebaseUtils> firebaseUtilsProvider) {
    return new CartViewModel_Factory(fireStoreProvider, authProvider, firebaseUtilsProvider);
  }

  public static CartViewModel newInstance(FirebaseFirestore fireStore, FirebaseAuth auth,
      FirebaseUtils firebaseUtils) {
    return new CartViewModel(fireStore, auth, firebaseUtils);
  }
}
