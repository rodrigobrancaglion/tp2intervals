package org.freekode.tp2intervals.integration.platform.trainerroad.member.dto

import com.fasterxml.jackson.annotation.JsonAlias

class TrainerRoadMemberDTO(
    @JsonAlias("memberId")
    val MemberId: Long,
    @JsonAlias("username")
    val Username: String?,
)