package com.epetrashko.data.utils

fun <T> Set<T>.addMutably(element: T) =
    toMutableSet().apply { add(element) }