package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.anasmusa.portfolio.Data
import me.anasmusa.portfolio.component.InfoCell
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.select
import kotlinx.datetime.format
import me.anasmusa.portfolio.Strings
import org.jetbrains.compose.resources.ExperimentalResourceApi
import me.anasmusa.portfolio.core.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_case

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Experience(
    horizontalPadding: Dp
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = select(150, 220).dp
            )
    ) {

        Title(
            Res.drawable.ic_case,
            Strings.experience
        )

        Data.experience.forEach {
            InfoCell(
                it.startDate.format(Data.dateFormatter) + " - " + if (it.endDate != null)
                    it.endDate.format(Data.dateFormatter)
                else
                    stringResource(Strings.present),
                stringResource(it.position) + " - " + it.company,
                it.items
            )
        }

    }

}