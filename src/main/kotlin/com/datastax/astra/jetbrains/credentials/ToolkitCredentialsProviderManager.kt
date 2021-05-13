// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.datastax.astra.jetbrains.credentials

/**
 * TODO: Deprecate and remove this since it is less efficient than [CredentialsChangeEvent]
 */
interface ToolkitCredentialsChangeListener {
    fun providerAdded(profile: ProfileToken) {}
    fun providerModified(profile: ProfileToken) {}
    fun providerRemoved(profile: ProfileToken) {}
}

class CredentialProviderNotFoundException : RuntimeException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, exception: Exception) : super(msg, exception)
}
