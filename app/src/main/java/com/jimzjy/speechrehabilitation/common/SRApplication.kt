package com.jimzjy.speechrehabilitation.common

import android.app.Application
import com.jimzjy.speechrehabilitation.room.history.HistoryRepository
import com.jimzjy.speechrehabilitation.room.lattice.LatticeRepository
import com.jimzjy.speechrehabilitation.room.word.WordRepository
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

class SRApplication : Application() {
    companion object {
        lateinit var instance: SRApplication

        @JvmStatic
        lateinit var applicationComponent: ApplicationComponent
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this)).build()
    }
}

@Module
class ApplicationModule(private val application: SRApplication) {
    @Provides
    @ApplicationScope
    fun provideWordRepository() = WordRepository()

    @Provides
    @ApplicationScope
    fun provideHistoryRepository() = HistoryRepository()

    @Provides
    @ApplicationScope
    fun provideLatticeRepository() = LatticeRepository()
}

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun wordRepository(): WordRepository
    fun historyRepository(): HistoryRepository
    fun latticeRepository(): LatticeRepository
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope