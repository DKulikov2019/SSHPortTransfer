package com.dkulikov2019.sshporttransfer.data.ssh.model

data class ForwardConfig(
    val localHost: String,
    val localPort: Int,
    val remoteHost: String,
    val remotePort: Int
)
