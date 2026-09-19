package org.freekode.tp2intervals.config.log

enum class LogIndent(val prefix: String) {
    NONE(""),
    L1_IN(">   "),
    L1_OUT("<   "),
    L2_IN(">>  "),
    L2_OUT("<<  "),
    L3_IN(">>> "),

    L4_IN(">>_ "),
    L4_OUT("<<_ ")
}