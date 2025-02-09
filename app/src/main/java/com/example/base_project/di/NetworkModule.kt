package com.example.base_project.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.base_project.BuildConfig
import com.example.base_project.data.remote.ApiRequest
import com.example.base_project.data.remote.ApiService
import com.example.base_project.data.remote.AuthApiService
import com.example.base_project.util.NetworkConnectionInterceptor
import com.example.base_project.util.network.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthInterceptorClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        return Dispatcher().apply {
            this.maxRequests = 1
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @AuthInterceptorClient
    @Provides
    @Singleton
    fun provideAuthClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        dispatcher: Dispatcher
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(networkConnectionInterceptor)
            .connectTimeout(Duration.ofMinutes(1))
            .dispatcher(dispatcher)
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return clientBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NoAuthInterceptorClient
    @Provides
    @Singleton
    fun provideClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .connectTimeout(Duration.ofMinutes(1)) //set time out 1 munit
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(@ApplicationContext context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(
        gson: Gson,
        service: ApiService
    ): AuthInterceptor {
        return AuthInterceptor(service, gson)
    }

    @Singleton
    @Provides
    fun provideApiRequest(
        authService: AuthApiService,
        noAuthService: ApiService,
        gson: Gson
    ): ApiRequest {
        return ApiRequest(authService, noAuthService, gson)
    }

    @Singleton
    @Provides
    fun provideAuthRetrofit(
        @AuthInterceptorClient okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): AuthApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(AuthApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @NoAuthInterceptorClient okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): ApiService {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonConverterFactory: GsonConverterFactory): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(gsonConverterFactory)

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().generateNonExecutableJson().create()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }
}