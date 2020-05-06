/*
 * Copyright (C) 2018 The Tachiyomi Open Source Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package tachiyomi.data

import android.app.Application
import androidx.room.withTransaction
import tachiyomi.core.db.Transaction
import tachiyomi.core.prefs.PreferenceStoreFactory
import tachiyomi.data.catalog.api.CatalogGithubApi
import tachiyomi.data.catalog.service.AndroidCatalogInstallationReceiver
import tachiyomi.data.catalog.service.AndroidCatalogInstaller
import tachiyomi.data.catalog.service.AndroidCatalogLoader
import tachiyomi.data.catalog.service.CatalogRemoteRepositoryImpl
import tachiyomi.data.library.service.CategoryRepositoryImpl
import tachiyomi.data.library.service.LibraryRepositoryImpl
import tachiyomi.data.library.service.LibraryUpdateSchedulerImpl
import tachiyomi.data.library.service.MangaCategoryRepositoryImpl
import tachiyomi.data.manga.service.ChapterRepositoryImpl
import tachiyomi.data.manga.service.MangaRepositoryImpl
import tachiyomi.data.sync.api.SyncDeviceAndroid
import tachiyomi.domain.catalog.service.CatalogInstallationReceiver
import tachiyomi.domain.catalog.service.CatalogInstaller
import tachiyomi.domain.catalog.service.CatalogLoader
import tachiyomi.domain.catalog.service.CatalogPreferences
import tachiyomi.domain.catalog.service.CatalogRemoteApi
import tachiyomi.domain.catalog.service.CatalogRemoteRepository
import tachiyomi.domain.catalog.service.CatalogStore
import tachiyomi.domain.library.service.CategoryRepository
import tachiyomi.domain.library.service.LibraryCovers
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.library.service.LibraryRepository
import tachiyomi.domain.library.service.LibraryUpdateScheduler
import tachiyomi.domain.library.service.MangaCategoryRepository
import tachiyomi.domain.manga.service.ChapterRepository
import tachiyomi.domain.manga.service.MangaRepository
import tachiyomi.domain.sync.api.SyncDevice
import tachiyomi.domain.sync.service.SyncPreferences
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import java.io.File
import javax.inject.Inject

@Suppress("FunctionName")
fun DataModule(context: Application) = module {

  val preferenceFactory = PreferenceStoreFactory(context)

  bind<AppDatabase>().toProviderInstance { AppDatabase.build(context) }.providesSingleton()
  bind<Transaction>().toClass<RoomTransaction>()

  bind<MangaRepository>().toClass<MangaRepositoryImpl>().singleton()

  bind<ChapterRepository>().toClass<ChapterRepositoryImpl>().singleton()

  bind<CategoryRepository>().toClass<CategoryRepositoryImpl>().singleton()
  bind<MangaCategoryRepository>().toClass<MangaCategoryRepositoryImpl>().singleton()

  bind<LibraryRepository>().toClass<LibraryRepositoryImpl>().singleton()
  bind<LibraryPreferences>()
    .toProviderInstance { LibraryPreferences(preferenceFactory.create("library")) }
    .providesSingleton()

  bind<LibraryCovers>()
    .toProviderInstance { LibraryCovers(File(context.filesDir, "library_covers")) }
    .providesSingleton()
  bind<LibraryUpdateScheduler>().toClass<LibraryUpdateSchedulerImpl>().singleton()

  bind<SyncPreferences>()
    .toProviderInstance { SyncPreferences(preferenceFactory.create("sync")) }
    .providesSingleton()

  bind<SyncDevice>().toClass<SyncDeviceAndroid>().singleton()

  bind<CatalogRemoteRepository>().toClass<CatalogRemoteRepositoryImpl>().singleton()
  bind<CatalogRemoteApi>().toClass<CatalogGithubApi>().singleton()
  bind<CatalogPreferences>()
    .toProviderInstance { CatalogPreferences(preferenceFactory.create("catalog")) }
    .providesSingleton()

  bind<CatalogInstaller>().toClass<AndroidCatalogInstaller>().singleton()
  bind<CatalogStore>().singleton()
  bind<CatalogInstallationReceiver>().toClass<AndroidCatalogInstallationReceiver>().singleton()
  bind<CatalogLoader>().toClass<AndroidCatalogLoader>().singleton()

}

private class RoomTransaction @Inject constructor(private val db: AppDatabase) : Transaction {

  override suspend fun <T> withAction(action: suspend () -> T?) {
    db.withTransaction(action)
  }

}