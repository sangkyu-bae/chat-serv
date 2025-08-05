package com.chat.proxykotlin.common.converter

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object  ClassConverter {
    inline fun <reified T> Map<String, String>.convertTo(): T {
        val mapper = jacksonObjectMapper()
        return mapper.convertValue(this)
    }
}