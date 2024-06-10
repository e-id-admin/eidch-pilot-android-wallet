package ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter

import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier

interface GetActivityListItem {
    suspend operator fun invoke(activityWithVerifier: ActivityWithVerifier): ActivityListItem
}
