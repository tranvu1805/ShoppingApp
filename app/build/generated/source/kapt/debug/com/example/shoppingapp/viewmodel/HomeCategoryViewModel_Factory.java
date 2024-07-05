// Generated by Dagger (https://dagger.dev).
package com.example.shoppingapp.viewmodel;

import android.app.Application;
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
public final class HomeCategoryViewModel_Factory implements Factory<HomeCategoryViewModel> {
  private final Provider<FirebaseFirestore> fireStoreProvider;

  private final Provider<Application> appProvider;

  public HomeCategoryViewModel_Factory(Provider<FirebaseFirestore> fireStoreProvider,
      Provider<Application> appProvider) {
    this.fireStoreProvider = fireStoreProvider;
    this.appProvider = appProvider;
  }

  @Override
  public HomeCategoryViewModel get() {
    return newInstance(fireStoreProvider.get(), appProvider.get());
  }

  public static HomeCategoryViewModel_Factory create(Provider<FirebaseFirestore> fireStoreProvider,
      Provider<Application> appProvider) {
    return new HomeCategoryViewModel_Factory(fireStoreProvider, appProvider);
  }

  public static HomeCategoryViewModel newInstance(FirebaseFirestore fireStore, Application app) {
    return new HomeCategoryViewModel(fireStore, app);
  }
}