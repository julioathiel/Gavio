package com.gastosdiarios.gavio.di.module


import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.credentials.CredentialManager
import com.gastosdiarios.gavio.data.commons.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore


    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun providePackageManager(application: Application): PackageManager {
        return application.packageManager
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideCredentialManager(application: Application): CredentialManager {
        return CredentialManager.create(application)
    }

    @Provides
    @Singleton
    fun provideSnackbarManager(): SnackbarManager {
        return SnackbarManager()
    }

}