package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.component.InfoCell
import me.anasmusa.portfolio.component.ShimmerInfoCell
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.deviceValue
import me.anasmusa.portfolio.data.model.Experience
import org.jetbrains.compose.resources.ExperimentalResourceApi
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_case

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExperienceSection(
    modifier: Modifier,
    data: Experience?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = deviceValue(150, 220).dp
            )
    ) {
        Title(
            Res.drawable.ic_case,
            Strings.experience
        )

        data?.let { data ->
            data.entities.forEach {
                InfoCell(
                    it.date,
                    "${it.position} - ${it.company}",
                    it.items
                )
            }
        } ?: run {
            repeat(2) {
                ShimmerInfoCell(7)
            }
        }

    }
}