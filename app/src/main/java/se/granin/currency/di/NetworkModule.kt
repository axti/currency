package se.granin.currency.di

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.granin.currency.BuildConfig
import se.granin.currency.network.RatesApiService
import se.granin.currency.utils.rx.SchedulerProvider
import java.util.concurrent.TimeUnit

val networkModule = module {

    single(named(SIMPLE_AUTH_INTERCEPTOR)) { createAuthInterceptor(getProperty(API_APP_ID)) }

    single(named(OKHTTP_CLIENT_STANDARD)) { createOkHttpClient(get(named(SIMPLE_AUTH_INTERCEPTOR)), get()) }

    factory (named(CACHE_DIR)) { androidContext().cacheDir }
    factory {
        val cacheSize: Long = 1024 * 1024 // 1MB
        Cache(get(named(CACHE_DIR)), cacheSize)
    }

    single {
        createWebService<RatesApiService>(
            get(named(OKHTTP_CLIENT_STANDARD)),
            getProperty(SERVER_URL), get(), GsonConverterFactory.create()
        )
    }
}

private fun createHttpLoggingInterceptor(): Interceptor {
    return HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.HEADERS
        else
            HttpLoggingInterceptor.Level.NONE
    }
}

private fun createAuthInterceptor(appId: String): Interceptor {
    return object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original: Request = chain.request()

            val url = original.url.newBuilder().addQueryParameter("app_id", appId).build()

            val request: Request = original.newBuilder().url(url).build()
            return chain.proceed(request)
        }

    }
}

private fun createOkHttpClientBuilder(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
        .connectTimeout(500L, TimeUnit.MILLISECONDS)
        .readTimeout(500L, TimeUnit.MILLISECONDS)
        .addInterceptor(createHttpLoggingInterceptor())
}

private fun createOkHttpClient(authInterceptor: Interceptor, cache: Cache): OkHttpClient = createOkHttpClientBuilder()
    .addInterceptor(authInterceptor)
    .cache(cache)
    .build()

inline fun <reified T : Any> createWebService(
    okHttpClient: OkHttpClient,
    baseUrl: String,
    schedulerProvider: SchedulerProvider,
    converter: Converter.Factory
): T {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converter)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulerProvider.io()))
        .build()
        .create(T::class.java)
}

private const val OKHTTP_CLIENT_STANDARD = "standard"
private const val SIMPLE_AUTH_INTERCEPTOR = "app_id_interceptor"
private const val CACHE_DIR = "cache_dir"
private const val SERVER_URL = "SERVER_URL"
private const val API_APP_ID = "API_APP_ID"