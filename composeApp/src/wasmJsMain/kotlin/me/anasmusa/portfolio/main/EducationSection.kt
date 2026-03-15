package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.component.InfoCell
import me.anasmusa.portfolio.component.ShimmerInfoCell
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.data.model.Education
import org.jetbrains.compose.resources.ExperimentalResourceApi
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_edu

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EducationSection(
    modifier: Modifier,
    data: Education?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 60.dp)
    ) {
        Title(
            icon = Res.drawable.ic_edu,
            title = Strings.education
        )

        data?.let { data ->
            data.entities.forEach {
                InfoCell(
                    it.date,
                    it.university + " - " + it.field,
                    it.items
                )
            }
        } ?: run {
            ShimmerInfoCell(
                modifier = Modifier.shimmer(),
                count = 1
            )
        }
    }
}