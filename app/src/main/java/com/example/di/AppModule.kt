package com.example.launderagent.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.launder.R
import com.example.launder.data.local.ShoppingItemDatabase
import com.example.launder.domain.mainRepository
import com.example.launder.domain.mainRepositoryImpl
import com.example.launder.other.Constants.DATABASE_NAME
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: mainRepositoryImpl): mainRepository = impl

    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context


    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.profile)
            .error(R.drawable.place)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )
    @Provides
    fun provideMainDispatcher() = Dispatchers.Main as CoroutineDispatcher


    @Provides
    fun provideShoppingItemDataBase(
        @ApplicationContext context: Context
    )= Room.databaseBuilder(context, ShoppingItemDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()


    @Provides
    fun provideShoppingDao(
        database:ShoppingItemDatabase
    ) = database.shoppingDao()

}