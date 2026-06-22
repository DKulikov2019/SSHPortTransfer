package com.dkulikov2019.sshporttransfer.presentation.navigation

object Destinations {
    const val Profiles = "profiles"
    const val EditProfile = "edit_profile"
    const val EditProfileArg = "profileId"
    const val EditProfileRoute = "$EditProfile?$EditProfileArg={$EditProfileArg}"
    const val Settings = "settings"

    fun editProfileRoute(profileId: String): String = "$EditProfile?$EditProfileArg=$profileId"
}
