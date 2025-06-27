package me.anasmusa.portfolio.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
val ioDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1)