/**
 * Groupe : Pellissier David, Ruckstuhl Michael, Sauge Ryan
 * Description: Class représentant un beacon
 **/

package com.heigvd.sym.lab3_environment.utils

data class BeaconUtils (
    val uuid : String,
    val mineur: String,
    val majeur : String,
    val RSSI: String
)
