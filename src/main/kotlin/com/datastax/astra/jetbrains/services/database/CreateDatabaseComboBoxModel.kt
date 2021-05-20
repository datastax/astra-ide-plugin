package com.datastax.astra.jetbrains.services.database

import com.datastax.astra.devops_v2.models.AvailableRegionCombination
import com.datastax.astra.devops_v2.models.RegionCombination

//TODO: Create custom renderer for adding country/region flags
class CreateDatabaseComboBoxModel {
}
class DatabaseComboBoxItem(val regionInfo: AvailableRegionCombination){
    override fun toString(): String {
        return regionInfo.region
    }
}