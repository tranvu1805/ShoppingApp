package com.example.shoppingapp;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = ShoppingAppApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface ShoppingAppApplication_GeneratedInjector {
  void injectShoppingAppApplication(ShoppingAppApplication shoppingAppApplication);
}
