package org.freekode.tp2intervals.integration.platform.trainingpeaks.user

import java.io.Serializable

class TPUser(
    var userId: String,
    val isAthlete: Boolean,
    val isPremium: Boolean,
) : Serializable
