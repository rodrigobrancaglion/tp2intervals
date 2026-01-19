package org.freekode.tp2intervals.infrastructure.platform.trainingpeaks.workout.structure

class TPStructureStepDTO(
    val type: String?, // step, repetition, rampUp, rampDown
    val length: TPLengthDTO?,
    val steps: List<TPStepDTO> = listOf(),
    val begin: Int?,
    val end: Int?,
) {
    companion object {
        fun singleStep(stepDTO: TPStepDTO): TPStructureStepDTO =
            TPStructureStepDTO("step", TPLengthDTO.single(), listOf(stepDTO), null, null)

        fun multiStep(repetitions: Int, stepDTOs: List<TPStepDTO>): TPStructureStepDTO =
            TPStructureStepDTO("repetition", TPLengthDTO.repetitions(repetitions.toLong()), stepDTOs, null, null)
    }
}
