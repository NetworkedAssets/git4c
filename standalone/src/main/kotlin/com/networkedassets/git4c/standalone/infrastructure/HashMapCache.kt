package com.networkedassets.git4c.standalone.infrastructure

import com.networkedassets.git4c.core.bussiness.Cache

abstract class HashMapCache<T> : HashMapDataSource<T>(), Cache<T>