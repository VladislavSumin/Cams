package ru.vladislavsumin.cams.app

import dagger.Component
import ru.vladislavsumin.cams.database.DatabaseModule
import ru.vladislavsumin.cams.domain.DomainModule
import ru.vladislavsumin.cams.network.ChangeBaseUrlInterceptor
import ru.vladislavsumin.cams.network.NetworkModule
import ru.vladislavsumin.core.mvp.BaseActivity
import ru.vladislavsumin.cams.ui.cams.CamsActivity
import ru.vladislavsumin.cams.ui.cams.CamsPresenter
import ru.vladislavsumin.cams.ui.cams.details.CamDetailsPresenter
import ru.vladislavsumin.cams.ui.login.LoginActivity
import ru.vladislavsumin.cams.ui.login.LoginPresenter
import ru.vladislavsumin.cams.ui.video.VideoPresenter
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    DatabaseModule::class,
    DomainModule::class,
    NetworkModule::class]
)
@Singleton
interface AppComponent {
    fun inject(app: App)

    fun inject(activity: BaseActivity)
    fun inject(activity: CamsActivity)
    fun inject(activity: LoginActivity)

    fun inject(presenter: CamsPresenter)
    fun inject(presenter: VideoPresenter)
    fun inject(presenter: LoginPresenter)
    fun inject(presenter: CamDetailsPresenter)

    fun inject(interceptor: ChangeBaseUrlInterceptor)
}