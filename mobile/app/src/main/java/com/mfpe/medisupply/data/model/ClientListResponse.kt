package com.mfpe.medisupply.data.model

import java.io.Serializable

data class ClientListResponse(
    val clients: List<Client>
): Serializable
