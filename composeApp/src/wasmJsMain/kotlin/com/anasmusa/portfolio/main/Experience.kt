package com.anasmusa.portfolio.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anasmusa.portfolio.Data
import com.anasmusa.portfolio.component.InfoCell
import com.anasmusa.portfolio.component.Title
import com.anasmusa.portfolio.utils.select
import kotlinx.datetime.format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.allStringResources
import portfolio.composeapp.generated.resources.experience
import portfolio.composeapp.generated.resources.ic_case
import portfolio.composeapp.generated.resources.present

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Experience() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = select(150, 220).dp)
    ) {

        Title(
            Res.drawable.ic_case,
            Res.string.experience
        )

        Data.experience.forEach {
            InfoCell(
                it.startDate.format(Data.dateFormatter) + " - " + if (it.endDate != null)
                    it.endDate.format(Data.dateFormatter)
                else
                    stringResource(Res.string.present),
                stringResource(it.position) + " - " + it.company,
                it.items
            )
        }

    }

}