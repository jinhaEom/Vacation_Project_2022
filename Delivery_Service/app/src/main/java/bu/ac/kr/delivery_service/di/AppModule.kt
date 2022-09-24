package bu.ac.kr.delivery_service.di

import android.app.Presentation
import androidx.core.view.MotionEventCompat.getSource
import bu.ac.kr.delivery_service.api.SweetTrackerApi
import bu.ac.kr.delivery_service.api.Url
import bu.ac.kr.delivery_service.db.AppDatabase
import bu.ac.kr.delivery_service.presentation.trackingitems.TrackingItemsContract
import bu.ac.kr.delivery_service.presentation.trackingitems.TrackingItemsFragment
import bu.ac.kr.delivery_service.presentation.trackingitems.TrackingItemsPresenter
import bu.ac.kr.delivery_service.repository.*
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val appModule = module {
    single { Dispatchers.IO}

    // Database
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().trackingItemDao()}

    //Api
    single {
        OkHttpClient()
            .newBuilder().addInterceptor(
                HttpLoggingInterceptor().apply{
                    level = if(BuildConfig.DEBUG){
                        HttpLoggingInterceptor.Level.BODY
                    }else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<SweetTrackerApi>{
        Retrofit.Builder().baseUrl(Url.SWEET_TRACKER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }
    single<TrackingItemRepository> { TrackingItemRepositoryImpl(get(), get(), get()) }
    single<ShippingCompanyRepository> { ShippingCompanyRepositoryImpl(get(), get(), get(), get()) }
        //Presentation

    scope<TrackingItemsFragment> {
        scoped<TrackingItemsContract.Presenter> { TrackingItemsPresenter(getSource(), get()) }
    }
}