/*
 * Copyright (C) 2023-24 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

private const val TAG = "DolbyTileService"

class DolbyTileService : TileService() {

    private val dolbyController by lazy { DolbyController.getInstance(applicationContext) }

    private fun cycleProfile() {
        val profiles = dolbyController.getProfiles()

        if (dolbyController.dsOn) {
            val profile = dolbyController.profile
            var profileIndex = profiles.indexOf(profile)

            profileIndex += 1 // next profile

            if (profileIndex >= profiles.size) { // off
                dolbyController.setDsOnAndPersist(false)
            } else {
                dolbyController.setCurrentProfileAndPersist(profiles[profileIndex], true)
            }
        } else {
            dolbyController.setCurrentProfileAndPersist(profiles[0], true)
        }
    }

    override fun onStartListening() {
        qsTile.apply {
            state = if (dolbyController.dsOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            subtitle = dolbyController.getProfileName() ?: getString(R.string.dolby_unknown)
            updateTile()
        }
        super.onStartListening()
    }

    override fun onClick() {
        cycleProfile()
        qsTile.apply {
            state = if (dolbyController.dsOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            subtitle = dolbyController.getProfileName() ?: getString(R.string.dolby_unknown)
            updateTile()
        }
        super.onClick()
    }
}
