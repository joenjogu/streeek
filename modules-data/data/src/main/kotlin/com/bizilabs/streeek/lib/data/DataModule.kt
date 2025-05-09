package com.bizilabs.streeek.lib.data

import com.bizilabs.streeek.lib.data.managers.NotificationManagerImpl
import com.bizilabs.streeek.lib.data.monitors.NetworkMonitorImpl
import com.bizilabs.streeek.lib.data.repositories.AccountRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.AuthenticationRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.ContributionRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.IssuesRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.LabelRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.LeaderboardRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.LevelRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.NotificationRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.PreferenceRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.ReminderRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.TauntRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.TeamInvitationCodeRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.TeamRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.UserRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.VersionRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.WorkersRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.team.TeamInviteRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.team.TeamRequestRepositoryImpl
import com.bizilabs.streeek.lib.data.workers.ReminderWorker
import com.bizilabs.streeek.lib.data.workers.SaveFCMTokenWork
import com.bizilabs.streeek.lib.data.workers.SyncAccountWork
import com.bizilabs.streeek.lib.data.workers.SyncContributionsWork
import com.bizilabs.streeek.lib.data.workers.SyncDailyContributionsWork
import com.bizilabs.streeek.lib.data.workers.SyncLeaderboardWork
import com.bizilabs.streeek.lib.data.workers.SyncLevelsWork
import com.bizilabs.streeek.lib.data.workers.SyncTeamsWork
import com.bizilabs.streeek.lib.domain.managers.NotificationManager
import com.bizilabs.streeek.lib.domain.monitors.NetworkMonitor
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.domain.repositories.IssueRepository
import com.bizilabs.streeek.lib.domain.repositories.LabelRepository
import com.bizilabs.streeek.lib.domain.repositories.LeaderboardRepository
import com.bizilabs.streeek.lib.domain.repositories.LevelRepository
import com.bizilabs.streeek.lib.domain.repositories.NotificationRepository
import com.bizilabs.streeek.lib.domain.repositories.PointsRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.domain.repositories.ReminderRepository
import com.bizilabs.streeek.lib.domain.repositories.TauntRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamInvitationCodeRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.domain.repositories.VersionRepository
import com.bizilabs.streeek.lib.domain.repositories.WorkersRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamInviteRepository
import com.bizilabs.streeek.lib.domain.repositories.team.TeamRequestRepository
import com.bizilabs.streeek.lib.local.LocalModule
import com.bizilabs.streeek.lib.remote.RemoteModule
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule =
    module {
        includes(RemoteModule, LocalModule)
        single<AuthenticationRepository> { AuthenticationRepositoryImpl(remote = get()) }
        single<UserRepository> { UserRepositoryImpl(remote = get(), accountLocalSource = get()) }
        single<AccountRepository> {
            AccountRepositoryImpl(
                context = get(),
                remote = get(),
                local = get(),
                contributionsLocalSource = get(),
                notificationManager = get(),
            )
        }
        single<ContributionRepository> {
            ContributionRepositoryImpl(
                local = get(),
                remote = get(),
                accountLocalSource = get(),
            )
        }
        single<PreferenceRepository> {
            PreferenceRepositoryImpl(localSource = get())
        }
        single<TeamRepository> {
            TeamRepositoryImpl(
                remoteSource = get(),
                localSource = get(),
                accountLocalSource = get(),
            )
        }
        single<TeamInvitationCodeRepository> {
            TeamInvitationCodeRepositoryImpl(remote = get(), accountLocalSource = get())
        }
        single<LevelRepository> {
            LevelRepositoryImpl(remoteSource = get(), localSource = get())
        }
        single<NotificationRepository> {
            NotificationRepositoryImpl(
                remoteSource = get(),
                localSource = get(),
                accountLocalSource = get(),
            )
        }
        single<IssueRepository> {
            IssuesRepositoryImpl(remoteSource = get(), accountLocalSource = get())
        }
        single<LabelRepository> {
            LabelRepositoryImpl(remoteSource = get())
        }
        single<LeaderboardRepository> {
            LeaderboardRepositoryImpl(
                remoteSource = get(),
                localSource = get(),
                accountLocalSource = get(),
            )
        }
        single<VersionRepository> {
            VersionRepositoryImpl(
                versionCode = get(named("version_code")),
                versionName = get(named("version_name")),
            )
        }
        single<PointsRepository> { PointsRepository() }
        single<TeamRequestRepository> {
            TeamRequestRepositoryImpl(
                remoteSource = get(),
                accountLocalSource = get(),
            )
        }
        single<ReminderRepository> { ReminderRepositoryImpl(localSource = get()) }
        single<NetworkMonitor> { NetworkMonitorImpl(context = get(), repository = get()) }
        single<TeamInviteRepository> {
            TeamInviteRepositoryImpl(
                invitationRemoteSource = get(),
                accountLocalSource = get(),
            )
        }
        single<TauntRepository> {
            TauntRepositoryImpl(
                tauntRemoteSource = get(),
                accountLocalSource = get(),
            )
        }
        single<WorkersRepository> { WorkersRepositoryImpl(context = get()) }
        single<NotificationManager> { NotificationManagerImpl(context = get()) }
        workerOf(::SyncTeamsWork)
        workerOf(::SyncLevelsWork)
        workerOf(::SyncAccountWork)
        workerOf(::SaveFCMTokenWork)
        workerOf(::SyncLeaderboardWork)
        workerOf(::SyncContributionsWork)
        workerOf(::SyncDailyContributionsWork)
        workerOf(::ReminderWorker)
    }
